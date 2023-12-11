package io.whispers.app.postwhisper;

import io.whispers.domain.*;
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
                "sender",
                ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()),
                "text",
                Optional.of("topic"),
                Collections.emptyList()
        );

        var whisperRepositoryMock = mock(WhisperRepository.class);
        when(whisperRepositoryMock.create(new WhisperCreationRequest("text", "sender")))
                .thenReturn(whisper);

        var userRepositoryMock = mock(UserRepository.class);
        var whisperEventPublisherMock = mock(WhisperEventPublisher.class);

        var subject = new PostWhisperUseCase(whisperRepositoryMock, userRepositoryMock, whisperEventPublisherMock);

        var response = subject.execute(new PostWhisperRequest("sender", "text"));
        verify(userRepositoryMock).createIfNotExists("sender");
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
