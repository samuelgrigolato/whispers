package io.whispers.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReplyRepositoryJpa extends JpaRepository<ReplyJpa, UUID> {
}
