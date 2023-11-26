package io.whispers.app.postwhisper;

import io.whispers.domain.Whisper;
import io.whispers.domain.CreateWhisperData;
import io.whispers.domain.WhisperRepository;

public class PostWhisperUseCase {

    private WhisperRepository whisperRepository;

    public PostWhisperUseCase(WhisperRepository whisperRepository) {
        this.whisperRepository = whisperRepository;
    }

    public PostWhisperResponse execute(PostWhisperRequest request) {
        Whisper whisper = this.whisperRepository.create(new CreateWhisperData(
                request.text(),
                request.sender()
        ));
        return PostWhisperResponse.from(whisper);
    }

}
