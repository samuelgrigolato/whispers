package io.whispers.domain.model;

/**
 * Value object that represents a trending topic.
 * The system only maintains the top 10 trending topics.
 */
public record TrendingTopic (
        Topic topic,
        Integer whisperCount
) {
    public TrendingTopic(String topic, Long whisperCount) {
        this(new Topic(topic), whisperCount.intValue());
    }
}
