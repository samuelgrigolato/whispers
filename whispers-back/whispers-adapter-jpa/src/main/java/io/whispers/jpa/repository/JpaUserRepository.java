package io.whispers.jpa.repository;

import io.whispers.jpa.entity.JpaUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<JpaUser, UUID> {

    Optional<JpaUser> findByUsername(String sender);

}
