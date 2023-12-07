package io.whispers.trending.redis;

import io.whispers.trending.domain.TopicRepository;
import org.redisson.api.RedissonClient;

import java.util.Map;

public class RedisTopicRepository implements TopicRepository {

    private RedissonClient redisson;

    public RedisTopicRepository(RedissonClient redisson) {
        this.redisson = redisson;
    }

    @Override
    public void incrementWhisperCounts(Map<String, Integer> topicIncrements) {
        for (var entry : topicIncrements.entrySet()) {
            var topic = entry.getKey();
            var increment = entry.getValue();
            var topicBucket = "topic-bucket-" + topic.hashCode() % 100;
            var map = this.redisson.getMap(topicBucket);
            map.addAndGet(topic, increment);
        }
    }

}
