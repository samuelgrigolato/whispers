package io.whispers.trending.domain;

import io.whispers.trending.domain.TrendingTopic;

import java.util.List;
import java.util.Map;

public interface TrendingTopicRepository {

    void incrementWhisperCounts(Map<String, Integer> incrementsPerTopicTitle);

    List<TrendingTopic> getTrendingTopics();

}
