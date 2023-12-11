package io.whispers.trending.domain.event;

import java.util.UUID;

public record TopicResolvedEvent(
        UUID whisperUuid,
        String topic
) {
}
