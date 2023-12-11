package io.whispers.app.gettrendingtopics;

import java.util.Collection;

public record GetTrendingTopicsUseCaseResponse(
        Collection<TrendingTopicOutput> trendingTopics
) {
}
