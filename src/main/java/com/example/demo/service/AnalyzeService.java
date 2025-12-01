package com.example.demo.service;

import com.example.demo.dto.AnalyzeTextRequest;
import com.example.demo.dto.AnalyzeResponse;
import com.example.demo.dto.AnalyzeUrlRequest;
import com.example.demo.entity.*;
import com.example.demo.entity.enums.InputType;
import com.example.demo.entity.enums.PredictedLabel;
import com.example.demo.repository.AnalyzedItemRepository;
import com.example.demo.repository.AnalysisResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AnalyzeService {
    private final AnalyzedItemRepository itemRepo;
    private final AnalysisResultRepository resultRepo;
    private final Random random = new Random();

    public AnalyzeService(AnalyzedItemRepository itemRepo,
                          AnalysisResultRepository resultRepo) {
        this.itemRepo = itemRepo;
        this.resultRepo = resultRepo;
    }

    // ==================== 1. PHÂN TÍCH TEXT ====================

    public AnalyzeResponse analyzeText(Session session, AnalyzeTextRequest req) {

        // 1. Tạo và lưu AnalyzedItem
        AnalyzedItem item = new AnalyzedItem();
        item.setSession(session);
        item.setInputType(InputType.TEXT);
        item.setTitle(req.getTitle());
        item.setRawContent(req.getContent());
        item.setLanguage("vi"); // tạm thời fix cứng, sau này detect ngôn ngữ cũng được
        item.setCreatedAt(LocalDateTime.now());

        item = itemRepo.save(item);

        // 2. Sinh kết quả giả lập
        AnalysisResult result = buildFakeResultForItem(item);

        // 3. Build response
        AnalyzeResponse res = new AnalyzeResponse();
        res.setItemId(item.getId());
        res.setResultId(result.getId());
        res.setLabel(result.getPredictedLabel().name());
        res.setProbFake(result.getProbFake());
        res.setProbReal(result.getProbReal());

        return res;
    }

    // ==================== 2. PHÂN TÍCH URL ====================

    public AnalyzeResponse analyzeUrl(Session session, AnalyzeUrlRequest req) {

        // TODO: Sau này có thể crawl nội dung thật từ URL,
        // hiện tại ta mock bằng cách dùng chính URL làm rawContent.

        String rawContent = "CONTENT_FROM_URL: " + req.getUrl();

        AnalyzedItem item = new AnalyzedItem();
        item.setSession(session);
        item.setInputType(InputType.URL);
        item.setUrl(req.getUrl());
        item.setTitle(req.getTitle());
        item.setRawContent(rawContent);
        item.setLanguage("vi"); // tạm thời
        item.setCreatedAt(LocalDateTime.now());

        item = itemRepo.save(item);

        AnalysisResult result = buildFakeResultForItem(item);

        AnalyzeResponse res = new AnalyzeResponse();
        res.setItemId(item.getId());
        res.setResultId(result.getId());
        res.setLabel(result.getPredictedLabel().name());
        res.setProbFake(result.getProbFake());
        res.setProbReal(result.getProbReal());

        return res;
    }

    // ==================== 3. PHÂN TÍCH FILE ====================

    public AnalyzeResponse analyzeFile(Session session, MultipartFile file) throws IOException {

        // Đọc nội dung file (giả sử là text-based: txt, csv, md...)
        // Nếu là PDF/DOCX thì sau này sẽ phải dùng thư viện đọc riêng.
        String rawContent = new String(file.getBytes(), StandardCharsets.UTF_8);

        AnalyzedItem item = new AnalyzedItem();
        item.setSession(session);
        item.setInputType(InputType.FILE);
        item.setOriginalFileName(file.getOriginalFilename());
        item.setContentType(file.getContentType());
        // storedFilePath: hiện tại chưa lưu file ra disk, để null hoặc TODO
        item.setStoredFilePath(null);
        item.setTitle(file.getOriginalFilename());
        item.setRawContent(rawContent);
        item.setLanguage("vi"); // tạm thời
        item.setCreatedAt(LocalDateTime.now());

        item = itemRepo.save(item);

        AnalysisResult result = buildFakeResultForItem(item);

        AnalyzeResponse res = new AnalyzeResponse();
        res.setItemId(item.getId());
        res.setResultId(result.getId());
        res.setLabel(result.getPredictedLabel().name());
        res.setProbFake(result.getProbFake());
        res.setProbReal(result.getProbReal());

        return res;
    }

    // ==================== HÀM DÙNG CHUNG: SINH KẾT QUẢ GIẢ LẬP ====================

    private AnalysisResult buildFakeResultForItem(AnalyzedItem item) {

        // Random từ 0.2 đến 0.8
        BigDecimal rand = BigDecimal.valueOf(random.nextDouble());        // 0.0 - 1.0
        BigDecimal probFake = BigDecimal.valueOf(0.2)                     // 0.2
                .add(BigDecimal.valueOf(0.6).multiply(rand));             // + 0.6 * rand → [0.2;0.8]
        BigDecimal probReal = BigDecimal.ONE.subtract(probFake);          // 1 - probFake

        PredictedLabel label =
                probFake.compareTo(probReal) >= 0 ? PredictedLabel.FAKE : PredictedLabel.REAL;

        AnalysisResult result = new AnalysisResult();
        result.setItem(item);
        result.setPredictedLabel(label);
        result.setProbFake(probFake);
        result.setProbReal(probReal);
        result.setCreatedAt(LocalDateTime.now());

        return resultRepo.save(result);
    }
}
