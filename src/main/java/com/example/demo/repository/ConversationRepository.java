package com.example.demo.repository;


import com.example.demo.entity.Conversation;
import com.example.demo.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findBySessionAndDeletedFalseOrderByUpdatedAtDesc(Session session);

    Optional<Conversation> findByIdAndSessionAndDeletedFalse(Long id, Session session);
}
