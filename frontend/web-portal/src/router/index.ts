import { createRouter, createWebHistory } from 'vue-router'
import LearnerHome from '../pages/LearnerHome.vue'
import AdminLayout from '../pages/admin/AdminLayout.vue'
import AdminDashboard from '../pages/admin/AdminDashboard.vue'
import AdminCourses from '../pages/admin/AdminCourses.vue'
import AdminAnalytics from '../pages/admin/AdminAnalytics.vue'
import AdminKnowledge from '../pages/admin/AdminKnowledge.vue'

const routes = [
  { path: '/', component: LearnerHome },
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      { path: '', component: AdminDashboard },
      { path: 'courses', component: AdminCourses },
      { path: 'analytics', component: AdminAnalytics },
      { path: 'knowledge', component: AdminKnowledge },
    ]
  }
]

export default createRouter({ history: createWebHistory(), routes })

