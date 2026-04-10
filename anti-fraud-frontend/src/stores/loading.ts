/**
 * 全局加载状态管理
 * 统一管理全局加载状态，支持全屏加载和局部加载
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLoadingStore = defineStore('loading', () => {
  // 全局加载状态
  const isLoading = ref(false)
  const loadingText = ref('加载中...')
  const loadingType = ref<'fullscreen' | 'inline' | 'button'>('fullscreen')
  
  // 请求计数器（支持多个请求并发）
  let requestCount = 0
  
  // 开始加载
  const startLoading = (options?: {
    text?: string
    type?: 'fullscreen' | 'inline' | 'button'
  }) => {
    requestCount++
    if (!isLoading.value) {
      isLoading.value = true
      loadingText.value = options?.text || '加载中...'
      loadingType.value = options?.type || 'fullscreen'
    }
  }
  
  // 结束加载
  const stopLoading = () => {
    requestCount--
    if (requestCount <= 0) {
      requestCount = 0
      isLoading.value = false
      loadingText.value = '加载中...'
    }
  }
  
  // 强制结束所有加载
  const forceStopLoading = () => {
    requestCount = 0
    isLoading.value = false
    loadingText.value = '加载中...'
  }
  
  // 包装异步函数，自动管理加载状态
  const wrap = async <T>(
    fn: () => Promise<T>,
    options?: {
      text?: string
      type?: 'fullscreen' | 'inline' | 'button'
    }
  ): Promise<T> => {
    startLoading(options)
    try {
      const result = await fn()
      return result
    } finally {
      stopLoading()
    }
  }
  
  return {
    isLoading,
    loadingText,
    loadingType,
    startLoading,
    stopLoading,
    forceStopLoading,
    wrap
  }
})
