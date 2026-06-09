var api = require("../../utils/api.js");
var app = getApp();
Page({
  data: { exam: null, currentQ: 0, questions: [], recordId: null, answers: {}, submitted: false, score: 0, passed: false },
  onLoad: function (opts) {
    var t = this;
    t.examId = parseInt(opts.id);
    app.ensureLogin().then(function () { return api.startExam(t.examId); }).then(function (r) {
      t.setData({ exam: r.exam, questions: r.questions, recordId: r.record.id, currentQ: 0 });
    });
  },
  selectAnswer: function (e) {
    var q = this.data.questions[this.data.currentQ];
    var answer = e.currentTarget.dataset.answer;
    var answers = this.data.answers;
    answers[q.id] = answer;
    this.setData({ answers: answers });
    api.submitAnswer(this.data.recordId, q.id, answer);
  },
  next: function () {
    if (this.data.currentQ < this.data.questions.length - 1) this.setData({ currentQ: this.data.currentQ + 1 });
  },
  prev: function () { if (this.data.currentQ > 0) this.setData({ currentQ: this.data.currentQ - 1 }); },
  finish: function () {
    var t = this;
    api.finishExam(t.data.recordId).then(function (r) { t.setData({ submitted: true, score: r.score, passed: r.passed === 1 }); });
  }
});
