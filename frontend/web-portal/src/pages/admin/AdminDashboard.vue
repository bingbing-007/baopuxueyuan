<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getToken } from '../../api'

const overview = ref<any>({})
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await fetch('/api/analytics/overview', {
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    overview.value = await res.json()
  } finally { loading.value = false }
})
</script>

<template>
  <div>
    <h1>仪表盘</h1>
    <div class="stat-cards">
      <div class="stat-card"><strong>{{ overview.totalUsers ?? '-' }}</strong><span>总用户</span></div>
      <div class="stat-card"><strong>{{ overview.totalCourses ?? '-' }}</strong><span>课程数</span></div>
      <div class="stat-card"><strong>{{ overview.totalEnrollments ?? '-' }}</strong><span>总报名</span></div>
      <div class="stat-card"><strong>{{ overview.completionRate ?? '-' }}%</strong><span>完成率</span></div>
    </div>
  </div>
</template>

<style scoped>
h1 { font-size: 24px; margin-bottom: 20px; }
.stat-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); gap: 16px; }
.stat-card { padding: 20px; background: #fff; border-radius: 8px; border: 1px solid #dedbd0; }
.stat-card strong { display: block; font-size: 28px; color: #176b52; }
.stat-card span { color: #667067; font-size: 13px; }
</style>
