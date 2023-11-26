package io.whispers.app;

import io.whispers.domain.Reply;
import io.whispers.domain.Whisper;
import io.whispers.domain.WhisperRepository;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GetMostRecentWhispersUseCaseTest {

    @Test
    void testExecute() {
        var whisper = mock(Whisper.class);
        when(whisper.getId()).thenReturn(UUID.fromString("4fa16e90-04f7-47cc-8dec-26d36a95fbf4"));
        when(whisper.getSender()).thenReturn("sender");
        when(whisper.getTimestamp()).thenReturn(ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()));
        when(whisper.getText()).thenReturn("text");
        when(whisper.getTopic()).thenReturn(Optional.of("topic"));
        var reply = mock(Reply.class);
        when(reply.getSender()).thenReturn("replySender");
        when(reply.getText()).thenReturn("replyText");
        when(reply.getTimestamp()).thenReturn(ZonedDateTime.of(2023, 1, 10, 10, 31, 0, 0, ZoneId.systemDefault()));
        when(whisper.getReplies()).thenReturn(List.of(reply));
        var whispers = List.of(whisper);

        var whisperRepositoryMock = mock(WhisperRepository.class);
        when(whisperRepositoryMock.findMostRecent(10))
                .thenReturn(whispers);

        var subject = new GetMostRecentWhispersUseCase(whisperRepositoryMock);

        var result = subject.execute();

        verify(whisperRepositoryMock).findMostRecent(10);

        assertEquals(1, result.size());
        var whisperResult = result.iterator().next();

        var expectedWhisperResult = new RecentWhisperView(
                UUID.fromString("4fa16e90-04f7-47cc-8dec-26d36a95fbf4"),
                "sender",
                ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()),
                "text",
                Optional.of("topic"),
                List.of(new RecentWhisperReplyView(
                        "replySender",
                        ZonedDateTime.of(2023, 1, 10, 10, 31, 0, 0, ZoneId.systemDefault()),
                        "replyText"
                ))
        );
        assertEquals(expectedWhisperResult, whisperResult);
    }

}
