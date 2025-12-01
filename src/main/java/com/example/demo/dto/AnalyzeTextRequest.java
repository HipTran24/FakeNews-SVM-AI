package com.example.demo.dto;

public class AnalyzeTextRequest {
    private String title;

    // Nội dung cần phân tích (bắt buộc)
    private String content;

    public AnalyzeTextRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
