<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getToken } from '../../api'

const authed = ref(false)
const user = ref<any>(null)

onMounted(() => {
  const stored = localStorage.getItem('baopu-session')
  if (stored) {
    user.value = JSON.parse(stored)
    authed.value = true
  } else {
    authed.value = false
  }
})
</script>

<template>
  <div class="admin-layout" v-if="authed">
    <aside class="admin-sidebar">
      <div class="admin-brand">
        <strong>抱朴学院</strong>
        <span>管理后台</span>
      </div>
      <nav>
        <router-link to="/admin">仪表盘</router-link>
        <router-link to="/admin/courses">课程管理</router-link>
        <router-link to="/admin/analytics">数据分析</router-link>
        <router-link to="/admin/knowledge">知识库</router-link>
        <router-link to="/" class="back-link">返回学员端</router-link>
      </nav>
      <div class="user-badge" v-if="user">{{ user.name }}</div>
    </aside>
    <main class="admin-main"><router-view /></main>
  </div>
  <div v-else class="admin-layout" style="justify-content:center;align-items:center">
    <p>请先在钉钉客户端内登录</p>
  </div>
</template>

<style scoped>
.admin-layout { display: flex; min-height: 100vh; }
.admin-sidebar {
  width: 220px; background: #1a2e25; color: #e8ece9; display: flex; flex-direction: column; padding: 20px 0;
  position: sticky; top: 0; height: 100vh;
}
.admin-brand { padding: 0 20px 20px; border-bottom: 1px solid #2a4035; margin-bottom: 12px; }
.admin-brand strong { display: block; font-size: 18px; }
.admin-brand span { font-size: 12px; color: #8a9e91; }
nav { display: flex; flex-direction: column; flex: 1; padding: 0 12px; }
nav a { padding: 10px 12px; color: #c5d2c9; text-decoration: none; border-radius: 6px; font-size: 14px; }
nav a:hover, nav a.router-link-active { background: #2a4035; color: #fff; }
.back-link { margin-top: 12px; border-top: 1px solid #2a4035; padding-top: 12px; color: #8a9e91 !important; font-size: 13px; }
.user-badge { padding: 12px 20px; font-size: 13px; color: #8a9e91; border-top: 1px solid #2a4035; margin-top: auto; }
.admin-main { flex: 1; padding: 28px 32px; background: #f6f5ef; }
</style>
