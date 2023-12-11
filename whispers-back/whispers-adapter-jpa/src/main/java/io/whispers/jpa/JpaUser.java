package io.whispers.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users")
public class JpaUser {
    @Id
    private UUID id;
    private String username;

    JpaUser() {}

    JpaUser(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    UUID getId() {
        return id;
    }

    String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
