package io.whispers.domain.event;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Event that represents a whisper being created.
 * Starts the process of topic resolution and counting.
 */
public record WhisperCreatedEvent(
        UUID uuid,
        String sender,
        String text,
        ZonedDateTime timestamp
) {
}
