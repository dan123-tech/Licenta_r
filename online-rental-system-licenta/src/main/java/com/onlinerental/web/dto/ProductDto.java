package com.onlinerental.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal dailyPrice,
        String category,
        String brand,
        String model,
        String imageUrl,
        BigDecimal discountPercent,
        Instant createdAt,
        Instant updatedAt
) {
}
