package io.whispers.app;

import java.time.ZonedDateTime;

public record RecentWhisperReplyView(
        String sender,
        ZonedDateTime timestamp,
        String text
) {
}
