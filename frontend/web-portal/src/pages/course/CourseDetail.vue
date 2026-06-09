<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, BookOpen, CheckCircle2, Clock3, PlayCircle, Trophy } from 'lucide-vue-next'
import { enrollCourse, getCourseDetail, updateProgress, type CourseSummary } from '../../api'

const route = useRoute(); const router = useRouter()
const course = ref<CourseSummary | null>(null); const loading = ref(true); const error = ref('')

onMounted(async () => {
  try { course.value = await getCourseDetail(Number(route.params.id)) }
  catch (e: any) { error.value = e.message }
  finally { loading.value = false }
})

async function enroll() {
  if (!course.value) return
  await enrollCourse(course.value.id); course.value = await getCourseDetail(course.value.id)
}

async function learn(step: number) {
  if (!course.value) return
  const next = Math.min(100, course.value.progressPercent + step)
  await updateProgress(course.value.id, next); course.value = await getCourseDetail(course.value.id)
}
</script>

<template>
  <div class="page" v-if="course">
    <button class="back-btn" @click="router.back()"><ArrowLeft :size="20" /> 返回</button>
    <img class="hero" :src="course.coverUrl" :alt="course.title" />

    <div class="meta-row">
      <span class="badge">{{ course.category }}</span>
      <span><Clock3 :size="15" /> {{ course.durationMinutes }} 分钟</span>
      <span><BookOpen :size="15" /> {{ course.lecturer }}</span>
    </div>

    <h1>{{ course.title }}</h1>
    <p class="desc">{{ course.description }}</p>

    <div v-if="course.enrolled" class="progress-section">
      <div class="progress-header"><span>学习进度</span><strong>{{ course.progressPercent }}%</strong></div>
      <div class="progress-bar"><div class="progress-fill" :style="{ width: course.progressPercent + '%' }"></div></div>
      <div class="learn-actions" v-if="course.progressPercent < 100">
        <button class="btn-primary" @click="learn(25)"><PlayCircle :size="18" /> 继续学习 25%</button>
        <button class="btn-outline" @click="learn(50)">快速学习 50%</button>
        <button class="btn-outline" @click="learn(100)"><Trophy :size="18" /> 一键完成</button>
      </div>
      <div v-else class="completed-badge"><CheckCircle2 :size="20" /> 已完成学习</div>
    </div>

    <button v-else class="btn-primary enroll-btn" @click="enroll"><PlayCircle :size="20" /> 报名学习</button>

    <div class="price-tag">{{ Number(course.price) > 0 ? `¥${course.price}` : '免费课程' }}</div>
  </div>

  <div v-else-if="loading" class="loading">加载中…</div>
  <div v-else class="error">{{ error || '课程不存在' }}</div>
</template>

<style scoped>
.page { max-width:720px; margin:0 auto; padding:24px 16px; }
.back-btn { display:inline-flex; align-items:center; gap:6px; border:0; background:none; color:#176b52; font-size:14px; cursor:pointer; margin-bottom:16px; padding:0; }
.hero { width:100%; aspect-ratio:16/9; object-fit:cover; border-radius:12px; margin-bottom:20px; }
.meta-row { display:flex; flex-wrap:wrap; gap:12px; align-items:center; margin-bottom:12px; font-size:13px; color:#667067; }
.meta-row span { display:inline-flex; align-items:center; gap:4px; }
.badge { background:#e8f0ec; color:#176b52; padding:3px 10px; border-radius:20px; font-size:12px; font-weight:600; }
h1 { font-size:26px; margin:0 0 12px; }
.desc { color:#59635c; line-height:1.7; margin-bottom:24px; }
.progress-section { background:#fff; border:1px solid #dedbd0; border-radius:12px; padding:20px; margin-bottom:20px; }
.progress-header { display:flex; justify-content:space-between; margin-bottom:10px; font-size:14px; }
.progress-header strong { color:#176b52; }
.progress-bar { height:8px; background:#e5e1d8; border-radius:99px; overflow:hidden; margin-bottom:16px; }
.progress-fill { height:100%; background:linear-gradient(90deg,#176b52,#2a9d6e); border-radius:99px; transition:width .4s ease; }
.learn-actions { display:flex; gap:10px; flex-wrap:wrap; }
.btn-primary { display:inline-flex; align-items:center; gap:8px; border:0; border-radius:8px; padding:12px 24px; color:#fff; background:#176b52; font-size:15px; cursor:pointer; }
.btn-outline { border:1px solid #176b52; border-radius:8px; padding:12px 20px; color:#176b52; background:transparent; font-size:14px; cursor:pointer; }
.completed-badge { display:inline-flex; align-items:center; gap:8px; color:#176b52; font-size:16px; font-weight:600; }
.enroll-btn { width:100%; justify-content:center; padding:16px; font-size:17px; margin-bottom:12px; }
.price-tag { text-align:center; color:#9b4b1f; font-weight:700; font-size:15px; }
.loading, .error { text-align:center; padding:60px 20px; color:#667067; }
</style>
