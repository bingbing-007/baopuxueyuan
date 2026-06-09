<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Users, BookOpen, CheckCircle, TrendingUp, BarChart3 } from 'lucide-vue-next'
import { getAnalyticsOverview, getTopCourses, getTopLearners, getMonthlyTrend } from '../../../api'

const overview = ref<any>({})
const topCourses = ref<any[]>([])
const topLearners = ref<any[]>([])
const trend = ref<any[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    [overview.value, topCourses.value, topLearners.value, trend.value] = await Promise.all([
      getAnalyticsOverview(), getTopCourses(5), getTopLearners(5), getMonthlyTrend()
    ])
  } finally { loading.value = false }
})

const statCards = [
  { key: 'totalUsers', label: '总用户', icon: Users, color: '#176b52' },
  { key: 'totalCourses', label: '课程数', icon: BookOpen, color: '#2a9d6e' },
  { key: 'totalEnrollments', label: '总报名', icon: TrendingUp, color: '#6b4ce6' },
  { key: 'completionRate', label: '完成率(%)', icon: CheckCircle, color: '#e67e22' },
]
</script>

<template>
  <div>
    <h1>数据仪表盘</h1>
    <div v-if="loading" class="loading">加载中…</div>
    <template v-else>
      <div class="stat-cards">
        <div v-for="s in statCards" :key="s.key" class="stat-card" :style="{ borderTopColor: s.color }">
          <div class="stat-icon"><component :is="s.icon" :size="22" /></div>
          <strong>{{ overview[s.key] ?? '-' }}</strong>
          <span>{{ s.label }}</span>
        </div>
      </div>

      <div class="grid-2">
        <div class="card">
          <h2><BarChart3 :size="18" /> 热门课程 TOP5</h2>
          <table><thead><tr><th>课程</th><th>报名</th><th>完成</th><th>进度</th></tr></thead>
            <tbody><tr v-for="c in topCourses" :key="c.id">
              <td>{{ c.title }}</td><td>{{ c.enrollment_count }}</td><td>{{ c.completed_count }}</td><td>{{ c.avg_progress }}%</td>
            </tr></tbody></table>
        </div>
        <div class="card">
          <h2>学习标兵 TOP5</h2>
          <table><thead><tr><th>姓名</th><th>报名</th><th>完成</th><th>进度</th></tr></thead>
            <tbody><tr v-for="u in topLearners" :key="u.id">
              <td>{{ u.name }}</td><td>{{ u.enrolled_count }}</td><td>{{ u.completed_count }}</td><td>{{ u.avg_progress }}%</td>
            </tr></tbody></table>
        </div>
      </div>

      <div class="card">
        <h2>月度趋势</h2>
        <div class="trend-bars">
          <div v-for="t in trend" :key="t.month" class="trend-bar">
            <div class="bar-fill" :style="{ height: Math.max(4, (t.enrollments||0) * 3) + 'px' }"></div>
            <span>{{ t.month?.slice(5) }}月</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
h1 { font-size:22px; margin-bottom:20px; }
h2 { font-size:16px; margin:0 0 14px; display:flex; align-items:center; gap:8px; }
.stat-cards { display:grid; grid-template-columns:repeat(auto-fit,minmax(150px,1fr)); gap:14px; margin-bottom:24px; }
.stat-card { padding:18px; background:#fff; border-radius:10px; border:1px solid #dedbd0; border-top:3px solid; }
.stat-icon { margin-bottom:8px; opacity:.7; }
.stat-card strong { display:block; font-size:26px; }
.stat-card span { font-size:13px; color:#667067; }
.grid-2 { display:grid; grid-template-columns:repeat(auto-fit,minmax(320px,1fr)); gap:16px; margin-bottom:20px; }
.card { background:#fff; border:1px solid #dedbd0; border-radius:10px; padding:20px; }
table { width:100%; border-collapse:collapse; font-size:13px; }
th, td { padding:8px 10px; text-align:left; border-bottom:1px solid #f0ede4; }
th { color:#667067; font-weight:600; }
.trend-bars { display:flex; align-items:flex-end; gap:16px; height:120px; padding-top:12px; }
.trend-bar { flex:1; display:flex; flex-direction:column; align-items:center; gap:6px; }
.bar-fill { width:24px; background:linear-gradient(0deg,#176b52,#2a9d6e); border-radius:4px 4px 0 0; min-height:4px; }
.trend-bar span { font-size:11px; color:#667067; }
.loading { text-align:center; padding:40px; color:#667067; }
</style>
