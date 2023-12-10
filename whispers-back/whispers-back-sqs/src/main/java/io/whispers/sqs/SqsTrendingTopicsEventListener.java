package io.whispers.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.whispers.app.updatetrendingtopics.UpdateTrendingTopicsRequest;
import io.whispers.app.updatetrendingtopics.UpdateTrendingTopicsUseCase;
import io.whispers.domain.TopicRepository;
import io.whispers.domain.TopicResolutionEvent;
import io.whispers.domain.TrendingTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class SqsTrendingTopicsEventListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TopicRepository topicRepository;

    @SqsListener(value = "${trending_topics.queue_name}")
    void onTrendingTopics(SnsNotification snsNotification) {
        try {
            List<TrendingTopic> trendingTopics = this.objectMapper
                    .readerForListOf(TrendingTopic.class)
                    .readValue(snsNotification.message());
            var useCase = new UpdateTrendingTopicsUseCase(this.topicRepository);
            useCase.execute(new UpdateTrendingTopicsRequest(trendingTopics));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to parse message", e);
        }
    }

}
