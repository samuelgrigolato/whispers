package io.whispers.app.getmostrecentwhispers;

import java.time.ZonedDateTime;

public record RecentReplyView(
        String sender,
        ZonedDateTime timestamp,
        String text
) {
}
