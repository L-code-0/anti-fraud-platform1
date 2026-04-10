/**
 * P2 体验类优化 - 组件导出
 * 
 * 使用方式：
 * import { GlobalLoading, SkeletonScreen, ErrorDisplay, LoadingButton, LoadingInput } from '@/components/common'
 */

// 加载状态组件
export { default as GlobalLoading } from './GlobalLoading.vue'
export { default as LoadingButton } from './LoadingButton.vue'
export { default as LoadingInput } from './LoadingInput.vue'

// 骨架屏组件
export { default as SkeletonScreen } from './SkeletonScreen.vue'
export { default as SkeletonCard } from './SkeletonCard.vue'

// 错误展示组件
export { default as ErrorDisplay } from './ErrorDisplay.vue'
