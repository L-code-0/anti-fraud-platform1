<template>
  <div class="report-page">
    <!-- 时间筛选 -->
    <div class="filter-bar">
      <el-radio-group v-model="period" @change="handlePeriodChange">
        <el-radio-button value="week">本周</el-radio-button>
        <el-radio-button value="month">本月</el-radio-button>
        <el-radio-button value="semester">本学期</el-radio-button>
      </el-radio-group>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出报告
      </el-button>
    </div>

    <!-- 概览卡片 -->
    <div class="overview-cards">
      <div class="overview-card">
        <div class="card-icon blue">
          <el-icon><Timer /></el-icon>
        </div>
        <div class="card-info">
          <span class="card-value">{{ formatDuration(report.summary.totalLearningTime) }}</span>
          <span class="card-label">学习时长</span>
        </div>
        <div class="card-trend up">
          <el-icon><ArrowUp /></el-icon>
          <span>12%</span>
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-icon green">
          <el-icon><Reading /></el-icon>
        </div>
        <div class="card-info">
          <span class="card-value">{{ report.summary.knowledgeRead }}</span>
          <span class="card-label">知识阅读</span>
        </div>
        <div class="card-trend up">
          <el-icon><ArrowUp /></el-icon>
          <span>8%</span>
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-icon orange">
          <el-icon><Document /></el-icon>
        </div>
        <div class="card-info">
          <span class="card-value">{{ report.summary.testsTaken }}</span>
          <span class="card-label">参加考试</span>
        </div>
        <div class="card-trend up">
          <el-icon><ArrowUp /></el-icon>
          <span>5%</span>
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-icon purple">
          <el-icon><Trophy /></el-icon>
        </div>
        <div class="card-info">
          <span class="card-value">{{ report.summary.avgScore }}</span>
          <span class="card-label">平均分数</span>
        </div>
        <div class="card-trend up">
          <el-icon><ArrowUp /></el-icon>
          <span>3%</span>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 学习进度雷达图 -->
      <div class="chart-card">
        <h3>能力雷达图</h3>
        <div class="chart-container">
          <div ref="radarChartRef" class="chart"></div>
        </div>
      </div>
      
      <!-- 周学习趋势 -->
      <div class="chart-card">
        <h3>学习趋势</h3>
        <div class="chart-container">
          <div ref="lineChartRef" class="chart"></div>
        </div>
      </div>
      
      <!-- 知识分类进度 -->
      <div class="chart-card">
        <h3>知识分类掌握度</h3>
        <div class="chart-container">
          <div ref="barChartRef" class="chart"></div>
        </div>
      </div>
      
      <!-- 薄弱知识点 -->
      <div class="chart-card">
        <h3>薄弱知识点</h3>
        <div class="weak-points-list">
          <div 
            v-for="(point, index) in report.weakPoints" 
            :key="point.knowledgeId"
            class="weak-point-item"
          >
            <span class="point-rank">{{ index + 1 }}</span>
            <div class="point-info">
              <span class="point-title">{{ point.title }}</span>
              <el-progress 
                :percentage="point.correctRate" 
                :stroke-width="6"
                :show-text="false"
                :color="getProgressColor(point.correctRate)"
              />
            </div>
            <span class="point-rate">{{ point.correctRate }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 学习数据表格 -->
    <div class="data-table-card">
      <h3>详细数据</h3>
      <el-table :data="report.weeklyProgress" stripe>
        <el-table-column prop="day" label="日期" width="120" />
        <el-table-column label="学习时长" width="120">
          <template #default="{ row }">
            {{ formatDuration(row.learningTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="testsCompleted" label="完成考试" width="120" />
        <el-table-column prop="pointsEarned" label="获得积分" />
        <el-table-column label="操作" width="100">
          <template #default>
            <el-button text type="primary" size="small">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 建议 -->
    <div class="suggestions-card">
      <h3>学习建议</h3>
      <div class="suggestions-list">
        <div v-for="suggestion in suggestions" :key="suggestion.type" class="suggestion-item">
          <div class="suggestion-icon" :class="suggestion.type">
            <el-icon><component :is="suggestion.icon" /></el-icon>
          </div>
          <div class="suggestion-content">
            <h4>{{ suggestion.title }}</h4>
            <p>{{ suggestion.content }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { Download, Timer, Reading, Document, Trophy, ArrowUp, Warning, TrophyBase, Star } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const period = ref('week')
const radarChartRef = ref<HTMLElement>()
const lineChartRef = ref<HTMLElement>()
const barChartRef = ref<HTMLElement>()

let radarChart: echarts.ECharts | null = null
let lineChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null

const report = reactive({
  summary: {
    totalLearningTime: 1250,
    knowledgeRead: 28,
    testsTaken: 5,
    avgScore: 85,
    simulationsCompleted: 3,
    pointsEarned: 680,
    rank: 23
  },
  weakPoints: [
    { knowledgeId: 1, title: '网络购物诈骗防范', correctRate: 45 },
    { knowledgeId: 2, title: '冒充客服诈骗识别', correctRate: 52 },
    { knowledgeId: 3, title: '杀猪盘骗局解析', correctRate: 58 }
  ],
  weeklyProgress: [
    { day: '周一', learningTime: 45, testsCompleted: 1, pointsEarned: 80 },
    { day: '周二', learningTime: 60, testsCompleted: 0, pointsEarned: 50 },
    { day: '周三', learningTime: 30, testsCompleted: 1, pointsEarned: 70 },
    { day: '周四', learningTime: 90, testsCompleted: 0, pointsEarned: 100 },
    { day: '周五', learningTime: 75, testsCompleted: 2, pointsEarned: 120 },
    { day: '周六', learningTime: 120, testsCompleted: 1, pointsEarned: 150 },
    { day: '周日', learningTime: 55, testsCompleted: 0, pointsEarned: 60 }
  ],
  radarData: [
    { dimension: '防骗意识', score: 85 },
    { dimension: '识别能力', score: 72 },
    { dimension: '应对能力', score: 68 },
    { dimension: '知识储备', score: 90 },
    { dimension: '心理素质', score: 75 }
  ]
})

const suggestions = [
  {
    type: 'warning',
    icon: Warning,
    title: '加强薄弱环节',
    content: '您在"网络购物诈骗防范"方面正确率较低，建议重点学习相关知识并多加练习。'
  },
  {
    type: 'trophy',
    icon: TrophyBase,
    title: '保持学习热情',
    content: '您的学习时长较上周有所提升，继续保持！本周学习时长已达 20.8 小时。'
  },
  {
    type: 'star',
    icon: Star,
    title: '考试技巧',
    content: '建议您在考试前复习错题本中的题目，可以有效提高考试成绩。'
  }
]

function formatDuration(minutes: number): string {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}h ${mins}m` : `${mins}m`
}

function getProgressColor(rate: number): string {
  if (rate < 50) return '#f56c6c'
  if (rate < 70) return '#e6a23c'
  return '#67c23a'
}

function handlePeriodChange() {
  // 重新加载数据
}

function handleExport() {
  // 导出报告
}

function initCharts() {
  initRadarChart()
  initLineChart()
  initBarChart()
}

function initRadarChart() {
  if (!radarChartRef.value) return
  radarChart = echarts.init(radarChartRef.value)
  radarChart.setOption({
    radar: {
      indicator: report.radarData.map(item => ({
        name: item.dimension,
        max: 100
      })),
      shape: 'polygon',
      splitNumber: 4,
      axisName: {
        color: '#666'
      },
      splitLine: {
        lineStyle: {
          color: '#e0e0e0'
        }
      },
      splitArea: {
        show: true,
        areaStyle: {
          color: ['#f8f9fa', '#ffffff']
        }
      }
    },
    series: [{
      type: 'radar',
      data: [{
        value: report.radarData.map(item => item.score),
        name: '能力评估',
        areaStyle: {
          color: 'rgba(102, 126, 234, 0.3)'
        },
        lineStyle: {
          color: '#667eea'
        },
        itemStyle: {
          color: '#667eea'
        }
      }]
    }]
  })
}

function initLineChart() {
  if (!lineChartRef.value) return
  lineChart = echarts.init(lineChartRef.value)
  lineChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: report.weeklyProgress.map(p => p.day),
      boundaryGap: false,
      axisLine: {
        lineStyle: { color: '#e0e0e0' }
      }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0' } }
    },
    series: [{
      type: 'line',
      data: report.weeklyProgress.map(p => p.learningTime),
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(102, 126, 234, 0.4)' },
          { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
        ])
      },
      lineStyle: { color: '#667eea', width: 3 },
      itemStyle: { color: '#667eea' },
      symbol: 'circle',
      symbolSize: 8
    }]
  })
}

function initBarChart() {
  if (!barChartRef.value) return
  barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['冒充公检法', '网络刷单', '杀猪盘', '冒充客服', '虚假投资'],
      axisLine: { lineStyle: { color: '#e0e0e0' } }
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0' } }
    },
    series: [{
      type: 'bar',
      data: [85, 72, 68, 90, 65],
      barWidth: '50%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#667eea' },
          { offset: 1, color: '#764ba2' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }]
  })
}

function handleResize() {
  radarChart?.resize()
  lineChart?.resize()
  barChart?.resize()
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  radarChart?.dispose()
  lineChart?.dispose()
  barChart?.dispose()
})
</script>

<style scoped>
.report-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 概览卡片 */
.overview-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.overview-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s, box-shadow 0.3s;
}

.overview-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-right: 16px;
}

.card-icon.blue { background: #ecf5ff; color: #409eff; }
.card-icon.green { background: #f0f9eb; color: #67c23a; }
.card-icon.orange { background: #fdf6ec; color: #e6a23c; }
.card-icon.purple { background: #f4f0ff; color: #9c27b0; }

.card-info {
  flex: 1;
}

.card-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.card-label {
  font-size: 14px;
  color: #999;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 13px;
}

.card-trend.up { color: #67c23a; }
.card-trend.down { color: #f56c6c; }

/* 图表区域 */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.chart-card h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #333;
}

.chart-container {
  height: 280px;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 薄弱知识点 */
.weak-points-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.weak-point-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.point-rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #f56c6c;
  color: white;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.point-info {
  flex: 1;
  min-width: 0;
}

.point-title {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 6px;
}

.point-rate {
  font-size: 14px;
  font-weight: 600;
  color: #f56c6c;
  margin-left: 12px;
}

/* 数据表格 */
.data-table-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.data-table-card h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #333;
}

/* 建议 */
.suggestions-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.suggestions-card h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #333;
}

.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.suggestion-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 10px;
}

.suggestion-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.suggestion-icon.warning { background: #fef0f0; color: #f56c6c; }
.suggestion-icon.trophy { background: #fdf6ec; color: #e6a23c; }
.suggestion-icon.star { background: #ecf5ff; color: #409eff; }

.suggestion-content h4 {
  margin: 0 0 4px;
  font-size: 15px;
  color: #333;
}

.suggestion-content p {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

@media (max-width: 1024px) {
  .overview-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .report-page {
    padding: 16px;
  }
  
  .overview-cards {
    grid-template-columns: 1fr;
  }
  
  .filter-bar {
    flex-direction: column;
    gap: 16px;
  }
}
</style>
