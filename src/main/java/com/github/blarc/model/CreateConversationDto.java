package com.github.blarc.model;

import com.github.blarc.entity.ConversationTypeEnum;

public record CreateConversationDto(
        ConversationTypeEnum type
) {
}
