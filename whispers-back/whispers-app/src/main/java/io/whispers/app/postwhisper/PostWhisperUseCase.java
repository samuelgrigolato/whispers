package io.whispers.app.postwhisper;

import io.whispers.domain.event.WhisperCreatedEvent;
import io.whispers.domain.event.WhisperCreatedEventPublisher;
import io.whispers.domain.model.Whisper;
import io.whispers.domain.repository.WhisperRepository;

public class PostWhisperUseCase {

    private WhisperRepository whisperRepository;

    private WhisperCreatedEventPublisher whisperCreatedEventPublisher;

    public PostWhisperUseCase(WhisperRepository whisperRepository,
                              WhisperCreatedEventPublisher whisperCreatedEventPublisher) {
        this.whisperRepository = whisperRepository;
        this.whisperCreatedEventPublisher = whisperCreatedEventPublisher;
    }

    public PostWhisperResponse execute(PostWhisperRequest request) {
        Whisper whisper = this.whisperRepository.create(request.whisper());
        this.whisperCreatedEventPublisher.publish(new WhisperCreatedEvent(
                whisper.id(),
                whisper.sender().username(),
                whisper.text(),
                whisper.timestamp()
        ));
        return PostWhisperResponse.from(whisper);
    }

}
