package com.example.demo.ml;

import com.example.demo.entity.enums.PredictedLabel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class SvmService {

    private final Map<String, Integer> vocab = new HashMap<>();
    private final double[] weights;
    private final double bias;

    public SvmService() {
        try {
            ClassPathResource vocabRes = new ClassPathResource("model/vocab.txt");
            int index = 0;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(vocabRes.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        vocab.put(line, index++);
                    }
                }
            }

            this.weights = new double[vocab.size()];
            ClassPathResource weightsRes = new ClassPathResource("model/weights.txt");
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(weightsRes.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                int i = 0;
                while ((line = br.readLine()) != null && i < weights.length) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        weights[i++] = Double.parseDouble(line);
                    }
                }
            }

            ClassPathResource biasRes = new ClassPathResource("model/bias.txt");
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(biasRes.getInputStream(), StandardCharsets.UTF_8))) {
                String line = br.readLine();
                this.bias = (line == null) ? 0.0 : Double.parseDouble(line);
            }

        } catch (IOException e) {
            throw new RuntimeException("Không load được model SVM từ resources", e);
        }
    }

    private String preprocess(String text) {
        if (text == null) return "";
        text = text.toLowerCase(Locale.ROOT);
        text = text.replaceAll("[^\\p{L}\\p{N}]+", " ");
        text = text.replaceAll("\\s+", " ").trim();
        return text;
    }

    private double[] vectorize(String rawText) {
        double[] x = new double[weights.length];

        String clean = preprocess(rawText);
        if (clean.isEmpty()) return x;

        String[] tokens = clean.split(" ");
        for (String token : tokens) {
            Integer idx = vocab.get(token);
            if (idx != null) {
                x[idx] += 1.0;
            }
        }

        return x;
    }

    private double computeScore(double[] x) {
        double score = bias;
        for (int i = 0; i < weights.length; i++) {
            score += weights[i] * x[i];
        }
        return score;
    }

    private BigDecimal sigmoid(double score) {
        double p = 1.0 / (1.0 + Math.exp(-score));
        return BigDecimal.valueOf(p).setScale(4, RoundingMode.HALF_UP);
    }

    public SvmPrediction predict(String text) {
        double[] x = vectorize(text);
        double score = computeScore(x);

        BigDecimal probFake = sigmoid(score);
        BigDecimal probReal = BigDecimal.ONE.subtract(probFake).setScale(4, RoundingMode.HALF_UP);

        PredictedLabel label =
                probFake.compareTo(probReal) >= 0 ? PredictedLabel.FAKE : PredictedLabel.REAL;

        return new SvmPrediction(label, probFake, probReal);
    }
}
