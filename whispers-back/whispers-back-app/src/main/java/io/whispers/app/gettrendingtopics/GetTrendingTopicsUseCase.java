package io.whispers.app.gettrendingtopics;

import io.whispers.domain.TopicRepository;
import io.whispers.domain.TrendingTopic;

import java.util.Collection;
import java.util.stream.Collectors;

public class GetTrendingTopicsUseCase {
    private TopicRepository topicRepository;

    public GetTrendingTopicsUseCase(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public GetTrendingTopicsUseCaseResponse execute() {
        Collection<TrendingTopic> trendingTopics = this.topicRepository.getTrending();
        return new GetTrendingTopicsUseCaseResponse(trendingTopics.stream()
                .map(TrendingTopicView::from)
                .collect(Collectors.toList()));
    }

}
