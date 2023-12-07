package io.whispers.trending.domain;

public record TrendingTopic(
        String topic,
        Integer whisperCount
) {
}
