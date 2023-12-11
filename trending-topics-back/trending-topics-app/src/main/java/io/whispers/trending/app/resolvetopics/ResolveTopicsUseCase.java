package io.whispers.trending.app.resolvetopics;

import io.whispers.trending.domain.*;

import java.util.*;

public class ResolveTopicsUseCase {

    private final TopicRepository topicRepository;
    private final TopicResolver topicResolver;
    private final TopicEventPublisher topicEventPublisher;

    public ResolveTopicsUseCase(TopicRepository topicRepository, TopicResolver topicResolver, TopicEventPublisher topicEventPublisher) {
        this.topicRepository = topicRepository;
        this.topicResolver = topicResolver;
        this.topicEventPublisher = topicEventPublisher;
    }

    public void execute(Collection<WhisperCreatedEvent> events) {
        var topicIncrements = new HashMap<String, Integer>();
        var resolutionEvents = new ArrayList<TopicResolutionEvent>();
        for (var event : events) {
            var maybeTopic = this.topicResolver.resolveTopic(event.text());
            if (maybeTopic.isPresent()) {
                var topic = maybeTopic.get();
                resolutionEvents.add(new TopicResolutionEvent(event.uuid(), topic));
                topicIncrements.put(topic, topicIncrements.getOrDefault(topic, 0) + 1);
            }
        }
        if (!resolutionEvents.isEmpty()) {
            this.topicEventPublisher.publishBatch(resolutionEvents);
        }
        if (!topicIncrements.isEmpty()) {
            this.topicRepository.incrementWhisperCounts(topicIncrements);
        }
    }

}
