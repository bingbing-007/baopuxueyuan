package com.baopu.learning.model;

import java.time.LocalDateTime;

public record User(
    Long id,
    Long tenantId,
    String dingtalkUserId,
    String name,
    String mobile,
    Integer status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
