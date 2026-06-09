package com.baopu.learning.path.api;

import com.baopu.learning.path.model.LearningPath;
import com.baopu.learning.path.service.LearningPathService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paths")
public class LearningPathController {
  private final LearningPathService pathService;

  public LearningPathController(LearningPathService pathService) { this.pathService = pathService; }

  @GetMapping
  List<LearningPath> list() { return pathService.listPaths(); }

  @GetMapping("/{pathId}")
  Map<String, Object> detail(HttpServletRequest req, @PathVariable Long pathId) {
    Long userId = (Long) req.getAttribute("userId");
    return pathService.getPathDetail(userId, pathId);
  }

  @PostMapping("/{pathId}/enroll")
  Map<String, Object> enroll(HttpServletRequest req, @PathVariable Long pathId) {
    Long userId = (Long) req.getAttribute("userId");
    return pathService.enroll(userId, pathId);
  }
}
