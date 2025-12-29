package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConversationSummaryResponse {
    private Long id;
    private String title;
    private String lastVerdict;
    private LocalDateTime updatedAt;

    public ConversationSummaryResponse() {
    }

    public ConversationSummaryResponse(Long id, String title, String lastVerdict, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.lastVerdict = lastVerdict;
        this.updatedAt = updatedAt;
    }


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLastVerdict() {
        return lastVerdict;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLastVerdict(String lastVerdict) {
        this.lastVerdict = lastVerdict;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

