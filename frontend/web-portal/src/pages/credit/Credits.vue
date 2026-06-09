<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { TrendingUp, Award, Zap } from 'lucide-vue-next'
import { getCreditAccount, getCreditRecords, getCreditRules, type CreditRule } from '../../api'

const account = ref({ totalEarned: 0, balance: 0 }); const records = ref<any[]>([])
const rules = ref<CreditRule[]>([]); const loading = ref(true)

onMounted(async () => {
  try {
    [account.value, records.value, rules.value] = await Promise.all([getCreditAccount(), getCreditRecords(), getCreditRules()])
  } finally { loading.value = false }
})
</script>

<template>
  <div class="page">
    <h1>学分中心</h1>

    <div class="credit-card">
      <div class="cc-balance"><span class="cc-num">{{ account.balance }}</span><span class="cc-label">当前学分</span></div>
      <div class="cc-total"><TrendingUp :size="16" /> 累计获得 {{ account.totalEarned }}</div>
    </div>

    <h2>获取规则</h2>
    <div class="rules-grid">
      <div v-for="r in rules" :key="r.id" class="rule-card">
        <Award :size="20" /><div><strong>+{{ r.credits }} 学分</strong><span>{{ r.name }}</span></div>
      </div>
    </div>

    <h2>学分流水</h2>
    <div v-if="records.length" class="record-list">
      <div v-for="r in records" :key="r.id" class="record-item">
        <div><Zap :size="16" :class="r.credits>0?'plus':'minus'" /></div>
        <div class="record-body"><strong>{{ r.description }}</strong><span>{{ r.created_at }}</span></div>
        <span class="record-pts" :class="r.credits>0?'plus':'minus'">{{ r.credits > 0 ? '+' : '' }}{{ r.credits }}</span>
      </div>
    </div>
    <div v-else class="empty">暂无学分记录，完成课程即可获得学分</div>
  </div>
</template>

<style scoped>
.page { max-width:720px; margin:0 auto; padding:24px 16px; }
h1 { font-size:24px; margin-bottom:20px; }
h2 { font-size:18px; margin:24px 0 14px; }
.credit-card { background:linear-gradient(135deg,#176b52,#2a9d6e); color:#fff; border-radius:14px; padding:28px; margin-bottom:8px; }
.cc-balance { display:flex; flex-direction:column; gap:4px; }
.cc-num { font-size:42px; font-weight:800; }
.cc-label { font-size:14px; opacity:.85; }
.cc-total { display:inline-flex; align-items:center; gap:6px; margin-top:12px; font-size:13px; opacity:.8; }
.rules-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(160px,1fr)); gap:10px; }
.rule-card { display:flex; align-items:center; gap:12px; background:#fff; border:1px solid #dedbd0; border-radius:10px; padding:16px; }
.rule-card strong { display:block; font-size:15px; }
.rule-card span { font-size:12px; color:#667067; }
.record-list { display:grid; gap:8px; }
.record-item { display:flex; align-items:center; gap:12px; background:#fff; border:1px solid #dedbd0; border-radius:8px; padding:14px; }
.record-body { flex:1; } .record-body strong { display:block; font-size:14px; } .record-body span { font-size:12px; color:#8a9e91; }
.record-pts { font-weight:700; font-size:16px; } .plus { color:#176b52; } .minus { color:#a0a0a0; }
.empty { text-align:center; padding:40px; color:#667067; }
</style>
