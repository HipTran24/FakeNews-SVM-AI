package com.example.demo.controller;
import com.example.demo.dto.AnalyzeTextRequest;
import com.example.demo.dto.AnalyzeResponse;
import com.example.demo.dto.AnalyzeUrlRequest;
import com.example.demo.dto.HistoryItemResponse;
import com.example.demo.entity.Session;
import com.example.demo.service.AnalyzeService;
import com.example.demo.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/analyze")
public class AnalyzeController {
    private final SessionService sessionService;
    private final AnalyzeService analyzeService;

    public AnalyzeController(SessionService sessionService,
                             AnalyzeService analyzeService) {
        this.sessionService = sessionService;
        this.analyzeService = analyzeService;
    }

    @PostMapping("/text")
    public ResponseEntity<AnalyzeResponse> analyzeText(
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken,
            @RequestBody AnalyzeTextRequest request,
            HttpServletRequest httpServletRequest) {

        Session session = sessionService.getOrCreateSession(sessionToken, httpServletRequest);

        AnalyzeResponse response = analyzeService.analyzeText(session, request);

        response.setSessionToken(session.getSessionToken());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/file")
    public ResponseEntity<AnalyzeResponse> analyzeFile(
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpServletRequest) throws IOException {

        Session session = sessionService.getOrCreateSession(sessionToken, httpServletRequest);

        AnalyzeResponse response = analyzeService.analyzeFile(session, file);
        response.setSessionToken(session.getSessionToken());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/url")
    public ResponseEntity<AnalyzeResponse> analyzeUrl(
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken,
            @RequestBody AnalyzeUrlRequest request,
            HttpServletRequest httpServletRequest) {

        Session session = sessionService.getOrCreateSession(sessionToken, httpServletRequest);

        AnalyzeResponse response = analyzeService.analyzeUrl(session, request);
        response.setSessionToken(session.getSessionToken());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/history")
    public ResponseEntity<List<HistoryItemResponse>> getHistory(
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {

        List<HistoryItemResponse> history =
                analyzeService.getHistoryBySessionToken(sessionToken);

        return ResponseEntity.ok(history);
    }


}
