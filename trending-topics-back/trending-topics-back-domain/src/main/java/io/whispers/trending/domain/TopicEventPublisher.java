package io.whispers.trending.domain;

import java.util.Collection;

public interface TopicEventPublisher {
    void publishBatch(Collection<TopicResolutionEvent> events);
}
