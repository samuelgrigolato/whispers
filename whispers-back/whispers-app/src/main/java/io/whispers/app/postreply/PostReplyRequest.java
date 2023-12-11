package io.whispers.app.postreply;

import io.whispers.domain.model.UnsavedReply;

import java.util.UUID;

public record PostReplyRequest(
        UnsavedReply reply,
        UUID replyingTo
) {
}
