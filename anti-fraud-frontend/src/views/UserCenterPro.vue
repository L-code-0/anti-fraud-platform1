<template>
  <div class="user-center">
    <el-row :gutter="20">
      <el-col :xs="24" :lg="6">
        <el-card class="user-profile-card">
          <div class="profile-header">
            <el-avatar :size="100" :src="userInfo.avatar">
              <el-icon :size="40"><User /></el-icon>
            </el-avatar>
            <h2>{{ userInfo.nickname }}</h2>
            <p class="user-role">
              <el-tag :type="getRoleType(userInfo.role)" size="small">{{ getRoleName(userInfo.role) }}</el-tag>
            </p>
            <div class="profile-stats">
              <div class="stat">
                <span class="value">{{ userInfo.points }}</span>
                <span class="label">积分</span>
              </div>
              <div class="stat">
                <span class="value">{{ userInfo.learningDays }}</span>
                <span class="label">学习天数</span>
              </div>
              <div class="stat">
                <span class="value">{{ userInfo.badges }}</span>
                <span class="label">获得勋章</span>
              </div>
            </div>
          </div>
          <el-divider />
          <div class="profile-actions">
            <el-button type="primary" @click="$router.push('/profile')">编辑资料</el-button>
            <el-button @click="$router.push('/settings')">账号设置</el-button>
          </div>
        </el-card>

        <el-card class="medals-card">
          <template #header>
            <span>我的勋章</span>
          </template>
          <div class="medals-grid">
            <el-tooltip v-for="badge in userBadges" :key="badge.id" :content="badge.name">
              <div class="badge-item" :class="{ 'earned': badge.earned }">
                <el-icon :size="32" :color="badge.earned ? badge.color : '#ccc'">
                  <component :is="badge.icon" />
                </el-icon>
                <span class="badge-name">{{ badge.name }}</span>
              </div>
            </el-tooltip>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="18">
        <el-card class="overview-card">
          <template #header>
            <span>学习进度总览</span>
            <el-radio-group v-model="timeRange" size="small" class="time-selector">
              <el-radio-button value="week">本周</el-radio-button>
              <el-radio-button value="month">本月</el-radio-button>
              <el-radio-button value="all">全部</el-radio-button>
            </el-radio-group>
          </template>
          <div class="overview-stats">
            <div class="stat-item">
              <div class="stat-icon blue"><el-icon><Reading /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ overviewStats.knowledgeLearned }}</span>
                <span class="stat-label">知识学习</span>
                <el-progress :percentage="overviewStats.knowledgePercent" :show-text="false" :stroke-width="4" />
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon green"><el-icon><Edit /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ overviewStats.testsCompleted }}</span>
                <span class="stat-label">测试完成</span>
                <el-progress :percentage="overviewStats.testPercent" :show-text="false" :stroke-width="4" color="#67c23a" />
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon orange"><el-icon><VideoPlay /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ overviewStats.scenesCompleted }}</span>
                <span class="stat-label">演练完成</span>
                <el-progress :percentage="overviewStats.scenePercent" :show-text="false" :stroke-width="4" color="#e6a23c" />
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon purple"><el-icon><Trophy /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ overviewStats.rank }}</span>
                <span class="stat-label">当前排名</span>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="chart-card">
          <template #header>
            <span>学习趋势</span>
          </template>
          <div class="chart-container">
            <div id="learningChart" style="height: 300px;"></div>
          </div>
        </el-card>

        <el-card class="calendar-card">
          <template #header>
            <span>学习日历</span>
          </template>
          <div class="calendar-container">
            <el-calendar v-model="currentDate">
              <template #date-cell="{ data }">
                <div class="calendar-cell" :class="{ 'is-selected': data.isSelected }">
                  <span class="date-num">{{ data.day.split('-').slice(2).join('') }}</span>
                  <div v-if="getLearningData(data.day)" class="learning-dot" :style="{ background: getLearningData(data.day).color }"></div>
                </div>
              </template>
            </el-calendar>
          </div>
        </el-card>

        <el-row :gutter="20" class="quick-cards">
          <el-col :xs="24" :sm="12">
            <el-card class="quick-card" @click="$router.push('/knowledge')">
              <el-icon class="card-icon" color="#409eff"><Reading /></el-icon>
              <div class="card-info">
                <h4>继续学习</h4>
                <p>上次学习：冒充客服诈骗</p>
                <span class="progress-text">进度 60%</span>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-card class="quick-card" @click="$router.push('/wrongbook')">
              <el-icon class="card-icon" color="#f56c6c"><Document /></el-icon>
              <div class="card-info">
                <h4>错题本</h4>
                <p>待复习：12 题</p>
                <span class="progress-text">去复习</span>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-card class="recent-activity-card">
          <template #header>
            <span>最近活动</span>
          </template>
          <el-timeline>
            <el-timeline-item v-for="activity in recentActivities" :key="activity.id" :timestamp="activity.time" placement="top">
              <el-card>
                <div class="activity-item">
                  <el-icon :size="20" :color="activity.color"><component :is="activity.icon" /></el-icon>
                  <div class="activity-info">
                    <span class="activity-title">{{ activity.title }}</span>
                    <span class="activity-desc">{{ activity.description }}</span>
                  </div>
                  <span class="activity-points" :class="{ 'positive': activity.points > 0 }">
                    {{ activity.points > 0 ? '+' : '' }}{{ activity.points }}积分
                  </span>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { User, Reading, Edit, VideoPlay, Trophy, Document, Star, Check, Clock, DataLine } from '@element-plus/icons-vue'

const currentDate = ref(new Date())
const timeRange = ref('week')

const userInfo = reactive({
  nickname: '张三同学',
  avatar: '',
  role: 1,
  points: 2850,
  learningDays: 45,
  badges: 8
})

const overviewStats = reactive({
  knowledgeLearned: 38,
  knowledgePercent: 76,
  testsCompleted: 15,
  testPercent: 75,
  scenesCompleted: 8,
  scenePercent: 53,
  rank: 56
})

const userBadges = [
  { id: 1, name: '防骗新星', icon: Star, color: '#f1c40f', earned: true },
  { id: 2, name: '学习达人', icon: Check, color: '#2ecc71', earned: true },
  { id: 3, name: '测试高手', icon: Edit, color: '#3498db', earned: true },
  { id: 4, name: '反诈专家', icon: Trophy, color: '#9b59b6', earned: false },
  { id: 5, name: '安全卫士', icon: Clock, color: '#e74c3c', earned: false },
  { id: 6, name: '知识渊博', icon: DataLine, color: '#1abc9c', earned: true }
]

const recentActivities = ref([
  { id: 1, time: '2026-01-15 14:30', title: '完成测试', description: '高校反诈安全测试 - 得分92分', icon: Edit, color: '#409eff', points: 50 },
  { id: 2, time: '2026-01-15 10:20', title: '学习知识', description: '学习完成《冒充客服诈骗防范》', icon: Reading, color: '#67c23a', points: 20 },
  { id: 3, time: '2026-01-14 16:45', title: '参与演练', description: '完成刷单返利陷阱演练', icon: VideoPlay, color: '#e6a23c', points: 30 },
  { id: 4, time: '2026-01-14 09:00', title: '每日签到', description: '连续签到7天', icon: Clock, color: '#909399', points: 10 }
])

const getRoleType = (role: number) => ({ 1: 'primary', 2: 'success', 3: 'warning', 4: 'danger', 5: 'info' }[role] || 'info')
const getRoleName = (role: number) => ({ 1: '学生', 2: '教师', 3: '专家', 4: '管理员', 5: '系统管理员' }[role] || '用户')

const getLearningData = (day: string) => {
  const data: Record<string, { color: string }> = {
    '2026-01-15': { color: '#67c23a' },
    '2026-01-14': { color: '#409eff' },
    '2026-01-13': { color: '#e6a23c' }
  }
  return data[day]
}

onMounted(() => {
  // 初始化图表
  if (typeof window !== 'undefined') {
    const chartDom = document.getElementById('learningChart')
    if (chartDom) {
      // 模拟图表初始化
      chartDom.innerHTML = '<div style="display: flex; align-items: center; justify-content: center; height: 100%; color: var(--text-secondary);">学习趋势图表区域</div>'
    }
  }
})
</script>

<style scoped>
.user-center { max-width: 1440px; margin: 0 auto; }
.user-profile-card { margin-bottom: var(--spacing-6); }
.profile-header { text-align: center; }
.profile-header h2 { margin: var(--spacing-4) 0 var(--spacing-2); }
.user-role { margin-bottom: var(--spacing-4); }
.profile-stats { display: flex; justify-content: space-around; padding: var(--spacing-4) 0; }
.profile-stats .stat { text-align: center; }
.profile-stats .value { display: block; font-size: var(--font-size-2xl); font-weight: var(--font-weight-bold); color: var(--primary-color); }
.profile-stats .label { font-size: var(--font-size-xs); color: var(--text-secondary); }
.profile-actions { display: flex; gap: var(--spacing-2); }
.profile-actions .el-button { flex: 1; }
.medals-card { margin-bottom: var(--spacing-6); }
.medals-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--spacing-4); text-align: center; }
.badge-item { display: flex; flex-direction: column; align-items: center; gap: var(--spacing-2); padding: var(--spacing-3); border-radius: var(--radius-md); cursor: pointer; transition: background var(--transition-fast); }
.badge-item:hover { background: var(--bg-page); }
.badge-item.earned { }
.badge-name { font-size: var(--font-size-xs); color: var(--text-secondary); }
.overview-card { margin-bottom: var(--spacing-6); }
.time-selector { float: right; }
.overview-stats { display: grid; grid-template-columns: repeat(4, 1fr); gap: var(--spacing-4); }
.stat-item { display: flex; align-items: center; gap: var(--spacing-3); padding: var(--spacing-3); background: var(--bg-page); border-radius: var(--radius-lg); }
.stat-icon { width: 48px; height: 48px; border-radius: var(--radius-lg); display: flex; align-items: center; justify-content: center; color: white; font-size: 20px; }
.stat-icon.blue { background: linear-gradient(135deg, #667eea, #764ba2); }
.stat-icon.green { background: linear-gradient(135deg, #11998e, #38ef7d); }
.stat-icon.orange { background: linear-gradient(135deg, #f97316, #fbbf24); }
.stat-icon.purple { background: linear-gradient(135deg, #7c3aed, #a855f7); }
.stat-info { flex: 1; }
.stat-value { display: block; font-size: var(--font-size-xl); font-weight: var(--font-weight-bold); }
.stat-label { font-size: var(--font-size-xs); color: var(--text-secondary); }
.chart-card { margin-bottom: var(--spacing-6); }
.chart-container { min-height: 300px; }
.calendar-card { margin-bottom: var(--spacing-6); }
.calendar-cell { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; }
.date-num { font-size: var(--font-size-sm); }
.learning-dot { width: 6px; height: 6px; border-radius: 50%; margin-top: 2px; }
.quick-cards { margin-bottom: var(--spacing-6); }
.quick-card { display: flex; align-items: center; gap: var(--spacing-4); cursor: pointer; transition: all var(--transition-fast); }
.quick-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
.quick-card .card-icon { font-size: 40px; }
.quick-card .card-info h4 { margin: 0 0 var(--spacing-1); }
.quick-card .card-info p { margin: 0; font-size: var(--font-size-sm); color: var(--text-secondary); }
.quick-card .progress-text { font-size: var(--font-size-xs); color: var(--primary-color); }
.activity-item { display: flex; align-items: center; gap: var(--spacing-3); }
.activity-info { flex: 1; display: flex; flex-direction: column; }
.activity-title { font-weight: var(--font-weight-medium); }
.activity-desc { font-size: var(--font-size-sm); color: var(--text-secondary); }
.activity-points { font-size: var(--font-size-sm); color: var(--text-secondary); }
.activity-points.positive { color: var(--success-color); }
@media (max-width: 992px) {
  .overview-stats { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .overview-stats { grid-template-columns: 1fr; }
  .medals-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
