<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { CheckCircle2, Lock, PlayCircle, ChevronRight } from 'lucide-vue-next'
import { getPathDetail, enrollPath, type PathDetail } from '../../api'

const route = useRoute(); const data = ref<PathDetail | null>(null); const loading = ref(true)

onMounted(async () => {
  data.value = await getPathDetail(Number(route.params.id)); loading.value = false
})

async function enroll() { if (!data.value) return; await enrollPath(data.value.path.id); data.value.enrolled = true }
</script>

<template>
  <div class="page" v-if="data">
    <h1>{{ data.path.title }}</h1>
    <p class="desc">{{ data.path.description }}</p>

    <div v-if="!data.enrolled" class="enroll-banner">
      <button class="btn-primary" @click="enroll"><PlayCircle :size="20" /> 加入学习路径</button>
    </div>

    <div class="stages">
      <div v-for="(s, i) in data.stages" :key="s.id" :class="['stage', { locked: !s.unlocked }]">
        <div class="stage-header">
          <div class="stage-num">
            <CheckCircle2 v-if="s.completed" :size="20" class="done" />
            <Lock v-else-if="!s.unlocked" :size="20" class="locked" />
            <span v-else class="num">{{ i + 1 }}</span>
          </div>
          <div class="stage-info"><strong>第{{ i+1 }}阶段</strong><span>{{ s.unlocked ? '可学习' : '需完成上一阶段' }}</span></div>
        </div>
        <div v-if="s.unlocked" class="stage-courses">
          <div v-for="c in s.courses" :key="c.courseId" class="sc-item" @click="$router.push('/course/'+c.courseId)">
            <img :src="c.courseCover" /><div><strong>{{ c.courseTitle }}</strong><span>{{ c.progressPercent }}%</span></div>
            <ChevronRight :size="16" />
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else-if="loading" class="loading">加载中…</div>
</template>

<style scoped>
.page { max-width:720px; margin:0 auto; padding:24px 16px; }
h1 { font-size:24px; margin-bottom:8px; }
.desc { color:#667067; margin-bottom:24px; line-height:1.6; }
.enroll-banner { text-align:center; margin-bottom:24px; }
.btn-primary { display:inline-flex; align-items:center; gap:8px; border:0; border-radius:10px; padding:14px 32px; color:#fff; background:#176b52; font-size:16px; cursor:pointer; }
.stages { display:grid; gap:14px; }
.stage { background:#fff; border:1px solid #dedbd0; border-radius:12px; padding:18px; }
.stage.locked { opacity:.5; }
.stage-header { display:flex; align-items:center; gap:14px; margin-bottom:12px; }
.stage-num { width:36px; height:36px; border-radius:50%; display:flex; align-items:center; justify-content:center; background:#e8f0ec; }
.num { font-weight:700; color:#176b52; }
.done { color:#176b52; } .locked { color:#8a9e91; }
.stage-info strong { display:block; font-size:15px; } .stage-info span { font-size:12px; color:#667067; }
.sc-item { display:flex; align-items:center; gap:12px; padding:10px; border-radius:8px; cursor:pointer; }
.sc-item:hover { background:#f6f5ef; }
.sc-item img { width:48px; height:36px; border-radius:6px; object-fit:cover; }
.sc-item div strong { display:block; font-size:14px; } .sc-item div span { font-size:12px; color:#176b52; }
.loading { text-align:center; padding:60px; color:#667067; }
</style>
