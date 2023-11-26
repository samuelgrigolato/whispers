package io.whispers.domain;

public record CreateWhisperData(
        String text,
        String sender
) {
}
