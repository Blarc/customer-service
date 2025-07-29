package com.github.blarc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record ErrorDto(
        String message,
        Integer errorCode,
        LocalDateTime timestamp
)
{
}
