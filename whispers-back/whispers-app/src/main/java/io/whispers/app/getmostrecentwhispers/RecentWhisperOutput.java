package io.whispers.app.getmostrecentwhispers;

import io.whispers.domain.model.Whisper;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public record RecentWhisperOutput(
        UUID id,
        String sender,
        ZonedDateTime timestamp,
        String text,
        Optional<String> topic,
        Collection<RecentReplyOutput> replies
) {
    static RecentWhisperOutput from(Whisper whisper) {
        return new RecentWhisperOutput(
                whisper.id(),
                whisper.sender().username(),
                whisper.timestamp(),
                whisper.text(),
                whisper.topic().map(x -> x.title()),
                whisper.replies().stream()
                        .map(x -> new RecentReplyOutput(x.sender().username(),
                                x.timestamp(),
                                x.text()))
                        .toList()
        );
    }
}
