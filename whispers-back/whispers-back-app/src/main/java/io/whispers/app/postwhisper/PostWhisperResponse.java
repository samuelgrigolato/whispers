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
        if (!whisper.getReplies().isEmpty()) {
            throw new IllegalArgumentException("whisper.getReplies() should be empty!");
        }
        return new PostWhisperResponse(
                whisper.getId(),
                whisper.getSender(),
                whisper.getTimestamp(),
                whisper.getText(),
                whisper.getTopic(),
                Collections.emptyList()
        );
    }
}
