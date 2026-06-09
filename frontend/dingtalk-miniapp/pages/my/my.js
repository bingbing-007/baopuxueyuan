var api=require("../../utils/api.js");var auth=require("../../utils/auth.js");var app=getApp();
Page({data:{userInfo:null,dashboard:null,loading:true,weekDays:["一","二","三","四","五","六","日"],weekStudied:[true,true,false,true,true,false,false]},
onShow:function(){var t=this;t.setData({loading:true});app.ensureLogin().then(function(u){t.setData({userInfo:u});return api.getDashboard()}).then(function(d){t.setData({dashboard:d,loading:false})}).catch(function(e){dd.showToast({content:e.message,type:"fail"});t.setData({loading:false})})},
logout:function(){dd.confirm({title:"退出登录",content:"确定退出当前账号吗？",confirmButtonText:"退出",cancelButtonText:"取消",success:function(r){if(r.confirm){auth.logout();app.globalData.userInfo=null;app.globalData.userId=null;dd.reLaunch({url:"/pages/index/index"})}}})},
goDetail:function(e){dd.navigateTo({url:"/pages/course-detail/course-detail?id="+e.currentTarget.dataset.id})}
});