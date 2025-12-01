package com.example.demo.dto;

public class AnalyzeUrlRequest {
    private String url;

    // Tiêu đề (optional) – FE có thể gửi riêng hoặc để BE crawl
    private String title;

    public AnalyzeUrlRequest() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
