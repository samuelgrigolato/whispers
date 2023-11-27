package io.whispers.jpa;

import io.whispers.domain.TrendingTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.UUID;

public interface TopicRepositoryJpa  extends JpaRepository<TopicJpa, UUID> {

    @Query("""
    select new io.whispers.jpa.DefaultTrendingTopic(t.topic, (
        select count(1)
        from WhisperJpa w
        where w.topic = t
    ))
    from TopicJpa t
    order by (
        select count(1)
        from WhisperJpa w
        where w.topic = t
    ) desc
    limit 10
    """)
    Collection<TrendingTopic> getTrending();

}
