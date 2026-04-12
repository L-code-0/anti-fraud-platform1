<template>
  <Layout>
    <router-view v-slot="{ Component }">
      <transition name="page-fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    <!-- 全局加载状态 -->
    <GlobalLoading />
    <!-- AI助手 -->
    <AIAssistant />
  </Layout>
</template>

<script setup lang="ts">
import Layout from '@/components/common/Layout.vue'
import GlobalLoading from '@/components/common/GlobalLoading.vue'
import AIAssistant from '@/components/common/AIAssistant.vue'
</script>

<style scoped>
/* 页面过渡动画 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity var(--transition-normal);
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}
</style>
