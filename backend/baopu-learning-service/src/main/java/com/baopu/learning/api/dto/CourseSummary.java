package com.baopu.learning.api.dto;

import java.math.BigDecimal;

public record CourseSummary(
    Long id,
    String title,
    String description,
    String coverUrl,
    String category,
    String lecturer,
    Integer durationMinutes,
    BigDecimal price,
    Integer progressPercent,
    Boolean enrolled) {}
