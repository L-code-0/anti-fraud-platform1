<template>
  <div class="skeleton-wrapper" :style="wrapperStyle">
    <!-- 骨架屏动画 -->
    <div v-if="animated" class="skeleton-animated-layer" :style="animatedStyle"></div>
    
    <!-- 卡片骨架 -->
    <div v-if="type === 'card'" class="skeleton-card">
      <el-skeleton animated :rows="0">
        <template #template>
          <div class="skeleton-card-image" :style="imageStyle"></div>
          <div class="skeleton-card-body">
            <el-skeleton-item variant="h3" style="width: 80%;" />
            <el-skeleton-item variant="text" style="margin-top: 12px;" />
            <el-skeleton-item variant="text" style="margin-top: 8px; width: 60%;" />
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 列表骨架 -->
    <div v-else-if="type === 'list'" class="skeleton-list">
      <div v-for="i in rows" :key="i" class="skeleton-list-item">
        <el-skeleton animated :rows="0">
          <template #template>
            <div class="skeleton-list-row">
              <el-skeleton-item variant="circle" style="width: 40px; height: 40px;" />
              <div class="skeleton-list-content">
                <el-skeleton-item variant="text" style="width: 40%;" />
                <el-skeleton-item variant="text" style="margin-top: 8px; width: 80%;" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>
    
    <!-- 表格骨架 -->
    <div v-else-if="type === 'table'" class="skeleton-table">
      <div class="skeleton-table-header">
        <el-skeleton animated :rows="0">
          <template #template>
            <div class="skeleton-table-row header-row">
              <div v-for="i in columns" :key="i" class="skeleton-table-cell" :style="{ width: `${100/columns}%` }"></div>
            </div>
          </template>
        </el-skeleton>
      </div>
      <div class="skeleton-table-body">
        <div v-for="i in rows" :key="i" class="skeleton-table-row">
          <div v-for="j in columns" :key="j" class="skeleton-table-cell" :style="{ width: `${100/columns}%` }"></div>
        </div>
      </div>
    </div>
    
    <!-- 详情骨架 -->
    <div v-else-if="type === 'detail'" class="skeleton-detail">
      <el-skeleton animated :rows="0">
        <template #template>
          <div class="skeleton-detail-image" :style="imageStyle"></div>
          <div class="skeleton-detail-content">
            <el-skeleton-item variant="h1" style="width: 60%;" />
            <div class="skeleton-detail-meta">
              <el-skeleton-item variant="text" style="width: 100px;" />
              <el-skeleton-item variant="text" style="width: 80px;" />
              <el-skeleton-item variant="text" style="width: 120px;" />
            </div>
            <el-skeleton-item variant="text" style="margin-top: 24px;" />
            <el-skeleton-item variant="text" style="margin-top: 8px;" />
            <el-skeleton-item variant="text" style="margin-top: 8px; width: 80%;" />
            <el-skeleton-item variant="text" style="margin-top: 8px;" />
            <el-skeleton-item variant="text" style="margin-top: 8px; width: 60%;" />
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 统计卡片骨架 -->
    <div v-else-if="type === 'stat'" class="skeleton-stat">
      <el-skeleton animated :rows="0">
        <template #template>
          <div class="skeleton-stat-row">
            <el-skeleton-item variant="circle" style="width: 56px; height: 56px;" />
            <div class="skeleton-stat-content">
              <el-skeleton-item variant="h2" style="width: 80px;" />
              <el-skeleton-item variant="text" style="margin-top: 8px; width: 100px;" />
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 用户卡片骨架 -->
    <div v-else-if="type === 'user'" class="skeleton-user">
      <el-skeleton animated :rows="0">
        <template #template>
          <div class="skeleton-user-row">
            <el-skeleton-item variant="circle" style="width: 48px; height: 48px;" />
            <div class="skeleton-user-info">
              <el-skeleton-item variant="text" style="width: 120px;" />
              <el-skeleton-item variant="text" style="margin-top: 6px; width: 80px;" />
            </div>
            <div class="skeleton-user-action">
              <el-skeleton-item variant="button" style="width: 80px; height: 32px;" />
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 搜索栏骨架 -->
    <div v-else-if="type === 'search'" class="skeleton-search">
      <el-skeleton animated :rows="0">
        <template #template>
          <div class="skeleton-search-row">
            <el-skeleton-item variant="input" style="width: 300px; height: 40px;" />
            <el-skeleton-item variant="button" style="width: 80px; height: 40px; margin-left: 12px;" />
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 标签页骨架 -->
    <div v-else-if="type === 'tabs'" class="skeleton-tabs">
      <el-skeleton animated :rows="0">
        <template #template>
          <div class="skeleton-tabs-header">
            <el-skeleton-item variant="text" style="width: 80px; height: 24px; margin-right: 16px;" />
            <el-skeleton-item variant="text" style="width: 80px; height: 24px; margin-right: 16px;" />
            <el-skeleton-item variant="text" style="width: 80px; height: 24px;" />
          </div>
          <div class="skeleton-tabs-content">
            <el-skeleton-item variant="text" style="width: 100%; height: 200px;" />
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 图表骨架 -->
    <div v-else-if="type === 'chart'" class="skeleton-chart">
      <el-skeleton animated :rows="0">
        <template #template>
          <div class="skeleton-chart-header">
            <el-skeleton-item variant="text" style="width: 150px;" />
            <el-skeleton-item variant="text" style="width: 100px;" />
          </div>
          <div class="skeleton-chart-body">
            <div class="skeleton-chart-bars">
              <div v-for="i in 7" :key="i" class="skeleton-chart-bar" :style="{ height: `${Math.random() * 60 + 40}%` }"></div>
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 默认骨架 -->
    <el-skeleton v-else :rows="rows" animated />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  type?: 'card' | 'list' | 'table' | 'detail' | 'stat' | 'user' | 'search' | 'tabs' | 'chart' | 'default'
  rows?: number
  columns?: number
  width?: string
  height?: string
  imageHeight?: string
  animated?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'default',
  rows: 4,
  columns: 4,
  width: '100%',
  height: 'auto',
  imageHeight: '160px',
  animated: true
})

const wrapperStyle = computed(() => ({
  width: props.width,
  height: props.height
}))

const imageStyle = computed(() => ({
  height: props.imageHeight
}))

const animatedStyle = computed(() => ({
  background: `var(--skeleton-shimmer, linear-gradient(90deg, var(--skeleton-base, #E2E8F0) 25%, var(--skeleton-highlight, #EDF2F7) 50%, var(--skeleton-base, #E2E8F0) 75%))`
}))
</script>

<style scoped>
.skeleton-wrapper {
  position: relative;
  overflow: hidden;
  background: var(--color-bg-card, #fff);
  border-radius: var(--radius-md, 12px);
}

.skeleton-animated-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-size: 200% 100%;
  animation: skeleton-shimmer 1.5s ease-in-out infinite;
}

@keyframes skeleton-shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* 卡片骨架 */
.skeleton-card {
  overflow: hidden;
}

.skeleton-card-image {
  width: 100%;
  background: var(--skeleton-base, #E2E8F0);
}

.skeleton-card-body {
  padding: 16px;
}

/* 列表骨架 */
.skeleton-list {
  padding: 8px 0;
}

.skeleton-list-item {
  padding: 12px 16px;
  border-bottom: 1px solid var(--color-border-light, #EDF2F7);
}

.skeleton-list-item:last-child {
  border-bottom: none;
}

.skeleton-list-row {
  display: flex;
  align-items: center;
}

.skeleton-list-content {
  margin-left: 16px;
  flex: 1;
}

/* 表格骨架 */
.skeleton-table {
  padding: 0;
}

.skeleton-table-header {
  border-bottom: 2px solid var(--color-border, #E2E8F0);
}

.skeleton-table-body {
  padding-top: 8px;
}

.skeleton-table-row {
  display: flex;
  gap: 16px;
  padding: 12px 16px;
}

.header-row {
  font-weight: 600;
}

.skeleton-table-cell {
  height: 16px;
  background: var(--skeleton-base, #E2E8F0);
  border-radius: 4px;
}

/* 详情骨架 */
.skeleton-detail {
  padding: 24px;
}

.skeleton-detail-image {
  width: 100%;
  background: var(--skeleton-base, #E2E8F0);
  border-radius: var(--radius-md, 12px);
}

.skeleton-detail-content {
  padding-top: 24px;
}

.skeleton-detail-meta {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

/* 统计卡片骨架 */
.skeleton-stat-row {
  display: flex;
  align-items: center;
}

.skeleton-stat-content {
  margin-left: 16px;
  flex: 1;
}

/* 用户卡片骨架 */
.skeleton-user-row {
  display: flex;
  align-items: center;
  padding: 12px 16px;
}

.skeleton-user-info {
  margin-left: 16px;
  flex: 1;
}

.skeleton-user-action {
  margin-left: auto;
}

/* 搜索栏骨架 */
.skeleton-search-row {
  display: flex;
  align-items: center;
}

/* 标签页骨架 */
.skeleton-tabs-header {
  display: flex;
  margin-bottom: 16px;
}

.skeleton-tabs-content {
  margin-top: 16px;
}

/* 图表骨架 */
.skeleton-chart {
  padding: 16px;
}

.skeleton-chart-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
}

.skeleton-chart-body {
  height: 200px;
  display: flex;
  align-items: flex-end;
}

.skeleton-chart-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  gap: 8px;
}

.skeleton-chart-bar {
  flex: 1;
  background: var(--skeleton-base, #E2E8F0);
  border-radius: 4px 4px 0 0;
  min-height: 40px;
}

/* 暗黑模式适配 */
@media (prefers-color-scheme: dark) {
  .skeleton-wrapper {
    background: var(--color-bg-card, #1A202C);
  }
  
  .skeleton-table-row {
    border-color: var(--color-border, #2D3748);
  }
  
  .skeleton-user-row {
    border-color: var(--color-border, #2D3748);
  }
  
  .skeleton-list-item {
    border-color: var(--color-border, #2D3748);
  }
}
</style>
