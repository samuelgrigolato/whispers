package io.whispers.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = JpaTopicRepository.class)
class JpaTopicRepositoryTest extends BaseJpaTest {

    @Autowired
    private JpaTopicRepository jpaTopicRepository;

    @Test
    @Sql("JpaTopicRepositoryTest_shouldQueryTrendingTopics.sql")
    void shouldQueryTrendingTopics() {
        var result = this.jpaTopicRepository.getTrending();
        assertEquals(10, result.size());
        var iterator = result.iterator();
        var topic1 = iterator.next();
        var topic2 = iterator.next();
        var topic3 = iterator.next();
        assertEquals("AI", topic1.topic());
        assertEquals("food", topic2.topic());
        assertEquals("music", topic3.topic());
        assertEquals(4, topic1.whisperCount());
        assertEquals(3, topic2.whisperCount());
        assertEquals(2, topic3.whisperCount());
    }

}
