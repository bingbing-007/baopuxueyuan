var api = require("../../utils/api.js");
var app = getApp();
Page({
  data: { account: null, records: [], loading: true },
  onShow: function () {
    var t = this;
    app.ensureLogin().then(function () {
      return Promise.all([api.getCreditAccount(), api.getCreditRecords()]);
    }).then(function (r) { t.setData({ account: r[0], records: r[1], loading: false }); });
  }
});
