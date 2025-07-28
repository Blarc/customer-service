package com.github.blarc.model;

import com.github.blarc.entity.ConversationTypeEnum;

public record ConversationDto(
    Long id,
    ConversationTypeEnum type,
    String username
) {

}
