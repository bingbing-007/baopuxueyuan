var api = require("../../utils/api.js");
var auth = require("../../utils/auth.js");
var app = getApp();

Page({
  data: { userInfo: null, creditAccount: null, examRecords: [], loading: true },
  onShow: function () { this.load(); },
  load: function () {
    var t = this;
    t.setData({ loading: true });
    app.ensureLogin().then(function (u) {
      t.setData({ userInfo: u });
      return Promise.all([api.getCreditAccount(), api.getExamRecords()]);
    }).then(function (r) {
      t.setData({ creditAccount: r[0], examRecords: r[1] || [], loading: false });
    }).catch(function () { t.setData({ loading: false }); });
  },
  logout: function () {
    auth.logout();
    app.globalData.userId = null;
    app.globalData.token = null;
    app.globalData.userInfo = null;
    dd.switchTab({ url: "/pages/index/index" });
  }
});
