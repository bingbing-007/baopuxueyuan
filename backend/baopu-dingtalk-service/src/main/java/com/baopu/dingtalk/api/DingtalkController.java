package com.baopu.dingtalk.api;

import com.baopu.dingtalk.service.DingtalkAuthService;
import com.baopu.dingtalk.service.MessageService;
import com.baopu.dingtalk.service.OrgSyncService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dingtalk")
public class DingtalkController {
  private final DingtalkAuthService authService;
  private final OrgSyncService orgSyncService;
  private final MessageService messageService;

  public DingtalkController(DingtalkAuthService authService, OrgSyncService orgSyncService, MessageService messageService) {
    this.authService = authService;
    this.orgSyncService = orgSyncService;
    this.messageService = messageService;
  }

  @GetMapping("/config")
  Map<String, Object> config() { return authService.getConfig(); }

  @PostMapping("/sync/departments")
  Map<String, String> syncDepartments() {
    orgSyncService.syncDepartments();
    return Map.of("status", "ok");
  }

  @PostMapping("/sync/users")
  Map<String, String> syncUsers() {
    orgSyncService.syncUsers();
    return Map.of("status", "ok");
  }

  @PostMapping("/sync/full")
  Map<String, String> fullSync() {
    orgSyncService.fullSync();
    return Map.of("status", "ok");
  }

  @PostMapping("/message/send")
  Map<String, String> sendMessage(@RequestBody Map<String, String> body) {
    messageService.sendCourseReminder(
        body.get("dingtalkUserId"),
        body.getOrDefault("title", "课程提醒"),
        body.getOrDefault("url", ""));
    return Map.of("status", "ok");
  }
}
