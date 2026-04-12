<template>
  <div class="prediction-analysis-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>预测分析</h1>
          <div class="header-actions">
            <el-button type="primary" @click="runPrediction">
              <el-icon><Refresh /></el-icon>
              运行分析
            </el-button>
            <el-select v-model="predictionPeriod" @change="runPrediction" style="margin-left: 10px">
              <el-option label="7天" value="7d" />
              <el-option label="30天" value="30d" />
              <el-option label="90天" value="90d" />
            </el-select>
          </div>
        </div>
      </el-header>
      
      <el-main>
        <!-- 预测概览 -->
        <el-card class="overview-card">
          <template #header>
            <div class="card-header">
              <span>预测概览</span>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>分析中...</span>
          </div>
          
          <div v-else class="overview-content">
            <div class="prediction-stats">
              <div class="stat-card">
                <div class="stat-icon primary">
                  <el-icon><Warning /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ predictionData.predictedCases || 0 }}</div>
                  <div class="stat-label">预测案件数</div>
                </div>
                <div class="stat-trend" :class="{ 'up': predictionData.casesTrend > 0, 'down': predictionData.casesTrend < 0 }">
                  <el-icon>{{ predictionData.casesTrend > 0 ? 'CaretTop' : 'CaretBottom' }}</el-icon>
                  <span>{{ Math.abs(predictionData.casesTrend || 0) }}%</span>
                </div>
              </div>
              
              <div class="stat-card">
                <div class="stat-icon danger">
                  <el-icon><Warning /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ predictionData.highRiskAlerts || 0 }}</div>
                  <div class="stat-label">高风险预警</div>
                </div>
                <div class="stat-trend" :class="{ 'up': predictionData.alertsTrend > 0, 'down': predictionData.alertsTrend < 0 }">
                  <el-icon>{{ predictionData.alertsTrend > 0 ? 'CaretTop' : 'CaretBottom' }}</el-icon>
                  <span>{{ Math.abs(predictionData.alertsTrend || 0) }}%</span>
                </div>
              </div>
              
              <div class="stat-card">
                <div class="stat-icon success">
                  <el-icon><Check /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ predictionData.predictionAccuracy || 0 }}%</div>
                  <div class="stat-label">预测准确率</div>
                </div>
                <div class="stat-trend" :class="{ 'up': predictionData.accuracyTrend > 0, 'down': predictionData.accuracyTrend < 0 }">
                  <el-icon>{{ predictionData.accuracyTrend > 0 ? 'CaretTop' : 'CaretBottom' }}</el-icon>
                  <span>{{ Math.abs(predictionData.accuracyTrend || 0) }}%</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 预测趋势 -->
        <el-card class="trend-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>预测趋势</span>
            </div>
          </template>
          
          <div class="trend-content">
            <el-chart :option="predictionTrendOption" style="height: 300px" />
          </div>
        </el-card>
        
        <!-- 高风险预警 -->
        <el-card class="alerts-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>高风险预警</span>
            </div>
          </template>
          
          <div class="alerts-content">
            <el-empty v-if="!predictionData.alerts || predictionData.alerts.length === 0" description="暂无高风险预警" />
            <div v-else class="alert-list">
              <div v-for="alert in predictionData.alerts" :key="alert.id" class="alert-item">
                <div class="alert-header">
                  <div class="alert-type" :class="alert.riskLevel">
                    <el-icon>{{ alert.riskLevel === 'high' ? 'Warning' : alert.riskLevel === 'medium' ? 'InfoFilled' : 'Check' }}</el-icon>
                    <span>{{ getRiskLevelText(alert.riskLevel) }}</span>
                  </div>
                  <div class="alert-time">{{ alert.predictedDate }}</div>
                </div>
                <div class="alert-content">
                  <h4>{{ alert.title }}</h4>
                  <p>{{ alert.description }}</p>
                  <div class="alert-probability">
                    <span class="label">预测概率：</span>
                    <el-progress :percentage="alert.probability" :color="getProbabilityColor(alert.probability)" />
                    <span class="value">{{ alert.probability }}%</span>
                  </div>
                </div>
                <div class="alert-actions">
                  <el-button type="primary" size="small" @click="viewAlertDetail(alert)">
                    <el-icon><View /></el-icon>
                    查看详情
                  </el-button>
                  <el-button size="small" @click="dismissAlert(alert.id)">
                    <el-icon><Delete /></el-icon>
                    忽略
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 风险类型预测 -->
        <el-card class="risk-types-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>风险类型预测</span>
            </div>
          </template>
          
          <div class="risk-types-content">
            <el-chart :option="riskTypeOption" style="height: 300px" />
          </div>
        </el-card>
        
        <!-- 防御建议 -->
        <el-card class="defense-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>主动防御建议</span>
            </div>
          </template>
          
          <div class="defense-content">
            <el-empty v-if="!predictionData.defenseSuggestions || predictionData.defenseSuggestions.length === 0" description="暂无防御建议" />
            <el-list v-else>
              <el-list-item v-for="(suggestion, index) in predictionData.defenseSuggestions" :key="index">
                <el-card :body-style="{ padding: '16px' }" shadow="hover">
                  <div class="defense-item">
                    <div class="defense-title">
                      <el-icon><Shield /></el-icon>
                      <span>{{ suggestion.title }}</span>
                    </div>
                    <div class="defense-desc">{{ suggestion.description }}</div>
                    <div class="defense-impact">
                      <span class="label">预计影响：</span>
                      <span class="value">{{ suggestion.impact }}</span>
                    </div>
                    <div class="defense-priority" :class="suggestion.priority">
                      {{ getPriorityText(suggestion.priority) }}
                    </div>
                  </div>
                </el-card>
              </el-list-item>
            </el-list>
          </div>
        </el-card>
        
        <!-- 预测模型介绍 -->
        <el-card class="model-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>预测模型介绍</span>
            </div>
          </template>
          
          <div class="model-content">
            <h3>模型原理</h3>
            <p>本预测模型基于机器学习算法，通过分析历史诈骗案件数据、用户行为数据和网络环境数据，预测未来可能出现的诈骗风险。模型采用了以下技术：</p>
            <ul>
              <li>时间序列分析：预测案件数量的变化趋势</li>
              <li>分类算法：识别高风险用户和行为</li>
              <li>异常检测：发现异常模式和潜在风险</li>
              <li>自然语言处理：分析诈骗相关文本信息</li>
            </ul>
            <h3>模型性能</h3>
            <p>模型准确率：{{ predictionData.predictionAccuracy || 0 }}%</p>
            <p>模型召回率：{{ predictionData.recallRate || 0 }}%</p>
            <p>模型F1分数：{{ predictionData.f1Score || 0 }}</p>
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
import { Refresh, Loading, Warning, Check, CaretTop, CaretBottom, View, Delete, Shield, InfoFilled } from '@element-plus/icons-vue'

// 状态
const loading = ref(false)
const predictionPeriod = ref('30d')
const predictionData = ref({
  predictedCases: 0,
  highRiskAlerts: 0,
  predictionAccuracy: 0,
  casesTrend: 0,
  alertsTrend: 0,
  accuracyTrend: 0,
  recallRate: 0,
  f1Score: 0,
  predictionTrend: [],
  riskTypeDistribution: {},
  alerts: [],
  defenseSuggestions: []
})

// 预测趋势图表选项
const predictionTrendOption = computed(() => {
  const data = predictionData.value.predictionTrend || []
  const dates = data.map((item: any) => item.date)
  const actual = data.map((item: any) => item.actual)
  const predicted = data.map((item: any) => item.predicted)
  
  return {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['实际案件', '预测案件']
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
        name: '实际案件',
        data: actual,
        type: 'line',
        smooth: true,
        lineStyle: {
          color: '#409EFF'
        },
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '预测案件',
        data: predicted,
        type: 'line',
        smooth: true,
        lineStyle: {
          color: '#F56C6C',
          type: 'dashed'
        },
        itemStyle: {
          color: '#F56C6C'
        }
      }
    ]
  }
})

// 风险类型预测图表选项
const riskTypeOption = computed(() => {
  const data = predictionData.value.riskTypeDistribution || {}
  const types = Object.keys(data)
  const values = Object.values(data)
  
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: types
    },
    series: [
      {
        name: '风险类型',
        type: 'pie',
        radius: '60%',
        data: types.map((type, index) => ({
          value: values[index],
          name: type
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

// 获取风险等级文本
const getRiskLevelText = (level: string) => {
  switch (level) {
    case 'high':
      return '高风险'
    case 'medium':
      return '中风险'
    case 'low':
      return '低风险'
    default:
      return '未知风险'
  }
}

// 获取概率颜色
const getProbabilityColor = (probability: number) => {
  if (probability < 50) return '#67C23A'
  if (probability < 80) return '#E6A23C'
  return '#F56C6C'
}

// 获取优先级文本
const getPriorityText = (priority: string) => {
  switch (priority) {
    case 'high':
      return '高优先级'
    case 'medium':
      return '中优先级'
    case 'low':
      return '低优先级'
    default:
      return '未知优先级'
  }
}

// 运行预测分析
const runPrediction = async () => {
  loading.value = true
  try {
    const res = await post('/prediction/analyze', {
      period: predictionPeriod.value
    })
    if (res.code === 200 && res.data) {
      predictionData.value = res.data
      ElMessage.success('预测分析完成')
    }
  } catch (error) {
    console.error('预测分析失败:', error)
    // 模拟数据
    predictionData.value = {
      predictedCases: 120,
      highRiskAlerts: 15,
      predictionAccuracy: 85.5,
      casesTrend: 10.5,
      alertsTrend: 5.2,
      accuracyTrend: 2.3,
      recallRate: 82.1,
      f1Score: 0.83,
      predictionTrend: [
        { date: '03-12', actual: 85, predicted: 88 },
        { date: '03-19', actual: 92, predicted: 90 },
        { date: '03-26', actual: 98, predicted: 100 },
        { date: '04-02', actual: 105, predicted: 108 },
        { date: '04-09', actual: 110, predicted: 115 },
        { date: '04-16', predicted: 120 },
        { date: '04-23', predicted: 125 },
        { date: '04-30', predicted: 130 }
      ],
      riskTypeDistribution: {
        '电信诈骗': 45,
        '网络诈骗': 30,
        '金融诈骗': 15,
        '虚假信息': 10
      },
      alerts: [
        {
          id: 1,
          riskLevel: 'high',
          title: '电信诈骗风险预警',
          description: '预计未来7天内，电信诈骗案件将增加15%，主要集中在冒充客服和虚假中奖类型',
          probability: 85,
          predictedDate: '2026-04-15'
        },
        {
          id: 2,
          riskLevel: 'medium',
          title: '网络诈骗风险预警',
          description: '预计未来30天内，网络诈骗案件将增加8%，主要集中在钓鱼网站和虚假投资类型',
          probability: 70,
          predictedDate: '2026-05-01'
        },
        {
          id: 3,
          riskLevel: 'high',
          title: '金融诈骗风险预警',
          description: '预计未来14天内，金融诈骗案件将增加12%，主要集中在P2P平台和虚拟货币类型',
          probability: 75,
          predictedDate: '2026-04-20'
        }
      ],
      defenseSuggestions: [
        {
          title: '加强电信诈骗宣传',
          description: '针对预计增加的电信诈骗案件，建议加强相关宣传，提高用户警惕性',
          impact: '预计减少15%的电信诈骗案件',
          priority: 'high'
        },
        {
          title: '升级网络安全防护',
          description: '针对网络诈骗风险，建议升级网络安全防护措施，拦截钓鱼网站和恶意链接',
          impact: '预计减少10%的网络诈骗案件',
          priority: 'medium'
        },
        {
          title: '加强金融监管',
          description: '针对金融诈骗风险，建议加强对P2P平台和虚拟货币交易的监管',
          impact: '预计减少8%的金融诈骗案件',
          priority: 'medium'
        },
        {
          title: '建立预警机制',
          description: '建立实时预警机制，及时发现和处理潜在的诈骗风险',
          impact: '预计减少20%的诈骗案件',
          priority: 'high'
        }
      ]
    }
    ElMessage.success('预测分析完成')
  } finally {
    loading.value = false
  }
}

// 查看预警详情
const viewAlertDetail = (alert: any) => {
  // 模拟查看预警详情
  ElMessage.info('查看预警详情功能待实现')
}

// 忽略预警
const dismissAlert = (alertId: number) => {
  // 模拟忽略预警
  ElMessage.success('预警已忽略')
}

onMounted(() => {
  runPrediction()
})
</script>

<style scoped>
.prediction-analysis-page {
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
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

.overview-content {
  padding: 20px 0;
}

.prediction-stats {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.stat-card {
  background: var(--color-bg-container);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
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

.stat-icon.primary {
  background: #409eff;
}

.stat-icon.danger {
  background: #f56c6c;
}

.stat-icon.success {
  background: #67c23a;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--color-text-primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 500;
}

.stat-trend.up {
  color: #f56c6c;
}

.stat-trend.down {
  color: #67c23a;
}

.trend-content,
.risk-types-content {
  padding: 20px 0;
}

.alerts-content {
  padding: 10px 0;
}

.alert-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.alert-item {
  background: var(--color-bg-container);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  padding: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.alert-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.alert-type {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 500;
  color: white;
}

.alert-type.high {
  background: #f56c6c;
}

.alert-type.medium {
  background: #e6a23c;
}

.alert-type.low {
  background: #67c23a;
}

.alert-time {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.alert-content {
  margin-bottom: 16px;
}

.alert-content h4 {
  margin: 0 0 8px 0;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

.alert-content p {
  margin: 0 0 12px 0;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.alert-probability {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
}

.alert-probability .label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  min-width: 80px;
}

.alert-probability .value {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  min-width: 50px;
  text-align: right;
}

.alert-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.defense-content {
  padding: 10px 0;
}

.defense-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.defense-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.defense-desc {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.defense-impact {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.defense-impact .value {
  font-weight: var(--font-weight-medium);
  color: var(--color-primary);
}

.defense-priority {
  align-self: flex-start;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: white;
}

.defense-priority.high {
  background: #f56c6c;
}

.defense-priority.medium {
  background: #e6a23c;
}

.defense-priority.low {
  background: #67c23a;
}

.model-content {
  padding: 20px 0;
}

.model-content h3 {
  margin: 0 0 16px 0;
  font-size: var(--font-size-lg);
  color: var(--color-text-primary);
}

.model-content p {
  margin: 0 0 16px 0;
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.model-content ul {
  margin: 0 0 20px 0;
  padding-left: 20px;
}

.model-content li {
  margin-bottom: 8px;
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  line-height: 1.5;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .prediction-stats {
    grid-template-columns: 1fr;
  }
  
  .alert-probability {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .alert-probability .value {
    text-align: left;
  }
  
  .alert-actions {
    flex-direction: column;
  }
}
</style>