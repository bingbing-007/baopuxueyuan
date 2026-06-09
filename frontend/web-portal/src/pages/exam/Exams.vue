<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Clock3, ChevronRight } from 'lucide-vue-next'
import { listExams, startExam, submitAnswer, finishExam, type ExamInfo, type QuestionInfo } from '../../api'

const exams = ref<ExamInfo[]>([])
const currentExam = ref<ExamInfo | null>(null)
const questions = ref<QuestionInfo[]>([])
const currentQ = ref(0)
const recordId = ref<number | null>(null)
const answers = ref<Record<number,string>>({})
const result = ref<{score:number;passed:boolean}|null>(null)
const loading = ref(true)

onMounted(async () => { exams.value = await listExams(); loading.value = false })

async function start(id: number) {
  const r = await startExam(id)
  currentExam.value = r.exam; questions.value = r.questions; recordId.value = r.record.id; currentQ.value = 0
}

function select(questionId: number, answer: string) {
  answers.value[questionId] = answer
  if (recordId.value) submitAnswer(recordId.value, questionId, answer)
}

async function finish() {
  if (!recordId.value) return
  const r = await finishExam(recordId.value)
  result.value = { score: r.score, passed: r.passed === 1 }
}

const q = () => questions.value[currentQ.value]
const isSelected = (id: number, opt: string) => answers.value[id] === opt
</script>

<template>
  <div class="page" v-if="!currentExam">
    <h1>考试中心</h1>
    <div v-if="loading" class="loading">加载中…</div>
    <div v-else class="exam-list">
      <div v-for="e in exams" :key="e.id" class="exam-card" @click="start(e.id)">
        <div><h3>{{ e.title }}</h3><p>{{ e.description }}</p></div>
        <div class="exam-meta"><Clock3 :size="14" /> {{ e.durationMinutes }}分钟 · {{ e.totalScore }}分<ChevronRight :size="18" /></div>
      </div>
    </div>
  </div>

  <div v-else-if="result" class="result-page">
    <div class="result-icon">{{ result.passed ? '🎉' : '📝' }}</div>
    <h1>{{ result.passed ? '恭喜通过！' : '考试完成' }}</h1>
    <p>得分：<strong>{{ result.score }}</strong> 分</p>
    <button class="btn-primary" @click="currentExam=null;result=null">返回列表</button>
  </div>

  <div v-else class="exam-page">
    <div class="exam-header">
      <span>{{ currentExam.title }}</span>
      <span>第 {{ currentQ + 1 }} / {{ questions.length }} 题</span>
    </div>
    <div class="question-block">
      <h3>{{ q().stem }}</h3>
      <div class="options">
        <button v-for="opt in (typeof q().options==='string'?JSON.parse(q().options):q().options)" :key="opt.key"
          :class="['opt-btn', { selected: isSelected(q().id, opt.key) }]"
          @click="select(q().id, opt.key)">{{ opt.key }}. {{ opt.text }}</button>
      </div>
    </div>
    <div class="exam-nav">
      <button :disabled="currentQ===0" @click="currentQ--">上一题</button>
      <button v-if="currentQ<questions.length-1" @click="currentQ++">下一题</button>
      <button v-else class="btn-finish" @click="finish">交卷</button>
    </div>
  </div>
</template>

<style scoped>
.page { max-width:720px; margin:0 auto; padding:24px 16px; }
h1 { font-size:24px; margin-bottom:20px; }
.exam-card { background:#fff; border:1px solid #dedbd0; border-radius:10px; padding:18px; margin-bottom:12px; cursor:pointer; display:flex; justify-content:space-between; align-items:center; }
.exam-card h3 { margin:0 0 4px; font-size:17px; }
.exam-card p { margin:0; color:#667067; font-size:13px; }
.exam-meta { display:flex; align-items:center; gap:6px; color:#667067; font-size:13px; white-space:nowrap; }
.result-page { text-align:center; padding:60px 20px; }
.result-icon { font-size:56px; margin-bottom:16px; }
.result-page p { font-size:18px; margin-bottom:20px; }
.exam-header { display:flex; justify-content:space-between; padding:14px 0; border-bottom:1px solid #dedbd0; font-size:14px; color:#667067; }
.question-block { padding:24px 0; }
.question-block h3 { font-size:18px; margin-bottom:16px; line-height:1.6; }
.options { display:grid; gap:10px; }
.opt-btn { border:1px solid #dedbd0; border-radius:8px; padding:14px 16px; background:#fff; text-align:left; font-size:15px; cursor:pointer; }
.opt-btn.selected { border-color:#176b52; background:#e8f0ec; color:#176b52; font-weight:600; }
.exam-nav { display:flex; gap:10px; margin-top:20px; }
.exam-nav button { flex:1; border:1px solid #dedbd0; border-radius:8px; padding:12px; background:#fff; font-size:14px; cursor:pointer; }
.exam-nav button:disabled { opacity:.4; cursor:default; }
.btn-finish { background:#176b52 !important; color:#fff !important; border-color:#176b52 !important; }
.btn-primary { border:0; border-radius:8px; padding:12px 32px; color:#fff; background:#176b52; font-size:15px; cursor:pointer; }
</style>
