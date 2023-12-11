package io.whispers.api;

import io.whispers.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = TopicsController.class)
@WebMvcTest(TopicsController.class)
class TopicsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopicRepository topicRepository;

    @Test
    void shouldReturnEmptyResponse() throws Exception {
        when(this.topicRepository.getTrending())
                .thenReturn(Collections.emptyList());
        this.mockMvc.perform(get("/topics/trending"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturnTrendingTopics() throws Exception {
        var topic = new TrendingTopic("topic1", 10L);
        var topics = List.of(topic);
        when(this.topicRepository.getTrending())
                .thenReturn(topics);
        this.mockMvc.perform(get("/topics/trending"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    [
                        {
                            "topic": "topic1",
                            "whispers": 10
                        }
                    ]
                """));
    }
}
