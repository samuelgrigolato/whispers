package io.whispers.app.getmostrecentwhispers;

import io.whispers.domain.WhisperRepository;

public class GetMostRecentWhispersUseCase {
    private static final int LIMIT = 10;

    private WhisperRepository whisperRepository;

    public GetMostRecentWhispersUseCase(WhisperRepository whisperRepository) {
        this.whisperRepository = whisperRepository;
    }

    public GetMostRecentWhispersResponse execute() {
        var whispers = this.whisperRepository.findMostRecent(LIMIT).stream()
                .map(RecentWhisperView::from)
                .toList();
        return new GetMostRecentWhispersResponse(whispers);
    }

}
