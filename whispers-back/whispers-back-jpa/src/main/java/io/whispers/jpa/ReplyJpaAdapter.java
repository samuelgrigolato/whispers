package io.whispers.jpa;

import io.whispers.domain.Reply;

import java.time.ZonedDateTime;

class ReplyJpaAdapter implements Reply {
    private ReplyJpa replyJpa;

    ReplyJpaAdapter(ReplyJpa replyJpa) {
        this.replyJpa = replyJpa;
    }

    @Override
    public String getSender() {
        return this.replyJpa.getSender().getUsername();
    }

    @Override
    public ZonedDateTime getTimestamp() {
        return this.replyJpa.getTimestamp();
    }

    @Override
    public String getText() {
        return this.replyJpa.getText();
    }
}
