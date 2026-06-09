<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { BookOpen, CheckCircle2, Clock3, GraduationCap, LogIn, PlayCircle, RefreshCw, UserRound } from 'lucide-vue-next'
import {
  enrollCourse,
  getDashboard,
  listCourses,
  login,
  updateProgress,
  type CourseSummary,
  type DashboardResponse
} from './api'

type Session = {
  userId: number
  name: string
  token: string
}

const storedSession = localStorage.getItem('baopu-session')
const session = ref<Session | null>(storedSession ? JSON.parse(storedSession) : null)
const courses = ref<CourseSummary[]>([])
const dashboard = ref<DashboardResponse | null>(null)
const loading = ref(false)
const error = ref('')
const loginForm = reactive({
  dingtalkUserId: 'demo-user',
  name: '演示学员',
  mobile: ''
})

const enrolledCourses = computed(() => dashboard.value?.courses ?? courses.value.filter((course) => course.enrolled))
const recommendedCourses = computed(() => courses.value.filter((course) => !course.enrolled).slice(0, 3))

async function loadData() {
  loading.value = true
  error.value = ''
  try {
    courses.value = await listCourses(session.value?.userId)
    if (session.value) {
      dashboard.value = await getDashboard(session.value.userId)
    } else {
      dashboard.value = null
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载失败'
  } finally {
    loading.value = false
  }
}

async function submitLogin() {
  loading.value = true
  error.value = ''
  try {
    const result = await login(loginForm)
    session.value = result
    localStorage.setItem('baopu-session', JSON.stringify(result))
    await loadData()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '登录失败'
  } finally {
    loading.value = false
  }
}

function logout() {
  session.value = null
  dashboard.value = null
  localStorage.removeItem('baopu-session')
  void loadData()
}

async function enroll(course: CourseSummary) {
  if (!session.value) {
    error.value = '请先登录后再报名课程'
    return
  }
  await enrollCourse(session.value.userId, course.id)
  await loadData()
}

async function addProgress(course: CourseSummary, step: number) {
  if (!session.value) {
    error.value = '请先登录后再学习课程'
    return
  }
  const nextProgress = Math.min(100, course.progressPercent + step)
  await updateProgress(session.value.userId, course.id, nextProgress)
  await loadData()
}

onMounted(loadData)
</script>

<template>
  <main class="app-shell">
    <section class="topbar">
      <div class="brand">
        <span class="brand-mark"><GraduationCap :size="24" /></span>
        <div>
          <strong>抱朴学院</strong>
          <span>企业学习平台</span>
        </div>
      </div>
      <div class="top-actions">
        <button class="icon-button" :disabled="loading" title="刷新数据" @click="loadData">
          <RefreshCw :size="18" />
        </button>
        <button v-if="session" class="ghost-button" @click="logout">
          <UserRound :size="18" />
          {{ session.name }}
        </button>
      </div>
    </section>

    <section class="workspace">
      <aside class="side-panel">
        <div v-if="!session" class="login-panel">
          <h1>登录学习门户</h1>
          <form @submit.prevent="submitLogin">
            <label>
              钉钉用户 ID
              <input v-model="loginForm.dingtalkUserId" required maxlength="64" />
            </label>
            <label>
              姓名
              <input v-model="loginForm.name" required maxlength="100" />
            </label>
            <label>
              手机号
              <input v-model="loginForm.mobile" maxlength="30" />
            </label>
            <button class="primary-button" type="submit" :disabled="loading">
              <LogIn :size="18" />
              进入学习
            </button>
          </form>
        </div>

        <div v-else class="dashboard-panel">
          <span class="eyebrow">学习看板</span>
          <h1>{{ session.name }}</h1>
          <div class="stat-grid">
            <div>
              <strong>{{ dashboard?.enrolledCount ?? 0 }}</strong>
              <span>已报名</span>
            </div>
            <div>
              <strong>{{ dashboard?.completedCount ?? 0 }}</strong>
              <span>已完成</span>
            </div>
            <div>
              <strong>{{ dashboard?.averageProgress ?? 0 }}%</strong>
              <span>平均进度</span>
            </div>
          </div>
        </div>

        <div v-if="enrolledCourses.length" class="learning-list">
          <h2>继续学习</h2>
          <article v-for="course in enrolledCourses" :key="course.id" class="compact-course">
            <div>
              <strong>{{ course.title }}</strong>
              <span>{{ course.progressPercent }}%</span>
            </div>
            <progress :value="course.progressPercent" max="100" />
          </article>
        </div>
      </aside>

      <section class="content-area">
        <div v-if="error" class="notice">{{ error }}</div>

        <div class="section-heading">
          <div>
            <span class="eyebrow">课程中心</span>
            <h2>可学习课程</h2>
          </div>
          <span>{{ courses.length }} 门课程</span>
        </div>

        <div class="course-grid">
          <article v-for="course in courses" :key="course.id" class="course-card">
            <img :src="course.coverUrl" :alt="course.title" />
            <div class="course-body">
              <div class="course-meta">
                <span>{{ course.category }}</span>
                <span><Clock3 :size="15" /> {{ course.durationMinutes }} 分钟</span>
              </div>
              <h3>{{ course.title }}</h3>
              <p>{{ course.description }}</p>
              <div class="lecturer">
                <BookOpen :size="16" />
                {{ course.lecturer }}
              </div>
              <div v-if="course.enrolled" class="progress-block">
                <div>
                  <span>学习进度</span>
                  <strong>{{ course.progressPercent }}%</strong>
                </div>
                <progress :value="course.progressPercent" max="100" />
              </div>
              <div class="course-actions">
                <button v-if="!course.enrolled" class="primary-button" @click="enroll(course)">
                  <PlayCircle :size="18" />
                  报名学习
                </button>
                <button v-else-if="course.progressPercent < 100" class="primary-button" @click="addProgress(course, 25)">
                  <PlayCircle :size="18" />
                  学习 25%
                </button>
                <button v-else class="complete-button" disabled>
                  <CheckCircle2 :size="18" />
                  已完成
                </button>
                <span class="price">{{ Number(course.price) > 0 ? `¥${course.price}` : '免费' }}</span>
              </div>
            </div>
          </article>
        </div>

        <div v-if="recommendedCourses.length" class="section-heading low">
          <div>
            <span class="eyebrow">推荐</span>
            <h2>下一步可以报名</h2>
          </div>
        </div>
      </section>
    </section>
  </main>
</template>
