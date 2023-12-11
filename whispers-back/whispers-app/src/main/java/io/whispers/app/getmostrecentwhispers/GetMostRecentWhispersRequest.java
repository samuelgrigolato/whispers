package io.whispers.app.getmostrecentwhispers;

import java.util.Optional;

public record GetMostRecentWhispersRequest(MostRecentFilter filter) {

    public static GetMostRecentWhispersRequest filterBySender(String sender) {
        return new GetMostRecentWhispersRequest(new MostRecentFilterBySender(sender));
    }

    public static GetMostRecentWhispersRequest filterByTopic(String topic) {
        return new GetMostRecentWhispersRequest(new MostRecentFilterByTopic(topic));
    }

    public static GetMostRecentWhispersRequest noFilter() {
        return new GetMostRecentWhispersRequest(new MostRecentFilterAll());
    }

}
