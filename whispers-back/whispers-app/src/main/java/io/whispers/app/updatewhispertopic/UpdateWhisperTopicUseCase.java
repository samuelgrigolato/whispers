package io.whispers.app.updatewhispertopic;

import io.whispers.domain.TopicResolutionEvent;
import io.whispers.domain.WhisperRepository;

public class UpdateWhisperTopicUseCase {

    private final WhisperRepository whisperRepository;

    public UpdateWhisperTopicUseCase(WhisperRepository whisperRepository) {
        this.whisperRepository = whisperRepository;
    }

    public void execute(UpdateWhisperTopicRequest request) {
        TopicResolutionEvent event = request.event();
        this.whisperRepository.updateTopic(event.whisperUuid(), event.topic());
    }

}
