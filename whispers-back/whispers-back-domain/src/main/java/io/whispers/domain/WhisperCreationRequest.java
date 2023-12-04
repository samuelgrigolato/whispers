package io.whispers.domain;

public record WhisperCreationRequest(
        String text,
        String sender
) {
}
