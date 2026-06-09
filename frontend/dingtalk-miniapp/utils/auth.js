var api = require("./api.js");
module.exports = {
  login: function () {
    return new Promise(function (ok, no) {
      dd.getAuthCode({
        success: function (r) {
          api.login({ authCode: r.authCode })
            .then(function (u) {
              dd.setStorageSync({ key: "session", data: u });
              ok(u);
            })
            .catch(no);
        },
        fail: function () { no(new Error("授权失败")); }
      });
    });
  },
  logout: function () { try { dd.removeStorageSync({ key: "session" }); } catch (e) {} }
};
