package io.whispers.app.getmostrecentwhispers;

import java.util.Optional;

public record GetMostRecentWhispersRequest(
        Optional<String> topic,
        Optional<String> sender
) {
}
