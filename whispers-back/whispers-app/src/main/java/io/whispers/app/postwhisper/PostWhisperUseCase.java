package io.whispers.app.postwhisper;

import io.whispers.domain.model.Whisper;
import io.whispers.domain.repository.WhisperRepository;

public class PostWhisperUseCase {

    private WhisperRepository whisperRepository;

    public PostWhisperUseCase(WhisperRepository whisperRepository) {
        this.whisperRepository = whisperRepository;
    }

    public PostWhisperResponse execute(PostWhisperRequest request) {
        Whisper whisper = this.whisperRepository.create(request.whisper());
        return PostWhisperResponse.from(whisper);
    }

}
