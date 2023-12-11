package io.whispers.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.domain.WhisperCreatedEvent;
import io.whispers.domain.WhisperEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SnsWhisperEventPublisher implements WhisperEventPublisher {

    @Autowired
    private AmazonSNS amazonSns;

    @Value("${whisper_created.topic_arn}")
    private String whisperCreatedTopicArn;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void publish(WhisperCreatedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            amazonSns.publish(whisperCreatedTopicArn, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize event", e);
        }
    }

}
