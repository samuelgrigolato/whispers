package io.whispers.trending.top10publisherlambda;

import io.whispers.trending.app.publishtop10.PublishTop10UseCase;

public class Top10PublisherLambda {

    private final PublishTop10UseCase publishTop10UseCase;

    public Top10PublisherLambda(PublishTop10UseCase publishTop10UseCase) {
        this.publishTop10UseCase = publishTop10UseCase;
    }

    public void handleRequest() {
        this.publishTop10UseCase.execute();
    }
}
