package io.whispers.domain;

import java.util.Collection;

public interface TopicRepository {

    Collection<TrendingTopic> getTrending();

}
