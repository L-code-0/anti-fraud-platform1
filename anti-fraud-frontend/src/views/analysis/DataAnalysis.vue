<template>
  <div class="data-analysis-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <el-icon><DataAnalysis /></el-icon>
          数据分析中心
        </h1>
        <p>深入分析学习数据，洞察学习趋势</p>
        <div class="header-stats">
          <div class="stat-item">
            <el-icon><TrendCharts /></el-icon>
            <span>{{ totalUsers }} 名用户</span>
          </div>
          <div class="stat-item">
            <el-icon><Document /></el-icon>
            <span>{{ totalKnowledge }} 篇知识</span>
          </div>
          <div class="stat-item">
            <el-icon><View /></el-icon>
            <span>{{ totalViews }} 次浏览</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 时间选择器 -->
    <el-card class="filter-card" shadow="hover">
      <div class="filter-bar">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          size="large"
          class="date-picker"
          @change="handleDateChange"
        />
        <el-select v-model="dataType" placeholder="数据类型" size="large" class="data-select">
          <el-option label="学习数据" :value="1" />
          <el-option label="考试数据" :value="2" />
          <el-option label="预警数据" :value="3" />
        </el-select>
        <el-button type="primary" size="large" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </el-card>

    <!-- 数据概览 -->
    <el-row :gutter="32" style="margin-bottom: 32px;">
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="overview-content">
            <el-icon class="overview-icon"><User /></el-icon>
            <div class="overview-info">
              <div class="overview-value">{{ activeUsers }}</div>
              <div class="overview-label">活跃用户</div>
              <div class="overview-change positive">+{{ userGrowth }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="overview-content">
            <el-icon class="overview-icon"><Reading /></el-icon>
            <div class="overview-info">
              <div class="overview-value">{{ studyHours }}</div>
              <div class="overview-label">学习时长</div>
              <div class="overview-change positive">+{{ timeGrowth }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="overview-content">
            <el-icon class="overview-icon"><Star /></el-icon>
            <div class="overview-info">
              <div class="overview-value">{{ avgScore }}</div>
              <div class="overview-label">平均分数</div>
              <div class="overview-change" :class="scoreChange >= 0 ? 'positive' : 'negative'">
                {{ scoreChange >= 0 ? '+' : '' }}{{ scoreChange }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="overview-content">
            <el-icon class="overview-icon"><Warning /></el-icon>
            <div class="overview-info">
              <div class="overview-value">{{ warningCount }}</div>
              <div class="overview-label">预警数量</div>
              <div class="overview-change" :class="warningChange >= 0 ? 'negative' : 'positive'">
                {{ warningChange >= 0 ? '+' : '' }}{{ warningChange }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="32">
      <!-- 学习趋势 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3>学习趋势</h3>
            </div>
          </template>
          <LineChart :data="studyTrendData" />
        </el-card>
      </el-col>
      <!-- 知识类别分布 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3>知识类别分布</h3>
            </div>
          </template>
          <PieChart :data="categoryData" />
        </el-card>
      </el-col>
      <!-- 学习时间分布 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3>学习时间分布</h3>
            </div>
          </template>
          <BarChart :data="timeDistributionData" />
        </el-card>
      </el-col>
      <!-- 风险等级分布 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3>风险等级分布</h3>
            </div>
          </template>
          <PieChart :data="riskLevelData" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细数据表格 -->
    <el-card class="table-card" shadow="hover" style="margin-top: 32px;">
      <template #header>
        <div class="card-header">
          <h3>详细数据</h3>
          <el-button type="primary" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </div>
      </template>

      <el-table :data="detailData" style="width: 100%">
        <el-table-column prop="date" label="日期" width="180" />
        <el-table-column prop="users" label="活跃用户" width="120" />
        <el-table-column prop="studyTime" label="学习时长(小时)" width="150" />
        <el-table-column prop="completion" label="完成率(%)" width="120" />
        <el-table-column prop="avgScore" label="平均分" width="100" />
        <el-table-column prop="warnings" label="预警数" width="100" />
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="detailData.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { 
  DataAnalysis, TrendCharts, Document, View, User, 
  Reading, Star, Warning, Refresh, Download 
} from '@element-plus/icons-vue'
import LineChart from '@/components/charts/LineChart.vue'
import PieChart from '@/components/charts/PieChart.vue'
import BarChart from '@/components/charts/BarChart.vue'

// 时间范围
const dateRange = ref<[Date, Date] | null>(null)
const dataType = ref(1)

// 分页
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 统计数据
const totalUsers = ref(1250)
const totalKnowledge = ref(568)
const totalViews = ref(125000)
const activeUsers = ref(856)
const studyHours = ref('1,245')
const avgScore = ref(82)
const warningCount = ref(85)
const userGrowth = ref(12.5)
const timeGrowth = ref(8.3)
const scoreChange = ref(2.1)
const warningChange = ref(-5.2)

// 图表数据
const studyTrendData = ref({
  labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
  datasets: [{
    label: '学习人数',
    data: [650, 720, 810, 780, 850, 920],
    borderColor: '#3b82f6',
    backgroundColor: 'rgba(59, 130, 246, 0.1)'
  }, {
    label: '学习时长',
    data: [120, 150, 180, 160, 190, 210],
    borderColor: '#42b883',
    backgroundColor: 'rgba(66, 184, 131, 0.1)'
  }]
})

const categoryData = ref({
  labels: ['电信诈骗', '网络诈骗', '校园贷', '兼职诈骗', '其他'],
  datasets: [{
    data: [35, 25, 15, 20, 5],
    backgroundColor: ['#3b82f6', '#42b883', '#f59e0b', '#ef4444', '#8b5cf6']
  }]
})

const timeDistributionData = ref({
  labels: ['00:00-06:00', '06:00-12:00', '12:00-18:00', '18:00-24:00'],
  datasets: [{
    label: '学习时长(小时)',
    data: [50, 320, 280, 595],
    backgroundColor: ['#3b82f6', '#42b883', '#f59e0b', '#ef4444']
  }]
})

const riskLevelData = ref({
  labels: ['高风险', '中风险', '低风险'],
  datasets: [{
    data: [15, 35, 45],
    backgroundColor: ['#ef4444', '#f59e0b', '#3b82f6']
  }]
})

// 详细数据
const detailData = ref<any[]>([])

// 加载数据
const loadData = async () => {
  try {
    const params = {
      startDate: dateRange.value?.[0]?.toISOString(),
      endDate: dateRange.value?.[1]?.toISOString(),
      dataType: dataType.value,
      page: page.value,
      size: size.value
    }
    const res = await get('/analysis/data', params)
    // 模拟数据
    detailData.value = Array.from({ length: 10 }, (_, i) => ({
      date: `2026-01-${String(i + 1).padStart(2, '0')}`,
      users: Math.floor(Math.random() * 200) + 700,
      studyTime: Math.floor(Math.random() * 50) + 100,
      completion: Math.floor(Math.random() * 20) + 70,
      avgScore: Math.floor(Math.random() * 20) + 70,
      warnings: Math.floor(Math.random() * 15) + 5
    }))
    total.value = 30
  } catch (e) {
    // 模拟数据
    detailData.value = Array.from({ length: 10 }, (_, i) => ({
      date: `2026-01-${String(i + 1).padStart(2, '0')}`,
      users: Math.floor(Math.random() * 200) + 700,
      studyTime: Math.floor(Math.random() * 50) + 100,
      completion: Math.floor(Math.random() * 20) + 70,
      avgScore: Math.floor(Math.random() * 20) + 70,
      warnings: Math.floor(Math.random() * 15) + 5
    }))
    total.value = 30
  }
}

// 日期变化
const handleDateChange = () => {
  page.value = 1
  loadData()
}

// 刷新数据
const handleRefresh = () => {
  loadData()
  ElMessage.success('数据已刷新')
}

// 导出数据
const handleExport = () => {
  ElMessage.success('数据导出功能开发中')
}

// 分页
const handlePageChange = () => {
  loadData()
}

const handleSizeChange = () => {
  page.value = 1
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.data-analysis-page {
  padding: 0 0 40px;
  min-height: calc(100vh - 160px);
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
  padding: 60px 24px;
  margin-bottom: 40px;
  border-radius: 16px;
  color: white;
  position: relative;
  overflow: hidden;
  text-align: center;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.header-content {
  position: relative;
  z-index: 1;
}

.header-content h1 {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 40px;
  font-weight: 700;
  margin-bottom: 16px;
  justify-content: center;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.header-content p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 24px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.header-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-top: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-4px);
}

.stat-item span {
  font-size: 16px;
  font-weight: 500;
}

/* 筛选栏 */
.filter-card {
  border-radius: 12px;
  margin-bottom: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.filter-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.filter-bar {
  display: flex;
  gap: 16px;
  align-items: center;
}

.date-picker {
  min-width: 300px;
  border-radius: 8px;
}

.data-select {
  min-width: 160px;
  border-radius: 8px;
}

/* 概览卡片 */
.overview-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.overview-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.overview-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
}

.overview-icon {
  font-size: 32px;
  color: var(--el-color-primary);
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-color-primary-light-9);
  border-radius: 12px;
}

.overview-info {
  flex: 1;
}

.overview-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  line-height: 1;
}

.overview-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.overview-change {
  font-size: 12px;
  font-weight: 500;
  margin-top: 4px;
}

.overview-change.positive {
  color: var(--el-color-success);
}

.overview-change.negative {
  color: var(--el-color-danger);
}

/* 图表卡片 */
.chart-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 32px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0;
  margin: -20px -20px 24px;
  padding: 20px;
  background: var(--el-color-primary-light-9);
  border-radius: 12px 12px 0 0;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

/* 表格卡片 */
.table-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 2px solid var(--el-color-primary-light-9);
}

/* 响应式 */
@media (max-width: 1200px) {
  .el-col {
    width: 100% !important;
  }
  
  .chart-card {
    margin-bottom: 16px;
  }
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .date-picker {
    min-width: auto;
  }
  
  .header-stats {
    flex-direction: column;
    gap: 16px;
  }
  
  .stat-item {
    width: 200px;
  }
}
</style>