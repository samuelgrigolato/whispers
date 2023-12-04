package io.whispers.jpa;

import io.whispers.domain.TopicRepository;
import io.whispers.domain.TrendingTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.UUID;

public interface JpaTopicRepository extends JpaRepository<JpaTopic, UUID>, TopicRepository {

    @Query("""
    select new io.whispers.domain.TrendingTopic(t.topic, (
        select count(1)
        from JpaWhisper w
        where w.topic = t
    ))
    from JpaTopic t
    order by (
        select count(1)
        from JpaWhisper w
        where w.topic = t
    ) desc
    limit 10
    """)
    Collection<TrendingTopic> getTrending();

}
