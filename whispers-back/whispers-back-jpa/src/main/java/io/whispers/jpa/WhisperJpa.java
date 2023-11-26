package io.whispers.jpa;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "whispers")
public class WhisperJpa {
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

    public WhisperJpa() {}

    public WhisperJpa(UUID id, UserJpa sender, String text, ZonedDateTime timestamp, TopicJpa topic, List<ReplyJpa> replies) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
        this.topic = topic;
        this.replies = replies;
    }

    public UUID getId() {
        return id;
    }

    public UserJpa getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public TopicJpa getTopic() {
        return topic;
    }

    public List<ReplyJpa> getReplies() {
        return replies;
    }
}
