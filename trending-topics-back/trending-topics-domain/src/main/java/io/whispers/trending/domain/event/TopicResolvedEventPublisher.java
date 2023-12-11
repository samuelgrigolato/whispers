package io.whispers.trending.domain.event;

import java.util.Collection;

public interface TopicResolvedEventPublisher {
    void publishBatch(Collection<TopicResolvedEvent> events);
}
