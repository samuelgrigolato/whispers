package io.whispers.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpa {
    @Id
    private UUID id;
    private String username;

    UserJpa() {}

    UserJpa(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    UUID getId() {
        return id;
    }

    String getUsername() {
        return username;
    }
}
