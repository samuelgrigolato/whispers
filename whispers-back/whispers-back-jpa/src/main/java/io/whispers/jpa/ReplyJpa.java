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

    ReplyJpa() {}

    ReplyJpa(UUID id, String text, ZonedDateTime timestamp, UserJpa sender, WhisperJpa whisper) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.sender = sender;
        this.whisper = whisper;
    }

    UUID getId() {
        return id;
    }

    String getText() {
        return text;
    }

    ZonedDateTime getTimestamp() {
        return timestamp;
    }

    UserJpa getSender() {
        return sender;
    }
}
