package io.whispers.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.whispers.app.updatewhispertopic.UpdateWhisperTopicRequest;
import io.whispers.app.updatewhispertopic.UpdateWhisperTopicUseCase;
import io.whispers.domain.event.TopicResolvedEvent;
import io.whispers.domain.repository.WhisperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SqsTopicResolvedEventListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WhisperRepository whisperRepository;

    void onTopicResolved(SnsNotification snsNotification) {
    }

}
