package io.whispers.app.getmostrecentwhispers;

import io.whispers.domain.Whisper;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public record RecentWhisperView(
        UUID id,
        String sender,
        ZonedDateTime timestamp,
        String text,
        Optional<String> topic,
        Collection<RecentReplyView> replies
) {
    static RecentWhisperView from(Whisper whisper) {
        return new RecentWhisperView(
                whisper.id(),
                whisper.sender(),
                whisper.timestamp(),
                whisper.text(),
                whisper.topic(),
                whisper.replies().stream()
                        .map(x -> new RecentReplyView(x.sender(),
                                x.timestamp(),
                                x.text()))
                        .toList()
        );
    }
}
