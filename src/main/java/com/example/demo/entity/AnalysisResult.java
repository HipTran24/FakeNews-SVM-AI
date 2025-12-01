package com.example.demo.entity;

import com.example.demo.entity.enums.PredictedLabel;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nhiều kết quả thuộc về 1 item
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private AnalyzedItem item;

    // Nhãn AI dự đoán: FAKE / REAL / UNKNOWN
    @Enumerated(EnumType.STRING)
    @Column(name = "predicted_label", nullable = false, length = 10)
    private PredictedLabel predictedLabel;

    // Xác suất tin giả
    @Column(name = "prob_fake", precision = 5, scale = 4)
    private BigDecimal probFake;

    // Xác suất tin thật
    @Column(name = "prob_real", precision = 5, scale = 4)
    private BigDecimal probReal;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ===== Constructors =====
    public AnalysisResult() {}

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public AnalyzedItem getItem() {
        return item;
    }

    public void setItem(AnalyzedItem item) {
        this.item = item;
    }

    public PredictedLabel getPredictedLabel() {
        return predictedLabel;
    }

    public void setPredictedLabel(PredictedLabel predictedLabel) {
        this.predictedLabel = predictedLabel;
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
