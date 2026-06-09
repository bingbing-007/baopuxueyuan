<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="admin-brand" @click="$router.push('/')">
        <strong>抱朴学院</strong>
        <span>管理后台</span>
      </div>
      <nav>
        <router-link to="/admin">📊 仪表盘</router-link>
        <router-link to="/admin/courses">📚 课程管理</router-link>
        <router-link to="/admin/analytics">📈 数据分析</router-link>
        <router-link to="/admin/knowledge">📖 知识库</router-link>
        <a href="/" style="margin-top:auto;color:#667067">← 返回学员端</a>
      </nav>
    </aside>
    <main class="admin-main"><router-view /></main>
  </div>
</template>

<style scoped>
.admin-layout { display: flex; min-height: 100vh; }
.admin-sidebar {
  width: 220px; background: #1a2e25; color: #e8ece9; display: flex; flex-direction: column; padding: 20px 0;
  position: sticky; top: 0; height: 100vh;
}
.admin-brand { padding: 0 20px 20px; cursor: pointer; border-bottom: 1px solid #2a4035; margin-bottom: 12px; }
.admin-brand strong { display: block; font-size: 18px; }
.admin-brand span { font-size: 12px; color: #8a9e91; }
nav { display: flex; flex-direction: column; flex: 1; padding: 0 12px; }
nav a { padding: 10px 12px; color: #c5d2c9; text-decoration: none; border-radius: 6px; font-size: 14px; }
nav a:hover, nav a.router-link-active { background: #2a4035; color: #fff; }
.admin-main { flex: 1; padding: 28px 32px; background: #f6f5ef; }
</style>
