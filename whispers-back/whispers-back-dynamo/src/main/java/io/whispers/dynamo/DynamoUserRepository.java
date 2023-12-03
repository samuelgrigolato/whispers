package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import io.whispers.domain.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class DynamoUserRepository extends BaseDynamoRepository implements UserRepository {

    @Override
    public void createIfNotExists(String username) {
        var pk = "user#" + username;
        var sk = "profile";
        var keys = new Item()
                .withString("pk", pk)
                .withString("sk", sk);
        var result = dynamoDB.getItem(getTableName(), ItemUtils.toAttributeValues(keys));
        if (result.getItem() == null) {
            var item = new Item()
                    .withString("pk", pk)
                    .withString("sk", sk)
                    .withString("username", username);
            dynamoDB.putItem(getTableName(), ItemUtils.toAttributeValues(item));
        }
    }

}
