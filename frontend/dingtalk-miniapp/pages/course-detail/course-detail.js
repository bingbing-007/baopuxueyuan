var api=require("../../utils/api.js");var app=getApp();
Page({data:{cid:null,course:null,loading:true,error:"",enrolling:false,chapters:[{id:1,title:"课程导论与目标",dur:"8分钟",done:true},{id:2,title:"核心概念与框架",dur:"15分钟",done:true},{id:3,title:"实战案例分析",dur:"20分钟",done:false},{id:4,title:"团队协作演练",dur:"12分钟",done:false},{id:5,title:"总结与考核",dur:"10分钟",done:false}]},
onLoad:function(o){if(o.id){this.setData({cid:Number(o.id)});this.load()}},
onShow:function(){if(this.data.cid)this.load()},
load:function(){var t=this;t.setData({loading:true,error:""});api.listCourses().then(function(c){for(var i=0;i<c.length;i++)if(c[i].id===t.data.cid){t.setData({course:c[i],loading:false});dd.setNavigationBar({title:c[i].title});return}t.setData({error:"课程不存在",loading:false})}).catch(function(e){t.setData({error:e.message,loading:false})})},
enroll:function(){var t=this;if(!app.globalData.userId){dd.showToast({content:"请先登录",type:"fail"});return}t.setData({enrolling:true});api.enrollCourse(t.data.cid).then(function(){dd.showToast({content:"报名成功",type:"success"});t.load()}).catch(function(e){dd.showToast({content:e.message,type:"fail"})}).finally(function(){t.setData({enrolling:false})})},
startLearn:function(){dd.navigateTo({url:"/pages/learning/learning?id="+this.data.cid})},
addPct:function(e){var t=this,s=Number(e.currentTarget.dataset.step)||25,n=Math.min(100,(t.data.course.progressPercent||0)+s);api.updateProgress(t.data.cid,n).then(function(){t.load()}).catch(function(e){dd.showToast({content:e.message,type:"fail"})})}
});