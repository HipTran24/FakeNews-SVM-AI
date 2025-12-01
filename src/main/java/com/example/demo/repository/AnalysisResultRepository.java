package com.example.demo.repository;

import com.example.demo.entity.AnalysisResult;
import com.example.demo.entity.AnalyzedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    List<AnalysisResult> findByItem(AnalyzedItem item);
}
