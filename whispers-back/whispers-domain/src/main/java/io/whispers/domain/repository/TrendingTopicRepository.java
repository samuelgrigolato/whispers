package io.whispers.domain.repository;

import io.whispers.domain.model.TrendingTopic;

import java.util.Collection;
import java.util.List;

public interface TrendingTopicRepository {

    Collection<TrendingTopic> findAll();

    void replaceAllWith(Collection<TrendingTopic> trendingTopics);

}
