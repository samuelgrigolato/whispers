package io.whispers.trending.sns;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;

class BaseSnsTest {

    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(SNS)
            .withEnv("DEFAULT_REGION", "us-east-1");

    protected static AmazonSNS amazonSNS;

    @BeforeAll
    static void beforeAll() {
        localstack.start();

        var credentials = new BasicAWSCredentials("fakeAccessKeyId", "fakeSecretAccessKey");
        amazonSNS = AmazonSNSClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                localstack.getEndpoint().toString(),
                                localstack.getRegion()
                        ))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        amazonSNS.createTopic("topic-resolved");
        amazonSNS.createTopic("trending-topics");
    }

    @AfterAll
    static void afterAll() {
        localstack.stop();
    }

    @BeforeEach
    void beforeEach() {
        amazonSNS.createTopic("whisper-created");
    }

}
