package com.example.demo.controller;

import com.example.demo.dto.ConversationDetailResponse;
import com.example.demo.dto.MessageDto;
import com.example.demo.dto.ConversationSummaryResponse;
import com.example.demo.entity.AnalyzedItem;
import com.example.demo.entity.AnalysisResult;
import com.example.demo.entity.Session;
import com.example.demo.repository.AnalyzedItemRepository;
import com.example.demo.repository.AnalysisResultRepository;
import com.example.demo.service.ConversationService;
import com.example.demo.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final SessionService sessionService;
    private final ConversationService conversationService;

    public ConversationController(SessionService sessionService,
                                  ConversationService conversationService) {
        this.sessionService = sessionService;
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<List<ConversationSummaryResponse>> listConversations(
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken,
            HttpServletRequest request
    ) {
        Session session = sessionService.getOrCreateSession(sessionToken, request);
        List<ConversationSummaryResponse> list = conversationService.listConversations(session);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationDetailResponse> getConversation(
            @PathVariable("id") Long id,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken,
            HttpServletRequest request
    ) {
        Session session = sessionService.getOrCreateSession(sessionToken, request);
        ConversationDetailResponse resp = conversationService.getConversation(session, id);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable("id") Long id,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken,
            HttpServletRequest request
    ) {
        Session session = sessionService.getOrCreateSession(sessionToken, request);
        conversationService.deleteConversation(session, id);
        return ResponseEntity.noContent().build();
    }
}

