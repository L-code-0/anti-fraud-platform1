<template>
  <div class="simulation-detail">
    <el-card class="main-card">
      <template #header>
        <div class="header">
          <h2 class="scene-title">{{ scene?.sceneName }}</h2>
          <el-tag :type="getDifficultyType(scene?.difficulty)" class="difficulty-tag">
            {{ getDifficultyText(scene?.difficulty) }}
          </el-tag>
        </div>
      </template>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-content">
          <el-skeleton :rows="10" animated />
          <p class="loading-text">正在加载演练场景...</p>
        </div>
      </div>
      
      <!-- 错误状态 -->
      <div v-else-if="error" class="error-container">
        <el-empty description="加载失败，请重试" :image-size="200" />
        <el-button type="primary" @click="loadDetail" class="retry-button">
          <el-icon><Refresh /></el-icon> 重新加载
        </el-button>
      </div>
      
      <div v-else>
        <!-- 未开始状态 -->
        <div v-if="!started" class="intro">
          <div class="background">
            <h3 class="section-title">场景背景</h3>
            <p class="background-content">{{ scene?.background }}</p>
          </div>
          <el-button type="primary" size="large" @click="startSimulation" :loading="starting" class="start-button">
            <el-icon><VideoPlay /></el-icon> 开始演练
          </el-button>
        </div>
        
        <!-- 演练中状态 -->
        <div v-else class="simulation-content">
          <!-- 进度条 -->
          <div v-if="!completed" class="progress-section">
            <el-progress 
              :percentage="Math.round(((currentStep + 1) / totalSteps) * 100)" 
              :format="formatProgress"
              :stroke-width="8"
              :color="progressColor"
              class="main-progress"
            />
            <div class="progress-text">
              <span>第 {{ currentStep + 1 }} 步</span>
              <span>共 {{ totalSteps }} 步</span>
            </div>
          </div>
          
          <!-- 时间限制 -->
          <div v-if="!completed && currentStep < script.length && script[currentStep]?.timeLimit > 0" class="time-limit">
            <el-progress 
              type="circle" 
              :percentage="timeLimitPercentage" 
              :color="timeLimitColor"
              :stroke-width="8"
              :format="formatTimeLimit"
              :width="60"
              class="time-progress"
            />
            <span class="time-limit-text">剩余时间: {{ remainingTime }}秒</span>
          </div>
          
          <!-- 对话区域 -->
          <div class="dialog-area" ref="dialogArea">
            <div v-if="script.length === 0" class="empty-message">
              <el-empty description="暂无演练内容" :image-size="150" />
            </div>
            <div
              v-else
              v-for="(item, index) in script.slice(0, currentStep + 1)"
              :key="index"
              class="dialog-item"
              :class="[item.role, { 'user-choice': index === currentStep && item.role === 'user' }]"
              :data-correct="item.isCorrect"
            >
              <div class="role-name">
                <el-avatar :size="24" :src="item.role === 'system' ? 'https://neeko-copilot.bytedance.net/api/text2image?prompt=police%20officer%20avatar&size=128x128' : 'https://neeko-copilot.bytedance.net/api/text2image?prompt=user%20avatar&size=128x128'" class="role-avatar" />
                {{ item.role === 'system' ? '对方' : '你' }}
                <span v-if="item.isCorrect !== undefined" class="correct-indicator" :class="item.isCorrect ? 'correct' : 'incorrect'">
                  {{ item.isCorrect ? '✓' : '✗' }}
                </span>
              </div>
              <div class="message">{{ item.content }}</div>
            </div>
          </div>
          
          <!-- 选项区域 -->
          <div v-if="currentStep < script.length" class="options-area">
            <h4 class="section-title">请选择你的回应：</h4>
            <div class="options">
              <el-button
                v-for="(option, index) in script[currentStep]?.options || []"
                :key="index"
                @click="selectOption(index)"
                class="option-btn"
                :loading="submitting"
                :disabled="submitting || timeUp"
                :class="{ 'time-up': timeUp }"
              >
                <span class="option-index">{{ String.fromCharCode(65 + index) }}.</span>
                <span class="option-text">{{ option }}</span>
              </el-button>
            </div>
          </div>
          
          <!-- 结果区域 -->
          <div v-if="completed" class="result-area">
            <el-result
              :icon="score >= 60 ? 'success' : 'warning'"
              :title="score >= 60 ? '演练通过！' : '需要加强学习'"
              :sub-title="`得分: ${score}分`"
              class="result-card"
            >
              <template #sub-title>
                <div class="result-subtitle">
                  <p>得分: {{ score }}分</p>
                  <p>用时: {{ Math.floor(duration / 60) }}分{{ duration % 60 }}秒</p>
                  <p v-if="score >= 60" class="success-text">恭喜你掌握了防诈骗技巧！</p>
                  <p v-else class="warning-text">建议继续学习相关防诈骗知识</p>
                </div>
              </template>
              <template #extra>
                <el-button type="primary" @click="retry" class="action-button">
                  <el-icon><Refresh /></el-icon> 重新演练
                </el-button>
                <el-button @click="$router.push('/simulation')" class="action-button">
                  <el-icon><ArrowLeft /></el-icon> 返回列表
                </el-button>
                <el-button @click="viewAnalysis" class="action-button">
                  <el-icon><View /></el-icon> 查看分析报告
                </el-button>
              </template>
            </el-result>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed, onUnmounted } from 'vue'
import * as VueRouter from 'vue-router'
const useRoute = VueRouter.useRoute
const useRouter = VueRouter.useRouter
import { get, post } from '@/utils/request'
import { Refresh, ArrowLeft, View, VideoPlay } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 状态变量
const scene = ref<any>(null)
const script = ref<any[]>([])
const started = ref(false)
const completed = ref(false)
const currentStep = ref(0)
const score = ref(0)
const choices = ref<number[]>([])
const startTime = ref(Date.now())
const loading = ref(true)
const error = ref(false)
const starting = ref(false)
const submitting = ref(false)
const recordId = ref<number>(0)
const dialogArea = ref<HTMLElement>()
const timeLimit = ref(0)
const remainingTime = ref(0)
const timeUp = ref(false)
const timer = ref<number | null>(null)
const duration = ref(0)

// 计算属性
const progressColor = computed(() => {
  const progress = ((currentStep.value + 1) / totalSteps.value) * 100
  if (progress < 33) return '#67c23a'
  if (progress < 66) return '#409EFF'
  return '#e6a23c'
})

const totalSteps = computed(() => {
  // 计算总步骤数，考虑分支剧情
  return script.value.filter(item => item.role === 'system').length
})

const timeLimitPercentage = computed(() => {
  if (timeLimit.value === 0) return 100
  return Math.max(0, (remainingTime.value / timeLimit.value) * 100)
})

const timeLimitColor = computed(() => {
  const percentage = timeLimitPercentage.value
  if (percentage > 60) return '#67c23a'
  if (percentage > 30) return '#e6a23c'
  return '#f56c6c'
})

// 方法
const getDifficultyText = (level: number) => {
  const levels: Record<number, string> = { 1: '简单', 2: '普通', 3: '困难' }
  return levels[level] || '未知'
}

const getDifficultyType = (level: number) => {
  const types: Record<number, string> = { 1: 'success', 2: 'warning', 3: 'danger' }
  return types[level] || 'info'
}

const formatProgress = (percentage: number) => {
  return `${percentage}%`
}

const formatTimeLimit = (percentage: number) => {
  return `${remainingTime.value}s`
}

const scrollToBottom = async () => {
  await nextTick()
  if (dialogArea.value) {
    dialogArea.value.scrollTop = dialogArea.value.scrollHeight
  }
}

const startTimer = (seconds: number) => {
  clearInterval(timer.value as number)
  timeLimit.value = seconds
  remainingTime.value = seconds
  timeUp.value = false
  
  timer.value = window.setInterval(() => {
    remainingTime.value--
    if (remainingTime.value <= 0) {
      clearInterval(timer.value as number)
      timeUp.value = true
      handleTimeUp()
    }
  }, 1000)
}

const stopTimer = () => {
  clearInterval(timer.value as number)
  timeLimit.value = 0
  remainingTime.value = 0
  timeUp.value = false
}

const handleTimeUp = async () => {
  // 时间到，自动选择错误选项
  choices.value.push(-1) // -1 表示超时
  
  // 添加用户选择的对话
  const currentItem = script.value[currentStep.value]
  const userChoice = {
    role: 'user',
    content: '（超时未选择）',
    isCorrect: false
  }
  
  // 插入用户选择到脚本中
  script.value.splice(currentStep.value + 1, 0, userChoice)
  
  // 延迟一下，让用户看到选择结果
  await new Promise(resolve => setTimeout(resolve, 1000))
  
  // 更新进度
  currentStep.value += 2 // 跳过用户选择的步骤
  
  // 滚动到底部
  await scrollToBottom()
  
  if (currentStep.value >= script.value.length) {
    await completeSimulation()
  } else {
    // 检查下一个步骤是否有时间限制
    checkTimeLimit()
  }
}

const checkTimeLimit = () => {
  if (currentStep.value < script.value.length) {
    const currentItem = script.value[currentStep.value]
    if (currentItem?.timeLimit && currentItem.timeLimit > 0) {
      startTimer(currentItem.timeLimit)
    } else {
      stopTimer()
    }
  } else {
    stopTimer()
  }
}

const loadDetail = async () => {
  loading.value = true
  error.value = false
  try {
    console.log('路由参数:', route.params)
    const id = Number(route.params.id)
    const res = await get(`/simulation/${id}`)
    console.log('API响应:', res)
    scene.value = res.data
    script.value = res.data.script || []
  } catch (e) {
    console.error('加载演练详情失败:', e)
    error.value = true
    // 根据不同的ID设置不同的演练场景
    const id = Number(route.params.id)
    if (id === 1) {
      scene.value = {
        sceneName: '冒充公检法诈骗',
        difficulty: 2,
        background: '你接到一个自称是公安局的电话，说你涉及一起洗钱案件，需要配合调查...'
      }
      script.value = [
        { role: 'system', content: '您好，我是市公安局的民警，您的身份证号涉及一起洗钱案件，请配合我们调查。', options: ['好的，我配合', '你怎么证明你是警察？', '我要报警核实'], correctIndex: 2, timeLimit: 30 },
        { role: 'system', content: '我们需要您将资金转移到安全账户，以配合调查。', options: ['好的，我马上转', '我需要先核实', '这是诈骗！'], correctIndex: 2, timeLimit: 20 },
        { role: 'system', content: '这是最后一次机会，否则我们将对你采取强制措施。', options: ['我马上转钱', '我要向真正的警察核实', '我不怕你'], correctIndex: 1, timeLimit: 15 }
      ]
    } else if (id === 2) {
      scene.value = {
        sceneName: '刷单返利诈骗',
        difficulty: 1,
        background: '你在网上看到一个兼职刷单的广告，声称可以轻松赚取佣金...'
      }
      script.value = [
        { role: 'system', content: '你好，我们是正规刷单平台，只需完成任务就能获得高额佣金。', options: ['好的，我加入', '需要先交钱吗？', '这是诈骗吧'], correctIndex: 1, timeLimit: 25 },
        { role: 'system', content: '需要先缴纳保证金，完成任务后会一并返还。', options: ['好的，我交', '我不做了', '你是骗子'], correctIndex: 1, timeLimit: 20 },
        { role: 'system', content: '任务完成了，但是需要再做几单才能提现。', options: ['好的，我继续', '我要提现', '你是骗子，我报警'], correctIndex: 2, timeLimit: 15 }
      ]
    } else {
      scene.value = {
        sceneName: '网络兼职刷单诈骗',
        difficulty: 2,
        background: '你在社交媒体上看到一个兼职刷单的广告，声称可以轻松赚取佣金...'
      }
      script.value = [
        { role: 'system', content: '你好，我们是正规刷单平台，只需完成任务就能获得高额佣金。', options: ['好的，我加入', '需要先交钱吗？', '这是诈骗吧'], correctIndex: 1, timeLimit: 30 },
        { role: 'system', content: '需要先缴纳保证金，完成任务后会一并返还。', options: ['好的，我交', '我不做了', '你是骗子'], correctIndex: 1, timeLimit: 25 },
        { role: 'system', content: '任务完成了，但是需要再做几单才能提现。', options: ['好的，我继续', '我要提现', '你是骗子，我报警'], correctIndex: 2, timeLimit: 20 }
      ]
    }
  } finally {
    loading.value = false
  }
}

const startSimulation = async () => {
  starting.value = true
  try {
    const id = Number(route.params.id)
    const res = await post(`/simulation/${id}/start`)
    recordId.value = res.data
  } catch (e) {
    console.error('开始演练失败:', e)
  } finally {
    starting.value = false
    started.value = true
    startTime.value = Date.now()
    // 检查第一个步骤是否有时间限制
    checkTimeLimit()
  }
}

const selectOption = async (index: number) => {
  if (submitting.value || timeUp.value) return
  
  submitting.value = true
  try {
    // 记录选择
    choices.value.push(index)
    
    // 添加用户选择的对话
    const currentItem = script.value[currentStep.value]
    const userChoice = {
      role: 'user',
      content: currentItem.options[index],
      isCorrect: index === currentItem.correctIndex
    }
    
    // 插入用户选择到脚本中
    script.value.splice(currentStep.value + 1, 0, userChoice)
    
    // 延迟一下，让用户看到选择结果
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 检查是否有分支剧情
    if (currentItem.branchId) {
      // 查找分支剧情
      const branchIndex = script.value.findIndex((item, i) => 
        i > currentStep.value && item.branchId === currentItem.branchId
      )
      if (branchIndex !== -1) {
        currentStep.value = branchIndex
      } else {
        currentStep.value += 2 // 跳过用户选择的步骤
      }
    } else {
      currentStep.value += 2 // 跳过用户选择的步骤
    }
    
    // 滚动到底部
    await scrollToBottom()
    
    if (currentStep.value >= script.value.length) {
      await completeSimulation()
    } else {
      // 检查下一个步骤是否有时间限制
      checkTimeLimit()
    }
  } finally {
    submitting.value = false
  }
}

const completeSimulation = async () => {
  stopTimer()
  duration.value = Math.floor((Date.now() - startTime.value) / 1000)
  
  let correctCount = 0
  script.value.forEach((item, index) => {
    if (item.isCorrect) {
      correctCount++
    }
  })
  
  score.value = Math.round((correctCount / totalSteps.value) * 100)
  completed.value = true
  
  try {
    await post('/simulation/submit', {
      sceneId: Number(route.params.id),
      recordId: recordId.value,
      choices: choices.value,
      duration: duration.value
    })
  } catch (e) {
    console.error('提交演练结果失败:', e)
  }
}

const retry = () => {
  stopTimer()
  started.value = false
  completed.value = false
  currentStep.value = 0
  choices.value = []
  score.value = 0
  recordId.value = 0
  duration.value = 0
  // 重新加载脚本，恢复原始状态
  loadDetail()
}

const viewAnalysis = () => {
  // 跳转到分析报告页面
  router.push(`/simulation/analysis/${recordId.value}`)
}

onMounted(() => {
  loadDetail()
})

onUnmounted(() => {
  stopTimer()
})
</script>

<style scoped>
.simulation-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.main-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.main-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.scene-title {
  margin: 0;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
  animation: fadeIn 0.5s ease-in;
}

.difficulty-tag {
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 16px;
  animation: slideInUp 0.3s ease-out;
}

/* 加载状态 */
.loading-container {
  padding: 60px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.loading-content {
  text-align: center;
}

.loading-text {
  margin-top: 20px;
  color: #606266;
  font-size: 14px;
  animation: pulse 1.5s infinite;
}

/* 错误状态 */
.error-container {
  padding: 60px 20px;
  text-align: center;
  animation: fadeIn 0.5s ease-in;
}

.retry-button {
  margin-top: 24px;
  padding: 10px 24px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.retry-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 介绍区域 */
.intro {
  text-align: center;
  padding: 60px 40px;
  animation: fadeIn 0.5s ease-in;
}

.background {
  text-align: left;
  margin-bottom: 36px;
  padding: 24px;
  background: #f5f7fa;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  animation: slideInUp 0.3s ease-out;
}

.section-title {
  margin-bottom: 16px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.background-content {
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
}

.start-button {
  padding: 12px 32px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  animation: slideInUp 0.3s ease-out 0.1s both;
}

.start-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 进度条区域 */
.progress-section {
  margin-bottom: 24px;
  animation: slideInUp 0.3s ease-out;
}

.main-progress {
  border-radius: 4px;
  overflow: hidden;
}

.progress-text {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #606266;
  margin-top: 12px;
  font-weight: 500;
}

/* 时间限制 */
.time-limit {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  animation: slideInUp 0.3s ease-out;
}

.time-progress {
  margin-right: 20px;
  flex-shrink: 0;
}

.time-limit-text {
  font-size: 14px;
  color: #606266;
  font-weight: 600;
  flex: 1;
}

/* 对话区域 */
.dialog-area {
  padding: 24px;
  background: #f5f7fa;
  border-radius: 12px;
  min-height: 350px;
  max-height: 450px;
  overflow-y: auto;
  margin-bottom: 24px;
  scroll-behavior: smooth;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.dialog-item {
  margin-bottom: 20px;
  animation: fadeIn 0.3s ease-in;
  position: relative;
}

.dialog-item.system {
  text-align: left;
}

.dialog-item.user {
  text-align: right;
}

.dialog-item.user-choice {
  animation: pulse 0.5s ease-in-out;
}

.role-name {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-avatar {
  flex-shrink: 0;
}

.correct-indicator {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.correct-indicator.correct {
  background-color: #67c23a;
  color: white;
  box-shadow: 0 2px 4px rgba(103, 194, 58, 0.3);
}

.correct-indicator.incorrect {
  background-color: #f56c6c;
  color: white;
  box-shadow: 0 2px 4px rgba(245, 108, 108, 0.3);
}

.message {
  display: inline-block;
  padding: 14px 20px;
  border-radius: 16px;
  max-width: 70%;
  line-height: 1.6;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  font-size: 14px;
  transition: all 0.3s ease;
}

.dialog-item.system .message {
  background: white;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.dialog-item.user .message {
  background: #409EFF;
  color: white;
  border-bottom-right-radius: 4px;
}

/* 选项区域 */
.options-area {
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  animation: slideInUp 0.3s ease-out;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.option-btn {
  justify-content: flex-start;
  text-align: left;
  padding: 16px 24px;
  height: auto;
  border-radius: 10px;
  transition: all 0.3s ease;
  border: 2px solid #dcdfe6;
  overflow: hidden;
  position: relative;
}

.option-btn:hover:not(:disabled) {
  border-color: #409EFF;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transform: translateY(-3px);
}

.option-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.option-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

.option-btn.time-up {
  border-color: #f56c6c;
  background-color: #fef0f0;
}

.option-index {
  font-weight: 600;
  margin-right: 12px;
  color: #606266;
  min-width: 24px;
  display: inline-block;
}

.option-text {
  flex: 1;
  line-height: 1.5;
}

/* 结果区域 */
.result-area {
  padding: 60px 40px;
  text-align: center;
  animation: fadeIn 0.5s ease-in;
}

.result-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.result-subtitle {
  margin-top: 24px;
  padding: 0 20px;
}

.result-subtitle p {
  margin: 12px 0;
  font-size: 14px;
  color: #606266;
}

.success-text {
  color: #67c23a;
  font-weight: 600;
  font-size: 16px !important;
  animation: pulse 1s ease-in-out;
}

.warning-text {
  color: #e6a23c;
  font-weight: 600;
  font-size: 16px !important;
}

.action-button {
  margin: 0 8px;
  padding: 10px 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(25px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes pulse-slow {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.6;
  }
}

/* 滚动条样式 */
.dialog-area::-webkit-scrollbar {
  width: 6px;
}

.dialog-area::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.dialog-area::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.dialog-area::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .simulation-detail {
    padding: 16px;
  }
  
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .scene-title {
    font-size: 18px;
  }
  
  .intro {
    padding: 40px 20px;
  }
  
  .background {
    padding: 20px;
    margin-bottom: 28px;
  }
  
  .dialog-area {
    min-height: 300px;
    max-height: 400px;
    padding: 20px;
  }
  
  .message {
    max-width: 85%;
    padding: 12px 16px;
  }
  
  .options-area {
    padding: 20px;
  }
  
  .option-btn {
    padding: 14px 20px;
  }
  
  .time-limit {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    padding: 16px;
  }
  
  .time-progress {
    margin-right: 0;
  }
  
  .result-area {
    padding: 40px 20px;
  }
  
  .action-button {
    margin: 8px 4px;
    padding: 8px 16px;
  }
}

/* 平板设备适配 */
@media (min-width: 769px) and (max-width: 1024px) {
  .simulation-detail {
    max-width: 90%;
  }
  
  .dialog-area {
    min-height: 320px;
    max-height: 420px;
  }
}
</style>
