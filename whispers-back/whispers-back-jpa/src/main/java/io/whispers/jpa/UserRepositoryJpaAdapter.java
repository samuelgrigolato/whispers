package io.whispers.jpa;

import io.whispers.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepositoryJpaAdapter implements UserRepository {

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Override
    public void createIfNotExists(String username) {
        UserJpa existing = this.userRepositoryJpa.findByUsername(username);
        if (existing == null) {
            UserJpa user = new UserJpa(
                    UUID.randomUUID(),
                    username
            );
            this.userRepositoryJpa.save(user);
        }
    }
}
