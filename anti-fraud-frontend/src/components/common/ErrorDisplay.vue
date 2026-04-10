<template>
  <div class="error-display" :class="[`error-${type}`, { 'is-compact': compact }]">
    <!-- 图标 -->
    <div class="error-icon">
      <svg v-if="type === 'error'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10"/>
        <line x1="15" y1="9" x2="9" y2="15"/>
        <line x1="9" y1="9" x2="15" y2="15"/>
      </svg>
      <svg v-else-if="type === 'warning'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
        <line x1="12" y1="9" x2="12" y2="13"/>
        <line x1="12" y1="17" x2="12.01" y2="17"/>
      </svg>
      <svg v-else-if="type === 'info'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10"/>
        <line x1="12" y1="16" x2="12" y2="12"/>
        <line x1="12" y1="8" x2="12.01" y2="8"/>
      </svg>
      <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10"/>
        <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/>
        <line x1="12" y1="17" x2="12.01" y2="17"/>
      </svg>
    </div>
    
    <!-- 内容 -->
    <div class="error-content">
      <h3 v-if="title" class="error-title">{{ title }}</h3>
      <p class="error-message">{{ message }}</p>
      
      <!-- 详细信息 -->
      <div v-if="details && showDetails" class="error-details">
        <pre>{{ details }}</pre>
      </div>
      
      <!-- 操作按钮 -->
      <div v-if="$slots.actions || showRetry" class="error-actions">
        <slot name="actions">
          <el-button v-if="showRetry" type="primary" @click="handleRetry">
            {{ retryText }}
          </el-button>
          <el-button v-if="showDetails" text @click="showDetails = !showDetails">
            {{ showDetails ? '收起详情' : '查看详情' }}
          </el-button>
        </slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface Props {
  type?: 'error' | 'warning' | 'info' | 'unknown'
  title?: string
  message: string
  details?: string
  compact?: boolean
  showRetry?: boolean
  retryText?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'error',
  compact: false,
  showRetry: false,
  retryText: '重试'
})

const emit = defineEmits<{
  retry: []
}>()

const showDetails = ref(false)

const handleRetry = () => {
  emit('retry')
}
</script>

<style scoped>
.error-display {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: var(--color-bg-card, #fff);
  border-radius: var(--radius-md, 12px);
  border: 1px solid var(--color-border, #E2E8F0);
}

.error-display.is-compact {
  padding: 12px 16px;
  gap: 12px;
}

.error-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.error-icon svg {
  width: 32px;
  height: 32px;
}

.is-compact .error-icon {
  width: 24px;
  height: 24px;
}

.is-compact .error-icon svg {
  width: 24px;
  height: 24px;
}

/* 类型颜色 */
.error-error .error-icon {
  color: var(--color-danger, #C53030);
}

.error-warning .error-icon {
  color: var(--color-warning, #D69E2E);
}

.error-info .error-icon {
  color: var(--color-info, #4299E1);
}

.error-unknown .error-icon {
  color: var(--color-text-muted, #718096);
}

.error-content {
  flex: 1;
  min-width: 0;
}

.error-title {
  margin: 0 0 8px 0;
  font-size: var(--font-size-lg, 16px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1A365D);
}

.error-message {
  margin: 0;
  font-size: var(--font-size-base, 14px);
  color: var(--color-text-secondary, #4A5568);
  line-height: 1.5;
}

.error-details {
  margin-top: 12px;
  padding: 12px;
  background: var(--color-bg-hover, #EDF2F7);
  border-radius: var(--radius-sm, 8px);
  overflow-x: auto;
}

.error-details pre {
  margin: 0;
  font-family: var(--font-family-mono, monospace);
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-muted, #718096);
  white-space: pre-wrap;
  word-break: break-all;
}

.error-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

/* 暗黑模式适配 */
@media (prefers-color-scheme: dark) {
  .error-display {
    background: var(--color-bg-card, #1A202C);
    border-color: var(--color-border, #2D3748);
  }
  
  .error-title {
    color: var(--color-text-primary, #F7FAFC);
  }
  
  .error-message {
    color: var(--color-text-secondary, #CBD5E0);
  }
  
  .error-details {
    background: var(--color-bg-hover, #2D3748);
  }
  
  .error-details pre {
    color: var(--color-text-muted, #A0AEC0);
  }
}
</style>
