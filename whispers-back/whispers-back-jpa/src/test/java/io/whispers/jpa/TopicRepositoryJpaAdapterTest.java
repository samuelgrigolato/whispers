package io.whispers.jpa;

import io.whispers.domain.TrendingTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = TopicRepositoryJpaAdapter.class)
class TopicRepositoryJpaAdapterTest extends BaseJpaTest {

    @Autowired
    private TopicRepositoryJpaAdapter topicRepositoryJpaAdapter;

    @Test
    @Sql("TopicRepositoryJpaAdapterTest_shouldQueryTrendingTopics.sql")
    void shouldQueryTrendingTopics() {
        var result = this.topicRepositoryJpaAdapter.getTrending();
        assertEquals(10, result.size());
        var iterator = result.iterator();
        var topic1 = iterator.next();
        var topic2 = iterator.next();
        var topic3 = iterator.next();
        assertEquals("AI", topic1.getTopic());
        assertEquals("food", topic2.getTopic());
        assertEquals("music", topic3.getTopic());
        assertEquals(4, topic1.getWhispers());
        assertEquals(3, topic2.getWhispers());
        assertEquals(2, topic3.getWhispers());
    }

}
