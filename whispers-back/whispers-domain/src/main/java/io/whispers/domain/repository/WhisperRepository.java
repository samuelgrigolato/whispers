package io.whispers.domain.repository;

import io.whispers.domain.model.*;

import java.util.Collection;
import java.util.UUID;

public interface WhisperRepository {

    Collection<Whisper> findMostRecentBySender(String sender, int limit);

    Collection<Whisper> findMostRecentByTopic(String topic, int limit);

    Collection<Whisper> findMostRecent(int limit);

    Whisper create(UnsavedWhisper data);

    Reply createReply(UUID whisperId, UnsavedReply data);

    void updateTopic(UUID whisperId, String topic);
}
