package io.whispers.app.updatetrendingtopics;

import io.whispers.domain.model.TrendingTopic;

import java.util.Collection;
import java.util.List;

public record UpdateTrendingTopicsRequest(
        Collection<TrendingTopic> trendingTopics
) {
}
