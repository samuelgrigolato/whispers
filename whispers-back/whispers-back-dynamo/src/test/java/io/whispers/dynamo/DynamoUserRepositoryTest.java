package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = DynamoUserRepository.class)
public class DynamoUserRepositoryTest extends BaseDynamoTest {

    @Autowired
    private DynamoUserRepository dynamoUserRepository;

    @Test
    void shouldCreateUserIfNotExists() {
        assertFalse(checkUserExistence("non_existent"));
        this.dynamoUserRepository.createIfNotExists("non_existent");
        assertTrue(checkUserExistence("non_existent"));
    }

    @Test
    void shouldIgnoreIfAlreadyExists() {
        this.dynamoUserRepository.createIfNotExists("existent");
        this.dynamoUserRepository.createIfNotExists("existent");
        this.dynamoUserRepository.createIfNotExists("existent");
        this.dynamoUserRepository.createIfNotExists("existent");
        assertTrue(checkUserExistence("existent"));
    }

    private boolean checkUserExistence(String username) {
        QueryResult result = dynamoDB.query(new QueryRequest()
                .withTableName(tableName)
                .withKeyConditionExpression("pk = :pk and sk = :profile")
                .withExpressionAttributeValues(ItemUtils.fromSimpleMap(Map.of(
                        ":pk", "user#" + username,
                        ":profile", "profile"))));
        return result.getCount() > 0;
    }

}
