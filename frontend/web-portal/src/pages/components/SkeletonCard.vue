<template>
  <div class="skeleton-card" v-for="n in count" :key="n">
    <div class="sk-img"></div>
    <div class="sk-body">
      <div class="sk-line w60"></div>
      <div class="sk-line w80"></div>
      <div class="sk-line w40"></div>
    </div>
  </div>
</template>
<script setup lang="ts">defineProps<{ count?: number }>()</script>
<style scoped>
.skeleton-card { overflow:hidden; border-radius:8px; background:#fff; border:1px solid #dedbd0; }
.sk-img { width:100%; aspect-ratio:16/9; background:linear-gradient(90deg,#e8e5dc 25%,#f0ede4 50%,#e8e5dc 75%); background-size:200% 100%; animation:shimmer 1.5s infinite; }
.sk-body { padding:16px; display:grid; gap:10px; }
.sk-line { height:14px; border-radius:4px; background:linear-gradient(90deg,#e8e5dc 25%,#f0ede4 50%,#e8e5dc 75%); background-size:200% 100%; animation:shimmer 1.5s infinite; }
.w60 { width:60%; } .w80 { width:80%; } .w40 { width:40%; }
@keyframes shimmer { 0% { background-position:-200% 0; } 100% { background-position:200% 0; } }
</style>
