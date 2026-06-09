package com.baopu.iam.repository;

import com.baopu.iam.model.Tenant;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TenantRepository {
  private final JdbcTemplate jdbc;

  public TenantRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<Tenant> findAll() {
    return jdbc.query("SELECT id, name, code, status, created_at FROM bp_tenant ORDER BY id", this::map);
  }

  public Optional<Tenant> findById(Long id) {
    return jdbc.query("SELECT id, name, code, status, created_at FROM bp_tenant WHERE id=?", this::map, id).stream().findFirst();
  }

  public Tenant create(String name, String code) {
    jdbc.update("INSERT INTO bp_tenant (name, code) VALUES (?, ?)", name, code);
    return jdbc.query("SELECT id, name, code, status, created_at FROM bp_tenant WHERE code=?", this::map, code)
        .stream().findFirst().orElseThrow();
  }

  private Tenant map(ResultSet rs, int n) throws SQLException {
    return new Tenant(rs.getLong("id"), rs.getString("name"), rs.getString("code"),
        rs.getInt("status"), rs.getTimestamp("created_at").toLocalDateTime());
  }
}
