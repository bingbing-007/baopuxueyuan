var api=require("../../utils/api.js");
Page({data:{cid:null,course:null,loading:true,activeChap:1,chapters:[{id:1,title:"课程导论与目标",dur:"8分钟",done:true},{id:2,title:"核心概念与框架",dur:"15分钟",done:true},{id:3,title:"实战案例分析",dur:"20分钟",done:false},{id:4,title:"团队协作演练",dur:"12分钟",done:false},{id:5,title:"总结与考核",dur:"10分钟",done:false}]},
onLoad:function(o){if(o.id){this.setData({cid:Number(o.id)});this.load()}},
load:function(){var t=this;api.listCourses().then(function(c){for(var i=0;i<c.length;i++)if(c[i].id===t.data.cid){t.setData({course:c[i],loading:false});dd.setNavigationBar({title:c[i].title});return}t.setData({loading:false})}).catch(function(){t.setData({loading:false})})},
tapChap:function(e){this.setData({activeChap:Number(e.currentTarget.dataset.id)})},
addPct:function(){var t=this,n=Math.min(100,(t.data.course.progressPercent||0)+20);api.updateProgress(t.data.cid,n).then(function(){t.load();dd.showToast({content:"进度已更新",type:"success"})})}
});