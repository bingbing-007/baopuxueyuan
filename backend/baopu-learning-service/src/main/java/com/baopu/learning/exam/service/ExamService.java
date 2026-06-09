package com.baopu.learning.exam.service;

import com.baopu.learning.exam.model.Exam;
import com.baopu.learning.exam.model.ExamRecord;
import com.baopu.learning.exam.model.Question;
import com.baopu.learning.exam.repository.ExamRepository;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExamService {
  private final ExamRepository examRepo;

  public ExamService(ExamRepository examRepo) { this.examRepo = examRepo; }

  public List<Exam> listExams() { return examRepo.findPublished(); }

  public Exam getExam(Long examId) {
    return examRepo.findById(examId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));
  }

  public Map<String, Object> startExam(Long examId, Long userId) {
    Exam exam = getExam(examId);
    List<Question> questions = examRepo.findQuestionsByExam(examId);
    ExamRecord record = examRepo.startExam(examId, userId);
    return Map.of("record", record, "exam", exam, "questions", questions);
  }

  public Map<String, Object> submitAnswer(Long recordId, Long questionId, Long userId, String userAnswer) {
    Question question = examRepo.findQuestionsByExam(1L).stream()
        .filter(q -> q.id().equals(questionId)).findFirst().orElse(null);
    if (question == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found");
    int correct = question.answer().equals(userAnswer) ? 1 : 0;
    examRepo.submitAnswer(recordId, questionId, userAnswer, correct);
    return Map.of("correct", correct, "explanation", question.explanation());
  }

  public ExamRecord finishExam(Long recordId, Long userId) {
    var questions = examRepo.findQuestionsByExam(
        examRepo.findPublished().stream().findFirst().orElseThrow().id());
    int totalScore = 0; // simplified: would count from answers
    return examRepo.finishExam(recordId, totalScore, 60);
  }

  public List<ExamRecord> myRecords(Long userId) { return examRepo.findRecordsByUser(userId); }
}
