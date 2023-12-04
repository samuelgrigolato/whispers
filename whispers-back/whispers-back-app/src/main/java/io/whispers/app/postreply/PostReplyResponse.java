package io.whispers.app.postreply;

import io.whispers.domain.Reply;

import java.time.ZonedDateTime;

public record PostReplyResponse(
        String sender,
        ZonedDateTime timestamp,
        String text
) {
    static PostReplyResponse from(Reply reply) {
        return new PostReplyResponse(
                reply.sender(),
                reply.timestamp(),
                reply.text()
        );
    }
}
