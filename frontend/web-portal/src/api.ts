export type CourseSummary = {
  id: number
  title: string
  description: string
  coverUrl: string
  category: string
  lecturer: string
  durationMinutes: number
  price: number
  progressPercent: number
  enrolled: boolean
}

export type LoginResponse = {
  userId: number
  name: string
  token: string
}

export type DashboardResponse = {
  enrolledCount: number
  completedCount: number
  averageProgress: number
  courses: CourseSummary[]
}

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''

function getToken(): string | null {
  try {
    const raw = localStorage.getItem('baopu-session')
    if (!raw) return null
    return JSON.parse(raw).token ?? null
  } catch {
    return null
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  headers.set('Content-Type', 'application/json')
  const token = getToken()
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers })
  if (!response.ok) {
    const body = await response.json().catch(() => ({ message: '请求失败' }))
    throw new Error(body.message ?? '请求失败')
  }
  return response.json() as Promise<T>
}

export function login(payload: { dingtalkUserId: string; name: string; mobile?: string }) {
  return request<LoginResponse>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ ...payload, authCode: payload.dingtalkUserId })
  })
}

export function dingtalkLogin(authCode: string) {
  return request<LoginResponse>('/api/auth/dingtalk/login', {
    method: 'POST',
    body: JSON.stringify({ authCode })
  })
}

export function listCourses() {
  return request<CourseSummary[]>('/api/courses')
}

export function getDashboard() {
  return request<DashboardResponse>('/api/me/dashboard')
}

export function enrollCourse(courseId: number) {
  return request<CourseSummary>(`/api/courses/${courseId}/enroll`, { method: 'POST' })
}

export function updateProgress(courseId: number, progressPercent: number) {
  return request<CourseSummary>(
    `/api/courses/${courseId}/progress`,
    { method: 'PUT', body: JSON.stringify({ progressPercent }) }
  )
}
