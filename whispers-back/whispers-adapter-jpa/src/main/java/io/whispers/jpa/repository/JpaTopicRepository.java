package io.whispers.jpa.repository;

import io.whispers.domain.model.TrendingTopic;
import io.whispers.jpa.entity.JpaTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface JpaTopicRepository extends JpaRepository<JpaTopic, UUID> {

    @Query("""
    select new io.whispers.domain.model.TrendingTopic(
        t.topic,
        (
            select count(1)
            from JpaWhisper w
            where w.topic = t
        )
    )
    from JpaTopic t
    order by (
        select count(1)
        from JpaWhisper w
        where w.topic = t
    ) desc
    limit 10
    """)
    Collection<TrendingTopic> findTrendingTopics();

    Optional<JpaTopic> findByTopic(String topic);

}
