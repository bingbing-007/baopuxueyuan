var app = getApp();

function get(p) {
  return new Promise(function (ok, no) {
    var h = { "Content-Type": "application/json" };
    if (app.globalData.token) h["Authorization"] = "Bearer " + app.globalData.token;
    dd.httpRequest({
      url: app.globalData.apiBase + p, method: "GET", headers: h, dataType: "json", timeout: 15000,
      success: function (r) { r.status >= 200 && r.status < 300 ? ok(r.data) : no(new Error("请求失败")); },
      fail: function () { no(new Error("网络异常")); }
    });
  });
}
function post(p, d) {
  return new Promise(function (ok, no) {
    var h = { "Content-Type": "application/json" };
    if (app.globalData.token) h["Authorization"] = "Bearer " + app.globalData.token;
    dd.httpRequest({
      url: app.globalData.apiBase + p, method: "POST", data: d, headers: h, dataType: "json", timeout: 15000,
      success: function (r) { r.status >= 200 && r.status < 300 ? ok(r.data) : no(new Error("请求失败")); },
      fail: function () { no(new Error("网络异常")); }
    });
  });
}
function put(p, d) {
  return new Promise(function (ok, no) {
    var h = { "Content-Type": "application/json" };
    if (app.globalData.token) h["Authorization"] = "Bearer " + app.globalData.token;
    dd.httpRequest({
      url: app.globalData.apiBase + p, method: "PUT", data: d, headers: h, dataType: "json", timeout: 15000,
      success: function (r) { r.status >= 200 && r.status < 300 ? ok(r.data) : no(new Error("请求失败")); },
      fail: function () { no(new Error("网络异常")); }
    });
  });
}

module.exports = {
  login: function (d) { return post("/api/auth/dingtalk/login", d); },
  listCourses: function () { return get("/api/courses"); },
  getDashboard: function () { return get("/api/me/dashboard"); },
  enrollCourse: function (id) { return post("/api/courses/" + id + "/enroll"); },
  updateProgress: function (id, pct) { return put("/api/courses/" + id + "/progress", { progressPercent: pct }); }
};
