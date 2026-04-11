<template>
  <div class="drill-scenario-page">
    <!-- 页面头部 -->
    <div class="scenario-header">
      <div class="header-content">
        <div class="header-left">
          <el-button type="primary" @click="backToIndex" plain>
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <h1>{{ currentScenario?.title }}</h1>
        </div>
        <div class="header-right">
          <div class="timer">
            <el-icon><Clock /></el-icon>
            <span>{{ formatTime(remainingTime) }}</span>
          </div>
          <div class="score">
            <el-icon><Trophy /></el-icon>
            <span>{{ score }}分</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 3D场景容器 -->
    <div class="scenario-container">
      <div class="three-scene" ref="sceneContainer">
        <!-- 3D场景将在这里渲染 -->
        <div class="loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>加载3D场景中...</span>
        </div>
      </div>

      <!-- 实时反馈面板 -->
      <div class="feedback-panel">
        <div class="panel-header">
          <h3>实时反馈</h3>
          <el-button type="text" @click="toggleFeedback">
            <el-icon><ArrowUp /></el-icon>
          </el-button>
        </div>
        <div class="panel-content" :class="{ 'collapsed': !showFeedback }">
          <div class="feedback-item" v-for="(item, index) in feedbacks" :key="index">
            <div class="feedback-time">{{ item.time }}</div>
            <div class="feedback-content" :class="item.type">
              <el-icon :class="item.icon"></el-icon>
              <span>{{ item.message }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 交互选项 -->
      <div class="interaction-panel">
        <div class="panel-header">
          <h3>请选择您的应对方式</h3>
        </div>
        <div class="options-grid">
          <div
            class="option-card"
            v-for="(option, index) in currentOptions"
            :key="index"
            @click="selectOption(option)"
          >
            <div class="option-content">
              <span class="option-label">{{ String.fromCharCode(65 + index) }}</span>
              <p>{{ option.text }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 演练结束对话框 -->
    <el-dialog
      v-model="showResultDialog"
      title="演练结束"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="result-content">
        <div class="result-header">
          <div class="result-score">
            <span class="score-number">{{ finalScore }}</span>
            <span class="score-label">最终得分</span>
          </div>
          <div class="result-status" :class="finalScore >= 60 ? 'pass' : 'fail'">
            {{ finalScore >= 60 ? '通过' : '未通过' }}
          </div>
        </div>
        <div class="result-analysis">
          <h4>演练分析</h4>
          <div class="analysis-item">
            <span class="analysis-label">正确选择率</span>
            <span class="analysis-value">{{ correctRate }}%</span>
          </div>
          <div class="analysis-item">
            <span class="analysis-label">平均响应时间</span>
            <span class="analysis-value">{{ avgResponseTime }}秒</span>
          </div>
          <div class="analysis-item">
            <span class="analysis-label">知识点掌握</span>
            <span class="analysis-value">{{ masteryLevel }}</span>
          </div>
        </div>
        <div class="result-suggestions">
          <h4>改进建议</h4>
          <ul>
            <li v-for="(suggestion, index) in suggestions" :key="index">
              {{ suggestion }}
            </li>
          </ul>
        </div>
      </div>
      <template #footer>
        <el-button @click="showResultDialog = false; backToIndex()">返回首页</el-button>
        <el-button type="primary" @click="showResultDialog = false; restartDrill()">
          重新演练
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as VueRouter from 'vue-router'
const useRouter = VueRouter.useRouter
import {
  ArrowLeft, Clock, Trophy, Loading, ArrowUp
} from '@element-plus/icons-vue'

const router = useRouter()
const sceneContainer = ref<HTMLElement>()
const showFeedback = ref(true)
const showResultDialog = ref(false)
const remainingTime = ref(900) // 15分钟
const score = ref(0)
const finalScore = ref(0)
const correctRate = ref(0)
const avgResponseTime = ref(0)
const masteryLevel = ref('')
const suggestions = ref<string[]>([])
const feedbacks = ref<Array<{
  time: string
  message: string
  type: string
  icon: string
}>>([])

const currentScenario = ref({
  id: 1,
  title: '电信诈骗演练',
  desc: '模拟接到冒充公检法的电话，学习如何识别和应对',
  duration: 15
})

const currentOptions = ref([
  {
    text: '立即挂断电话，不相信对方的任何说法',
    correct: true,
    score: 25
  },
  {
    text: '按照对方指示，提供个人信息',
    correct: false,
    score: 0
  },
  {
    text: '询问对方警号和单位，然后挂断电话核实',
    correct: true,
    score: 20
  },
  {
    text: '按照对方要求转账到安全账户',
    correct: false,
    score: 0
  }
])

const formatTime = (seconds: number): string => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

const toggleFeedback = () => {
  showFeedback.value = !showFeedback.value
}

const selectOption = (option: any) => {
  if (option.correct) {
    score.value += option.score
    addFeedback('正确！您的选择是正确的，这是典型的电信诈骗手法。', 'success', 'CircleCheck')
  } else {
    addFeedback('错误！您的选择存在风险，这是诈骗分子的常见手法。', 'error', 'Warning')
  }
  
  // 模拟场景进展
  setTimeout(() => {
    // 这里可以根据选择加载不同的场景
    if (score.value >= 40) {
      completeDrill()
    } else {
      // 加载下一组选项
      loadNextOptions()
    }
  }, 2000)
}

const addFeedback = (message: string, type: string, icon: string) => {
  const now = new Date()
  const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`
  feedbacks.value.push({ time, message, type, icon })
  // 限制反馈数量
  if (feedbacks.value.length > 10) {
    feedbacks.value.shift()
  }
}

const loadNextOptions = () => {
  currentOptions.value = [
    {
      text: '拨打110报警，向警方核实情况',
      correct: true,
      score: 25
    },
    {
      text: '按照对方要求，在ATM机上操作',
      correct: false,
      score: 0
    },
    {
      text: '忽略对方的威胁，挂掉电话',
      correct: true,
      score: 20
    },
    {
      text: '相信对方的话，继续与对方沟通',
      correct: false,
      score: 0
    }
  ]
}

const completeDrill = () => {
  finalScore.value = score.value
  correctRate.value = Math.round((score.value / 100) * 100)
  avgResponseTime.value = 5
  masteryLevel.value = '良好'
  suggestions.value = [
    '继续保持警惕，不轻易相信陌生电话',
    '遇到类似情况及时报警',
    '定期学习反诈知识，了解最新诈骗手法'
  ]
  showResultDialog.value = true
}

const backToIndex = () => {
  router.push('/drill')
}

const restartDrill = () => {
  score.value = 0
  remainingTime.value = 900
  feedbacks.value = []
  currentOptions.value = [
    {
      text: '立即挂断电话，不相信对方的任何说法',
      correct: true,
      score: 25
    },
    {
      text: '按照对方指示，提供个人信息',
      correct: false,
      score: 0
    },
    {
      text: '询问对方警号和单位，然后挂断电话核实',
      correct: true,
      score: 20
    },
    {
      text: '按照对方要求转账到安全账户',
      correct: false,
      score: 0
    }
  ]
  initThreeScene()
}

const initThreeScene = () => {
  // 这里将初始化Three.js 3D场景
  console.log('Initializing 3D scene...')
  // 模拟3D场景加载
  setTimeout(() => {
    if (sceneContainer.value) {
      sceneContainer.value.innerHTML = `
        <div class="scene-content">
          <div class="phone-scene">
            <div class="phone">
              <div class="phone-screen">
                <div class="caller-info">
                  <div class="caller-number">010-110</div>
                  <div class="caller-name">北京市公安局</div>
                </div>
                <div class="call-message">
                  <p>您好，我是北京市公安局民警，您的身份证号码涉嫌洗钱案件，请配合调查。</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      `
    }
  }, 2000)
}

let timer: number | undefined

onMounted(() => {
  initThreeScene()
  // 启动计时器
  timer = window.setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      completeDrill()
      clearInterval(timer)
    }
  }, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.drill-scenario-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

/* 场景头部 */
.scenario-header {
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-primary);
  padding: var(--spacing-4) var(--spacing-6);
  box-shadow: var(--shadow-md);
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
}

.header-left h1 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-6);
}

.timer, .score {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

/* 场景容器 */
.scenario-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: var(--spacing-6);
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: var(--spacing-6);
  min-height: calc(100vh - 120px);
}

.three-scene {
  position: relative;
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  min-height: 500px;
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

.scene-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-8);
}

.phone-scene {
  display: flex;
  align-items: center;
  justify-content: center;
}

.phone {
  width: 300px;
  height: 600px;
  background: #333;
  border-radius: 40px;
  padding: 20px;
  box-shadow: var(--shadow-xl);
}

.phone-screen {
  width: 100%;
  height: 100%;
  background: white;
  border-radius: 30px;
  padding: 30px;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.caller-info {
  text-align: center;
  margin-bottom: var(--spacing-6);
}

.caller-number {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.caller-name {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.call-message {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-size: var(--font-size-base);
  color: var(--text-primary);
  line-height: 1.5;
}

/* 反馈面板 */
.feedback-panel {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.panel-header {
  padding: var(--spacing-4);
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-primary);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.panel-content {
  padding: var(--spacing-4);
  max-height: 300px;
  overflow-y: auto;
  transition: max-height 0.3s ease;
}

.panel-content.collapsed {
  max-height: 0;
  padding: 0;
}

.feedback-item {
  display: flex;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-3);
  padding: var(--spacing-2);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
}

.feedback-time {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
  min-width: 80px;
  flex-shrink: 0;
}

.feedback-content {
  flex: 1;
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-2);
  font-size: var(--font-size-sm);
  line-height: 1.4;
}

.feedback-content.success {
  color: var(--success-color);
}

.feedback-content.error {
  color: var(--danger-color);
}

.feedback-content.info {
  color: var(--info-color);
}

/* 交互选项 */
.interaction-panel {
  grid-column: 2;
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  overflow: hidden;
  margin-top: var(--spacing-6);
}

.options-grid {
  padding: var(--spacing-4);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
}

.option-card {
  padding: var(--spacing-4);
  background: var(--bg-secondary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.option-card:hover {
  border-color: var(--primary-color);
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.option-content {
  display: flex;
  gap: var(--spacing-3);
  align-items: flex-start;
}

.option-label {
  width: 24px;
  height: 24px;
  background: var(--primary-color);
  color: white;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  flex-shrink: 0;
}

.option-content p {
  flex: 1;
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  line-height: 1.4;
  margin: 0;
}

/* 结果对话框 */
.result-content {
  padding: var(--spacing-4);
}

.result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-6);
  padding-bottom: var(--spacing-4);
  border-bottom: 1px solid var(--border-primary);
}

.result-score {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-2);
}

.score-number {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  color: var(--primary-color);
}

.score-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.result-status {
  padding: var(--spacing-2) var(--spacing-4);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: white;
}

.result-status.pass {
  background: var(--success-color);
}

.result-status.fail {
  background: var(--danger-color);
}

.result-analysis {
  margin-bottom: var(--spacing-6);
}

.result-analysis h4,
.result-suggestions h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-3);
}

.analysis-item {
  display: flex;
  justify-content: space-between;
  padding: var(--spacing-2);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-2);
  font-size: var(--font-size-sm);
}

.analysis-label {
  color: var(--text-secondary);
}

.analysis-value {
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.result-suggestions ul {
  margin: 0;
  padding-left: var(--spacing-5);
}

.result-suggestions li {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-2);
  line-height: 1.4;
}

/* 响应式 */
@media (max-width: 1024px) {
  .scenario-container {
    grid-template-columns: 1fr;
  }

  .interaction-panel {
    grid-column: 1;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: var(--spacing-4);
    align-items: flex-start;
  }

  .scenario-container {
    padding: var(--spacing-4);
  }

  .phone {
    width: 250px;
    height: 500px;
  }
}
</style>
