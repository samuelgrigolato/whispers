package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import io.whispers.domain.TopicRepository;
import io.whispers.domain.TrendingTopic;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class DynamoTopicRepository extends BaseDynamoRepository implements TopicRepository {

    @Override
    public Collection<TrendingTopic> getTrending() {
        var result = dynamoDB.query(new QueryRequest(getTableName())
                .withKeyConditionExpression("pk = :pk")
                .withExpressionAttributeValues(Map.of(":pk", new AttributeValue("trending-topics")))
                .withLimit(10)
                .withScanIndexForward(false)
                .withProjectionExpression("topic, whisperCount"));
        return result.getItems().stream()
                .map(ItemUtils::toItem)
                .map(DynamoTopicRepository::toTrendingTopic)
                .collect(Collectors.toList());
    }

    private static TrendingTopic toTrendingTopic(Item item) {
        return new TrendingTopic(
                item.getString("topic"),
                item.getInt("whisperCount")
        );
    }

    @Override
    public void saveTrendingTopic(TrendingTopic trendingTopic) {
        String paddedWhisperCount = String.format("%020d", trendingTopic.whisperCount());
        dynamoDB.putItem(getTableName(), ItemUtils.fromSimpleMap(
                Map.of(
                        "pk", "trending-topics",
                        "sk", paddedWhisperCount,
                        "topic", trendingTopic.topic(),
                        "whisperCount", trendingTopic.whisperCount()
                )
        ));
    }

    @Override
    public void deleteAllTrendingTopicsExcept(List<TrendingTopic> trendingTopics) {
        dynamoDB.query(new QueryRequest(getTableName())
                .withKeyConditionExpression("pk = :pk")
                .withExpressionAttributeValues(Map.of(":pk", new AttributeValue("trending-topics")))
                .withProjectionExpression("pk, sk, topic, whisperCount"))
                .getItems().stream()
                .filter(item -> trendingTopics.stream()
                        .noneMatch(trendingTopic ->
                                trendingTopic.topic().equals(item.get("topic").getS()) &&
                                trendingTopic.whisperCount().equals(Integer.valueOf(item.get("whisperCount").getN()))))
                .forEach(item -> dynamoDB.deleteItem(getTableName(), Map.of(
                        "pk", item.get("pk"),
                        "sk", item.get("sk")
                )));
    }
}
