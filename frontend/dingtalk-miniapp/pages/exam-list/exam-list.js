var api = require("../../utils/api.js");
var app = getApp();
Page({
  data: { exams: [], loading: true },
  onShow: function () {
    var t = this;
    app.ensureLogin().then(function () { return api.listExams(); }).then(function (exams) { t.setData({ exams: exams, loading: false }); });
  },
  start: function (e) { dd.navigateTo({ url: "/pages/exam-take/exam-take?id=" + e.currentTarget.dataset.id }); }
});
