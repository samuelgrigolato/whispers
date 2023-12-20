package io.whispers.trending.whispercreated;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.AuthorizationErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.trending.app.resolvetopics.ResolveTopicsUseCase;
import io.whispers.trending.domain.TrendingTopicRepository;
import io.whispers.trending.domain.TopicResolver;
import io.whispers.trending.domain.event.TopicResolvedEventPublisher;
import io.whispers.trending.redis.RedisTopicRepository;
import io.whispers.trending.simpleresolver.SimpleTopicResolver;
import io.whispers.trending.sns.SnsTopicResolvedEventPublisher;
import io.whispers.trending.whispercreatedlambda.WhisperCreatedLambda;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class Handler {

    private final Properties configuration;
    private final ObjectMapper objectMapper;
    private final RedissonClient redisson;
    private final TrendingTopicRepository topicRepository;
    private final TopicResolver topicResolver;
    private final TopicResolvedEventPublisher topicResolvedEventPublisher;

    public Handler() throws IOException {
        this.configuration = loadConfiguration();

        var objectMapper = new ObjectMapper();
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper = objectMapper;

        this.redisson = buildRedissonClient();
        this.topicRepository = new RedisTopicRepository(redisson);

        this.topicResolver = new SimpleTopicResolver();

        var topicResolvedTopicArn = this.configuration.getProperty("topic_resolved.topic_arn");
        var amazonSns = buildAmazonSnsClient();
        this.topicResolvedEventPublisher = new SnsTopicResolvedEventPublisher(amazonSns, this.objectMapper, topicResolvedTopicArn);
        System.out.println("Finished building Handler");
    }

    public void handleRequest(SQSEvent input, Context context) {
        System.out.println("Handling request");
        var useCase = new ResolveTopicsUseCase(topicRepository, topicResolver, topicResolvedEventPublisher);
        new WhisperCreatedLambda(objectMapper, useCase).handleRequest(input, context);
    }

    private AmazonSNS buildAmazonSnsClient() {
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
            var topicResolvedTopicName = this.configuration.getProperty("topic_resolved.topic_name");
            amazonSNS.createTopic(topicResolvedTopicName);
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
            var event = new SQSEvent();
            var message = new SQSEvent.SQSMessage();
            var uuid = UUID.randomUUID();
            message.setBody("""
            {
                  "Type" : "Notification",
                  "MessageId" : "84102bd5-8890-4ed5-aeba-c15fafc926da",
                  "TopicArn" : "arn:aws:sns:eu-west-1:000:HelloWorld",
                  "Message" : "{\\"uuid\\":\\"%s\\",\\"text\\":\\"test message #topic1\\"}",
                  "Timestamp" : "2012-06-05T13:44:22.360Z",
                  "SignatureVersion" : "1",
                  "Signature" : "signature",
                  "SigningCertURL" : "url",
                  "UnsubscribeURL" : "url"
            }
            """.formatted(uuid.toString()));
            event.setRecords(List.of(message));
            handler.handleRequest(event, null);
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
