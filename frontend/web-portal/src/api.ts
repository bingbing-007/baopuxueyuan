export type CourseSummary = {
  id: number; title: string; description: string; coverUrl: string; category: string
  lecturer: string; durationMinutes: number; price: number; progressPercent: number; enrolled: boolean
}

export type LoginResponse = { userId: number; name: string; token: string }
export type DashboardResponse = { enrolledCount: number; completedCount: number; averageProgress: number; courses: CourseSummary[] }
export type LearningPath = { id: number; title: string; description: string; coverUrl: string; category: string }
export type PathStageCourse = { courseId: number; courseTitle: string; courseCover: string; progressPercent: number; enrolled: boolean }
export type PathStage = { id: number; title: string; sortOrder: number; unlocked: boolean; completed: boolean; courses: PathStageCourse[] }
export type PathDetail = { path: LearningPath; stages: PathStage[]; enrolled: boolean }
export type ExamInfo = { id: number; title: string; description: string; durationMinutes: number; passScore: number; totalScore: number }
export type QuestionInfo = { id: number; type: string; stem: string; options: string; score: number }
export type ExamStartResponse = { exam: ExamInfo; questions: QuestionInfo[]; record: { id: number } }
export type ExamFinishResponse = { id: number; score: number; passed: number }
export type KnowledgeArticle = { id: number; title: string; summary: string; content: string; tags: string; category: string; view_count: number }
export type CreditRule = { id: number; name: string; code: string; actionType: string; credits: number }

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''
export function getToken(): string | null {
  try { const r = localStorage.getItem('baopu-session'); return r ? JSON.parse(r).token ?? null : null } catch { return null }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers); headers.set('Content-Type', 'application/json')
  const token = getToken(); if (token) headers.set('Authorization', `Bearer ${token}`)
  const res = await fetch(`${API_BASE}${path}`, { ...options, headers })
  if (!res.ok) { const b = await res.json().catch(() => ({ message: '请求失败' })); throw new Error(b.message ?? '请求失败') }
  return res.json() as Promise<T>
}

export const login = (p: { dingtalkUserId: string; name: string; mobile?: string }) =>
  request<LoginResponse>('/api/auth/login', { method: 'POST', body: JSON.stringify({ ...p, authCode: p.dingtalkUserId }) })

export const dingtalkLogin = (authCode: string) =>
  request<LoginResponse>('/api/auth/dingtalk/login', { method: 'POST', body: JSON.stringify({ authCode }) })

export const listCourses = () => request<CourseSummary[]>('/api/courses')
export const getCourseDetail = (id: number) => request<CourseSummary>('/api/courses/' + id)
export const getDashboard = () => request<DashboardResponse>('/api/me/dashboard')
export const enrollCourse = (id: number) => request<CourseSummary>('/api/courses/' + id + '/enroll', { method: 'POST' })
export const updateProgress = (id: number, p: number) =>
  request<CourseSummary>('/api/courses/' + id + '/progress', { method: 'PUT', body: JSON.stringify({ progressPercent: p }) })

export const listPaths = () => request<LearningPath[]>('/api/paths')
export const getPathDetail = (id: number) => request<PathDetail>('/api/paths/' + id)
export const enrollPath = (id: number) => request<{ status: string }>('/api/paths/' + id + '/enroll', { method: 'POST' })

export const listExams = () => request<ExamInfo[]>('/api/exams')
export const getExam = (id: number) => request<ExamInfo>('/api/exams/' + id)
export const startExam = (id: number) => request<ExamStartResponse>('/api/exams/' + id + '/start', { method: 'POST' })
export const submitAnswer = (recId: number, qid: number, ans: string) =>
  request('/api/exams/records/' + recId + '/answer', { method: 'POST', body: JSON.stringify({ questionId: String(qid), userAnswer: ans }) })
export const finishExam = (recId: number) => request<ExamFinishResponse>('/api/exams/records/' + recId + '/finish', { method: 'POST' })
export const getExamRecords = () => request<any[]>('/api/exams/my-records')

export const searchKnowledge = (kw?: string) => request<KnowledgeArticle[]>('/api/knowledge' + (kw ? '?keyword=' + encodeURIComponent(kw) : ''))
export const getKnowledgeDetail = (id: number) => request<KnowledgeArticle>('/api/knowledge/' + id)
export const hotKnowledgeTags = () => request<{tag:string;cnt:number}[]>('/api/knowledge/tags/hot')

export const getCreditAccount = () => request<{totalEarned:number;balance:number}>('/api/credits/my-account')
export const getCreditRecords = () => request<any[]>('/api/credits/my-records')
export const getCreditRules = () => request<CreditRule[]>('/api/credits/rules')

export const getAnalyticsOverview = () => request<any>('/api/analytics/overview')
export const getTopCourses = (limit = 10) => request<any[]>('/api/analytics/top-courses?limit=' + limit)
export const getTopLearners = (limit = 10) => request<any[]>('/api/analytics/top-learners?limit=' + limit)
export const getMonthlyTrend = () => request<any[]>('/api/analytics/monthly-trend')
