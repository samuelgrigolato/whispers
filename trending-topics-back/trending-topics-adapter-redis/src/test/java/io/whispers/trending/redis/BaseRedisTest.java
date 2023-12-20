package io.whispers.trending.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

class BaseRedisTest {

    RedisContainer redis = new RedisContainer(
            RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG));

    protected RedissonClient redisson;

    @BeforeEach
    void beforeEach() {
        redis.start();

        var config = new Config();
        config.useSingleServer().setAddress(redis.getRedisURI());
        config.setCodec(new JsonJacksonCodec(new ObjectMapper()));
        redisson = Redisson.create(config);
    }

    @AfterEach
    void afterEach() {
        redis.stop();
    }

}
