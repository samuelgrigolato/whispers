package io.whispers.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "topics")
public class JpaTopic {
    @Id
    private UUID id;
    private String topic;

    UUID getId() {
        return id;
    }

    String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
