package com.baopu.learning.exam.repository;

import com.baopu.learning.exam.model.Exam;
import com.baopu.learning.exam.model.ExamRecord;
import com.baopu.learning.exam.model.Question;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ExamRepository {
  private final JdbcTemplate jdbc;

  public ExamRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  public List<Exam> findPublished() {
    return jdbc.query("SELECT id, tenant_id, course_id, title, description, duration_minutes, pass_score, total_score, status, created_at, updated_at FROM bp_exam WHERE status=1 ORDER BY id", this::mapExam);
  }

  public Optional<Exam> findById(Long id) {
    return jdbc.query("SELECT id, tenant_id, course_id, title, description, duration_minutes, pass_score, total_score, status, created_at, updated_at FROM bp_exam WHERE id=? AND status=1", this::mapExam, id).stream().findFirst();
  }

  public List<Question> findQuestionsByExam(Long examId) {
    return jdbc.query("SELECT q.id, q.tenant_id, q.type, q.stem, q.options, q.answer, q.explanation, q.score, q.status, q.created_at FROM bp_question q JOIN bp_exam_question eq ON q.id=eq.question_id WHERE eq.exam_id=? ORDER BY eq.sort_order", this::mapQuestion, examId);
  }

  public ExamRecord startExam(Long examId, Long userId) {
    jdbc.update("INSERT INTO bp_exam_record (exam_id, user_id, started_at) VALUES (?, ?, NOW())", examId, userId);
    return jdbc.query("SELECT id, exam_id, user_id, score, passed, started_at, submitted_at FROM bp_exam_record WHERE exam_id=? AND user_id=? ORDER BY id DESC", this::mapRecord, examId, userId).stream().findFirst().orElseThrow();
  }

  public void submitAnswer(Long recordId, Long questionId, String userAnswer, Integer correct) {
    jdbc.update("INSERT INTO bp_exam_answer (record_id, question_id, user_answer, correct) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE user_answer=VALUES(user_answer), correct=VALUES(correct)", recordId, questionId, userAnswer, correct);
  }

  public ExamRecord finishExam(Long recordId, int totalScore, int passScore) {
    jdbc.update("UPDATE bp_exam_record SET score=?, passed=CASE WHEN ?>=? THEN 1 ELSE 0 END, submitted_at=NOW() WHERE id=?", totalScore, totalScore, passScore, recordId);
    return jdbc.query("SELECT id, exam_id, user_id, score, passed, started_at, submitted_at FROM bp_exam_record WHERE id=?", this::mapRecord, recordId).stream().findFirst().orElseThrow();
  }

  public List<ExamRecord> findRecordsByUser(Long userId) {
    return jdbc.query("SELECT id, exam_id, user_id, score, passed, started_at, submitted_at FROM bp_exam_record WHERE user_id=? ORDER BY started_at DESC", this::mapRecord, userId);
  }

  private Exam mapExam(ResultSet rs, int n) throws SQLException {
    return new Exam(rs.getLong("id"), rs.getLong("tenant_id"), rs.getLong("course_id"),
        rs.getString("title"), rs.getString("description"), rs.getInt("duration_minutes"),
        rs.getInt("pass_score"), rs.getInt("total_score"), rs.getInt("status"),
        rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("updated_at").toLocalDateTime());
  }

  private Question mapQuestion(ResultSet rs, int n) throws SQLException {
    return new Question(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("type"),
        rs.getString("stem"), rs.getString("options"), rs.getString("answer"),
        rs.getString("explanation"), rs.getInt("score"), rs.getInt("status"),
        rs.getTimestamp("created_at").toLocalDateTime());
  }

  private ExamRecord mapRecord(ResultSet rs, int n) throws SQLException {
    var sub = rs.getTimestamp("submitted_at");
    return new ExamRecord(rs.getLong("id"), rs.getLong("exam_id"), rs.getLong("user_id"),
        rs.getInt("score"), rs.getInt("passed"), rs.getTimestamp("started_at").toLocalDateTime(),
        sub != null ? sub.toLocalDateTime() : null);
  }
}
