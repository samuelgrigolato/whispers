package io.whispers.trending.top10publisherlambda;

import io.whispers.trending.app.publishtop10.PublishTop10UseCase;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class Top10PublisherLambdaTest {

    @Test
    void testHandleRequest() {
        var useCaseMock = mock(PublishTop10UseCase.class);
        var subject = new Top10PublisherLambda(useCaseMock);
        subject.handleRequest();
        verify(useCaseMock).execute();
    }

}
