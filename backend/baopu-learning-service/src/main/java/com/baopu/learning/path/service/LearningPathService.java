package com.baopu.learning.path.service;

import com.baopu.learning.model.Enrollment;
import com.baopu.learning.path.model.LearningPath;
import com.baopu.learning.path.model.PathStage;
import com.baopu.learning.path.model.PathStageCourse;
import com.baopu.learning.path.repository.LearningPathRepository;
import com.baopu.learning.repository.CourseRepository;
import com.baopu.learning.repository.EnrollmentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LearningPathService {
  private final LearningPathRepository pathRepo;
  private final CourseRepository courseRepo;
  private final EnrollmentRepository enrollmentRepo;

  public LearningPathService(LearningPathRepository pr, CourseRepository cr, EnrollmentRepository er) {
    this.pathRepo = pr; this.courseRepo = cr; this.enrollmentRepo = er;
  }

  public List<LearningPath> listPaths() { return pathRepo.findPublished(); }

  public Map<String, Object> getPathDetail(Long userId, Long pathId) {
    var path = pathRepo.findById(pathId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    List<Long> stageIds = pathRepo.findStageIds(pathId);
    Map<Long, Enrollment> enrollments = userId != null
        ? enrollmentRepo.findByUserId(userId).stream().collect(Collectors.toMap(Enrollment::courseId, e -> e, (a, b) -> a))
        : Map.of();

    List<PathStage> stages = new ArrayList<>();
    boolean prevCompleted = true;
    for (Long stageId : stageIds) {
      boolean unlocked = prevCompleted;
      List<Long> courseIds = pathRepo.findStageCourseIds(stageId);
      List<PathStageCourse> stageCourses = courseIds.stream().map(cid -> {
        var course = courseRepo.findPublishedById(cid).orElse(null);
        Enrollment e = enrollments.get(cid);
        return new PathStageCourse(null, stageId, cid, 1, 0,
            course != null ? course.title() : "", course != null ? course.coverUrl() : "",
            e != null ? e.progressPercent() : 0, e != null);
      }).toList();
      boolean stageCompleted = !stageCourses.isEmpty() && stageCourses.stream().allMatch(c -> c.progressPercent() >= 100);
      stages.add(new PathStage(stageId, pathId, "", "", stages.size(), "previous_completed", stageCourses, unlocked, stageCompleted));
      prevCompleted = stageCompleted;
    }

    boolean enrolled = userId != null && pathRepo.findUserPathEnrollment(userId, pathId).isPresent();
    return Map.of("path", path, "stages", stages, "enrolled", enrolled);
  }

  public Map<String, Object> enroll(Long userId, Long pathId) {
    pathRepo.enrollUser(userId, pathId);
    return Map.of("status", "ok");
  }
}
