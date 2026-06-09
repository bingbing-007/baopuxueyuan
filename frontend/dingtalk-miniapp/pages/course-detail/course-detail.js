var api = require("../../utils/api.js");
var app = getApp();

Page({
  data: { course: null, enrolled: false, progress: 0, loading: true, error: "" },
  onLoad: function (opts) {
    var t = this;
    this.courseId = parseInt(opts.id);
    app.ensureLogin().then(function () {
      return api.getCourseDetail(t.courseId);
    }).then(function (c) {
      t.setData({ course: c, enrolled: c.enrolled, progress: c.progressPercent, loading: false });
    }).catch(function (e) { t.setData({ loading: false, error: e.message }); });
  },
  enroll: function () {
    var t = this;
    api.enrollCourse(t.courseId).then(function (c) {
      t.setData({ enrolled: true, progress: 0, course: c });
      dd.showToast({ content: "报名成功", type: "success" });
    }).catch(function (e) { dd.showToast({ content: e.message, type: "fail" }); });
  },
  addProgress: function () {
    var t = this;
    var next = Math.min(100, t.data.progress + 25);
    api.updateProgress(t.courseId, next).then(function (c) {
      t.setData({ progress: c.progressPercent, course: c });
      if (next >= 100) dd.showToast({ content: "课程完成！", type: "success" });
    });
  }
});
