package io.whispers.jpa;

import io.whispers.domain.Whisper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
