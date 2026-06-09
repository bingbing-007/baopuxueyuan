<template>
  <div class="empty-state">
    <div class="empty-icon">{{ icon }}</div>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <button v-if="action" class="primary-button" @click="$emit('action')">{{ action }}</button>
  </div>
</template>
<script setup lang="ts">
defineProps<{ icon?: string; title: string; description?: string; action?: string }>()
defineEmits(['action'])
</script>
<style scoped>
.empty-state { text-align:center; padding:48px 20px; }
.empty-icon { font-size:48px; margin-bottom:12px; }
h3 { font-size:18px; margin-bottom:6px; }
p { color:#667067; font-size:14px; margin-bottom:16px; }
.primary-button { border:0; border-radius:8px; padding:10px 24px; color:#fff; background:#176b52; cursor:pointer; font-size:14px; }
</style>
