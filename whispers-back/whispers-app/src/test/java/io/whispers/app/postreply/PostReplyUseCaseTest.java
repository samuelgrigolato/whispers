package io.whispers.app.postreply;

import io.whispers.domain.*;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PostReplyUseCaseTest {

    @Test
    void testExecute() {
        var reply = new Reply(
                "sender",
                ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()),
                "text"
        );

        var whisperRepositoryMock = mock(WhisperRepository.class);
        var createReplyData = new ReplyRequest(
                "text",
                "sender",
                UUID.fromString("530bbffb-455b-42e7-9759-23e508e89f03")
        );
        when(whisperRepositoryMock.createReply(createReplyData))
                .thenReturn(reply);

        var userRepositoryMock = mock(UserRepository.class);

        var subject = new PostReplyUseCase(whisperRepositoryMock, userRepositoryMock);

        var response = subject.execute(new PostReplyRequest(
                "sender",
                "text",
                UUID.fromString("530bbffb-455b-42e7-9759-23e508e89f03")
        ));
        verify(userRepositoryMock).createIfNotExists("sender");

        var expectedResponse = new PostReplyResponse(
                "sender",
                ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()),
                "text"
        );
        assertEquals(expectedResponse, response);
    }

}
