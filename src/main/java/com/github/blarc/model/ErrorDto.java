package com.github.blarc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record ErrorDto(
        @JsonProperty("message") String message,
        @JsonProperty("errorCode") Integer errorCode,
        @JsonProperty("timestamp") LocalDateTime timestamp
)
{
}
