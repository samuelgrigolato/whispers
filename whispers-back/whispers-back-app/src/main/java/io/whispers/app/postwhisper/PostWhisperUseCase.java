package io.whispers.app.postwhisper;

import io.whispers.domain.UserRepository;
import io.whispers.domain.Whisper;
import io.whispers.domain.WhisperCreationRequest;
import io.whispers.domain.WhisperRepository;

public class PostWhisperUseCase {

    private WhisperRepository whisperRepository;

    private UserRepository userRepository;

    public PostWhisperUseCase(WhisperRepository whisperRepository,
                              UserRepository userRepository) {
        this.whisperRepository = whisperRepository;
        this.userRepository = userRepository;
    }

    public PostWhisperResponse execute(PostWhisperRequest request) {
        this.userRepository.createIfNotExists(request.sender());
        Whisper whisper = this.whisperRepository.create(new WhisperCreationRequest(
                request.text(),
                request.sender()
        ));
        return PostWhisperResponse.from(whisper);
    }

}
