package io.whispers.jpa;

import io.whispers.domain.Whisper;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "whispers")
public class JpaWhisper {
    @Id
    private UUID id;

    @ManyToOne
    private JpaUser sender;

    private String text;

    private ZonedDateTime timestamp;

    @ManyToOne
    private JpaTopic topic;

    @OneToMany(mappedBy = "whisper")
    private List<JpaReply> replies;

    JpaWhisper() {}

    JpaWhisper(UUID id, JpaUser sender, String text, ZonedDateTime timestamp, JpaTopic topic, List<JpaReply> replies) {
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

    JpaUser getSender() {
        return sender;
    }

    public void setSender(JpaUser sender) {
        this.sender = sender;
    }

    String getText() {
        return text;
    }

    ZonedDateTime getTimestamp() {
        return timestamp;
    }

    JpaTopic getTopic() {
        return topic;
    }

    public void setTopic(JpaTopic topic) {
        this.topic = topic;
    }

    List<JpaReply> getReplies() {
        return replies;
    }

    Whisper toWhisper() {
        return new Whisper(
                this.id,
                this.sender.getUsername(),
                this.timestamp,
                this.text,
                Optional.ofNullable(this.topic).map(JpaTopic::getTopic),
                this.replies.stream().map(JpaReply::toReply).toList()
        );
    }
}
