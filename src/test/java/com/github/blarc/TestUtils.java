package com.github.blarc;

import com.github.blarc.entity.*;
import com.github.blarc.repository.ConversationRepository;
import com.github.blarc.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.common.constraint.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@ApplicationScoped
public class TestUtils {

    @Inject
    UserRepository userRepository;

    @Inject
    ConversationRepository conversationRepository;

    public User persistUser(@NotNull String role) {
        return persistUser(role, user -> {});
    }

    public User persistUser(@NotNull String role, Consumer<User> modify) {
        User user = new User();
        user.setUsername(UUID.randomUUID().toString());
        user.setPassword(BcryptUtil.bcryptHash("qweasd123"));
        user.setRole(role);
        modify.accept(user);
        userRepository.persist(user);
        return user;
    }

    public Conversation persistConversation(@Nullable User user) {
        return persistConversation(user, conversation -> {});
    }

    public Conversation persistConversation(@Nullable User user, Consumer<Conversation> modify) {
        Conversation conversation = new Conversation();
        conversation.setConversationType(ConversationTypeEnum.IT);

        if (user.getRole().equals(UserRole.OPERATOR)) {
            conversation.setOperator(user);
        } else {
            conversation.setUser(user);
        }

        conversation.setMessages(List.of(
                createMessage(user),
                createMessage(user),
                createMessage(user)
        ));

        modify.accept(conversation);
        conversationRepository.persist(conversation);
        return conversation;
    }

    public Message createMessage(User user) {
        Message message = new Message();
        message.setMessage("Hello World");
        message.setUser(user);
        return message;
    }
}
