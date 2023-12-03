package io.whispers.dynamo;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class WhispersDynamoConfiguration {

    @Bean
    AmazonDynamoDB amazonDynamoDB(@Value("${dynamodb.endpoint}") String maybeEndpoint,
                                  @Value("${dynamodb.table.name}") String tableName,
                                  @Value("${aws.credentials.access_key_id}") String accessKeyId,
                                  @Value("${aws.credentials.secret_access_key}") String secretAccessKey) {
        var builder = AmazonDynamoDBClientBuilder.standard();
        if (maybeEndpoint.isBlank()) {
            builder.withRegion("us-east-1");
        } else {
            builder.withEndpointConfiguration(
                    new AmazonDynamoDBClientBuilder.EndpointConfiguration(
                            maybeEndpoint,
                            "us-east-1"
                    )
            );
        }
        if (!accessKeyId.isBlank()) {
            var credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
            builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        }
        var amazonDynamoDB = builder.build();
        try {
            amazonDynamoDB.describeTable(tableName);
        } catch (ResourceNotFoundException e) {
            createTable(tableName, amazonDynamoDB);
        }
        return amazonDynamoDB;
    }

    static void createTable(String tableName, AmazonDynamoDB amazonDynamoDB) {
        amazonDynamoDB.createTable(new CreateTableRequest()
                .withTableName(tableName)
                .withAttributeDefinitions(
                        new AttributeDefinition()
                                .withAttributeName("pk")
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName("sk")
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName("gsi1Pk")
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName("gsi1Sk")
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName("gsi2Pk")
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName("gsi2Sk")
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName("gsi3Pk")
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName("gsi3Sk")
                                .withAttributeType(ScalarAttributeType.S)
                )
                .withBillingMode(BillingMode.PAY_PER_REQUEST)
                .withKeySchema(
                        new KeySchemaElement()
                                .withAttributeName("pk")
                                .withKeyType(KeyType.HASH),
                        new KeySchemaElement()
                                .withAttributeName("sk")
                                .withKeyType(KeyType.RANGE)
                )
                .withGlobalSecondaryIndexes(
                        new GlobalSecondaryIndex()
                                .withIndexName("gsi1")
                                .withKeySchema(
                                        new KeySchemaElement()
                                                .withAttributeName("gsi1Pk")
                                                .withKeyType(KeyType.HASH),
                                        new KeySchemaElement()
                                                .withAttributeName("gsi1Sk")
                                                .withKeyType(KeyType.RANGE))
                                .withProjection(new Projection()
                                        .withProjectionType(ProjectionType.KEYS_ONLY)),
                        new GlobalSecondaryIndex()
                                .withIndexName("gsi2")
                                .withKeySchema(
                                        new KeySchemaElement()
                                                .withAttributeName("gsi2Pk")
                                                .withKeyType(KeyType.HASH),
                                        new KeySchemaElement()
                                                .withAttributeName("gsi2Sk")
                                                .withKeyType(KeyType.RANGE))
                                .withProjection(new Projection()
                                        .withProjectionType(ProjectionType.KEYS_ONLY)),
                        new GlobalSecondaryIndex()
                                .withIndexName("gsi3")
                                .withKeySchema(
                                        new KeySchemaElement()
                                                .withAttributeName("gsi3Pk")
                                                .withKeyType(KeyType.HASH),
                                        new KeySchemaElement()
                                                .withAttributeName("gsi3Sk")
                                                .withKeyType(KeyType.RANGE))
                                .withProjection(new Projection()
                                        .withProjectionType(ProjectionType.KEYS_ONLY))));
    }

}
