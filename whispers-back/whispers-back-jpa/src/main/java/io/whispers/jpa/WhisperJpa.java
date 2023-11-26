package io.whispers.jpa;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "whispers")
class WhisperJpa {
    @Id
    private UUID id;

    @ManyToOne
    private UserJpa sender;

    private String text;

    private ZonedDateTime timestamp;

    @ManyToOne
    private TopicJpa topic;

    @OneToMany(mappedBy = "whisper")
    private List<ReplyJpa> replies;

    WhisperJpa() {}

    WhisperJpa(UUID id, UserJpa sender, String text, ZonedDateTime timestamp, TopicJpa topic, List<ReplyJpa> replies) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
        this.topic = topic;
        this.replies = replies;
    }

    UUID getId() {
        return id;
    }

    UserJpa getSender() {
        return sender;
    }

    String getText() {
        return text;
    }

    ZonedDateTime getTimestamp() {
        return timestamp;
    }

    TopicJpa getTopic() {
        return topic;
    }

    List<ReplyJpa> getReplies() {
        return replies;
    }
}
