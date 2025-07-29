package com.github.blarc.service;

import com.github.blarc.TestUtils;
import com.github.blarc.entity.ConversationTypeEnum;
import com.github.blarc.entity.Message;
import com.github.blarc.entity.UserRole;
import com.github.blarc.exception.ExpectedCustomerServiceException;
import com.github.blarc.model.MessageDto;
import com.github.blarc.repository.ConversationRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
@TestTransaction
public class ConversationServiceTest {

    @Inject
    ConversationService conversationService;

    @Inject
    ConversationRepository conversationRepository;

    @Inject
    TestUtils testUtils;

    @Test
    public void createConversation() {
        var user = testUtils.persistUser(UserRole.USER);

        conversationService.createConversation(ConversationTypeEnum.IT, user.getUsername());
        var conversations = conversationRepository.findAllConversationsForUser(user.getUsername());
        assertThat(conversations).hasSize(1);
        assertThat(conversations.getFirst().getConversationType()).isEqualTo(ConversationTypeEnum.IT);
        assertThat(conversations.getFirst().getUser().getUsername()).isEqualTo(user.getUsername());

        conversationService.createConversation(ConversationTypeEnum.IT, user.getUsername());
        conversations = conversationRepository.findAllConversationsForUser(user.getUsername());
        assertThat(conversations).hasSize(2);
    }

    @Test
    public void takeConversation() {
        var user = testUtils.persistUser(UserRole.USER);
        var conversation = testUtils.persistConversation(user);

        var operator = testUtils.persistUser(UserRole.OPERATOR);
        // Operator can take open conversations
        var conversationDto = conversationService.takeConversation(conversation.getId(), operator.getUsername());
        assertThat(conversationDto.username()).isEqualTo(user.getUsername());
        assertThat(conversation.getOperator().getUsername()).isEqualTo(operator.getUsername());
        // Operator can see the conversations he's taken
        var conversations = conversationService.getConversations(operator.getUsername(), true);
        assertThat(conversations).hasSize(1);

        // No conversations available for other operators
        var operator2 = testUtils.persistUser(UserRole.OPERATOR);
        conversations = conversationService.getConversations(operator2.getUsername(), true);
        assertThat(conversations).hasSize(0);
        // Operator cannot take already taken conversations
        assertThatThrownBy(() -> conversationService.takeConversation(conversation.getId(), operator2.getUsername()))
                .isInstanceOf(ExpectedCustomerServiceException.class)
                .hasMessageContaining(String.format("Conversation with ID %s has already been taken.", conversation.getId()));
    }

    @Test
    public void operatorCanNotSeeOrCreateMessagesForOpenConversations() {
        var user = testUtils.persistUser(UserRole.USER);
        var conversation = testUtils.persistConversation(user);
        var operator = testUtils.persistUser(UserRole.OPERATOR);

        assertThatThrownBy(() -> conversationService.getMessagesForConversation(conversation.getId(), operator.getUsername(), true))
                .isInstanceOf(ExpectedCustomerServiceException.class)
                .hasMessageContaining(String.format("Conversation with ID %s can not be accessed by user %s.", conversation.getId(), operator.getUsername()));

        assertThatThrownBy(() -> conversationService.addMessageToConversation("Hello World!", conversation.getId(), operator.getUsername(), true))
                .isInstanceOf(ExpectedCustomerServiceException.class)
                .hasMessageContaining(String.format("Conversation with ID %s can not be accessed by user %s.", conversation.getId(), operator.getUsername()));

    }

    @Test
    public void getMessagesForConversationForUser() {
        var user = testUtils.persistUser(UserRole.USER);
        var conversation = testUtils.persistConversation(user);
        assertThat(conversationService.getMessagesForConversation(conversation.getId(), user.getUsername(), false))
                .hasSize(3)
                .isSortedAccordingTo(Comparator.comparing(MessageDto::timestamp));
    }

    @Test
    public void conversation() {
        var user = testUtils.persistUser(UserRole.USER);
        var operator = testUtils.persistUser(UserRole.OPERATOR);

        // User creates new conversation
        var conversationDto = conversationService.createConversation(ConversationTypeEnum.IT, user.getUsername());
        String messageFromUser = "A message from the user.";
        conversationService.addMessageToConversation(messageFromUser, conversationDto.id(), user.getUsername(), false);
        List<MessageDto> userMessages = conversationService.getMessagesForConversation(conversationDto.id(), user.getUsername(), false);
        assertThat(userMessages).hasSize(1);

        // Operator takes the conversation
        conversationDto = conversationService.takeConversation(conversationDto.id(), operator.getUsername());
        assertThat(conversationDto.username()).isEqualTo(user.getUsername());
        List<MessageDto> operatorMessages = conversationService.getMessagesForConversation(conversationDto.id(), operator.getUsername(), true);
        assertThat(operatorMessages).hasSize(1);
        assertThat(operatorMessages.getFirst().message()).isEqualTo(messageFromUser);

        // Operator leaves a message
        String messageFromOperator = "A message from the operator.";
        conversationService.addMessageToConversation(messageFromOperator,  conversationDto.id(), operator.getUsername(), true);
        operatorMessages = conversationService.getMessagesForConversation(conversationDto.id(), operator.getUsername(), true);
        assertThat(operatorMessages).hasSize(2);
        assertThat(operatorMessages.stream().map(MessageDto::message)).contains(messageFromOperator, messageFromUser);

        // User retrieves the messages
        userMessages = conversationService.getMessagesForConversation(conversationDto.id(), user.getUsername(), false);
        assertThat(userMessages).hasSize(2);
        assertThat(operatorMessages.stream().map(MessageDto::message)).contains(messageFromOperator, messageFromUser);
    }

}
