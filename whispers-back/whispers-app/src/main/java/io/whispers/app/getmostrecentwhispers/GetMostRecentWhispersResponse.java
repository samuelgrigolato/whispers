package io.whispers.app.getmostrecentwhispers;

import java.util.Collection;

public record GetMostRecentWhispersResponse(
        Collection<RecentWhisperOutput> whispers
) {
}
