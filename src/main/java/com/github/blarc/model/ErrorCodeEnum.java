package com.github.blarc.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCodeEnum
{
    CONVERSATION_TYPE_EXISTS(101),
    CONVERSATION_WITH_ID_DOES_NOT_EXIST(102),
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
