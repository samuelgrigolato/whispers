package io.whispers.app.getmostrecentwhispers;

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
     void testExecuteNotFiltering() {
        var whisper = new Whisper(
                UUID.fromString("4fa16e90-04f7-47cc-8dec-26d36a95fbf4"),
                "sender",
                ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()),
                "text",
                Optional.of("topic"),
                List.of(new Reply(
                        "replySender",
                        ZonedDateTime.of(2023, 1, 10, 10, 31, 0, 0, ZoneId.systemDefault()),
                        "replyText"
                ))
        );
        var whispers = List.of(whisper);

        var whisperRepositoryMock = mock(WhisperRepository.class);
        when(whisperRepositoryMock.findMostRecent(10))
                .thenReturn(whispers);

        var subject = new GetMostRecentWhispersUseCase(whisperRepositoryMock);

        var response = subject.execute(new GetMostRecentWhispersRequest(Optional.empty()));
        var result = response.whispers();

        assertEquals(1, result.size());
        var whisperResult = result.iterator().next();

        var expectedWhisperResult = new RecentWhisperView(
                UUID.fromString("4fa16e90-04f7-47cc-8dec-26d36a95fbf4"),
                "sender",
                ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.systemDefault()),
                "text",
                Optional.of("topic"),
                List.of(new RecentReplyView(
                        "replySender",
                        ZonedDateTime.of(2023, 1, 10, 10, 31, 0, 0, ZoneId.systemDefault()),
                        "replyText"
                ))
        );
        assertEquals(expectedWhisperResult, whisperResult);
    }

    @Test
    void testExecuteFilteringBySender() {
        var whisperRepositoryMock = mock(WhisperRepository.class);

        var subject = new GetMostRecentWhispersUseCase(whisperRepositoryMock);
        subject.execute(new GetMostRecentWhispersRequest(Optional.of(
                new MostRecentFilterBySender("sender")
        )));

        verify(whisperRepositoryMock).findMostRecentBySender("sender", 10);
    }

    @Test
    void testExecuteFilteringByTopic() {
        var whisperRepositoryMock = mock(WhisperRepository.class);

        var subject = new GetMostRecentWhispersUseCase(whisperRepositoryMock);
        subject.execute(new GetMostRecentWhispersRequest(Optional.of(
                new MostRecentFilterByTopic("topic")
        )));

        verify(whisperRepositoryMock).findMostRecentByTopic("topic", 10);
    }

}
