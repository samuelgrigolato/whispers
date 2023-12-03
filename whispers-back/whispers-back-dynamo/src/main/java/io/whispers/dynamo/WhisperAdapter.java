package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.domain.Reply;
import io.whispers.domain.Whisper;

import java.time.ZonedDateTime;
import java.util.*;

public class WhisperAdapter implements Whisper {
    private final Item item;
    private final List<Reply> replies;

    public WhisperAdapter(Item item, ObjectMapper objectMapper) {
        this.item = item;
        this.replies = parseReplies(item, objectMapper);
    }

    @Override
    public UUID getId() {
        return UUID.fromString(item.getString("uuid"));
    }

    @Override
    public String getSender() {
        return item.getString("sender");
    }

    @Override
    public ZonedDateTime getTimestamp() {
        return ZonedDateTime.parse(item.getString("timestamp"));
    }

    @Override
    public String getText() {
        return item.getString("text");
    }

    @Override
    public Optional<String> getTopic() {
        return Optional.ofNullable(item.getString("topic"));
    }

    @Override
    public Collection<Reply> getReplies() {
        return replies;
    }

    static List<Reply> parseReplies(Item item, ObjectMapper objectMapper) {
        List<Reply> replies;
        String repliesStr = item.getString("replies");
        if (repliesStr != null) {
            try {
                replies = objectMapper
                        .readerForListOf(ReplyAdapter.class)
                        .readValue(repliesStr);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Unable to parse replies", e);
            }
        } else {
            replies = List.of();
        }
        return replies;
    }

    static String serializeReplies(List<Reply> replies, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(replies);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize replies", e);
        }
    }
}
