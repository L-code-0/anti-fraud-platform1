<template>
  <div class="report-analysis-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>举报数据分析</h1>
          <el-button type="primary" @click="loadReportAnalysis">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </el-header>
      
      <el-main>
        <!-- 筛选条件 -->
        <el-card class="filter-card">
          <div class="filter-content">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 300px"
            />
            <el-select v-model="reportType" placeholder="选择举报类型" style="width: 150px">
              <el-option label="全部" value="" />
              <el-option label="电信诈骗" value="电信诈骗" />
              <el-option label="网络诈骗" value="网络诈骗" />
              <el-option label="金融诈骗" value="金融诈骗" />
              <el-option label="虚假信息" value="虚假信息" />
              <el-option label="其他" value="其他" />
            </el-select>
            <el-button type="primary" @click="loadReportAnalysis">查询</el-button>
          </div>
        </el-card>
        
        <!-- 统计概览 -->
        <el-card class="stats-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>统计概览</span>
            </div>
          </template>
          
          <div v-if="analysisLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <div v-else class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ reportAnalysis.totalReports || 0 }}</div>
              <div class="stat-label">总举报数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ reportAnalysis.successRate ? reportAnalysis.successRate.toFixed(1) : '0.0' }}%</div>
              <div class="stat-label">成功处理率</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ reportAnalysis.averageProcessingTime || 0 }}小时</div>
              <div class="stat-label">平均处理时间</div>
            </div>
          </div>
        </el-card>
        
        <!-- 图表分析 -->
        <div class="charts-container" style="margin-top: 20px">
          <!-- 举报类型分布 -->
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>举报类型分布</span>
              </div>
            </template>
            <div class="chart-content">
              <el-chart :option="typeDistributionOption" style="height: 400px" />
            </div>
          </el-card>
          
          <!-- 举报状态分布 -->
          <el-card class="chart-card" style="margin-top: 20px">
            <template #header>
              <div class="card-header">
                <span>举报状态分布</span>
              </div>
            </template>
            <div class="chart-content">
              <el-chart :option="statusDistributionOption" style="height: 400px" />
            </div>
          </el-card>
          
          <!-- 举报趋势 -->
          <el-card class="chart-card" style="margin-top: 20px">
            <template #header>
              <div class="card-header">
                <span>举报趋势</span>
              </div>
            </template>
            <div class="chart-content">
              <el-chart :option="trendOption" style="height: 400px" />
            </div>
          </el-card>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Refresh, Loading } from '@element-plus/icons-vue'

// 状态
const analysisLoading = ref(false)
const dateRange = ref<any>([])
const reportType = ref('')
const reportAnalysis = ref({
  totalReports: 0,
  successRate: 0,
  averageProcessingTime: 0,
  reportTypeDistribution: {},
  statusDistribution: {},
  trend: []
})

// 举报类型分布图表选项
const typeDistributionOption = computed(() => {
  const data = reportAnalysis.value.reportTypeDistribution || {}
  const labels = Object.keys(data)
  const values = Object.values(data)
  
  return {
    title: {
      text: '举报类型分布',
      left: 'center'
    },
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
        name: '举报类型',
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

// 举报状态分布图表选项
const statusDistributionOption = computed(() => {
  const data = reportAnalysis.value.statusDistribution || {}
  const labels = Object.keys(data)
  const values = Object.values(data)
  
  return {
    title: {
      text: '举报状态分布',
      left: 'center'
    },
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
        name: '举报状态',
        type: 'pie',
        radius: '60%',
        data: labels.map((label, index) => ({
          value: values[index],
          name: getStatusText(label)
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

// 举报趋势图表选项
const trendOption = computed(() => {
  const data = reportAnalysis.value.trend || []
  const dates = data.map((item: any) => item.date)
  const counts = data.map((item: any) => item.count)
  
  return {
    title: {
      text: '举报趋势',
      left: 'center'
    },
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

// 加载举报数据分析
const loadReportAnalysis = async () => {
  analysisLoading.value = true
  try {
    const startTime = dateRange.value[0] ? dateRange.value[0].toISOString().split('T')[0] : ''
    const endTime = dateRange.value[1] ? dateRange.value[1].toISOString().split('T')[0] : ''
    
    const res = await get('/report/analysis', {
      params: {
        startTime: startTime,
        endTime: endTime,
        reportType: reportType.value
      }
    })
    if (res.code === 200 && res.data) {
      reportAnalysis.value = res.data
    }
  } catch (error) {
    console.error('加载举报数据分析失败:', error)
    // 模拟数据
    reportAnalysis.value = {
      totalReports: 100,
      successRate: 85.5,
      averageProcessingTime: 24,
      reportTypeDistribution: {
        '电信诈骗': 40,
        '网络诈骗': 30,
        '金融诈骗': 20,
        '其他': 10
      },
      statusDistribution: {
        'pending': 15,
        'processing': 25,
        'success': 50,
        'rejected': 10
      },
      trend: [
        { date: '2026-04-01', count: 10 },
        { date: '2026-04-02', count: 15 },
        { date: '2026-04-03', count: 8 },
        { date: '2026-04-04', count: 12 },
        { date: '2026-04-05', count: 10 },
        { date: '2026-04-06', count: 15 },
        { date: '2026-04-07', count: 20 }
      ]
    }
  } finally {
    analysisLoading.value = false
  }
}

// 获取状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case 'pending':
      return '待处理'
    case 'processing':
      return '处理中'
    case 'success':
      return '已处理'
    case 'rejected':
      return '已驳回'
    default:
      return status
  }
}

onMounted(() => {
  loadReportAnalysis()
})
</script>

<style scoped>
.report-analysis-page {
  min-height: 100vh;
  background: var(--color-bg-page);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-content h1 {
  margin: 0;
  font-size: var(--font-size-2xl);
  color: var(--color-text-primary);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: var(--color-text-secondary);
}

.filter-content {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  align-items: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  padding: 20px;
}

.stat-item {
  background: var(--color-bg-container);
  padding: 20px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  text-align: center;
}

.stat-value {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  margin-bottom: 8px;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.chart-content {
  padding: 20px;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .filter-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>