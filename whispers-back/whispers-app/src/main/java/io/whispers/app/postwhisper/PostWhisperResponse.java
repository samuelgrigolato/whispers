package io.whispers.app.postwhisper;

import io.whispers.domain.model.Topic;
import io.whispers.domain.model.Whisper;

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
        Collection<Void> replies
) {
    static PostWhisperResponse from(Whisper whisper) {
        if (!whisper.replies().isEmpty()) {
            throw new IllegalArgumentException("whisper.replies() should be empty!");
        }
        return new PostWhisperResponse(
                whisper.id(),
                whisper.sender().username(),
                whisper.timestamp(),
                whisper.text(),
                whisper.topic().map(Topic::title),
                Collections.emptyList()
        );
    }
}
