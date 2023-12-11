package io.whispers.app.gettrendingtopics;

import io.whispers.domain.model.TrendingTopic;

public record TrendingTopicOutput(
        String topic,
        Integer whispers
) {
    public static TrendingTopicOutput from(TrendingTopic trendingTopic) {
        return new TrendingTopicOutput(
                trendingTopic.topic().title(),
                trendingTopic.whisperCount()
        );
    }
}
