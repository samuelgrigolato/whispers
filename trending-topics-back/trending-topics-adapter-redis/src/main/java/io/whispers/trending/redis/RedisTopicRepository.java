package io.whispers.trending.redis;

import io.whispers.trending.domain.TrendingTopicRepository;
import io.whispers.trending.domain.TrendingTopic;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

public class RedisTopicRepository implements TrendingTopicRepository {
    private static final int BUCKETS = 100;
    private static final int MINUTES = 3;

    private RedissonClient redisson;

    public RedisTopicRepository(RedissonClient redisson) {
        this.redisson = redisson;
    }

    @Override
    public void incrementWhisperCounts(Map<String, Integer> topicIncrements) {
    }

    @Override
    public List<TrendingTopic> getTrendingTopics() {
        var lastMinutesMapKeys = getLastMinutesMapKeys();
        var aggregatedWhisperCounts = new HashMap<String, Integer>();

        var map = this.redisson.<String, Map<String, Integer>>getMap("topics");
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

        return aggregatedWhisperCounts.entrySet().stream()
                .sorted(comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .map(entry -> new TrendingTopic(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private static Set<String> getLastMinutesMapKeys() {
        var result = new HashSet<String>();
        var dateTime = ZonedDateTime.now(ZoneId.of("Etc/UTC"));
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
