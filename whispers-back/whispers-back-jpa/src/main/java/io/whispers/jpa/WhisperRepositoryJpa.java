package io.whispers.jpa;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface WhisperRepositoryJpa extends JpaRepository<WhisperJpa, UUID> {
    List<WhisperJpa> findByOrderByTimestampDesc(Limit limit);
}
