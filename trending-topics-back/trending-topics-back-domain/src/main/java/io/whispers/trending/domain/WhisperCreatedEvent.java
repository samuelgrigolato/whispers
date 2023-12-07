package io.whispers.trending.domain;

import java.util.UUID;

public record WhisperCreatedEvent(
        UUID uuid,
        String text
) {
}
