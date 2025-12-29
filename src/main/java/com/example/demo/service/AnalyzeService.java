package com.example.demo.service;

import com.example.demo.dto.AnalyzeTextRequest;
import com.example.demo.dto.AnalyzeResponse;
import com.example.demo.dto.AnalyzeUrlRequest;
import com.example.demo.dto.HistoryItemResponse;
import com.example.demo.entity.*;
import com.example.demo.entity.enums.InputType;
import com.example.demo.repository.AnalyzedItemRepository;
import com.example.demo.repository.AnalysisResultRepository;
import com.example.demo.ml.SvmService;
import com.example.demo.ml.SvmPrediction;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.SessionRepository;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AnalyzeService {


        private final AnalyzedItemRepository itemRepo;
        private final AnalysisResultRepository resultRepo;
        private final SvmService svmService;
        private final SessionRepository sessionRepo;
        private final ConversationRepository conversationRepository;

    public AnalyzeService(AnalyzedItemRepository itemRepo,
                              AnalysisResultRepository resultRepo,
                              SvmService svmService,
                              SessionRepository sessionRepo, ConversationRepository conversationRepository) {
            this.itemRepo = itemRepo;
            this.resultRepo = resultRepo;
            this.svmService = svmService;
            this.sessionRepo = sessionRepo;
            this.conversationRepository = conversationRepository;

    }

    private Conversation resolveConversation(Session session, Long conversationId, String fallbackTitle) {
        if (conversationId != null) {
            return conversationRepository.findByIdAndSessionAndDeletedFalse(conversationId, session)
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
        }
        Conversation conv = new Conversation();
        conv.setSession(session);
        conv.setTitle((fallbackTitle != null && !fallbackTitle.isBlank())
                ? fallbackTitle
                : "Cuộc hội thoại mới");
        conv.setCreatedAt(java.time.LocalDateTime.now());
        conv.setUpdatedAt(conv.getCreatedAt());
        conv.setDeleted(false);
        return conversationRepository.save(conv);
    }

    public AnalyzeResponse analyzeText(Session session, AnalyzeTextRequest req) {

        Conversation conversation = resolveConversation(
                session,
                req.getConversationId(),
                req.getTitle() != null ? req.getTitle() : (req.getContent() != null
                        ? req.getContent().substring(0, Math.min(50, req.getContent().length()))
                        : "Cuộc hội thoại mới")
        );

        AnalyzedItem item = new AnalyzedItem();
        item.setSession(session);
        item.setConversation(conversation);
        item.setInputType(InputType.TEXT);
        item.setTitle(req.getTitle());
        item.setRawContent(req.getContent());
        item.setLanguage("vi");
        item.setCreatedAt(LocalDateTime.now());

        item = itemRepo.save(item);


        AnalysisResult result = buildResultForItem(item);


        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        AnalyzeResponse res = new AnalyzeResponse();
        res.setItemId(item.getId());
        res.setResultId(result.getId());
        res.setLabel(result.getPredictedLabel().name());
        res.setProbFake(result.getProbFake());
        res.setProbReal(result.getProbReal());

        res.setConversationId(conversation.getId());

        return res;
    }


    public AnalyzeResponse analyzeUrl(Session session, AnalyzeUrlRequest req) {

        Conversation conversation = resolveConversation(
                session,
                req.getConversationId(),
                req.getTitle() != null ? req.getTitle() : "Kiểm tra URL"
        );

        String url = req.getUrl();
        String title = req.getTitle();

        String rawContent;
        try {
            Document doc = Jsoup.connect(url).get();
            rawContent = doc.body().text();
        } catch (Exception e) {
            rawContent = "UNABLE_TO_FETCH_URL_CONTENT";
        }

        AnalyzedItem item = new AnalyzedItem();
        item.setSession(session);
        item.setConversation(conversation);
        item.setInputType(InputType.URL);
        item.setUrl(url);
        item.setTitle(title);
        item.setRawContent(rawContent);
        item.setLanguage("vi");
        item.setCreatedAt(LocalDateTime.now());

        item = itemRepo.save(item);

        AnalysisResult result = buildResultForItem(item);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        AnalyzeResponse res = new AnalyzeResponse();
        res.setItemId(item.getId());
        res.setResultId(result.getId());
        res.setLabel(result.getPredictedLabel().name());
        res.setProbFake(result.getProbFake());
        res.setProbReal(result.getProbReal());
        res.setConversationId(conversation.getId());
        return res;
    }



    public AnalyzeResponse analyzeFile(Session session, MultipartFile file, Long conversationId) throws IOException {

        Conversation conversation = resolveConversation(
                session,
                conversationId,
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "Kiểm tra file"
        );

        String rawContent;
        String filename = file.getOriginalFilename();
        String lowerName = filename != null ? filename.toLowerCase() : "";

        if (lowerName.endsWith(".docx")) {
            try (InputStream is = file.getInputStream();
                 XWPFDocument doc = new XWPFDocument(is);
                 XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                rawContent = extractor.getText();
            }
        } else {
            rawContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        }

        if (rawContent == null || rawContent.isBlank()) rawContent = "EMPTY_FILE_CONTENT";

        AnalyzedItem item = new AnalyzedItem();
        item.setSession(session);
        item.setConversation(conversation);
        item.setInputType(InputType.FILE);
        item.setOriginalFileName(filename);
        item.setContentType(file.getContentType());
        item.setStoredFilePath(null);
        item.setTitle(filename);
        item.setRawContent(rawContent);
        item.setLanguage("vi");
        item.setCreatedAt(LocalDateTime.now());

        item = itemRepo.save(item);

        AnalysisResult result = buildResultForItem(item);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        AnalyzeResponse res = new AnalyzeResponse();
        res.setItemId(item.getId());
        res.setResultId(result.getId());
        res.setLabel(result.getPredictedLabel().name());
        res.setProbFake(result.getProbFake());
        res.setProbReal(result.getProbReal());
        res.setConversationId(conversation.getId());
        return res;
    }


    private AnalysisResult buildResultForItem(AnalyzedItem item) {
        SvmPrediction prediction = svmService.predict(item.getRawContent());

        AnalysisResult result = new AnalysisResult();
        result.setItem(item);
        result.setPredictedLabel(prediction.getLabel());
        result.setProbFake(prediction.getProbFake());
        result.setProbReal(prediction.getProbReal());
        result.setCreatedAt(LocalDateTime.now());

        return resultRepo.save(result);
    }

    public List<HistoryItemResponse> getHistoryBySessionToken(String sessionToken) {
        if (sessionToken == null || sessionToken.isBlank()) {
            return Collections.emptyList();
        }

        return sessionRepo.findBySessionToken(sessionToken)
                .map(session -> {
                    List<AnalyzedItem> items =
                            itemRepo.findBySessionOrderByCreatedAtDesc(session);

                    List<HistoryItemResponse> history = new ArrayList<>();

                    for (AnalyzedItem item : items) {

                        List<AnalysisResult> results = resultRepo.findByItem(item);
                        if (results.isEmpty()) {
                            continue;
                        }
                        AnalysisResult r = results.get(results.size() - 1);

                        HistoryItemResponse dto = new HistoryItemResponse();
                        dto.setItemId(item.getId());
                        dto.setInputType(item.getInputType());
                        dto.setTitle(item.getTitle());
                        dto.setUrl(item.getUrl());
                        dto.setFileName(item.getOriginalFileName());
                        dto.setLabel(r.getPredictedLabel());
                        dto.setProbFake(r.getProbFake());
                        dto.setProbReal(r.getProbReal());
                        dto.setCreatedAt(item.getCreatedAt());

                        history.add(dto);
                    }

                    return history;
                })
                .orElse(Collections.emptyList());
    }

}
