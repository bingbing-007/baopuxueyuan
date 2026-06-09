package com.baopu.learning.api;

import com.baopu.learning.api.dto.CourseSummary;
import com.baopu.learning.api.dto.DashboardResponse;
import com.baopu.learning.api.dto.ProgressRequest;
import com.baopu.learning.service.LearningService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CourseController {
  private final LearningService learningService;

  public CourseController(LearningService learningService) {
    this.learningService = learningService;
  }

  @GetMapping("/courses")
  List<CourseSummary> listCourses(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
    return learningService.listCourses(userId);
  }

  @GetMapping("/courses/{courseId}")
  CourseSummary getCourse(
      @RequestHeader(value = "X-User-Id", required = false) Long userId,
      @PathVariable Long courseId) {
    return learningService.getCourse(userId, courseId);
  }

  @PostMapping("/courses/{courseId}/enroll")
  CourseSummary enroll(
      @RequestHeader("X-User-Id") Long userId,
      @PathVariable Long courseId) {
    return learningService.enroll(userId, courseId);
  }

  @PutMapping("/courses/{courseId}/progress")
  CourseSummary updateProgress(
      @RequestHeader("X-User-Id") Long userId,
      @PathVariable Long courseId,
      @Valid @RequestBody ProgressRequest request) {
    return learningService.updateProgress(userId, courseId, request);
  }

  @GetMapping("/me/dashboard")
  DashboardResponse dashboard(@RequestHeader("X-User-Id") Long userId) {
    return learningService.dashboard(userId);
  }
}
