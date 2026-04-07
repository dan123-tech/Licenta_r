package com.onlinerental.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinerental.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AiComparisonService {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public AiCompareResult compare(Long rentalId, List<String> baselineImageUrls, List<String> returnImageUrls) {
        AppProperties.Ai ai = appProperties.getAi();
        if (ai.getCompareUrl() == null || ai.getCompareUrl().isBlank()) {
            return new AiCompareResult(null, null, false, "SKIPPED", "Serviciul AI nu este configurat.", null);
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("rentalId", rentalId);
        payload.put("baselineImageUrls", baselineImageUrls);
        payload.put("returnImageUrls", returnImageUrls);

        try {
            RestTemplate rt = new RestTemplateBuilder()
                    .setConnectTimeout(Duration.ofMillis(ai.getConnectTimeoutMs()))
                    .setReadTimeout(Duration.ofMillis(ai.getReadTimeoutMs()))
                    .build();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> resp = rt.postForEntity(
                    Objects.requireNonNull(ai.getCompareUrl()),
                    new HttpEntity<>(payload, headers),
                    (Class<Map<String, Object>>) (Class<?>) Map.class
            );
            Map<?, ?> body = Objects.requireNonNullElse(resp.getBody(), Map.of());
            String raw = safeJson(body);

            BigDecimal score = toBigDecimal(firstNonNull(body, "score", "damageScore", "similarityScore"));
            String predicted = toStringValue(firstNonNull(body, "predictedCondition", "condition", "verdict"));
            boolean needsReview = toBoolean(firstNonNull(body, "needsReview", "flagged"))
                    || (score != null && score.compareTo(BigDecimal.valueOf(ai.getReviewThreshold())) >= 0);
            String message = toStringValue(firstNonNull(body, "message", "detail", "statusMessage"));
            if (message == null || message.isBlank()) {
                message = "Comparare AI finalizată.";
            }
            return new AiCompareResult(score, predicted, needsReview, "SUCCESS", message, raw);
        } catch (RestClientException ex) {
            return new AiCompareResult(
                    null,
                    null,
                    false,
                    "FAILED",
                    "Serviciul AI nu a răspuns: " + ex.getMessage(),
                    null
            );
        }
    }

    private static Object firstNonNull(Map<?, ?> body, String... keys) {
        for (String k : keys) {
            Object v = body.get(k);
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    private String safeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return String.valueOf(value);
        }
    }

    private static String toStringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static boolean toBoolean(Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        if (value == null) {
            return false;
        }
        return "true".equalsIgnoreCase(String.valueOf(value));
    }

    public record AiCompareResult(
            BigDecimal score,
            String predictedCondition,
            boolean needsReview,
            String status,
            String message,
            String rawResponse
    ) {
    }
}
