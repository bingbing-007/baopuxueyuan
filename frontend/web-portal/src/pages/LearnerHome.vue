<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { BookOpen, CheckCircle2, Clock3, GraduationCap, LogIn, PlayCircle, RefreshCw, Settings, UserRound } from 'lucide-vue-next'
import {
  enrollCourse, getDashboard, listCourses, login, listPaths, type CourseSummary,
  type DashboardResponse, type LearningPath
} from '../api'

type Session = { userId: number; name: string; token: string }
const stored = localStorage.getItem('baopu-session')
const session = ref<Session | null>(stored ? JSON.parse(stored) : null)
const courses = ref<CourseSummary[]>([])
const paths = ref<LearningPath[]>([])
const dashboard = ref<DashboardResponse | null>(null)
const loading = ref(false)
const error = ref('')
const loginForm = reactive({ dingtalkUserId: 'demo-user', name: '演示学员', mobile: '' })

const enrolledCourses = computed(() => dashboard.value?.courses ?? courses.value.filter(c => c.enrolled))

async function loadData() {
  loading.value = true; error.value = ''
  try {
    courses.value = await listCourses()
    paths.value = await listPaths()
    if (session.value) dashboard.value = await getDashboard()
    else dashboard.value = null
  } catch (e: any) { error.value = e.message } finally { loading.value = false }
}

async function submitLogin() {
  loading.value = true; error.value = ''
  try {
    const r = await login(loginForm)
    session.value = r; localStorage.setItem('baopu-session', JSON.stringify(r))
    await loadData()
  } catch (e: any) { error.value = e.message } finally { loading.value = false }
}

function logout() { session.value = null; dashboard.value = null; localStorage.removeItem('baopu-session'); loadData() }

async function enroll(c: CourseSummary) {
  if (!session.value) { error.value = '请先登录'; return }
  await enrollCourse(c.id); await loadData()
}

async function addProgress(c: CourseSummary, step: number) {
  if (!session.value) { error.value = '请先登录'; return }
  await (await import('../api')).updateProgress(c.id, Math.min(100, c.progressPercent + step))
  await loadData()
}

onMounted(loadData)
</script>

<template>
  <main class="app-shell">
    <section class="topbar">
      <div class="brand"><span class="brand-mark"><GraduationCap :size="24" /></span><div><strong>抱朴学院</strong><span>企业学习平台</span></div></div>
      <div class="top-actions">
        <button class="icon-button" @click="$router.push('/admin')" title="管理后台"><Settings :size="18" /></button>
        <button class="icon-button" :disabled="loading" @click="loadData"><RefreshCw :size="18" /></button>
        <button v-if="session" class="ghost-button" @click="logout"><UserRound :size="18" />{{ session.name }}</button>
      </div>
    </section>

    <section class="workspace">
      <aside class="side-panel">
        <div v-if="!session" class="login-panel">
          <h1>登录学习门户</h1>
          <form @submit.prevent="submitLogin">
            <label>钉钉用户 ID<input v-model="loginForm.dingtalkUserId" required maxlength="64" /></label>
            <label>姓名<input v-model="loginForm.name" required maxlength="100" /></label>
            <label>手机号<input v-model="loginForm.mobile" maxlength="30" /></label>
            <button class="primary-button" type="submit" :disabled="loading"><LogIn :size="18" />进入学习</button>
          </form>
        </div>
        <div v-else class="dashboard-panel">
          <span class="eyebrow">学习看板</span><h1>{{ session.name }}</h1>
          <div class="stat-grid">
            <div><strong>{{ dashboard?.enrolledCount ?? 0 }}</strong><span>已报名</span></div>
            <div><strong>{{ dashboard?.completedCount ?? 0 }}</strong><span>已完成</span></div>
            <div><strong>{{ dashboard?.averageProgress ?? 0 }}%</strong><span>平均进度</span></div>
          </div>
        </div>
        <div v-if="enrolledCourses.length" class="learning-list">
          <h2>继续学习</h2>
          <article v-for="c in enrolledCourses" :key="c.id" class="compact-course">
            <div><strong>{{ c.title }}</strong><span>{{ c.progressPercent }}%</span></div>
            <progress :value="c.progressPercent" max="100" />
          </article>
        </div>
      </aside>

      <section class="content-area">
        <div v-if="error" class="notice">{{ error }}</div>

        <div class="section-heading"><div><span class="eyebrow">学习地图</span><h2>推荐学习路径</h2></div></div>
        <div class="path-list">
          <div v-for="p in paths" :key="p.id" class="path-card">
            <strong>{{ p.title }}</strong><span>{{ p.category }}</span>
            <p>{{ p.description }}</p>
          </div>
        </div>

        <div class="section-heading low"><div><span class="eyebrow">课程中心</span><h2>可学习课程</h2></div><span>{{ courses.length }} 门课程</span></div>
        <div class="course-grid">
          <article v-for="c in courses" :key="c.id" class="course-card">
            <img :src="c.coverUrl" :alt="c.title" />
            <div class="course-body">
              <div class="course-meta"><span>{{ c.category }}</span><span><Clock3 :size="15" /> {{ c.durationMinutes }} 分钟</span></div>
              <h3>{{ c.title }}</h3><p>{{ c.description }}</p>
              <div class="lecturer"><BookOpen :size="16" />{{ c.lecturer }}</div>
              <div v-if="c.enrolled" class="progress-block">
                <div><span>学习进度</span><strong>{{ c.progressPercent }}%</strong></div>
                <progress :value="c.progressPercent" max="100" />
              </div>
              <div class="course-actions">
                <button v-if="!c.enrolled" class="primary-button" @click="enroll(c)"><PlayCircle :size="18" />报名学习</button>
                <button v-else-if="c.progressPercent < 100" class="primary-button" @click="addProgress(c, 25)"><PlayCircle :size="18" />学习 25%</button>
                <button v-else class="complete-button" disabled><CheckCircle2 :size="18" />已完成</button>
                <span class="price">{{ Number(c.price) > 0 ? `¥${c.price}` : '免费' }}</span>
              </div>
            </div>
          </article>
        </div>
      </section>
    </section>
  </main>
</template>

<style scoped>
.path-list { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 14px; margin-bottom: 8px; }
.path-card { padding: 18px; background: #fff; border: 1px solid #dedbd0; border-radius: 8px; }
.path-card strong { display: block; font-size: 16px; }
.path-card span { font-size: 12px; color: #176b52; }
.path-card p { margin-top: 8px; color: #59635c; font-size: 14px; }
</style>
