var api = require("../../utils/api.js");
Page({
  data: { courses:[], filtered:[], categories:[], activeCat:"", searchText:"", sortBy:"", loading:true, error:"" },
  onShow:function(){this.load()},
  onPullDownRefresh:function(){var t=this;this.load().then(function(){dd.stopPullDownRefresh()})},
  load:function(){var t=this;t.setData({loading:true,error:""});return api.listCourses().then(function(c){var cats=[];c.forEach(function(x){if(cats.indexOf(x.category)<0)cats.push(x.category)});t.setData({courses:c,categories:cats,loading:false});t.filter()}).catch(function(e){t.setData({error:e.message,loading:false})})},
  filter:function(){var c=this.data.courses.slice(),a=this.data.activeCat,q=this.data.searchText.toLowerCase(),s=this.data.sortBy;if(a)c=c.filter(function(x){return x.category===a});if(q)c=c.filter(function(x){return x.title.toLowerCase().indexOf(q)>=0||x.lecturer.toLowerCase().indexOf(q)>=0});if(s==="dur")c.sort(function(a,b){return a.durationMinutes-b.durationMinutes});if(s==="price")c.sort(function(a,b){return a.price-b.price});this.setData({filtered:c})},
  tapCat:function(e){var c=e.currentTarget.dataset.cat;this.setData({activeCat:this.data.activeCat===c?"":c});this.filter()},
  tapSort:function(e){var s=e.currentTarget.dataset.sort;this.setData({sortBy:this.data.sortBy===s?"":s});this.filter()},
  onInput:function(e){this.setData({searchText:e.detail.value});this.filter()},
  goDetail:function(e){dd.navigateTo({url:"/pages/course-detail/course-detail?id="+e.currentTarget.dataset.id})}
});