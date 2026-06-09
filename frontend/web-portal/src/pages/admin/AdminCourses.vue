<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listCourses, type CourseSummary } from '../../../api'

const courses = ref<CourseSummary[]>([]); const loading = ref(true)
onMounted(async () => { courses.value = await listCourses(); loading.value = false })
</script>

<template>
  <div>
    <h1>课程管理</h1>
    <p class="sub">共 {{ courses.length }} 门课程</p>
    <table v-if="!loading">
      <thead><tr><th>封面</th><th>标题</th><th>分类</th><th>讲师</th><th>时长</th><th>价格</th><th>状态</th></tr></thead>
      <tbody><tr v-for="c in courses" :key="c.id">
        <td><img :src="c.coverUrl" class="thumb" /></td>
        <td><strong>{{ c.title }}</strong></td>
        <td>{{ c.category }}</td><td>{{ c.lecturer }}</td>
        <td>{{ c.durationMinutes }} min</td>
        <td>{{ Number(c.price)>0?'¥'+c.price:'免费' }}</td>
        <td><span class="status on">已上架</span></td>
      </tr></tbody>
    </table>
    <div v-else class="loading">加载中…</div>
  </div>
</template>

<style scoped>
h1 { font-size:22px; margin-bottom:4px; }
.sub { color:#667067; font-size:14px; margin-bottom:18px; }
table { width:100%; border-collapse:collapse; background:#fff; border:1px solid #dedbd0; border-radius:8px; overflow:hidden; }
th, td { padding:12px 14px; text-align:left; border-bottom:1px solid #f0ede4; font-size:14px; }
th { background:#f8f7f2; color:#667067; font-weight:600; }
.thumb { width:60px; height:36px; border-radius:4px; object-fit:cover; }
.status { padding:3px 10px; border-radius:12px; font-size:12px; font-weight:600; }
.status.on { background:#e8f0ec; color:#176b52; }
.loading { text-align:center; padding:40px; color:#667067; }
</style>
