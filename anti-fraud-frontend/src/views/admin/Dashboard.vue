<template>
  <div class="admin-dashboard">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1>
            <el-icon><DataAnalysis /></el-icon>
            数据概览
          </h1>
          <p>实时监控平台运行状态，掌握各项数据指标</p>
        </div>
        <div class="header-right">
          <el-tag type="success" effect="dark" size="large" class="status-tag">
            <el-icon><CircleCheckFilled /></el-icon>
            系统运行正常
          </el-tag>
          <div class="system-info">
            <span class="update-time">最后更新: {{ currentTime }}</span>
            <span class="user-count">在线用户: {{ onlineUsers }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card" @mouseenter="animateStat('totalUsers')">
          <div class="stat-glow" style="background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%)"></div>
          <div class="stat-icon" style="background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%)">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ animatedStats.totalUsers.toLocaleString() }}</div>
            <div class="stat-label">总用户数</div>
            <div class="stat-trend up">
              <el-icon><Top /></el-icon>
              较昨日 +{{ stats.todayNewUsers }}
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-glow" style="background: linear-gradient(135deg, #48bb78 0%, #38a169 100%)"></div>
          <div class="stat-icon" style="background: linear-gradient(135deg, #48bb78 0%, #38a169 100%)">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ animatedStats.totalKnowledge }}</div>
            <div class="stat-label">知识文章</div>
            <div class="stat-trend">
              <el-icon><View /></el-icon>
              总浏览 {{ formatNumber(stats.totalViews) }}
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-glow" style="background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%)"></div>
          <div class="stat-icon" style="background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%)">
            <el-icon><EditPen /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ animatedStats.totalTests.toLocaleString() }}</div>
            <div class="stat-label">测试完成</div>
            <div class="stat-trend up">
              <el-icon><TrendCharts /></el-icon>
              通过率 {{ stats.passRate }}%
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card warning">
          <div class="stat-glow" style="background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%)"></div>
          <div class="stat-icon" style="background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%)">
            <el-icon><Bell /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ animatedStats.pendingReports }}</div>
            <div class="stat-label">待处理举报</div>
            <div class="stat-trend warning">
              <el-icon><Warning /></el-icon>
              需及时处理
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 趋势图表 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">用户增长趋势</span>
              <el-radio-group v-model="userTrendRange" size="small">
                <el-radio-button value="7">近7天</el-radio-button>
                <el-radio-button value="30">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <LineChart 
            :data="userTrend" 
            title="新增用户" 
            color="#4299e1"
            height="280px"
          />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">测试参与趋势</span>
              <el-radio-group v-model="testTrendRange" size="small">
                <el-radio-button value="7">近7天</el-radio-button>
                <el-radio-button value="30">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <LineChart 
            :data="testTrend" 
            title="测试次数" 
            color="#48bb78"
            height="280px"
          />
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 第二行图表 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span class="chart-title">诈骗类型分布</span>
          </template>
          <PieChart 
            :data="fraudTypeData" 
            title="诈骗类型"
            height="260px"
          />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span class="chart-title">院系参与率TOP5</span>
          </template>
          <BarChart 
            :data="departmentData" 
            title="参与率"
            color="#4299e1"
            height="260px"
            horizontal
          />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span class="chart-title">演练成绩分布</span>
          </template>
          <BarChart 
            :data="scoreDistribution" 
            title="人数"
            color="#48bb78"
            height="260px"
          />
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 快捷操作与最新动态 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="quick-actions">
          <template #header>
            <span class="chart-title">快捷操作</span>
          </template>
          <el-row :gutter="16">
            <el-col :span="12">
              <div class="action-item" @click="$router.push('/admin/knowledge')">
                <div class="action-icon" style="background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%)">
                  <el-icon><Plus /></el-icon>
                </div>
                <div class="action-info">
                  <div class="action-title">发布知识</div>
                  <div class="action-desc">添加新的反诈知识</div>
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="action-item" @click="$router.push('/admin/warnings')">
                <div class="action-icon" style="background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%)">
                  <el-icon><Bell /></el-icon>
                </div>
                <div class="action-info">
                  <div class="action-title">发布预警</div>
                  <div class="action-desc">发布最新诈骗预警</div>
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="action-item" @click="$router.push('/admin/reports')">
                <div class="action-icon" style="background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%)">
                  <el-icon><Warning /></el-icon>
                </div>
                <div class="action-info">
                  <div class="action-title">处理举报</div>
                  <div class="action-desc">处理待审核举报</div>
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="action-item" @click="$router.push('/admin/activities')">
                <div class="action-icon" style="background: linear-gradient(135deg, #48bb78 0%, #38a169 100%)">
                  <el-icon><Calendar /></el-icon>
                </div>
                <div class="action-info">
                  <div class="action-title">创建活动</div>
                  <div class="action-desc">组织反诈宣传活动</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="recent-activities">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">最新动态</span>
              <el-button text size="small">查看更多</el-button>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in recentActivities"
              :key="index"
              :type="item.type"
              :timestamp="item.time"
              placement="top"
            >
              {{ item.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { get } from '@/utils/request'
import { DataAnalysis, CircleCheckFilled } from '@element-plus/icons-vue'
import LineChart from '@/components/charts/LineChart.vue'
import PieChart from '@/components/charts/PieChart.vue'
import BarChart from '@/components/charts/BarChart.vue'

const currentTime = ref('')
const onlineUsers = ref(0)

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

// 模拟在线用户数更新
const updateOnlineUsers = () => {
  onlineUsers.value = Math.floor(Math.random() * 50) + 10
}

let timeInterval: number
let onlineUsersInterval: number

const stats = ref({
  totalUsers: 0,
  todayNewUsers: 0,
  totalKnowledge: 0,
  totalTests: 0,
  totalSimulations: 0,
  totalReports: 0,
  pendingReports: 0,
  totalWarnings: 0,
  totalViews: 0,
  passRate: 0
})

const animatedStats = reactive({
  totalUsers: 0,
  totalKnowledge: 0,
  totalTests: 0,
  pendingReports: 0
})

const userTrendRange = ref('7')
const testTrendRange = ref('7')

const userTrend = ref<{ label: string; value: number }[]>([])
const testTrend = ref<{ label: string; value: number }[]>([])

const fraudTypeData = ref<{ name: string; value: number }[]>([])
const departmentData = ref<{ name: string; value: number }[]>([])
const scoreDistribution = ref<{ name: string; value: number }[]>([])

const recentActivities = ref<any[]>([])

// 格式化数字
const formatNumber = (num: number) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num
}

// 数字动画
const animateNumber = (key: keyof typeof animatedStats, target: number) => {
  const duration = 1000
  const start = animatedStats[key]
  const increment = (target - start) / (duration / 16)
  let current = start
  
  const timer = setInterval(() => {
    current += increment
    if ((increment > 0 && current >= target) || (increment < 0 && current <= target)) {
      animatedStats[key] = target
      clearInterval(timer)
    } else {
      animatedStats[key] = Math.round(current)
    }
  }, 16)
}

// 悬停时触发动画
const animateStat = (key: keyof typeof animatedStats) => {
  if (stats.value && stats.value[key] !== undefined) {
    animateNumber(key, stats.value[key])
  }
}

const loadData = async () => {
  try {
    const [statsRes, userRes, testRes] = await Promise.all([
      get('/admin/dashboard/stats'),
      get('/admin/dashboard/user-trend'),
      get('/admin/dashboard/test-trend')
    ])
    stats.value = statsRes.data
    userTrend.value = userRes.data || []
    testTrend.value = testRes.data || []
    
    animateNumber('totalUsers', stats.value.totalUsers)
    animateNumber('totalKnowledge', stats.value.totalKnowledge)
    animateNumber('totalTests', stats.value.totalTests)
    animateNumber('pendingReports', stats.value.pendingReports)
  } catch (e) {
    // 模拟数据
    stats.value = {
      totalUsers: 1024,
      todayNewUsers: 25,
      totalKnowledge: 156,
      totalTests: 3560,
      totalSimulations: 890,
      totalReports: 128,
      pendingReports: 12,
      totalWarnings: 45,
      totalViews: 12500,
      passRate: 85
    }
    
    animateNumber('totalUsers', stats.value.totalUsers)
    animateNumber('totalKnowledge', stats.value.totalKnowledge)
    animateNumber('totalTests', stats.value.totalTests)
    animateNumber('pendingReports', stats.value.pendingReports)
    
    userTrend.value = [
      { label: '01-09', value: 15 },
      { label: '01-10', value: 22 },
      { label: '01-11', value: 18 },
      { label: '01-12', value: 30 },
      { label: '01-13', value: 25 },
      { label: '01-14', value: 35 },
      { label: '01-15', value: 28 }
    ]
    
    testTrend.value = [
      { label: '01-09', value: 120 },
      { label: '01-10', value: 150 },
      { label: '01-11', value: 98 },
      { label: '01-12', value: 180 },
      { label: '01-13', value: 145 },
      { label: '01-14', value: 200 },
      { label: '01-15', value: 175 }
    ]
    
    fraudTypeData.value = [
      { name: '电信诈骗', value: 35 },
      { name: '网络诈骗', value: 28 },
      { name: '兼职诈骗', value: 18 },
      { name: '校园贷', value: 12 },
      { name: '其他', value: 7 }
    ]
    
    departmentData.value = [
      { name: '计算机学院', value: 95 },
      { name: '商学院', value: 88 },
      { name: '外国语学院', value: 82 },
      { name: '艺术学院', value: 76 },
      { name: '体育学院', value: 68 }
    ]
    
    scoreDistribution.value = [
      { name: '0-59', value: 45 },
      { name: '60-69', value: 120 },
      { name: '70-79', value: 280 },
      { name: '80-89', value: 350 },
      { name: '90-100', value: 229 }
    ]
    
    recentActivities.value = [
      { content: '张三完成了"反诈知识入门测试"', time: '10分钟前', type: 'success' },
      { content: '新增预警：警惕新型AI换脸诈骗', time: '30分钟前', type: 'warning' },
      { content: '李四提交了举报信息', time: '1小时前', type: 'info' },
      { content: '"反诈知识竞赛"活动已开始', time: '2小时前', type: 'primary' },
      { content: '王专家回答了在线提问', time: '3小时前', type: 'success' }
    ]
  }
}

onMounted(() => {
  loadData()
  updateTime()
  updateOnlineUsers()
  timeInterval = window.setInterval(updateTime, 1000)
  onlineUsersInterval = window.setInterval(updateOnlineUsers, 5000)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
  if (onlineUsersInterval) {
    clearInterval(onlineUsersInterval)
  }
})
</script>

<style scoped>
.admin-dashboard {
  padding: 0 0 40px;
  min-height: calc(100vh - 160px);
}

/* 页面标题 */
.page-header {
  margin-bottom: 32px;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
}

.header-content {
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.header-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
  z-index: -1;
}

.header-left h1 {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.header-left p {
  opacity: 0.9;
  font-size: 16px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.header-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.status-tag {
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
  50% {
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
  }
  100% {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
}

.system-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.update-time {
  font-size: 12px;
  opacity: 0.8;
}

.user-count {
  font-size: 14px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  position: relative;
  border-radius: 16px;
  padding: 24px;
  background: white;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
}

.stat-glow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card:hover .stat-glow {
  opacity: 1;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.stat-icon .el-icon {
  font-size: 26px;
  color: white;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1a365d;
  line-height: 1.2;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #718096;
  margin-bottom: 8px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #718096;
}

.stat-trend.up {
  color: #38a169;
}

.stat-trend.warning {
  color: #dd6b20;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 16px;
  transition: all 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a365d;
}

.quick-actions {
  border-radius: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 12px;
  background: #f7fafc;
}

.action-item:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  transform: translateX(8px);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-icon .el-icon {
  font-size: 22px;
  color: white;
}

.action-title {
  font-weight: 600;
  color: #1a365d;
  font-size: 15px;
}

.action-desc {
  font-size: 13px;
  color: #718096;
  margin-top: 4px;
}

.recent-activities {
  border-radius: 16px;
  min-height: 320px;
}

.recent-activities :deep(.el-timeline-item__content) {
  font-size: 14px;
  color: #4a5568;
}

.recent-activities :deep(.el-timeline-item__timestamp) {
  font-size: 12px;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .header-right {
    align-items: flex-start;
  }
}
</style>

