package com.github.blarc.model;

import java.time.OffsetDateTime;

public record ErrorDto(
        String message,
        Integer errorCode,
        OffsetDateTime timestamp
)
{
}
