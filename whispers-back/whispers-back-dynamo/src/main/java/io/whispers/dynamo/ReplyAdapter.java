package io.whispers.dynamo;

import io.whispers.domain.Reply;

import java.time.ZonedDateTime;

public record ReplyAdapter (String sender, ZonedDateTime timestamp, String text) implements Reply {

    @Override
    public String getSender() {
        return sender();
    }

    @Override
    public ZonedDateTime getTimestamp() {
        return timestamp();
    }

    @Override
    public String getText() {
        return text();
    }
}
