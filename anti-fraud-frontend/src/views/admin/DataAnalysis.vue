<template>
  <div class="data-analysis-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1>数据分析中心</h1>
        <p class="subtitle">全面的用户行为和内容数据分析</p>
      </div>
      <div class="header-actions">
        <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
          <el-radio-button value="today">今日</el-radio-button>
          <el-radio-button value="week">本周</el-radio-button>
          <el-radio-button value="month">本月</el-radio-button>
          <el-radio-button value="year">本年</el-radio-button>
        </el-radio-group>
        <el-button type="primary" @click="handleExportReport">
          <el-icon><Download /></el-icon>
          导出报告
        </el-button>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-grid">
      <div class="metric-card">
        <div class="metric-icon blue">
          <el-icon><User /></el-icon>
        </div>
        <div class="metric-content">
          <span class="metric-value">{{ formatNumber(metrics.activeUsers) }}</span>
          <span class="metric-label">活跃用户</span>
          <div class="metric-trend up">
            <el-icon><Top /></el-icon>
            <span>{{ metrics.userGrowth }}%</span>
          </div>
        </div>
      </div>
      
      <div class="metric-card">
        <div class="metric-icon green">
          <el-icon><Reading /></el-icon>
        </div>
        <div class="metric-content">
          <span class="metric-value">{{ formatNumber(metrics.knowledgeViews) }}</span>
          <span class="metric-label">知识浏览量</span>
          <div class="metric-trend up">
            <el-icon><Top /></el-icon>
            <span>{{ metrics.knowledgeGrowth }}%</span>
          </div>
        </div>
      </div>
      
      <div class="metric-card">
        <div class="metric-icon orange">
          <el-icon><Document /></el-icon>
        </div>
        <div class="metric-content">
          <span class="metric-value">{{ formatNumber(metrics.testCompleted) }}</span>
          <span class="metric-label">完成考试</span>
          <div class="metric-trend up">
            <el-icon><Top /></el-icon>
            <span>{{ metrics.testGrowth }}%</span>
          </div>
        </div>
      </div>
      
      <div class="metric-card">
        <div class="metric-icon purple">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="metric-content">
          <span class="metric-value">{{ formatNumber(metrics.reportsHandled) }}</span>
          <span class="metric-label">处理举报</span>
          <div class="metric-trend" :class="metrics.reportGrowth >= 0 ? 'up' : 'down'">
            <el-icon><component :is="metrics.reportGrowth >= 0 ? 'Top' : 'Bottom'" /></el-icon>
            <span>{{ Math.abs(metrics.reportGrowth) }}%</span>
          </div>
        </div>
      </div>
      
      <div class="metric-card">
        <div class="metric-icon cyan">
          <el-icon><Timer /></el-icon>
        </div>
        <div class="metric-content">
          <span class="metric-value">{{ formatDuration(metrics.avgLearningTime) }}</span>
          <span class="metric-label">平均学习时长</span>
          <div class="metric-trend up">
            <el-icon><Top /></el-icon>
            <span>{{ metrics.timeGrowth }}%</span>
          </div>
        </div>
      </div>
      
      <div class="metric-card">
        <div class="metric-icon pink">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="metric-content">
          <span class="metric-value">{{ metrics.avgScore }}分</span>
          <span class="metric-label">平均考试成绩</span>
          <div class="metric-trend up">
            <el-icon><Top /></el-icon>
            <span>{{ metrics.scoreGrowth }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <!-- 左侧：用户行为趋势 -->
      <div class="chart-card wide">
        <div class="chart-header">
          <h3>用户活跃趋势</h3>
          <el-radio-group v-model="activeChartType" size="small">
            <el-radio-button value="line">折线图</el-radio-button>
            <el-radio-button value="bar">柱状图</el-radio-button>
            <el-radio-button value="area">面积图</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="activeTrendChartRef" class="chart-container"></div>
      </div>

      <!-- 右侧：用户构成 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>用户角色分布</h3>
        </div>
        <div ref="userRoleChartRef" class="chart-container"></div>
        <div class="chart-legend">
          <div v-for="item in userRoleData" :key="item.name" class="legend-item">
            <span class="legend-dot" :style="{ background: item.color }"></span>
            <span class="legend-label">{{ item.name }}</span>
            <span class="legend-value">{{ item.value }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 第二行图表 -->
    <div class="charts-section">
      <!-- 内容热度分析 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>知识内容热度 TOP10</h3>
          <el-button text size="small" @click="handleViewMore('knowledge')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="hot-content-list">
          <div 
            v-for="(item, index) in hotKnowledge" 
            :key="item.id"
            class="hot-content-item"
          >
            <span class="hot-rank" :class="`rank-${index + 1}`">{{ index + 1 }}</span>
            <div class="hot-info">
              <span class="hot-title">{{ item.title }}</span>
              <div class="hot-stats">
                <span><el-icon><View /></el-icon> {{ formatNumber(item.views) }}</span>
                <span><el-icon><Star /></el-icon> {{ item.favorites }}</span>
              </div>
            </div>
            <div class="hot-bar">
              <div class="hot-bar-fill" :style="{ width: `${(item.views / hotKnowledge[0].views) * 100}%` }"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 学习行为分析 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>用户学习行为分析</h3>
        </div>
        <div ref="learningBehaviorChartRef" class="chart-container"></div>
      </div>

      <!-- 诈骗类型分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>诈骗类型分布</h3>
        </div>
        <div ref="fraudTypeChartRef" class="chart-container"></div>
      </div>
    </div>

    <!-- 第三行：详细数据表格 -->
    <div class="data-tables-section">
      <!-- 用户活跃明细 -->
      <div class="table-card">
        <div class="table-header">
          <h3>用户活跃明细</h3>
          <el-button text size="small" @click="handleExportTable('users')">
            导出 <el-icon><Download /></el-icon>
          </el-button>
        </div>
        <el-table :data="userActivityData" stripe size="small">
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="newUsers" label="新增用户" width="100" />
          <el-table-column prop="activeUsers" label="活跃用户" width="100" />
          <el-table-column prop="dauWau" label="DAU/WAU" width="100">
            <template #default="{ row }">
              {{ (row.activeUsers / row.weeklyUsers * 100).toFixed(1) }}%
            </template>
          </el-table-column>
          <el-table-column prop="avgSessionTime" label="平均会话时长" width="120">
            <template #default="{ row }">
              {{ formatDuration(row.avgSessionTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="bounceRate" label="跳出率" width="100">
            <template #default="{ row }">
              {{ row.bounceRate }}%
            </template>
          </el-table-column>
          <el-table-column prop="retentionRate" label="次日留存" width="100">
            <template #default="{ row }">
              <span :class="row.retentionRate >= 50 ? 'text-success' : 'text-warning'">
                {{ row.retentionRate }}%
              </span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 内容分析明细 -->
      <div class="table-card">
        <div class="table-header">
          <h3>内容分析明细</h3>
          <el-button text size="small" @click="handleExportTable('content')">
            导出 <el-icon><Download /></el-icon>
          </el-button>
        </div>
        <el-table :data="contentAnalysisData" stripe size="small">
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column prop="totalKnowledge" label="内容量" width="100" />
          <el-table-column prop="totalViews" label="总浏览" width="100" />
          <el-table-column prop="avgReadTime" label="平均阅读时长" width="120">
            <template #default="{ row }">
              {{ formatDuration(row.avgReadTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="completionRate" label="完成率" width="100">
            <template #default="{ row }">
              <el-progress :percentage="row.completionRate" :stroke-width="8" :show-text="false" />
              <span class="progress-text">{{ row.completionRate }}%</span>
            </template>
          </el-table-column>
          <el-table-column prop="interactionRate" label="互动率" width="100">
            <template #default="{ row }">
              {{ row.interactionRate }}%
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 用户漏斗分析 -->
    <div class="funnel-section">
      <div class="section-header">
        <h3>用户转化漏斗</h3>
        <p class="section-desc">分析用户从注册到完成学习的转化路径</p>
      </div>
      <div class="funnel-chart">
        <div 
          v-for="(stage, index) in funnelData" 
          :key="stage.name"
          class="funnel-stage"
          :style="{ width: `${100 - index * 12}%` }"
        >
          <div class="funnel-content">
            <span class="funnel-name">{{ stage.name }}</span>
            <span class="funnel-value">{{ formatNumber(stage.value) }}</span>
            <span class="funnel-rate" v-if="index > 0">
              转化率: {{ ((stage.value / funnelData[index - 1].value) * 100).toFixed(1) }}%
            </span>
          </div>
          <div class="funnel-bar" :style="{ background: stage.color }">
            <div 
              class="funnel-bar-fill" 
              :style="{ width: `${(stage.value / funnelData[0].value) * 100}%` }"
            ></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 用户画像分析 -->
    <div class="profile-section">
      <div class="section-header">
        <h3>用户画像分析</h3>
      </div>
      <div class="profile-grid">
        <div class="profile-card">
          <h4>学习时段分布</h4>
          <div ref="studyTimeChartRef" class="chart-container-small"></div>
        </div>
        <div class="profile-card">
          <h4>设备类型分布</h4>
          <div ref="deviceChartRef" class="chart-container-small"></div>
        </div>
        <div class="profile-card">
          <h4>用户年龄段分布</h4>
          <div ref="ageChartRef" class="chart-container-small"></div>
        </div>
        <div class="profile-card">
          <h4>学习偏好TOP5</h4>
          <div class="preference-list">
            <div v-for="(pref, index) in learningPreferences" :key="pref.name" class="preference-item">
              <span class="pref-rank">{{ index + 1 }}</span>
              <span class="pref-name">{{ pref.name }}</span>
              <el-progress :percentage="pref.percentage" :stroke-width="6" :show-text="false" />
              <span class="pref-value">{{ pref.percentage }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 实时数据监控 -->
    <div class="realtime-section">
      <div class="section-header">
        <h3>实时数据监控</h3>
        <div class="realtime-indicator">
          <span class="pulse-dot"></span>
          <span>实时更新中</span>
        </div>
      </div>
      <div class="realtime-grid">
        <div class="realtime-card">
          <div class="realtime-value">{{ realtimeData.currentOnline }}</div>
          <div class="realtime-label">当前在线</div>
          <div class="realtime-trend up">
            <el-icon><Top /></el-icon>
            <span>+{{ realtimeData.onlineGrowth }}</span>
          </div>
        </div>
        <div class="realtime-card">
          <div class="realtime-value">{{ realtimeData.todayPV }}</div>
          <div class="realtime-label">今日PV</div>
          <div class="realtime-trend up">
            <el-icon><Top /></el-icon>
            <span>+{{ realtimeData.pvGrowth }}%</span>
          </div>
        </div>
        <div class="realtime-card">
          <div class="realtime-value">{{ realtimeData.todayUV }}</div>
          <div class="realtime-label">今日UV</div>
          <div class="realtime-trend up">
            <el-icon><Top /></el-icon>
            <span>+{{ realtimeData.uvGrowth }}%</span>
          </div>
        </div>
        <div class="realtime-card">
          <div class="realtime-value">{{ realtimeData.avgResponseTime }}ms</div>
          <div class="realtime-label">平均响应</div>
          <div class="realtime-trend" :class="realtimeData.responseTrend === 'up' ? 'up' : 'down'">
            <el-icon><component :is="realtimeData.responseTrend === 'up' ? 'Top' : 'Bottom'" /></el-icon>
            <span>{{ realtimeData.responseChange }}ms</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, User, Reading, Document, Warning, Timer, TrendCharts, View, Star, ArrowRight, Top, Bottom } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 图表引用
const activeTrendChartRef = ref<HTMLElement>()
const userRoleChartRef = ref<HTMLElement>()
const learningBehaviorChartRef = ref<HTMLElement>()
const fraudTypeChartRef = ref<HTMLElement>()
const studyTimeChartRef = ref<HTMLElement>()
const deviceChartRef = ref<HTMLElement>()
const ageChartRef = ref<HTMLElement>()

let charts: echarts.ECharts[] = []
let updateTimer: number | null = null

// 时间范围
const timeRange = ref('week')
const activeChartType = ref('line')

// 核心指标
const metrics = reactive({
  activeUsers: 12580,
  userGrowth: 12.5,
  knowledgeViews: 85642,
  knowledgeGrowth: 8.3,
  testCompleted: 3256,
  testGrowth: 15.2,
  reportsHandled: 189,
  reportGrowth: -5.2,
  avgLearningTime: 2580,
  timeGrowth: 6.8,
  avgScore: 82.5,
  scoreGrowth: 3.2
})

// 用户角色数据
const userRoleData = [
  { name: '学生', value: 65, color: '#409eff' },
  { name: '教师', value: 20, color: '#67c23a' },
  { name: '管理员', value: 8, color: '#e6a23c' },
  { name: '专家', value: 7, color: '#9c27b0' }
]

// 热门知识
const hotKnowledge = ref([
  { id: 1, title: '如何识别网络刷单诈骗', views: 12580, favorites: 856 },
  { id: 2, title: '杀猪盘骗局深度解析', views: 11250, favorites: 742 },
  { id: 3, title: '冒充公检法诈骗防范指南', views: 10890, favorites: 698 },
  { id: 4, title: '虚假投资理财诈骗识别', views: 9650, favorites: 621 },
  { id: 5, title: '网络购物退款诈骗套路', views: 8920, favorites: 585 },
  { id: 6, title: '校园贷注销骗局防范', views: 7850, favorites: 512 },
  { id: 7, title: '假冒客服诈骗识别技巧', views: 7230, favorites: 478 },
  { id: 8, title: '游戏装备交易诈骗警示', views: 6890, favorites: 445 },
  { id: 9, title: '熟人借钱诈骗防范要点', views: 6450, favorites: 412 },
  { id: 10, title: '虚假招聘诈骗识别方法', views: 5980, favorites: 389 }
])

// 用户活跃数据
const userActivityData = ref([
  { date: '2026-01-15', newUsers: 125, activeUsers: 1258, weeklyUsers: 5680, avgSessionTime: 1860, bounceRate: 32, retentionRate: 68 },
  { date: '2026-01-16', newUsers: 142, activeUsers: 1385, weeklyUsers: 5720, avgSessionTime: 1920, bounceRate: 28, retentionRate: 72 },
  { date: '2026-01-17', newUsers: 138, activeUsers: 1420, weeklyUsers: 5780, avgSessionTime: 2100, bounceRate: 25, retentionRate: 75 },
  { date: '2026-01-18', newUsers: 156, activeUsers: 1568, weeklyUsers: 5850, avgSessionTime: 1980, bounceRate: 30, retentionRate: 70 },
  { date: '2026-01-19', newUsers: 168, activeUsers: 1685, weeklyUsers: 5920, avgSessionTime: 2250, bounceRate: 22, retentionRate: 78 }
])

// 内容分析数据
const contentAnalysisData = ref([
  { category: '网络诈骗', totalKnowledge: 156, totalViews: 42580, avgReadTime: 480, completionRate: 72, interactionRate: 18 },
  { category: '防骗技巧', totalKnowledge: 98, totalViews: 35820, avgReadTime: 420, completionRate: 68, interactionRate: 22 },
  { category: '案例分析', totalKnowledge: 45, totalViews: 28950, avgReadTime: 560, completionRate: 85, interactionRate: 35 },
  { category: '法律法规', totalKnowledge: 32, totalViews: 15680, avgReadTime: 320, completionRate: 55, interactionRate: 12 },
  { category: '安全提醒', totalKnowledge: 78, totalViews: 41250, avgReadTime: 280, completionRate: 62, interactionRate: 15 }
])

// 漏斗数据
const funnelData = [
  { name: '访问量', value: 125680, color: '#409eff' },
  { name: '注册用户', value: 45680, color: '#67c23a' },
  { name: '活跃用户', value: 28560, color: '#e6a23c' },
  { name: '学习完成', value: 15820, color: '#9c27b0' },
  { name: '考试通过', value: 12580, color: '#f56c6c' }
]

// 学习偏好
const learningPreferences = ref([
  { name: '案例分析类', percentage: 38 },
  { name: '视频教程类', percentage: 28 },
  { name: '互动演练类', percentage: 18 },
  { name: '图文讲解类', percentage: 12 },
  { name: '测试练习类', percentage: 4 }
])

// 实时数据
const realtimeData = reactive({
  currentOnline: 1256,
  onlineGrowth: 58,
  todayPV: 89562,
  pvGrowth: 12.5,
  todayUV: 12580,
  uvGrowth: 8.3,
  avgResponseTime: 128,
  responseTrend: 'down',
  responseChange: 15
})

// 格式化数字
function formatNumber(num: number): string {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toLocaleString()
}

// 格式化时长
function formatDuration(seconds: number): string {
  if (seconds < 60) return `${seconds}秒`
  if (seconds < 3600) return `${Math.floor(seconds / 60)}分`
  const hours = Math.floor(seconds / 3600)
  const mins = Math.floor((seconds % 3600) / 60)
  return `${hours}小时${mins}分`
}

// 处理时间范围变化
function handleTimeRangeChange() {
  initCharts()
  ElMessage.success('数据已更新')
}

// 导出报告
function handleExportReport() {
  ElMessage.success('报告导出中，请稍候...')
}

// 查看更多
function handleViewMore(type: string) {
  ElMessage.info(`查看更多${type === 'knowledge' ? '知识内容' : '数据'}`)
}

// 导出表格
function handleExportTable(type: string) {
  ElMessage.success(`${type === 'users' ? '用户活跃' : '内容分析'}数据导出中...`)
}

// 初始化图表
function initCharts() {
  // 销毁旧图表
  charts.forEach(chart => chart.dispose())
  charts = []

  // 用户活跃趋势图
  if (activeTrendChartRef.value) {
    const chart = echarts.init(activeTrendChartRef.value)
    const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    const data = [1250, 1380, 1520, 1450, 1680, 1820, 1750]
    
    let seriesType: 'line' | 'bar' = 'line'
    let areaStyle: any = null
    
    if (activeChartType.value === 'bar') {
      seriesType = 'bar'
    } else if (activeChartType.value === 'area') {
      areaStyle = {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.4)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      }
    }
    
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: days, boundaryGap: seriesType === 'bar' },
      yAxis: { type: 'value' },
      series: [{
        type: seriesType,
        data,
        smooth: true,
        areaStyle,
        lineStyle: { color: '#409eff', width: 3 },
        itemStyle: { color: '#409eff' }
      }]
    })
    charts.push(chart)
  }

  // 用户角色饼图
  if (userRoleChartRef.value) {
    const chart = echarts.init(userRoleChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c}% ({d}%)' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' }
        },
        data: userRoleData.map(item => ({
          name: item.name,
          value: item.value,
          itemStyle: { color: item.color }
        }))
      }]
    })
    charts.push(chart)
  }

  // 学习行为分析
  if (learningBehaviorChartRef.value) {
    const chart = echarts.init(learningBehaviorChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['浏览', '学习', '练习', '考试'], bottom: 0 },
      xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
      yAxis: { type: 'value' },
      series: [
        { name: '浏览', type: 'bar', data: [320, 380, 420, 390, 450, 520, 480] },
        { name: '学习', type: 'bar', data: [180, 220, 250, 230, 280, 320, 290] },
        { name: '练习', type: 'bar', data: [120, 150, 180, 160, 200, 240, 210] },
        { name: '考试', type: 'bar', data: [45, 65, 85, 75, 95, 110, 98] }
      ]
    })
    charts.push(chart)
  }

  // 诈骗类型分布
  if (fraudTypeChartRef.value) {
    const chart = echarts.init(fraudTypeChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: ['50%', '75%'],
        data: [
          { name: '刷单诈骗', value: 28, itemStyle: { color: '#409eff' } },
          { name: '杀猪盘', value: 22, itemStyle: { color: '#67c23a' } },
          { name: '冒充公检法', value: 18, itemStyle: { color: '#e6a23c' } },
          { name: '虚假投资', value: 15, itemStyle: { color: '#9c27b0' } },
          { name: '网购诈骗', value: 10, itemStyle: { color: '#f56c6c' } },
          { name: '其他', value: 7, itemStyle: { color: '#909399' } }
        ]
      }]
    })
    charts.push(chart)
  }

  // 学习时段分布
  if (studyTimeChartRef.value) {
    const chart = echarts.init(studyTimeChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['0-6', '6-9', '9-12', '12-14', '14-18', '18-21', '21-24'] },
      yAxis: { type: 'value' },
      series: [{
        type: 'bar',
        data: [120, 680, 1250, 820, 980, 1580, 680],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ])
        }
      }]
    })
    charts.push(chart)
  }

  // 设备类型分布
  if (deviceChartRef.value) {
    const chart = echarts.init(deviceChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: ['40%', '60%'],
        data: [
          { name: '移动端', value: 62, itemStyle: { color: '#409eff' } },
          { name: 'PC端', value: 32, itemStyle: { color: '#67c23a' } },
          { name: '平板', value: 6, itemStyle: { color: '#e6a23c' } }
        ]
      }]
    })
    charts.push(chart)
  }

  // 年龄段分布
  if (ageChartRef.value) {
    const chart = echarts.init(ageChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['18以下', '18-22', '23-28', '29-35', '35以上'] },
      yAxis: { type: 'value' },
      series: [{
        type: 'line',
        data: [15, 58, 22, 4, 1],
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(156, 39, 176, 0.4)' },
            { offset: 1, color: 'rgba(156, 39, 176, 0.05)' }
          ])
        },
        lineStyle: { color: '#9c27b0', width: 3 },
        itemStyle: { color: '#9c27b0' }
      }]
    })
    charts.push(chart)
  }
}

// 窗口调整
function handleResize() {
  charts.forEach(chart => chart.resize())
}

// 实时数据更新
function updateRealtimeData() {
  realtimeData.currentOnline = Math.floor(1000 + Math.random() * 500)
  realtimeData.todayPV = Math.floor(80000 + Math.random() * 20000)
  realtimeData.todayUV = Math.floor(10000 + Math.random() * 5000)
  realtimeData.avgResponseTime = Math.floor(100 + Math.random() * 50)
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
  updateTimer = window.setInterval(updateRealtimeData, 5000)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (updateTimer) clearInterval(updateTimer)
  charts.forEach(chart => chart.dispose())
})
</script>

<style scoped>
.data-analysis-page {
  padding: 24px;
  background: #f5f7fa;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left h1 {
  margin: 0 0 4px;
  font-size: 24px;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 核心指标网格 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s, box-shadow 0.3s;
}

.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.metric-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}

.metric-icon.blue { background: #ecf5ff; color: #409eff; }
.metric-icon.green { background: #f0f9eb; color: #67c23a; }
.metric-icon.orange { background: #fdf6ec; color: #e6a23c; }
.metric-icon.purple { background: #f4f0ff; color: #9c27b0; }
.metric-icon.cyan { background: #e8f8f8; color: #008080; }
.metric-icon.pink { background: #fdf0f5; color: #e91e63; }

.metric-content {
  flex: 1;
  min-width: 0;
}

.metric-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #333;
  line-height: 1.2;
}

.metric-label {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.metric-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
}

.metric-trend.up { color: #67c23a; }
.metric-trend.down { color: #f56c6c; }

/* 图表区域 */
.charts-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.charts-section.three-col {
  grid-template-columns: repeat(3, 1fr);
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.chart-card.wide {
  grid-column: span 1;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
}

.chart-container {
  height: 280px;
}

.chart-container-small {
  height: 180px;
}

.chart-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.legend-label {
  font-size: 13px;
  color: #666;
}

.legend-value {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

/* 热门内容列表 */
.hot-content-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-content-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.hot-rank {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  background: #f0f0f0;
  color: #666;
  flex-shrink: 0;
}

.hot-rank.rank-1 { background: #ffd700; color: #fff; }
.hot-rank.rank-2 { background: #c0c0c0; color: #fff; }
.hot-rank.rank-3 { background: #cd7f32; color: #fff; }

.hot-info {
  flex: 1;
  min-width: 0;
}

.hot-title {
  display: block;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.hot-stats span {
  display: flex;
  align-items: center;
  gap: 2px;
}

.hot-bar {
  width: 60px;
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.hot-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px;
  transition: width 0.5s ease;
}

/* 数据表格 */
.data-tables-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.table-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-header h3 {
  margin: 0;
  font-size: 16px;
}

.progress-text {
  font-size: 12px;
  color: #999;
  margin-left: 4px;
}

.text-success { color: #67c23a; }
.text-warning { color: #e6a23c; }

/* 漏斗分析 */
.funnel-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  margin-bottom: 24px;
}

.section-header h3 {
  margin: 0 0 4px;
  font-size: 18px;
}

.section-desc {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.funnel-chart {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.funnel-stage {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.funnel-content {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  justify-content: center;
}

.funnel-name {
  font-size: 14px;
  color: #666;
  width: 80px;
  text-align: right;
}

.funnel-value {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}

.funnel-rate {
  font-size: 12px;
  color: #67c23a;
  padding: 2px 8px;
  background: #f0f9eb;
  border-radius: 10px;
}

.funnel-bar {
  width: 100%;
  height: 32px;
  border-radius: 16px;
  overflow: hidden;
}

.funnel-bar-fill {
  height: 100%;
  background: rgba(255, 255, 255, 0.5);
  transition: width 1s ease;
}

/* 用户画像 */
.profile-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.profile-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.profile-card {
  padding: 16px;
  background: #f9f9f9;
  border-radius: 10px;
}

.profile-card h4 {
  margin: 0 0 16px;
  font-size: 14px;
  color: #333;
}

.preference-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preference-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pref-rank {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #409eff;
  color: white;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pref-name {
  font-size: 13px;
  color: #666;
  width: 80px;
}

.preference-item :deep(.el-progress) {
  flex: 1;
}

.pref-value {
  font-size: 12px;
  font-weight: 600;
  color: #333;
  width: 40px;
  text-align: right;
}

/* 实时监控 */
.realtime-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.realtime-section .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.realtime-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #67c23a;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #67c23a;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.5); opacity: 0.5; }
}

.realtime-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.realtime-card {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.realtime-value {
  font-size: 32px;
  font-weight: 700;
}

.realtime-label {
  font-size: 14px;
  opacity: 0.9;
  margin: 4px 0;
}

.realtime-trend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
  font-size: 12px;
}

.realtime-trend.up { color: #90f7a8; }
.realtime-trend.down { color: #f89b9b; }

/* 响应式 */
@media (max-width: 1400px) {
  .metrics-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .charts-section.three-col {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .profile-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .realtime-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 1024px) {
  .data-tables-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .data-analysis-page {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-section.three-col {
    grid-template-columns: 1fr;
  }
  
  .profile-grid {
    grid-template-columns: 1fr;
  }
  
  .realtime-grid {
    grid-template-columns: 1fr;
  }
}
</style>
