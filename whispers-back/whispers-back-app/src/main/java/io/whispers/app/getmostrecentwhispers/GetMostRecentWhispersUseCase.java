package io.whispers.app.getmostrecentwhispers;

import io.whispers.domain.Whisper;
import io.whispers.domain.WhisperRepository;

import java.util.Collection;
import java.util.Optional;

public class GetMostRecentWhispersUseCase {
    private static final int LIMIT = 10;

    private WhisperRepository whisperRepository;

    public GetMostRecentWhispersUseCase(WhisperRepository whisperRepository) {
        this.whisperRepository = whisperRepository;
    }

    public GetMostRecentWhispersResponse execute(GetMostRecentWhispersRequest request) {
        Collection<Whisper> mostRecent;
        Optional<MostRecentFilter> maybeFilter = request.filter();
        if (maybeFilter.isPresent()) {
            MostRecentFilter filter = maybeFilter.get();
            if (filter instanceof MostRecentFilterBySender senderFilter) {
                mostRecent = this.whisperRepository.findMostRecentBySender(senderFilter.sender(), LIMIT);
            } else if (filter instanceof MostRecentFilterByTopic topicFilter) {
                mostRecent = this.whisperRepository.findMostRecentByTopic(topicFilter.topic(), LIMIT);
            } else {
                throw new IllegalArgumentException("Unknown filter type: " + filter.getClass().getName());
            }
        } else {
            mostRecent = this.whisperRepository.findMostRecent(LIMIT);
        }
        var whispers = mostRecent.stream()
                .map(RecentWhisperView::from)
                .toList();
        return new GetMostRecentWhispersResponse(whispers);
    }

}
