package io.whispers.jpa;

import io.whispers.domain.Reply;
import io.whispers.domain.Whisper;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public class WhisperJpaAdapter implements Whisper {
    private WhisperJpa whisperJpa;

    public WhisperJpaAdapter(WhisperJpa whisperJpa) {
        this.whisperJpa = whisperJpa;
    }

    @Override
    public UUID getId() {
        return this.whisperJpa.getId();
    }

    @Override
    public String getSender() {
        return this.whisperJpa.getSender().getUsername();
    }

    @Override
    public ZonedDateTime getTimestamp() {
        return this.whisperJpa.getTimestamp();
    }

    @Override
    public String getText() {
        return this.whisperJpa.getText();
    }

    @Override
    public Optional<String> getTopic() {
        return Optional.ofNullable(this.whisperJpa.getTopic())
                .map(TopicJpa::getTopic);
    }

    @Override
    public Collection<Reply> getReplies() {
        return this.whisperJpa
                .getReplies().stream()
                .sorted(Comparator.comparing(ReplyJpa::getTimestamp))
                .map(x -> (Reply) new ReplyJpaAdapter(x))
                .toList();
    }
}
