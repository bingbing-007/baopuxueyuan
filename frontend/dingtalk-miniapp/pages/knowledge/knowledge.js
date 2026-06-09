var api = require("../../utils/api.js");
Page({
  data: { articles: [], keyword: "", page: 1, loading: true, hotTags: [] },
  onShow: function () { this.search(); this.loadHotTags(); },
  search: function () {
    var t = this;
    t.setData({ loading: true });
    api.searchKnowledge(t.data.keyword).then(function (list) { t.setData({ articles: list, loading: false }); });
  },
  onSearch: function (e) { this.setData({ keyword: e.detail.value }); },
  loadHotTags: function () { api.hotKnowledgeTags().then(function (t) { this.setData({ hotTags: t }); }.bind(this)); },
  tapTag: function (e) { this.setData({ keyword: e.currentTarget.dataset.tag }); this.search(); },
  goDetail: function (e) { dd.navigateTo({ url: "/pages/article-detail/article-detail?id=" + e.currentTarget.dataset.id }); }
});
