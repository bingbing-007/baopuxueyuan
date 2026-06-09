package com.baopu.learning.exam.model;

import java.time.LocalDateTime;

public record Exam(Long id, Long tenantId, Long courseId, String title, String description,
    Integer durationMinutes, Integer passScore, Integer totalScore, Integer status,
    LocalDateTime createdAt, LocalDateTime updatedAt) {}

public record Question(Long id, Long tenantId, String type, String stem, String options,
    String answer, String explanation, Integer score, Integer status, LocalDateTime createdAt) {}

public record ExamRecord(Long id, Long examId, Long userId, Integer score, Integer passed,
    LocalDateTime startedAt, LocalDateTime submittedAt) {}

public record ExamAnswer(Long id, Long recordId, Long questionId, String userAnswer,
    Integer score, Integer correct, LocalDateTime createdAt) {}
