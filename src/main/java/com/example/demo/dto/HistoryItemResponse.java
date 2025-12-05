package com.example.demo.dto;

import com.example.demo.entity.enums.InputType;
import com.example.demo.entity.enums.PredictedLabel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistoryItemResponse {

    private Long itemId;
    private InputType inputType;

    private String title;
    private String url;
    private String fileName;

    private PredictedLabel label;
    private BigDecimal probFake;
    private BigDecimal probReal;

    private LocalDateTime createdAt;

    public HistoryItemResponse() {
    }

    public HistoryItemResponse(Long itemId,
                               InputType inputType,
                               String title,
                               String url,
                               String fileName,
                               PredictedLabel label,
                               BigDecimal probFake,
                               BigDecimal probReal,
                               LocalDateTime createdAt) {
        this.itemId = itemId;
        this.inputType = inputType;
        this.title = title;
        this.url = url;
        this.fileName = fileName;
        this.label = label;
        this.probFake = probFake;
        this.probReal = probReal;
        this.createdAt = createdAt;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public PredictedLabel getLabel() {
        return label;
    }

    public void setLabel(PredictedLabel label) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
