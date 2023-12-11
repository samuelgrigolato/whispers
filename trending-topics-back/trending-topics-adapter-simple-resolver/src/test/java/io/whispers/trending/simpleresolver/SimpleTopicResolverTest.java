package io.whispers.trending.simpleresolver;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleTopicResolverTest {

    @Test
    void testResolveTopic() {
        var subject = new SimpleTopicResolver();
        var result = subject.resolveTopic("text #topic text");
        assertEquals(Optional.of("topic"), result);
    }

    @Test
    void testNoTopic() {
        var subject = new SimpleTopicResolver();
        var result = subject.resolveTopic("text text");
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testMultipleTopicsPickTheFirst() {
        var subject = new SimpleTopicResolver();
        var result = subject.resolveTopic("text #topic1 #topic2 text");
        assertEquals(Optional.of("topic1"), result);
    }

}
