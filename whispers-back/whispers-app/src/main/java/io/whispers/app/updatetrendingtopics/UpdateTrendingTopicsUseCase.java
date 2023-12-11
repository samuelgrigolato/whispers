package io.whispers.app.updatetrendingtopics;

import io.whispers.domain.TopicRepository;
import io.whispers.domain.TrendingTopic;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateTrendingTopicsUseCase {

    private TopicRepository topicRepository;

    public UpdateTrendingTopicsUseCase(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public void execute(UpdateTrendingTopicsRequest request) {
        List<TrendingTopic> trendingTopics = request.trendingTopics();
        for (var trendingTopic : trendingTopics) {
            this.topicRepository.saveTrendingTopic(trendingTopic);
        }
        this.topicRepository.deleteAllTrendingTopicsExcept(trendingTopics);
    }
}
