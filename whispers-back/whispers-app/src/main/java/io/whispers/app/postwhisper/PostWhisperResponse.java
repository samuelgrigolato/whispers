package io.whispers.app.postwhisper;

import io.whispers.app.getmostrecentwhispers.RecentReplyView;
import io.whispers.domain.Whisper;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public record PostWhisperResponse(
        UUID id,
        String sender,
        ZonedDateTime timestamp,
        String text,
        Optional<String> topic,
        Collection<RecentReplyView> replies
) {
    static PostWhisperResponse from(Whisper whisper) {
        if (!whisper.replies().isEmpty()) {
            throw new IllegalArgumentException("whisper.replies() should be empty!");
        }
        return new PostWhisperResponse(
                whisper.id(),
                whisper.sender(),
                whisper.timestamp(),
                whisper.text(),
                whisper.topic(),
                Collections.emptyList()
        );
    }
}
