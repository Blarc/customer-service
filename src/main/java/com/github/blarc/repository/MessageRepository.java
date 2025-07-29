package com.github.blarc.repository;

import com.github.blarc.entity.Message;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MessageRepository implements PanacheRepository<Message> {

    public List<Message> findByConversationIdOrderedByTimestamp(Long conversationId) {
        return find("conversation.id = ?1 ORDER BY timestamp ASC", conversationId).list();
    }
}
