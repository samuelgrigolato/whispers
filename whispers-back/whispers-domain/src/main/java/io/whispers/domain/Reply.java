package io.whispers.domain;

import java.time.ZonedDateTime;

public record Reply (
        String sender,
        ZonedDateTime timestamp,
        String text) {
}
