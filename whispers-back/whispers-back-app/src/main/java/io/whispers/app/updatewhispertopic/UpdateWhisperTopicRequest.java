package io.whispers.app.updatewhispertopic;

import io.whispers.domain.TopicResolutionEvent;

public record UpdateWhisperTopicRequest(
        TopicResolutionEvent event
) {
}
