package com.baopu.learning.analytics.api;

import com.baopu.learning.analytics.service.AnalyticsService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
  private final AnalyticsService analyticsService;

  public AnalyticsController(AnalyticsService analyticsService) { this.analyticsService = analyticsService; }

  @GetMapping("/overview")
  Map<String, Object> overview() { return analyticsService.overview(); }

  @GetMapping("/top-courses")
  List<Map<String, Object>> topCourses(@RequestParam(defaultValue = "10") int limit) {
    return analyticsService.topCourses(limit);
  }

  @GetMapping("/top-learners")
  List<Map<String, Object>> topLearners(@RequestParam(defaultValue = "10") int limit) {
    return analyticsService.topLearners(limit);
  }

  @GetMapping("/monthly-trend")
  List<Map<String, Object>> monthlyTrend() { return analyticsService.monthlyTrend(); }
}
