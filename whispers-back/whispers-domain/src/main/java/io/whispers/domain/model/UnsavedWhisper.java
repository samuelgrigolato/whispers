package io.whispers.domain.model;

/**
 * Value object that represents an unsaved whisper.
 */
public record UnsavedWhisper(
        User sender,
        String text
) {
}
