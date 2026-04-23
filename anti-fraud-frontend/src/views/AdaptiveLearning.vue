<template>
  <div class="adaptive-learning">
    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>自适应学习路径</h1>
          <div class="differentiator">
            <span class="tag">个性化推荐</span>
            <span class="tag">实时调整</span>
            <span class="competitor">对比：国家反诈中心</span>
          </div>
        </div>
        <div class="header-features">
          <div class="feature-item">
            <span class="feature-icon">🎯</span>
            <span class="feature-text">AI智能推荐</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">📊</span>
            <span class="feature-text">学习数据分析</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🔄</span>
            <span class="feature-text">动态路径调整</span>
          </div>
        </div>
      </div>
    </div>

    <div class="learning-content">
      <div class="main-area">
        <el-card shadow="hover" class="profile-card">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span class="icon">👤</span>
                <span>学习画像</span>
              </div>
              <el-button type="primary" size="small" @click="refreshProfile">
                <el-icon><Refresh /></el-icon>
                刷新画像
              </el-button>
            </div>
          </template>
          <div class="profile-content">
            <div class="profile-grid">
              <div class="profile-item">
                <div class="item-label">风险等级</div>
                <div class="item-value">
                  <el-tag :type="getRiskTagType(userProfile.riskLevel)" size="large">
                    {{ userProfile.riskLevel }}
                  </el-tag>
                </div>
              </div>
              <div class="profile-item">
                <div class="item-label">学习阶段</div>
                <div class="item-value">
                  <el-tag type="info" size="large">{{ userProfile.learningStage }}</el-tag>
                </div>
              </div>
              <div class="profile-item">
                <div class="item-label">学习偏好</div>
                <div class="item-value">
                  <el-tag size="large" effect="plain">{{ userProfile.learningPreference }}</el-tag>
                </div>
              </div>
              <div class="profile-item">
                <div class="item-label">学习进度</div>
                <div class="item-value progress-value">
                  <el-progress :percentage="userProfile.progress" :color="progressColor" />
                </div>
              </div>
            </div>

            <div class="interests-section">
              <div class="section-label">兴趣领域</div>
              <div class="interest-tags">
                <el-tag
                  v-for="(interest, index) in userProfile.interests"
                  :key="index"
                  size="large"
                  effect="plain"
                  class="interest-tag"
                  :class="{ active: interest.active }"
                  @click="toggleInterest(interest)"
                >
                  <span class="tag-icon">{{ interest.icon }}</span>
                  {{ interest.name }}
                  <span class="match-rate">{{ interest.matchRate }}%</span>
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="path-card">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span class="icon">🛤️</span>
                <span>学习路径</span>
                <el-tag type="warning" size="small" effect="plain">AI实时调整中</el-tag>
              </div>
              <div class="header-actions">
                <el-button type="info" size="small" text @click="showPathAnalysis">
                  <el-icon><DataAnalysis /></el-icon>
                  路径分析
                </el-button>
              </div>
            </div>
          </template>
          <div class="path-content">
            <div class="path-timeline">
              <el-timeline>
                <el-timeline-item
                  v-for="(stage, index) in learningPath"
                  :key="stage.id"
                  :type="getStageType(stage.status)"
                  :hollow="stage.status !== 'current'"
                  placement="top"
                >
                  <div class="timeline-stage" :class="stage.status">
                    <div class="stage-header">
                      <h3>{{ stage.title }}</h3>
                      <el-tag :type="getStatusTagType(stage.status)" size="small">
                        {{ getStatusText(stage.status) }}
                      </el-tag>
                    </div>
                    <p class="stage-desc">{{ stage.description }}</p>
                    <div class="stage-courses">
                      <div
                        v-for="course in stage.courses"
                        :key="course.id"
                        class="course-chip"
                        :class="course.status"
                        @click="viewCourse(course)"
                      >
                        <span class="course-icon">{{ getCourseIcon(course.status) }}</span>
                        <span class="course-title">{{ course.title }}</span>
                        <span class="course-duration">{{ course.duration }}分钟</span>
                      </div>
                    </div>
                    <div class="stage-progress" v-if="stage.status === 'current'">
                      <el-progress :percentage="stage.progress" :color="progressColor" :show-text="true" />
                      <span class="progress-tip">已完成 {{ stage.completedCount }}/{{ stage.totalCount }} 个课程</span>
                    </div>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="recommend-card">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span class="icon">✨</span>
                <span>智能推荐</span>
              </div>
              <el-button type="primary" size="small" @click="refreshRecommendations">
                <el-icon><Refresh /></el-icon>
                刷新推荐
              </el-button>
            </div>
          </template>
          <div class="recommend-content">
            <div class="recommend-grid">
              <div
                v-for="(item, index) in recommendations"
                :key="item.id"
                class="recommend-item"
                :class="{ highlighted: index < 3 }"
                @click="viewCourse(item)"
              >
                <div class="item-rank" v-if="index < 3">
                  {{ index + 1 }}
                </div>
                <div class="item-content">
                  <div class="content-header">
                    <h4>{{ item.title }}</h4>
                    <el-tag size="small" :type="getRecommendType(item.matchRate)">
                      匹配度 {{ item.matchRate }}%
                    </el-tag>
                  </div>
                  <p class="content-desc">{{ item.description }}</p>
                  <div class="content-meta">
                    <span class="meta-item">
                      <el-icon><VideoPlay /></el-icon>
                      {{ item.type }}
                    </span>
                    <span class="meta-item">
                      <el-icon><Clock /></el-icon>
                      {{ item.duration }}分钟
                    </span>
                    <span class="meta-item reason">
                      推荐理由：{{ item.reason }}
                    </span>
                  </div>
                </div>
                <div class="item-action">
                  <el-button type="primary" circle>
                    <el-icon><ArrowRight /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <div class="side-area">
        <el-card shadow="hover" class="stats-card">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span class="icon">📊</span>
                <span>学习统计</span>
              </div>
            </div>
          </template>
          <div class="stats-content">
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-icon">⏱️</div>
                <div class="stat-info">
                  <span class="stat-value">{{ stats.totalLearningTime }}</span>
                  <span class="stat-label">总学习时长(分钟)</span>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">✅</div>
                <div class="stat-info">
                  <span class="stat-value">{{ stats.completedCourses }}</span>
                  <span class="stat-label">已完成课程</span>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">🎯</div>
                <div class="stat-info">
                  <span class="stat-value">{{ stats.assessmentScore }}</span>
                  <span class="stat-label">平均评估分数</span>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon">🔥</div>
                <div class="stat-info">
                  <span class="stat-value">{{ stats.streakDays }}</span>
                  <span class="stat-label">连续学习天数</span>
                </div>
              </div>
            </div>

            <div class="stats-chart">
              <h4>学习趋势</h4>
              <div class="mini-chart">
                <div
                  v-for="(day, index) in weeklyProgress"
                  :key="index"
                  class="chart-bar"
                  :style="{ height: day + '%' }"
                >
                  <span class="bar-label">{{ getDayLabel(index) }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="weakness-card">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span class="icon">⚠️</span>
                <span>薄弱环节</span>
              </div>
              <el-button type="danger" size="small" text @click="getWeaknessAnalysis">
                <el-icon><DataAnalysis /></el-icon>
                分析
              </el-button>
            </div>
          </template>
          <div class="weakness-content">
            <div
              v-for="weakness in weaknesses"
              :key="weakness.id"
              class="weakness-item"
              @click="startRemedial(weakness)"
            >
              <div class="weakness-info">
                <span class="weakness-icon">{{ weakness.icon }}</span>
                <div class="weakness-text">
                  <h5>{{ weakness.name }}</h5>
                  <p>正确率 {{ weakness.correctRate }}%</p>
                </div>
              </div>
              <el-button type="danger" size="small">
                去学习
              </el-button>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="adjust-log-card">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span class="icon">🔄</span>
                <span>调整日志</span>
              </div>
            </div>
          </template>
          <div class="log-content">
            <el-timeline size="small">
              <el-timeline-item
                v-for="log in adjustmentLogs"
                :key="log.id"
                :type="log.type"
                : hollow="true"
              >
                <div class="log-item">
                  <p class="log-text">{{ log.message }}</p>
                  <span class="log-time">{{ log.time }}</span>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog v-model="courseVisible" :title="currentCourse?.title" width="800px" class="course-dialog">
      <div class="course-detail" v-if="currentCourse">
        <div class="course-header">
          <div class="header-badges">
            <el-tag type="primary">{{ currentCourse.type }}</el-tag>
            <el-tag type="info">{{ currentCourse.duration }}分钟</el-tag>
            <el-tag :type="getDifficultyType(currentCourse.difficulty)">
              {{ currentCourse.difficulty }}
            </el-tag>
          </div>
          <h2>{{ currentCourse.title }}</h2>
          <p class="course-desc">{{ currentCourse.description }}</p>
        </div>

        <div class="course-body">
          <el-tabs v-model="activeTab" class="course-tabs">
            <el-tab-pane label="课程内容" name="content">
              <div class="tab-content">
                <h4>学习目标</h4>
                <ul class="objective-list">
                  <li v-for="(obj, index) in currentCourse.objectives" :key="index">
                    {{ obj }}
                  </li>
                </ul>

                <h4>课程内容</h4>
                <div class="content-list">
                  <div
                    v-for="(section, index) in currentCourse.sections"
                    :key="index"
                    class="section-item"
                  >
                    <div class="section-header">
                      <span class="section-number">{{ index + 1 }}</span>
                      <span class="section-title">{{ section.title }}</span>
                      <el-tag size="small" type="info">{{ section.duration }}分钟</el-tag>
                    </div>
                    <p class="section-desc">{{ section.description }}</p>
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="课后评估" name="assessment">
              <div class="tab-content">
                <h4>课后测试</h4>
                <p>完成课程后，您可以参加课后测试来检验学习效果。</p>
                <el-button type="primary" size="large" @click="startAssessment">
                  开始测试
                </el-button>
              </div>
            </el-tab-pane>
            <el-tab-pane label="相关推荐" name="related">
              <div class="tab-content">
                <h4>相关课程</h4>
                <div class="related-list">
                  <div
                    v-for="related in currentCourse.relatedCourses"
                    :key="related.id"
                    class="related-item"
                    @click="viewCourse(related)"
                  >
                    <span class="related-icon">{{ related.icon }}</span>
                    <div class="related-info">
                      <h5>{{ related.title }}</h5>
                      <span>{{ related.duration }}分钟</span>
                    </div>
                    <el-icon><ArrowRight /></el-icon>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="courseVisible = false">关闭</el-button>
          <el-button type="primary" @click="startLearning">
            <el-icon><VideoPlay /></el-icon>
            开始学习
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, DataAnalysis, Clock, VideoPlay, ArrowRight } from '@element-plus/icons-vue'

interface Interest {
  name: string
  icon: string
  matchRate: number
  active: boolean
}

interface Course {
  id: number
  title: string
  type: string
  duration: number
  difficulty?: string
  description: string
  status?: string
  objectives?: string[]
  sections?: { title: string; description: string; duration: number }[]
  relatedCourses?: any[]
}

interface LearningStage {
  id: number
  title: string
  description: string
  status: string
  progress: number
  completedCount: number
  totalCount: number
  courses: Course[]
}

interface Recommendation {
  id: number
  title: string
  description: string
  type: string
  duration: number
  matchRate: number
  reason: string
}

interface Weakness {
  id: number
  name: string
  icon: string
  correctRate: number
}

interface AdjustmentLog {
  id: number
  message: string
  time: string
  type: string
}

const userProfile = ref({
  riskLevel: '低风险',
  learningStage: '进阶学习',
  learningPreference: '视频+实践',
  progress: 45,
  interests: [
    { name: '电信诈骗', icon: '📞', matchRate: 95, active: true },
    { name: '网络诈骗', icon: '🌐', matchRate: 90, active: true },
    { name: '校园贷', icon: '🎓', matchRate: 85, active: false },
    { name: '杀猪盘', icon: '💔', matchRate: 80, active: false },
    { name: '刷单诈骗', icon: '💼', matchRate: 75, active: false }
  ]
})

const learningPath = ref<LearningStage[]>([
  {
    id: 1,
    title: '基础认知',
    description: '了解常见诈骗类型和基本防范措施',
    status: 'completed',
    progress: 100,
    completedCount: 5,
    totalCount: 5,
    courses: [
      { id: 101, title: '诈骗类型介绍', type: '视频', duration: 20, status: 'completed', description: '介绍常见的诈骗类型' },
      { id: 102, title: '防范基本常识', type: '图文', duration: 15, status: 'completed', description: '学习基本的防范知识' },
      { id: 103, title: '案例分析入门', type: '案例', duration: 25, status: 'completed', description: '通过案例学习防范' }
    ]
  },
  {
    id: 2,
    title: '进阶学习',
    description: '深入学习各类诈骗的识别和防范技巧',
    status: 'current',
    progress: 60,
    completedCount: 3,
    totalCount: 5,
    courses: [
      { id: 201, title: '电信诈骗防范', type: '视频', duration: 30, status: 'completed', description: '深入了解电信诈骗' },
      { id: 202, title: '网络诈骗防范', type: '视频', duration: 35, status: 'completed', description: '识别网络诈骗手法' },
      { id: 203, title: '校园贷诈骗防范', type: '视频', duration: 25, status: 'in-progress', description: '防范校园贷陷阱' },
      { id: 204, title: '兼职诈骗防范', type: '图文', duration: 20, status: 'pending', description: '识别兼职诈骗' },
      { id: 205, title: '游戏充值诈骗', type: '视频', duration: 25, status: 'pending', description: '游戏相关诈骗防范' }
    ]
  },
  {
    id: 3,
    title: '高级应用',
    description: '掌握高级防范技巧和实战应对能力',
    status: 'pending',
    progress: 0,
    completedCount: 0,
    totalCount: 4,
    courses: [
      { id: 301, title: '情景实战演练', type: '实践', duration: 45, status: 'pending', description: '模拟真实场景演练' },
      { id: 302, title: '风险评估能力', type: '测试', duration: 30, status: 'pending', description: '学习风险评估' },
      { id: 303, title: '防诈宣传技巧', type: '实践', duration: 40, status: 'pending', description: '成为防诈宣传员' },
      { id: 304, title: '综合能力测试', type: '测试', duration: 60, status: 'pending', description: '综合能力评估' }
    ]
  }
])

const recommendations = ref<Recommendation[]>([
  {
    id: 401,
    title: '新型AI诈骗识别技巧',
    description: '学习如何识别利用AI技术实施的新型诈骗手段',
    type: '视频课程',
    duration: 35,
    matchRate: 96,
    reason: '基于您的学习进度和兴趣推荐'
  },
  {
    id: 402,
    title: '杀猪盘诈骗深度解析',
    description: '深入分析杀猪盘诈骗的套路和防范方法',
    type: '案例分析',
    duration: 40,
    matchRate: 92,
    reason: '您对网络诈骗感兴趣'
  },
  {
    id: 403,
    title: '电信诈骗话术破解',
    description: '通过真实通话案例学习如何应对诈骗话术',
    type: '互动课程',
    duration: 30,
    matchRate: 88,
    reason: '完善您在电信诈骗方面的知识'
  },
  {
    id: 404,
    title: '防诈意识自测',
    description: '测试您的防诈意识水平，发现知识盲区',
    type: '互动测试',
    duration: 15,
    matchRate: 85,
    reason: '帮助您发现学习薄弱点'
  },
  {
    id: 405,
    title: '校园贷危害警示',
    description: '了解校园贷的危害和正规借款渠道',
    type: '视频课程',
    duration: 25,
    matchRate: 82,
    reason: '您可能面临的校园贷风险'
  }
])

const stats = ref({
  totalLearningTime: 285,
  completedCourses: 8,
  assessmentScore: 87,
  streakDays: 12
})

const weeklyProgress = ref([65, 80, 45, 90, 70, 85, 55])

const weaknesses = ref<Weakness[]>([
  { id: 1, name: '游戏充值诈骗', icon: '🎮', correctRate: 55 },
  { id: 2, name: '杀猪盘识别', icon: '💔', correctRate: 68 },
  { id: 3, name: '钓鱼网站识别', icon: '🎣', correctRate: 72 }
])

const adjustmentLogs = ref<AdjustmentLog[]>([
  { id: 1, message: '根据您的学习情况，自动调整了学习路径', time: '刚刚', type: 'primary' },
  { id: 2, message: '检测到您在"游戏充值诈骗"方面正确率较低，已添加专项课程', time: '10分钟前', type: 'warning' },
  { id: 3, message: '恭喜您完成"电信诈骗防范"课程，已解锁下一阶段内容', time: '1小时前', type: 'success' }
])

const courseVisible = ref(false)
const currentCourse = ref<Course | null>(null)
const activeTab = ref('content')

const progressColor = computed(() => {
  const progress = userProfile.value.progress
  if (progress < 30) return '#f56c6c'
  if (progress < 70) return '#e6a23c'
  return '#67c23a'
})

function getRiskTagType(level: string): string {
  const typeMap: Record<string, string> = {
    '低风险': 'success',
    '中风险': 'warning',
    '高风险': 'danger'
  }
  return typeMap[level] || 'info'
}

function getStageType(status: string): string {
  const typeMap: Record<string, string> = {
    'completed': 'success',
    'current': 'primary',
    'pending': 'info'
  }
  return typeMap[status] || 'info'
}

function getStatusTagType(status: string): string {
  const typeMap: Record<string, string> = {
    'completed': 'success',
    'current': 'warning',
    'pending': 'info'
  }
  return typeMap[status] || 'info'
}

function getStatusText(status: string): string {
  const textMap: Record<string, string> = {
    'completed': '已完成',
    'current': '进行中',
    'pending': '未开始'
  }
  return textMap[status] || status
}

function getCourseIcon(status: string): string {
  const iconMap: Record<string, string> = {
    'completed': '✅',
    'in-progress': '⏳',
    'pending': '📖'
  }
  return iconMap[status] || '📖'
}

function getRecommendType(rate: number): string {
  if (rate >= 90) return 'success'
  if (rate >= 80) return 'primary'
  return 'warning'
}

function getDifficultyType(difficulty?: string): string {
  const typeMap: Record<string, string> = {
    '简单': 'success',
    '中等': 'warning',
    '困难': 'danger'
  }
  return typeMap[difficulty || ''] || 'info'
}

function getDayLabel(index: number): string {
  const days = ['一', '二', '三', '四', '五', '六', '日']
  return days[index]
}

function toggleInterest(interest: Interest) {
  interest.active = !interest.active
  ElMessage.success(`已${interest.active ? '添加' : '取消'}${interest.name}到学习计划`)
  refreshRecommendations()
}

function refreshProfile() {
  ElMessage.success('画像已更新')
  adjustmentLogs.value.unshift({
    id: Date.now(),
    message: '学习画像已刷新，基于最新学习数据重新分析',
    time: '刚刚',
    type: 'primary'
  })
}

function refreshRecommendations() {
  ElMessage.success('推荐已刷新')
}

function showPathAnalysis() {
  ElMessage.info('路径分析功能开发中')
}

function viewCourse(course: any) {
  currentCourse.value = {
    ...course,
    objectives: [
      '了解' + course.title + '的基本概念',
      '掌握识别' + course.title + '的方法',
      '学会应对' + course.title + '的技巧'
    ],
    sections: [
      { title: '基础概念', description: '学习相关基础知识', duration: 10 },
      { title: '案例分析', description: '通过真实案例深入理解', duration: 15 },
      { title: '实战技巧', description: '学习应对方法和技巧', duration: 10 }
    ],
    relatedCourses: [
      { id: 1, title: '相关课程1', icon: '📚', duration: 20 },
      { id: 2, title: '相关课程2', icon: '📚', duration: 25 }
    ]
  }
  courseVisible.value = true
}

function startLearning() {
  ElMessage.success('开始学习：' + currentCourse.value?.title)
  courseVisible.value = false
}

function startAssessment() {
  ElMessage.info('课后测试功能开发中')
}

function getWeaknessAnalysis() {
  ElMessage.success('正在分析学习薄弱点...')
  setTimeout(() => {
    adjustmentLogs.value.unshift({
      id: Date.now(),
      message: 'AI分析完成：发现3个薄弱环节，已自动调整学习计划',
      time: '刚刚',
      type: 'warning'
    })
  }, 1000)
}

function startRemedial(weakness: Weakness) {
  ElMessage.success(`开始学习：${weakness.name}`)
}

onMounted(() => {
  ElMessage.success({
    message: '自适应学习路径已根据您的学习情况优化完成',
    duration: 3000
  })
})
</script>

<style scoped>
.adaptive-learning {
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f0f9eb 0%, #e8f5e9 100%);
  min-height: calc(100vh - 60px);
}

.header {
  margin-bottom: 24px;
}

.header-content {
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
  border-radius: 16px;
  padding: 32px;
  color: white;
  box-shadow: 0 8px 32px rgba(103, 194, 58, 0.3);
}

.header-text h1 {
  font-size: 32px;
  margin: 0 0 12px 0;
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
  backdrop-filter: blur(10px);
}

.competitor {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  margin-left: 8px;
}

.header-features {
  display: flex;
  gap: 32px;
  margin-top: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.feature-icon {
  font-size: 24px;
}

.feature-text {
  font-size: 14px;
  opacity: 0.9;
}

.learning-content {
  display: flex;
  gap: 24px;
}

.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.side-area {
  width: 360px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}

.header-title .icon {
  font-size: 20px;
}

.profile-content {
  padding: 12px 0;
}

.profile-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.profile-item {
  text-align: center;
}

.item-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.item-value {
  display: flex;
  justify-content: center;
}

.progress-value {
  width: 100%;
}

.interests-section {
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.section-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.interest-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.interest-tag {
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.interest-tag:hover {
  transform: translateY(-2px);
}

.interest-tag.active {
  background: linear-gradient(135deg, #67c23a, #95d475);
  color: white;
  border-color: #67c23a;
}

.tag-icon {
  font-size: 16px;
}

.match-rate {
  background: rgba(255, 255, 255, 0.3);
  padding: 2px 6px;
  border-radius: 8px;
  font-size: 11px;
}

.path-card {
  background: white;
}

.path-content {
  padding: 12px 0;
}

.timeline-stage {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 16px;
  transition: all 0.3s ease;
}

.timeline-stage:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.timeline-stage.current {
  background: linear-gradient(135deg, #f0f9eb, #e8f5e9);
  border: 2px solid #67c23a;
}

.timeline-stage.completed {
  opacity: 0.8;
}

.stage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.stage-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.stage-desc {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #909399;
}

.stage-courses {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.course-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: white;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e4e7ed;
}

.course-chip:hover {
  border-color: #67c23a;
  background: #f0f9eb;
}

.course-chip.completed {
  background: #e1f3d8;
  border-color: #67c23a;
}

.course-chip.in-progress {
  background: #fdf6ec;
  border-color: #e6a23c;
}

.course-icon {
  font-size: 14px;
}

.course-duration {
  color: #909399;
  font-size: 12px;
}

.stage-progress {
  margin-top: 12px;
}

.progress-tip {
  display: block;
  text-align: center;
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.recommend-card {
  background: white;
}

.recommend-content {
  padding: 12px 0;
}

.recommend-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommend-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.recommend-item:hover {
  background: #f0f9eb;
  border-color: #67c23a;
  transform: translateX(4px);
}

.recommend-item.highlighted {
  background: linear-gradient(135deg, #f0f9eb, #e8f5e9);
}

.item-rank {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #67c23a, #95d475);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.item-content {
  flex: 1;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.content-header h4 {
  margin: 0;
  font-size: 15px;
  color: #333;
}

.content-desc {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #909399;
}

.content-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #606266;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-item.reason {
  color: #67c23a;
  flex: 1;
}

.item-action {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.recommend-item:hover .item-action {
  opacity: 1;
}

.stats-card {
  background: white;
}

.stats-content {
  padding: 12px 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 10px;
}

.stat-icon {
  font-size: 28px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #67c23a;
}

.stat-label {
  font-size: 11px;
  color: #909399;
}

.stats-chart {
  border-top: 1px solid #ebeef5;
  padding-top: 16px;
}

.stats-chart h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
}

.mini-chart {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 60px;
  padding: 0 8px;
}

.chart-bar {
  width: 30px;
  background: linear-gradient(180deg, #67c23a, #95d475);
  border-radius: 4px 4px 0 0;
  position: relative;
  transition: height 0.5s ease;
  min-height: 10px;
}

.bar-label {
  position: absolute;
  bottom: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
  color: #909399;
}

.weakness-card {
  background: white;
}

.weakness-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.weakness-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fff9f9;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #fde2e2;
}

.weakness-item:hover {
  background: #fef0f0;
  border-color: #f56c6c;
}

.weakness-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.weakness-icon {
  font-size: 24px;
}

.weakness-text h5 {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #333;
}

.weakness-text p {
  margin: 0;
  font-size: 12px;
  color: #f56c6c;
}

.adjust-log-card {
  background: white;
}

.log-content {
  padding: 8px 0;
}

.log-item {
  line-height: 1.4;
}

.log-text {
  margin: 0 0 4px 0;
  font-size: 13px;
  color: #606266;
}

.log-time {
  font-size: 11px;
  color: #909399;
}

.course-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.course-detail {
  padding: 24px;
}

.course-header {
  margin-bottom: 24px;
}

.header-badges {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.course-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.course-desc {
  margin: 0;
  color: #606266;
}

.course-tabs {
  padding: 0 12px;
}

.tab-content {
  padding: 16px 0;
}

.tab-content h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
}

.objective-list {
  margin: 0 0 24px 0;
  padding-left: 20px;
}

.objective-list li {
  line-height: 1.8;
  color: #606266;
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-item {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 10px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.section-number {
  width: 24px;
  height: 24px;
  background: #67c23a;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

.section-title {
  flex: 1;
  font-weight: 500;
  color: #333;
}

.section-desc {
  margin: 0;
  padding-left: 36px;
  font-size: 13px;
  color: #909399;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.related-item:hover {
  background: #f0f9eb;
}

.related-icon {
  font-size: 24px;
}

.related-info {
  flex: 1;
}

.related-info h5 {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #333;
}

.related-info span {
  font-size: 12px;
  color: #909399;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 1200px) {
  .learning-content {
    flex-direction: column;
  }

  .side-area {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .side-area > * {
    flex: 1;
    min-width: 300px;
  }
}

@media (max-width: 768px) {
  .adaptive-learning {
    padding: 16px;
  }

  .header-content {
    padding: 20px;
  }

  .header-text h1 {
    font-size: 24px;
  }

  .header-features {
    flex-wrap: wrap;
    gap: 16px;
  }

  .profile-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
