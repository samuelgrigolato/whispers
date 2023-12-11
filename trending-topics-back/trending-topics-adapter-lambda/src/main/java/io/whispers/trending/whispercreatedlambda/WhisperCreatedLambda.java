package io.whispers.trending.whispercreatedlambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.trending.app.resolvetopics.ResolveTopicsUseCase;
import io.whispers.trending.domain.event.WhisperCreatedEvent;

import java.util.HashMap;
import java.util.stream.Collectors;

public class WhisperCreatedLambda {

    private ObjectMapper objectMapper;
    private final ResolveTopicsUseCase resolveTopicsUseCase;

    public WhisperCreatedLambda(ObjectMapper objectMapper,
                                ResolveTopicsUseCase resolveTopicsUseCase) {
        this.objectMapper = objectMapper;
        this.resolveTopicsUseCase = resolveTopicsUseCase;
    }

    public void handleRequest(SQSEvent input, Context context) {
        var events = input.getRecords().stream()
                .map(this::parseMessage)
                .collect(Collectors.toList());
        this.resolveTopicsUseCase.execute(events);
    }

    private WhisperCreatedEvent parseMessage(SQSEvent.SQSMessage message) {
        try {
            var typeRef = new TypeReference<HashMap<String, String>>() {};
            var snsMessage = objectMapper.readValue(message.getBody(), typeRef);
            return objectMapper.readValue(snsMessage.get("Message"), WhisperCreatedEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to parse", e);
        }
    }
}
