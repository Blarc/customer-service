package com.github.blarc.entity;

import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_")
@UserDefinition
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Roles
    @Column(nullable = false)
    private String role;
    @Username
    @Column(nullable = false, unique = true)
    private String username;
    @Password
    @Column(nullable = false)
    private String password;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Conversation> userConversations = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "operator")
    private List<Conversation> operatorConversations = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Conversation> getUserConversations() {
        return userConversations;
    }

    public void setUserConversations(List<Conversation> userConversations) {
        this.userConversations = userConversations;
    }

    public List<Conversation> getOperatorConversations() {
        return operatorConversations;
    }

    public void setOperatorConversations(List<Conversation> operatorConversations) {
        this.operatorConversations = operatorConversations;
    }
}
