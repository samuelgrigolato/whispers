package io.whispers.jpa;

import io.whispers.domain.TrendingTopic;

class DefaultTrendingTopic implements TrendingTopic {
    private String topic;
    private Long whispers;

    DefaultTrendingTopic(String topic, Long whispers) {
        this.topic = topic;
        this.whispers = whispers;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public Long getWhispers() {
        return whispers;
    }
}
