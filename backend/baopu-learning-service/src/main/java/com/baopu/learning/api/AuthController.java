package com.baopu.learning.api;

import com.baopu.learning.api.dto.LoginRequest;
import com.baopu.learning.api.dto.LoginResponse;
import com.baopu.learning.service.LearningService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final LearningService learningService;

  public AuthController(LearningService learningService) {
    this.learningService = learningService;
  }

  @PostMapping("/login")
  LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return learningService.login(request.dingtalkUserId(), request.name(), request.mobile());
  }
}
