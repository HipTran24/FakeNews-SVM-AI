package com.example.demo.service;

import com.example.demo.dto.ConversationDetailResponse;
import com.example.demo.dto.ConversationSummaryResponse;
import com.example.demo.dto.MessageDto;
import com.example.demo.entity.AnalysisResult;
import com.example.demo.entity.AnalyzedItem;
import com.example.demo.entity.Conversation;
import com.example.demo.entity.Session;
import com.example.demo.entity.enums.PredictedLabel;
import com.example.demo.repository.AnalysisResultRepository;
import com.example.demo.repository.ConversationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final AnalysisResultRepository analysisResultRepository;

    public ConversationService(ConversationRepository conversationRepository,
                               AnalysisResultRepository analysisResultRepository) {
        this.conversationRepository = conversationRepository;
        this.analysisResultRepository = analysisResultRepository;
    }

    @Transactional
    public Conversation createConversation(Session session, String title) {
        Conversation conv = new Conversation();
        conv.setSession(session);
        conv.setTitle(title != null && !title.isBlank() ? title : "Cuộc hội thoại mới");
        conv.setCreatedAt(java.time.LocalDateTime.now());
        conv.setUpdatedAt(conv.getCreatedAt());
        conv.setDeleted(false);
        return conversationRepository.save(conv);
    }

    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> listConversations(Session session) {
        List<Conversation> list = conversationRepository
                .findBySessionAndDeletedFalseOrderByUpdatedAtDesc(session);

        return list.stream()
                .map(conv -> {
                    PredictedLabel lastLabel = null;
                    java.time.LocalDateTime lastTime = null;

                    for (AnalyzedItem item : conv.getItems()) {
                        for (AnalysisResult r : item.getAnalysisResults()) {
                            if (lastTime == null || r.getCreatedAt().isAfter(lastTime)) {
                                lastTime = r.getCreatedAt();
                                lastLabel = r.getPredictedLabel();
                            }
                        }
                    }

                    String lastVerdict = lastLabel != null ? lastLabel.name() : null;
                    return new ConversationSummaryResponse(
                            conv.getId(),
                            conv.getTitle(),
                            lastVerdict,
                            conv.getUpdatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConversationDetailResponse getConversation(Session session, Long conversationId) {
        Conversation conv = conversationRepository
                .findByIdAndSessionAndDeletedFalse(conversationId, session)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        List<MessageDto> messages = new ArrayList<>();

        List<AnalyzedItem> items = new ArrayList<>(conv.getItems());
        items.sort(Comparator.comparing(AnalyzedItem::getCreatedAt));

        PredictedLabel lastLabel = null;

        for (AnalyzedItem item : items) {
            String userContent;
            switch (item.getInputType()) {
                case TEXT -> userContent = Optional.ofNullable(item.getTitle())
                        .orElseGet(() -> {
                            String raw = item.getRawContent();
                            if (raw == null) return "(Văn bản trống)";
                            return raw.length() > 200 ? raw.substring(0, 200) + "..." : raw;
                        });
                case URL -> userContent = "Kiểm tra link: " + item.getUrl();
                case FILE -> userContent = "Kiểm tra file: " + item.getOriginalFileName();
                default -> userContent = "(Yêu cầu không xác định)";
            }

            messages.add(new MessageDto(
                    "USER",
                    userContent,
                    null,
                    null,
                    item.getCreatedAt()
            ));

            AnalysisResult latest = item.getAnalysisResults().stream()
                    .max(Comparator.comparing(AnalysisResult::getCreatedAt))
                    .orElse(null);

            if (latest != null) {
                lastLabel = latest.getPredictedLabel();
                double probReal = latest.getProbReal() != null
                        ? latest.getProbReal().doubleValue()
                        : 0.0;

                String modelText;
                if (lastLabel == PredictedLabel.FAKE) {
                    modelText = "TIN GIẢ. Hãy cẩn thận khi chia sẻ nội dung này.";
                } else if (lastLabel == PredictedLabel.REAL) {
                    modelText = "Kết quả: TIN THẬT / ĐÁNG TIN CẬY.";
                } else {
                    modelText = "Kết quả: " + lastLabel;
                }

                messages.add(new MessageDto(
                        "MODEL",
                        modelText,
                        lastLabel.name(),
                        probReal,
                        latest.getCreatedAt()
                ));
            }
        }

        String lastVerdict = lastLabel != null ? lastLabel.name() : null;

        return new ConversationDetailResponse(
                conv.getId(),
                conv.getTitle(),
                lastVerdict,
                messages
        );
    }

    @Transactional
    public void deleteConversation(Session session, Long conversationId) {
        Conversation conv = conversationRepository
                .findByIdAndSessionAndDeletedFalse(conversationId, session)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        conv.setDeleted(true);
        conversationRepository.save(conv);
    }
}
