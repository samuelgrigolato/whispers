package io.whispers.app.getmostrecentwhispers;

import java.time.ZonedDateTime;

public record RecentReplyOutput(
        String sender,
        ZonedDateTime timestamp,
        String text
) {
}
