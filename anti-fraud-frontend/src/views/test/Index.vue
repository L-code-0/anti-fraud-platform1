<template>
  <div class="test-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
      </div>
      <div class="header-content">
        <h1>在线测试中心</h1>
        <p>检验学习成果，巩固反诈知识</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 统计概览 -->
      <div class="stats-overview">
        <div class="stat-card main-stat">
          <div class="stat-icon">
            <el-icon><Trophy /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">85.5</span>
            <span class="stat-label">我的平均分</span>
          </div>
          <div class="stat-badge">
            <span class="badge-text">优秀</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon warning">
            <el-icon><EditPen /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">23</span>
            <span class="stat-label">已完成测试</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon success">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">18</span>
            <span class="stat-label">通过考试</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon danger">
            <el-icon><Collection /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">156</span>
            <span class="stat-label">错题数</span>
          </div>
        </div>
      </div>

      <!-- 测试类型选择 -->
      <div class="test-types">
        <div class="types-header">
          <h2>选择测试类型</h2>
          <p>根据您的学习进度选择合适的测试</p>
        </div>

        <div class="types-grid">
          <div
            class="type-card"
            v-for="type in testTypes"
            :key="type.id"
            @click="selectTestType(type)"
          >
            <div class="type-icon" :style="{ background: type.gradient }">
              <el-icon><component :is="type.icon" /></el-icon>
            </div>
            <div class="type-info">
              <h3>{{ type.title }}</h3>
              <p>{{ type.desc }}</p>
              <div class="type-meta">
                <span><el-icon><Clock /></el-icon> {{ type.duration }}分钟</span>
                <span><el-icon><Document /></el-icon> {{ type.questions }}题</span>
              </div>
            </div>
            <div class="type-action">
              <el-button type="primary" circle>
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 推荐试卷 -->
      <div class="recommended-papers">
        <div class="section-header">
          <h2>推荐试卷</h2>
          <el-button text @click="router.push('/test/papers')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div class="papers-grid">
          <div class="paper-card" v-for="paper in recommendedPapers" :key="paper.id">
            <div class="paper-header">
              <span class="paper-difficulty" :class="'level-' + paper.level">
                {{ paper.levelName }}
              </span>
              <span class="paper-count">{{ paper.questionCount }}题</span>
            </div>
            <h3>{{ paper.title }}</h3>
            <p>{{ paper.desc }}</p>
            <div class="paper-footer">
              <div class="paper-info">
                <span><el-icon><Clock /></el-icon> {{ paper.duration }}分钟</span>
                <span><el-icon><User /></el-icon> {{ paper.taken }}人做过</span>
              </div>
              <el-button type="primary" size="small" @click="startTest(paper)">
                开始答题
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 错题本入口 -->
      <div class="wrong-book-section">
        <div class="wrong-book-card">
          <div class="wrong-book-content">
            <div class="wrong-book-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="wrong-book-info">
              <h3>错题本</h3>
              <p>系统记录您的错题，帮助您针对性地复习提高</p>
            </div>
          </div>
          <el-button type="primary" size="large" @click="router.push('/wrongbook')">
            查看错题
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 开始测试对话框 -->
    <el-dialog
      v-model="showStartDialog"
      title="开始测试"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="start-dialog-content" v-if="selectedTest">
        <div class="test-info-card">
          <h4>{{ selectedTest.title }}</h4>
          <div class="info-grid">
            <div class="info-item">
              <el-icon><Document /></el-icon>
              <span>题目数量</span>
              <strong>{{ selectedTest.questions }}题</strong>
            </div>
            <div class="info-item">
              <el-icon><Clock /></el-icon>
              <span>测试时长</span>
              <strong>{{ selectedTest.duration }}分钟</strong>
            </div>
            <div class="info-item">
              <el-icon><Trophy /></el-icon>
              <span>及格分数</span>
              <strong>{{ selectedTest.passScore }}分</strong>
            </div>
          </div>
          <div class="test-rules">
            <h5>测试规则</h5>
            <ul>
              <li>请在规定时间内完成所有题目</li>
              <li>每题只能选择一个正确答案</li>
              <li>提交后无法修改答案</li>
              <li>达到及格分数视为通过</li>
            </ul>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showStartDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmStartTest">
          <el-icon><VideoPlay /></el-icon>
          开始测试
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Trophy, EditPen, CircleCheck, Collection, Clock, Document,
  ArrowRight, User, Warning, VideoPlay, Reading, School, Medal
} from '@element-plus/icons-vue'

const router = useRouter()
const showStartDialog = ref(false)
const selectedTest = ref<any>(null)

const testTypes = [
  {
    id: 1,
    icon: 'Reading',
    title: '章节练习',
    desc: '按知识点练习，巩固基础',
    duration: 30,
    questions: 30,
    gradient: 'var(--gradient-primary)'
  },
  {
    id: 2,
    icon: 'School',
    title: '模拟考试',
    desc: '全真模拟，熟悉考试流程',
    duration: 60,
    questions: 50,
    gradient: 'var(--gradient-success)'
  },
  {
    id: 3,
    icon: 'Medal',
    title: '能力认证',
    desc: '通过认证考试获得证书',
    duration: 90,
    questions: 80,
    gradient: 'var(--gradient-warning)'
  },
  {
    id: 4,
    icon: 'Collection',
    title: '专项训练',
    desc: '针对薄弱环节强化训练',
    duration: 20,
    questions: 20,
    gradient: 'var(--gradient-danger)'
  }
]

const recommendedPapers = ref([
  {
    id: 1,
    title: '电信诈骗防范知识测试',
    desc: '测试您对电信诈骗的认识程度',
    level: 1,
    levelName: '入门',
    questionCount: 20,
    duration: 30,
    taken: 1234
  },
  {
    id: 2,
    title: '网络购物诈骗识别',
    desc: '检验网购防骗能力',
    level: 2,
    levelName: '基础',
    questionCount: 30,
    duration: 45,
    taken: 892
  },
  {
    id: 3,
    title: '杀猪盘诈骗深度测试',
    desc: '深入了解杀猪盘诈骗手法',
    level: 3,
    levelName: '进阶',
    questionCount: 25,
    duration: 40,
    taken: 567
  },
  {
    id: 4,
    title: '综合反诈能力测试',
    desc: '全面测试反诈知识掌握程度',
    level: 4,
    levelName: '高级',
    questionCount: 50,
    duration: 60,
    taken: 345
  }
])

const selectTestType = (type: any) => {
  selectedTest.value = type
  showStartDialog.value = true
}

const startTest = (paper: any) => {
  selectedTest.value = {
    title: paper.title,
    questions: paper.questionCount,
    duration: paper.duration,
    passScore: 60
  }
  showStartDialog.value = true
}

const confirmStartTest = () => {
  showStartDialog.value = false
  ElMessage.success('即将开始测试，祝您取得好成绩！')
  router.push('/test/paper')
}
</script>

<style scoped>
.test-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

/* 页面头部 */
.page-header {
  position: relative;
  padding: var(--spacing-16) var(--spacing-6);
  overflow: hidden;
}

.header-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.header-bg .bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #059669 0%, #10b981 50%, #34d399 100%);
}

.header-content {
  position: relative;
  z-index: 1;
  max-width: 1280px;
  margin: 0 auto;
  text-align: center;
  color: white;
}

.header-content h1 {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-4);
}

.header-content p {
  font-size: var(--font-size-lg);
  opacity: 0.9;
}

/* 页面容器 */
.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6) var(--spacing-12);
}

/* 统计概览 */
.stats-overview {
  display: grid;
  grid-template-columns: 2fr repeat(3, 1fr);
  gap: var(--spacing-4);
  margin-top: calc(-1 * var(--spacing-10));
  margin-bottom: var(--spacing-8);
}

.stat-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-5);
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.stat-card.main-stat {
  padding: var(--spacing-6);
  background: var(--gradient-primary);
  color: white;
  position: relative;
  overflow: hidden;
}

.stat-card.main-stat::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(255,255,255,0.2) 0%, transparent 70%);
}

.main-stat .stat-info {
  flex-direction: column;
  align-items: flex-start;
}

.main-stat .stat-value {
  font-size: var(--font-size-4xl);
  color: white;
}

.main-stat .stat-label {
  color: rgba(255, 255, 255, 0.8);
}

.stat-badge {
  position: absolute;
  top: var(--spacing-4);
  right: var(--spacing-4);
  padding: var(--spacing-1) var(--spacing-3);
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-full);
}

.badge-text {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: white;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  background: var(--gradient-primary);
}

.stat-icon.warning {
  background: var(--gradient-warning);
}

.stat-icon.success {
  background: var(--gradient-success);
}

.stat-icon.danger {
  background: var(--gradient-danger);
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

/* 测试类型 */
.test-types {
  margin-bottom: var(--spacing-10);
}

.types-header {
  margin-bottom: var(--spacing-6);
}

.types-header h2 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.types-header p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
}

.types-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-4);
}

.type-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-5);
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.type-card:hover {
  border-color: var(--primary-color);
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.type-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  flex-shrink: 0;
}

.type-info {
  flex: 1;
}

.type-info h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.type-info p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-2);
}

.type-meta {
  display: flex;
  gap: var(--spacing-4);
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.type-meta span {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
}

.type-action {
  flex-shrink: 0;
}

/* 推荐试卷 */
.recommended-papers {
  margin-bottom: var(--spacing-10);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
}

.section-header h2 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.papers-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-4);
}

.paper-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-5);
  transition: all var(--transition-normal);
}

.paper-card:hover {
  border-color: var(--primary-color);
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.paper-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-3);
}

.paper-difficulty {
  padding: var(--spacing-1) var(--spacing-2);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-sm);
  color: white;
}

.paper-difficulty.level-1 { background: var(--success-color); }
.paper-difficulty.level-2 { background: var(--info-color); }
.paper-difficulty.level-3 { background: var(--warning-color); }
.paper-difficulty.level-4 { background: var(--danger-color); }

.paper-count {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.paper-card h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.paper-card p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-4);
}

.paper-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.paper-info {
  display: flex;
  gap: var(--spacing-4);
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.paper-info span {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
}

/* 错题本 */
.wrong-book-section {
  margin-bottom: var(--spacing-8);
}

.wrong-book-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-6);
  background: var(--gradient-danger);
  border-radius: var(--radius-xl);
  color: white;
}

.wrong-book-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
}

.wrong-book-icon {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.wrong-book-info h3 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-1);
}

.wrong-book-info p {
  font-size: var(--font-size-sm);
  opacity: 0.9;
}

/* 开始测试对话框 */
.start-dialog-content {
  padding: var(--spacing-2);
}

.test-info-card {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: var(--spacing-5);
}

.test-info-card h4 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-4);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-5);
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-1);
  padding: var(--spacing-3);
  background: var(--bg-primary);
  border-radius: var(--radius-md);
}

.info-item .el-icon {
  font-size: 20px;
  color: var(--primary-color);
}

.info-item span {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.info-item strong {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.test-rules {
  background: var(--bg-primary);
  border-radius: var(--radius-md);
  padding: var(--spacing-4);
}

.test-rules h5 {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.test-rules ul {
  margin: 0;
  padding-left: var(--spacing-5);
}

.test-rules li {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-1);
}

/* 响应式 */
@media (max-width: 1024px) {
  .stats-overview {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-card.main-stat {
    grid-column: span 2;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: var(--spacing-10) var(--spacing-4);
  }

  .header-content h1 {
    font-size: var(--font-size-2xl);
  }

  .page-container {
    padding: 0 var(--spacing-4) var(--spacing-8);
  }

  .stats-overview {
    grid-template-columns: 1fr;
    margin-top: 0;
  }

  .stat-card.main-stat {
    grid-column: span 1;
  }

  .types-grid,
  .papers-grid {
    grid-template-columns: 1fr;
  }

  .wrong-book-card {
    flex-direction: column;
    gap: var(--spacing-4);
    text-align: center;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
