package com.baopu.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthGatewayFilter implements GlobalFilter, Ordered {
  private static final List<String> PUBLIC_PATHS = List.of(
      "/api/auth/", "/api/health", "/api/courses", "/api/courses/", "/api/exams", "/api/dingtalk/config");

  private final SecretKey key;

  public JwtAuthGatewayFilter(@Value("${baopu.jwt.secret:baopu-learning-platform-jwt-secret-key-2026-min-256-bits!!}") String secret) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();
    if (isPublic(path)) return chain.filter(exchange);

    String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    try {
      Claims claims = Jwts.parser().verifyWith(key).build()
          .parseSignedClaims(header.substring(7)).getPayload();
      var req = exchange.getRequest().mutate()
          .header("X-User-Id", claims.getSubject())
          .header("X-User-Name", claims.get("name", String.class))
          .build();
      return chain.filter(exchange.mutate().request(req).build());
    } catch (Exception e) {
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }
  }

  @Override
  public int getOrder() { return -100; }

  private boolean isPublic(String path) {
    return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
  }
}

