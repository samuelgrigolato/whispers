package io.whispers.domain;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface Whisper {
    UUID getId();
    String getSender();
    ZonedDateTime getTimestamp();
    String getText();
    Optional<String> getTopic();
    Collection<Reply> getReplies();
}
