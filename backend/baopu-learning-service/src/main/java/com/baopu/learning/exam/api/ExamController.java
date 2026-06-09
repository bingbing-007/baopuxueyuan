package com.baopu.learning.exam.api;

import com.baopu.learning.exam.model.Exam;
import com.baopu.learning.exam.model.ExamRecord;
import com.baopu.learning.exam.service.ExamService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
  private final ExamService examService;

  public ExamController(ExamService examService) { this.examService = examService; }

  @GetMapping
  List<Exam> list() { return examService.listExams(); }

  @GetMapping("/{examId}")
  Exam get(@PathVariable Long examId) { return examService.getExam(examId); }

  @PostMapping("/{examId}/start")
  Map<String, Object> start(HttpServletRequest req, @PathVariable Long examId) {
    return examService.startExam(examId, getUserId(req));
  }

  @PostMapping("/records/{recordId}/answer")
  Map<String, Object> answer(HttpServletRequest req, @PathVariable Long recordId, @RequestBody Map<String, String> body) {
    return examService.submitAnswer(recordId, Long.parseLong(body.get("questionId")), getUserId(req), body.get("userAnswer"));
  }

  @PostMapping("/records/{recordId}/finish")
  ExamRecord finish(HttpServletRequest req, @PathVariable Long recordId) {
    return examService.finishExam(recordId, getUserId(req));
  }

  @GetMapping("/my-records")
  List<ExamRecord> myRecords(HttpServletRequest req) { return examService.myRecords(getUserId(req)); }

  private Long getUserId(HttpServletRequest req) {
    return (Long) req.getAttribute("userId");
  }
}
