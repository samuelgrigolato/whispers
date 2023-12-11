package io.whispers.app.updatewhispertopic;

import io.whispers.domain.event.TopicResolvedEvent;

public record UpdateWhisperTopicRequest(
        TopicResolvedEvent event
) {
}
