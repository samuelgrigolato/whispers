package io.whispers.jpa.repository;

import io.whispers.jpa.entity.JpaReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaReplyRepository extends JpaRepository<JpaReply, UUID> {
}
