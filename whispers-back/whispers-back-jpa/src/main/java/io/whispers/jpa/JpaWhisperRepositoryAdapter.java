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
class JpaWhisperRepositoryAdapter implements WhisperRepository {

    @Autowired
    private JpaWhisperRepository jpaWhisperRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaReplyRepository jpaReplyRepository;

    @Autowired
    private JpaTopicRepository jpaTopicRepository;

    @Override
    public Collection<Whisper> findMostRecent(int limit) {
        Pageable pageable = PageRequest.ofSize(limit).withSort(Sort.by("timestamp").descending());
        return this.jpaWhisperRepository.findAll(pageable)
                .stream()
                .map(JpaWhisper::toWhisper)
                .toList();
    }

    @Override
    public Collection<Whisper> findMostRecentBySender(String sender, int limit) {
        var whisperJpa = new JpaWhisper();
        var userJpa = new JpaUser();
        userJpa.setUsername(sender);
        whisperJpa.setSender(userJpa);
        Pageable pageable = PageRequest.ofSize(limit).withSort(Sort.by("timestamp").descending());
        return this.jpaWhisperRepository.findAll(Example.of(whisperJpa), pageable)
                .stream()
                .map(JpaWhisper::toWhisper)
                .toList();
    }

    @Override
    public Collection<Whisper> findMostRecentByTopic(String topic, int limit) {
        var whisperJpa = new JpaWhisper();
        var topicJpa = new JpaTopic();
        topicJpa.setTopic(topic);
        whisperJpa.setTopic(topicJpa);
        Pageable pageable = PageRequest.ofSize(limit).withSort(Sort.by("timestamp").descending());
        return this.jpaWhisperRepository.findAll(Example.of(whisperJpa), pageable)
                .stream()
                .map(JpaWhisper::toWhisper)
                .toList();
    }

    @Override
    public Whisper create(WhisperCreationRequest data) {
        var sender = this.jpaUserRepository.findByUsername(data.sender());
        if (sender == null) {
            throw new IllegalArgumentException("sender not found");
        }
        var whisper = new JpaWhisper(
                UUID.randomUUID(),
                sender,
                data.text(),
                ZonedDateTime.now(),
                null,
                Collections.emptyList()
        );
        this.jpaWhisperRepository.save(whisper);
        return whisper.toWhisper();
    }

    @Override
    public Reply createReply(ReplyRequest data) {
        var sender = this.jpaUserRepository.findByUsername(data.sender());
        if (sender == null) {
            throw new IllegalArgumentException("sender not found");
        }
        var whisper = this.jpaWhisperRepository.findById(data.replyingTo())
                .orElseThrow();
        var reply = new JpaReply(
                UUID.randomUUID(),
                data.text(),
                ZonedDateTime.now(),
                sender,
                whisper
        );
        this.jpaReplyRepository.save(reply);
        return reply.toReply();
    }

    @Override
    public void updateTopic(UUID whisperId, String topic) {
        var whisperJpa = this.jpaWhisperRepository.findById(whisperId)
                .orElseThrow();
        var topicJpa = this.jpaTopicRepository.findByTopic(topic)
                .orElseGet(() -> {
                    var newTopic = new JpaTopic();
                    newTopic.setId(UUID.randomUUID());
                    newTopic.setTopic(topic);
                    return this.jpaTopicRepository.save(newTopic);
                });
        whisperJpa.setTopic(topicJpa);
        this.jpaWhisperRepository.save(whisperJpa);
    }
}
