package com.baopu.learning.repository;

import com.baopu.learning.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
  private final JdbcTemplate jdbcTemplate;

  public UserRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Optional<User> findByDingtalkUserId(Long tenantId, String dingtalkUserId) {
    return jdbcTemplate.query("""
            SELECT id, tenant_id, dingtalk_user_id, name, mobile, status, created_at, updated_at
            FROM bp_user
            WHERE tenant_id = ? AND dingtalk_user_id = ?
            """, this::mapUser, tenantId, dingtalkUserId)
        .stream()
        .findFirst();
  }

  public Optional<User> findById(Long id) {
    return jdbcTemplate.query("""
            SELECT id, tenant_id, dingtalk_user_id, name, mobile, status, created_at, updated_at
            FROM bp_user
            WHERE id = ?
            """, this::mapUser, id)
        .stream()
        .findFirst();
  }

  public User upsert(Long tenantId, String dingtalkUserId, String name, String mobile) {
    jdbcTemplate.update("""
        INSERT INTO bp_user (tenant_id, dingtalk_user_id, name, mobile)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE name = VALUES(name), mobile = VALUES(mobile), updated_at = CURRENT_TIMESTAMP
        """, tenantId, dingtalkUserId, name, mobile);
    return findByDingtalkUserId(tenantId, dingtalkUserId).orElseThrow();
  }

  private User mapUser(ResultSet rs, int rowNum) throws SQLException {
    return new User(
        rs.getLong("id"),
        rs.getLong("tenant_id"),
        rs.getString("dingtalk_user_id"),
        rs.getString("name"),
        rs.getString("mobile"),
        rs.getInt("status"),
        rs.getTimestamp("created_at").toLocalDateTime(),
        rs.getTimestamp("updated_at").toLocalDateTime());
  }
}
