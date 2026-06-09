package com.baopu.learning.path.repository;

import com.baopu.learning.path.model.LearningPath;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LearningPathRepository {
  private final JdbcTemplate jdbc;

  public LearningPathRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<LearningPath> findPublished() {
    return jdbc.query("SELECT id, tenant_id, title, description, cover_url, category, status, created_at, updated_at FROM bp_learning_path WHERE status=1 ORDER BY id", this::map);
  }

  public Optional<LearningPath> findById(Long id) {
    return jdbc.query("SELECT id, tenant_id, title, description, cover_url, category, status, created_at, updated_at FROM bp_learning_path WHERE id=? AND status=1", this::map, id).stream().findFirst();
  }

  public List<Long> findStageIds(Long pathId) {
    return jdbc.queryForList("SELECT id FROM bp_path_stage WHERE path_id=? ORDER BY sort_order", Long.class, pathId);
  }

  public List<Long> findStageCourseIds(Long stageId) {
    return jdbc.queryForList("SELECT course_id FROM bp_path_stage_course WHERE stage_id=? ORDER BY sort_order", Long.class, stageId);
  }

  public boolean isStageUnlocked(Long userId, Long stageId, String unlockRule) {
    if ("none".equals(unlockRule)) return true;
    return switch (unlockRule) {
      case "previous_completed" -> {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM bp_path_stage ps WHERE ps.id=? AND NOT EXISTS (SELECT 1 FROM bp_path_stage_course psc LEFT JOIN bp_enrollment e ON psc.course_id=e.course_id AND e.user_id=? AND e.completed=0 WHERE psc.stage_id=(SELECT id FROM bp_path_stage WHERE path_id=ps.path_id AND sort_order=ps.sort_order-1))", Long.class, stageId, userId);
        yield count != null && count > 0;
      }
      default -> false;
    };
  }

  public void enrollUser(Long userId, Long pathId) {
    jdbc.update("INSERT IGNORE INTO bp_user_path_enrollment (user_id, path_id) VALUES (?, ?)", userId, pathId);
  }

  public Optional<Long> findUserPathEnrollment(Long userId, Long pathId) {
    var rows = jdbc.queryForList("SELECT id FROM bp_user_path_enrollment WHERE user_id=? AND path_id=?", Long.class, userId, pathId);
    return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
  }

  private LearningPath map(ResultSet rs, int n) throws SQLException {
    return new LearningPath(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("title"),
        rs.getString("description"), rs.getString("cover_url"), rs.getString("category"),
        rs.getInt("status"), rs.getTimestamp("created_at").toLocalDateTime(),
        rs.getTimestamp("updated_at").toLocalDateTime());
  }
}
