package io.whispers.trending.redis;

import io.whispers.trending.domain.TopicRepository;
import io.whispers.trending.domain.TrendingTopic;
import org.redisson.api.RedissonClient;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RedisTopicRepository implements TopicRepository {

    public static final int BUCKETS = 100;
    private RedissonClient redisson;

    public RedisTopicRepository(RedissonClient redisson) {
        this.redisson = redisson;
    }

    @Override
    public void incrementWhisperCounts(Map<String, Integer> topicIncrements) {
        for (var entry : topicIncrements.entrySet()) {
            var topic = entry.getKey();
            var increment = entry.getValue();
            var topicBucket = "topic-bucket-" + Math.abs(topic.hashCode()) % BUCKETS;
            var map = this.redisson.getMap(topicBucket);
            map.addAndGet(topic, increment.longValue());
        }
    }

    @Override
    public List<TrendingTopic> getTrendingTopics() {
        var all = new TreeSet<>(Comparator.comparing(TrendingTopic::whisperCount).reversed());
        for (var i = 0; i < BUCKETS; i++) {
            var topicBucket = "topic-bucket-" + i;
            var map = this.redisson.getMap(topicBucket);
            map.entrySet().forEach(entry -> all.add(new TrendingTopic(
                    (String) entry.getKey(),
                    (Integer) entry.getValue()
            )));
        }
        return all.stream()
                .limit(10)
                .collect(Collectors.toList());
    }

}
