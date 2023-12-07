package io.whispers.trending.sns.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishBatchRequest;
import com.amazonaws.services.sns.model.PublishBatchRequestEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.trending.domain.TopicEventPublisher;
import io.whispers.trending.domain.TopicResolutionEvent;

import java.util.Collection;
import java.util.stream.Collectors;

public class SnsTopicEventPublisher implements TopicEventPublisher {

    private AmazonSNS amazonSns;
    private ObjectMapper objectMapper;
    private String topicResolvedTopicArn;

    public SnsTopicEventPublisher(AmazonSNS amazonSns, ObjectMapper objectMapper, String topicResolvedTopicArn) {
        this.amazonSns = amazonSns;
        this.objectMapper = objectMapper;
        this.topicResolvedTopicArn = topicResolvedTopicArn;
    }

    public void publishBatch(Collection<TopicResolutionEvent> events) {
        amazonSns.publishBatch(new PublishBatchRequest()
                .withTopicArn(topicResolvedTopicArn)
                .withPublishBatchRequestEntries(toBatchEntries(events)));
    }

    private Collection<PublishBatchRequestEntry> toBatchEntries(Collection<TopicResolutionEvent> events) {
        return events.stream()
                .map(this::toBatchEntry)
                .collect(Collectors.toList());
    }

    private PublishBatchRequestEntry toBatchEntry(TopicResolutionEvent event) {
        try {
            return new PublishBatchRequestEntry()
                    .withId(event.whisperUuid().toString())
                    .withMessage(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to parse", e);
        }
    }

}
