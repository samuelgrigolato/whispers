package io.whispers.dynamo;

import com.amazonaws.services.dynamodbv2.document.Item;
import io.whispers.domain.TrendingTopic;

public class TrendingTopicAdapter implements TrendingTopic {
    private final Item item;

    public TrendingTopicAdapter(Item item) {
        this.item = item;
    }

    @Override
    public String getTopic() {
        return this.item.getString("topic");
    }

    @Override
    public Long getWhispers() {
        return this.item.getNumber("whisperCount").longValue();
    }
}
