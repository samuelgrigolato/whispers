package io.whispers.trending.domain;

import java.util.UUID;

public record TopicResolutionEvent(
        UUID whisperUuid,
        String topic
) {
}
