package io.whispers.app.postwhisper;

import io.whispers.domain.model.UnsavedWhisper;

public record PostWhisperRequest(
        UnsavedWhisper whisper
) {
}
