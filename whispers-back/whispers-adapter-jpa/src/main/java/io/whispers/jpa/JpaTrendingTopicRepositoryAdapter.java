package io.whispers.jpa;

import io.whispers.domain.model.TrendingTopic;
import io.whispers.domain.repository.TrendingTopicRepository;
import io.whispers.jpa.repository.JpaTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class JpaTrendingTopicRepositoryAdapter implements TrendingTopicRepository {

    @Autowired
    private JpaTopicRepository jpaTopicRepository;

    @Override
    public Collection<TrendingTopic> findAll() {
        return this.jpaTopicRepository.findTrendingTopics();
    }

    @Override
    public void replaceAllWith(Collection<TrendingTopic> trendingTopics) {
        // this is a no-op for JPA because findAll uses the whispers table
    }
}
