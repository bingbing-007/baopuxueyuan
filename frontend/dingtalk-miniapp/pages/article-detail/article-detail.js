var api = require("../../utils/api.js");
Page({
  data: { article: null },
  onLoad: function (opts) { api.getKnowledgeDetail(parseInt(opts.id)).then(function (a) { this.setData({ article: a }); }.bind(this)); }
});
