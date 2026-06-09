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
  getCourseDetail: function (id) { return get("/api/courses/" + id); },
  getDashboard: function () { return get("/api/me/dashboard"); },
  enrollCourse: function (id) { return post("/api/courses/" + id + "/enroll"); },
  updateProgress: function (id, pct) { return put("/api/courses/" + id + "/progress", { progressPercent: pct }); },
  listPaths: function () { return get("/api/paths"); },
  getPathDetail: function (id) { return get("/api/paths/" + id); },
  enrollPath: function (id) { return post("/api/paths/" + id + "/enroll"); },
  listExams: function () { return get("/api/exams"); },
  getExam: function (id) { return get("/api/exams/" + id); },
  startExam: function (id) { return post("/api/exams/" + id + "/start"); },
  submitAnswer: function (recId, qid, ans) { return post("/api/exams/records/" + recId + "/answer", { questionId: String(qid), userAnswer: ans }); },
  finishExam: function (recId) { return post("/api/exams/records/" + recId + "/finish"); },
  getExamRecords: function () { return get("/api/exams/my-records"); },
  getCreditAccount: function () { return get("/api/credits/my-account"); },
  getCreditRecords: function () { return get("/api/credits/my-records"); },
  searchKnowledge: function (kw) { return get("/api/knowledge" + (kw ? "?keyword=" + encodeURIComponent(kw) : "")); },
  getKnowledgeDetail: function (id) { return get("/api/knowledge/" + id); },
  hotKnowledgeTags: function () { return get("/api/knowledge/tags/hot"); }
};
