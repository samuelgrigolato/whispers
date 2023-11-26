package io.whispers.app.postwhisper;

public record PostWhisperRequest(
        String sender,
        String text
) {
}
