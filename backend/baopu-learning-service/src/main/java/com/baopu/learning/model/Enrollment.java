package com.baopu.learning.model;

import java.time.LocalDateTime;

public record Enrollment(
    Long id,
    Long userId,
    Long courseId,
    Integer progressPercent,
    Integer completed,
    LocalDateTime enrolledAt,
    LocalDateTime completedAt) {}
