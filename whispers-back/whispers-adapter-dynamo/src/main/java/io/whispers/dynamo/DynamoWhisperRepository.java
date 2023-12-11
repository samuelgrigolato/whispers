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
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

@Repository
public class DynamoWhisperRepository extends BaseDynamoRepository implements WhisperRepository {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Collection<Whisper> findMostRecentBySender(String sender, int limit) {
        var result = dynamoDB.query(new QueryRequest()
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
        var result = dynamoDB.query(new QueryRequest()
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

        var executorService = Executors.newCachedThreadPool();
        var tasks = new ArrayList<Callable<QueryResult>>();
        for (var i = 0; i < 10; i++) {
            var thisI = i;
            tasks.add(() -> dynamoDB.query(new QueryRequest()
                    .withTableName(getTableName())
                    .withIndexName("gsi1")
                    .withKeyConditionExpression("gsi1Pk = :global")
                    .withExpressionAttributeValues(Map.of(
                            ":global", new AttributeValue("global" + thisI)))
                    .withProjectionExpression("pk, sk, gsi1Sk")
                    .withScanIndexForward(false)
                    .withLimit(limit)));
        }
        try {
            var futures = executorService.invokeAll(tasks);
            executorService.close();
            var allItems = futures.stream()
                    .flatMap(future -> {
                        try {
                            return future.get().getItems().stream();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .sorted(Comparator.comparing(item -> item.get("gsi1Sk").getS(), Comparator.reverseOrder()))
                    .map(item -> Map.of(
                            "pk", item.get("pk"),
                            "sk", item.get("sk")))
                    .collect(Collectors.toList());
            return batchGetWhispers(new QueryResult()
                    .withItems(allItems.subList(0, Math.min(allItems.size(), 10))));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
                .map(item -> toWhisper(item, objectMapper))
                .sorted(Comparator.comparing(Whisper::timestamp).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Whisper create(WhisperCreationRequest data) {
        try {
            var whisperUuid = UUID.randomUUID();
            var formattedTimestamp = ZonedDateTime.now().format(ISO_INSTANT);
            var suffixedFormattedTimestamp = formattedTimestamp + "#" + whisperUuid;
            var item = new Item()
                    .withString("pk", "whisper#" + whisperUuid)
                    .withString("sk", "entry")
                    .withString("gsi1Pk", "global" + (Math.abs(whisperUuid.hashCode()) % 10))
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
            return toWhisper(item, objectMapper);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize replies", e);
        }
    }

    @Override
    public Reply createReply(ReplyRequest data) {
        var whisperUuid = data.replyingTo();
        var whisperKey = ItemUtils.fromSimpleMap(Map.of(
                "pk", "whisper#" + whisperUuid,
                "sk", "entry"));

        var result = dynamoDB.getItem(new GetItemRequest()
                .withTableName(getTableName())
                .withKey(whisperKey));

        var reply = new Reply(data.sender(), ZonedDateTime.now(), data.text());
        var replies = parseReplies(ItemUtils.toItem(result.getItem()), objectMapper);
        replies.add(reply);
        var updatedRepliesStr = serializeReplies(replies, objectMapper);

        dynamoDB.updateItem(new UpdateItemRequest()
                .withTableName(getTableName())
                .withKey(whisperKey)
                .withUpdateExpression("SET replies = :replies")
                .withExpressionAttributeValues(ItemUtils.fromSimpleMap(Map.of(
                        ":replies", updatedRepliesStr))));

        return reply;
    }

    @Override
    public void updateTopic(UUID whisperId, String topic) {
        var whisperKey = ItemUtils.fromSimpleMap(Map.of(
                "pk", "whisper#" + whisperId,
                "sk", "entry"));
        var updateValues = ItemUtils.fromSimpleMap(Map.of(":topic", topic));

        dynamoDB.updateItem(new UpdateItemRequest()
                .withTableName(getTableName())
                .withKey(whisperKey)
                .withUpdateExpression("""
                    SET topic = :topic,
                        gsi2Pk = :topic
                """)
                .withExpressionAttributeValues(updateValues));
    }

    private Whisper toWhisper(Item item, ObjectMapper objectMapper) {
        return new Whisper(
                UUID.fromString(item.getString("uuid")),
                item.getString("sender"),
                ZonedDateTime.parse(item.getString("timestamp")),
                item.getString("text"),
                Optional.ofNullable(item.getString("topic")),
                parseReplies(item, objectMapper)
        );
    }

    private static List<Reply> parseReplies(Item item, ObjectMapper objectMapper) {
        List<Reply> replies;
        var repliesStr = item.getString("replies");
        if (repliesStr != null) {
            try {
                replies = objectMapper
                        .readerForListOf(Reply.class)
                        .readValue(repliesStr);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Unable to parse replies", e);
            }
        } else {
            replies = List.of();
        }
        return replies;
    }

    private static String serializeReplies(List<Reply> replies, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(replies);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize replies", e);
        }
    }
}
