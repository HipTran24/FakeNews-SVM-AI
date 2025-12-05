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
import com.example.demo.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
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

        public AnalyzeService(AnalyzedItemRepository itemRepo,
                              AnalysisResultRepository resultRepo,
                              SvmService svmService,
                              SessionRepository sessionRepo) {
            this.itemRepo = itemRepo;
            this.resultRepo = resultRepo;
            this.svmService = svmService;
            this.sessionRepo = sessionRepo;

    }


    public AnalyzeResponse analyzeText(Session session, AnalyzeTextRequest req) {

        AnalyzedItem item = new AnalyzedItem();
        item.setSession(session);
        item.setInputType(InputType.TEXT);
        item.setTitle(req.getTitle());
        item.setRawContent(req.getContent());
        item.setLanguage("vi");
        item.setCreatedAt(LocalDateTime.now());

        item = itemRepo.save(item);

        AnalysisResult result = buildResultForItem(item);

        AnalyzeResponse res = new AnalyzeResponse();
        res.setItemId(item.getId());
        res.setResultId(result.getId());
        res.setLabel(result.getPredictedLabel().name());
        res.setProbFake(result.getProbFake());
        res.setProbReal(result.getProbReal());

        return res;
    }


    public AnalyzeResponse analyzeUrl(Session session, AnalyzeUrlRequest req) {

        String url = req.getUrl();
        String title = req.getTitle();

        String rawContent = "";
        try {
            Document doc = Jsoup.connect(url).get();

            // Lấy text từ HTML
            String extractedText = doc.body().text();

            rawContent = extractedText;
        } catch (Exception e) {
            rawContent = "UNABLE_TO_FETCH_URL_CONTENT";
        }

        AnalyzedItem item = new AnalyzedItem();
        item.setSession(session);
        item.setInputType(InputType.URL);
        item.setUrl(url);
        item.setTitle(title);
        item.setRawContent(rawContent);
        item.setLanguage("vi");
        item.setCreatedAt(LocalDateTime.now());

        item = itemRepo.save(item);

        AnalysisResult result = buildResultForItem(item);

        AnalyzeResponse res = new AnalyzeResponse();
        res.setItemId(item.getId());
        res.setResultId(result.getId());
        res.setLabel(result.getPredictedLabel().name());
        res.setProbFake(result.getProbFake());
        res.setProbReal(result.getProbReal());

        return res;
    }


    public AnalyzeResponse analyzeFile(Session session, MultipartFile file) throws IOException {

        String rawContent = new String(file.getBytes(), StandardCharsets.UTF_8);

        AnalyzedItem item = new AnalyzedItem();
        item.setSession(session);
        item.setInputType(InputType.FILE);
        item.setOriginalFileName(file.getOriginalFilename());
        item.setContentType(file.getContentType());
        item.setStoredFilePath(null); // nếu sau này có lưu file thì set lại
        item.setTitle(file.getOriginalFilename());
        item.setRawContent(rawContent);
        item.setLanguage("vi");
        item.setCreatedAt(LocalDateTime.now());

        item = itemRepo.save(item);

        AnalysisResult result = buildResultForItem(item);

        AnalyzeResponse res = new AnalyzeResponse();
        res.setItemId(item.getId());
        res.setResultId(result.getId());
        res.setLabel(result.getPredictedLabel().name());
        res.setProbFake(result.getProbFake());
        res.setProbReal(result.getProbReal());

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
                        // Mỗi item hiện tại tương ứng 1 lần phân tích
                        // → lấy list result rồi lấy phần tử cuối cùng
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
                        // Thời gian tạo dùng createdAt của item
                        dto.setCreatedAt(item.getCreatedAt());

                        history.add(dto);
                    }

                    return history;
                })
                .orElse(Collections.emptyList());
    }



}
