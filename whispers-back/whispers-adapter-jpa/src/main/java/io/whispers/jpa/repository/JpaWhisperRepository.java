package io.whispers.jpa.repository;

import io.whispers.jpa.entity.JpaWhisper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaWhisperRepository extends JpaRepository<JpaWhisper, UUID> {
}
