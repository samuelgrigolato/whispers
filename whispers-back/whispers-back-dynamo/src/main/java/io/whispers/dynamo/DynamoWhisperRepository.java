package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

@Repository
public class DynamoWhisperRepository extends BaseDynamoRepository implements WhisperRepository {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Collection<Whisper> findMostRecentBySender(String sender, int limit) {
        QueryResult result = dynamoDB.query(new QueryRequest()
                .withTableName(getTableName())
                .withIndexName("gsi3")
                .withKeyConditionExpression("gsi3Pk = :sender")
                .withExpressionAttributeValues(Map.of(
                        ":sender", new AttributeValue(sender)))
                .withProjectionExpression("pk, sk")
                .withScanIndexForward(false)
                .withLimit(limit));

        return batchGetWhispers(result);
    }

    @Override
    public Collection<Whisper> findMostRecentByTopic(String topic, int limit) {
        QueryResult result = dynamoDB.query(new QueryRequest()
                .withTableName(getTableName())
                .withIndexName("gsi2")
                .withKeyConditionExpression("gsi2Pk = :topic")
                .withExpressionAttributeValues(Map.of(
                        ":topic", new AttributeValue(topic)))
                .withProjectionExpression("pk, sk")
                .withScanIndexForward(false)
                .withLimit(limit));

        return batchGetWhispers(result);
    }

    @Override
    public Collection<Whisper> findMostRecent(int limit) {
        QueryResult result = dynamoDB.query(new QueryRequest()
                .withTableName(getTableName())
                .withIndexName("gsi1")
                .withKeyConditionExpression("gsi1Pk = :global")
                .withExpressionAttributeValues(Map.of(
                        ":global", new AttributeValue("global")))
                .withProjectionExpression("pk, sk")
                .withScanIndexForward(false)
                .withLimit(limit));

        return batchGetWhispers(result);
    }

    private List<Whisper> batchGetWhispers(QueryResult result) {
        if (result.getItems().isEmpty()) {
            return List.of();
        }

        var keysAndAttributes = new KeysAndAttributes()
                .withKeys(result.getItems())
                .withProjectionExpression("#uuid, sender, topic, #text, #timestamp, replies")
                .withExpressionAttributeNames(Map.of(
                        "#uuid", "uuid",
                        "#text", "text",
                        "#timestamp", "timestamp"));

        var batchResult = dynamoDB.batchGetItem(new BatchGetItemRequest()
                .withRequestItems(Map.of(getTableName(), keysAndAttributes)));

        return batchResult.getResponses().get(getTableName()).stream()
                .map(ItemUtils::toItem)
                .map(item -> new WhisperAdapter(item, objectMapper))
                .sorted(Comparator.comparing(Whisper::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Whisper create(CreateWhisperData data) {
        try {
            var whisperUuid = UUID.randomUUID();
            var formattedTimestamp = ZonedDateTime.now().format(ISO_INSTANT);
            var suffixedFormattedTimestamp = formattedTimestamp + "#" + whisperUuid;
            Item item = new Item()
                    .withString("pk", "whisper#" + whisperUuid)
                    .withString("sk", "entry")
                    .withString("gsi1Pk", "global")
                    .withString("gsi1Sk", suffixedFormattedTimestamp)
                    .withString("gsi2Sk", suffixedFormattedTimestamp)
                    .withString("gsi3Pk", data.sender())
                    .withString("gsi3Sk", formattedTimestamp)
                    .withString("uuid", whisperUuid.toString())
                    .withString("sender", data.sender())
                    .withString("text", data.text())
                    .withString("timestamp", formattedTimestamp)
                    .withString("replies", objectMapper.writeValueAsString(List.of()));
            dynamoDB.putItem(getTableName(), ItemUtils.toAttributeValues(item));
            return new WhisperAdapter(item, objectMapper);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize replies", e);
        }
    }

    @Override
    public Reply createReply(CreateReplyData data) {
        var whisperUuid = data.replyingTo();
        Map<String, AttributeValue> whisperKey = ItemUtils.fromSimpleMap(Map.of(
                "pk", "whisper#" + whisperUuid,
                "sk", "entry"));

        GetItemResult result = dynamoDB.getItem(new GetItemRequest()
                .withTableName(getTableName())
                .withKey(whisperKey));

        ReplyAdapter reply = new ReplyAdapter(data.sender(), ZonedDateTime.now(), data.text());
        List<Reply> replies = WhisperAdapter.parseReplies(ItemUtils.toItem(result.getItem()), objectMapper);
        replies.add(reply);
        String updatedRepliesStr = WhisperAdapter.serializeReplies(replies, objectMapper);

        dynamoDB.updateItem(new UpdateItemRequest()
                .withTableName(getTableName())
                .withKey(whisperKey)
                .withUpdateExpression("SET replies = :replies")
                .withExpressionAttributeValues(ItemUtils.fromSimpleMap(Map.of(
                        ":replies", updatedRepliesStr))));

        return reply;
    }

}
