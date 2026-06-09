<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getAnalyticsOverview, getTopCourses, getTopLearners, getMonthlyTrend } from '../../../api'

const overview = ref<any>({})
const topCourses = ref<any[]>([])
const topLearners = ref<any[]>([])
const trend = ref<any[]>([])
const loading = ref(true)

onMounted(async () => {
  [overview.value, topCourses.value, topLearners.value, trend.value] = await Promise.all([
    getAnalyticsOverview(), getTopCourses(20), getTopLearners(20), getMonthlyTrend()
  ])
  loading.value = false
})
</script>

<template>
  <div>
    <h1>数据分析</h1>
    <div v-if="loading" class="loading">加载中…</div>
    <template v-else>
      <div class="overview-grid">
        <div class="ov-item"><strong>{{ overview.totalUsers }}</strong><span>总用户</span></div>
        <div class="ov-item"><strong>{{ overview.totalCourses }}</strong><span>总课程</span></div>
        <div class="ov-item"><strong>{{ overview.totalEnrollments }}</strong><span>总报名</span></div>
        <div class="ov-item"><strong>{{ overview.completedEnrollments }}</strong><span>已完成</span></div>
        <div class="ov-item"><strong>{{ overview.completionRate }}%</strong><span>完成率</span></div>
        <div class="ov-item"><strong>{{ overview.averageProgress }}%</strong><span>平均进度</span></div>
        <div class="ov-item"><strong>{{ overview.totalExams }}</strong><span>考试数</span></div>
        <div class="ov-item"><strong>{{ overview.examPassed }}</strong><span>考试通过</span></div>
      </div>

      <h2>月度趋势</h2>
      <div class="trend-bars">
        <div v-for="t in trend" :key="t.month" class="t-bar">
          <div class="t-enroll" :style="{ height: Math.max(2,(t.enrollments||0)*4)+'px' }"></div>
          <div class="t-complete" :style="{ height: Math.max(2,(t.completions||0)*4)+'px' }"></div>
          <span>{{ t.month?.slice(5) }}月</span>
        </div>
      </div>

      <div class="grid-2">
        <div class="card"><h2>课程排名</h2>
          <div v-for="c in topCourses.slice(0,10)" :key="c.id" class="rank-row">
            <span class="rank-title">{{ c.title }}</span><span>{{ c.enrollment_count }} 报名 · {{ c.avg_progress }}%</span>
          </div>
        </div>
        <div class="card"><h2>学员排名</h2>
          <div v-for="u in topLearners.slice(0,10)" :key="u.id" class="rank-row">
            <span class="rank-title">{{ u.name }}</span><span>{{ u.completed_count }} 完成 · {{ u.avg_progress }}%</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
h1 { font-size:22px; margin-bottom:20px; }
h2 { font-size:17px; margin:20px 0 12px; }
.overview-grid { display:grid; grid-template-columns:repeat(4,1fr); gap:12px; }
.ov-item { background:#fff; border:1px solid #dedbd0; border-radius:8px; padding:16px; text-align:center; }
.ov-item strong { display:block; font-size:24px; }
.ov-item span { font-size:12px; color:#667067; }
.trend-bars { display:flex; align-items:flex-end; gap:8px; height:100px; }
.t-bar { flex:1; display:flex; flex-direction:column; align-items:center; gap:2px; }
.t-enroll { width:14px; background:#176b52; border-radius:3px 3px 0 0; min-height:2px; }
.t-complete { width:14px; background:#2a9d6e; border-radius:3px 3px 0 0; min-height:2px; }
.t-bar span { font-size:10px; color:#667067; margin-top:4px; }
.grid-2 { display:grid; grid-template-columns:repeat(auto-fit,minmax(320px,1fr)); gap:16px; }
.card { background:#fff; border:1px solid #dedbd0; border-radius:10px; padding:18px; }
.rank-row { display:flex; justify-content:space-between; padding:8px 0; border-bottom:1px solid #f0ede4; font-size:14px; }
.rank-title { font-weight:600; }
.loading { text-align:center; padding:40px; color:#667067; }
</style>
