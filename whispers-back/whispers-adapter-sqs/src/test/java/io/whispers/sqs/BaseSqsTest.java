package io.whispers.sqs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.concurrent.ExecutionException;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        SqsTestConfiguration.class,
        JacksonTestConfiguration.class
})
@TestPropertySource(properties = {
        "aws.credentials.access_key_id=fakeAccessKeyId",
        "aws.credentials.secret_access_key=fakeSecretAccessKey",
        "trending_topics.queue_name=trending-topics",
        "topic_resolved.queue_name=topic-resolved"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BaseSqsTest {

    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(SQS)
            .withEnv("DEFAULT_REGION", "us-east-1")
            .waitingFor(Wait.forLogMessage(".*Ready\\.\n", 1));

    @Autowired
    protected SqsAsyncClient sqsAsyncClient;

    @Value("${sqs.endpoint}")
    private String sqsEndpoint;

    @BeforeAll
    static void beforeAll() {
        localstack.start();
        System.setProperty("aws.region", "us-east-1"); // hacky way to get the aws sdk to stop complaining
    }

    @AfterAll
    static void afterAll() {
        localstack.stop();
    }

    protected void sendMessage(String queueName, String message) {
        try {
            sqsAsyncClient.sendMessage(builder -> builder
                    .queueUrl(buildQueueUrl(queueName))
                    .messageBody(message))
                    .get();
        } catch (InterruptedException e) {
            throw new RuntimeException("Unable to send message.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Unable to send message.", e);
        }
    }

    private String buildQueueUrl(String queueName) {
        return sqsEndpoint + "/000000000000/" + queueName;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("sqs.endpoint", () -> localstack.getEndpoint());
    }

}
