package com.baopu.learning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Course(
    Long id,
    Long tenantId,
    String title,
    String description,
    String coverUrl,
    String category,
    String lecturer,
    Integer durationMinutes,
    BigDecimal price,
    Integer status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
