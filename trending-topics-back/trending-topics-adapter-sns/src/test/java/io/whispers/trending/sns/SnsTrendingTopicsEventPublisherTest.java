package io.whispers.trending.sns;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.trending.domain.TrendingTopic;
import io.whispers.trending.domain.event.TopicResolvedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

class SnsTrendingTopicsEventPublisherTest extends BaseSnsTest {

    @Test
    void testIntegrationWithSns() {
        var trendingTopicsTopicArn = "arn:aws:sns:us-east-1:000000000000:trending-topics";
        var subject = new SnsTrendingTopicsEventPublisher(amazonSNS, new ObjectMapper(), trendingTopicsTopicArn);
        subject.publish(List.of(
                new TrendingTopic("topic1", 1)
        ));
        // no errors means success (i.e. the topic exists and the sns client is able to publish to it)
    }

}
