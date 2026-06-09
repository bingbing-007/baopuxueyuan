package com.baopu.learning.repository;

import com.baopu.learning.model.Enrollment;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EnrollmentRepository {
  private final JdbcTemplate jdbcTemplate;

  public EnrollmentRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Enrollment> findByUserId(Long userId) {
    return jdbcTemplate.query("""
        SELECT id, user_id, course_id, progress_percent, completed, enrolled_at, completed_at
        FROM bp_enrollment
        WHERE user_id = ?
        ORDER BY enrolled_at DESC
        """, this::mapEnrollment, userId);
  }

  public Optional<Enrollment> findByUserAndCourse(Long userId, Long courseId) {
    return jdbcTemplate.query("""
            SELECT id, user_id, course_id, progress_percent, completed, enrolled_at, completed_at
            FROM bp_enrollment
            WHERE user_id = ? AND course_id = ?
            """, this::mapEnrollment, userId, courseId)
        .stream()
        .findFirst();
  }

  public Enrollment enroll(Long userId, Long courseId) {
    jdbcTemplate.update("""
        INSERT INTO bp_enrollment (user_id, course_id)
        VALUES (?, ?)
        ON DUPLICATE KEY UPDATE enrolled_at = enrolled_at
        """, userId, courseId);
    return findByUserAndCourse(userId, courseId).orElseThrow();
  }

  public Enrollment updateProgress(Long userId, Long courseId, int progressPercent) {
    jdbcTemplate.update("""
        UPDATE bp_enrollment
        SET progress_percent = ?,
            completed = CASE WHEN ? >= 100 THEN 1 ELSE 0 END,
            completed_at = CASE WHEN ? >= 100 THEN COALESCE(completed_at, CURRENT_TIMESTAMP) ELSE NULL END
        WHERE user_id = ? AND course_id = ?
        """, progressPercent, progressPercent, progressPercent, userId, courseId);
    return findByUserAndCourse(userId, courseId).orElseThrow();
  }

  private Enrollment mapEnrollment(ResultSet rs, int rowNum) throws SQLException {
    var completedAt = rs.getTimestamp("completed_at");
    return new Enrollment(
        rs.getLong("id"),
        rs.getLong("user_id"),
        rs.getLong("course_id"),
        rs.getInt("progress_percent"),
        rs.getInt("completed"),
        rs.getTimestamp("enrolled_at").toLocalDateTime(),
        completedAt == null ? null : completedAt.toLocalDateTime());
  }
}
