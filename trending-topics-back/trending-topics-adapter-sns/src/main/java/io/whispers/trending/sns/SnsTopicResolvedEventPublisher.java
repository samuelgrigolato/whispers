package io.whispers.trending.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishBatchRequest;
import com.amazonaws.services.sns.model.PublishBatchRequestEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.trending.domain.event.TopicResolvedEvent;
import io.whispers.trending.domain.event.TopicResolvedEventPublisher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class SnsTopicResolvedEventPublisher implements TopicResolvedEventPublisher {

    private AmazonSNS amazonSns;
    private ObjectMapper objectMapper;
    private String topicResolvedTopicArn;

    public SnsTopicResolvedEventPublisher(AmazonSNS amazonSns,
                                          ObjectMapper objectMapper,
                                          String topicResolvedTopicArn) {
        this.amazonSns = amazonSns;
        this.objectMapper = objectMapper;
        this.topicResolvedTopicArn = topicResolvedTopicArn;
    }

    public void publishBatch(Collection<TopicResolvedEvent> events) {
        var batch = new ArrayList<TopicResolvedEvent>();
        for (var event : events) {
            batch.add(event);
            if (batch.size() == 10) {
                publishSingleBatch(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            publishSingleBatch(batch);
        }
    }

    private void publishSingleBatch(Collection<TopicResolvedEvent> events) {
        amazonSns.publishBatch(new PublishBatchRequest()
                .withTopicArn(topicResolvedTopicArn)
                .withPublishBatchRequestEntries(toBatchEntries(events)));
    }

    private Collection<PublishBatchRequestEntry> toBatchEntries(Collection<TopicResolvedEvent> events) {
        return events.stream()
                .map(this::toBatchEntry)
                .collect(Collectors.toList());
    }

    private PublishBatchRequestEntry toBatchEntry(TopicResolvedEvent event) {
        try {
            return new PublishBatchRequestEntry()
                    .withId(event.whisperUuid().toString())
                    .withMessage(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to parse", e);
        }
    }

}
