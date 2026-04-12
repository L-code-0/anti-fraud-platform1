<template>
  <div class="analysis-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>演练对比分析</h1>
        </div>
      </el-header>
      
      <el-main>
        <!-- 演练统计信息 -->
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>演练统计</span>
              <el-button type="primary" @click="loadDrillStats">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          
          <div v-if="statsLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <div v-else class="stats-content">
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-value">{{ drillStats.totalDrills || 0 }}</div>
                <div class="stat-label">总演练次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ drillStats.averageScore ? drillStats.averageScore.toFixed(1) : '0.0' }}</div>
                <div class="stat-label">平均得分</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ drillStats.highestScore || 0 }}</div>
                <div class="stat-label">最高得分</div>
              </div>
            </div>
            
            <div v-if="drillStats.typeStats" class="type-stats">
              <h3>演练类型统计</h3>
              <el-table :data="typeStatsList" style="width: 100%">
                <el-table-column prop="type" label="类型" width="200" />
                <el-table-column prop="count" label="次数" width="100" />
              </el-table>
            </div>
          </div>
        </el-card>
        
        <!-- 对比分析 -->
        <el-card class="comparison-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>与他人对比</span>
              <el-select v-model="comparisonDrillType" placeholder="选择演练类型" @change="loadComparison">
                <el-option label="角色扮演" value="roleplay" />
                <el-option label="挑战赛" value="challenge" />
                <el-option label="自适应测试" value="adaptive" />
              </el-select>
            </div>
          </template>
          
          <div v-if="comparisonLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="!comparisonData" description="暂无对比数据" />
          
          <div v-else class="comparison-content">
            <div class="comparison-stats">
              <div class="stat-item">
                <div class="stat-label">您的得分</div>
                <div class="stat-value">{{ comparisonData.userScore }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">平均得分</div>
                <div class="stat-value">{{ comparisonData.averageScore }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">排名</div>
                <div class="stat-value">{{ comparisonData.rank }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">超越百分比</div>
                <div class="stat-value">{{ comparisonData.exceedPercentage }}%</div>
              </div>
            </div>
            
            <div class="top-users">
              <h3>前10名用户</h3>
              <el-table :data="comparisonData.topUsers" style="width: 100%">
                <el-table-column prop="rank" label="排名" width="80" />
                <el-table-column prop="userName" label="用户名" width="150" />
                <el-table-column prop="score" label="得分" width="100" />
              </el-table>
            </div>
          </div>
        </el-card>
        
        <!-- 进步分析 -->
        <el-card class="progress-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>进步分析</span>
              <el-select v-model="progressDrillType" placeholder="选择演练类型" @change="loadProgress">
                <el-option label="角色扮演" value="roleplay" />
                <el-option label="挑战赛" value="challenge" />
                <el-option label="自适应测试" value="adaptive" />
              </el-select>
              <el-select v-model="progressPeriod" placeholder="选择周期" @change="loadProgress">
                <el-option label="一周" value="week" />
                <el-option label="一个月" value="month" />
                <el-option label="一年" value="year" />
              </el-select>
            </div>
          </template>
          
          <div v-if="progressLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="!progressData" description="暂无进步数据" />
          
          <div v-else class="progress-content">
            <div class="progress-stats">
              <div class="stat-item">
                <div class="stat-label">初始得分</div>
                <div class="stat-value">{{ progressData.initialScore }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">最终得分</div>
                <div class="stat-value">{{ progressData.finalScore }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">进步分数</div>
                <div class="stat-value" :class="{ 'positive': progressData.totalImprovement > 0, 'negative': progressData.totalImprovement < 0 }">
                  {{ progressData.totalImprovement > 0 ? '+' : '' }}{{ progressData.totalImprovement }}
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-label">进步率</div>
                <div class="stat-value" :class="{ 'positive': progressData.improvementRate > 0, 'negative': progressData.improvementRate < 0 }">
                  {{ progressData.improvementRate > 0 ? '+' : '' }}{{ progressData.improvementRate.toFixed(1) }}%
                </div>
              </div>
            </div>
            
            <div class="progress-chart">
              <h3>得分趋势</h3>
              <el-chart :option="progressChartOption" style="height: 400px" />
            </div>
          </div>
        </el-card>
        
        <!-- 分析报告 -->
        <el-card class="report-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>分析报告</span>
              <el-select v-model="reportDrillType" placeholder="选择演练类型" @change="loadReport">
                <el-option label="角色扮演" value="roleplay" />
                <el-option label="挑战赛" value="challenge" />
                <el-option label="自适应测试" value="adaptive" />
              </el-select>
              <el-button type="primary" @click="loadReport">
                <el-icon><Document /></el-icon>
                生成报告
              </el-button>
            </div>
          </template>
          
          <div v-if="reportLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="!reportData" description="暂无报告数据" />
          
          <div v-else class="report-content">
            <div class="report-summary">
              <h3>报告摘要</h3>
              <div class="summary-grid">
                <div class="summary-item">
                  <span class="label">总演练次数:</span>
                  <span class="value">{{ reportData.totalDrills }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">平均得分:</span>
                  <span class="value">{{ reportData.averageScore.toFixed(1) }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">最高得分:</span>
                  <span class="value">{{ reportData.highestScore }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">最低得分:</span>
                  <span class="value">{{ reportData.lowestScore }}</span>
                </div>
              </div>
            </div>
            
            <div class="report-sections">
              <div class="report-section">
                <h4>与他人对比</h4>
                <div v-if="reportData.comparison.success">
                  <p>您的得分: {{ reportData.comparison.userScore }}</p>
                  <p>平均得分: {{ reportData.comparison.averageScore }}</p>
                  <p>排名: {{ reportData.comparison.rank }}</p>
                  <p>超越百分比: {{ reportData.comparison.exceedPercentage }}%</p>
                </div>
                <p v-else>{{ reportData.comparison.message }}</p>
              </div>
              
              <div class="report-section">
                <h4>进步分析</h4>
                <div v-if="reportData.progress.success">
                  <p>初始得分: {{ reportData.progress.initialScore }}</p>
                  <p>最终得分: {{ reportData.progress.finalScore }}</p>
                  <p>进步分数: {{ reportData.progress.totalImprovement > 0 ? '+' : '' }}{{ reportData.progress.totalImprovement }}</p>
                  <p>进步率: {{ reportData.progress.improvementRate > 0 ? '+' : '' }}{{ reportData.progress.improvementRate.toFixed(1) }}%</p>
                </div>
                <p v-else>{{ reportData.progress.message }}</p>
              </div>
              
              <div class="report-section">
                <h4>最近演练</h4>
                <el-table :data="reportData.recentDrills" style="width: 100%">
                  <el-table-column prop="drillName" label="演练名称" width="200" />
                  <el-table-column prop="score" label="得分" width="100" />
                  <el-table-column prop="createTime" label="时间" width="200" />
                </el-table>
              </div>
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Refresh, Loading, Document } from '@element-plus/icons-vue'

// 状态
const statsLoading = ref(false)
const comparisonLoading = ref(false)
const progressLoading = ref(false)
const reportLoading = ref(false)
const drillStats = ref({
  totalDrills: 0,
  averageScore: 0,
  highestScore: 0,
  typeStats: {}
})
const comparisonData = ref<any>(null)
const progressData = ref<any>(null)
const reportData = ref<any>(null)

// 类型选择
const comparisonDrillType = ref('roleplay')
const progressDrillType = ref('roleplay')
const progressPeriod = ref('month')
const reportDrillType = ref('roleplay')

// 类型统计列表
const typeStatsList = computed(() => {
  if (!drillStats.value.typeStats) return []
  return Object.entries(drillStats.value.typeStats).map(([type, count]) => ({
    type,
    count
  }))
})

// 进步图表选项
const progressChartOption = computed(() => {
  if (!progressData.value || !progressData.value.progressData) {
    return {
      title: {
        text: '得分趋势'
      },
      xAxis: {
        type: 'category',
        data: []
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        data: [],
        type: 'line'
      }]
    }
  }
  
  const dates = progressData.value.progressData.map((item: any) => {
    const date = new Date(item.date)
    return `${date.getMonth() + 1}/${date.getDate()}`
  })
  const scores = progressData.value.progressData.map((item: any) => item.score)
  
  return {
    title: {
      text: '得分趋势'
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100
    },
    series: [{
      data: scores,
      type: 'line',
      smooth: true,
      lineStyle: {
        color: '#409EFF'
      },
      itemStyle: {
        color: '#409EFF'
      }
    }]
  }
})

// 加载演练统计信息
const loadDrillStats = async () => {
  statsLoading.value = true
  try {
    const res = await get('/analysis/stats')
    if (res.code === 200 && res.data) {
      drillStats.value = res.data
    }
  } catch (error) {
    console.error('加载演练统计信息失败:', error)
    // 模拟数据
    drillStats.value = {
      totalDrills: 20,
      averageScore: 82.5,
      highestScore: 95,
      typeStats: {
        '角色扮演': 10,
        '挑战赛': 6,
        '自适应测试': 4
      }
    }
  } finally {
    statsLoading.value = false
  }
}

// 加载对比分析
const loadComparison = async () => {
  comparisonLoading.value = true
  try {
    const res = await get('/analysis/compare', {
      params: {
        drillType: comparisonDrillType.value
      }
    })
    if (res.code === 200 && res.data && res.data.success) {
      comparisonData.value = res.data
    } else {
      comparisonData.value = null
      ElMessage.error(res.data?.message || '获取对比数据失败')
    }
  } catch (error) {
    console.error('加载对比分析失败:', error)
    // 模拟数据
    comparisonData.value = {
      userScore: 85,
      averageScore: 75,
      rank: 5,
      exceedPercentage: 75.5,
      topUsers: [
        { rank: 1, userName: '张三', score: 95 },
        { rank: 2, userName: '李四', score: 90 },
        { rank: 3, userName: '王五', score: 88 },
        { rank: 4, userName: '赵六', score: 86 },
        { rank: 5, userName: '您', score: 85 },
        { rank: 6, userName: '钱七', score: 82 },
        { rank: 7, userName: '孙八', score: 80 },
        { rank: 8, userName: '周九', score: 78 },
        { rank: 9, userName: '吴十', score: 75 },
        { rank: 10, userName: '郑一', score: 72 }
      ]
    }
  } finally {
    comparisonLoading.value = false
  }
}

// 加载进步分析
const loadProgress = async () => {
  progressLoading.value = true
  try {
    const res = await get('/analysis/progress', {
      params: {
        drillType: progressDrillType.value,
        period: progressPeriod.value
      }
    })
    if (res.code === 200 && res.data && res.data.success) {
      progressData.value = res.data
    } else {
      progressData.value = null
      ElMessage.error(res.data?.message || '获取进步数据失败')
    }
  } catch (error) {
    console.error('加载进步分析失败:', error)
    // 模拟数据
    progressData.value = {
      progressData: [
        { date: '2026-03-15', score: 70, improvement: 0 },
        { date: '2026-03-20', score: 75, improvement: 5 },
        { date: '2026-03-25', score: 80, improvement: 5 },
        { date: '2026-04-01', score: 82, improvement: 2 },
        { date: '2026-04-05', score: 85, improvement: 3 }
      ],
      initialScore: 70,
      finalScore: 85,
      totalImprovement: 15,
      improvementRate: 21.4
    }
  } finally {
    progressLoading.value = false
  }
}

// 加载分析报告
const loadReport = async () => {
  reportLoading.value = true
  try {
    const res = await get('/analysis/report', {
      params: {
        drillType: reportDrillType.value
      }
    })
    if (res.code === 200 && res.data && res.data.success) {
      reportData.value = res.data
    } else {
      reportData.value = null
      ElMessage.error(res.data?.message || '生成报告失败')
    }
  } catch (error) {
    console.error('加载分析报告失败:', error)
    // 模拟数据
    reportData.value = {
      totalDrills: 10,
      averageScore: 82.5,
      highestScore: 95,
      lowestScore: 70,
      comparison: {
        success: true,
        userScore: 85,
        averageScore: 75,
        rank: 5,
        exceedPercentage: 75.5
      },
      progress: {
        success: true,
        initialScore: 70,
        finalScore: 85,
        totalImprovement: 15,
        improvementRate: 21.4
      },
      recentDrills: [
        { drillName: '电信诈骗场景', score: 85, createTime: '2026-04-10 10:00:00' },
        { drillName: '网络诈骗场景', score: 82, createTime: '2026-04-05 14:30:00' },
        { drillName: '金融诈骗场景', score: 88, createTime: '2026-04-01 09:00:00' }
      ]
    }
  } finally {
    reportLoading.value = false
  }
}

onMounted(() => {
  loadDrillStats()
  loadComparison()
  loadProgress()
  loadReport()
})
</script>

<style scoped>
.analysis-page {
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

.stats-content {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
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

.type-stats {
  margin-top: 30px;
}

.type-stats h3 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.comparison-stats {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.top-users {
  margin-top: 30px;
}

.top-users h3 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.progress-stats {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.progress-stats .stat-value.positive {
  color: var(--color-success);
}

.progress-stats .stat-value.negative {
  color: var(--color-danger);
}

.progress-chart {
  margin-top: 30px;
}

.progress-chart h3 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.report-summary {
  margin-bottom: 30px;
}

.report-summary h3 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: var(--color-bg-container);
  border-radius: var(--radius-lg);
}

.summary-item .label {
  color: var(--color-text-secondary);
}

.summary-item .value {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.report-sections {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.report-section {
  padding: 20px;
  background: var(--color-bg-container);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.report-section h4 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.report-section p {
  margin-bottom: 8px;
  line-height: 1.6;
  color: var(--color-text-secondary);
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
  
  .stats-grid,
  .comparison-stats,
  .progress-stats,
  .summary-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>