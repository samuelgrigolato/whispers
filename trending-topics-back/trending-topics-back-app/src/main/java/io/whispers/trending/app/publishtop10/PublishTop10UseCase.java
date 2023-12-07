package io.whispers.trending.app.publishtop10;

import io.whispers.trending.domain.TopicRepository;
import io.whispers.trending.domain.TrendingTopicsEventPublisher;

public class PublishTop10UseCase {

    private TopicRepository topicRepository;
    private TrendingTopicsEventPublisher trendingTopicsEventPublisher;

    public PublishTop10UseCase(TopicRepository topicRepository, TrendingTopicsEventPublisher trendingTopicsEventPublisher) {
        this.topicRepository = topicRepository;
        this.trendingTopicsEventPublisher = trendingTopicsEventPublisher;
    }

    public void execute() {
        var trendingTopics = this.topicRepository.getTrendingTopics();
        this.trendingTopicsEventPublisher.publish(trendingTopics);
    }

}
