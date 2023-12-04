package io.whispers.jpa;

import io.whispers.domain.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<JpaUser, UUID>, UserRepository {
    JpaUser findByUsername(String sender);

    default void createIfNotExists(String username) {
        JpaUser existing = findByUsername(username);
        if (existing == null) {
            JpaUser user = new JpaUser(
                    UUID.randomUUID(),
                    username
            );
            save(user);
        }
    }
}
