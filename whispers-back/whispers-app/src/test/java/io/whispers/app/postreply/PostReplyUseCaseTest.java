package io.whispers.app.postreply;

import io.whispers.domain.model.Reply;
import io.whispers.domain.model.UnsavedReply;
import io.whispers.domain.model.User;
import io.whispers.domain.repository.WhisperRepository;
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
                new User("sender"),
                ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()),
                "text"
        );

        var whisperRepositoryMock = mock(WhisperRepository.class);
        var createReplyData = new UnsavedReply(
                new User("sender"),
                "text"
        );
        when(whisperRepositoryMock.createReply(UUID.fromString("530bbffb-455b-42e7-9759-23e508e89f03"), createReplyData))
                .thenReturn(reply);

        var subject = new PostReplyUseCase(whisperRepositoryMock);

        var response = subject.execute(new PostReplyRequest(
                new UnsavedReply(
                    new User("sender"),
                    "text"
                ),
                UUID.fromString("530bbffb-455b-42e7-9759-23e508e89f03")
        ));

        var expectedResponse = new PostReplyResponse(
                "sender",
                ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()),
                "text"
        );
        assertEquals(expectedResponse, response);
    }

}
