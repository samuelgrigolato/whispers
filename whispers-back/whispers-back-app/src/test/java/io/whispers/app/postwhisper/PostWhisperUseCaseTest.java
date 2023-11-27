package io.whispers.app.postwhisper;

import io.whispers.domain.CreateWhisperData;
import io.whispers.domain.UserRepository;
import io.whispers.domain.Whisper;
import io.whispers.domain.WhisperRepository;
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
        var whisper = mock(Whisper.class);
        when(whisper.getId()).thenReturn(UUID.fromString("4fa16e90-04f7-47cc-8dec-26d36a95fbf4"));
        when(whisper.getSender()).thenReturn("sender");
        when(whisper.getTimestamp()).thenReturn(ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()));
        when(whisper.getText()).thenReturn("text");
        when(whisper.getTopic()).thenReturn(Optional.of("topic"));

        var whisperRepositoryMock = mock(WhisperRepository.class);
        when(whisperRepositoryMock.create(new CreateWhisperData("text", "sender")))
                .thenReturn(whisper);

        var userRepositoryMock = mock(UserRepository.class);

        var subject = new PostWhisperUseCase(whisperRepositoryMock, userRepositoryMock);

        var response = subject.execute(new PostWhisperRequest("sender", "text"));
        verify(userRepositoryMock).createIfNotExists("sender");

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
