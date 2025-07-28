package com.github.blarc.service;

import com.github.blarc.entity.*;
import com.github.blarc.exception.ExpectedCustomerServiceException;
import com.github.blarc.model.ConversationDto;
import com.github.blarc.model.ErrorCodeEnum;
import com.github.blarc.model.MessageDto;
import com.github.blarc.model.UserDto;
import com.github.blarc.repository.ConversationRepository;
import com.github.blarc.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ConversationService {

    @Inject
    ConversationRepository conversationRepository;
    @Inject
    UserRepository userRepository;

    public List<ConversationDto> getConversations(String username, Boolean isOperator) {
        if (isOperator) {
            return conversationsToDto(conversationRepository.findConversationsForOperator(username));
        } else {
            return conversationsToDto(conversationRepository.findConversationsForUser(username));
        }
    }

    @Transactional
    public ConversationDto createConversation(ConversationTypeEnum conversationType, String username) {
        User user = userRepository.findByUsername(username);
//        var conversations = conversationRepository.findConversationsForUser(username);
//        if (conversations.stream().anyMatch(c -> c.getConversationType().equals(conversationType))) {
//            throw new ExpectedCustomerServiceException("Conversation of this type already exist.", Response.Status.BAD_REQUEST, ErrorCodeEnum.CONVERSATION_TYPE_EXISTS, null);
//        }

        var conversation = new Conversation();
        conversation.setConversationType(conversationType);
        conversation.setUser(user);
        conversationRepository.persist(conversation);
        return new ConversationDto(conversation.getId(), conversation.getConversationType(), user.getUsername());
    }

    @Transactional
    public ConversationDto takeConversation(@NotNull @Valid Long id, String username) {
        User user = userRepository.findByUsername(username);
        var conversation = getConversationForUser(id, user);
        conversation.setOperator(user);
        conversationRepository.persist(conversation);
        return new ConversationDto(conversation.getId(), conversation.getConversationType(), conversation.getUser().getUsername());
    }

    public List<MessageDto> getMessagesForConversation(Long conversationId, String username) {
        User user = userRepository.findByUsername(username);
        var conversation = getConversationForUser(conversationId, user);
        return conversation.getMessages().stream()
                .map(message -> new MessageDto(
                        message.getMessage(),
                        message.getTimestamp(),
                        new UserDto(
                                message.getUser().getUsername(),
                                message.getUser().getRole()
                        )
                ))
                .toList();
    }

    public void addMessageToConversation(String msg, Long conversationId, String username) {
        var user = userRepository.findByUsername(username);
        var conversation = getConversationForUser(conversationId, user);

        var message = new Message();
        message.setMessage(msg);
        message.setUser(user);

        conversation.getMessages().add(message);
        conversationRepository.persist(conversation);
    }

    private Conversation getConversationForUser(Long conversationId, User user) {
        List<Conversation> conversations;
        if (Objects.equals(user.getRole(), UserRole.OPERATOR)) {
            conversations = conversationRepository.findConversationsForOperator(user.getUsername());
        } else {
            conversations = conversationRepository.findConversationsForUser(user.getUsername());
        }

        return conversations.stream()
                .filter(c -> c.getId().equals(conversationId))
                .findAny()
                .orElseThrow(() -> new ExpectedCustomerServiceException(
                        String.format("Conversation with ID %s does not exist or can not be accessed.", conversationId),
                        Response.Status.BAD_REQUEST,
                        ErrorCodeEnum.CONVERSATION_WITH_ID_DOES_NOT_EXIST,
                        null
                ));
    }

    private List<ConversationDto> conversationsToDto(List<Conversation> conversations) {
        return conversations.stream()
                .map(conversation -> new ConversationDto(conversation.getId(), conversation.getConversationType(), conversation.getUser().getUsername()))
                .toList();
    }
}
