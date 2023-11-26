package io.whispers.domain;

import java.time.ZonedDateTime;

public interface Reply {
    String getSender();
    ZonedDateTime getTimestamp();
    String getText();
}
