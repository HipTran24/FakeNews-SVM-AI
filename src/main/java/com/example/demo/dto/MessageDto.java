package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MessageDto {
    private String sender;   // "USER" | "MODEL"
    private String content;
    private String verdict;  // FAKE / REAL / null (chỉ cho MODEL)
    private Double score;    // độ tin cậy (0..1)
    private LocalDateTime createdAt;

    public MessageDto() {
    }

    public MessageDto(String sender, String content, String verdict, Double score, LocalDateTime createdAt) {
        this.sender = sender;
        this.content = content;
        this.verdict = verdict;
        this.score = score;
        this.createdAt = createdAt;
    }

    // getter / setter

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getVerdict() {
        return verdict;
    }

    public Double getScore() {
        return score;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

