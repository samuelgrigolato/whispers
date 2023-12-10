package io.whispers.domain;

import java.util.Collection;
import java.util.List;

public interface TopicRepository {

    Collection<TrendingTopic> getTrending();

    void saveTrendingTopic(TrendingTopic trendingTopic);

    void deleteAllTrendingTopicsExcept(List<TrendingTopic> trendingTopics);
}
