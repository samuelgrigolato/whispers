package io.whispers.domain;

public interface UserRepository {
    void createIfNotExists(String username);
}
