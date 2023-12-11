package io.whispers.domain.model;

import java.time.ZonedDateTime;

/**
 * Value object representing a reply to a whisper.
 */
public record Reply (
        User sender,
        ZonedDateTime timestamp,
        String text
) {
}
