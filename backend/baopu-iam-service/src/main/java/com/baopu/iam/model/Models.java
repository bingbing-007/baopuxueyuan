package com.baopu.iam.model;

import java.time.LocalDateTime;

public record Tenant(Long id, String name, String code, Integer status, LocalDateTime createdAt) {}

public record Department(Long id, Long tenantId, Long parentId, String name, Integer sortOrder, LocalDateTime createdAt) {}

public record Role(Long id, Long tenantId, String name, String code, LocalDateTime createdAt) {}

public record UserInfo(Long id, Long tenantId, String dingtalkUserId, String name, String mobile, Integer status, LocalDateTime createdAt) {}
