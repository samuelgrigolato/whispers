package io.whispers.sns;

import com.amazonaws.services.sns.AmazonSNS;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        JacksonTestConfiguration.class,
        WhispersSnsConfiguration.class
})
@TestPropertySource(properties = {
        "aws.credentials.access_key_id=fakeAccessKeyId",
        "aws.credentials.secret_access_key=fakeSecretAccessKey",
        "whisper_created.topic_arn=arn:aws:sns:us-east-1:000000000000:whisper-created"
})
class BaseSnsTest {

    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.11.3"))
            .withServices(SNS)
            .withEnv("DEFAULT_REGION", "us-east-1");

    @Autowired
    protected AmazonSNS amazonSNS;

    @BeforeAll
    static void beforeAll() {
        localstack.start();
    }

    @AfterAll
    static void afterAll() {
        localstack.stop();
    }

    @BeforeEach
    void beforeEach() {
        amazonSNS.createTopic("whisper-created");
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("sns.endpoint", () -> localstack.getEndpoint());
    }

}
