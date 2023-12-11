package io.whispers.app.updatewhispertopic;

import io.whispers.domain.event.TopicResolvedEvent;
import io.whispers.domain.repository.WhisperRepository;

public class UpdateWhisperTopicUseCase {

    private final WhisperRepository whisperRepository;

    public UpdateWhisperTopicUseCase(WhisperRepository whisperRepository) {
        this.whisperRepository = whisperRepository;
    }

    public void execute(UpdateWhisperTopicRequest request) {
        TopicResolvedEvent event = request.event();
        this.whisperRepository.updateTopic(event.whisperUuid(), event.topic());
    }

}
