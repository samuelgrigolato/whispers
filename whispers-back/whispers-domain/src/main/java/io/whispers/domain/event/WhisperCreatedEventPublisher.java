package io.whispers.domain.event;


public interface WhisperCreatedEventPublisher {
    void publish(WhisperCreatedEvent event);
}
