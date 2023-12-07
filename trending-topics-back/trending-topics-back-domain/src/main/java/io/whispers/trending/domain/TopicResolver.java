package io.whispers.trending.domain;

import java.util.Optional;

public interface TopicResolver {
    Optional<String> resolveTopic(String whisperText);
}
