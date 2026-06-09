<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Search, Eye, Tag } from 'lucide-vue-next'
import { searchKnowledge, hotKnowledgeTags, getKnowledgeDetail, type KnowledgeArticle } from '../../api'

const articles = ref<KnowledgeArticle[]>([]); const keyword = ref('')
const hotTags = ref<{tag:string;cnt:number}[]>([]); const loading = ref(true)
const selected = ref<KnowledgeArticle | null>(null)

onMounted(async () => { await load() })

async function load() {
  loading.value = true
  articles.value = await searchKnowledge(keyword.value)
  hotTags.value = await hotKnowledgeTags()
  loading.value = false
}

async function openDetail(id: number) { selected.value = await getKnowledgeDetail(id) }
</script>

<template>
  <div class="page" v-if="!selected">
    <h1>企业知识库</h1>
    <div class="search-bar">
      <Search :size="18" /><input v-model="keyword" placeholder="搜索知识文章…" @keyup.enter="load" />
      <button @click="load">搜索</button>
    </div>
    <div v-if="hotTags.length" class="tags-row">
      <button v-for="t in hotTags.slice(0,8)" :key="t.tag" class="tag" @click="keyword=t.tag;load()">{{ t.tag }}</button>
    </div>
    <div v-if="loading" class="loading">搜索中…</div>
    <div v-else class="article-list">
      <div v-for="a in articles" :key="a.id" class="article-card" @click="openDetail(a.id)">
        <h3>{{ a.title }}</h3>
        <p>{{ a.summary }}</p>
        <div class="article-meta">
          <span><Tag :size="13" /> {{ a.category }}</span>
          <span><Eye :size="13" /> {{ a.view_count }} 阅读</span>
        </div>
      </div>
      <div v-if="!articles.length" class="empty">暂无文章，换个关键词试试</div>
    </div>
  </div>

  <div v-else class="detail-page">
    <button class="back-btn" @click="selected=null">← 返回</button>
    <span class="badge">{{ selected.category }}</span>
    <h1>{{ selected.title }}</h1>
    <div class="article-body" v-html="selected.content?.replace(/\n/g,'<br>')"></div>
    <div class="detail-meta">阅读 {{ selected.view_count }} 次</div>
  </div>
</template>

<style scoped>
.page, .detail-page { max-width:760px; margin:0 auto; padding:24px 16px; }
h1 { font-size:24px; margin-bottom:20px; }
.search-bar { display:flex; gap:10px; background:#fff; border:1px solid #dedbd0; border-radius:10px; padding:10px 14px; margin-bottom:14px; align-items:center; }
.search-bar input { flex:1; border:0; outline:none; font-size:15px; background:transparent; }
.search-bar button { border:0; background:#176b52; color:#fff; padding:8px 18px; border-radius:6px; cursor:pointer; }
.tags-row { display:flex; flex-wrap:wrap; gap:8px; margin-bottom:18px; }
.tag { border:1px solid #c5d2c9; border-radius:20px; padding:5px 14px; font-size:13px; color:#176b52; background:transparent; cursor:pointer; }
.article-card { background:#fff; border:1px solid #dedbd0; border-radius:10px; padding:18px; margin-bottom:12px; cursor:pointer; }
.article-card h3 { margin:0 0 6px; font-size:17px; }
.article-card p { margin:0 0 10px; color:#667067; font-size:14px; line-height:1.5; display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden; }
.article-meta { display:flex; gap:14px; color:#8a9e91; font-size:12px; }
.article-meta span { display:inline-flex; align-items:center; gap:4px; }
.badge { background:#e8f0ec; color:#176b52; padding:3px 10px; border-radius:20px; font-size:12px; font-weight:600; }
.article-body { margin:18px 0; line-height:1.8; font-size:15px; color:#26342d; }
.detail-meta { color:#8a9e91; font-size:13px; }
.back-btn { border:0; background:none; color:#176b52; font-size:15px; cursor:pointer; padding:0; margin-bottom:14px; display:block; }
.loading, .empty { text-align:center; padding:40px; color:#667067; }
</style>
