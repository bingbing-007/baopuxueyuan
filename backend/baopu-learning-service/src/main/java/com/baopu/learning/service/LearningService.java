package com.baopu.learning.service;

import com.baopu.learning.api.dto.CourseSummary;
import com.baopu.learning.api.dto.DashboardResponse;
import com.baopu.learning.api.dto.LoginResponse;
import com.baopu.learning.api.dto.ProgressRequest;
import com.baopu.learning.model.Course;
import com.baopu.learning.model.Enrollment;
import com.baopu.learning.repository.CourseRepository;
import com.baopu.learning.repository.EnrollmentRepository;
import com.baopu.learning.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LearningService {
  private static final long DEFAULT_TENANT_ID = 1L;

  private final UserRepository userRepository;
  private final CourseRepository courseRepository;
  private final EnrollmentRepository enrollmentRepository;

  public LearningService(
      UserRepository userRepository,
      CourseRepository courseRepository,
      EnrollmentRepository enrollmentRepository) {
    this.userRepository = userRepository;
    this.courseRepository = courseRepository;
    this.enrollmentRepository = enrollmentRepository;
  }

  public LoginResponse login(String dingtalkUserId, String name, String mobile) {
    var user = userRepository.upsert(DEFAULT_TENANT_ID, dingtalkUserId, name, mobile);
    return new LoginResponse(user.id(), user.name(), "demo-token-" + user.id());
  }

  public List<CourseSummary> listCourses(Long userId) {
    Map<Long, Enrollment> enrollments = enrollmentsByCourse(userId);
    return courseRepository.findPublished().stream()
        .map(course -> toSummary(course, enrollments.get(course.id())))
        .toList();
  }

  public CourseSummary getCourse(Long userId, Long courseId) {
    Course course = courseRepository.findPublishedById(courseId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    Enrollment enrollment = userId == null
        ? null
        : enrollmentRepository.findByUserAndCourse(userId, courseId).orElse(null);
    return toSummary(course, enrollment);
  }

  public CourseSummary enroll(Long userId, Long courseId) {
    requireUser(userId);
    Course course = requireCourse(courseId);
    Enrollment enrollment = enrollmentRepository.enroll(userId, courseId);
    return toSummary(course, enrollment);
  }

  public CourseSummary updateProgress(Long userId, Long courseId, ProgressRequest request) {
    requireUser(userId);
    Course course = requireCourse(courseId);
    enrollmentRepository.findByUserAndCourse(userId, courseId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Please enroll first"));
    Enrollment enrollment = enrollmentRepository.updateProgress(userId, courseId, request.progressPercent());
    return toSummary(course, enrollment);
  }

  public DashboardResponse dashboard(Long userId) {
    requireUser(userId);
    List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
    Map<Long, Course> courses = courseRepository.findPublished().stream()
        .collect(Collectors.toMap(Course::id, Function.identity()));
    List<CourseSummary> summaries = enrollments.stream()
        .map(enrollment -> {
          Course course = courses.get(enrollment.courseId());
          return course == null ? null : toSummary(course, enrollment);
        })
        .filter(summary -> summary != null)
        .toList();
    long completedCount = enrollments.stream().filter(enrollment -> enrollment.completed() == 1).count();
    int averageProgress = enrollments.isEmpty()
        ? 0
        : (int) Math.round(enrollments.stream().mapToInt(Enrollment::progressPercent).average().orElse(0));
    return new DashboardResponse(enrollments.size(), completedCount, averageProgress, summaries);
  }

  private Course requireCourse(Long courseId) {
    return courseRepository.findPublishedById(courseId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
  }

  private void requireUser(Long userId) {
    if (userId == null || userRepository.findById(userId).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first");
    }
  }

  private Map<Long, Enrollment> enrollmentsByCourse(Long userId) {
    if (userId == null) {
      return Map.of();
    }
    return enrollmentRepository.findByUserId(userId).stream()
        .collect(Collectors.toMap(Enrollment::courseId, Function.identity(), (left, right) -> left));
  }

  private CourseSummary toSummary(Course course, Enrollment enrollment) {
    return new CourseSummary(
        course.id(),
        course.title(),
        course.description(),
        course.coverUrl(),
        course.category(),
        course.lecturer(),
        course.durationMinutes(),
        course.price(),
        enrollment == null ? 0 : enrollment.progressPercent(),
        enrollment != null);
  }
}
