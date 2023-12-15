package io.whispers.sns;

//import io.whispers.domain.event.WhisperCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.ZonedDateTime;
import java.util.UUID;

@ContextConfiguration(classes = SnsWhisperEventPublisher.class)
class SnsWhisperEventPublisherTest extends BaseSnsTest {

    @Autowired
    private SnsWhisperEventPublisher subject;

    @Test
    void testPublishWhisperCreatedEvent() {
//        subject.publish(new WhisperCreatedEvent(
//                UUID.randomUUID(),
//                "sender",
//                "text",
//                ZonedDateTime.now()
//        ));
        // no errors means success (i.e. the topic exists and the sns client is able to publish to it)
    }

}
