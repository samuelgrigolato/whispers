package io.whispers.jpa;

import io.whispers.domain.Whisper;
import io.whispers.domain.WhisperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
class WhisperRepositoryJpaAdapter implements WhisperRepository {

    @Autowired
    private WhisperRepositoryJpa whisperRepositoryJpa;

    @Override
    public Collection<Whisper> findMostRecent(int limit) {
        return this.whisperRepositoryJpa.findByOrderByTimestampDesc(Limit.of(limit))
                .stream()
                .map(x -> (Whisper) new WhisperJpaAdapter(x))
                .toList();
    }
}
