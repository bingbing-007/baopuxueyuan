package com.baopu.learning.api.dto;

import java.util.List;

public record DashboardResponse(
    long enrolledCount,
    long completedCount,
    int averageProgress,
    List<CourseSummary> courses) {}
