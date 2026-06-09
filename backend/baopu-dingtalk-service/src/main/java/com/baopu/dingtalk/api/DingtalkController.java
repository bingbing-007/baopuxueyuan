package com.baopu.dingtalk.api;

import com.baopu.dingtalk.service.DingtalkAuthService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dingtalk")
public class DingtalkController {
  private final DingtalkAuthService authService;

  public DingtalkController(DingtalkAuthService authService) {
    this.authService = authService;
  }

  @GetMapping("/config")
  Map<String, Object> config() {
    return authService.getConfig();
  }
}
