<template>
  <div class="drill-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
      </div>
      <div class="header-content">
        <h1>反诈演练中心</h1>
        <p>通过模拟场景，提升反诈意识和应对能力</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 演练场景选择 -->
      <div class="scenario-section">
        <div class="section-header">
          <h2>选择演练场景</h2>
          <p>根据不同的诈骗类型进行针对性演练</p>
        </div>

        <div class="scenario-grid">
          <div
            class="scenario-card"
            v-for="scenario in scenarios"
            :key="scenario.id"
            @click="selectScenario(scenario)"
          >
            <div class="scenario-icon" :style="{ background: scenario.gradient }">
              <el-icon><component :is="scenario.icon" /></el-icon>
            </div>
            <div class="scenario-info">
              <h3>{{ scenario.title }}</h3>
              <p>{{ scenario.desc }}</p>
              <div class="scenario-meta">
                <span class="difficulty" :class="'level-' + scenario.difficulty">
                  {{ scenario.difficultyName }}
                </span>
                <span><el-icon><Timer /></el-icon> {{ scenario.duration }}分钟</span>
              </div>
            </div>
            <div class="scenario-action">
              <el-button type="primary" circle>
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 3D场景预览 -->
      <div class="preview-section">
        <div class="section-header">
          <h2>3D场景预览</h2>
          <p>体验沉浸式反诈演练</p>
        </div>

        <div class="preview-container">
          <div id="3d-container" class="three-container">
            <!-- 3D场景将在这里渲染 -->
            <div class="loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>加载3D场景中...</span>
            </div>
          </div>
          <div class="preview-info">
            <h3>3D演练场景</h3>
            <p>通过3D场景模拟真实诈骗情境，让您身临其境地体验诈骗过程，学习如何识别和应对各类诈骗手法。</p>
            <div class="preview-features">
              <div class="feature-item">
                <el-icon><VideoCamera /></el-icon>
                <span>沉浸式体验</span>
              </div>
              <div class="feature-item">
                <el-icon><ChatLineSquare /></el-icon>
                <span>实时互动</span>
              </div>
              <div class="feature-item">
                <el-icon><DataLine /></el-icon>
                <span>实时反馈</span>
              </div>
              <div class="feature-item">
                <el-icon><Medal /></el-icon>
                <span>技能评估</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 演练统计 -->
      <div class="stats-section">
        <div class="section-header">
          <h2>演练统计</h2>
          <p>查看您的演练历史和进步</p>
        </div>

        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">12</span>
              <span class="stat-label">已完成演练</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon success">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">85%</span>
              <span class="stat-label">成功率</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon warning">
              <el-icon><Medal /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">92</span>
              <span class="stat-label">平均得分</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon danger">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">25</span>
              <span class="stat-label">总耗时(分钟)</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 开始演练对话框 -->
    <el-dialog
      v-model="showStartDialog"
      title="开始演练"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="start-dialog-content" v-if="selectedScenario">
        <div class="scenario-info-card">
          <h4>{{ selectedScenario.title }}</h4>
          <div class="info-grid">
            <div class="info-item">
              <el-icon><Timer /></el-icon>
              <span>演练时长</span>
              <strong>{{ selectedScenario.duration }}分钟</strong>
            </div>
            <div class="info-item">
              <el-icon><Warning /></el-icon>
              <span>难度等级</span>
              <strong>{{ selectedScenario.difficultyName }}</strong>
            </div>
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>参与人数</span>
              <strong>{{ selectedScenario.participants }}人</strong>
            </div>
          </div>
          <div class="scenario-description">
            <h5>场景描述</h5>
            <p>{{ selectedScenario.desc }}</p>
          </div>
          <div class="scenario-rules">
            <h5>演练规则</h5>
            <ul>
              <li>根据场景提示做出正确的判断和选择</li>
              <li>每个选择都会影响演练结果</li>
              <li>完成后会生成详细的分析报告</li>
              <li>可以多次演练以提高应对能力</li>
            </ul>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showStartDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmStartDrill">
          <el-icon><VideoPlay /></el-icon>
          开始演练
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as VueRouter from 'vue-router'
const useRouter = VueRouter.useRouter
import { ElMessage } from 'element-plus'
import {
  Trophy, Timer, Warning, User, VideoCamera, ChatLineSquare, DataLine, Medal,
  ArrowRight, CircleCheck, Clock, Loading, VideoPlay
} from '@element-plus/icons-vue'

const router = useRouter()
const showStartDialog = ref(false)
const selectedScenario = ref<any>(null)

const scenarios = [
  {
    id: 1,
    icon: 'Mobile',
    title: '电信诈骗演练',
    desc: '模拟接到冒充公检法的电话，学习如何识别和应对',
    difficulty: 1,
    difficultyName: '入门',
    duration: 15,
    participants: 1234,
    gradient: 'var(--gradient-primary)'
  },
  {
    id: 2,
    icon: 'Laptop',
    title: '网络诈骗演练',
    desc: '模拟网购诈骗场景，学习如何保护个人信息',
    difficulty: 2,
    difficultyName: '基础',
    duration: 20,
    participants: 987,
    gradient: 'var(--gradient-success)'
  },
  {
    id: 3,
    icon: 'Message',
    title: '短信诈骗演练',
    desc: '模拟收到钓鱼短信，学习如何辨别真伪',
    difficulty: 3,
    difficultyName: '进阶',
    duration: 15,
    participants: 765,
    gradient: 'var(--gradient-warning)'
  },
  {
    id: 4,
    icon: 'Trophy',
    title: '综合反诈演练',
    desc: '多种诈骗场景混合，全面提升应对能力',
    difficulty: 4,
    difficultyName: '高级',
    duration: 30,
    participants: 543,
    gradient: 'var(--gradient-danger)'
  }
]

const selectScenario = (scenario: any) => {
  selectedScenario.value = scenario
  showStartDialog.value = true
}

const confirmStartDrill = () => {
  showStartDialog.value = false
  ElMessage.success('即将开始演练，祝您取得好成绩！')
  router.push('/drill/scenario')
}

const initThreeScene = () => {
  // 这里将初始化Three.js 3D场景
  console.log('Initializing 3D scene...')
  // 模拟3D场景加载
  setTimeout(() => {
    const container = document.getElementById('3d-container')
    if (container) {
      container.innerHTML = `
        <div class="scene-placeholder">
          <el-icon class="scene-icon"><Laptop /></el-icon>
          <h3>3D演练场景</h3>
          <p>点击开始演练体验沉浸式反诈模拟</p>
        </div>
      `
    }
  }, 2000)
}

onMounted(() => {
  initThreeScene()
})
</script>

<style scoped>
.drill-page {
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
  background: linear-gradient(135deg, #7c3aed 0%, #8b5cf6 50%, #a78bfa 100%);
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

/* 场景选择 */
.scenario-section {
  margin-bottom: var(--spacing-10);
}

.section-header {
  margin-bottom: var(--spacing-6);
}

.section-header h2 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.section-header p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
}

.scenario-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-4);
}

.scenario-card {
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

.scenario-card:hover {
  border-color: var(--primary-color);
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.scenario-icon {
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

.scenario-info {
  flex: 1;
}

.scenario-info h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.scenario-info p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-2);
}

.scenario-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  font-size: var(--font-size-xs);
}

.difficulty {
  padding: var(--spacing-1) var(--spacing-2);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-sm);
  color: white;
}

.difficulty.level-1 { background: var(--success-color); }
.difficulty.level-2 { background: var(--info-color); }
.difficulty.level-3 { background: var(--warning-color); }
.difficulty.level-4 { background: var(--danger-color); }

.scenario-meta span {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  color: var(--text-muted);
}

.scenario-action {
  flex-shrink: 0;
}

/* 3D预览 */
.preview-section {
  margin-bottom: var(--spacing-10);
}

.preview-container {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--spacing-6);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-6);
  box-shadow: var(--shadow-md);
}

.three-container {
  position: relative;
  height: 400px;
  border-radius: var(--radius-lg);
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-3);
  color: var(--text-secondary);
}

.loading .el-icon {
  font-size: 48px;
  animation: spin 2s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.scene-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-4);
  color: var(--text-primary);
  text-align: center;
}

.scene-placeholder .scene-icon {
  font-size: 64px;
  color: var(--primary-color);
}

.preview-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.preview-info h3 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.preview-info p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.5;
}

.preview-features {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
  margin-top: var(--spacing-4);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  color: var(--text-primary);
}

.feature-item .el-icon {
  color: var(--primary-color);
  font-size: 18px;
}

/* 演练统计 */
.stats-section {
  margin-bottom: var(--spacing-8);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-4);
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
  flex-shrink: 0;
}

.stat-icon.success {
  background: var(--gradient-success);
}

.stat-icon.warning {
  background: var(--gradient-warning);
}

.stat-icon.danger {
  background: var(--gradient-danger);
}

.stat-info {
  flex: 1;
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

/* 开始演练对话框 */
.start-dialog-content {
  padding: var(--spacing-2);
}

.scenario-info-card {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: var(--spacing-5);
}

.scenario-info-card h4 {
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

.scenario-description {
  margin-bottom: var(--spacing-5);
}

.scenario-description h5,
.scenario-rules h5 {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.scenario-description p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.5;
}

.scenario-rules ul {
  margin: 0;
  padding-left: var(--spacing-5);
}

.scenario-rules li {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-1);
}

/* 响应式 */
@media (max-width: 1024px) {
  .scenario-grid {
    grid-template-columns: 1fr;
  }

  .preview-container {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
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

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
