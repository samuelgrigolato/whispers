package io.whispers.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "replies")
public class ReplyJpa {
    @Id
    private UUID id;

    private String text;

    private ZonedDateTime timestamp;

    @ManyToOne
    private UserJpa sender;

    @ManyToOne
    private WhisperJpa whisper;

    public ReplyJpa() {}

    public ReplyJpa(UUID id, String text, ZonedDateTime timestamp, UserJpa sender, WhisperJpa whisper) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.sender = sender;
        this.whisper = whisper;
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public UserJpa getSender() {
        return sender;
    }
}
