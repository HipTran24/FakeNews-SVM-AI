package com.example.demo.dto;

import java.util.List;

public class ConversationDetailResponse {
    private Long id;
    private String title;
    private String lastVerdict;
    private List<MessageDto> messages;

    public ConversationDetailResponse() {
    }

    public ConversationDetailResponse(Long id, String title, String lastVerdict, List<MessageDto> messages) {
        this.id = id;
        this.title = title;
        this.lastVerdict = lastVerdict;
        this.messages = messages;
    }

    // getter / setter

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLastVerdict() {
        return lastVerdict;
    }

    public List<MessageDto> getMessages() {
        return messages;
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

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }
}
