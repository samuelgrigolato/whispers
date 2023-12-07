package io.whispers.app.postwhisper;

import io.whispers.domain.*;

public class PostWhisperUseCase {

    private WhisperRepository whisperRepository;

    private UserRepository userRepository;

    private WhisperEventPublisher whisperEventPublisher;

    public PostWhisperUseCase(WhisperRepository whisperRepository,
                              UserRepository userRepository,
                              WhisperEventPublisher whisperEventPublisher) {
        this.whisperRepository = whisperRepository;
        this.userRepository = userRepository;
        this.whisperEventPublisher = whisperEventPublisher;
    }

    public PostWhisperResponse execute(PostWhisperRequest request) {
        this.userRepository.createIfNotExists(request.sender());
        Whisper whisper = this.whisperRepository.create(new WhisperCreationRequest(
                request.text(),
                request.sender()
        ));
        this.whisperEventPublisher.publish(new WhisperCreatedEvent(
                whisper.id(),
                whisper.sender(),
                whisper.text(),
                whisper.timestamp()
        ));
        return PostWhisperResponse.from(whisper);
    }

}
