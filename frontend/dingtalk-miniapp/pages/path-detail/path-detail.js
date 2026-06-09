var api = require("../../utils/api.js");
var app = getApp();
Page({
  data: { path: null, stages: [], enrolled: false, loading: true },
  onLoad: function (opts) {
    var t = this;
    t.pathId = parseInt(opts.id);
    app.ensureLogin().then(function () { return api.getPathDetail(t.pathId); }).then(function (r) {
      t.setData({ path: r.path, stages: r.stages, enrolled: r.enrolled, loading: false });
    });
  },
  enroll: function () {
    var t = this;
    api.enrollPath(t.pathId).then(function () { t.setData({ enrolled: true }); dd.showToast({ content: "已加入学习路径", type: "success" }); });
  }
});
