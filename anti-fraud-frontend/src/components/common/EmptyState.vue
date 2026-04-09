<template>
  <div class="empty-state" :class="[`empty-${type}`, { 'empty-compact': compact }]">
    <!-- 动画背景 -->
    <div class="empty-bg">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
    
    <!-- 图片/图标 -->
    <div class="empty-icon">
      <slot name="icon">
        <div class="icon-wrapper" :class="[`icon-${type}`]">
          <el-icon v-if="icon" :size="compact ? 40 : 64">
            <component :is="icon" />
          </el-icon>
          <img v-else-if="image" :src="image" :style="{ width: compact ? '80px' : '120px' }" />
          <el-icon v-else :size="compact ? 32 : 48">
            <component :is="getDefaultIcon" />
          </el-icon>
        </div>
      </slot>
    </div>
    
    <!-- 标题 -->
    <h3 class="empty-title" v-if="title">{{ title }}</h3>
    <h3 class="empty-title" v-else>{{ getDefaultTitle }}</h3>
    
    <!-- 描述 -->
    <p class="empty-description" v-if="description">{{ description }}</p>
    <p class="empty-description" v-else>{{ getDefaultDescription }}</p>
    
    <!-- 操作按钮 -->
    <div class="empty-actions" v-if="$slots.action || actionText">
      <slot name="action">
        <el-button 
          :type="actionType" 
          size="large"
          :icon="actionIcon"
          @click="$emit('action')"
        >
          {{ actionText }}
        </el-button>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Document, Search, WarningFilled, Box, Lock, Connection } from '@element-plus/icons-vue'

interface Props {
  type?: 'default' | 'search' | 'error' | 'network' | 'no-data' | 'no-permission'
  title?: string
  description?: string
  icon?: string
  image?: string
  actionText?: string
  actionType?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  actionIcon?: string
  compact?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'default',
  title: '',
  description: '',
  actionType: 'primary',
  compact: false
})

defineEmits(['action'])

const getDefaultIcon = computed(() => {
  const icons: Record<string, any> = {
    default: Document,
    search: Search,
    error: WarningFilled,
    network: Connection,
    'no-data': Box,
    'no-permission': Lock
  }
  return icons[props.type] || Document
})

const getDefaultTitle = computed(() => {
  const titles: Record<string, string> = {
    default: '暂无内容',
    search: '未找到结果',
    error: '出错了',
    network: '网络连接失败',
    'no-data': '暂无数据',
    'no-permission': '无权限访问'
  }
  return titles[props.type] || '暂无内容'
})

const getDefaultDescription = computed(() => {
  const descriptions: Record<string, string> = {
    default: '这里什么都没有',
    search: '换个关键词试试吧',
    error: '请稍后重试或联系管理员',
    network: '请检查网络连接后重试',
    'no-data': '还没有任何数据',
    'no-permission': '您没有权限访问此内容'
  }
  return descriptions[props.type] || ''
})
</script>

<style scoped>
.empty-state {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  text-align: center;
  overflow: hidden;
}

.empty-compact {
  padding: 40px 24px;
}

/* 背景动画 */
.empty-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  top: -50px;
  right: -50px;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, #f093fb, #f5576c);
  bottom: -30px;
  left: -30px;
  animation-delay: -2s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  top: 50%;
  left: 10%;
  animation-delay: -4s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  25% { transform: translate(10px, -10px) rotate(5deg); }
  50% { transform: translate(0, -20px) rotate(0deg); }
  75% { transform: translate(-10px, -10px) rotate(-5deg); }
}

/* 图标容器 */
.empty-icon {
  margin-bottom: 24px;
  animation: bounceIn 0.6s ease-out;
}

@keyframes bounceIn {
  0% { transform: scale(0.5); opacity: 0; }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); opacity: 1; }
}

.icon-wrapper {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.icon-wrapper::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 50%;
  padding: 3px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.1); opacity: 0.7; }
}

.icon-default {
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  color: #94a3b8;
}

.icon-search {
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #3b82f6;
}

.icon-error {
  background: linear-gradient(135deg, #fee2e2, #fecaca);
  color: #ef4444;
}

.icon-network {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #f59e0b;
}

.icon-no-data {
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  color: #64748b;
}

.icon-no-permission {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #d97706;
}

/* 标题和描述 */
.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
  animation: fadeInUp 0.6s ease-out 0.1s both;
}

.empty-description {
  font-size: 14px;
  color: #64748b;
  max-width: 400px;
  line-height: 1.6;
  margin-bottom: 24px;
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 操作按钮 */
.empty-actions {
  animation: fadeInUp 0.6s ease-out 0.3s both;
}

/* 紧凑模式适配 */
.empty-compact .empty-icon {
  margin-bottom: 16px;
}

.empty-compact .icon-wrapper {
  width: 80px;
  height: 80px;
}

.empty-compact .empty-title {
  font-size: 16px;
}

.empty-compact .empty-description {
  font-size: 13px;
  margin-bottom: 16px;
}
</style>
