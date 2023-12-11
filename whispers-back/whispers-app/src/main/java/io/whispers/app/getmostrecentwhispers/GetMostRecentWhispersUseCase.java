package io.whispers.app.getmostrecentwhispers;

import io.whispers.domain.model.Whisper;
import io.whispers.domain.repository.WhisperRepository;

import java.util.Collection;

public class GetMostRecentWhispersUseCase {
    private static final int LIMIT = 10;

    private WhisperRepository whisperRepository;

    public GetMostRecentWhispersUseCase(WhisperRepository whisperRepository) {
        this.whisperRepository = whisperRepository;
    }

    public GetMostRecentWhispersResponse execute(GetMostRecentWhispersRequest request) {
        Collection<Whisper> mostRecent;
        switch (request.filter()) {
            case MostRecentFilterBySender filterBySender ->
                    mostRecent = this.whisperRepository.findMostRecentBySender(filterBySender.sender(), LIMIT);
            case MostRecentFilterByTopic filterByTopic ->
                    mostRecent = this.whisperRepository.findMostRecentByTopic(filterByTopic.topic(), LIMIT);
            case MostRecentFilterAll ignored ->
                    mostRecent = this.whisperRepository.findMostRecent(LIMIT);
            default -> throw new IllegalArgumentException("Unknown filter type: " + request.filter().getClass().getName());
        }
        var whispers = mostRecent.stream()
                .map(RecentWhisperOutput::from)
                .toList();
        return new GetMostRecentWhispersResponse(whispers);
    }

}
