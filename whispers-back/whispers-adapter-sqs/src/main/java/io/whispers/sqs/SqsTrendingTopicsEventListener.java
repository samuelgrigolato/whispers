package io.whispers.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.whispers.app.updatetrendingtopics.UpdateTrendingTopicsRequest;
import io.whispers.app.updatetrendingtopics.UpdateTrendingTopicsUseCase;
import io.whispers.domain.repository.TrendingTopicRepository;
import io.whispers.domain.model.TrendingTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SqsTrendingTopicsEventListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrendingTopicRepository topicRepository;

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
