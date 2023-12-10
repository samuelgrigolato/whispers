package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = DynamoTopicRepository.class)
public class DynamoTopicRepositoryTest extends BaseDynamoTest {

    @Autowired
    private DynamoTopicRepository dynamoTopicRepository;

    @Test
    void shouldQueryTrendingTopics() {
        for (int i = 1; i <= 12; i++) {
            // pad with 0s to make sure the sort key is sorted correctly
            String paddedI = String.format("%020d", i);
            dynamoDB.putItem(tableName, ItemUtils.fromSimpleMap(Map.of(
                    "pk", "trending-topics",
                    "sk", paddedI,
                    "topic", "topic" + i,
                    "whisperCount", i
            )));
        }

        var result = this.dynamoTopicRepository.getTrending();
        assertEquals(10, result.size());
        var iterator = result.iterator();
        var topic1 = iterator.next();
        var topic2 = iterator.next();
        var topic3 = iterator.next();
        assertEquals("topic12", topic1.topic());
        assertEquals("topic11", topic2.topic());
        assertEquals("topic10", topic3.topic());
        assertEquals(12, topic1.whisperCount());
        assertEquals(11, topic2.whisperCount());
        assertEquals(10, topic3.whisperCount());
    }

}
