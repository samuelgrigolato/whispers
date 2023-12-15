package io.whispers.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnsWhisperEventPublisher {

    @Autowired
    private AmazonSNS amazonSns;

    @Value("${whisper_created.topic_arn}")
    private String whisperCreatedTopicArn;

    @Autowired
    private ObjectMapper objectMapper;

}
