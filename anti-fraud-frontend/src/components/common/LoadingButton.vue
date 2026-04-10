<template>
  <el-button
    :type="type"
    :size="size"
    :disabled="loading || disabled"
    :plain="plain"
    :round="round"
    :circle="circle"
    :icon="loading ? undefined : icon"
    class="loading-button"
    :class="{ 'is-loading': loading }"
    v-bind="$attrs"
    @click="handleClick"
  >
    <template v-if="loading" #icon>
      <svg class="btn-loading-spinner" viewBox="0 0 24 24">
        <circle 
          cx="12" 
          cy="12" 
          r="10" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="3" 
          stroke-linecap="round"
        >
          <animate 
            attributeName="stroke-dasharray" 
            values="0 63;63 63;0 63" 
            dur="1.5s" 
            repeatCount="indefinite"
          />
          <animate 
            attributeName="stroke-dashoffset" 
            values="0;-63" 
            dur="1.5s" 
            repeatCount="indefinite"
          />
        </circle>
      </svg>
    </template>
    <slot v-if="!loading">{{ text }}</slot>
    <span v-else>{{ loadingText || text }}</span>
  </el-button>
</template>

<script setup lang="ts">
import type { ButtonType, ButtonSize } from 'element-plus'

interface Props {
  loading?: boolean
  disabled?: boolean
  type?: ButtonType
  size?: ButtonSize
  text?: string
  loadingText?: string
  plain?: boolean
  round?: boolean
  circle?: boolean
  icon?: any
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  disabled: false,
  type: 'primary',
  size: 'default',
  text: '确定',
  plain: false,
  round: false,
  circle: false
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const handleClick = (event: MouseEvent) => {
  if (!props.loading && !props.disabled) {
    emit('click', event)
  }
}
</script>

<style scoped>
.loading-button {
  position: relative;
  transition: all 0.3s ease;
}

.loading-button.is-loading {
  pointer-events: none;
}

.btn-loading-spinner {
  width: 16px;
  height: 16px;
  animation: btn-spin 1.5s linear infinite;
}

@keyframes btn-spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 加载状态下的按钮样式微调 */
.loading-button.is-loading:not(.el-button--text):not(.el-button--plain) {
  opacity: 0.9;
}

.loading-button.is-loading.el-button--text {
  color: inherit;
  opacity: 0.7;
}
</style>
