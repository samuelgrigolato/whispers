package io.whispers.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "topics")
public class TopicJpa {
    @Id
    private UUID id;
    private String topic;

    public UUID getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }
}
