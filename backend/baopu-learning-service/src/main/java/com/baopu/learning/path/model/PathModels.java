package com.baopu.learning.path.model;

import java.time.LocalDateTime;
import java.util.List;

public record LearningPath(Long id, Long tenantId, String title, String description, String coverUrl,
    String category, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {}

public record PathStage(Long id, Long pathId, String title, String description, Integer sortOrder,
    String unlockRule, List<PathStageCourse> courses, Boolean unlocked, Boolean completed) {}

public record PathStageCourse(Long id, Long stageId, Long courseId, Integer required, Integer sortOrder,
    String courseTitle, String courseCover, Integer progressPercent, Boolean enrolled) {}

public record UserPathProgress(Long id, Long userId, Long pathId, Long currentStageId,
    Integer completed, LocalDateTime enrolledAt, LocalDateTime completedAt,
    LearningPath path, List<PathStage> stages) {}
