<template>
  <div class="achievement-page">
    <!-- 头部 -->
    <div class="header">
      <h1>成就中心</h1>
      <div class="achievement-summary">
        <div class="summary-item">
          <span class="summary-value">{{ unlockedCount }}</span>
          <span class="summary-label">已解锁</span>
        </div>
        <div class="summary-item">
          <span class="summary-value">{{ achievementStore.achievements.length - unlockedCount }}</span>
          <span class="summary-label">进行中</span>
        </div>
        <div class="summary-item">
          <span class="summary-value">+{{ totalPoints }}</span>
          <span class="summary-label">积分奖励</span>
        </div>
      </div>
    </div>

    <!-- 进度概览 -->
    <div class="progress-overview">
      <div class="progress-ring">
        <svg viewBox="0 0 100 100">
          <circle cx="50" cy="50" r="45" class="ring-bg" />
          <circle 
            cx="50" cy="50" r="45" 
            class="ring-progress"
            :style="{ strokeDashoffset: ringOffset }"
          />
        </svg>
        <div class="ring-text">
          <span class="percentage">{{ achievementRate }}%</span>
          <span class="label">完成度</span>
        </div>
      </div>
      <div class="progress-detail">
        <h3>总体进度</h3>
        <p>您已完成 {{ unlockedCount }} / {{ achievementStore.achievements.length }} 个成就</p>
        <el-progress 
          :percentage="achievementRate" 
          :stroke-width="10"
          :show-text="false"
        />
        <div class="category-progress">
          <div 
            v-for="category in categoryStats" 
            :key="category.key"
            class="category-item"
          >
            <span class="category-name">{{ category.name }}</span>
            <el-progress 
              :percentage="category.percentage"
              :stroke-width="6"
              :show-text="false"
              :status="category.percentage === 100 ? 'success' : undefined"
            />
            <span class="category-count">{{ category.unlocked }}/{{ category.total }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分类标签 -->
    <div class="category-tabs">
      <el-radio-group v-model="activeCategory" @change="handleCategoryChange">
        <el-radio-button value="all">全部</el-radio-button>
        <el-radio-button value="unlocked">已解锁</el-radio-button>
        <el-radio-button value="learning">学习成就</el-radio-button>
        <el-radio-button value="test">考试成就</el-radio-button>
        <el-radio-button value="simulation">演练成就</el-radio-button>
        <el-radio-button value="social">社交成就</el-radio-button>
        <el-radio-button value="special">特殊成就</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 成就列表 -->
    <div class="achievement-list" v-loading="loading">
      <template v-if="filteredAchievements.length > 0">
        <div 
          v-for="achievement in filteredAchievements" 
          :key="achievement.id"
          class="achievement-card"
          :class="{ unlocked: achievement.isUnlocked }"
        >
          <div class="achievement-icon" :class="{ locked: !achievement.isUnlocked }">
            <span class="icon">{{ achievement.icon }}</span>
            <div v-if="!achievement.isUnlocked" class="lock-overlay">
              <el-icon><Lock /></el-icon>
            </div>
          </div>
          <div class="achievement-content">
            <div class="achievement-header">
              <h3 class="achievement-name">{{ achievement.name }}</h3>
              <span class="achievement-points" :class="{ active: achievement.isUnlocked }">
                +{{ achievement.points }}
              </span>
            </div>
            <p class="achievement-desc">{{ achievement.description }}</p>
            <div class="achievement-condition">
              <el-icon><InfoFilled /></el-icon>
              <span>{{ achievement.condition }}</span>
            </div>
            <div v-if="!achievement.isUnlocked" class="progress-section">
              <el-progress 
                :percentage="Math.round((achievement.progress / achievement.maxProgress) * 100)" 
                :stroke-width="6"
                :show-text="false"
              />
              <span class="progress-text">
                {{ achievement.progress }} / {{ achievement.maxProgress }}
              </span>
            </div>
            <div v-else class="unlocked-section">
              <el-icon color="#67c23a"><CircleCheckFilled /></el-icon>
              <span class="unlocked-time">
                已解锁于 {{ formatDate(achievement.unlockedAt) }}
              </span>
            </div>
          </div>
        </div>
      </template>
      <el-empty v-else description="暂无成就" />
    </div>

    <!-- 近期解锁 -->
    <div v-if="recentUnlocked.length > 0" class="recent-section">
      <h2>近期解锁</h2>
      <div class="recent-list">
        <div 
          v-for="achievement in recentUnlocked" 
          :key="achievement.id"
          class="recent-item"
        >
          <span class="recent-icon">{{ achievement.icon }}</span>
          <span class="recent-name">{{ achievement.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Lock, InfoFilled, CircleCheckFilled } from '@element-plus/icons-vue'
import { useAchievementStore } from '@/stores/achievement'
import type { Achievement, AchievementCategory } from '@/types'

const achievementStore = useAchievementStore()

const activeCategory = ref('all')

const loading = computed(() => achievementStore.loading)
const unlockedCount = computed(() => achievementStore.unlockedAchievements.length)
const totalPoints = computed(() => achievementStore.totalPoints)
const achievementRate = computed(() => achievementStore.achievementRate)

const ringOffset = computed(() => {
  const circumference = 2 * Math.PI * 45
  return circumference - (achievementRate.value / 100) * circumference
})

const recentUnlocked = computed(() => 
  achievementStore.unlockedAchievements
    .filter(a => a.unlockedAt)
    .sort((a, b) => new Date(b.unlockedAt!).getTime() - new Date(a.unlockedAt!).getTime())
    .slice(0, 5)
)

const categoryStats = computed(() => {
  const categories: { key: AchievementCategory; name: string }[] = [
    { key: 'learning', name: '学习' },
    { key: 'test', name: '考试' },
    { key: 'simulation', name: '演练' },
    { key: 'social', name: '社交' },
    { key: 'special', name: '特殊' }
  ]
  
  return categories.map(cat => {
    const all = achievementStore.achievements.filter(a => a.category === cat.key)
    const unlocked = all.filter(a => a.isUnlocked)
    return {
      ...cat,
      total: all.length,
      unlocked: unlocked.length,
      percentage: all.length > 0 ? Math.round((unlocked.length / all.length) * 100) : 0
    }
  })
})

const filteredAchievements = computed(() => {
  let achievements = achievementStore.achievements
  
  switch (activeCategory.value) {
    case 'unlocked':
      achievements = achievements.filter(a => a.isUnlocked)
      break
    case 'learning':
    case 'test':
    case 'simulation':
    case 'social':
    case 'special':
      achievements = achievements.filter(a => a.category === activeCategory.value)
      break
  }
  
  // 已解锁的排在前面
  return [...achievements].sort((a, b) => {
    if (a.isUnlocked && !b.isUnlocked) return -1
    if (!a.isUnlocked && b.isUnlocked) return 1
    return b.points - a.points
  })
})

function handleCategoryChange() {
  // 分类切换后的处理
}

function formatDate(dateStr?: string): string {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

onMounted(() => {
  achievementStore.fetchAchievements()
})
</script>

<style scoped>
.achievement-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.header {
  text-align: center;
  margin-bottom: 32px;
}

.header h1 {
  margin: 0 0 20px;
  font-size: 28px;
}

.achievement-summary {
  display: flex;
  justify-content: center;
  gap: 48px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.summary-value {
  font-size: 32px;
  font-weight: 700;
  color: #409eff;
}

.summary-label {
  font-size: 14px;
  color: #999;
}

/* 进度概览 */
.progress-overview {
  display: flex;
  gap: 32px;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  margin-bottom: 32px;
  color: white;
}

.progress-ring {
  position: relative;
  width: 160px;
  height: 160px;
  flex-shrink: 0;
}

.progress-ring svg {
  transform: rotate(-90deg);
}

.ring-bg {
  fill: none;
  stroke: rgba(255, 255, 255, 0.2);
  stroke-width: 10;
}

.ring-progress {
  fill: none;
  stroke: white;
  stroke-width: 10;
  stroke-linecap: round;
  stroke-dasharray: 283;
  transition: stroke-dashoffset 1s ease;
}

.ring-text {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.ring-text .percentage {
  font-size: 36px;
  font-weight: 700;
}

.ring-text .label {
  font-size: 14px;
  opacity: 0.8;
}

.progress-detail {
  flex: 1;
}

.progress-detail h3 {
  margin: 0 0 8px;
  font-size: 20px;
}

.progress-detail p {
  margin: 0 0 16px;
  opacity: 0.9;
  font-size: 14px;
}

.progress-detail .el-progress {
  margin-bottom: 20px;
}

.progress-detail .el-progress-bar__outer {
  background: rgba(255, 255, 255, 0.2);
}

.category-progress {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-name {
  width: 50px;
  font-size: 13px;
  opacity: 0.9;
}

.category-item :deep(.el-progress) {
  flex: 1;
}

.category-item :deep(.el-progress-bar__outer) {
  background: rgba(255, 255, 255, 0.2);
}

.category-count {
  width: 40px;
  font-size: 12px;
  opacity: 0.8;
  text-align: right;
}

/* 分类标签 */
.category-tabs {
  margin-bottom: 24px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.category-tabs .el-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 成就列表 */
.achievement-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.achievement-card {
  display: flex;
  gap: 20px;
  padding: 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s, box-shadow 0.3s;
}

.achievement-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.achievement-card.unlocked {
  background: linear-gradient(135deg, #f0f9eb 0%, #e8f5e9 100%);
  border: 1px solid #67c23a;
}

.achievement-icon {
  position: relative;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  flex-shrink: 0;
}

.achievement-icon.locked {
  background: #f5f7fa;
}

.achievement-icon .icon {
  font-size: 40px;
}

.achievement-icon.locked .icon {
  filter: grayscale(100%);
  opacity: 0.5;
}

.lock-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 24px;
}

.achievement-content {
  flex: 1;
  min-width: 0;
}

.achievement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.achievement-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.achievement-points {
  font-size: 20px;
  font-weight: 700;
  color: #999;
}

.achievement-points.active {
  color: #67c23a;
}

.achievement-desc {
  margin: 0 0 8px;
  font-size: 14px;
  color: #666;
}

.achievement-condition {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
}

.progress-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-section :deep(.el-progress) {
  flex: 1;
}

.progress-text {
  font-size: 13px;
  color: #999;
  white-space: nowrap;
}

.unlocked-section {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #67c23a;
}

/* 近期解锁 */
.recent-section {
  margin-top: 40px;
}

.recent-section h2 {
  margin: 0 0 16px;
  font-size: 18px;
}

.recent-list {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.recent-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.recent-icon {
  font-size: 20px;
}

.recent-name {
  font-size: 14px;
  color: #333;
}

@media (max-width: 768px) {
  .achievement-page {
    padding: 16px;
  }
  
  .progress-overview {
    flex-direction: column;
    align-items: center;
  }
  
  .achievement-card {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .achievement-header {
    flex-direction: column;
    gap: 8px;
  }
  
  .category-progress {
    display: none;
  }
}
</style>
