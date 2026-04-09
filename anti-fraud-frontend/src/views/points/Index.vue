<template>
  <div class="points-page">
    <el-row :gutter="20">
      <!-- 积分统计 -->
      <el-col :span="8">
        <el-card class="stats-card">
          <div class="user-info">
            <el-avatar :size="80">
              {{ userStore.userInfo.realName?.charAt(0) }}
            </el-avatar>
            <h3>{{ userStore.userInfo.realName }}</h3>
            <el-tag type="warning">Lv.{{ stats.currentLevel }}</el-tag>
          </div>
          
          <div class="points-display">
            <div class="current-points">
              <span class="label">当前积分</span>
              <span class="value">{{ stats.currentPoints }}</span>
            </div>
            <div class="progress">
              <el-progress
                :percentage="progressPercent"
                :stroke-width="10"
                :show-text="false"
              />
              <span class="next-level">距离下一级还需 {{ stats.nextLevelPoints - stats.currentPoints }} 积分</span>
            </div>
          </div>
          
          <div class="quick-stats">
            <div class="stat-item">
              <span class="value">{{ stats.ranking }}</span>
              <span class="label">排名</span>
            </div>
            <div class="stat-item">
              <span class="value">{{ stats.weeklyPoints }}</span>
              <span class="label">本周获得</span>
            </div>
            <div class="stat-item">
              <span class="value">{{ stats.totalPoints }}</span>
              <span class="label">累计获得</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 积分记录和勋章 -->
      <el-col :span="16">
        <el-card>
          <el-tabs>
            <el-tab-pane label="积分记录">
              <el-table :data="pointsRecords">
                <el-table-column prop="description" label="描述" />
                <el-table-column prop="points" label="积分" width="100">
                  <template #default="{ row }">
                    <span :class="row.type === 1 ? 'earn' : 'spend'">
                      {{ row.type === 1 ? '+' : '-' }}{{ row.points }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="时间" width="180" />
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="我的勋章">
              <div class="badges-grid">
                <div
                  v-for="badge in badges"
                  :key="badge.id"
                  class="badge-item"
                  :class="{ acquired: badge.isAcquired }"
                >
                  <div class="badge-icon">{{ badge.badgeIcon || '🏆' }}</div>
                  <div class="badge-name">{{ badge.badgeName }}</div>
                  <div class="badge-desc">{{ badge.description }}</div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { get } from '@/utils/request'

const userStore = useUserStore()

const stats = ref({
  currentPoints: 0,
  currentLevel: 1,
  nextLevelPoints: 100,
  ranking: 1,
  weeklyPoints: 0,
  monthlyPoints: 0,
  totalPoints: 0
})

const pointsRecords = ref<any[]>([])
const badges = ref<any[]>([])

const progressPercent = computed(() => {
  const current = stats.value.currentPoints
  const next = stats.value.nextLevelPoints
  return Math.min((current / next) * 100, 100)
})

const loadStats = async () => {
  try {
    const res = await get('/points/stats')
    stats.value = res.data
  } catch (e) {
    stats.value = {
      currentPoints: userStore.userInfo.points || 100,
      currentLevel: userStore.userInfo.level || 2,
      nextLevelPoints: 300,
      ranking: 56,
      weeklyPoints: 50,
      monthlyPoints: 120,
      totalPoints: 350
    }
  }
}

const loadRecords = async () => {
  try {
    const res = await get('/points/records')
    pointsRecords.value = res.data?.records || []
  } catch (e) {
    pointsRecords.value = [
      { id: 1, description: '完成知识学习', points: 10, type: 1, createTime: '2024-01-15 10:30:00' },
      { id: 2, description: '测试通过', points: 20, type: 1, createTime: '2024-01-14 15:20:00' },
      { id: 3, description: '演练完成', points: 15, type: 1, createTime: '2024-01-13 09:15:00' }
    ]
  }
}

const loadBadges = async () => {
  try {
    const res = await get('/points/badges')
    badges.value = res.data || []
  } catch (e) {
    badges.value = [
      { id: 1, badgeName: '初学者', badgeIcon: '📚', description: '完成首次学习', isAcquired: true },
      { id: 2, badgeName: '勤奋学习者', badgeIcon: '📖', description: '累计学习100小时', isAcquired: false },
      { id: 3, badgeName: '满分达人', badgeIcon: '💯', description: '获得10次测试满分', isAcquired: false },
      { id: 4, badgeName: '场景大师', badgeIcon: '🎮', description: '通关所有演练场景', isAcquired: false },
      { id: 5, badgeName: '举报先锋', badgeIcon: '🛡️', description: '有效举报10次', isAcquired: false }
    ]
  }
}

onMounted(() => {
  loadStats()
  loadRecords()
  loadBadges()
})
</script>

<style scoped>
.points-page {
  max-width: 1400px;
  margin: 0 auto;
}

.stats-card {
  text-align: center;
}

.user-info {
  padding: 20px 0;
}

.user-info h3 {
  margin: 12px 0 8px;
  color: #303133;
}

.points-display {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
  margin-bottom: 20px;
}

.current-points {
  margin-bottom: 16px;
}

.current-points .label {
  font-size: 14px;
  opacity: 0.9;
}

.current-points .value {
  display: block;
  font-size: 48px;
  font-weight: bold;
  margin-top: 8px;
}

.progress .next-level {
  font-size: 12px;
  opacity: 0.8;
  margin-top: 8px;
  display: block;
}

.quick-stats {
  display: flex;
  justify-content: space-around;
}

.quick-stats .stat-item {
  text-align: center;
}

.quick-stats .value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.quick-stats .label {
  font-size: 12px;
  color: #909399;
}

.earn {
  color: #67C23A;
  font-weight: bold;
}

.spend {
  color: #F56C6C;
  font-weight: bold;
}

.badges-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.badge-item {
  text-align: center;
  padding: 20px;
  border-radius: 12px;
  background: #f5f7fa;
  opacity: 0.5;
}

.badge-item.acquired {
  opacity: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.badge-icon {
  font-size: 40px;
  margin-bottom: 8px;
}

.badge-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.badge-desc {
  font-size: 12px;
  opacity: 0.8;
}
</style>
