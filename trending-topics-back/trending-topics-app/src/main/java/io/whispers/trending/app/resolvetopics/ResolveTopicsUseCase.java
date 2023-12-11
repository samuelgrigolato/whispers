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
        var topicIncrements = new HashMap<String, Integer>();
        var resolutionEvents = new ArrayList<TopicResolvedEvent>();
        for (var event : events) {
            var maybeTopic = this.topicResolver.resolveTopic(event.text());
            if (maybeTopic.isPresent()) {
                var topic = maybeTopic.get();
                resolutionEvents.add(new TopicResolvedEvent(event.uuid(), topic));
                topicIncrements.put(topic, topicIncrements.getOrDefault(topic, 0) + 1);
            }
        }
        if (!resolutionEvents.isEmpty()) {
            this.topicResolvedEventPublisher.publishBatch(resolutionEvents);
        }
        if (!topicIncrements.isEmpty()) {
            this.topicRepository.incrementWhisperCounts(topicIncrements);
        }
    }

}
