package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class BaseDynamoRepository {

    @Autowired
    protected AmazonDynamoDB dynamoDB;

    @Value("${dynamodb.table.name}")
    private String tableName;

    protected String getTableName() {
        return tableName;
    }

}
