package io.whispers.jpa;

import io.whispers.domain.model.*;
import io.whispers.jpa.repository.JpaWhisperRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = JpaWhisperRepositoryAdapter.class)
class JpaWhisperRepositoryAdapterTest extends BaseJpaTest {

    @Autowired
    private JpaWhisperRepositoryAdapter jpaWhisperRepositoryAdapter;

    @Autowired
    private JpaWhisperRepository jpaWhisperRepository;

    @Test
    @Sql("JpaWhisperRepositoryAdapterTest_shouldReturnLatestWhispers.sql")
    void shouldReturnLatestWhispers() {
        var result = this.jpaWhisperRepositoryAdapter.findMostRecent(2);
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
    @Sql("JpaWhisperRepositoryAdapterTest_shouldReturnLatestWhispers.sql")
    void shouldReturnLatestWhispersFilteringBySender() {
        var result = this.jpaWhisperRepositoryAdapter.findMostRecentBySender("user2", 2);
        assertEquals(1, result.size());
        Iterator<Whisper> iterator = result.iterator();
        var obtainedWhisper = iterator.next();
        assertEquals(UUID.fromString("a70d21e6-a6c1-4b45-bf71-18aa15c06ee9"), obtainedWhisper.id());
    }

    @Test
    @Sql("JpaWhisperRepositoryAdapterTest_shouldReturnLatestWhispers.sql")
    void shouldReturnLatestWhispersFilteringByTopic() {
        var result = this.jpaWhisperRepositoryAdapter.findMostRecentByTopic("topic1", 2);
        assertEquals(1, result.size());
        Iterator<Whisper> iterator = result.iterator();
        var obtainedWhisper = iterator.next();
        assertEquals(UUID.fromString("d1e99baf-6fde-44e2-88a7-a4a7d94ae3ef"), obtainedWhisper.id());
    }

    @Test
    @Sql("JpaWhisperRepositoryAdapterTest_shouldCreate.sql")
    void shouldCreate() {
        var result = this.jpaWhisperRepositoryAdapter.create(new UnsavedWhisper(
                new User("user"),
                "text"
        ));
        assertNotNull(result.id());
        assertEquals("text", result.text());
        assertEquals("user", result.sender().username());
        assertTrue(result.topic().isEmpty());
    }

    @Test
    @Sql("JpaWhisperRepositoryAdapterTest_shouldCreateReply.sql")
    void shouldCreateReply() {
        var result = this.jpaWhisperRepositoryAdapter.createReply(
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
    @Sql("JpaWhisperRepositoryAdapterTest_shouldUpdateTopic.sql")
    void shouldUpdateTopic() {
        this.jpaWhisperRepositoryAdapter.updateTopic(
                UUID.fromString("d1e99baf-6fde-44e2-88a7-a4a7d94ae3ef"),
                "topicX"
        );
        var result = this.jpaWhisperRepository
                .findById(UUID.fromString("d1e99baf-6fde-44e2-88a7-a4a7d94ae3ef"))
                .orElseThrow();
        assertEquals("topicX", result.getTopic().getTopic());
    }

}
