package io.whispers.domain;

import java.util.Collection;

public interface WhisperRepository {

    Collection<Whisper> findMostRecent(int limit);

}
