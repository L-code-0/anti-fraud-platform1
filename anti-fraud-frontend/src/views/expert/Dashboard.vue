<template>
  <div class="expert-dashboard">
    <h2 class="page-title">专家中心</h2>
    
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <el-avatar :size="64" :src="userStore.userInfo?.avatar">
          {{ userStore.userInfo?.realName?.charAt(0) || 'E' }}
        </el-avatar>
        <div class="welcome-text">
          <h3>欢迎回来，{{ userStore.userInfo?.realName || '专家' }}！</h3>
          <p>您可以在这里发布反诈案例分析、提供专家建议，帮助更多人提升防骗意识。</p>
        </div>
      </div>
    </el-card>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #409eff;">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.analysisCount }}</span>
            <span class="stat-label">案例分析</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #67c23a;">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.adviceCount }}</span>
            <span class="stat-label">专家建议</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #e6a23c;">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.viewCount }}</span>
            <span class="stat-label">阅读量</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #f56c6c;">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.thumbCount }}</span>
            <span class="stat-label">获赞数</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 快捷操作 -->
    <el-card class="action-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>
      <div class="action-buttons">
        <el-button type="primary" @click="$router.push('/expert/analysis')">
          <el-icon><Plus /></el-icon>
          发布案例分析
        </el-button>
        <el-button type="success" @click="$router.push('/expert/advice')">
          <el-icon><Plus /></el-icon>
          发布专家建议
        </el-button>
        <el-button @click="$router.push('/analysis')">
          <el-icon><TrendCharts /></el-icon>
          查看学习报告
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { Document, ChatDotRound, View, Star, Plus, TrendCharts } from '@element-plus/icons-vue'

const userStore = useUserStore()

const stats = ref({
  analysisCount: 0,
  adviceCount: 0,
  viewCount: 0,
  thumbCount: 0
})

onMounted(() => {
  // 加载统计数据
  stats.value = {
    analysisCount: 5,
    adviceCount: 12,
    viewCount: 1234,
    thumbCount: 89
  }
})
</script>

<style scoped>
.expert-dashboard {
  max-width: 1200px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: var(--text-primary);
}

.welcome-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.welcome-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome-text h3 {
  margin: 0 0 8px;
  font-size: 20px;
  color: var(--text-primary);
}

.welcome-text p {
  margin: 0;
  color: var(--text-secondary);
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary);
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.action-card {
  border-radius: 12px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>