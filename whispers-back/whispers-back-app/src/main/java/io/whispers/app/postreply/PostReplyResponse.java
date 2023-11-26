package io.whispers.app.postreply;

import io.whispers.app.getmostrecentwhispers.RecentReplyView;
import io.whispers.domain.Reply;
import io.whispers.domain.Whisper;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public record PostReplyResponse(
        String sender,
        ZonedDateTime timestamp,
        String text
) {
    static PostReplyResponse from(Reply reply) {
        return new PostReplyResponse(
                reply.getSender(),
                reply.getTimestamp(),
                reply.getText()
        );
    }
}
