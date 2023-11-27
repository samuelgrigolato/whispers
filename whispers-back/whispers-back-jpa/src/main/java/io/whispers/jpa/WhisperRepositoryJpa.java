package io.whispers.jpa;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface WhisperRepositoryJpa extends JpaRepository<WhisperJpa, UUID> {
    List<WhisperJpa> findBySenderUsernameAndTopicTopicOrderByTimestampDesc(Optional<String> sender, Optional<String> topic, Limit of);
}
