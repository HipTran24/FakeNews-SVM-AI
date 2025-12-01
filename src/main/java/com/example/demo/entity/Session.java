package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_token", nullable = false, unique = true, length = 100)
    private String sessionToken;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_access_at")
    private LocalDateTime lastAccessAt;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "ip_hash", length = 128)
    private String ipHash;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<AnalyzedItem> analyzedItems = new ArrayList<>();

    public Session() {}

    // GETTERS + SETTERS

    public Long getId() {
        return id;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastAccessAt() {
        return lastAccessAt;
    }

    public void setLastAccessAt(LocalDateTime lastAccessAt) {
        this.lastAccessAt = lastAccessAt;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIpHash() {
        return ipHash;
    }

    public void setIpHash(String ipHash) {
        this.ipHash = ipHash;
    }

    public List<AnalyzedItem> getAnalyzedItems() {
        return analyzedItems;
    }

    public void setAnalyzedItems(List<AnalyzedItem> analyzedItems) {
        this.analyzedItems = analyzedItems;
    }
}
