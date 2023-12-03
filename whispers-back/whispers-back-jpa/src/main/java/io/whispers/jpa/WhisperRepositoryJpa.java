package io.whispers.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface WhisperRepositoryJpa extends JpaRepository<WhisperJpa, UUID> {
}
