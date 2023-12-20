package io.whispers.trending.sns;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.trending.domain.event.TopicResolvedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

class SnsTopicResolvedEventPublisherTest extends BaseSnsTest {

    @Test
    void testIntegrationWithSns() {
        var topicResolvedTopicArn = "arn:aws:sns:us-east-1:000000000000:topic-resolved";
        var subject = new SnsTopicResolvedEventPublisher(amazonSNS, new ObjectMapper(), topicResolvedTopicArn);
        subject.publishBatch(List.of(
                new TopicResolvedEvent(UUID.randomUUID(), "topic1")
        ));
        // no errors means success (i.e. the topic exists and the sns client is able to publish to it)
    }

}
