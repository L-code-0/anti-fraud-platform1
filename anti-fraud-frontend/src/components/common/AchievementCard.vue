<template>
  <div class="achievement-card" :class="{ unlocked: achievement.isUnlocked }">
    <div class="achievement-icon">
      <span class="icon">{{ achievement.icon }}</span>
      <div v-if="!achievement.isUnlocked" class="lock-overlay">
        <el-icon><Lock /></el-icon>
      </div>
    </div>
    <div class="achievement-info">
      <h4 class="achievement-name">{{ achievement.name }}</h4>
      <p class="achievement-desc">{{ achievement.description }}</p>
      <div v-if="!achievement.isUnlocked" class="progress-bar">
        <el-progress 
          :percentage="Math.round((achievement.progress / achievement.maxProgress) * 100)" 
          :stroke-width="4"
          :show-text="false"
        />
        <span class="progress-text">{{ achievement.progress }} / {{ achievement.maxProgress }}</span>
      </div>
      <div v-else class="unlocked-info">
        <el-icon color="#67c23a"><CircleCheck /></el-icon>
        <span>{{ formatDate(achievement.unlockedAt) }}</span>
      </div>
    </div>
    <div class="achievement-points" :class="{ active: achievement.isUnlocked }">
      +{{ achievement.points }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { Lock, CircleCheck } from '@element-plus/icons-vue'
import type { Achievement } from '@/types'

defineProps<{
  achievement: Achievement
}>()

function formatDate(dateStr?: string): string {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}
</script>

<style scoped>
.achievement-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.achievement-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.achievement-card.unlocked {
  background: linear-gradient(135deg, #f0f9eb 0%, #e1f3d8 100%);
  border: 1px solid #67c23a;
}

.achievement-icon {
  position: relative;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 50%;
  margin-right: 16px;
  flex-shrink: 0;
}

.achievement-card.unlocked .achievement-icon {
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
}

.icon {
  font-size: 28px;
}

.lock-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.achievement-info {
  flex: 1;
  min-width: 0;
}

.achievement-name {
  margin: 0 0 4px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.achievement-desc {
  margin: 0 0 8px;
  font-size: 13px;
  color: #999;
}

.progress-bar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-bar .el-progress {
  flex: 1;
}

.progress-text {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.unlocked-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #67c23a;
}

.achievement-points {
  font-size: 18px;
  font-weight: 700;
  color: #999;
  padding-left: 16px;
}

.achievement-points.active {
  color: #67c23a;
}
</style>
