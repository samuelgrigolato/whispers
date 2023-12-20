package io.whispers.trending.whispercreatedlambda;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.whispers.trending.app.resolvetopics.ResolveTopicsUseCase;
import io.whispers.trending.domain.event.WhisperCreatedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WhisperCreatedLambdaTest {

    @Test
    void testHandleRequest() {
        var useCaseMock = mock(ResolveTopicsUseCase.class);
        var subject = new WhisperCreatedLambda(new ObjectMapper(), useCaseMock);
        var sqsEvent = new SQSEvent();
        var sqsMessage = new SQSEvent.SQSMessage();
        sqsMessage.setBody("""
        {
            "Message": "{\\\"uuid\\\":\\\"00000000-0000-0000-0000-000000000000\\\",\\\"text\\\":\\\"text\\\"}"
        }
        """);
        sqsEvent.setRecords(List.of(sqsMessage));
        subject.handleRequest(sqsEvent, null);
        verify(useCaseMock).execute(List.of(
                new WhisperCreatedEvent(
                        UUID.fromString("00000000-0000-0000-0000-000000000000"),
                        "text"
                )
        ));
    }

}
