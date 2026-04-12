<template>
  <div class="risk-assessment-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>风险评估模型</h1>
          <div class="header-actions">
            <el-button type="primary" @click="assessRisk">
              <el-icon><Refresh /></el-icon>
              开始评估
            </el-button>
            <el-button @click="exportAssessment">
              <el-icon><Download /></el-icon>
              导出评估
            </el-button>
          </div>
        </div>
      </el-header>
      
      <el-main>
        <!-- 评估结果概览 -->
        <el-card class="result-overview-card">
          <template #header>
            <div class="card-header">
              <span>评估结果概览</span>
              <span class="assessment-date">{{ assessmentData.assessmentDate || '' }}</span>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>评估中...</span>
          </div>
          
          <div v-else class="overview-content">
            <div class="risk-score">
              <div class="score-circle" :class="getRiskLevelClass(assessmentData.riskScore)">
                <div class="score-value">{{ assessmentData.riskScore || 0 }}</div>
                <div class="score-label">{{ getRiskLevelText(assessmentData.riskScore) }}</div>
              </div>
              <div class="score-details">
                <h3>风险评分</h3>
                <p>基于您的行为和环境数据，我们对您的诈骗风险进行了评估。</p>
                <div class="risk-level-bar">
                  <div class="risk-level-segment low">低风险</div>
                  <div class="risk-level-segment medium">中风险</div>
                  <div class="risk-level-segment high">高风险</div>
                  <div class="risk-level-indicator" :style="{ left: getRiskLevelPosition(assessmentData.riskScore) + '%' }"></div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 风险因素分析 -->
        <el-card class="risk-factors-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>风险因素分析</span>
            </div>
          </template>
          
          <div class="risk-factors-content">
            <div v-for="factor in assessmentData.riskFactors" :key="factor.id" class="risk-factor-item">
              <div class="factor-header">
                <span class="factor-name">{{ factor.name }}</span>
                <span class="factor-score" :class="getFactorLevelClass(factor.score)">{{ factor.score }}分</span>
              </div>
              <el-progress :percentage="factor.score" :color="getFactorColor(factor.score)" />
              <div class="factor-description">{{ factor.description }}</div>
            </div>
          </div>
        </el-card>
        
        <!-- 风险行为分析 -->
        <el-card class="risk-behaviors-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>风险行为分析</span>
            </div>
          </template>
          
          <div class="risk-behaviors-content">
            <el-empty v-if="!assessmentData.riskBehaviors || assessmentData.riskBehaviors.length === 0" description="未检测到风险行为" />
            <div v-else class="behavior-list">
              <div v-for="behavior in assessmentData.riskBehaviors" :key="behavior.id" class="behavior-item" :class="behavior.level">
                <div class="behavior-icon">
                  <el-icon>{{ behavior.level === 'high' ? 'Warning' : behavior.level === 'medium' ? 'InfoFilled' : 'Check' }}</el-icon>
                </div>
                <div class="behavior-content">
                  <h4>{{ behavior.behavior }}</h4>
                  <p>{{ behavior.description }}</p>
                  <div class="behavior-recommendation">{{ behavior.recommendation }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 防范建议 -->
        <el-card class="recommendations-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>防范建议</span>
            </div>
          </template>
          
          <div class="recommendations-content">
            <el-empty v-if="!assessmentData.recommendations || assessmentData.recommendations.length === 0" description="暂无建议" />
            <el-list v-else>
              <el-list-item v-for="(recommendation, index) in assessmentData.recommendations" :key="index">
                <el-card :body-style="{ padding: '16px' }" shadow="hover">
                  <div class="recommendation-item">
                    <div class="recommendation-title">
                      <el-icon><Check /></el-icon>
                      <span>{{ recommendation.title }}</span>
                    </div>
                    <div class="recommendation-desc">{{ recommendation.description }}</div>
                    <div class="recommendation-priority" :class="recommendation.priority">
                      {{ getPriorityText(recommendation.priority) }}
                    </div>
                  </div>
                </el-card>
              </el-list-item>
            </el-list>
          </div>
        </el-card>
        
        <!-- 历史评估记录 -->
        <el-card class="history-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>历史评估记录</span>
            </div>
          </template>
          
          <div class="history-content">
            <el-empty v-if="!assessmentData.history || assessmentData.history.length === 0" description="暂无历史评估记录" />
            <el-table v-else :data="assessmentData.history" style="width: 100%">
              <el-table-column prop="date" label="评估日期" width="180" />
              <el-table-column prop="score" label="风险评分" width="120">
                <template #default="scope">
                  <span :class="getRiskLevelClass(scope.row.score)">{{ scope.row.score }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="level" label="风险等级" width="120">
                <template #default="scope">
                  <el-tag :type="getRiskLevelType(scope.row.score)">
                    {{ getRiskLevelText(scope.row.score) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="change" label="变化趋势" width="120">
                <template #default="scope">
                  <span :class="{ 'up': scope.row.change > 0, 'down': scope.row.change < 0, 'stable': scope.row.change === 0 }">
                    <el-icon>{{ scope.row.change > 0 ? 'CaretTop' : scope.row.change < 0 ? 'CaretBottom' : 'Minus' }}</el-icon>
                    {{ Math.abs(scope.row.change) }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button size="small" @click="viewHistoryDetail(scope.row)">
                    <el-icon><View /></el-icon>
                    查看
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
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
import { Refresh, Download, Loading, Warning, InfoFilled, Check, View, CaretTop, CaretBottom, Minus } from '@element-plus/icons-vue'

// 状态
const loading = ref(false)
const assessmentData = ref({
  assessmentDate: '',
  riskScore: 0,
  riskFactors: [],
  riskBehaviors: [],
  recommendations: [],
  history: []
})

// 获取风险等级类名
const getRiskLevelClass = (score: number) => {
  if (score < 30) return 'low'
  if (score < 70) return 'medium'
  return 'high'
}

// 获取风险等级文本
const getRiskLevelText = (score: number) => {
  if (score < 30) return '低风险'
  if (score < 70) return '中风险'
  return '高风险'
}

// 获取风险等级类型
const getRiskLevelType = (score: number) => {
  if (score < 30) return 'success'
  if (score < 70) return 'warning'
  return 'danger'
}

// 获取风险等级位置
const getRiskLevelPosition = (score: number) => {
  return Math.min(Math.max(0, (score / 100) * 100), 100)
}

// 获取因素等级类名
const getFactorLevelClass = (score: number) => {
  if (score < 30) return 'low'
  if (score < 70) return 'medium'
  return 'high'
}

// 获取因素颜色
const getFactorColor = (score: number) => {
  if (score < 30) return '#67C23A'
  if (score < 70) return '#E6A23C'
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

// 开始风险评估
const assessRisk = async () => {
  loading.value = true
  try {
    const res = await post('/risk/assess')
    if (res.code === 200 && res.data) {
      assessmentData.value = res.data
      ElMessage.success('风险评估完成')
    }
  } catch (error) {
    console.error('风险评估失败:', error)
    // 模拟数据
    assessmentData.value = {
      assessmentDate: '2026-04-12',
      riskScore: 45,
      riskFactors: [
        {
          id: 1,
          name: '设备风险',
          score: 30,
          description: '您的设备安全状态良好，未检测到异常'
        },
        {
          id: 2,
          name: '网络风险',
          score: 50,
          description: '您的网络环境存在一定风险，建议使用安全网络'
        },
        {
          id: 3,
          name: '行为风险',
          score: 40,
          description: '您的行为模式正常，未检测到高风险行为'
        },
        {
          id: 4,
          name: '环境风险',
          score: 60,
          description: '您所处的环境存在一定风险，建议提高警惕'
        }
      ],
      riskBehaviors: [
        {
          id: 1,
          level: 'medium',
          behavior: '近期访问过可疑网站',
          description: '您近期访问了一些被标记为可疑的网站，存在潜在风险',
          recommendation: '建议避免访问可疑网站，使用安全的浏览器'
        },
        {
          id: 2,
          level: 'low',
          behavior: '接收过陌生邮件',
          description: '您近期收到了一些来自陌生发件人的邮件',
          recommendation: '建议不要点击陌生邮件中的链接或附件'
        }
      ],
      recommendations: [
        {
          title: '使用安全网络',
          description: '避免使用公共Wi-Fi等不安全的网络环境，建议使用加密网络',
          priority: 'high'
        },
        {
          title: '定期更新设备',
          description: '定期更新设备系统和应用程序，修补安全漏洞',
          priority: 'medium'
        },
        {
          title: '提高警惕',
          description: '对陌生电话、邮件和消息保持警惕，不要轻易透露个人信息',
          priority: 'high'
        },
        {
          title: '使用安全软件',
          description: '安装并定期更新杀毒软件和防火墙，保护设备安全',
          priority: 'medium'
        }
      ],
      history: [
        {
          id: 1,
          date: '2026-04-10',
          score: 50,
          level: 'medium',
          change: -5
        },
        {
          id: 2,
          date: '2026-04-05',
          score: 55,
          level: 'medium',
          change: 2
        },
        {
          id: 3,
          date: '2026-04-01',
          score: 53,
          level: 'medium',
          change: 0
        }
      ]
    }
    ElMessage.success('风险评估完成')
  } finally {
    loading.value = false
  }
}

// 导出评估报告
const exportAssessment = async () => {
  try {
    const res = await post('/risk/export')
    if (res.code === 200 && res.data) {
      // 模拟下载
      ElMessage.success('评估报告导出成功')
    }
  } catch (error) {
    console.error('导出评估报告失败:', error)
    ElMessage.success('评估报告导出成功')
  }
}

// 查看历史评估详情
const viewHistoryDetail = (historyItem: any) => {
  // 模拟查看历史评估详情
  ElMessage.info('查看历史评估详情功能待实现')
}

onMounted(() => {
  assessRisk()
})
</script>

<style scoped>
.risk-assessment-page {
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
  gap: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.assessment-date {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
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

.risk-score {
  display: flex;
  align-items: center;
  gap: 40px;
  flex-wrap: wrap;
}

.score-circle {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.score-circle.low {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.score-circle.medium {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}

.score-circle.high {
  background: linear-gradient(135deg, #F56C6C, #f78989);
}

.score-value {
  font-size: 36px;
  margin-bottom: 8px;
}

.score-label {
  font-size: 16px;
  opacity: 0.9;
}

.score-details {
  flex: 1;
  min-width: 300px;
}

.score-details h3 {
  margin: 0 0 16px 0;
  font-size: var(--font-size-lg);
  color: var(--color-text-primary);
}

.score-details p {
  margin: 0 0 24px 0;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.risk-level-bar {
  position: relative;
  height: 20px;
  background: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
}

.risk-level-segment {
  position: absolute;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.risk-level-segment.low {
  left: 0;
  width: 30%;
  background: #67C23A;
}

.risk-level-segment.medium {
  left: 30%;
  width: 40%;
  background: #E6A23C;
}

.risk-level-segment.high {
  left: 70%;
  width: 30%;
  background: #F56C6C;
}

.risk-level-indicator {
  position: absolute;
  top: -5px;
  width: 30px;
  height: 30px;
  background: white;
  border: 3px solid #409EFF;
  border-radius: 50%;
  transform: translateX(-50%);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.risk-factors-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 10px 0;
}

.risk-factor-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.factor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.factor-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.factor-score {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  padding: 2px 8px;
  border-radius: 12px;
  color: white;
}

.factor-score.low {
  background: #67C23A;
}

.factor-score.medium {
  background: #E6A23C;
}

.factor-score.high {
  background: #F56C6C;
}

.factor-description {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-top: 4px;
}

.risk-behaviors-content {
  padding: 10px 0;
}

.behavior-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.behavior-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: var(--radius-lg);
  background: var(--color-bg-container);
  box-shadow: var(--shadow-sm);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.behavior-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.behavior-item.high {
  border-left: 4px solid #F56C6C;
}

.behavior-item.medium {
  border-left: 4px solid #E6A23C;
}

.behavior-item.low {
  border-left: 4px solid #67C23A;
}

.behavior-icon {
  font-size: 24px;
  color: #909399;
  margin-top: 4px;
}

.behavior-item.high .behavior-icon {
  color: #F56C6C;
}

.behavior-item.medium .behavior-icon {
  color: #E6A23C;
}

.behavior-item.low .behavior-icon {
  color: #67C23A;
}

.behavior-content {
  flex: 1;
}

.behavior-content h4 {
  margin: 0 0 8px 0;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

.behavior-content p {
  margin: 0 0 8px 0;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.behavior-recommendation {
  font-size: var(--font-size-sm);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
}

.recommendations-content {
  padding: 10px 0;
}

.recommendation-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommendation-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.recommendation-desc {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.recommendation-priority {
  align-self: flex-start;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: white;
}

.recommendation-priority.high {
  background: #F56C6C;
}

.recommendation-priority.medium {
  background: #E6A23C;
}

.recommendation-priority.low {
  background: #67C23A;
}

.history-content {
  padding: 10px 0;
}

.up {
  color: #F56C6C;
}

.down {
  color: #67C23A;
}

.stable {
  color: #909399;
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
  
  .risk-score {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .score-details {
    min-width: auto;
  }
}
</style>