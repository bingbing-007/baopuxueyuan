package com.baopu.iam.repository;

import com.baopu.iam.model.Department;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentRepository {
  private final JdbcTemplate jdbc;

  public DepartmentRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<Department> findByTenant(Long tenantId) {
    return jdbc.query("SELECT id, tenant_id, parent_id, name, sort_order, created_at FROM bp_department WHERE tenant_id=? ORDER BY sort_order, id", this::map, tenantId);
  }

  public Department create(Long tenantId, Long parentId, String name, int sortOrder) {
    jdbc.update("INSERT INTO bp_department (tenant_id, parent_id, name, sort_order) VALUES (?, ?, ?, ?)", tenantId, parentId, name, sortOrder);
    return jdbc.query("SELECT id, tenant_id, parent_id, name, sort_order, created_at FROM bp_department WHERE tenant_id=? AND name=? ORDER BY id DESC", this::map, tenantId, name).stream().findFirst().orElseThrow();
  }

  public void upsertByDingtalkId(Long tenantId, Long dingtalkDeptId, Long parentId, String name) {
    jdbc.update("INSERT INTO bp_department (tenant_id, dingtalk_dept_id, parent_id, name, sort_order) VALUES (?, ?, ?, ?, 0) ON DUPLICATE KEY UPDATE name=VALUES(name), parent_id=VALUES(parent_id)", tenantId, dingtalkDeptId, parentId, name);
  }

  private Department map(ResultSet rs, int n) throws SQLException {
    return new Department(rs.getLong("id"), rs.getLong("tenant_id"),
        rs.getLong("parent_id"), rs.getString("name"), rs.getInt("sort_order"),
        rs.getTimestamp("created_at").toLocalDateTime());
  }
}
