package io.whispers.trending.top10publisher;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.AuthorizationErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.trending.app.publishtop10.PublishTop10UseCase;
import io.whispers.trending.domain.TopicRepository;
import io.whispers.trending.domain.TrendingTopicsEventPublisher;
import io.whispers.trending.redis.RedisTopicRepository;
import io.whispers.trending.sns.sns.SnsTrendingTopicsEventPublisher;
import io.whispers.trending.top10publisherlambda.Top10PublisherLambda;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.io.IOException;
import java.util.Properties;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class Handler {

    private final Properties configuration;
    private final ObjectMapper objectMapper;
    private final RedissonClient redisson;
    private final TopicRepository topicRepository;
    private final TrendingTopicsEventPublisher trendingTopicsEventPublisher;

    public Handler() throws IOException {
        this.configuration = loadConfiguration();

        var objectMapper = new ObjectMapper();
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper = objectMapper;

        this.redisson = buildRedissonClient();
        this.topicRepository = new RedisTopicRepository(redisson);

        var trendingTopicsTopicArn = this.configuration.getProperty("trending_topics.topic_arn");
        var amazonSns = buildAmazonSnsClient(trendingTopicsTopicArn);
        this.trendingTopicsEventPublisher = new SnsTrendingTopicsEventPublisher(amazonSns, this.objectMapper, trendingTopicsTopicArn);
        System.out.println("Finished building Handler");
    }

    public void handleRequest() {
        System.out.println("Handling request");
        var useCase = new PublishTop10UseCase(topicRepository, trendingTopicsEventPublisher);
        new Top10PublisherLambda(useCase).handleRequest();
    }

    private AmazonSNS buildAmazonSnsClient(String topicResolvedTopicArn) {
        System.out.println("Building Amazon SNS client");
        var builder = AmazonSNSClientBuilder.standard();
        var maybeEndpoint = this.configuration.getProperty("sns.endpoint", "");
        if (maybeEndpoint.isBlank()) {
            builder.withRegion("us-east-1");
        } else {
            builder.withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(
                            maybeEndpoint,
                            "us-east-1"
                    )
            );
        }
        var accessKeyId = this.configuration.getProperty("aws.credentials.access_key_id", "");
        var secretAccessKey = this.configuration.getProperty("aws.credentials.secret_access_key");
        if (!accessKeyId.isBlank()) {
            var credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
            builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        }
        var amazonSNS = builder.build();
        try {
            var trendingTopicsTopicName = this.configuration.getProperty("trending_topics.topic_name");
            amazonSNS.createTopic(trendingTopicsTopicName);
        } catch (AuthorizationErrorException e) {
            // this is expected when running in the cloud
        }
        return amazonSNS;
    }

    private RedissonClient buildRedissonClient() {
        System.out.println("Building Redisson client");
        var config = new Config();
        var nodeAddresses = this.configuration.getProperty("redis.node_addresses").split(",");
        if (nodeAddresses.length > 1) {
            config.useClusterServers().addNodeAddress(nodeAddresses);
        } else {
            config.useSingleServer().setAddress(nodeAddresses[0]);
        }
        config.setCodec(new JsonJacksonCodec(this.objectMapper));
        return Redisson.create(config);
    }

    private Properties loadConfiguration() throws IOException {
        var properties = new Properties();
        try (var stream = this.getClass().getResourceAsStream("/configuration.properties")) {
            properties.load(stream);
        }
        return properties;
    }

    public static void main(String[] args) {
        Handler handler = null;
        try {
            handler = new Handler();
            handler.handleRequest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (handler != null) {
                handler.close();
            }
        }
    }

    private void close() {
        this.redisson.shutdown();
    }
}
