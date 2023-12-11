package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import io.whispers.domain.model.Topic;
import io.whispers.domain.repository.TrendingTopicRepository;
import io.whispers.domain.model.TrendingTopic;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class DynamoTrendingTopicRepository extends BaseDynamoRepository implements TrendingTopicRepository {

    @Override
    public Collection<TrendingTopic> findAll() {
        var result = dynamoDB.query(new QueryRequest(getTableName())
                .withKeyConditionExpression("pk = :pk")
                .withExpressionAttributeValues(Map.of(":pk", new AttributeValue("trending-topics")))
                .withLimit(20)
                .withScanIndexForward(false)
                .withProjectionExpression("topic, whisperCount"));
        return result.getItems().stream()
                .map(ItemUtils::toItem)
                .map(DynamoTrendingTopicRepository::toTrendingTopic)
                .collect(Collectors.groupingBy(TrendingTopic::topic))
                .entrySet().stream()
                .map(entry -> entry.getValue().stream()
                        .reduce(new TrendingTopic(entry.getKey(), 0),
                                (t1, t2) -> new TrendingTopic(t1.topic(), Math.max(t1.whisperCount(), t2.whisperCount()))))
                .limit(10)
                .sorted(Comparator.comparing(TrendingTopic::whisperCount).reversed())
                .collect(Collectors.toList());
    }

    private static TrendingTopic toTrendingTopic(Item item) {
        return new TrendingTopic(
                new Topic(item.getString("topic")),
                item.getInt("whisperCount")
        );
    }

    @Override
    public void replaceAllWith(Collection<TrendingTopic> trendingTopics) {
        trendingTopics.forEach(this::save);
        deleteAllExcept(trendingTopics);
    }

    private void save(TrendingTopic trendingTopic) {
        String paddedWhisperCount = String.format("%020d", trendingTopic.whisperCount());
        dynamoDB.putItem(getTableName(), ItemUtils.fromSimpleMap(
                Map.of(
                        "pk", "trending-topics",
                        "sk", paddedWhisperCount,
                        "topic", trendingTopic.topic().title(),
                        "whisperCount", trendingTopic.whisperCount()
                )
        ));
    }

    private void deleteAllExcept(Collection<TrendingTopic> trendingTopics) {
        var existing = dynamoDB.query(new QueryRequest(getTableName())
                        .withKeyConditionExpression("pk = :pk")
                        .withExpressionAttributeValues(Map.of(":pk", new AttributeValue("trending-topics")))
                        .withProjectionExpression("pk, sk, topic, whisperCount"))
                        .getItems();

        existing.stream()
                .filter(item -> notOneOf(trendingTopics, item))
                .forEach(this::delete);
    }

    private void delete(Map<String, AttributeValue> item) {
        dynamoDB.deleteItem(getTableName(), Map.of(
                "pk", item.get("pk"),
                "sk", item.get("sk")
        ));
    }

    private boolean notOneOf(Collection<TrendingTopic> trendingTopics, Map<String, AttributeValue> item) {
        return trendingTopics.stream()
                .noneMatch(trendingTopic ->
                        trendingTopic.topic().title().equals(item.get("topic").getS()) &&
                        trendingTopic.whisperCount().equals(Integer.valueOf(item.get("whisperCount").getN())));
    }
}
