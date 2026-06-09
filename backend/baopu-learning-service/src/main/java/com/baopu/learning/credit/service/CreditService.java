package com.baopu.learning.credit.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditService {
  private final JdbcTemplate jdbc;

  public CreditService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<Map<String, Object>> listRules() {
    return jdbc.queryForList("SELECT id, tenant_id, name, code, action_type, credits, max_per_day, status FROM bp_credit_rule WHERE status=1 ORDER BY id");
  }

  public Map<String, Object> getAccount(Long userId) {
    ensureAccount(userId);
    var rows = jdbc.queryForList("SELECT total_earned, balance FROM bp_credit_account WHERE user_id=?", userId);
    if (rows.isEmpty()) return Map.of("totalEarned", 0, "balance", 0);
    var row = rows.get(0);
    return Map.of("totalEarned", row.get("total_earned"), "balance", row.get("balance"));
  }

  public List<Map<String, Object>> getRecords(Long userId, int limit) {
    return jdbc.queryForList(
        "SELECT id, action_type, credits, description, created_at FROM bp_credit_record WHERE user_id=? ORDER BY created_at DESC LIMIT ?",
        userId, limit);
  }

  @Transactional
  public void awardCredits(Long userId, String actionType, String description) {
    var rule = jdbc.queryForList(
        "SELECT id, credits, max_per_day FROM bp_credit_rule WHERE code=? AND status=1", actionType);
    if (rule.isEmpty()) return;
    var r = rule.get(0);
    int credits = ((Number) r.get("credits")).intValue();
    int maxPerDay = ((Number) r.get("max_per_day")).intValue();

    if (maxPerDay > 0) {
      int todayCount = jdbc.queryForObject(
          "SELECT COALESCE(SUM(credits), 0) FROM bp_credit_record WHERE user_id=? AND DATE(created_at)=? AND action_type=?",
          Integer.class, userId, LocalDate.now(), actionType);
      if (todayCount >= maxPerDay) return;
    }

    ensureAccount(userId);
    jdbc.update("UPDATE bp_credit_account SET total_earned=total_earned+?, balance=balance+? WHERE user_id=?", credits, credits, userId);
    jdbc.update("INSERT INTO bp_credit_record (user_id, rule_id, action_type, credits, description) VALUES (?, ?, ?, ?, ?)",
        userId, r.get("id"), actionType, credits, description);
  }

  private void ensureAccount(Long userId) {
    jdbc.update("INSERT IGNORE INTO bp_credit_account (user_id) VALUES (?)", userId);
  }
}
