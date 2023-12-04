package io.whispers.domain;

import java.util.UUID;

public record ReplyRequest(
        String text,
        String sender,
        UUID replyingTo
) {
}
