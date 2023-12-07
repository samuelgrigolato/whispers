package io.whispers.trending.domain;

import java.util.List;
import java.util.Map;

public interface TopicRepository {
    void incrementWhisperCounts(Map<String, Integer> topicIncrements);

    List<TrendingTopic> getTrendingTopics();
}
