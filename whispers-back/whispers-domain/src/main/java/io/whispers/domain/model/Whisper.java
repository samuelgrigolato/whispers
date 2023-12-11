package io.whispers.domain.model;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Entity representing a whisper.
 */
public record Whisper (
        UUID id,
        User sender,
        ZonedDateTime timestamp,
        String text,
        Optional<Topic> topic,
        Collection<Reply> replies
) {
}
