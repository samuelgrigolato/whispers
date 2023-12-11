package io.whispers.app.updatetrendingtopics;

import io.whispers.domain.repository.TrendingTopicRepository;

public class UpdateTrendingTopicsUseCase {

    private TrendingTopicRepository trendingTopicRepository;

    public UpdateTrendingTopicsUseCase(TrendingTopicRepository trendingTopicRepository) {
        this.trendingTopicRepository = trendingTopicRepository;
    }

    public void execute(UpdateTrendingTopicsRequest request) {
        this.trendingTopicRepository.replaceAllWith(request.trendingTopics());
    }
}
