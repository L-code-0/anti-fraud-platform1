<template>
  <div class="leaderboard-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
      </div>
      <div class="header-content">
        <h1>排行榜</h1>
        <p>看看谁是防诈达人，向优秀学习</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 筛选和视图 -->
      <div class="toolbar">
        <div class="type-tabs">
          <button
            v-for="tab in typeTabs"
            :key="tab.id"
            class="tab-btn"
            :class="{ active: activeType === tab.id }"
            @click="activeType = tab.id"
          >
            <el-icon><component :is="tab.icon" /></el-icon>
            {{ tab.name }}
          </button>
        </div>

        <div class="time-filter">
          <el-select v-model="timeRange" placeholder="时间范围">
            <el-option label="本周" value="week" />
            <el-option label="本月" value="month" />
            <el-option label="本季度" value="quarter" />
            <el-option label="全部" value="all" />
          </el-select>
        </div>
      </div>

      <!-- TOP3展示 -->
      <div class="top-three">
        <div class="rank-card second" v-if="topThree[1]">
          <div class="rank-avatar">
            <img :src="topThree[1].avatar" :alt="topThree[1].name" />
          </div>
          <div class="rank-crown">
            <el-icon><Trophy /></el-icon>
          </div>
          <div class="rank-badge">2</div>
          <h4>{{ topThree[1].name }}</h4>
          <p>{{ topThree[1].school }}</p>
          <div class="rank-score">{{ topThree[1].score }}分</div>
        </div>

        <div class="rank-card first" v-if="topThree[0]">
          <div class="rank-avatar">
            <img :src="topThree[0].avatar" :alt="topThree[0].name" />
            <div class="avatar-glow"></div>
          </div>
          <div class="rank-crown crown-gold">
            <el-icon><Trophy /></el-icon>
          </div>
          <div class="rank-badge">1</div>
          <h4>{{ topThree[0].name }}</h4>
          <p>{{ topThree[0].school }}</p>
          <div class="rank-score">{{ topThree[0].score }}分</div>
        </div>

        <div class="rank-card third" v-if="topThree[2]">
          <div class="rank-avatar">
            <img :src="topThree[2].avatar" :alt="topThree[2].name" />
          </div>
          <div class="rank-crown crown-bronze">
            <el-icon><Trophy /></el-icon>
          </div>
          <div class="rank-badge">3</div>
          <h4>{{ topThree[2].name }}</h4>
          <p>{{ topThree[2].school }}</p>
          <div class="rank-score">{{ topThree[2].score }}分</div>
        </div>
      </div>

      <!-- 完整排行 -->
      <div class="leaderboard-list">
        <div class="list-header">
          <span class="col-rank">排名</span>
          <span class="col-user">用户</span>
          <span class="col-school">学校</span>
          <span class="col-score">积分</span>
          <span class="col-tests">测试数</span>
          <span class="col-rate">通过率</span>
        </div>

        <div class="list-body">
          <div
            class="list-item"
            v-for="(user, index) in leaderboard"
            :key="user.id"
            :class="{ highlight: user.isCurrentUser }"
          >
            <div class="col-rank">
              <span class="rank-num">{{ user.rank }}</span>
            </div>
            <div class="col-user">
              <div class="user-avatar">
                <img :src="user.avatar" :alt="user.name" />
                <span v-if="user.rank <= 3" class="rank-indicator" :class="'rank-' + user.rank">
                  <el-icon><Trophy /></el-icon>
                </span>
              </div>
              <div class="user-info">
                <span class="user-name">{{ user.name }}</span>
                <span class="user-level">Lv.{{ user.level }}</span>
              </div>
            </div>
            <div class="col-school">{{ user.school }}</div>
            <div class="col-score">
              <span class="score-value">{{ user.score }}</span>
              <span class="score-unit">分</span>
            </div>
            <div class="col-tests">{{ user.tests }}次</div>
            <div class="col-rate">
              <el-progress
                :percentage="user.passRate"
                :stroke-width="6"
                :show-text="false"
              />
              <span class="rate-value">{{ user.passRate }}%</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="20"
          :total="total"
          :background="true"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Trophy } from '@element-plus/icons-vue'

const activeType = ref('score')
const timeRange = ref('week')
const currentPage = ref(1)
const total = ref(100)

const typeTabs = [
  { id: 'score', name: '积分排行', icon: 'Trophy' },
  { id: 'tests', name: '测试排行', icon: 'EditPen' },
  { id: 'streak', name: '连续学习', icon: 'Timer' },
  { id: 'reports', name: '举报排行', icon: 'WarnTriangleFilled' }
]

const topThree = ref([
  {
    id: 1,
    name: '张明',
    avatar: 'https://picsum.photos/seed/user1/200/200',
    school: '计算机学院',
    score: 9850,
    tests: 156,
    passRate: 98
  },
  {
    id: 2,
    name: '李小红',
    avatar: 'https://picsum.photos/seed/user2/200/200',
    school: '信息工程学院',
    score: 9620,
    tests: 148,
    passRate: 96
  },
  {
    id: 3,
    name: '王强',
    avatar: 'https://picsum.photos/seed/user3/200/200',
    school: '软件学院',
    score: 9400,
    tests: 142,
    passRate: 95
  }
])

const leaderboard = ref([
  { id: 4, rank: 4, name: '赵敏', avatar: 'https://picsum.photos/seed/user4/200/200', school: '网络空间安全学院', score: 9200, level: 12, tests: 138, passRate: 94, isCurrentUser: false },
  { id: 5, rank: 5, name: '刘洋', avatar: 'https://picsum.photos/seed/user5/200/200', school: '数学学院', score: 9050, level: 11, tests: 135, passRate: 93, isCurrentUser: false },
  { id: 6, rank: 6, name: '陈静', avatar: 'https://picsum.photos/seed/user6/200/200', school: '物理学院', score: 8900, level: 11, tests: 130, passRate: 92, isCurrentUser: false },
  { id: 7, rank: 7, name: '周杰', avatar: 'https://picsum.photos/seed/user7/200/200', school: '外语学院', score: 8750, level: 10, tests: 128, passRate: 91, isCurrentUser: true },
  { id: 8, rank: 8, name: '吴琳', avatar: 'https://picsum.photos/seed/user8/200/200', school: '经济管理学院', score: 8600, level: 10, tests: 125, passRate: 90, isCurrentUser: false },
  { id: 9, rank: 9, name: '郑浩', avatar: 'https://picsum.photos/seed/user9/200/200', school: '计算机学院', score: 8450, level: 10, tests: 122, passRate: 89, isCurrentUser: false },
  { id: 10, rank: 10, name: '孙丽', avatar: 'https://picsum.photos/seed/user10/200/200', school: '软件学院', score: 8300, level: 9, tests: 120, passRate: 88, isCurrentUser: false }
])

const handlePageChange = (page: number) => {
  currentPage.value = page
}
</script>

<style scoped>
.leaderboard-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

.page-header {
  position: relative;
  padding: var(--spacing-16) var(--spacing-6);
  overflow: hidden;
}

.header-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.header-bg .bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 50%, #fcd34d 100%);
}

.header-content {
  position: relative;
  z-index: 1;
  max-width: 1280px;
  margin: 0 auto;
  text-align: center;
  color: white;
}

.header-content h1 {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-4);
}

.header-content p {
  font-size: var(--font-size-lg);
  opacity: 0.9;
}

.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6) var(--spacing-12);
}

/* 工具栏 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-8);
}

.type-tabs {
  display: flex;
  gap: var(--spacing-2);
  background: var(--bg-primary);
  padding: var(--spacing-1);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-4);
  background: transparent;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.tab-btn:hover {
  color: var(--primary-color);
}

.tab-btn.active {
  background: var(--gradient-primary);
  color: white;
  box-shadow: var(--shadow-primary);
}

.time-filter :deep(.el-select) {
  width: 140px;
}

/* TOP3 */
.top-three {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: var(--spacing-6);
  margin-bottom: var(--spacing-10);
  padding: var(--spacing-8) 0;
}

.rank-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-6);
  text-align: center;
  box-shadow: var(--shadow-lg);
  transition: all var(--transition-normal);
  position: relative;
  width: 200px;
}

.rank-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
}

.rank-card.first {
  background: linear-gradient(180deg, #fef3c7 0%, #ffffff 100%);
  border: 2px solid #fbbf24;
  padding-top: var(--spacing-8);
}

.rank-card.second {
  background: linear-gradient(180deg, #f3f4f6 0%, #ffffff 100%);
  border: 2px solid #9ca3af;
}

.rank-card.third {
  background: linear-gradient(180deg, #fed7aa 0%, #ffffff 100%);
  border: 2px solid #f97316;
}

.rank-avatar {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto var(--spacing-3);
  border-radius: 50%;
  overflow: visible;
}

.rank-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
  box-shadow: var(--shadow-md);
}

.rank-card.first .rank-avatar img {
  width: 96px;
  height: 96px;
  border: 4px solid #fbbf24;
}

.avatar-glow {
  position: absolute;
  inset: -8px;
  background: radial-gradient(circle, rgba(251, 191, 36, 0.4) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.4; }
  50% { transform: scale(1.2); opacity: 0.2; }
}

.rank-crown {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 40px;
  background: #9ca3af;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.rank-crown.crown-gold {
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 100%);
  box-shadow: 0 4px 12px rgba(251, 191, 36, 0.4);
}

.rank-crown.crown-bronze {
  background: linear-gradient(135deg, #f97316 0%, #fb923c 100%);
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.4);
}

.rank-badge {
  position: absolute;
  top: var(--spacing-3);
  right: var(--spacing-3);
  width: 28px;
  height: 28px;
  background: var(--text-muted);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: white;
}

.rank-card.first .rank-badge {
  background: #fbbf24;
  width: 32px;
  height: 32px;
  font-size: var(--font-size-base);
}

.rank-card h4 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.rank-card p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-3);
}

.rank-score {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-light) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 排行榜列表 */
.leaderboard-list {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  overflow: hidden;
  margin-bottom: var(--spacing-6);
}

.list-header {
  display: grid;
  grid-template-columns: 80px 200px 1fr 100px 100px 150px;
  gap: var(--spacing-4);
  padding: var(--spacing-4) var(--spacing-6);
  background: var(--bg-secondary);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--text-secondary);
}

.list-body {
  padding: 0 var(--spacing-4);
}

.list-item {
  display: grid;
  grid-template-columns: 80px 200px 1fr 100px 100px 150px;
  gap: var(--spacing-4);
  padding: var(--spacing-4) var(--spacing-2);
  border-bottom: 1px solid var(--border-secondary);
  align-items: center;
  transition: background var(--transition-fast);
}

.list-item:last-child {
  border-bottom: none;
}

.list-item:hover {
  background: var(--bg-hover);
}

.list-item.highlight {
  background: var(--primary-bg);
}

.col-rank .rank-num {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: var(--bg-secondary);
  border-radius: 50%;
  font-weight: var(--font-weight-semibold);
  color: var(--text-secondary);
}

.col-user {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
}

.user-avatar {
  position: relative;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: visible;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.rank-indicator {
  position: absolute;
  bottom: -4px;
  right: -4px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: white;
}

.rank-indicator.rank-1 { background: #fbbf24; }
.rank-indicator.rank-2 { background: #9ca3af; }
.rank-indicator.rank-3 { background: #f97316; }

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

.user-level {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.col-school {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.col-score {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-1);
}

.score-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--primary-color);
}

.score-unit {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.col-tests {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.col-rate {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.rate-value {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  min-width: 36px;
}

.col-rate :deep(.el-progress-bar__outer) {
  border-radius: var(--radius-full);
}

.col-rate :deep(.el-progress-bar__inner) {
  border-radius: var(--radius-full);
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
}

.pagination-wrapper :deep(.el-pagination) {
  background: var(--bg-primary);
  padding: var(--spacing-4);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
}

/* 响应式 */
@media (max-width: 1024px) {
  .list-header {
    display: none;
  }

  .list-item {
    grid-template-columns: 60px 1fr;
    grid-template-rows: auto auto;
    gap: var(--spacing-2);
    padding: var(--spacing-4);
  }

  .col-school,
  .col-tests,
  .col-rate {
    grid-column: 2;
    font-size: var(--font-size-xs);
  }

  .col-score {
    position: absolute;
    top: var(--spacing-4);
    right: var(--spacing-4);
  }

  .list-item {
    position: relative;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: var(--spacing-10) var(--spacing-4);
  }

  .header-content h1 {
    font-size: var(--font-size-2xl);
  }

  .toolbar {
    flex-direction: column;
    gap: var(--spacing-3);
  }

  .type-tabs {
    width: 100%;
    overflow-x: auto;
  }

  .top-three {
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-4);
  }

  .rank-card {
    width: 100%;
    max-width: 280px;
  }

  .rank-card.first {
    order: -1;
  }
}
</style>
