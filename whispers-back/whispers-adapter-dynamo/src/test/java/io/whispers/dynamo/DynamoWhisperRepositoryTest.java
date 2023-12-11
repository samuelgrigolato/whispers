package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.ZonedDateTime;
import java.util.*;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration(classes = DynamoWhisperRepository.class)
public class DynamoWhisperRepositoryTest extends BaseDynamoTest {

    @Autowired
    private DynamoWhisperRepository dynamoWhisperRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnLatestWhispers() {
        insertLatestWhispersFixtures();

        var result = this.dynamoWhisperRepository.findMostRecent(2);
        assertEquals(2, result.size());

        Iterator<Whisper> iterator = result.iterator();

        var obtainedWhisper1 = iterator.next();
        assertEquals(UUID.fromString("47bd22fb-8b52-4c06-9c6d-ad8d5080ed7b"), obtainedWhisper1.id());

        var obtainedWhisper2 = iterator.next();
        assertEquals(UUID.fromString("d1e99baf-6fde-44e2-88a7-a4a7d94ae3ef"), obtainedWhisper2.id());
        assertEquals("user", obtainedWhisper2.sender().username());
        assertEquals("text1", obtainedWhisper2.text());
        assertEquals(Optional.of("topic1"), obtainedWhisper2.topic().map(Topic::title));
        assertEquals(1, obtainedWhisper2.replies().size());

        var obtainedReply = obtainedWhisper2.replies().iterator().next();
        assertEquals("user2", obtainedReply.sender().username());
        assertEquals("replyText", obtainedReply.text());
    }

    @Test
    void shouldReturnLatestWhispersFilteringBySender() {
        insertLatestWhispersFixtures();
        var result = this.dynamoWhisperRepository.findMostRecentBySender("user2", 2);
        assertEquals(1, result.size());
        Iterator<Whisper> iterator = result.iterator();
        var obtainedWhisper = iterator.next();
        assertEquals(UUID.fromString("a70d21e6-a6c1-4b45-bf71-18aa15c06ee9"), obtainedWhisper.id());
    }

    @Test
    void shouldReturnLatestWhispersFilteringByTopic() {
        insertLatestWhispersFixtures();
        var result = this.dynamoWhisperRepository.findMostRecentByTopic("topic1", 2);
        assertEquals(1, result.size());
        Iterator<Whisper> iterator = result.iterator();
        var obtainedWhisper = iterator.next();
        assertEquals(UUID.fromString("d1e99baf-6fde-44e2-88a7-a4a7d94ae3ef"), obtainedWhisper.id());
    }

    @Test
    void shouldCreate() {
        var result = this.dynamoWhisperRepository.create(new UnsavedWhisper(
                new User("user"),
                "text"
        ));
        assertNotNull(result.id());
        assertEquals("text", result.text());
        assertEquals("user", result.sender().username());
        assertTrue(result.topic().isEmpty());
    }

    @Test
    void shouldCreateReply() {
        insertWhisper(
                UUID.fromString("64050873-5b09-41f7-9d6d-41669917a3b9"),
                "user", "text", null,
                ZonedDateTime.parse("2000-01-01T10:32:00Z"),
                List.of()
        );
        var result = this.dynamoWhisperRepository.createReply(
                UUID.fromString("64050873-5b09-41f7-9d6d-41669917a3b9"),
                new UnsavedReply(
                    new User("user"),
                    "text"
                )
        );
        assertEquals("text", result.text());
        assertEquals("user", result.sender().username());
    }

    @Test
    void shouldUpdateTopic() {
        this.dynamoWhisperRepository.updateTopic(
                UUID.fromString("64050873-5b09-41f7-9d6d-41669917a3b9"),
                "topicX"
        );
        GetItemResult item = this.dynamoDB.getItem(tableName, ItemUtils.fromSimpleMap(Map.of(
                "pk", "whisper#64050873-5b09-41f7-9d6d-41669917a3b9",
                "sk", "entry"
        )), true);
        assertEquals("topicX", item.getItem().get("topic").getS());
    }

    private void insertLatestWhispersFixtures() {
        insertWhisper(
                UUID.fromString("d1e99baf-6fde-44e2-88a7-a4a7d94ae3ef"),
                "user", "text1", "topic1",
                ZonedDateTime.parse("2000-01-01T10:30:00Z"),
                List.of(new Reply(
                        new User("user2"),
                        ZonedDateTime.parse("2000-01-01T10:31:00Z"),
                        "replyText"
                ))
        );
        insertWhisper(
                UUID.fromString("47bd22fb-8b52-4c06-9c6d-ad8d5080ed7b"),
                "user", "text2", null,
                ZonedDateTime.parse("2000-01-01T10:32:00Z"),
                List.of()
        );
        insertWhisper(
                UUID.fromString("fd8ab3d4-201c-4941-aa08-0fa3b8b7976e"),
                "user", "text3", null,
                ZonedDateTime.parse("2000-01-01T10:29:00Z"),
                List.of()
        );
        insertWhisper(
                UUID.fromString("a70d21e6-a6c1-4b45-bf71-18aa15c06ee9"),
                "user2", "text4", null,
                ZonedDateTime.parse("2000-01-01T10:28:00Z"),
                List.of()
        );
    }

    private void insertWhisper(UUID uuid, String sender, String text,
                               String topic, ZonedDateTime timestamp,
                               List<Reply> replies) {
        try {
            var formattedTimestamp = timestamp.format(ISO_INSTANT);
            var suffixedFormattedTimestamp = formattedTimestamp + "#" + uuid;
            Item item = new Item()
                    .withString("pk", "whisper#" + uuid)
                    .withString("sk", "entry")
                    .withString("gsi1Pk", "global0")
                    .withString("gsi1Sk", suffixedFormattedTimestamp)
                    .withString("gsi2Sk", suffixedFormattedTimestamp)
                    .withString("gsi3Pk", sender)
                    .withString("gsi3Sk", formattedTimestamp)
                    .withString("uuid", uuid.toString())
                    .withString("sender", sender)
                    .withString("text", text)
                    .withString("timestamp", formattedTimestamp)
                    .withString("replies", objectMapper.writeValueAsString(replies));
            if (topic != null) {
                item.withString("topic", topic)
                        .withString("gsi2Pk", topic);
            }
            dynamoDB.putItem(tableName, ItemUtils.toAttributeValues(item));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
