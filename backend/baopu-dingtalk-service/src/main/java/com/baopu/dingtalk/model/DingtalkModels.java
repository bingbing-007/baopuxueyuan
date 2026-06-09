package com.baopu.dingtalk.model;

public record DingtalkUserInfo(
    String userId,
    String name,
    String mobile,
    String avatar,
    String email,
    String orgEmail) {}

public record DingtalkAccessToken(
    String accessToken,
    long expiresIn) {}
