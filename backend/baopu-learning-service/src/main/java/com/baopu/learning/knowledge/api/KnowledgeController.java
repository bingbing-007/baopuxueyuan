package com.baopu.learning.knowledge.api;

import com.baopu.learning.knowledge.service.KnowledgeService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
  private final KnowledgeService knowledgeService;

  public KnowledgeController(KnowledgeService knowledgeService) { this.knowledgeService = knowledgeService; }

  @GetMapping
  List<Map<String, Object>> list(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String category,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    return knowledgeService.list(keyword, category, page, size);
  }

  @GetMapping("/{id}")
  Map<String, Object> detail(@PathVariable Long id) { return knowledgeService.getDetail(id); }

  @PostMapping
  Map<String, Object> create(HttpServletRequest req, @RequestBody Map<String, String> body) {
    return knowledgeService.create(1L, body.get("title"), body.get("content"),
        body.get("summary"), body.get("tags"), body.getOrDefault("category", "通用"),
        (Long) req.getAttribute("userId"));
  }

  @PutMapping("/{id}")
  Map<String, Object> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
    return knowledgeService.update(id, body.get("title"), body.get("content"),
        body.get("summary"), body.get("tags"), body.getOrDefault("category", "通用"));
  }

  @GetMapping("/tags/hot")
  List<Map<String, Object>> hotTags(@RequestParam(defaultValue = "20") int limit) {
    return knowledgeService.hotTags(limit);
  }
}
