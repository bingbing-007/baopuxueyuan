<template>
  <transition name="page-fade" mode="out-in">
    <slot />
  </transition>
</template>
<style>
.page-fade-enter-active, .page-fade-leave-active { transition: opacity .2s ease; }
.page-fade-enter-from, .page-fade-leave-to { opacity: 0; }
</style>
