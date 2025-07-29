package com.github.blarc.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCodeEnum
{
    CONVERSATION_DOES_NOT_EXIST(101),
    CONVERSATION_NOT_TAKEN(102),
    CONVERSATION_NOT_ALLOWED(103),
    CONVERSATION_ALREADY_TAKEN(104),
    UNKNOWN(100);

    private final int value;

    ErrorCodeEnum(int value)
    {
        this.value = value;
    }

    @JsonValue
    public int getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return String.valueOf(value);
    }
}
