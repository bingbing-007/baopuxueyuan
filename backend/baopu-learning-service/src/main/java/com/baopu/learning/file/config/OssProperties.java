package com.baopu.learning.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "baopu.oss")
public record OssProperties(String endpoint, String bucket, String accessKeyId, String accessKeySecret,
    String cdnDomain, Long expireSeconds) {}
