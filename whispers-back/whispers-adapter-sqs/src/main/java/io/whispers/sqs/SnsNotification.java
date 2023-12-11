package io.whispers.sqs;

import com.fasterxml.jackson.annotation.JsonProperty;

record SnsNotification(
        @JsonProperty("Message")
        String message
) {
}
