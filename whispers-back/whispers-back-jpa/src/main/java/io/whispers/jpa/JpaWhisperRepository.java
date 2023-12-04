package io.whispers.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaWhisperRepository extends JpaRepository<JpaWhisper, UUID> {
}
