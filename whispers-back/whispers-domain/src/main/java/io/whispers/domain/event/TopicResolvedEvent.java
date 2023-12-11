package io.whispers.domain.event;

import java.util.UUID;

/**
 * Event that is received when a topic is resolved.
 */
public record TopicResolvedEvent(
        UUID whisperUuid,
        String topic
) {
}
