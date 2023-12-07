package io.whispers.trending.sns.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.trending.domain.TrendingTopic;
import io.whispers.trending.domain.TrendingTopicsEventPublisher;

import java.util.List;

public class SnsTrendingTopicsEventPublisher implements TrendingTopicsEventPublisher {

    private AmazonSNS amazonSns;
    private ObjectMapper objectMapper;
    private String trendingTopicsTopicArn;

    public SnsTrendingTopicsEventPublisher(AmazonSNS amazonSns,
                                           ObjectMapper objectMapper,
                                           String trendingTopicsTopicArn) {
        this.amazonSns = amazonSns;
        this.objectMapper = objectMapper;
        this.trendingTopicsTopicArn = trendingTopicsTopicArn;
    }

    @Override
    public void publish(List<TrendingTopic> trendingTopics) {
        amazonSns.publish(this.trendingTopicsTopicArn, toMessage(trendingTopics));
    }

    private String toMessage(List<TrendingTopic> trendingTopics) {
        try {
            return objectMapper.writeValueAsString(trendingTopics);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to parse", e);
        }
    }

}
