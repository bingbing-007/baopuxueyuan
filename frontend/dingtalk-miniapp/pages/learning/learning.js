var api = require("../../utils/api.js");
var app = getApp();

Page({
  data: { dashboard: null, paths: [], loading: true, error: "" },
  onShow: function () { this.load(); },
  load: function () {
    var t = this;
    t.setData({ loading: true });
    app.ensureLogin().then(function () {
      return Promise.all([api.getDashboard(), api.listPaths()]);
    }).then(function (r) {
      t.setData({ dashboard: r[0], paths: r[1] || [], loading: false });
    }).catch(function (e) { t.setData({ loading: false, error: e.message }); });
  },
  goDetail: function (e) { dd.navigateTo({ url: "/pages/course-detail/course-detail?id=" + e.currentTarget.dataset.id }); },
  goPath: function (e) { dd.navigateTo({ url: "/pages/path-detail/path-detail?id=" + e.currentTarget.dataset.id }); }
});
