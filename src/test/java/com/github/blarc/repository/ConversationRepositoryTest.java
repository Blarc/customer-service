package com.github.blarc.repository;

import com.github.blarc.TestUtils;
import com.github.blarc.entity.Conversation;
import com.github.blarc.entity.ConversationTypeEnum;
import com.github.blarc.entity.UserRole;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestTransaction
public class ConversationRepositoryTest {

    @Inject
    ConversationRepository conversationRepository;

    @Inject
    TestUtils testUtils;

    @Test
    public void testFindConversationsForOperator() {

        var operator = testUtils.persistUser(UserRole.OPERATOR);

        testUtils.persistConversation(testUtils.persistUser(UserRole.USER));
        testUtils.persistConversation(testUtils.persistUser(UserRole.USER));
        testUtils.persistConversation(testUtils.persistUser(UserRole.USER));

        testUtils.persistConversation(testUtils.persistUser(UserRole.USER), c -> c.setOperator(operator));
        testUtils.persistConversation(testUtils.persistUser(UserRole.USER), c -> c.setOperator(operator));
        testUtils.persistConversation(testUtils.persistUser(UserRole.USER), c -> c.setOperator(operator));

        List<Conversation> conversationsForOperator = conversationRepository.findAllConversationsForOperator(operator.getUsername());
        assertThat(conversationsForOperator).size().isGreaterThanOrEqualTo(6);
    }

    @Test
    public void testFindConversationsForUser() {
        var user = testUtils.persistUser(UserRole.USER);

        testUtils.persistConversation(user, c -> c.setConversationType(ConversationTypeEnum.IT));
        testUtils.persistConversation(user, c -> c.setConversationType(ConversationTypeEnum.IT));
        testUtils.persistConversation(user, c -> c.setConversationType(ConversationTypeEnum.CHAT));
        testUtils.persistConversation(user, c -> c.setConversationType(ConversationTypeEnum.SERVICES));
        testUtils.persistConversation(user, c -> c.setConversationType(ConversationTypeEnum.SERVICES));
        testUtils.persistConversation(user, c -> c.setConversationType(ConversationTypeEnum.SERVICES));

        List<Conversation> conversationsForUser = conversationRepository.findAllConversationsForUser(user.getUsername());
        assertThat(conversationsForUser).size().isEqualTo(6);
    }
}
