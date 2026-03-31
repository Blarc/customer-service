package com.github.blarc.repository;

import com.github.blarc.entity.Conversation;
import com.github.blarc.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ConversationRepository implements PanacheRepository<Conversation> {
    public List<Conversation> findAllConversationsForOperator(String username) {
        // language=jpaql
        return find("""
                        SELECT c FROM Conversation c
                        LEFT JOIN c.operator o
                        WHERE c.operator IS NULL OR o.username = ?1
                        """,
                username
        ).list();
    }

    public List<Conversation> findAllConversationsForUser(String username) {
        return find("user.username = ?1", username)
                .list();
    }

    public boolean assignOperatorIfUnassigned(User operator, Long conversationId) {
        return update("operator = ?1 where id = ?2 and operator is null", operator, conversationId) != 0;
    }
}
