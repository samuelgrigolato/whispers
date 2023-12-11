package io.whispers.trending.domain.event;

import io.whispers.trending.domain.TrendingTopic;

import java.util.List;

public interface TrendingTopicsEventPublisher {
    void publish(List<TrendingTopic> trendingTopics);
}
