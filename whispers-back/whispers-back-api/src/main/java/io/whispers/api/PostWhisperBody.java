package io.whispers.api;

import java.util.Optional;
import java.util.UUID;

public record PostWhisperBody (
        String text,
        Optional<UUID> replyingTo
) {
}
