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

async function request<T>(path: string, options: RequestInit = {}, userId?: number | null): Promise<T> {
  const headers = new Headers(options.headers)
  headers.set('Content-Type', 'application/json')
  if (userId) {
    headers.set('X-User-Id', String(userId))
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
    body: JSON.stringify(payload)
  })
}

export function listCourses(userId?: number | null) {
  return request<CourseSummary[]>('/api/courses', {}, userId)
}

export function getDashboard(userId: number) {
  return request<DashboardResponse>('/api/me/dashboard', {}, userId)
}

export function enrollCourse(userId: number, courseId: number) {
  return request<CourseSummary>(`/api/courses/${courseId}/enroll`, { method: 'POST' }, userId)
}

export function updateProgress(userId: number, courseId: number, progressPercent: number) {
  return request<CourseSummary>(
    `/api/courses/${courseId}/progress`,
    { method: 'PUT', body: JSON.stringify({ progressPercent }) },
    userId
  )
}
