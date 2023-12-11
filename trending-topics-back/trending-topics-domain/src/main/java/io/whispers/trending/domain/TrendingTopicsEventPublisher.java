package io.whispers.trending.domain;

import java.util.List;

public interface TrendingTopicsEventPublisher {
    void publish(List<TrendingTopic> trendingTopics);
}
