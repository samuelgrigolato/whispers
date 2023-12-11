package io.whispers.trending.simpleresolver;

import io.whispers.trending.domain.TopicResolver;

import java.util.Optional;
import java.util.regex.Pattern;

public class SimpleTopicResolver implements TopicResolver {

    private static final Pattern TOPIC_PATTERN = Pattern.compile("#(\\w+)", Pattern.MULTILINE);

    @Override
    public Optional<String> resolveTopic(String whisperText) {
        var matcher = TOPIC_PATTERN.matcher(whisperText);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }
}
