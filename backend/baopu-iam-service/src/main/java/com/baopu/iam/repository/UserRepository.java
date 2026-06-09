package com.baopu.iam.repository;

import com.baopu.iam.model.UserInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
  private final JdbcTemplate jdbc;

  public UserRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<UserInfo> findByTenant(Long tenantId) {
    return jdbc.query("SELECT id, tenant_id, dingtalk_user_id, name, mobile, status, created_at FROM bp_user WHERE tenant_id=? ORDER BY id", this::map, tenantId);
  }

  public Optional<UserInfo> findById(Long id) {
    return jdbc.query("SELECT id, tenant_id, dingtalk_user_id, name, mobile, status, created_at FROM bp_user WHERE id=?", this::map, id).stream().findFirst();
  }

  public UserInfo upsert(Long tenantId, String dingtalkUserId, String name, String mobile) {
    jdbc.update("INSERT INTO bp_user (tenant_id, dingtalk_user_id, name, mobile) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE name=VALUES(name), mobile=VALUES(mobile)", tenantId, dingtalkUserId, name, mobile);
    return jdbc.query("SELECT id, tenant_id, dingtalk_user_id, name, mobile, status, created_at FROM bp_user WHERE tenant_id=? AND dingtalk_user_id=?", this::map, tenantId, dingtalkUserId).stream().findFirst().orElseThrow();
  }

  private UserInfo map(ResultSet rs, int n) throws SQLException {
    return new UserInfo(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("dingtalk_user_id"),
        rs.getString("name"), rs.getString("mobile"), rs.getInt("status"),
        rs.getTimestamp("created_at").toLocalDateTime());
  }
}
