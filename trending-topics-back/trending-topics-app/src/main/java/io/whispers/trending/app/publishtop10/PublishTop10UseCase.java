package io.whispers.trending.app.publishtop10;

import io.whispers.trending.domain.TrendingTopicRepository;
import io.whispers.trending.domain.event.TrendingTopicsEventPublisher;

public class PublishTop10UseCase {

    private TrendingTopicRepository trendingTopicRepository;
    private TrendingTopicsEventPublisher trendingTopicsEventPublisher;

    public PublishTop10UseCase(TrendingTopicRepository trendingTopicRepository, TrendingTopicsEventPublisher trendingTopicsEventPublisher) {
        this.trendingTopicRepository = trendingTopicRepository;
        this.trendingTopicsEventPublisher = trendingTopicsEventPublisher;
    }

    public void execute() {
        var trendingTopics = this.trendingTopicRepository.getTrendingTopics();
        this.trendingTopicsEventPublisher.publish(trendingTopics);
    }

}
