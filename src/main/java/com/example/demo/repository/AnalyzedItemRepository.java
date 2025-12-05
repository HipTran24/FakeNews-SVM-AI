package com.example.demo.repository;

import com.example.demo.entity.AnalyzedItem;
import com.example.demo.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalyzedItemRepository extends JpaRepository<AnalyzedItem, Long> {

    List<AnalyzedItem> findBySession(Session session);
    List<AnalyzedItem> findBySessionOrderByCreatedAtDesc(Session session);

}
