package io.whispers.jpa;

import io.whispers.domain.Reply;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "replies")
public class JpaReply {
    @Id
    private UUID id;

    private String text;

    private ZonedDateTime timestamp;

    @ManyToOne
    private JpaUser sender;

    @ManyToOne
    private JpaWhisper whisper;

    JpaReply() {}

    JpaReply(UUID id, String text, ZonedDateTime timestamp, JpaUser sender, JpaWhisper whisper) {
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

    JpaUser getSender() {
        return sender;
    }

    Reply toReply() {
        return new Reply(
                this.sender.getUsername(),
                this.timestamp,
                this.text
        );
    }
}
