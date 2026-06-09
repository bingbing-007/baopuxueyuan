package com.baopu.learning.repository;

import com.baopu.learning.model.Course;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CourseRepository {
  private final JdbcTemplate jdbcTemplate;

  public CourseRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Course> findPublished() {
    return jdbcTemplate.query("""
        SELECT id, tenant_id, title, description, cover_url, category, lecturer, duration_minutes,
               price, status, created_at, updated_at
        FROM bp_course
        WHERE status = 1
        ORDER BY id
        """, this::mapCourse);
  }

  public Optional<Course> findPublishedById(Long id) {
    return jdbcTemplate.query("""
            SELECT id, tenant_id, title, description, cover_url, category, lecturer, duration_minutes,
                   price, status, created_at, updated_at
            FROM bp_course
            WHERE id = ? AND status = 1
            """, this::mapCourse, id)
        .stream()
        .findFirst();
  }

  private Course mapCourse(ResultSet rs, int rowNum) throws SQLException {
    return new Course(
        rs.getLong("id"),
        rs.getLong("tenant_id"),
        rs.getString("title"),
        rs.getString("description"),
        rs.getString("cover_url"),
        rs.getString("category"),
        rs.getString("lecturer"),
        rs.getInt("duration_minutes"),
        rs.getBigDecimal("price"),
        rs.getInt("status"),
        rs.getTimestamp("created_at").toLocalDateTime(),
        rs.getTimestamp("updated_at").toLocalDateTime());
  }
}
