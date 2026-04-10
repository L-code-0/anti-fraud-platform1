<template>
  <!-- 全屏加载遮罩 -->
  <Teleport to="body">
    <Transition name="loading-fade">
      <div v-if="loadingStore.isLoading && loadingStore.loadingType === 'fullscreen'" 
           class="loading-overlay">
        <div class="loading-content">
          <div class="loading-spinner">
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
            <div class="spinner-ring"></div>
          </div>
          <p class="loading-text">{{ loadingStore.loadingText }}</p>
          <div class="loading-progress" v-if="progress > 0">
            <div class="progress-bar" :style="{ width: progress + '%' }"></div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
  
  <!-- 按钮加载状态 -->
  <Teleport to="body">
    <div v-if="loadingStore.isLoading && loadingStore.loadingType === 'button'" 
         class="button-loading-indicator"
         :style="indicatorStyle">
      <svg class="btn-spinner" viewBox="0 0 24 24">
        <circle cx="12" cy="12" r="10" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round">
          <animate attributeName="stroke-dasharray" values="0 60;60 60;0 60" dur="1.5s" repeatCount="indefinite"/>
          <animate attributeName="stroke-dashoffset" values="0;-60" dur="1.5s" repeatCount="indefinite"/>
        </circle>
      </svg>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useLoadingStore } from '@/stores/loading'

interface Props {
  progress?: number
  indicatorStyle?: Record<string, string>
}

withDefaults(defineProps<Props>(), {
  progress: 0,
  indicatorStyle: () => ({})
})

const loadingStore = useLoadingStore()
</script>

<style scoped>
/* 全屏加载遮罩 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--color-bg-mask, rgba(0, 0, 0, 0.5));
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 32px 48px;
  background: var(--color-bg-card, #fff);
  border-radius: var(--radius-lg, 12px);
  box-shadow: var(--shadow-xl, 0 12px 36px rgba(0, 0, 0, 0.15));
}

/* 加载动画 */
.loading-spinner {
  position: relative;
  width: 60px;
  height: 60px;
}

.spinner-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 3px solid transparent;
  border-radius: 50%;
  animation: spin 1.2s linear infinite;
}

.spinner-ring:nth-child(1) {
  border-top-color: var(--color-primary, #2B6CB0);
  animation-delay: 0s;
}

.spinner-ring:nth-child(2) {
  border-right-color: var(--color-success, #38A169);
  animation-delay: -0.4s;
}

.spinner-ring:nth-child(3) {
  border-bottom-color: var(--color-warning, #D69E2E);
  animation-delay: -0.8s;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-text {
  margin: 0;
  font-size: var(--font-size-base, 14px);
  color: var(--color-text-secondary, #4A5568);
}

/* 进度条 */
.loading-progress {
  width: 200px;
  height: 4px;
  background: var(--color-bg-hover, #EDF2F7);
  border-radius: 2px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary) 0%, var(--color-success) 100%);
  border-radius: 2px;
  transition: width 0.3s ease;
}

/* 按钮加载指示器 */
.button-loading-indicator {
  position: fixed;
  z-index: 10000;
  pointer-events: none;
}

.btn-spinner {
  width: 24px;
  height: 24px;
  color: inherit;
}

/* 淡入淡出动画 */
.loading-fade-enter-active,
.loading-fade-leave-active {
  transition: opacity 0.3s ease;
}

.loading-fade-enter-from,
.loading-fade-leave-to {
  opacity: 0;
}
</style>
