package io.whispers.trending.domain.event;

import java.util.UUID;

public record WhisperCreatedEvent(
        UUID uuid,
        String text
) {
}
