<template>
  <div class="skeleton-card" :style="{ width, height }">
    <!-- 卡片骨架 -->
    <div v-if="type === 'card'" class="skeleton-card-content">
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item variant="image" style="width: 100%; height: 160px;" />
          <div style="padding: 16px;">
            <el-skeleton-item variant="h3" style="width: 80%;" />
            <el-skeleton-item variant="text" style="margin-top: 12px;" />
            <el-skeleton-item variant="text" style="margin-top: 8px; width: 60%;" />
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 列表骨架 -->
    <div v-else-if="type === 'list'" class="skeleton-list">
      <el-skeleton v-for="i in rows" :key="i" animated>
        <template #template>
          <div style="display: flex; align-items: center; padding: 12px 0;">
            <el-skeleton-item variant="circle" style="width: 40px; height: 40px; margin-right: 16px;" />
            <div style="flex: 1;">
              <el-skeleton-item variant="text" style="width: 40%;" />
              <el-skeleton-item variant="text" style="margin-top: 8px; width: 80%;" />
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 表格骨架 -->
    <div v-else-if="type === 'table'" class="skeleton-table">
      <div class="skeleton-table-header">
        <el-skeleton :rows="0" animated>
          <template #template>
            <div style="display: flex; gap: 16px; padding: 12px 0;">
              <el-skeleton-item v-for="i in columns" :key="i" variant="text" :style="{ width: `${100/columns}%` }" />
            </div>
          </template>
        </el-skeleton>
      </div>
      <div class="skeleton-table-body">
        <el-skeleton v-for="i in rows" :key="i" animated>
          <template #template>
            <div style="display: flex; gap: 16px; padding: 16px 0; border-bottom: 1px solid var(--color-border-light);">
              <el-skeleton-item v-for="j in columns" :key="j" variant="text" :style="{ width: `${100/columns}%` }" />
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>
    
    <!-- 详情骨架 -->
    <div v-else-if="type === 'detail'" class="skeleton-detail">
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item variant="image" style="width: 100%; height: 300px; border-radius: 12px;" />
          <div style="padding: 24px 0;">
            <el-skeleton-item variant="h1" style="width: 60%;" />
            <div style="display: flex; gap: 16px; margin-top: 16px;">
              <el-skeleton-item variant="text" style="width: 120px;" />
              <el-skeleton-item variant="text" style="width: 100px;" />
              <el-skeleton-item variant="text" style="width: 80px;" />
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
      <el-skeleton animated>
        <template #template>
          <div style="display: flex; align-items: center;">
            <el-skeleton-item variant="circle" style="width: 56px; height: 56px;" />
            <div style="margin-left: 16px; flex: 1;">
              <el-skeleton-item variant="h2" style="width: 60px;" />
              <el-skeleton-item variant="text" style="margin-top: 8px; width: 80px;" />
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
interface Props {
  type?: 'card' | 'list' | 'table' | 'detail' | 'stat' | 'default'
  rows?: number
  columns?: number
  width?: string
  height?: string
}

withDefaults(defineProps<Props>(), {
  type: 'default',
  rows: 4,
  columns: 4,
  width: '100%',
  height: 'auto'
})
</script>

<style scoped>
.skeleton-card {
  background: var(--color-bg-card);
  border-radius: var(--radius-md);
  padding: 0;
}

.skeleton-card-content {
  overflow: hidden;
}

.skeleton-list {
  padding: var(--spacing-md);
}

.skeleton-table {
  padding: var(--spacing-md);
}

.skeleton-table-header {
  border-bottom: 2px solid var(--color-border);
  margin-bottom: var(--spacing-sm);
}

.skeleton-detail {
  padding: var(--spacing-lg);
}
</style>
