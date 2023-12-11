package io.whispers.api;

import io.whispers.app.gettrendingtopics.GetTrendingTopicsUseCase;
import io.whispers.app.gettrendingtopics.GetTrendingTopicsUseCaseResponse;
import io.whispers.app.gettrendingtopics.TrendingTopicOutput;
import io.whispers.domain.repository.TrendingTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/topics")
class TopicsController {

    @Autowired
    private TrendingTopicRepository topicRepository;

    @GetMapping(path = "/trending")
    Collection<TrendingTopicOutput> getTrending() {
        GetTrendingTopicsUseCase useCase = new GetTrendingTopicsUseCase(this.topicRepository);
        GetTrendingTopicsUseCaseResponse response = useCase.execute();
        return response.trendingTopics();
    }
}
