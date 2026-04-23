<template>
  <div class="risk-profile">
    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>智能风险画像</h1>
          <div class="differentiator">
            <span class="tag">分众化</span>
            <span class="tag">精准预警</span>
            <span class="competitor">对比：通用产品</span>
          </div>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="refreshProfile">
            <el-icon><Refresh /></el-icon>
            刷新画像
          </el-button>
        </div>
      </div>
    </div>

    <div class="profile-content">
      <div class="main-area">
        <div class="profile-overview">
          <el-card shadow="hover" class="overview-card">
            <div class="overview-grid">
              <div class="risk-score-section">
                <div class="score-circle">
                  <svg class="score-svg" width="180" height="180">
                    <circle cx="90" cy="90" r="75" fill="none" stroke="#e4e7ed" stroke-width="12" />
                    <circle
                      cx="90"
                      cy="90"
                      r="75"
                      fill="none"
                      :stroke="getScoreColor(userRiskProfile.riskScore)"
                      stroke-width="12"
                      :stroke-dasharray="`${2 * Math.PI * 75 * userRiskProfile.riskScore / 100} ${2 * Math.PI * 75}`"
                      stroke-linecap="round"
                      transform="rotate(-90 90 90)"
                    />
                    <text x="90" y="85" text-anchor="middle" dominant-baseline="middle" font-size="40" font-weight="bold" :fill="getScoreColor(userRiskProfile.riskScore)">
                      {{ userRiskProfile.riskScore }}
                    </text>
                    <text x="90" y="115" text-anchor="middle" dominant-baseline="middle" font-size="14" fill="#909399">
                      风险评分
                    </text>
                  </svg>
                </div>
                <div class="score-details">
                  <div class="risk-level-badge" :class="userRiskProfile.riskLevelClass">
                    {{ userRiskProfile.riskLevel }}
                  </div>
                  <p class="risk-description">{{ userRiskProfile.riskDescription }}</p>
                </div>
              </div>

              <div class="user-info-section">
                <div class="user-header">
                  <img :src="userInfo.avatar" alt="avatar" class="user-avatar" />
                  <div class="user-badges">
                    <el-tag v-if="userInfo.isVerified" type="success" size="small">已认证</el-tag>
                    <el-tag v-if="userInfo.isHighRisk" type="danger" size="small">高风险</el-tag>
                  </div>
                </div>
                <h3 class="user-name">{{ userInfo.name }}</h3>
                <div class="user-tags">
                  <el-tag
                    v-for="tag in userInfo.tags"
                    :key="tag"
                    size="small"
                    :type="getTagType(tag)"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
                <div class="user-metrics">
                  <div class="metric-item">
                    <div class="metric-value">{{ userInfo.age }}</div>
                    <div class="metric-label">年龄</div>
                  </div>
                  <div class="metric-item">
                    <div class="metric-value">{{ userInfo.occupation }}</div>
                    <div class="metric-label">职业</div>
                  </div>
                  <div class="metric-item">
                    <div class="metric-value">{{ userInfo.education }}</div>
                    <div class="metric-label">学历</div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <div class="risk-analysis">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>风险类型分布</span>
                <el-select v-model="riskPeriod" size="small" style="width: 120px">
                  <el-option label="本周" value="week" />
                  <el-option label="本月" value="month" />
                  <el-option label="本季" value="quarter" />
                </el-select>
              </div>
            </template>
            <div class="risk-distribution">
              <div
                v-for="(risk, index) in userRiskProfile.riskDistribution"
                :key="index"
                class="distribution-item"
              >
                <div class="distribution-header">
                  <span class="distribution-type">{{ risk.type }}</span>
                  <span class="distribution-value">{{ risk.percentage }}%</span>
                </div>
                <div class="distribution-bar">
                  <div
                    class="distribution-progress"
                    :style="{ width: risk.percentage + '%', backgroundColor: getRiskColor(risk.type) }"
                  />
                </div>
                <div class="distribution-meta">
                  <span>遭遇次数: {{ risk.count }}</span>
                  <span>规避成功率: {{ risk.avoidRate }}%</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <div class="behavior-analysis">
          <el-card shadow="hover">
            <template #header>
              <span>行为特征分析</span>
            </template>
            <div class="behavior-grid">
              <div
                v-for="(behavior, index) in userRiskProfile.behaviorFeatures"
                :key="index"
                class="behavior-card"
              >
                <div class="behavior-header">
                  <span class="behavior-icon">{{ behavior.icon }}</span>
                  <span class="behavior-name">{{ behavior.feature }}</span>
                </div>
                <div class="behavior-risk">
                  <el-rate
                    v-model="behavior.riskLevel"
                    :max="5"
                    disabled
                    show-score
                    text-color="#ff9900"
                  />
                  <span class="risk-label" :class="getRiskLevelClass(behavior.riskLevel)">
                    {{ getRiskLevelText(behavior.riskLevel) }}
                  </span>
                </div>
                <div class="behavior-advice" v-if="behavior.advice">
                  <el-icon><Warning /></el-icon>
                  {{ behavior.advice }}
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <div class="environment-analysis">
          <el-card shadow="hover">
            <template #header>
              <span>环境风险评估</span>
            </template>
            <div class="environment-grid">
              <div
                v-for="(env, index) in userRiskProfile.environmentRisks"
                :key="index"
                class="environment-card"
              >
                <div class="env-header">
                  <span class="env-icon">{{ env.icon }}</span>
                  <span class="env-type">{{ env.type }}</span>
                </div>
                <div class="env-status" :class="env.statusClass">
                  {{ env.status }}
                </div>
                <div class="env-detail">{{ env.detail }}</div>
              </div>
            </div>
          </el-card>
        </div>

        <div class="risk-timeline">
          <el-card shadow="hover">
            <template #header>
              <span>风险时间线</span>
            </template>
            <el-timeline>
              <el-timeline-item
                v-for="(event, index) in riskTimeline"
                :key="index"
                :type="event.type"
                :hollow="event.hollow"
                :timestamp="event.time"
                placement="top"
              >
                <div class="timeline-content">
                  <h4>{{ event.title }}</h4>
                  <p>{{ event.description }}</p>
                  <el-tag v-if="event.level" :type="getAlertType(event.level)" size="small">
                    {{ event.level }}
                  </el-tag>
                </div>
              </el-timeline-item>
            </el-timeline>
          </el-card>
        </div>
      </div>

      <div class="side-area">
        <el-card shadow="hover" class="alerts-card">
          <template #header>
            <div class="card-header">
              <span>精准预警</span>
              <el-badge :value="unreadAlerts" :hidden="unreadAlerts === 0">
                <el-button type="text" size="small">全部已读</el-button>
              </el-badge>
            </div>
          </template>
          <div class="alerts-list">
            <div
              v-for="(alert, index) in userRiskProfile.alerts"
              :key="index"
              :class="['alert-item', alert.level, { unread: alert.unread }]"
              @click="viewAlertDetail(alert)"
            >
              <div class="alert-header">
                <el-tag :type="getAlertType(alert.level)" size="small">
                  {{ alert.level }}
                </el-tag>
                <span class="alert-time">{{ alert.time }}</span>
              </div>
              <h4 class="alert-title">{{ alert.title }}</h4>
              <p class="alert-desc">{{ alert.description }}</p>
              <div class="alert-actions">
                <el-button text type="primary" size="small" @click.stop="viewAlertDetail(alert)">
                  查看详情
                </el-button>
                <el-button text size="small" @click.stop="dismissAlert(alert)">
                  忽略
                </el-button>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="suggestions-card">
          <template #header>
            <span>风险防范建议</span>
          </template>
          <div class="suggestions-list">
            <div
              v-for="(suggestion, index) in userRiskProfile.suggestions"
              :key="index"
              :class="['suggestion-item', suggestion.priority]"
            >
              <div class="suggestion-priority">
                <span class="priority-icon">
                  {{ suggestion.priority === 'high' ? '🔴' : suggestion.priority === 'medium' ? '🟡' : '🟢' }}
                </span>
              </div>
              <div class="suggestion-content">
                <h4>{{ suggestion.title }}</h4>
                <p>{{ suggestion.description }}</p>
              </div>
              <el-button
                v-if="suggestion.action"
                type="primary"
                size="small"
                text
                @click="executeSuggestion(suggestion)"
              >
                {{ suggestion.action }}
              </el-button>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="segments-card">
          <template #header>
            <span>人群分众标签</span>
          </template>
          <div class="segments-display">
            <div class="segment-ring">
              <svg class="segment-svg" width="160" height="160">
                <circle cx="80" cy="80" r="60" fill="none" stroke="#f0f0f0" stroke-width="20" />
                <circle
                  v-for="(segment, index) in userSegments"
                  :key="index"
                  cx="80"
                  cy="80"
                  r="60"
                  fill="none"
                  :stroke="segment.color"
                  stroke-width="20"
                  :stroke-dasharray="`${2 * Math.PI * 60 * segment.percentage / 100} ${2 * Math.PI * 60}`"
                  :stroke-dashoffset="-segment.offset"
                  stroke-linecap="round"
                  transform="rotate(-90 80 80)"
                />
              </svg>
              <div class="segment-center">
                <span class="segment-main">{{ mainSegment }}</span>
                <span class="segment-label">主要标签</span>
              </div>
            </div>
            <div class="segment-legend">
              <div
                v-for="segment in userSegments"
                :key="segment.name"
                class="legend-item"
              >
                <span class="legend-color" :style="{ backgroundColor: segment.color }"></span>
                <span class="legend-name">{{ segment.name }}</span>
                <span class="legend-percent">{{ segment.percentage }}%</span>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="comparison-card">
          <template #header>
            <span>与同类用户对比</span>
          </template>
          <div class="comparison-content">
            <div class="comparison-item">
              <span class="comparison-label">风险评分</span>
              <div class="comparison-bar">
                <div class="bar-user" :style="{ width: userRiskProfile.riskScore + '%' }"></div>
                <div class="bar-avg" :style="{ left: avgComparison.riskScore + '%' }"></div>
              </div>
              <div class="comparison-value">
                <span class="user-val">{{ userRiskProfile.riskScore }}</span>
                <span class="avg-val">平均: {{ avgComparison.riskScore }}</span>
              </div>
            </div>
            <div class="comparison-item">
              <span class="comparison-label">预警响应速度</span>
              <div class="comparison-bar">
                <div class="bar-user" :style="{ width: avgComparison.responseSpeed + '%' }"></div>
                <div class="bar-avg" :style="{ left: avgComparison.responseSpeed + '%' }"></div>
              </div>
              <div class="comparison-value">
                <span class="user-val">{{ avgComparison.responseSpeed }}分</span>
                <span class="avg-val">超越{{ avgComparison.betterThan }}%用户</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog v-model="alertDetailVisible" :title="currentAlert?.title" width="600px">
      <div class="alert-detail" v-if="currentAlert">
        <el-alert
          :title="currentAlert.level"
          :type="getAlertType(currentAlert.level)"
          :description="currentAlert.description"
          show-icon
          :closable="false"
        />
        <div class="detail-section">
          <h4>风险说明</h4>
          <p>{{ currentAlert.description }}</p>
        </div>
        <div class="detail-section">
          <h4>可能影响</h4>
          <p>{{ currentAlert.impact }}</p>
        </div>
        <div class="detail-section">
          <h4>防范建议</h4>
          <p>{{ currentAlert.suggestion }}</p>
        </div>
        <div class="detail-section" v-if="currentAlert.relatedCases?.length">
          <h4>相关案例</h4>
          <div class="related-cases">
            <div v-for="c in currentAlert.relatedCases" :key="c.id" class="case-item">
              <span class="case-title">{{ c.title }}</span>
              <el-tag size="small" type="info">{{ c.type }}</el-tag>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="alertDetailVisible = false">关闭</el-button>
        <el-button type="primary" @click="alertDetailVisible = false">我已知晓</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Warning } from '@element-plus/icons-vue'

interface Alert {
  id: number
  title: string
  description: string
  level: string
  time: string
  impact: string
  suggestion: string
  unread?: boolean
  relatedCases?: { id: number; title: string; type: string }[]
}

interface Suggestion {
  id: number
  title: string
  description: string
  priority: string
  action?: string
}

interface Segment {
  name: string
  percentage: number
  color: string
  offset: number
}

const riskPeriod = ref('month')
const alertDetailVisible = ref(false)
const currentAlert = ref<Alert | null>(null)

const userInfo = ref({
  name: '反诈学习者',
  avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20friendly%20person&image_size=square',
  age: 25,
  occupation: '学生',
  education: '本科',
  tags: ['大学生', '高频上网', '月光族'],
  isVerified: true,
  isHighRisk: false
})

const userRiskProfile = ref({
  riskScore: 65,
  riskLevel: '中风险',
  riskLevelClass: 'medium',
  riskDescription: '您的风险评分为65分，属于中风险等级。根据您的上网行为和社交习惯，建议加强防范意识，关注相关预警信息。',
  riskDistribution: [
    { type: '电信诈骗', percentage: 35, count: 12, avoidRate: 85 },
    { type: '网络诈骗', percentage: 30, count: 8, avoidRate: 78 },
    { type: '金融诈骗', percentage: 20, count: 3, avoidRate: 92 },
    { type: '其他诈骗', percentage: 15, count: 5, avoidRate: 80 }
  ],
  behaviorFeatures: [
    { feature: '网络购物频率', icon: '🛒', riskLevel: 4, advice: '建议核实商家资质，避免冲动消费' },
    { feature: '社交软件使用', icon: '💬', riskLevel: 3, advice: '谨慎添加陌生好友，勿轻信转账请求' },
    { feature: '金融交易行为', icon: '💳', riskLevel: 5, advice: '务必核实对方身份，使用官方渠道交易' },
    { feature: '个人信息保护', icon: '🔒', riskLevel: 2, advice: '减少在社交媒体分享个人信息' },
    { feature: '防诈知识掌握', icon: '📚', riskLevel: 3, advice: '建议定期学习最新诈骗手法' }
  ],
  environmentRisks: [
    { type: '网络环境', icon: '🌐', status: '良好', statusClass: 'good', detail: '主要使用校园网络，风险较低' },
    { type: '社交圈子', icon: '👥', status: '一般', statusClass: 'normal', detail: '建议关注朋友圈异常转账请求' },
    { type: '经济状况', icon: '💰', status: '需注意', statusClass: 'warning', detail: '近期有大额消费，需警惕诈骗' },
    { type: '地理位置', icon: '📍', status: '安全', statusClass: 'good', detail: '当前位于常驻地区' }
  ],
  alerts: [
    {
      id: 1,
      title: '电信诈骗预警',
      description: '近期您所在地区电信诈骗案件频发，主要以冒充公检法名义进行诈骗。',
      level: '高',
      time: '2026-04-12 10:30',
      impact: '可能导致个人信息泄露和资金损失',
      suggestion: '接到自称公检法的电话时，应挂断后通过官方渠道核实。',
      unread: true,
      relatedCases: [
        { id: 1, title: '冒充公安机关诈骗案', type: '电信诈骗' },
        { id: 2, title: '冒充检察院诈骗案', type: '电信诈骗' }
      ]
    },
    {
      id: 2,
      title: '网络兼职诈骗预警',
      description: '近期发现多起网络兼职诈骗案件，骗子以刷单为名骗取钱财。',
      level: '中',
      time: '2026-04-11 15:45',
      impact: '可能导致资金损失',
      suggestion: '不要相信任何要求先垫付资金的兼职。',
      unread: true
    },
    {
      id: 3,
      title: '校园贷诈骗预警',
      description: '近期校园贷诈骗案件有所增加，骗子以低利息为诱饵诱导学生借款。',
      level: '中',
      time: '2026-04-10 09:15',
      impact: '可能导致陷入债务陷阱',
      suggestion: '遇到资金困难应向学校或正规金融机构寻求帮助。',
      unread: false
    }
  ],
  suggestions: [
    {
      id: 1,
      title: '加强个人信息保护',
      description: '不要轻易向陌生人透露个人信息，特别是身份证号、银行卡号、验证码等敏感信息。',
      priority: 'high',
      action: '去学习'
    },
    {
      id: 2,
      title: '开启支付验证',
      description: '建议开启银行卡/支付软件的二次验证功能，提高账户安全性。',
      priority: 'high',
      action: '立即设置'
    },
    {
      id: 3,
      title: '定期查看预警',
      description: '建议每周至少查看一次风险预警，及时了解最新诈骗手法。',
      priority: 'medium'
    },
    {
      id: 4,
      title: '参与防诈学习',
      description: '完成更多防诈课程可降低您的风险评分，获得更精准的保护。',
      priority: 'medium',
      action: '去学习'
    }
  ]
})

const riskTimeline = ref([
  { time: '2026-04-12 10:30', title: '收到高风险预警', description: '系统检测到您所在地区电信诈骗高发', type: 'danger', hollow: false },
  { time: '2026-04-10 14:20', title: '完成VR演练', description: '您完成了电信诈骗VR演练，获得85分', type: 'success', hollow: false },
  { time: '2026-04-08 09:15', title: '风险评分更新', description: '您的风险评分从68下降至65', type: 'primary', hollow: true },
  { time: '2026-04-05 16:45', title: '收到中风险预警', description: '网络兼职诈骗预警已忽略', type: 'warning', hollow: true }
])

const userSegments = ref<Segment[]>([
  { name: '学生群体', percentage: 40, color: '#409eff', offset: 0 },
  { name: '年轻网民', percentage: 30, color: '#67c23a', offset: 100.5 },
  { name: '网络高频', percentage: 20, color: '#e6a23c', offset: 188.5 },
  { name: '其他', percentage: 10, color: '#909399', offset: 226.2 }
])

const mainSegment = computed(() => {
  const main = userSegments.value.reduce((prev, curr) =>
    curr.percentage > prev.percentage ? curr : prev
  )
  return main.name
})

const unreadAlerts = computed(() => {
  return userRiskProfile.value.alerts.filter(a => a.unread).length
})

const avgComparison = ref({
  riskScore: 72,
  responseSpeed: 85,
  betterThan: 65
})

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

function getAlertType(level: string): string {
  const typeMap: Record<string, string> = {
    '高': 'danger',
    '中': 'warning',
    '低': 'success'
  }
  return typeMap[level] || 'info'
}

function getTagType(tag: string): string {
  const typeMap: Record<string, string> = {
    '大学生': 'primary',
    '高频上网': 'warning',
    '月光族': 'danger'
  }
  return typeMap[tag] || 'info'
}

function getRiskLevelClass(level: number): string {
  if (level >= 4) return 'high'
  if (level >= 3) return 'medium'
  return 'low'
}

function getRiskLevelText(level: number): string {
  if (level >= 4) return '风险较高'
  if (level >= 3) return '风险中等'
  return '风险较低'
}

function refreshProfile() {
  ElMessage.success('风险画像已刷新')
}

function viewAlertDetail(alert: Alert) {
  currentAlert.value = alert
  alert.unread = false
  alertDetailVisible.value = true
}

function dismissAlert(alert: Alert) {
  alert.unread = false
  ElMessage.success('预警已忽略')
}

function executeSuggestion(suggestion: Suggestion) {
  ElMessage.info(`跳转至：${suggestion.title}`)
}

onMounted(() => {
  console.log('智能风险画像初始化')
})
</script>

<style scoped>
.risk-profile {
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f0f3f7 0%, #e8ecf1 100%);
  min-height: calc(100vh - 60px);
}

.header {
  margin-bottom: 24px;
}

.header-content {
  background: linear-gradient(135deg, #f56c6c 0%, #e6a23c 100%);
  border-radius: 16px;
  padding: 32px;
  color: white;
  box-shadow: 0 8px 32px rgba(245, 108, 108, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-text h1 {
  margin: 0 0 12px 0;
  font-size: 32px;
}

.differentiator {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.tag {
  background: rgba(255, 255, 255, 0.2);
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
}

.competitor {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.profile-content {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

.main-area {
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-width: 0;
}

.side-area {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-card {
  overflow: hidden;
}

.overview-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 24px 0;
}

.risk-score-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.score-circle {
  flex-shrink: 0;
}

.score-details {
  flex: 1;
}

.risk-level-badge {
  display: inline-block;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
}

.risk-level-badge.high {
  background: rgba(245, 108, 108, 0.15);
  color: #f56c6c;
}

.risk-level-badge.medium {
  background: rgba(230, 162, 60, 0.15);
  color: #e6a23c;
}

.risk-level-badge.low {
  background: rgba(103, 194, 58, 0.15);
  color: #67c23a;
}

.risk-description {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.user-info-section {
  text-align: center;
}

.user-header {
  position: relative;
  display: inline-block;
  margin-bottom: 12px;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.user-badges {
  position: absolute;
  top: 0;
  right: -10px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  margin: 0 0 12px 0;
  font-size: 20px;
}

.user-tags {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.user-metrics {
  display: flex;
  justify-content: center;
  gap: 24px;
}

.metric-item {
  text-align: center;
}

.metric-value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.metric-label {
  font-size: 13px;
  color: #909399;
}

.risk-distribution {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 12px 0;
}

.distribution-item {
  padding: 12px 16px;
  background: #f9f9f9;
  border-radius: 8px;
}

.distribution-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.distribution-type {
  font-weight: 500;
  color: #333;
}

.distribution-value {
  font-weight: bold;
  color: #409eff;
}

.distribution-bar {
  height: 8px;
  background: #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.distribution-progress {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.distribution-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}

.behavior-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.behavior-card {
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  transition: all 0.2s;
}

.behavior-card:hover {
  background: #f5f5f5;
}

.behavior-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.behavior-icon {
  font-size: 20px;
}

.behavior-name {
  font-weight: 500;
  color: #333;
}

.behavior-risk {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.risk-label {
  font-size: 13px;
}

.risk-label.high {
  color: #f56c6c;
}

.risk-label.medium {
  color: #e6a23c;
}

.risk-label.low {
  color: #67c23a;
}

.behavior-advice {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
  padding: 8px;
  background: #fff;
  border-radius: 4px;
}

.environment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.environment-card {
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  text-align: center;
}

.env-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
}

.env-icon {
  font-size: 24px;
}

.env-type {
  font-weight: 500;
  color: #333;
}

.env-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  margin-bottom: 8px;
}

.env-status.good {
  background: rgba(103, 194, 58, 0.15);
  color: #67c23a;
}

.env-status.normal {
  background: rgba(230, 162, 60, 0.15);
  color: #e6a23c;
}

.env-status.warning {
  background: rgba(245, 108, 108, 0.15);
  color: #f56c6c;
}

.env-detail {
  font-size: 12px;
  color: #909399;
}

.risk-timeline {
  padding-bottom: 16px;
}

.timeline-content {
  padding: 8px 0;
}

.timeline-content h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
}

.timeline-content p {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #606266;
}

.alerts-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.alert-item {
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  border-left: 4px solid;
  transition: all 0.2s;
  cursor: pointer;
}

.alert-item:hover {
  background: #f5f5f5;
}

.alert-item.high {
  border-left-color: #f56c6c;
}

.alert-item.medium {
  border-left-color: #e6a23c;
}

.alert-item.low {
  border-left-color: #67c23a;
}

.alert-item.unread {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05) 0%, rgba(64, 158, 255, 0.02) 100%);
}

.alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.alert-time {
  font-size: 12px;
  color: #909399;
}

.alert-title {
  margin: 0 0 8px 0;
  font-size: 15px;
  color: #333;
}

.alert-desc {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.alert-actions {
  display: flex;
  gap: 8px;
}

.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
}

.suggestion-item.high {
  background: rgba(245, 108, 108, 0.08);
}

.suggestion-item.medium {
  background: rgba(230, 162, 60, 0.08);
}

.suggestion-item.low {
  background: rgba(103, 194, 58, 0.08);
}

.priority-icon {
  font-size: 18px;
}

.suggestion-content {
  flex: 1;
  min-width: 0;
}

.suggestion-content h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #333;
}

.suggestion-content p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
}

.segments-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.segment-ring {
  position: relative;
}

.segment-center {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.segment-main {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.segment-label {
  font-size: 12px;
  color: #909399;
}

.segment-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.legend-name {
  color: #333;
}

.legend-percent {
  color: #909399;
}

.comparison-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comparison-item {
  padding: 12px 0;
}

.comparison-label {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  display: block;
}

.comparison-bar {
  position: relative;
  height: 8px;
  background: #e4e7ed;
  border-radius: 4px;
  margin-bottom: 8px;
}

.bar-user {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #66b1ff);
  border-radius: 4px;
}

.bar-avg {
  position: absolute;
  top: -4px;
  width: 2px;
  height: 16px;
  background: #f56c6c;
  transform: translateX(-50%);
}

.comparison-value {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.user-val {
  font-weight: bold;
  color: #409eff;
}

.avg-val {
  color: #909399;
}

.alert-detail {
  padding: 8px 0;
}

.detail-section {
  margin-top: 20px;
}

.detail-section h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #333;
}

.detail-section p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.related-cases {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.case-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.case-title {
  font-size: 13px;
  color: #333;
}

@media (max-width: 1200px) {
  .profile-content {
    grid-template-columns: 1fr;
  }

  .side-area {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .overview-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .risk-score-section {
    flex-direction: column;
    text-align: center;
  }

  .behavior-grid {
    grid-template-columns: 1fr;
  }
}
</style>
