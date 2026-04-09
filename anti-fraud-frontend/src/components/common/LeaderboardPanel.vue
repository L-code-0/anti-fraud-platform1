<template>
  <div class="leaderboard-container">
    <!-- 切换类型 -->
    <div class="leaderboard-tabs">
      <el-radio-group v-model="activeType" @change="handleTypeChange">
        <el-radio-button value="points">
          <el-icon><Star /></el-icon> 积分榜
        </el-radio-button>
        <el-radio-button value="learning">
          <el-icon><Reading /></el-icon> 学习榜
        </el-radio-button>
        <el-radio-button value="test">
          <el-icon><Document /></el-icon> 考试榜
        </el-radio-button>
        <el-radio-button value="simulation">
          <el-icon><Monitor /></el-icon> 演练榜
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 顶部三名 -->
    <div class="top-three" v-if="!loading">
      <div 
        v-for="(entry, index) in topThree" 
        :key="entry.userId"
        class="top-item"
        :class="`rank-${index + 1}`"
      >
        <div class="rank-badge">
          <span v-if="index === 0" class="medal gold">🥇</span>
          <span v-else-if="index === 1" class="medal silver">🥈</span>
          <span v-else class="medal bronze">🥉</span>
        </div>
        <el-avatar :size="60" :src="entry.userAvatar" class="user-avatar">
          {{ entry.userName.charAt(0) }}
        </el-avatar>
        <div class="user-info">
          <span class="user-name">{{ entry.userName }}</span>
          <span class="user-institution">{{ entry.institution }}</span>
        </div>
        <div class="user-score">
          <span class="score-value">{{ formatScore(entry.points, activeType) }}</span>
          <span class="score-label">{{ getScoreLabel(activeType) }}</span>
        </div>
      </div>
    </div>

    <!-- 排行榜列表 -->
    <div class="leaderboard-list" v-loading="loading">
      <template v-if="!loading">
        <div 
          v-for="entry in restEntries" 
          :key="entry.userId"
          class="list-item"
          :class="{ 'is-current-user': entry.isCurrentUser }"
        >
          <div class="rank">
            <span class="rank-number">{{ entry.rank }}</span>
          </div>
          <el-avatar :size="40" :src="entry.userAvatar" class="user-avatar">
            {{ entry.userName.charAt(0) }}
          </el-avatar>
          <div class="user-info">
            <span class="user-name">
              {{ entry.userName }}
              <el-tag v-if="entry.isCurrentUser" size="small" type="primary">我</el-tag>
            </span>
            <span class="user-institution">{{ entry.institution }}</span>
          </div>
          <div class="user-level">
            <el-tag size="small" type="warning">Lv.{{ entry.level }}</el-tag>
          </div>
          <div class="user-score">
            <span class="score-value">{{ formatScore(entry.points, activeType) }}</span>
            <span class="score-label">{{ getScoreLabel(activeType) }}</span>
          </div>
        </div>
      </template>
      
      <el-skeleton v-else :rows="8" animated />
    </div>

    <!-- 我的排名 -->
    <div class="my-rank" v-if="myRank > 0">
      <div class="my-rank-info">
        <span class="label">我的排名</span>
        <span class="rank-value">第 {{ myRank }} 名</span>
        <span class="divider">/</span>
        <span class="total">{{ totalUsers }} 人</span>
      </div>
      <el-progress 
        :percentage="Math.round((myRank / totalUsers) * 100)" 
        :show-text="false"
        :stroke-width="6"
        color="#409eff"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Star, Reading, Document, Monitor } from '@element-plus/icons-vue'
import { useAchievementStore } from '@/stores/achievement'
import type { LeaderboardType } from '@/types'

const achievementStore = useAchievementStore()

const activeType = ref<LeaderboardType>('points')
const myRank = ref(0)
const totalUsers = ref(100)

const loading = computed(() => achievementStore.loading)

const topThree = computed(() => achievementStore.leaderboard.slice(0, 3))
const restEntries = computed(() => achievementStore.leaderboard.slice(3))

// 获取分数标签
function getScoreLabel(type: LeaderboardType): string {
  const labels = {
    points: '积分',
    learning: '时长',
    test: '分数',
    simulation: '得分'
  }
  return labels[type]
}

// 格式化分数
function formatScore(score: number, type: LeaderboardType): string {
  if (type === 'points') {
    return score.toLocaleString()
  }
  return score.toString()
}

// 切换类型
async function handleTypeChange(type: LeaderboardType) {
  await achievementStore.fetchLeaderboard(type)
  await fetchMyRank(type)
}

// 获取我的排名
async function fetchMyRank(type: LeaderboardType) {
  const result = await achievementStore.getUserRank(type)
  if (result) {
    myRank.value = result.rank
    totalUsers.value = result.total
  }
}

onMounted(async () => {
  await achievementStore.fetchLeaderboard('points')
  await fetchMyRank('points')
})
</script>

<style scoped>
.leaderboard-container {
  padding: 20px;
}

.leaderboard-tabs {
  margin-bottom: 24px;
  text-align: center;
}

.leaderboard-tabs .el-radio-group {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

/* 顶部三名 */
.top-three {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 16px;
  margin-bottom: 32px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
}

.top-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.top-item:hover {
  transform: translateY(-4px);
}

.top-item.rank-1 {
  order: 2;
  transform: scale(1.1);
  z-index: 2;
}

.top-item.rank-1:hover {
  transform: scale(1.15) translateY(-4px);
}

.top-item.rank-2 {
  order: 1;
}

.top-item.rank-3 {
  order: 3;
}

.rank-badge {
  margin-bottom: 8px;
}

.medal {
  font-size: 32px;
}

.user-avatar {
  border: 3px solid #f0f0f0;
}

.user-info {
  text-align: center;
  margin: 8px 0;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
  color: #333;
}

.user-institution {
  font-size: 12px;
  color: #999;
}

.user-score {
  text-align: center;
}

.score-value {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
}

.score-label {
  font-size: 12px;
  color: #999;
}

/* 排行榜列表 */
.leaderboard-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.list-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.list-item:last-child {
  border-bottom: none;
}

.list-item:hover {
  background-color: #f5f7fa;
}

.list-item.is-current-user {
  background-color: #ecf5ff;
}

.list-item.is-current-user:hover {
  background-color: #d9ecff;
}

.rank {
  width: 32px;
  text-align: center;
}

.rank-number {
  font-size: 16px;
  font-weight: 600;
  color: #666;
}

.list-item:nth-child(1) .rank-number { color: #c9a227; }
.list-item:nth-child(2) .rank-number { color: #8b8b8b; }
.list-item:nth-child(3) .rank-number { color: #cd7f32; }

.list-item .user-avatar {
  margin: 0 12px;
}

.list-item .user-info {
  flex: 1;
  text-align: left;
}

.list-item .user-level {
  margin-right: 16px;
}

.list-item .user-score {
  text-align: right;
  min-width: 60px;
}

/* 我的排名 */
.my-rank {
  margin-top: 24px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.my-rank-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.my-rank-info .label {
  color: #666;
  margin-right: 8px;
}

.my-rank-info .rank-value {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
}

.my-rank-info .divider {
  color: #ccc;
  margin: 0 4px;
}

.my-rank-info .total {
  color: #999;
}

/* 响应式 */
@media (max-width: 768px) {
  .top-three {
    flex-direction: column;
    align-items: center;
  }
  
  .top-item {
    width: 100%;
    max-width: 280px;
  }
  
  .top-item.rank-1 {
    order: 1;
    transform: none;
  }
  
  .list-item {
    padding: 8px 12px;
  }
  
  .list-item .user-level {
    display: none;
  }
}
</style>
