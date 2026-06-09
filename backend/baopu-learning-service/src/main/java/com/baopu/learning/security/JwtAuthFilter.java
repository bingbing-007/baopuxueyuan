package com.baopu.learning.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private static final List<String> PUBLIC_PATHS = List.of(
      ("/api/auth/login", "/api/auth/dingtalk/login", "/api/health", "/api/courses", "/api/courses/", "/api/exams"));

  private final JwtProvider jwtProvider;
  private final ObjectMapper objectMapper;

  public JwtAuthFilter(JwtProvider jwtProvider, ObjectMapper objectMapper) {
    this.jwtProvider = jwtProvider;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String path = request.getRequestURI();
    if (isPublic(path)) {
      chain.doFilter(request, response);
      return;
    }
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      sendError(response, 401, "Missing or invalid Authorization header");
      return;
    }
    try {
      Claims claims = jwtProvider.validate(header.substring(7));
      request.setAttribute("userId", Long.parseLong(claims.getSubject()));
      request.setAttribute("userName", claims.get("name", String.class));
      chain.doFilter(request, response);
    } catch (JwtException e) {
      sendError(response, 401, "Invalid or expired token");
    }
  }

  private boolean isPublic(String path) {
    return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
  }

  private void sendError(HttpServletResponse response, int status, String message) throws IOException {
    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(objectMapper.writeValueAsString(Map.of("message", message)));
  }
}

