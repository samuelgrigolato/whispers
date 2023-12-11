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

    @SqsListener(value = "${topic_resolved.queue_name}", maxConcurrentMessages = "500")
    void onTopicResolved(SnsNotification snsNotification) {
        try {
            var event = this.objectMapper.readValue(snsNotification.message(), TopicResolvedEvent.class);
            var useCase = new UpdateWhisperTopicUseCase(this.whisperRepository);
            useCase.execute(new UpdateWhisperTopicRequest(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to parse message", e);
        }
    }

}
