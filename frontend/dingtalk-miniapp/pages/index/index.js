var api = require("../../utils/api.js");
var app = getApp();

Page({
  data: {
    userInfo: null, dashboard: null, enrolled: [], recommend: [], loading: true, error: "",
    notice: "《钉钉组织协同实战》新课程已上线，欢迎报名学习！",
    tasks: [
      { label: "完成一门课程学习", done: false },
      { label: "学习时长达到 30 分钟", done: false },
      { label: "完成课后练习", done: false }
    ],
    quickActions: [
      { label: "考试中心", idx: 1 },
      { label: "学习证书", idx: 2 },
      { label: "学习排行", idx: 3 },
      { label: "学习任务", idx: 4 },
      { label: "学习地图", idx: 5 },
      { label: "数据报告", idx: 6 }
    ]
  },
  onShow: function () { this.load(); },
  onPullDownRefresh: function () { var t = this; this.load().then(function () { dd.stopPullDownRefresh(); }); },
  load: function () {
    var t = this;
    t.setData({ loading: true, error: "" });
    return app.ensureLogin().then(function (u) {
      t.setData({ userInfo: u });
      return Promise.all([api.getDashboard(), api.listCourses()]);
    }).then(function (r) {
      var dash = r[0], all = r[1], ids = {};
      (dash.courses || []).forEach(function (c) { ids[c.id] = true; });
      t.setData({
        dashboard: dash,
        enrolled: (dash.courses || []).slice(0, 4),
        recommend: all.filter(function (c) { return !ids[c.id]; }).slice(0, 4),
        loading: false
      });
    }).catch(function (e) { t.setData({ error: e.message, loading: false }); });
  },
  goDetail: function (e) { dd.navigateTo({ url: "/pages/course-detail/course-detail?id=" + e.currentTarget.dataset.id }); },
  goSearch: function () { dd.navigateTo({ url: "/pages/search/search" }); },
  goCourses: function () { dd.switchTab({ url: "/pages/courses/courses" }); },
  tapTask: function (e) {
    var i = e.currentTarget.dataset.idx;
    var tasks = this.data.tasks;
    tasks[i].done = !tasks[i].done;
    this.setData({ tasks: tasks });
  }
});