<template>
  <div class="lazy-image-container" :style="{ width: width, height: height }">
    <img
      v-if="isLoaded"
      :src="src"
      :alt="alt"
      :class="imageClass"
      @load="onLoad"
      @error="onError"
    />
    <div v-else class="lazy-image-placeholder">
      <el-icon class="loading-icon"><Loading /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Loading } from '@element-plus/icons-vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: ''
  },
  width: {
    type: String,
    default: '100%'
  },
  height: {
    type: String,
    default: 'auto'
  },
  imageClass: {
    type: String,
    default: ''
  }
})

const isLoaded = ref(false)
const observer = ref<IntersectionObserver | null>(null)
const imageRef = ref<HTMLImageElement | null>(null)

const onLoad = () => {
  isLoaded.value = true
}

const onError = () => {
  isLoaded.value = true
  // 可以在这里设置一个错误占位图
}

const handleIntersect = (entries: IntersectionObserverEntry[]) => {
  const entry = entries[0]
  if (entry.isIntersecting) {
    isLoaded.value = true
    if (observer.value) {
      observer.value.disconnect()
    }
  }
}

onMounted(() => {
  if ('IntersectionObserver' in window) {
    observer.value = new IntersectionObserver(handleIntersect, {
      root: null,
      rootMargin: '200px',
      threshold: 0.1
    })
    
    const container = document.querySelector('.lazy-image-container')
    if (container) {
      observer.value.observe(container)
    }
  } else {
    // 降级处理，直接加载图片
    isLoaded.value = true
  }
})

onUnmounted(() => {
  if (observer.value) {
    observer.value.disconnect()
  }
})
</script>

<style scoped>
.lazy-image-container {
  position: relative;
  overflow: hidden;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.lazy-image-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.loading-icon {
  font-size: 24px;
  color: #c0c4cc;
  animation: spin 1s linear infinite;
}

img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: opacity 0.3s ease;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>