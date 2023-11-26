package io.whispers.jpa;

import io.whispers.domain.CreateWhisperData;
import io.whispers.domain.Whisper;
import io.whispers.domain.WhisperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Repository
class WhisperRepositoryJpaAdapter implements WhisperRepository {

    @Autowired
    private WhisperRepositoryJpa whisperRepositoryJpa;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Override
    public Collection<Whisper> findMostRecent(int limit) {
        return this.whisperRepositoryJpa.findByOrderByTimestampDesc(Limit.of(limit))
                .stream()
                .map(x -> (Whisper) new WhisperJpaAdapter(x))
                .toList();
    }

    @Override
    public Whisper create(CreateWhisperData data) {
        var sender = this.userRepositoryJpa.findByUsername(data.sender());
        if (sender == null) {
            throw new IllegalArgumentException("sender not found");
        }
        var whisper = new WhisperJpa(
                UUID.randomUUID(),
                sender,
                data.text(),
                ZonedDateTime.now(),
                null,
                Collections.emptyList()
        );
        this.whisperRepositoryJpa.save(whisper);
        return new WhisperJpaAdapter(whisper);
    }
}
