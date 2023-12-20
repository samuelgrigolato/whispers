package io.whispers.trending.app.publishtop10;

import io.whispers.trending.domain.TrendingTopic;
import io.whispers.trending.domain.TrendingTopicRepository;
import io.whispers.trending.domain.event.TrendingTopicsEventPublisher;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class PublishTop10UseCaseTest {

    @Test
    void testPublishTop10() {
        var trendingTopicRepositoryMock = mock(TrendingTopicRepository.class);
        var trendingTopicsEventPublisherMock = mock(TrendingTopicsEventPublisher.class);
        when(trendingTopicRepositoryMock.getTrendingTopics()).thenReturn(List.of(
                new TrendingTopic("topic1", 1),
                new TrendingTopic("topic2", 2)
        ));
        var subject = new PublishTop10UseCase(
                trendingTopicRepositoryMock,
                trendingTopicsEventPublisherMock);
        subject.execute();
        verify(trendingTopicRepositoryMock).getTrendingTopics();
        verify(trendingTopicsEventPublisherMock).publish(List.of(
                new TrendingTopic("topic1", 1),
                new TrendingTopic("topic2", 2)
        ));
    }

}
