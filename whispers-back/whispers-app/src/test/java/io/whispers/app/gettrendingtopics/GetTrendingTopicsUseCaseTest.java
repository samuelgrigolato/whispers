package io.whispers.app.gettrendingtopics;

import io.whispers.domain.repository.TrendingTopicRepository;
import io.whispers.domain.model.TrendingTopic;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetTrendingTopicsUseCaseTest {

    @Test
    void testExecute() {
        var topic = new TrendingTopic("topic", 10L);
        var trendingTopics = List.of(topic);

        var trendingTopicRepositoryMock = mock(TrendingTopicRepository.class);
        when(trendingTopicRepositoryMock.findAll()).thenReturn(trendingTopics);

        var subject = new GetTrendingTopicsUseCase(trendingTopicRepositoryMock);

        var response = subject.execute();
        var result = response.trendingTopics();

        assertEquals(1, result.size());
        var topicResult = result.iterator().next();

        var expectedTopicResult = new TrendingTopicOutput(
                "topic",
                10
        );
        assertEquals(expectedTopicResult, topicResult);
    }

}
