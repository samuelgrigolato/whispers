package io.whispers.domain;

import java.util.Collection;
import java.util.UUID;

public interface WhisperRepository {

    /**
     * @param limit has to be between 1 and 100
     */
    Collection<Whisper> findMostRecentBySender(String sender, int limit);

    /**
     * @param limit has to be between 1 and 100
     */
    Collection<Whisper> findMostRecentByTopic(String topic, int limit);

    /**
     * @param limit has to be between 1 and 100
     */
    Collection<Whisper> findMostRecent(int limit);

    Whisper create(WhisperCreationRequest data);

    Reply createReply(ReplyRequest data);

    void updateTopic(UUID whisperId, String topic);
}
