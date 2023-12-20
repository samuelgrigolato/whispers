package io.whispers.trending.redis;

import io.whispers.trending.domain.TrendingTopicRepository;
import io.whispers.trending.domain.TrendingTopic;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

public class RedisTopicRepository implements TrendingTopicRepository {
    private static final int BUCKETS = 100;
    private static final int MINUTES = 3;

    private final Supplier<ZonedDateTime> nowSupplier;
    private final Function<Integer, Integer> randomIntegerSupplier;
    private final RedissonClient redisson;

    public RedisTopicRepository(RedissonClient redisson,
                                Supplier<ZonedDateTime> nowSupplier,
                                Function<Integer, Integer> randomIntegerSupplier) {
        this.redisson = redisson;
        this.nowSupplier = nowSupplier;
        this.randomIntegerSupplier = randomIntegerSupplier;
    }

    @Override
    public void incrementWhisperCounts(Map<String, Integer> topicIncrements) {
        String minuteMapKey = getMinuteMapKey(this.nowSupplier.get());
        var bucket = this.randomIntegerSupplier.apply(BUCKETS);
        RLock lock = this.redisson.getLock("topicsLock-" + bucket);
        try {
            lock.lockInterruptibly(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Unable to acquire lock.", e);
        }
        try {
            var map = this.redisson.<String, Map<String, Integer>>getMap("topics-" + bucket);
            for (var entry : topicIncrements.entrySet()) {
                var topic = entry.getKey();
                var increment = entry.getValue();
                var minuteMap = map.get(minuteMapKey);
                if (minuteMap == null) {
                    minuteMap = new HashMap<>();
                }
                minuteMap.putIfAbsent(topic, 0);
                minuteMap.put(topic, minuteMap.get(topic) + increment);
                map.put(minuteMapKey, minuteMap); // this is necessary even if the map already exists!
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<TrendingTopic> getTrendingTopics() {
        var lastMinutesMapKeys = getLastMinutesMapKeys();
        var aggregatedWhisperCounts = new HashMap<String, Integer>();

        for (var bucket = 0; bucket < BUCKETS; bucket++) {
            var map = this.redisson.<String, Map<String, Integer>>getMap("topics-" + bucket);
            for (var minuteMapKey : lastMinutesMapKeys) {
                var minuteMap = map.get(minuteMapKey);
                if (minuteMap != null) {
                    for (var entry : minuteMap.entrySet()) {
                        var topic = entry.getKey();
                        var whisperCount = entry.getValue();
                        aggregatedWhisperCounts.putIfAbsent(topic, 0);
                        aggregatedWhisperCounts.put(topic, aggregatedWhisperCounts.get(topic) + whisperCount);
                    }
                }
            }
            // older keys cleanup (older than the required minutes)
            map.keySet().stream()
                    .filter(key -> !lastMinutesMapKeys.contains(key))
                    .forEach(map::fastRemove);
        }

        return aggregatedWhisperCounts.entrySet().stream()
                .sorted(comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .map(entry -> new TrendingTopic(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Set<String> getLastMinutesMapKeys() {
        var result = new HashSet<String>();
        var dateTime = this.nowSupplier.get();
        for (var i = 0; i < MINUTES; i++) {
            var key = getMinuteMapKey(dateTime);
            result.add(key);
            dateTime = dateTime.minusMinutes(1);
        }
        return result;
    }

    private static String getMinuteMapKey(ZonedDateTime dateTime) {
        return String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
    }

}
