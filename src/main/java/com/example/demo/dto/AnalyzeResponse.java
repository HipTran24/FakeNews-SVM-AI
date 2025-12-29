package com.example.demo.dto;

import java.math.BigDecimal;

public class AnalyzeResponse {
    private Long itemId;

    private Long resultId;

    private BigDecimal probFake;

    private BigDecimal probReal;

    private String sessionToken;

    private String label;

    private Long conversationId;

    public AnalyzeResponse() {
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getProbFake() {
        return probFake;
    }

    public void setProbFake(BigDecimal probFake) {
        this.probFake = probFake;
    }

    public BigDecimal getProbReal() {
        return probReal;
    }

    public void setProbReal(BigDecimal probReal) {
        this.probReal = probReal;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
}
