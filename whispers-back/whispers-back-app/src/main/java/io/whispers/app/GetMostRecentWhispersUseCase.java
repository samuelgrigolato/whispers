package io.whispers.app;

import io.whispers.domain.WhisperRepository;

import java.util.Collection;

public class GetMostRecentWhispersUseCase {
    private static final int LIMIT = 10;

    private WhisperRepository whisperRepository;

    public GetMostRecentWhispersUseCase(WhisperRepository whisperRepository) {
        this.whisperRepository = whisperRepository;
    }

    public Collection<RecentWhisperView> execute() {
        return this.whisperRepository.findMostRecent(LIMIT).stream()
                .map(RecentWhisperView::from)
                .toList();
    }

}
