package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.domain.model.*;
import io.whispers.domain.repository.WhisperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Repository
public class DynamoWhisperRepository extends BaseDynamoRepository implements WhisperRepository {
    private static final int GLOBAL_WHISPERS_SHARDS = 10;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Collection<Whisper> findMostRecentBySender(String sender, int limit) {
        return List.of();
    }

    @Override
    public Collection<Whisper> findMostRecentByTopic(String topic, int limit) {
        return List.of();
    }

    @Override
    public Collection<Whisper> findMostRecent(int limit) {
        return List.of();
    }

    private Map<String, AttributeValue> toKeyAttributes(Map<String, AttributeValue> item) {
        return Map.of(
                "pk", item.get("pk"),
                "sk", item.get("sk"));
    }

    private Stream<Map<String, AttributeValue>> waitForFutureResult(Future<QueryResult> future) {
        try {
            return future.get().getItems().stream();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Unable to get shard results.", e);
        }
    }

    @Override
    public Whisper create(UnsavedWhisper data) {
        return null;
    }

    @Override
    public Reply createReply(UUID replyingTo, UnsavedReply data) {
        return null;
    }

    @Override
    public void updateTopic(UUID whisperId, String topic) {
    }

    private Whisper toWhisper(Item item, ObjectMapper objectMapper) {
        return new Whisper(
                UUID.fromString(item.getString("uuid")),
                new User(item.getString("sender")),
                ZonedDateTime.parse(item.getString("timestamp")),
                item.getString("text"),
                Optional.ofNullable(item.getString("topic")).map(Topic::new),
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
