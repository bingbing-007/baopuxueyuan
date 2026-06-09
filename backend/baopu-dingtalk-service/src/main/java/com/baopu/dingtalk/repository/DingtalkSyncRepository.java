package com.baopu.dingtalk.repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DingtalkSyncRepository {
  private final JdbcTemplate jdbc;

  public DingtalkSyncRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public void upsertDepartment(Long tenantId, Long dingtalkDeptId, Long parentId, String name) {
    jdbc.update("""
        INSERT INTO bp_department (tenant_id, dingtalk_dept_id, parent_id, name, sort_order)
        VALUES (?, ?, ?, ?, 0)
        ON DUPLICATE KEY UPDATE name=VALUES(name), parent_id=VALUES(parent_id)
        """, tenantId, dingtalkDeptId, parentId, name);
  }

  public void upsertUser(Long tenantId, String dingtalkUserId, String name, String mobile) {
    jdbc.update("""
        INSERT INTO bp_user (tenant_id, dingtalk_user_id, name, mobile)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE name=VALUES(name), mobile=VALUES(mobile)
        """, tenantId, dingtalkUserId, name, mobile);
  }

  public List<Long> listDingtalkDeptIds() {
    return jdbc.queryForList(
        "SELECT DISTINCT dingtalk_dept_id FROM bp_department WHERE dingtalk_dept_id IS NOT NULL", Long.class);
  }
}
