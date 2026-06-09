var api = require("../../utils/api.js");
var app = getApp();

Page({
  data: { courses: [], categories: [], activeCategory: "", loading: true, error: "" },
  onShow: function () { this.load(); },
  load: function () {
    var t = this;
    t.setData({ loading: true });
    app.ensureLogin().then(function () { return api.listCourses(); }).then(function (all) {
      var cats = [];
      var seen = {};
      all.forEach(function (c) { if (!seen[c.category]) { seen[c.category] = true; cats.push(c.category); } });
      t.setData({ courses: all, categories: cats, loading: false });
    }).catch(function (e) { t.setData({ loading: false, error: e.message }); });
  },
  filterCategory: function (e) {
    this.setData({ activeCategory: e.currentTarget.dataset.cat });
  },
  goDetail: function (e) { dd.navigateTo({ url: "/pages/course-detail/course-detail?id=" + e.currentTarget.dataset.id }); }
});
