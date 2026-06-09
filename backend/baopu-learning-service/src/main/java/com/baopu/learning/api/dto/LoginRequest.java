package com.baopu.learning.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    String dingtalkUserId,
    String name,
    String mobile,
    @NotBlank String authCode) {}
