package com.baopu.learning.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
  private final SecretKey key;
  private final long expirationMs;

  public JwtProvider(
      @Value("${baopu.jwt.secret:baopu-learning-platform-jwt-secret-key-2026-min-256-bits}") String secret,
      @Value("${baopu.jwt.expiration-ms:86400000}") long expirationMs) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  public String generate(Long userId, String name) {
    Date now = new Date();
    return Jwts.builder()
        .subject(String.valueOf(userId))
        .claim("name", name)
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expirationMs))
        .signWith(key)
        .compact();
  }

  public Claims validate(String token) {
    return Jwts.parser().verifyWith(key).build()
        .parseSignedClaims(token).getPayload();
  }
}
