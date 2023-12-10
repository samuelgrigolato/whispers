package io.whispers.app.gettrendingtopics;

import io.whispers.domain.TrendingTopic;

public record TrendingTopicView(
        String topic,
        Integer whispers
) {
    public static TrendingTopicView from(TrendingTopic trendingTopic) {
        return new TrendingTopicView(
                trendingTopic.topic(),
                trendingTopic.whisperCount()
        );
    }
}
