package com.baopu.learning.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank @Size(max = 64) String dingtalkUserId,
    @NotBlank @Size(max = 100) String name,
    @Size(max = 30) String mobile) {}
