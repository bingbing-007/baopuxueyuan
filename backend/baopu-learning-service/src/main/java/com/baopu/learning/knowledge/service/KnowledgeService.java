package com.baopu.learning.knowledge.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeService {
  private final JdbcTemplate jdbc;

  public KnowledgeService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<Map<String, Object>> list(String keyword, String category, int page, int size) {
    StringBuilder sql = new StringBuilder("SELECT id, title, summary, tags, category, view_count, created_at, updated_at FROM bp_knowledge_article WHERE status=1");
    var params = new java.util.ArrayList<>();

    if (keyword != null && !keyword.isBlank()) {
      sql.append(" AND MATCH(title, content, tags) AGAINST(? IN BOOLEAN MODE)");
      params.add(keyword + "*");
    }
    if (category != null && !category.isBlank()) {
      sql.append(" AND category=?");
      params.add(category);
    }
    sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
    params.add(size);
    params.add((page - 1) * size);

    return jdbc.queryForList(sql.toString(), params.toArray());
  }

  public Map<String, Object> getDetail(Long id) {
    jdbc.update("UPDATE bp_knowledge_article SET view_count=view_count+1 WHERE id=?", id);
    var rows = jdbc.queryForList("SELECT id, title, content, summary, tags, category, view_count, created_at, updated_at FROM bp_knowledge_article WHERE id=? AND status=1", id);
    if (rows.isEmpty()) throw new IllegalArgumentException("Article not found");
    return rows.get(0);
  }

  public Map<String, Object> create(Long tenantId, String title, String content, String summary, String tags, String category, Long authorId) {
    jdbc.update("INSERT INTO bp_knowledge_article (tenant_id, title, content, summary, tags, category, author_id) VALUES (?,?,?,?,?,?,?)",
        tenantId, title, content, summary, tags, category, authorId);
    return Map.of("status", "ok");
  }

  public Map<String, Object> update(Long id, String title, String content, String summary, String tags, String category) {
    jdbc.update("UPDATE bp_knowledge_article SET title=?, content=?, summary=?, tags=?, category=? WHERE id=?",
        title, content, summary, tags, category, id);
    return Map.of("status", "ok");
  }

  public List<Map<String, Object>> hotTags(int limit) {
    return jdbc.queryForList("""
        SELECT tag, COUNT(*) AS cnt FROM (
          SELECT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(tags, ',', n.n), ',', -1)) AS tag
          FROM bp_knowledge_article
          JOIN (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) n
          ON CHAR_LENGTH(tags) - CHAR_LENGTH(REPLACE(tags, ',', '')) >= n.n - 1
          WHERE status=1
        ) t GROUP BY tag ORDER BY cnt DESC LIMIT ?
        """, limit);
  }
}
