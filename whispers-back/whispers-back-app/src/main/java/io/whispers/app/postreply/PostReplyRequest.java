package io.whispers.app.postreply;

import java.util.UUID;

public record PostReplyRequest(
        String sender,
        String text,
        UUID replyingTo
) {
}
