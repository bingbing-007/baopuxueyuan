package com.baopu.learning.api;

import com.baopu.learning.api.dto.CourseSummary;
import com.baopu.learning.api.dto.DashboardResponse;
import com.baopu.learning.api.dto.ProgressRequest;
import com.baopu.learning.service.LearningService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  List<CourseSummary> listCourses(HttpServletRequest request) {
    Long userId = getUserId(request);
    return learningService.listCourses(userId);
  }

  @GetMapping("/courses/{courseId}")
  CourseSummary getCourse(HttpServletRequest request, @PathVariable Long courseId) {
    Long userId = getUserId(request);
    return learningService.getCourse(userId, courseId);
  }

  @PostMapping("/courses/{courseId}/enroll")
  CourseSummary enroll(HttpServletRequest request, @PathVariable Long courseId) {
    Long userId = requireUserId(request);
    return learningService.enroll(userId, courseId);
  }

  @PutMapping("/courses/{courseId}/progress")
  CourseSummary updateProgress(
      HttpServletRequest request,
      @PathVariable Long courseId,
      @Valid @RequestBody ProgressRequest progressRequest) {
    Long userId = requireUserId(request);
    return learningService.updateProgress(userId, courseId, progressRequest);
  }

  @GetMapping("/me/dashboard")
  DashboardResponse dashboard(HttpServletRequest request) {
    Long userId = requireUserId(request);
    return learningService.dashboard(userId);
  }

  private Long getUserId(HttpServletRequest request) {
    Object attr = request.getAttribute("userId");
    return attr instanceof Long id ? id : null;
  }

  private Long requireUserId(HttpServletRequest request) {
    Object attr = request.getAttribute("userId");
    if (attr instanceof Long id) {
      return id;
    }
    throw new org.springframework.web.server.ResponseStatusException(
        org.springframework.http.HttpStatus.UNAUTHORIZED, "Please login first");
  }
}
