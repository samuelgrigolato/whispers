package io.whispers.api;

import io.whispers.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ContextConfiguration(classes = WhispersController.class)
@WebMvcTest(WhispersController.class)
class WhispersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WhisperRepository whisperRepository;

    @Test
    void shouldReturnEmptyResponse() throws Exception {
        when(this.whisperRepository.findMostRecent(10)).thenReturn(Collections.emptyList());
        this.mockMvc.perform(get("/whispers"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturnLatestWhispers() throws Exception {
        var whisper = mock(Whisper.class);
        when(whisper.getId()).thenReturn(UUID.fromString("51d0eaaa-0b61-44a9-9d63-cd672727b792"));
        when(whisper.getSender()).thenReturn("sender");
        when(whisper.getTopic()).thenReturn(Optional.of("topic"));
        when(whisper.getText()).thenReturn("text");
        when(whisper.getTimestamp()).thenReturn(ZonedDateTime.of(2023, 1, 10, 15, 30, 0, 0, ZoneId.of("Etc/UTC")));
        var reply = mock(Reply.class);
        when(reply.getSender()).thenReturn("replySender");
        when(reply.getTimestamp()).thenReturn(ZonedDateTime.of(2023, 1, 10, 15, 31, 0, 0, ZoneId.of("Etc/UTC")));
        when(reply.getText()).thenReturn("replyText");
        when(whisper.getReplies()).thenReturn(List.of(reply));
        when(this.whisperRepository.findMostRecent(10)).thenReturn(List.of(whisper));
        this.mockMvc.perform(get("/whispers"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    [
                        {
                            "id": "51d0eaaa-0b61-44a9-9d63-cd672727b792",
                            "sender": "sender",
                            "timestamp": "2023-01-10T15:30:00Z",
                            "text": "text",
                            "topic": "topic",
                            "replies": [
                                {
                                    "sender": "replySender",
                                    "timestamp": "2023-01-10T15:31:00Z",
                                    "text":"replyText"
                                }
                            ]
                        }
                    ]
                """));
    }

    @Test
    void shouldCreate() throws Exception {
        var whisper = mock(Whisper.class);
        when(whisper.getId()).thenReturn(UUID.fromString("51d0eaaa-0b61-44a9-9d63-cd672727b792"));
        when(whisper.getSender()).thenReturn("sender");
        when(whisper.getTopic()).thenReturn(Optional.empty());
        when(whisper.getText()).thenReturn("text");
        when(whisper.getTimestamp()).thenReturn(ZonedDateTime.of(2023, 1, 10, 15, 30, 0, 0, ZoneId.of("Etc/UTC")));
        when(whisper.getReplies()).thenReturn(Collections.emptyList());
        when(this.whisperRepository.create(new CreateWhisperData("text", "sender")))
                .thenReturn(whisper);
        this.mockMvc.perform(post("/whispers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "text": "text"
                            }
                        """)
                        .header("Authorization", "Bearer sender"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "id": "51d0eaaa-0b61-44a9-9d63-cd672727b792",
                        "sender": "sender",
                        "timestamp": "2023-01-10T15:30:00Z",
                        "text": "text",
                        "topic": null,
                        "replies": []
                    }
                """));
    }

    @Test
    void shouldCreateReply() throws Exception {
        var reply = mock(Reply.class);
        when(reply.getSender()).thenReturn("sender");
        when(reply.getTimestamp()).thenReturn(ZonedDateTime.of(2023, 1, 10, 10, 30, 0, 0, ZoneId.of("Etc/UTC")));
        when(reply.getText()).thenReturn("text");
        var createReplyData = new CreateReplyData(
                "text",
                "sender",
                UUID.fromString("530bbffb-455b-42e7-9759-23e508e89f03")
        );
        when(this.whisperRepository.createReply(createReplyData))
                .thenReturn(reply);
        this.mockMvc.perform(post("/whispers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "text": "text",
                                "replyingTo": "530bbffb-455b-42e7-9759-23e508e89f03"
                            }
                        """)
                        .header("Authorization", "Bearer sender"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "sender": "sender",
                        "timestamp": "2023-01-10T10:30:00Z",
                        "text": "text"
                    }
                """));
    }

}
