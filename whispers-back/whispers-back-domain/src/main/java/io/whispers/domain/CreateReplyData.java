package io.whispers.domain;

import java.util.UUID;

public record CreateReplyData(
        String text,
        String sender,
        UUID replyingTo
) {
}
