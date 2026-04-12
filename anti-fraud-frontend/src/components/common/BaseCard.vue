<template>
  <div class="base-card" :class="cardClasses">
    <!-- 加载状态 -->
    <div v-if="loading" class="card-loading">
      <el-skeleton animated>
        <el-skeleton-item variant="h3" style="width: 50%" />
        <el-skeleton-item variant="p" style="width: 80%" />
        <el-skeleton-item variant="p" style="width: 60%" />
      </el-skeleton>
    </div>
    
    <!-- 错误状态 -->
    <div v-else-if="error" class="card-error">
      <el-empty
        description="
          <p class="error-message">{{ error }}</p>
        "
      >
        <el-button type="primary" @click="$emit('retry')">重试</el-button>
      </el-empty>
    </div>
    
    <!-- 正常状态 -->
    <div v-else class="card-content">
      <!-- 卡片标题 -->
      <div v-if="title" class="card-header">
        <h3 class="card-title">{{ title }}</h3>
        <div class="card-header-actions">
          <slot name="header-actions" />
        </div>
      </div>
      
      <!-- 卡片内容 -->
      <div class="card-body">
        <slot />
      </div>
      
      <!-- 卡片底部 -->
      <div v-if="$slots.footer" class="card-footer">
        <slot name="footer" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  title?: string
  loading?: boolean
  error?: string
  bordered?: boolean
  shadow?: 'none' | 'sm' | 'md' | 'lg'
  padding?: 'none' | 'sm' | 'md' | 'lg'
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  loading: false,
  error: '',
  bordered: false,
  shadow: 'md',
  padding: 'md'
})

defineEmits(['retry'])

const cardClasses = computed(() => ({
  'base-card-bordered': props.bordered,
  [`base-card-shadow-${props.shadow}`]: true,
  [`base-card-padding-${props.padding}`]: true
}))
</script>

<style scoped>
.base-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all var(--transition-normal);
}

/* 边框样式 */
.base-card-bordered {
  border: 1px solid var(--border-color);
}

/* 阴影样式 */
.base-card-shadow-none {
  box-shadow: none;
}

.base-card-shadow-sm {
  box-shadow: var(--shadow-sm);
}

.base-card-shadow-md {
  box-shadow: var(--shadow-md);
}

.base-card-shadow-lg {
  box-shadow: var(--shadow-lg);
}

/* 内边距样式 */
.base-card-padding-none .card-content {
  padding: 0;
}

.base-card-padding-sm .card-content {
  padding: var(--spacing-3);
}

.base-card-padding-md .card-content {
  padding: var(--spacing-4);
}

.base-card-padding-lg .card-content {
  padding: var(--spacing-6);
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-4);
  padding-bottom: var(--spacing-3);
  border-bottom: 1px solid var(--border-light);
}

.card-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin: 0;
}

.card-header-actions {
  display: flex;
  gap: var(--spacing-2);
}

/* 卡片内容 */
.card-body {
  color: var(--text-regular);
  line-height: var(--line-height-relaxed);
}

/* 卡片底部 */
.card-footer {
  margin-top: var(--spacing-4);
  padding-top: var(--spacing-3);
  border-top: 1px solid var(--border-light);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-2);
}

/* 加载状态 */
.card-loading {
  padding: var(--spacing-6);
}

/* 错误状态 */
.card-error {
  padding: var(--spacing-6);
}

.error-message {
  color: var(--danger-color);
  margin: 0;
}

/* 悬停效果 */
.base-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

/* 响应式 */
@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-2);
  }
  
  .card-header-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>