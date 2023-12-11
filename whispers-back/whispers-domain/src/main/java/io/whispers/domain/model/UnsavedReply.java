package io.whispers.domain.model;

/**
 * Value object that represents an unsaved reply.
 */
public record UnsavedReply(
        User sender,
        String text
) {
}
