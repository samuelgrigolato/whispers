package io.whispers.domain;

public record TrendingTopic (
        String topic,
        Integer whisperCount
) {
    public TrendingTopic(String topic, Long whisperCount) {
        this(topic, whisperCount.intValue());
    }
}
