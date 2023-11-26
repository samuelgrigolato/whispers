package io.whispers.app;

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
        Collection<RecentWhisperReplyView> replies
) {
    static RecentWhisperView from(Whisper whisper) {
        return new RecentWhisperView(
                whisper.getId(),
                whisper.getSender(),
                whisper.getTimestamp(),
                whisper.getText(),
                whisper.getTopic(),
                whisper.getReplies().stream()
                        .map(x -> new RecentWhisperReplyView(x.getSender(),
                                x.getTimestamp(),
                                x.getText()))
                        .toList()
        );
    }
}
