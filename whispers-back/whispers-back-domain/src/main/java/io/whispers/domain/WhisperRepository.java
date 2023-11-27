package io.whispers.domain;

import java.util.Collection;
import java.util.Optional;

public interface WhisperRepository {

    Collection<Whisper> findMostRecent(Optional<String> sender, Optional<String> topic, int limit);

    Whisper create(CreateWhisperData data);

    Reply createReply(CreateReplyData data);

}
