package io.whispers.jpa;

import io.whispers.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private ReplyRepositoryJpa replyRepositoryJpa;

    @Override
    public Collection<Whisper> findMostRecent(int limit) {
        Pageable pageable = PageRequest.ofSize(limit).withSort(Sort.by("timestamp").descending());
        return this.whisperRepositoryJpa.findAll(pageable)
                .stream()
                .map(x -> (Whisper) new WhisperJpaAdapter(x))
                .toList();
    }

    @Override
    public Collection<Whisper> findMostRecentBySender(String sender, int limit) {
        var whisperJpa = new WhisperJpa();
        var userJpa = new UserJpa();
        userJpa.setUsername(sender);
        whisperJpa.setSender(userJpa);
        Pageable pageable = PageRequest.ofSize(limit).withSort(Sort.by("timestamp").descending());
        return this.whisperRepositoryJpa.findAll(Example.of(whisperJpa), pageable)
                .stream()
                .map(x -> (Whisper) new WhisperJpaAdapter(x))
                .toList();
    }

    @Override
    public Collection<Whisper> findMostRecentByTopic(String topic, int limit) {
        var whisperJpa = new WhisperJpa();
        var topicJpa = new TopicJpa();
        topicJpa.setTopic(topic);
        whisperJpa.setTopic(topicJpa);
        Pageable pageable = PageRequest.ofSize(limit).withSort(Sort.by("timestamp").descending());
        return this.whisperRepositoryJpa.findAll(Example.of(whisperJpa), pageable)
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

    @Override
    public Reply createReply(CreateReplyData data) {
        var sender = this.userRepositoryJpa.findByUsername(data.sender());
        if (sender == null) {
            throw new IllegalArgumentException("sender not found");
        }
        var whisper = this.whisperRepositoryJpa.findById(data.replyingTo())
                .orElseThrow();
        var reply = new ReplyJpa(
                UUID.randomUUID(),
                data.text(),
                ZonedDateTime.now(),
                sender,
                whisper
        );
        this.replyRepositoryJpa.save(reply);
        return new ReplyJpaAdapter(reply);
    }
}
