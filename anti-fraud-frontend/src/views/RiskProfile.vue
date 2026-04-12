<template>
  <div class="risk-profile">
    <!-- 头部 -->
    <div class="header">
      <h1>智能风险画像</h1>
      <p>通用产品 - 分众化+精准预警</p>
    </div>

    <!-- 风险画像概览 -->
    <div class="profile-overview">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>风险画像概览</span>
            <el-button type="primary" @click="refreshProfile">刷新画像</el-button>
          </div>
        </template>
        <div class="overview-content">
          <div class="risk-score">
            <div class="score-circle">
              <svg class="score-svg" width="200" height="200">
                <circle cx="100" cy="100" r="80" fill="none" stroke="#e4e7ed" stroke-width="10" />
                <circle 
                  cx="100" 
                  cy="100" 
                  r="80" 
                  fill="none" 
                  :stroke="getScoreColor(userRiskProfile.riskScore)" 
                  stroke-width="10" 
                  :stroke-dasharray="`${2 * Math.PI * 80 * userRiskProfile.riskScore / 100} ${2 * Math.PI * 80}`"
                  stroke-linecap="round"
                  transform="rotate(-90 100 100)"
                />
                <text x="100" y="100" text-anchor="middle" dominant-baseline="middle" font-size="36" font-weight="bold" :fill="getScoreColor(userRiskProfile.riskScore)">
                  {{ userRiskProfile.riskScore }}
                </text>
                <text x="100" y="130" text-anchor="middle" dominant-baseline="middle" font-size="14" fill="#606266">
                  风险评分
                </text>
              </svg>
            </div>
            <div class="score-info">
              <h3>{{ userRiskProfile.riskLevel }}</h3>
              <p>{{ userRiskProfile.riskDescription }}</p>
            </div>
          </div>
          <div class="risk-metrics">
            <div class="metric-item">
              <div class="metric-value">{{ userRiskProfile.age }}</div>
              <div class="metric-label">年龄</div>
            </div>
            <div class="metric-item">
              <div class="metric-value">{{ userRiskProfile.occupation }}</div>
              <div class="metric-label">职业</div>
            </div>
            <div class="metric-item">
              <div class="metric-value">{{ userRiskProfile.education }}</div>
              <div class="metric-label">教育程度</div>
            </div>
            <div class="metric-item">
              <div class="metric-value">{{ userRiskProfile.internetUsage }}</div>
              <div class="metric-label">网络使用频率</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 风险分析 -->
    <div class="risk-analysis">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>风险分析</span>
          </div>
        </template>
        <div class="analysis-content">
          <!-- 风险类型分布 -->
          <div class="analysis-section">
            <h3>风险类型分布</h3>
            <div class="risk-distribution">
              <div 
                v-for="(risk, index) in userRiskProfile.riskDistribution" 
                :key="index"
                class="distribution-item"
              >
                <div class="distribution-label">{{ risk.type }}</div>
                <div class="distribution-bar">
                  <div 
                    class="distribution-progress" 
                    :style="{ width: risk.percentage + '%', backgroundColor: getRiskColor(risk.type) }"
                  />
                </div>
                <div class="distribution-percentage">{{ risk.percentage }}%</div>
              </div>
            </div>
          </div>

          <!-- 行为特征分析 -->
          <div class="analysis-section">
            <h3>行为特征分析</h3>
            <div class="behavior-analysis">
              <div 
                v-for="(behavior, index) in userRiskProfile.behaviorFeatures" 
                :key="index"
                class="behavior-item"
              >
                <div class="behavior-label">{{ behavior.feature }}</div>
                <div class="behavior-value">
                  <el-rate 
                    v-model="behavior.riskLevel" 
                    :max="5" 
                    disabled 
                    show-score 
                    text-color="#606266"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 环境风险分析 -->
          <div class="analysis-section">
            <h3>环境风险分析</h3>
            <div class="environment-analysis">
              <div 
                v-for="(env, index) in userRiskProfile.environmentRisks" 
                :key="index"
                class="environment-item"
              >
                <div class="environment-label">{{ env.type }}</div>
                <div class="environment-risk">
                  <el-tag :type="getEnvRiskType(env.riskLevel)">{{ env.riskLevel }}</el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 精准预警 -->
    <div class="risk-alerts">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>精准预警</span>
            <el-button type="info" @click="viewAllAlerts">查看全部</el-button>
          </div>
        </template>
        <div class="alerts-content">
          <div 
            v-for="(alert, index) in userRiskProfile.alerts" 
            :key="index"
            :class="['alert-item', alert.level]"
          >
            <div class="alert-header">
              <el-tag :type="getAlertType(alert.level)">{{ alert.level }}</el-tag>
              <span class="alert-time">{{ alert.time }}</span>
            </div>
            <div class="alert-content">
              <h4>{{ alert.title }}</h4>
              <p>{{ alert.description }}</p>
            </div>
            <div class="alert-actions">
              <el-button type="text" @click="viewAlertDetail(alert)">查看详情</el-button>
              <el-button type="text" @click="dismissAlert(alert)">忽略</el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 风险防范建议 -->
    <div class="risk-suggestions">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>风险防范建议</span>
          </div>
        </template>
        <div class="suggestions-content">
          <div 
            v-for="(suggestion, index) in userRiskProfile.suggestions" 
            :key="index"
            class="suggestion-item"
          >
            <div class="suggestion-icon">
              <span :style="{ fontSize: '24px', color: suggestion.type === '高' ? '#f56c6c' : suggestion.type === '中' ? '#e6a23c' : '#67c23a' }">
                ⚠️
              </span>
            </div>
            <div class="suggestion-content">
              <h4>{{ suggestion.title }}</h4>
              <p>{{ suggestion.description }}</p>
            </div>
            <div class="suggestion-type">
              <el-tag :type="suggestion.type === '高' ? 'danger' : suggestion.type === '中' ? 'warning' : 'success'">
                {{ suggestion.type }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 预警详情对话框 -->
    <el-dialog v-model="alertDetailVisible" :title="currentAlert?.title" width="600px">
      <div class="alert-detail" v-if="currentAlert">
        <div class="alert-meta">
          <el-tag :type="getAlertType(currentAlert.level)">{{ currentAlert.level }}</el-tag>
          <span class="alert-time">{{ currentAlert.time }}</span>
        </div>
        <div class="alert-description">
          <p>{{ currentAlert.description }}</p>
        </div>
        <div class="alert-impact">
          <h4>可能影响</h4>
          <p>{{ currentAlert.impact }}</p>
        </div>
        <div class="alert-suggestion">
          <h4>防范建议</h4>
          <p>{{ currentAlert.suggestion }}</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="alertDetailVisible = false">关闭</el-button>
          <el-button type="primary" @click="alertDetailVisible = false">已了解</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const userRiskProfile = ref({
  riskScore: 65,
  riskLevel: '中风险',
  riskDescription: '您的风险评分为65分，属于中风险等级。建议加强防范意识，关注相关预警信息。',
  age: 25,
  occupation: '学生',
  education: '本科',
  internetUsage: '高频',
  riskDistribution: [
    { type: '电信诈骗', percentage: 35 },
    { type: '网络诈骗', percentage: 30 },
    { type: '金融诈骗', percentage: 20 },
    { type: '其他诈骗', percentage: 15 }
  ],
  behaviorFeatures: [
    { feature: '网络购物频率', riskLevel: 4 },
    { feature: '社交软件使用', riskLevel: 3 },
    { feature: '金融交易行为', riskLevel: 5 },
    { feature: '个人信息保护意识', riskLevel: 2 },
    { feature: '防诈知识掌握程度', riskLevel: 3 }
  ],
  environmentRisks: [
    { type: '网络环境', riskLevel: '中风险' },
    { type: '社交圈子', riskLevel: '低风险' },
    { type: '经济状况', riskLevel: '中风险' },
    { type: '地域风险', riskLevel: '低风险' }
  ],
  alerts: [
    {
      id: 1,
      title: '电信诈骗预警',
      description: '近期您所在地区电信诈骗案件频发，主要以冒充公检法名义进行诈骗。',
      level: '高',
      time: '2026-04-12 10:30',
      impact: '可能导致个人信息泄露和资金损失',
      suggestion: '接到自称公检法的电话时，应挂断后通过官方渠道核实。'
    },
    {
      id: 2,
      title: '网络兼职诈骗预警',
      description: '近期发现多起网络兼职诈骗案件，骗子以刷单为名骗取钱财。',
      level: '中',
      time: '2026-04-11 15:45',
      impact: '可能导致资金损失',
      suggestion: '不要相信任何要求先垫付资金的兼职。'
    },
    {
      id: 3,
      title: '校园贷诈骗预警',
      description: '近期校园贷诈骗案件有所增加，骗子以低利息为诱饵诱导学生借款。',
      level: '中',
      time: '2026-04-10 09:15',
      impact: '可能导致陷入债务陷阱',
      suggestion: '遇到资金困难应向学校或正规金融机构寻求帮助。'
    }
  ],
  suggestions: [
    {
      id: 1,
      title: '加强个人信息保护',
      description: '不要轻易向陌生人透露个人信息，特别是身份证号、银行卡号、验证码等敏感信息。',
      type: '高'
    },
    {
      id: 2,
      title: '提高防诈意识',
      description: '定期学习防诈知识，了解最新的诈骗手法和防范措施。',
      type: '高'
    },
    {
      id: 3,
      title: '谨慎网络交易',
      description: '进行网络交易时，应选择正规平台，仔细核实对方身份。',
      type: '中'
    },
    {
      id: 4,
      title: '及时报警',
      description: '遇到可疑情况或被骗时，应及时报警，寻求警方帮助。',
      type: '高'
    },
    {
      id: 5,
      title: '关注预警信息',
      description: '定期查看系统发布的预警信息，了解最新的诈骗趋势。',
      type: '中'
    }
  ]
})

const alertDetailVisible = ref(false)
const currentAlert = ref<any>(null)

function getScoreColor(score: number): string {
  if (score >= 80) return '#f56c6c'
  if (score >= 60) return '#e6a23c'
  return '#67c23a'
}

function getRiskColor(riskType: string): string {
  const colorMap: Record<string, string> = {
    '电信诈骗': '#f56c6c',
    '网络诈骗': '#e6a23c',
    '金融诈骗': '#409eff',
    '其他诈骗': '#909399'
  }
  return colorMap[riskType] || '#909399'
}

function getEnvRiskType(riskLevel: string): string {
  const typeMap: Record<string, string> = {
    '高风险': 'danger',
    '中风险': 'warning',
    '低风险': 'success'
  }
  return typeMap[riskLevel] || 'info'
}

function getAlertType(level: string): string {
  const typeMap: Record<string, string> = {
    '高': 'danger',
    '中': 'warning',
    '低': 'success'
  }
  return typeMap[level] || 'info'
}

function refreshProfile() {
  // 模拟刷新风险画像
  ElMessage.success('风险画像已刷新')
}

function viewAllAlerts() {
  // 模拟查看全部预警
  ElMessage.info('查看全部预警功能开发中')
}

function viewAlertDetail(alert: any) {
  currentAlert.value = alert
  alertDetailVisible.value = true
}

function dismissAlert(alert: any) {
  // 模拟忽略预警
  ElMessage.success('预警已忽略')
}

onMounted(() => {
  // 初始化风险画像数据
  console.log('智能风险画像初始化')
})
</script>

<style scoped>
.risk-profile {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 32px;
}

.header h1 {
  font-size: 32px;
  margin-bottom: 8px;
  color: #333;
}

.header p {
  font-size: 16px;
  color: #666;
}

.profile-overview,
.risk-analysis,
.risk-alerts,
.risk-suggestions {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-content {
  display: flex;
  gap: 40px;
  align-items: center;
  padding: 20px 0;
}

.risk-score {
  display: flex;
  align-items: center;
  gap: 40px;
  flex: 1;
}

.score-circle {
  flex-shrink: 0;
}

.score-info {
  flex: 1;
}

.score-info h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.score-info p {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.risk-metrics {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  flex: 1;
}

.metric-item {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.metric-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.metric-label {
  font-size: 14px;
  color: #606266;
}

.analysis-content {
  padding: 20px 0;
}

.analysis-section {
  margin-bottom: 32px;
}

.analysis-section h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #333;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.risk-distribution {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.distribution-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.distribution-label {
  width: 100px;
  font-size: 14px;
  color: #333;
}

.distribution-bar {
  flex: 1;
  height: 20px;
  background: #f5f7fa;
  border-radius: 10px;
  overflow: hidden;
}

.distribution-progress {
  height: 100%;
  border-radius: 10px;
  transition: width 0.3s ease;
}

.distribution-percentage {
  width: 60px;
  font-size: 14px;
  color: #606266;
  text-align: right;
}

.behavior-analysis {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.behavior-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.behavior-label {
  font-size: 14px;
  color: #333;
}

.environment-analysis {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.environment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.environment-label {
  font-size: 14px;
  color: #333;
}

.alerts-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px 0;
}

.alert-item {
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid;
  transition: all 0.3s ease;
}

.alert-item.high {
  background: #fef0f0;
  border-left-color: #f56c6c;
}

.alert-item.medium {
  background: #fdf6ec;
  border-left-color: #e6a23c;
}

.alert-item.low {
  background: #f0f9eb;
  border-left-color: #67c23a;
}

.alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.alert-time {
  font-size: 12px;
  color: #909399;
}

.alert-content h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.alert-content p {
  margin: 0 0 16px 0;
  color: #666;
  line-height: 1.5;
}

.alert-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.suggestions-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px 0;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.suggestion-item:hover {
  background: #ecf5ff;
}

.suggestion-icon {
  margin-top: 4px;
  flex-shrink: 0;
}

.suggestion-content {
  flex: 1;
}

.suggestion-content h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.suggestion-content p {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.suggestion-type {
  flex-shrink: 0;
  margin-top: 4px;
}

.alert-detail {
  padding: 20px 0;
}

.alert-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.alert-description {
  margin-bottom: 24px;
}

.alert-description p {
  line-height: 1.5;
  color: #666;
}

.alert-impact,
.alert-suggestion {
  margin-bottom: 24px;
}

.alert-impact h4,
.alert-suggestion h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
}

.alert-impact p,
.alert-suggestion p {
  margin: 0;
  line-height: 1.5;
  color: #666;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .risk-profile {
    padding: 16px;
  }
  
  .overview-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 24px;
  }
  
  .risk-score {
    flex-direction: column;
    align-items: flex-start;
    gap: 24px;
  }
  
  .risk-metrics {
    grid-template-columns: 1fr;
  }
  
  .behavior-analysis,
  .environment-analysis {
    grid-template-columns: 1fr;
  }
  
  .distribution-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .distribution-bar {
    width: 100%;
  }
  
  .suggestion-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .suggestion-type {
    align-self: flex-end;
  }
}
</style>