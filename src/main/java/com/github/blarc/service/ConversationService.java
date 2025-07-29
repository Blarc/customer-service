package com.github.blarc.service;

import com.github.blarc.entity.Conversation;
import com.github.blarc.entity.ConversationTypeEnum;
import com.github.blarc.entity.Message;
import com.github.blarc.entity.User;
import com.github.blarc.exception.ExpectedCustomerServiceException;
import com.github.blarc.model.ConversationDto;
import com.github.blarc.model.ErrorCodeEnum;
import com.github.blarc.model.MessageDto;
import com.github.blarc.model.UserDto;
import com.github.blarc.repository.ConversationRepository;
import com.github.blarc.repository.MessageRepository;
import com.github.blarc.repository.UserRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class ConversationService {

    @Inject
    ConversationRepository conversationRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    MessageRepository messageRepository;

    public List<ConversationDto> getConversations(String username, boolean isOperator) {
        if (isOperator) {
            Log.infof("Retrieving all conversations for operator with username %s", username);
            return conversationsToDto(conversationRepository.findAllConversationsForOperator(username));
        } else {
            Log.infof("Retrieving all conversations for user with username %s", username);
            return conversationsToDto(conversationRepository.findAllConversationsForUser(username));
        }
    }

    @Transactional
    public ConversationDto createConversation(ConversationTypeEnum conversationType, String username) {
        User user = userRepository.findByUsername(username);

        var conversation = new Conversation();
        conversation.setConversationType(conversationType);
        conversation.setUser(user);
        conversationRepository.persist(conversation);
        return new ConversationDto(conversation.getId(), conversation.getConversationType(), user.getUsername());
    }

    @Transactional
    public ConversationDto takeConversation(@NotNull @Valid Long id, String username) {
        var conversation = conversationRepository.findByIdOptional(id)
                .orElseThrow(() -> new ExpectedCustomerServiceException(
                        String.format("Conversation with ID %s does not exist.", id),
                        Response.Status.BAD_REQUEST,
                        ErrorCodeEnum.CONVERSATION_DOES_NOT_EXIST,
                        null
                ));

        if (conversation.getOperator() != null) {
            throw new ExpectedCustomerServiceException(
                    String.format("Conversation with ID %s has already been taken.", id),
                    Response.Status.BAD_REQUEST,
                    ErrorCodeEnum.CONVERSATION_ALREADY_TAKEN,
                    null
            );
        }

        User user = userRepository.findByUsername(username);
        conversation.setOperator(user);
        conversationRepository.persist(conversation);
        return new ConversationDto(conversation.getId(), conversation.getConversationType(), conversation.getUser().getUsername());
    }

    public List<MessageDto> getMessagesForConversation(Long conversationId, String username, boolean isOperator) {
        // Check user access and ignore returned conversation
        getConversationIfUserHasAccess(conversationId, username, isOperator);
        return messageRepository.findByConversationIdOrderedByTimestamp(conversationId).stream()
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

    @Transactional
    public void addMessageToConversation(String msg, Long conversationId, String username, boolean isOperator) {
        var conversation = getConversationIfUserHasAccess(conversationId, username, isOperator);

        var user = userRepository.findByUsername(username);
        var message = new Message();
        message.setMessage(msg);
        message.setUser(user);
        message.setConversation(conversation);

        conversation.getMessages().add(message);
        conversationRepository.persist(conversation);
    }

    private Conversation getConversationIfUserHasAccess(Long conversationId, String username, boolean isOperator) {
        var conversation = conversationRepository.findByIdOptional(conversationId)
                .orElseThrow(() -> new ExpectedCustomerServiceException(
                        String.format("Conversation with ID %s does not exist.", conversationId),
                        Response.Status.BAD_REQUEST,
                        ErrorCodeEnum.CONVERSATION_DOES_NOT_EXIST,
                        null
                ));

        validateUserAccess(conversation,  username, isOperator);
        return conversation;
    }

    private void validateUserAccess(Conversation conversation, String username, boolean isOperator) {
        boolean hasAccess = isOperator
                ? conversation.getOperator() != null && conversation.getOperator().getUsername().equals(username)
                : conversation.getUser() != null && conversation.getUser().getUsername().equals(username);

        if (!hasAccess) {
            throw new ExpectedCustomerServiceException(
                    String.format("Conversation with ID %s can not be accessed by user %s.",
                            conversation.getId(), username),
                    Response.Status.BAD_REQUEST,
                    ErrorCodeEnum.CONVERSATION_NOT_ALLOWED,
                    null
            );
        }
    }


    private List<ConversationDto> conversationsToDto(List<Conversation> conversations) {
        return conversations.stream()
                .map(conversation -> new ConversationDto(conversation.getId(), conversation.getConversationType(), conversation.getUser().getUsername()))
                .toList();
    }
}
