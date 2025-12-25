package com.example.demo.entity;

import com.example.demo.entity.enums.InputType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "analyzed_items")
public class AnalyzedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Enumerated(EnumType.STRING)
    @Column(name = "input_type", nullable = false, length = 10)
    private InputType inputType;

    @Column(name = "url", length = 1000)
    private String url;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "stored_file_path", length = 500)
    private String storedFilePath;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "title", length = 500)
    private String title;
    @Lob
    @Column(name = "raw_content", columnDefinition = "LONGTEXT")
    private String rawContent;

    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 1 item có nhiều kết quả phân tích
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<AnalysisResult> analysisResults = new ArrayList<>();


    // ===== Constructor =====
    public AnalyzedItem() {}


    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFilePath() {
        return storedFilePath;
    }

    public void setStoredFilePath(String storedFilePath) {
        this.storedFilePath = storedFilePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<AnalysisResult> getAnalysisResults() {
        return analysisResults;
    }

    public void setAnalysisResults(List<AnalysisResult> analysisResults) {
        this.analysisResults = analysisResults;
    }
    // ...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

// ===== getter / setter =====

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

}

