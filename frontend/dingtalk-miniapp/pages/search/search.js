var api=require("../../utils/api.js");
Page({data:{text:"",courses:[],results:[],history:[],searching:false,hotTags:["钉钉协同","新员工","管理能力","客户服务","数字化"]},
onShow:function(){this.loadHist();var t=this;api.listCourses().then(function(c){t.setData({courses:c})}).catch(function(){})},
loadHist:function(){try{var h=dd.getStorageSync({key:"sh"});this.setData({history:h&&h.data||[]})}catch(e){}},
go:function(){var t=this,q=t.data.text.trim();if(!q)return;var h=t.data.history.filter(function(x){return x!==q});h.unshift(q);if(h.length>10)h.pop();dd.setStorageSync({key:"sh",data:h});t.setData({history:h});var lq=q.toLowerCase(),r=t.data.courses.filter(function(c){return c.title.toLowerCase().indexOf(lq)>=0||c.lecturer.toLowerCase().indexOf(lq)>=0||c.category.toLowerCase().indexOf(lq)>=0});t.setData({results:r,searching:true})},
onInput:function(e){this.setData({text:e.detail.value,searching:false})},
onConfirm:function(){this.go()},
tapHist:function(e){this.setData({text:e.currentTarget.dataset.t});this.go()},
tapHot:function(e){this.setData({text:e.currentTarget.dataset.t});this.go()},
clearHist:function(){dd.removeStorageSync({key:"sh"});this.setData({history:[]})},
goDetail:function(e){dd.navigateTo({url:"/pages/course-detail/course-detail?id="+e.currentTarget.dataset.id})}
});