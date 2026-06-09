var api = require("../../utils/api.js");
var app = getApp();

Page({
  data: {
    userInfo: null, dashboard: null, enrolled: [], paths: [], recommend: [],
    loading: true, error: "",
    notice: "《钉钉组织协同实战》新课程已上线，欢迎报名学习！",
    tasks: [{ label: "完成一门课程学习", done: false }, { label: "学习时长达到30分钟", done: false }, { label: "完成课后练习", done: false }],
    quickActions: [
      { label: "课程中心", icon: "📚", url: "/pages/courses/courses" },
      { label: "考试中心", icon: "📝", url: "/pages/exam-list/exam-list" },
      { label: "知识库", icon: "📖", url: "/pages/knowledge/knowledge" },
      { label: "学习地图", icon: "🗺️", url: "/pages/learning/learning" },
      { label: "学分中心", icon: "🎓", url: "/pages/credits/credits" },
      { label: "我的", icon: "👤", url: "/pages/my/my" }
    ]
  },
  onShow: function () { this.load(); },
  onPullDownRefresh: function () {
    var t = this;
    this.load().then(function () { dd.stopPullDownRefresh(); });
  },
  load: function () {
    var t = this;
    t.setData({ loading: true, error: "" });
    return app.ensureLogin().then(function (u) {
      t.setData({ userInfo: u });
      return Promise.all([api.getDashboard(), api.listCourses(), api.listPaths()]);
    }).then(function (r) {
      var dash = r[0], all = r[1], paths = r[2];
      var enrolledIds = {};
      (dash.courses || []).forEach(function (c) { enrolledIds[c.id] = true; });
      t.setData({
        dashboard: dash,
        enrolled: (dash.courses || []).slice(0, 3),
        paths: (paths || []).slice(0, 3),
        recommend: all.filter(function (c) { return !enrolledIds[c.id]; }).slice(0, 4),
        loading: false
      });
    }).catch(function (e) { t.setData({ error: e.message, loading: false }); });
  },
  goPath: function (e) { dd.navigateTo({ url: "/pages/path-detail/path-detail?id=" + e.currentTarget.dataset.id }); },
  goDetail: function (e) { dd.navigateTo({ url: "/pages/course-detail/course-detail?id=" + e.currentTarget.dataset.id }); },
  goAction: function (e) {
    var url = e.currentTarget.dataset.url;
    if (url.indexOf("/pages/courses") === 0 || url.indexOf("/pages/learning") === 0 || url.indexOf("/pages/my") === 0) {
      dd.switchTab({ url: url });
    } else {
      dd.navigateTo({ url: url });
    }
  },
  tapTask: function (e) {
    var i = e.currentTarget.dataset.idx;
    var tasks = this.data.tasks;
    tasks[i].done = !tasks[i].done;
    this.setData({ tasks: tasks });
  }
});
