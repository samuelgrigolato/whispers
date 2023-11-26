package io.whispers.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepositoryJpa extends JpaRepository<UserJpa, UUID> {
    UserJpa findByUsername(String sender);
}
