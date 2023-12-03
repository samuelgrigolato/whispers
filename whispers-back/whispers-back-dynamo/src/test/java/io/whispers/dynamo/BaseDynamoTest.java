package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        JacksonTestConfiguration.class,
        WhispersDynamoConfiguration.class
})
@TestPropertySource(properties = {
        "aws.credentials.access_key_id=fakeAccessKeyId",
        "aws.credentials.secret_access_key=fakeSecretAccessKey",
        "dynamodb.table.name=whispers-back-test"
})
public class BaseDynamoTest {

    static GenericContainer<?> dynamo = new GenericContainer<>("amazon/dynamodb-local:latest")
            .withExposedPorts(8000);

    @Autowired
    protected AmazonDynamoDB dynamoDB;

    @Value("${dynamodb.table.name}")
    protected String tableName;

    @BeforeAll
    static void beforeAll() {
        dynamo.start();
    }

    @AfterAll
    static void afterAll() {
        dynamo.stop();
    }

    @AfterEach
    void afterEach() {
        dynamoDB.deleteTable(tableName);
        WhispersDynamoConfiguration.createTable(tableName, dynamoDB);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("dynamodb.endpoint", () -> "http://localhost:" + dynamo.getMappedPort(8000));
    }

}
