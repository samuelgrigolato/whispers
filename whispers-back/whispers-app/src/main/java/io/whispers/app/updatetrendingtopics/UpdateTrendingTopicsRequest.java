package io.whispers.app.updatetrendingtopics;

import io.whispers.domain.TrendingTopic;

import java.util.List;

public record UpdateTrendingTopicsRequest(
        List<TrendingTopic> trendingTopics
) {
}
