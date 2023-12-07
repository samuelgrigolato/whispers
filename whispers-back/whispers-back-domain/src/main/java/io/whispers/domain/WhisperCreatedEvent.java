package io.whispers.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

public record WhisperCreatedEvent(
        UUID uuid,
        String sender,
        String text,
        ZonedDateTime timestamp
) {
}
