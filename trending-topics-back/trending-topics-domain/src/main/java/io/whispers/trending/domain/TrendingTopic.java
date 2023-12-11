package io.whispers.trending.domain;

public record TrendingTopic(
        String title,
        Integer whisperCount
) {
}
