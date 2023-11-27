package io.whispers.jpa;

import io.whispers.domain.TopicRepository;
import io.whispers.domain.TrendingTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.stream.Collectors;

@Repository
public class TopicRepositoryJpaAdapter implements TopicRepository {

    @Autowired
    private TopicRepositoryJpa topicRepositoryJpa;

    @Override
    public Collection<TrendingTopic> getTrending() {
        return this.topicRepositoryJpa.getTrending();
    }

}
