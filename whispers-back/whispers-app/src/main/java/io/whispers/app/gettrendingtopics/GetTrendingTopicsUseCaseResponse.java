package io.whispers.app.gettrendingtopics;

import java.util.Collection;

public record GetTrendingTopicsUseCaseResponse(
        Collection<TrendingTopicView> trendingTopics
) {
}
