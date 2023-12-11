package io.whispers.trending;

import io.whispers.trending.app.resolvetopics.ResolveTopicsUseCase;
import io.whispers.trending.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ResolveTopicsUseCaseTest {

    @Test
    void testNoopIfEmptyList() {
        var topicRepositoryMock = mock(TopicRepository.class);
        var topicResolverMock = mock(TopicResolver.class);
        var topicEventPublisherMock = mock(TopicEventPublisher.class);
        var subject = new ResolveTopicsUseCase(topicRepositoryMock, topicResolverMock, topicEventPublisherMock);
        subject.execute(List.of());
        verify(topicResolverMock, never()).resolveTopic(any());
        verify(topicEventPublisherMock, never()).publishBatch(any());
        verify(topicRepositoryMock, never()).incrementWhisperCounts(any());
    }

    @Test
    void testResolvingTopic() {
        var topicRepositoryMock = mock(TopicRepository.class);
        var topicResolverMock = mock(TopicResolver.class);
        var topicEventPublisherMock = mock(TopicEventPublisher.class);
        when(topicResolverMock.resolveTopic("text")).thenReturn(Optional.of("topic"));
        var subject = new ResolveTopicsUseCase(topicRepositoryMock, topicResolverMock, topicEventPublisherMock);
        subject.execute(List.of(new WhisperCreatedEvent(
                UUID.fromString("00f8da7a-5799-48f3-88cc-b96a916712b1"),
                "text"
        )));
        verify(topicEventPublisherMock).publishBatch(List.of(new TopicResolutionEvent(
                UUID.fromString("00f8da7a-5799-48f3-88cc-b96a916712b1"),
                "topic"
        )));
        verify(topicRepositoryMock).incrementWhisperCounts(Map.of(
                "topic", 1
        ));
    }

    @Test
    void testNotResolvingTopic() {
        var topicRepositoryMock = mock(TopicRepository.class);
        var topicResolverMock = mock(TopicResolver.class);
        var topicEventPublisherMock = mock(TopicEventPublisher.class);
        when(topicResolverMock.resolveTopic("text")).thenReturn(Optional.empty());
        var subject = new ResolveTopicsUseCase(topicRepositoryMock, topicResolverMock, topicEventPublisherMock);
        subject.execute(List.of(new WhisperCreatedEvent(
                UUID.fromString("00f8da7a-5799-48f3-88cc-b96a916712b1"),
                "text"
        )));
        verify(topicEventPublisherMock, never()).publishBatch(any());
        verify(topicRepositoryMock, never()).incrementWhisperCounts(any());
    }

}
