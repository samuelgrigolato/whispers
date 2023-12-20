package io.whispers.trending.redis;

import io.whispers.trending.domain.TrendingTopic;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedisTopicRepositoryTest extends BaseRedisTest {

    @Test
    void testGetTrendingTopics() {
        var topics0 = redisson.getMap("topics-0");

        var oneThirty = new HashMap<>();
        oneThirty.put("topic1", 1);
        oneThirty.put("topic2", 2);
        topics0.put("01:30", oneThirty);

        var oneThirtyOne = new HashMap<>();
        oneThirtyOne.put("topic1", 3);
        topics0.put("01:31", oneThirtyOne);

        var topics1 = redisson.getMap("topics-1");

        var oneThirtyOne2 = new HashMap<>();
        oneThirtyOne2.put("topic2", 1);
        oneThirtyOne2.put("topic3", 5);
        topics1.put("01:31", oneThirtyOne2);

        var subject = new RedisTopicRepository(
                redisson,
                () -> ZonedDateTime.of(2021, 1, 1, 1, 31, 0, 0, ZoneId.of("Etc/UTC")),
                (_buckets) -> 0
        );
        assertEquals(List.of(
                new TrendingTopic("topic3", 5),
                new TrendingTopic("topic1", 4),
                new TrendingTopic("topic2", 3)
        ), subject.getTrendingTopics());
    }

    @Test
    public void testStoreIncrement() {
        var subject = new RedisTopicRepository(
                redisson,
                () -> ZonedDateTime.of(2021, 1, 1, 1, 31, 0, 0, ZoneId.of("Etc/UTC")),
                (_buckets) -> 0
        );
        subject.incrementWhisperCounts(Map.of(
                "topic1", 1,
                "topic2", 2
        ));
        var topics0 = redisson.getMap("topics-0");
        assertEquals(Map.of(
                "01:31", Map.of(
                        "topic1", 1,
                        "topic2", 2
                )
        ), topics0);
    }
}
