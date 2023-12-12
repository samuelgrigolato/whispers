package io.whispers.sqs;

import io.whispers.domain.model.Topic;
import io.whispers.domain.model.TrendingTopic;
import io.whispers.domain.repository.TrendingTopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = SqsTrendingTopicsEventListener.class)
public class SqsTrendingTopicsEventListenerTest extends BaseSqsTest {

    @MockBean
    private TrendingTopicRepository trendingTopicRepository;

    @Test
    void shouldIngestRankingEvent() {
        sendMessage("trending-topics", """
        {
            "Message": "[{ \\\"title\\\": \\\"topic\\\", \\\"whisperCount\\\": 1 }]"
        }
        """);
        var expected = List.of(new TrendingTopic(new Topic("topic"), 1));
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(this.trendingTopicRepository).replaceAllWith(expected));
    }

}
