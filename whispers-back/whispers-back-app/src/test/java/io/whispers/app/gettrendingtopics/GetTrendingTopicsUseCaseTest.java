package io.whispers.app.gettrendingtopics;

import io.whispers.domain.TopicRepository;
import io.whispers.domain.TrendingTopic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetTrendingTopicsUseCaseTest {

    @Test
    void testExecute() {
        var topic = mock(TrendingTopic.class);
        when(topic.getTopic()).thenReturn("topic");
        when(topic.getWhispers()).thenReturn(10L);
        var trendingTopics = List.of(topic);

        var topicRepositoryMock = mock(TopicRepository.class);
        when(topicRepositoryMock.getTrending()).thenReturn(trendingTopics);

        var subject = new GetTrendingTopicsUseCase(topicRepositoryMock);

        var response = subject.execute();
        var result = response.trendingTopics();

        assertEquals(1, result.size());
        var topicResult = result.iterator().next();

        var expectedTopicResult = new TrendingTopicView(
                "topic",
                10L
        );
        assertEquals(expectedTopicResult, topicResult);
    }

}
