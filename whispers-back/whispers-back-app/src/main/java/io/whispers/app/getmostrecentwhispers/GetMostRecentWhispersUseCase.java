package io.whispers.app.getmostrecentwhispers;

import io.whispers.domain.WhisperRepository;

public class GetMostRecentWhispersUseCase {
    private static final int LIMIT = 10;

    private WhisperRepository whisperRepository;

    public GetMostRecentWhispersUseCase(WhisperRepository whisperRepository) {
        this.whisperRepository = whisperRepository;
    }

    public GetMostRecentWhispersResponse execute(GetMostRecentWhispersRequest request) {
        var whispers = this.whisperRepository
                .findMostRecent(request.sender(), request.topic(), LIMIT).stream()
                .map(RecentWhisperView::from)
                .toList();
        return new GetMostRecentWhispersResponse(whispers);
    }

}
