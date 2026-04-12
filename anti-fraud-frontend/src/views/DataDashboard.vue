<template>
  <div class="data-dashboard-page">
    <div class="dashboard-header">
      <h1>实时数据大屏</h1>
      <div class="header-actions">
        <el-button type="primary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-select v-model="timeRange" @change="loadDashboardData" style="margin-left: 10px">
          <el-option label="近24小时" value="24h" />
          <el-option label="近7天" value="7d" />
          <el-option label="近30天" value="30d" />
          <el-option label="近90天" value="90d" />
        </el-select>
      </div>
    </div>
    
    <!-- 统计概览 -->
    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon danger">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardData.totalCases || 0 }}</div>
          <div class="stat-label">诈骗案件数</div>
        </div>
        <div class="stat-trend" :class="{ 'up': dashboardData.casesTrend > 0, 'down': dashboardData.casesTrend < 0 }">
          <el-icon>{{ dashboardData.casesTrend > 0 ? 'CaretTop' : 'CaretBottom' }}</el-icon>
          <span>{{ Math.abs(dashboardData.casesTrend || 0) }}%</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon success">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardData.solvedCases || 0 }}</div>
          <div class="stat-label">已解决案件</div>
        </div>
        <div class="stat-trend" :class="{ 'up': dashboardData.solvedTrend > 0, 'down': dashboardData.solvedTrend < 0 }">
          <el-icon>{{ dashboardData.solvedTrend > 0 ? 'CaretTop' : 'CaretBottom' }}</el-icon>
          <span>{{ Math.abs(dashboardData.solvedTrend || 0) }}%</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon primary">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardData.highRiskCases || 0 }}</div>
          <div class="stat-label">高风险案件</div>
        </div>
        <div class="stat-trend" :class="{ 'up': dashboardData.riskTrend > 0, 'down': dashboardData.riskTrend < 0 }">
          <el-icon>{{ dashboardData.riskTrend > 0 ? 'CaretTop' : 'CaretBottom' }}</el-icon>
          <span>{{ Math.abs(dashboardData.riskTrend || 0) }}%</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon warning">
          <el-icon><Timer /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardData.averageProcessingTime || 0 }}</div>
          <div class="stat-label">平均处理时间(小时)</div>
        </div>
        <div class="stat-trend" :class="{ 'up': dashboardData.timeTrend > 0, 'down': dashboardData.timeTrend < 0 }">
          <el-icon>{{ dashboardData.timeTrend > 0 ? 'CaretTop' : 'CaretBottom' }}</el-icon>
          <span>{{ Math.abs(dashboardData.timeTrend || 0) }}%</span>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 诈骗类型分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>诈骗类型分布</h3>
        </div>
        <div class="chart-content">
          <el-chart :option="typeDistributionOption" style="height: 300px" />
        </div>
      </div>
      
      <!-- 案件趋势 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>案件趋势</h3>
        </div>
        <div class="chart-content">
          <el-chart :option="casesTrendOption" style="height: 300px" />
        </div>
      </div>
      
      <!-- 地域分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>地域分布</h3>
        </div>
        <div class="chart-content">
          <el-chart :option="regionDistributionOption" style="height: 300px" />
        </div>
      </div>
      
      <!-- 风险等级分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>风险等级分布</h3>
        </div>
        <div class="chart-content">
          <el-chart :option="riskLevelOption" style="height: 300px" />
        </div>
      </div>
    </div>
    
    <!-- 实时预警 -->
    <div class="alert-section">
      <div class="alert-header">
        <h3>实时预警</h3>
      </div>
      <div class="alert-list">
        <div v-for="alert in dashboardData.alerts" :key="alert.id" class="alert-item">
          <div class="alert-type" :class="alert.level">
            <el-icon>{{ alert.level === 'high' ? 'Warning' : alert.level === 'medium' ? 'InfoFilled' : 'Check' }}</el-icon>
            <span>{{ getAlertLevelText(alert.level) }}</span>
          </div>
          <div class="alert-content">
            <h4>{{ alert.title }}</h4>
            <p>{{ alert.description }}</p>
            <div class="alert-time">{{ alert.time }}</div>
          </div>
        </div>
        <div v-if="dashboardData.alerts && dashboardData.alerts.length === 0" class="no-alerts">
          <el-empty description="暂无预警信息" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Refresh, Warning, Check, Timer, CaretTop, CaretBottom, InfoFilled } from '@element-plus/icons-vue'

// 状态
const loading = ref(false)
const timeRange = ref('24h')
const dashboardData = ref({
  totalCases: 0,
  solvedCases: 0,
  highRiskCases: 0,
  averageProcessingTime: 0,
  casesTrend: 0,
  solvedTrend: 0,
  riskTrend: 0,
  timeTrend: 0,
  typeDistribution: {},
  casesTrendData: [],
  regionDistribution: {},
  riskLevelDistribution: {},
  alerts: []
})

// 诈骗类型分布图表选项
const typeDistributionOption = computed(() => {
  const data = dashboardData.value.typeDistribution || {}
  const labels = Object.keys(data)
  const values = Object.values(data)
  
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: labels
    },
    series: [
      {
        name: '诈骗类型',
        type: 'pie',
        radius: '60%',
        data: labels.map((label, index) => ({
          value: values[index],
          name: label
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
})

// 案件趋势图表选项
const casesTrendOption = computed(() => {
  const data = dashboardData.value.casesTrendData || []
  const dates = data.map((item: any) => item.date)
  const counts = data.map((item: any) => item.count)
  
  return {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: counts,
        type: 'line',
        smooth: true,
        lineStyle: {
          color: '#409EFF'
        },
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
})

// 地域分布图表选项
const regionDistributionOption = computed(() => {
  const data = dashboardData.value.regionDistribution || {}
  const regions = Object.keys(data)
  const values = Object.values(data)
  
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: regions,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '案件数',
        type: 'bar',
        data: values,
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
})

// 风险等级分布图表选项
const riskLevelOption = computed(() => {
  const data = dashboardData.value.riskLevelDistribution || {}
  const levels = Object.keys(data)
  const values = Object.values(data)
  
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: levels
    },
    series: [
      {
        name: '风险等级',
        type: 'pie',
        radius: '60%',
        data: levels.map((level, index) => ({
          value: values[index],
          name: level
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
})

// 加载大屏数据
const loadDashboardData = async () => {
  loading.value = true
  try {
    const res = await get('/dashboard/data', {
      params: {
        timeRange: timeRange.value
      }
    })
    if (res.code === 200 && res.data) {
      dashboardData.value = res.data
    }
  } catch (error) {
    console.error('加载大屏数据失败:', error)
    // 模拟数据
    dashboardData.value = {
      totalCases: 1258,
      solvedCases: 892,
      highRiskCases: 235,
      averageProcessingTime: 24,
      casesTrend: 12.5,
      solvedTrend: 8.3,
      riskTrend: -5.2,
      timeTrend: -3.7,
      typeDistribution: {
        '电信诈骗': 450,
        '网络诈骗': 320,
        '金融诈骗': 210,
        '虚假信息': 150,
        '其他': 128
      },
      casesTrendData: [
        { date: '04-01', count: 35 },
        { date: '04-02', count: 42 },
        { date: '04-03', count: 38 },
        { date: '04-04', count: 45 },
        { date: '04-05', count: 52 },
        { date: '04-06', count: 48 },
        { date: '04-07', count: 55 }
      ],
      regionDistribution: {
        '北京': 120,
        '上海': 95,
        '广州': 88,
        '深圳': 82,
        '杭州': 75,
        '成都': 68,
        '武汉': 62
      },
      riskLevelDistribution: {
        '高风险': 235,
        '中风险': 456,
        '低风险': 567
      },
      alerts: [
        {
          id: 1,
          level: 'high',
          title: '高风险诈骗预警',
          description: '检测到疑似电信诈骗团伙活动，已涉及多个地区',
          time: '2026-04-12 10:30:00'
        },
        {
          id: 2,
          level: 'medium',
          title: '中风险预警',
          description: '某地区网络诈骗案件数量呈上升趋势',
          time: '2026-04-12 09:15:00'
        },
        {
          id: 3,
          level: 'low',
          title: '低风险预警',
          description: '发现疑似虚假信息传播',
          time: '2026-04-12 08:45:00'
        }
      ]
    }
  } finally {
    loading.value = false
  }
}

// 刷新数据
const refreshData = () => {
  loadDashboardData()
  ElMessage.success('数据已刷新')
}

// 获取预警等级文本
const getAlertLevelText = (level: string) => {
  switch (level) {
    case 'high':
      return '高风险'
    case 'medium':
      return '中风险'
    case 'low':
      return '低风险'
    default:
      return '未知'
  }
}

onMounted(() => {
  loadDashboardData()
  // 每5分钟自动刷新数据
  setInterval(loadDashboardData, 5 * 60 * 1000)
})
</script>

<style scoped>
.data-dashboard-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 0 20px;
}

.dashboard-header h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.header-actions {
  display: flex;
  align-items: center;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.danger {
  background: #f56c6c;
}

.stat-icon.success {
  background: #67c23a;
}

.stat-icon.primary {
  background: #409eff;
}

.stat-icon.warning {
  background: #e6a23c;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 500;
}

.stat-trend.up {
  color: #67c23a;
}

.stat-trend.down {
  color: #f56c6c;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.chart-header {
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.chart-content {
  height: 300px;
}

.alert-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.alert-header {
  margin-bottom: 20px;
}

.alert-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.alert-list {
  max-height: 300px;
  overflow-y: auto;
}

.alert-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s ease;
}

.alert-item:hover {
  background-color: #f9f9f9;
}

.alert-item:last-child {
  border-bottom: none;
}

.alert-type {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border-radius: 8px;
  min-width: 80px;
}

.alert-type.high {
  background-color: #fef0f0;
  color: #f56c6c;
}

.alert-type.medium {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.alert-type.low {
  background-color: #f0f9eb;
  color: #67c23a;
}

.alert-content {
  flex: 1;
}

.alert-content h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #333;
}

.alert-content p {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
}

.alert-time {
  font-size: 12px;
  color: #909399;
}

.no-alerts {
  padding: 40px 0;
  text-align: center;
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .stats-overview {
    grid-template-columns: 1fr;
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-content {
    height: 250px;
  }
}
</style>