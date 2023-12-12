package io.whispers.sqs;

import io.whispers.domain.repository.WhisperRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = SqsTopicResolvedEventListener.class)
public class SqsTopicResolvedEventListenerTest extends BaseSqsTest {

    @MockBean
    private WhisperRepository whisperRepository;

    @Test
    void shouldIngestTopicResolvedEvent() {
        sendMessage("topic-resolved", """
        {
            "Message": "{ \\\"whisperUuid\\\": \\\"8530eff7-933f-47de-9186-6aedec8b045d\\\", \\\"topic\\\": \\\"topicX\\\" }"
        }
        """);
        var whisperUuid = UUID.fromString("8530eff7-933f-47de-9186-6aedec8b045d");
        var topic = "topicX";
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(this.whisperRepository).updateTopic(whisperUuid, topic));
    }

}
