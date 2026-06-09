package com.baopu.learning.analytics.service;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
  private final JdbcTemplate jdbc;

  public AnalyticsService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public Map<String, Object> overview() {
    long totalUsers = jdbc.queryForObject("SELECT COUNT(*) FROM bp_user WHERE status=1", Long.class);
    long totalCourses = jdbc.queryForObject("SELECT COUNT(*) FROM bp_course WHERE status=1", Long.class);
    long totalEnrollments = jdbc.queryForObject("SELECT COUNT(*) FROM bp_enrollment", Long.class);
    long completedEnrollments = jdbc.queryForObject("SELECT COUNT(*) FROM bp_enrollment WHERE completed=1", Long.class);
    double completionRate = totalEnrollments > 0 ? Math.round(completedEnrollments * 10000.0 / totalEnrollments) / 100.0 : 0;
    long totalExams = jdbc.queryForObject("SELECT COUNT(*) FROM bp_exam WHERE status=1", Long.class);
    long examPassed = jdbc.queryForObject("SELECT COUNT(*) FROM bp_exam_record WHERE passed=1", Long.class);
    double avgProgress = jdbc.queryForObject("SELECT COALESCE(AVG(progress_percent), 0) FROM bp_enrollment", Double.class);

    return Map.of(
        "totalUsers", totalUsers,
        "totalCourses", totalCourses,
        "totalEnrollments", totalEnrollments,
        "completedEnrollments", completedEnrollments,
        "completionRate", completionRate,
        "totalExams", totalExams,
        "examPassed", examPassed,
        "averageProgress", Math.round(avgProgress * 100.0) / 100.0
    );
  }

  public List<Map<String, Object>> topCourses(int limit) {
    return jdbc.queryForList("""
        SELECT c.id, c.title, c.category, c.lecturer,
               COUNT(e.id) AS enrollment_count,
               COALESCE(SUM(e.completed), 0) AS completed_count,
               COALESCE(ROUND(AVG(e.progress_percent), 1), 0) AS avg_progress
        FROM bp_course c
        LEFT JOIN bp_enrollment e ON c.id = e.course_id
        WHERE c.status = 1
        GROUP BY c.id
        ORDER BY enrollment_count DESC
        LIMIT ?
        """, limit);
  }

  public List<Map<String, Object>> topLearners(int limit) {
    return jdbc.queryForList("""
        SELECT u.id, u.name, u.mobile,
               COUNT(e.id) AS enrolled_count,
               COALESCE(SUM(e.completed), 0) AS completed_count,
               COALESCE(ROUND(AVG(e.progress_percent), 1), 0) AS avg_progress
        FROM bp_user u
        LEFT JOIN bp_enrollment e ON u.id = e.user_id
        WHERE u.status = 1
        GROUP BY u.id
        ORDER BY completed_count DESC, enrolled_count DESC
        LIMIT ?
        """, limit);
  }

  public List<Map<String, Object>> monthlyTrend() {
    return jdbc.queryForList("""
        SELECT DATE_FORMAT(enrolled_at, '%Y-%m') AS month,
               COUNT(*) AS enrollments,
               COALESCE(SUM(completed), 0) AS completions
        FROM bp_enrollment
        WHERE enrolled_at >= DATE_SUB(NOW(), INTERVAL 12 MONTH)
        GROUP BY DATE_FORMAT(enrolled_at, '%Y-%m')
        ORDER BY month
        """);
  }
}
