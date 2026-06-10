var api = require("./utils/api");
var auth = require("./utils/auth");

App({
  globalData: { userInfo: null, userId: null, token: null, apiBase: "http://10.0.2.2:8000" },
  onLaunch: function () {
    try {
      var c = dd.getStorageSync({ key: "session" });
      if (c && c.data && c.data.token) {
        this.globalData.userInfo = c.data;
        this.globalData.userId = c.data.userId;
        this.globalData.token = c.data.token;
      }
    } catch (e) {}
  },
  ensureLogin: function () {
    var t = this;
    if (t.globalData.token) return Promise.resolve(t.globalData.userInfo);
    return auth.login().then(function (u) {
      t.globalData.userInfo = u;
      t.globalData.userId = u.userId;
      t.globalData.token = u.token;
      return u;
    });
  }
});

