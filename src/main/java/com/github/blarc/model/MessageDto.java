package com.github.blarc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record MessageDto(
        String message,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime timestamp,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        UserDto user
) {
}
