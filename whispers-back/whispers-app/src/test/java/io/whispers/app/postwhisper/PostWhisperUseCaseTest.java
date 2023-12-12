package io.whispers.app.postwhisper;

import io.whispers.domain.event.WhisperCreatedEvent;
import io.whispers.domain.event.WhisperCreatedEventPublisher;
import io.whispers.domain.model.Topic;
import io.whispers.domain.model.User;
import io.whispers.domain.model.Whisper;
import io.whispers.domain.model.UnsavedWhisper;
import io.whispers.domain.repository.WhisperRepository;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PostWhisperUseCaseTest {

    @Test
    void testExecute() {
        var whisper = new Whisper(
                UUID.fromString("4fa16e90-04f7-47cc-8dec-26d36a95fbf4"),
                new User("sender"),
                ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()),
                "text",
                Optional.of(new Topic("topic")),
                Collections.emptyList()
        );

        var whisperRepositoryMock = mock(WhisperRepository.class);
        when(whisperRepositoryMock.create(new UnsavedWhisper(new User("sender"), "text")))
                .thenReturn(whisper);

        var whisperEventPublisherMock = mock(WhisperCreatedEventPublisher.class);

        var subject = new PostWhisperUseCase(whisperRepositoryMock, whisperEventPublisherMock);

        var response = subject.execute(new PostWhisperRequest(
                new UnsavedWhisper(new User("sender"), "text"))
        );
        verify(whisperEventPublisherMock).publish(new WhisperCreatedEvent(
                UUID.fromString("4fa16e90-04f7-47cc-8dec-26d36a95fbf4"),
                "sender",
                "text",
                ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault())
        ));

        var expectedResponse = new PostWhisperResponse(
                UUID.fromString("4fa16e90-04f7-47cc-8dec-26d36a95fbf4"),
                "sender",
                ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()),
                "text",
                Optional.of("topic"),
                Collections.emptyList()
        );
        assertEquals(expectedResponse, response);
    }

}
