package io.whispers.app.gettrendingtopics;

import io.whispers.domain.repository.TrendingTopicRepository;
import io.whispers.domain.model.TrendingTopic;

import java.util.Collection;
import java.util.stream.Collectors;

public class GetTrendingTopicsUseCase {
    private TrendingTopicRepository trendingTopicRepository;

    public GetTrendingTopicsUseCase(TrendingTopicRepository trendingTopicRepository) {
        this.trendingTopicRepository = trendingTopicRepository;
    }

    public GetTrendingTopicsUseCaseResponse execute() {
        Collection<TrendingTopic> trendingTopics = this.trendingTopicRepository.findAll();
        return new GetTrendingTopicsUseCaseResponse(trendingTopics.stream()
                .map(TrendingTopicOutput::from)
                .collect(Collectors.toList()));
    }

}
