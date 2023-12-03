package io.whispers.jpa;

import io.whispers.domain.CreateReplyData;
import io.whispers.domain.CreateWhisperData;
import io.whispers.domain.Whisper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = WhisperRepositoryJpaAdapter.class)
class WhisperRepositoryJpaAdapterTest extends BaseJpaTest {

    @Autowired
    private WhisperRepositoryJpaAdapter whisperRepositoryJpaAdapter;

    @Test
    @Sql("WhisperRepositoryJpaAdapterTest_shouldReturnLatestWhispers.sql")
    void shouldReturnLatestWhispers() {
        var result = this.whisperRepositoryJpaAdapter.findMostRecent(2);
        assertEquals(2, result.size());

        Iterator<Whisper> iterator = result.iterator();

        var obtainedWhisper1 = iterator.next();
        assertEquals(UUID.fromString("47bd22fb-8b52-4c06-9c6d-ad8d5080ed7b"), obtainedWhisper1.getId());

        var obtainedWhisper2 = iterator.next();
        assertEquals(UUID.fromString("d1e99baf-6fde-44e2-88a7-a4a7d94ae3ef"), obtainedWhisper2.getId());
        assertEquals("user", obtainedWhisper2.getSender());
        assertEquals("text1", obtainedWhisper2.getText());
        assertEquals(Optional.of("topic1"), obtainedWhisper2.getTopic());
        assertEquals(1, obtainedWhisper2.getReplies().size());

        var obtainedReply = obtainedWhisper2.getReplies().iterator().next();
        assertEquals("user2", obtainedReply.getSender());
        assertEquals("replyText", obtainedReply.getText());
    }

    @Test
    @Sql("WhisperRepositoryJpaAdapterTest_shouldReturnLatestWhispers.sql")
    void shouldReturnLatestWhispersFilteringBySender() {
        var result = this.whisperRepositoryJpaAdapter.findMostRecentBySender("user2", 2);
        assertEquals(1, result.size());
        Iterator<Whisper> iterator = result.iterator();
        var obtainedWhisper = iterator.next();
        assertEquals(UUID.fromString("a70d21e6-a6c1-4b45-bf71-18aa15c06ee9"), obtainedWhisper.getId());
    }

    @Test
    @Sql("WhisperRepositoryJpaAdapterTest_shouldReturnLatestWhispers.sql")
    void shouldReturnLatestWhispersFilteringByTopic() {
        var result = this.whisperRepositoryJpaAdapter.findMostRecentByTopic("topic1", 2);
        assertEquals(1, result.size());
        Iterator<Whisper> iterator = result.iterator();
        var obtainedWhisper = iterator.next();
        assertEquals(UUID.fromString("d1e99baf-6fde-44e2-88a7-a4a7d94ae3ef"), obtainedWhisper.getId());
    }

    @Test
    @Sql("WhisperRepositoryJpaAdapterTest_shouldCreate.sql")
    void shouldCreate() {
        var result = this.whisperRepositoryJpaAdapter.create(new CreateWhisperData(
                "text",
                "user"
        ));
        assertNotNull(result.getId());
        assertEquals("text", result.getText());
        assertEquals("user", result.getSender());
        assertTrue(result.getTopic().isEmpty());
    }

    @Test
    @Sql("WhisperRepositoryJpaAdapterTest_shouldCreateReply.sql")
    void shouldCreateReply() {
        var result = this.whisperRepositoryJpaAdapter.createReply(new CreateReplyData(
                "text",
                "user",
                UUID.fromString("64050873-5b09-41f7-9d6d-41669917a3b9")
        ));
        assertEquals("text", result.getText());
        assertEquals("user", result.getSender());
    }

}
