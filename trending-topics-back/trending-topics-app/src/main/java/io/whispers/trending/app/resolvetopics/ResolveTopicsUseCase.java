package io.whispers.trending.app.resolvetopics;

import io.whispers.trending.domain.*;
import io.whispers.trending.domain.event.TopicResolvedEvent;
import io.whispers.trending.domain.event.TopicResolvedEventPublisher;
import io.whispers.trending.domain.event.WhisperCreatedEvent;
import io.whispers.trending.domain.TrendingTopicRepository;

import java.util.*;

public class ResolveTopicsUseCase {

    private final TrendingTopicRepository topicRepository;
    private final TopicResolver topicResolver;
    private final TopicResolvedEventPublisher topicResolvedEventPublisher;

    public ResolveTopicsUseCase(TrendingTopicRepository topicRepository, TopicResolver topicResolver,
                                TopicResolvedEventPublisher topicResolvedEventPublisher) {
        this.topicRepository = topicRepository;
        this.topicResolver = topicResolver;
        this.topicResolvedEventPublisher = topicResolvedEventPublisher;
    }

    public void execute(Collection<WhisperCreatedEvent> events) {
    }

}
