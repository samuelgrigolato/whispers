package io.whispers.domain;

public interface WhisperEventPublisher {
    void publish(WhisperCreatedEvent event);
}
