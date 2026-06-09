<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { searchKnowledge, type KnowledgeArticle } from '../../../api'

const articles = ref<KnowledgeArticle[]>([]); const loading = ref(true)
const form = ref({ title: '', content: '', summary: '', tags: '', category: '通用' })
const editing = ref(false)

onMounted(async () => { articles.value = await searchKnowledge(); loading.value = false })

async function create() {
  await fetch('/api/knowledge', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', Authorization: 'Bearer ' + (await import('../../../api')).getToken() },
    body: JSON.stringify(form.value)
  })
  articles.value = await searchKnowledge()
  form.value = { title: '', content: '', summary: '', tags: '', category: '通用' }; editing.value = false
}
</script>

<template>
  <div>
    <h1>知识库管理</h1>
    <button class="btn-primary" @click="editing=!editing">{{ editing ? '取消' : '新建文章' }}</button>
    <div v-if="editing" class="edit-form">
      <input v-model="form.title" placeholder="标题" />
      <input v-model="form.summary" placeholder="摘要" />
      <input v-model="form.tags" placeholder="标签(逗号分隔)" />
      <input v-model="form.category" placeholder="分类" />
      <textarea v-model="form.content" placeholder="正文内容" rows="6"></textarea>
      <button class="btn-primary" @click="create">发布</button>
    </div>

    <table v-if="!loading" class="mt">
      <thead><tr><th>标题</th><th>分类</th><th>标签</th><th>阅读</th></tr></thead>
      <tbody><tr v-for="a in articles" :key="a.id">
        <td><strong>{{ a.title }}</strong></td>
        <td>{{ a.category }}</td>
        <td>{{ a.tags }}</td>
        <td>{{ a.view_count }}</td>
      </tr></tbody>
    </table>
    <div v-else class="loading">加载中…</div>
  </div>
</template>

<style scoped>
h1 { font-size:22px; margin-bottom:16px; }
.btn-primary { border:0; border-radius:8px; padding:10px 20px; color:#fff; background:#176b52; cursor:pointer; font-size:14px; margin-bottom:16px; display:inline-block; }
.edit-form { display:grid; gap:10px; background:#fff; border:1px solid #dedbd0; border-radius:10px; padding:18px; margin-bottom:18px; }
.edit-form input, .edit-form textarea { border:1px solid #d7d3c7; border-radius:6px; padding:10px; font-size:14px; width:100%; }
table { width:100%; border-collapse:collapse; background:#fff; border:1px solid #dedbd0; border-radius:8px; overflow:hidden; }
th, td { padding:12px 14px; border-bottom:1px solid #f0ede4; font-size:14px; text-align:left; }
th { background:#f8f7f2; color:#667067; }
.mt { margin-top:14px; }
.loading { text-align:center; padding:40px; color:#667067; }
</style>
