package com.github.blarc.repository;

import com.github.blarc.entity.Message;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MessageRepository implements PanacheRepository<Message> {

    public int findByConversationIdOrderedByTimestampPageCount(Long conversationId, int pageSize) {
        return find("conversation.id = ?1 ORDER BY timestamp ASC", conversationId)
                .page(Page.ofSize(pageSize))
                .pageCount();
    }

    public List<Message> findByConversationIdOrderedByTimestampPage(Long conversationId, int pageSize, int pageIndex) {
        return find("conversation.id = ?1 ORDER BY timestamp ASC", conversationId)
                .page(pageIndex, pageSize)
                .list();
    }
}
