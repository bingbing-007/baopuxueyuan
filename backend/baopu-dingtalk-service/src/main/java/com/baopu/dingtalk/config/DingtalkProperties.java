package com.baopu.dingtalk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "baopu.dingtalk")
public record DingtalkProperties(
    String appKey,
    String appSecret,
    String corpId,
    Long agentId) {}
