package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import io.whispers.domain.model.Topic;
import io.whispers.domain.model.TrendingTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = DynamoTrendingTopicRepository.class)
public class DynamoTrendingTopicRepositoryTest extends BaseDynamoTest {

    @Autowired
    private DynamoTrendingTopicRepository dynamoTrendingTopicRepository;

    @Test
    void shouldQueryTrendingTopics() {
        for (int i = 1; i <= 12; i++) {
            // pad with 0s to make sure the sort key is sorted correctly
            var paddedI = String.format("%020d", i);
            dynamoDB.putItem(tableName, ItemUtils.fromSimpleMap(Map.of(
                    "pk", "trending-topics",
                    "sk", paddedI,
                    "topic", "topic" + i,
                    "whisperCount", i
            )));
        }

        var result = this.dynamoTrendingTopicRepository.findAll();
        assertEquals(10, result.size());
        var iterator = result.iterator();
        var topic1 = iterator.next();
        var topic2 = iterator.next();
        var topic3 = iterator.next();
        assertEquals("topic12", topic1.topic().title());
        assertEquals("topic11", topic2.topic().title());
        assertEquals("topic10", topic3.topic().title());
        assertEquals(12, topic1.whisperCount());
        assertEquals(11, topic2.whisperCount());
        assertEquals(10, topic3.whisperCount());
    }

    @Test
    void shouldReplaceRanking() {
        assertEquals(0, this.queryTrendingTopics().getCount());
        this.dynamoTrendingTopicRepository.replaceAllWith(List.of(
                new TrendingTopic(new Topic("topic1"), 1),
                new TrendingTopic(new Topic("topic2"), 2),
                new TrendingTopic(new Topic("topic3"), 3)
        ));
        var result = this.queryTrendingTopics();
        var items = result.getItems();
        assertEquals(3, result.getCount());
        assertEquals("topic1", items.get(0).get("topic").getS());
        assertEquals("topic2", items.get(1).get("topic").getS());
        assertEquals("topic3", items.get(2).get("topic").getS());
        this.dynamoTrendingTopicRepository.replaceAllWith(List.of(
                new TrendingTopic(new Topic("topic1"), 2),
                new TrendingTopic(new Topic("topic4"), 3)
        ));
        result = this.queryTrendingTopics();
        items = result.getItems();
        assertEquals(2, result.getCount());
        assertEquals("topic1", items.get(0).get("topic").getS());
        assertEquals("topic4", items.get(1).get("topic").getS());
    }

    private QueryResult queryTrendingTopics() {
        return this.dynamoDB.query(new QueryRequest()
                .withTableName(tableName)
                .withConsistentRead(true)
                .withKeyConditionExpression("pk = :pk")
                .withExpressionAttributeValues(ItemUtils.fromSimpleMap(Map.of(
                        ":pk", "trending-topics"))));
    }
}
