<template>
  <div class="leaderboard-page">
    <!-- 头部 -->
    <div class="header">
      <h1>排行榜</h1>
      <div class="header-tabs">
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
    </div>

    <!-- 我的排名 -->
    <div class="my-rank-card" v-if="myRank > 0">
      <div class="my-rank-info">
        <el-avatar :size="50" class="my-avatar">
          {{ userStore.userInfo?.realName?.charAt(0) || '我' }}
        </el-avatar>
        <div class="my-info">
          <span class="my-name">{{ userStore.userInfo?.realName || '我' }}</span>
          <span class="my-level">Lv.{{ userStore.userInfo?.level || 1 }}</span>
        </div>
        <div class="my-rank-detail">
          <span class="rank-label">我的排名</span>
          <span class="rank-value">第 {{ myRank }} 名</span>
          <span class="rank-total">/ {{ totalUsers }}人</span>
        </div>
      </div>
      <div class="my-rank-progress">
        <el-progress 
          :percentage="Math.round((1 - myRank / totalUsers) * 100)" 
          :stroke-width="8"
          :show-text="false"
        />
        <span class="progress-label">超过 {{ Math.round((1 - myRank / totalUsers) * 100) }}% 的用户</span>
      </div>
    </div>

    <!-- 顶部三名 -->
    <div class="top-three-container" v-if="!loading && achievementStore.leaderboard.length > 0">
      <div class="top-three">
        <div 
          v-for="(entry, index) in topThree" 
          :key="entry.userId"
          class="top-item"
          :class="`rank-${index + 1}`"
        >
          <div class="rank-badge">
            <span v-if="index === 0" class="medal">🥇</span>
            <span v-else-if="index === 1" class="medal">🥈</span>
            <span v-else class="medal">🥉</span>
          </div>
          <div class="avatar-wrapper">
            <el-avatar :size="80" :src="entry.userAvatar" class="user-avatar">
              {{ entry.userName.charAt(0) }}
            </el-avatar>
            <div v-if="index === 0" class="crown">👑</div>
          </div>
          <div class="user-info">
            <span class="user-name">{{ entry.userName }}</span>
            <span class="user-institution">{{ entry.institution }}</span>
          </div>
          <div class="user-stats">
            <span class="level-badge">Lv.{{ entry.level }}</span>
            <span class="score">{{ formatScore(entry.points, activeType) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 排行榜列表 -->
    <div class="leaderboard-list" v-loading="loading">
      <template v-if="!loading">
        <div 
          v-for="(entry, index) in restEntries" 
          :key="entry.userId"
          class="list-item"
          :class="{ 'is-current-user': entry.isCurrentUser }"
        >
          <div class="rank">
            <span v-if="entry.rank <= 10" class="rank-top">{{ entry.rank }}</span>
            <span v-else class="rank-number">{{ entry.rank }}</span>
          </div>
          <el-avatar :size="44" :src="entry.userAvatar" class="user-avatar">
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
          <div class="user-badge" v-if="entry.rank <= 3">
            <span class="badge-icon">🎖️</span>
          </div>
        </div>
      </template>
      
      <el-skeleton v-else :rows="10" animated />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Star, Reading, Document, Monitor } from '@element-plus/icons-vue'
import { useAchievementStore } from '@/stores/achievement'
import { useUserStore } from '@/stores/user'
import type { LeaderboardType } from '@/types'

const achievementStore = useAchievementStore()
const userStore = useUserStore()

const activeType = ref<LeaderboardType>('points')
const currentPage = ref(1)
const pageSize = ref(20)
const myRank = ref(0)
const totalUsers = ref(100)

const loading = computed(() => achievementStore.loading)
const total = computed(() => achievementStore.leaderboard.length)
const topThree = computed(() => achievementStore.leaderboard.slice(0, 3))
const restEntries = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return achievementStore.leaderboard.slice(start, end).map((entry, index) => ({
    ...entry,
    rank: start + index + 1,
    isCurrentUser: entry.userId === userStore.userInfo?.id
  }))
})

function getScoreLabel(type: LeaderboardType): string {
  const labels = {
    points: '积分',
    learning: '学习时长',
    test: '平均分',
    simulation: '演练得分'
  }
  return labels[type]
}

function formatScore(score: number, type: LeaderboardType): string {
  if (type === 'points') {
    return score.toLocaleString()
  }
  if (type === 'learning') {
    const hours = Math.floor(score / 60)
    const mins = score % 60
    return hours > 0 ? `${hours}h ${mins}m` : `${mins}m`
  }
  return score.toString()
}

async function handleTypeChange(type: LeaderboardType) {
  await achievementStore.fetchLeaderboard(type)
  await fetchMyRank(type)
}

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
.leaderboard-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.header {
  margin-bottom: 24px;
}

.header h1 {
  margin: 0 0 20px;
  font-size: 28px;
  text-align: center;
}

.header-tabs {
  display: flex;
  justify-content: center;
}

/* 我的排名 */
.my-rank-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  color: white;
}

.my-rank-info {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.my-avatar {
  border: 3px solid rgba(255, 255, 255, 0.5);
}

.my-info {
  margin-left: 16px;
  display: flex;
  flex-direction: column;
}

.my-name {
  font-size: 18px;
  font-weight: 600;
}

.my-level {
  font-size: 14px;
  opacity: 0.8;
}

.my-rank-detail {
  margin-left: auto;
  text-align: right;
}

.rank-label {
  font-size: 14px;
  opacity: 0.8;
}

.rank-value {
  font-size: 28px;
  font-weight: 700;
  margin: 0 4px;
}

.rank-total {
  font-size: 14px;
  opacity: 0.8;
}

.my-rank-progress {
  display: flex;
  align-items: center;
  gap: 12px;
}

.my-rank-progress :deep(.el-progress-bar__outer) {
  background: rgba(255, 255, 255, 0.2);
}

.progress-label {
  font-size: 14px;
  opacity: 0.9;
  white-space: nowrap;
}

/* 顶部三名 */
.top-three-container {
  margin-bottom: 24px;
}

.top-three {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 16px;
  padding: 32px 24px;
  background: linear-gradient(180deg, #f5f7fa 0%, white 100%);
  border-radius: 16px;
}

.top-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
}

.top-item:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.top-item.rank-1 {
  transform: scale(1.15);
  z-index: 2;
  order: 2;
  background: linear-gradient(180deg, #fff9e6 0%, white 100%);
  border: 2px solid #ffd700;
}

.top-item.rank-1:hover {
  transform: scale(1.2) translateY(-8px);
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
  font-size: 36px;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 12px;
}

.crown {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 28px;
}

.top-item .user-avatar {
  border: 4px solid #f0f0f0;
}

.top-item.rank-1 .user-avatar {
  border-color: #ffd700;
}

.user-info {
  text-align: center;
  margin-bottom: 8px;
}

.user-name {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.user-institution {
  font-size: 12px;
  color: #999;
}

.user-stats {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.level-badge {
  font-size: 12px;
  padding: 2px 8px;
  background: #fff3e0;
  color: #ff9800;
  border-radius: 10px;
}

.score {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
}

/* 排行榜列表 */
.leaderboard-list {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.list-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
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

.rank {
  width: 40px;
  text-align: center;
}

.rank-top {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  font-size: 14px;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, #ff9800, #ff5722);
  border-radius: 50%;
}

.list-item:nth-child(1) .rank-top { background: linear-gradient(135deg, #ffd700, #ff9800); }
.list-item:nth-child(2) .rank-top { background: linear-gradient(135deg, #c0c0c0, #a0a0a0); }
.list-item:nth-child(3) .rank-top { background: linear-gradient(135deg, #cd7f32, #b8860b); }

.rank-number {
  font-size: 16px;
  font-weight: 600;
  color: #999;
}

.list-item .user-avatar {
  margin: 0 16px;
}

.list-item .user-info {
  flex: 1;
  min-width: 0;
}

.list-item .user-info .user-name {
  justify-content: flex-start;
}

.list-item .user-info .user-institution {
  font-size: 12px;
  color: #999;
}

.list-item .user-level {
  margin-right: 16px;
}

.list-item .user-score {
  text-align: right;
  min-width: 80px;
}

.score-value {
  display: block;
  font-size: 18px;
  font-weight: 700;
  color: #409eff;
}

.score-label {
  font-size: 12px;
  color: #999;
}

.user-badge {
  margin-left: 12px;
}

.badge-icon {
  font-size: 20px;
}

.pagination-wrapper {
  margin-top: 24px;
  text-align: center;
}

@media (max-width: 768px) {
  .leaderboard-page {
    padding: 16px;
  }
  
  .top-three {
    flex-direction: column;
    align-items: center;
  }
  
  .top-item {
    width: 100%;
    max-width: 300px;
  }
  
  .top-item.rank-1 {
    transform: none;
    order: 1;
  }
  
  .list-item {
    padding: 12px;
  }
  
  .list-item .user-level {
    display: none;
  }
}
</style>
