package com.github.blarc.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
//@Table(uniqueConstraints = {
//        // A user can only have one conversation of each type
//        @UniqueConstraint(name = "uk_conversationtype_user", columnNames = {"conversationtype", "user_id"})
//})
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private ConversationTypeEnum conversationType;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    private User operator;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "conversation")
    private List<Message> messages = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public ConversationTypeEnum getConversationType() {
        return conversationType;
    }

    public void setConversationType(ConversationTypeEnum conversationType) {
        this.conversationType = conversationType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
