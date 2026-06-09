var api = require("./utils/api");
var auth = require("./utils/auth");

App({
  globalData: { userInfo: null, userId: null, apiBase: "http://10.0.2.2:8080" },
  onLaunch: function () {
    try {
      var c = dd.getStorageSync({ key: "session" });
      if (c && c.data && c.data.userId) {
        this.globalData.userInfo = c.data;
        this.globalData.userId = c.data.userId;
      }
    } catch (e) {}
  },
  ensureLogin: function () {
    var t = this;
    if (t.globalData.userId) return Promise.resolve(t.globalData.userInfo);
    return auth.login().then(function (u) {
      t.globalData.userInfo = u;
      t.globalData.userId = u.userId;
      return u;
    });
  }
});