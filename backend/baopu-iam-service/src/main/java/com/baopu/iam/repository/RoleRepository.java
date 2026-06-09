package com.baopu.iam.repository;

import com.baopu.iam.model.Role;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepository {
  private final JdbcTemplate jdbc;

  public RoleRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<Role> findByTenant(Long tenantId) {
    return jdbc.query("SELECT id, tenant_id, name, code, created_at FROM bp_role WHERE tenant_id=? ORDER BY id", this::map, tenantId);
  }

  public Role create(Long tenantId, String name, String code) {
    jdbc.update("INSERT INTO bp_role (tenant_id, name, code) VALUES (?, ?, ?)", tenantId, name, code);
    return jdbc.query("SELECT id, tenant_id, name, code, created_at FROM bp_role WHERE tenant_id=? AND code=?", this::map, tenantId, code).stream().findFirst().orElseThrow();
  }

  public void assignUser(Long userId, Long roleId) {
    jdbc.update("INSERT IGNORE INTO bp_user_role (user_id, role_id) VALUES (?, ?)", userId, roleId);
  }

  private Role map(ResultSet rs, int n) throws SQLException {
    return new Role(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("name"), rs.getString("code"), rs.getTimestamp("created_at").toLocalDateTime());
  }
}
