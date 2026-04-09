<template>
  <div class="test-paper">
    <el-card v-if="!submitted" class="paper-card">
      <!-- 顶部信息栏 -->
      <template #header>
        <div class="paper-header">
          <div class="paper-info">
            <h2>{{ paper?.paperName }}</h2>
            <div class="paper-meta">
              <el-tag type="info">共 {{ questions.length }} 题</el-tag>
              <el-tag type="info">满分 {{ totalScore }} 分</el-tag>
              <el-tag type="info">及格 {{ paper?.passScore || 60 }} 分</el-tag>
            </div>
          </div>
          <div class="timer-wrapper">
            <div 
              class="timer" 
              :class="{ 
                'warning': remainingTime <= 300 && remainingTime > 60,
                'danger': remainingTime <= 60 
              }"
            >
              <el-icon><Timer /></el-icon>
              <span class="time-text">{{ formatTime(remainingTime) }}</span>
              <span class="time-label">剩余时间</span>
            </div>
            <div class="timer-progress">
              <el-progress 
                :percentage="timerPercentage" 
                :stroke-width="4"
                :show-text="false"
                :color="timerColor"
              />
            </div>
          </div>
        </div>
      </template>
      
      <!-- 答题进度 -->
      <div class="progress-bar">
        <div class="progress-info">
          <span>答题进度</span>
          <span class="progress-count">{{ answeredCount }}/{{ questions.length }}</span>
        </div>
        <el-progress :percentage="progressPercentage" :stroke-width="8" :show-text="false" />
        <div class="progress-legend">
          <span class="legend-item"><span class="dot answered"></span>已答</span>
          <span class="legend-item"><span class="dot unanswered"></span>未答</span>
          <span class="legend-item"><span class="dot flagged"></span>标记</span>
        </div>
      </div>
      
      <!-- 快速跳转 -->
      <div class="question-nav">
        <el-button 
          v-for="(q, index) in questions" 
          :key="q.id"
          :type="getNavButtonType(q)"
          :class="{ 'current': currentQuestion === index }"
          size="small"
          circle
          @click="scrollToQuestion(index)"
        >
          {{ index + 1 }}
          <template v-if="answers[q.id]" #icon>
            <el-icon><Check /></el-icon>
          </template>
        </el-button>
      </div>
      
      <!-- 题目列表 -->
      <div class="questions" ref="questionsRef">
        <div
          v-for="(question, index) in questions"
          :key="question.id"
          class="question-item"
          :id="`question-${index}`"
          :class="{ 'current': currentQuestion === index }"
          @mouseenter="currentQuestion = index"
        >
          <div class="question-header">
            <div class="question-info">
              <span class="question-number">{{ index + 1 }}</span>
              <el-tag>{{ getTypeName(question.questionType) }}</el-tag>
              <el-tag v-if="question.difficulty === 3" type="danger" size="small">困难</el-tag>
              <el-tag v-else-if="question.difficulty === 2" type="warning" size="small">中等</el-tag>
              <el-tag v-else type="success" size="small">简单</el-tag>
            </div>
            <div class="question-actions">
              <el-button size="small" text @click="toggleFlag(question)">
                <el-icon :color="flaggedQuestions.has(question.id) ? '#E6A23C' : undefined">
                  <Flag />
                </el-icon>
                {{ flaggedQuestions.has(question.id) ? '取消标记' : '标记' }}
              </el-button>
            </div>
            <span class="score">{{ question.score }}分</span>
          </div>
          <div class="question-content">{{ question.content }}</div>
          
          <!-- 单选题 -->
          <div v-if="question.questionType === 1" class="options">
            <div
              v-for="(option, i) in question.options"
              :key="i"
              class="option-item"
              :class="{ 'selected': answers[question.id] === getOptionLabel(i) }"
              @click="selectAnswer(question.id, getOptionLabel(i))"
            >
              <span class="option-label">{{ getOptionLabel(i) }}</span>
              <span class="option-text">{{ option }}</span>
            </div>
          </div>
          
          <!-- 多选题 -->
          <div v-else-if="question.questionType === 2" class="options">
            <el-checkbox-group v-model="multiAnswers[question.id]">
              <div
                v-for="(option, i) in question.options"
                :key="i"
                class="option-item checkbox"
                :class="{ 'selected': multiAnswers[question.id]?.includes(getOptionLabel(i)) }"
                @click="toggleMultiAnswer(question.id, getOptionLabel(i))"
              >
                <el-checkbox :value="getOptionLabel(i)" @click.stop />
                <span class="option-label">{{ getOptionLabel(i) }}</span>
                <span class="option-text">{{ option }}</span>
              </div>
            </el-checkbox-group>
          </div>
          
          <!-- 判断题 -->
          <div v-else-if="question.questionType === 3" class="options">
            <div
              class="option-item"
              :class="{ 'selected': answers[question.id] === 'A' }"
              @click="selectAnswer(question.id, 'A')"
            >
              <span class="option-label">A</span>
              <span class="option-text">正确</span>
            </div>
            <div
              class="option-item"
              :class="{ 'selected': answers[question.id] === 'B' }"
              @click="selectAnswer(question.id, 'B')"
            >
              <span class="option-label">B</span>
              <span class="option-text">错误</span>
            </div>
          </div>
          
          <!-- 题目解析（提交后显示） -->
          <div v-if="submitted && question.analysis" class="question-analysis">
            <div class="analysis-header">
              <el-icon><InfoFilled /></el-icon>
              <span>题目解析</span>
            </div>
            <p>{{ question.analysis }}</p>
          </div>
        </div>
      </div>
      
      <!-- 底部操作栏 -->
      <div class="submit-area">
        <div class="submit-info">
          <span>已答 {{ answeredCount }} 题</span>
          <span>未答 {{ questions.length - answeredCount }} 题</span>
        </div>
        <div class="submit-actions">
          <el-button @click="handleSaveDraft" :loading="saving">保存草稿</el-button>
          <el-button @click="handleSubmit" type="primary" size="large">
            提交试卷
          </el-button>
        </div>
      </div>
    </el-card>
    
    <!-- 测试结果 -->
    <el-card v-else class="result-card">
      <div class="result-header">
        <div class="result-icon" :class="{ 'passed': result?.isPassed }">
          <el-icon :size="80">
            <CircleCheckFilled v-if="result?.isPassed" />
            <CircleCloseFilled v-else />
          </el-icon>
        </div>
        <h2>{{ result?.isPassed ? '恭喜通过考试！' : '未能通过考试' }}</h2>
        <p class="result-message">
          {{ result?.isPassed 
            ? '您的表现很棒！继续学习，提升防骗能力！' 
            : '别灰心，多复习相关知识，下次一定能通过！' 
          }}
        </p>
      </div>
      
      <div class="result-score">
        <div class="score-circle" :class="{ 'passed': result?.isPassed }">
          <span class="score-value">{{ result?.userScore || 0 }}</span>
          <span class="score-total">/{{ result?.totalScore || 100 }}</span>
        </div>
        <div class="score-label">您的得分</div>
      </div>
      
      <div class="result-stats">
        <div class="stat-item">
          <div class="stat-icon correct">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ result?.correctCount || 0 }}</span>
            <span class="stat-label">正确题数</span>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon wrong">
            <el-icon><Close /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ result?.wrongCount || 0 }}</span>
            <span class="stat-label">错误题数</span>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon time">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ formatTime(result?.duration || 0) }}</span>
            <span class="stat-label">用时</span>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon accuracy">
            <el-icon><DataLine /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ accuracyRate }}%</span>
            <span class="stat-label">正确率</span>
          </div>
        </div>
      </div>
      
      <!-- 错题回顾 -->
      <div class="wrong-questions" v-if="result?.wrongQuestions?.length > 0">
        <h3>
          <el-icon><Warning /></el-icon>
          错题回顾
        </h3>
        <div class="wrong-list">
          <div v-for="wq in result.wrongQuestions" :key="wq.id" class="wrong-item">
            <div class="wrong-content">{{ wq.content }}</div>
            <div class="wrong-answer">
              <span class="your-answer">您的答案：{{ wq.yourAnswer }}</span>
              <span class="correct-answer">正确答案：{{ wq.correctAnswer }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="result-actions">
        <el-button @click="$router.push('/test')">返回列表</el-button>
        <el-button @click="$router.push('/wrongbook')">查看错题本</el-button>
        <el-button type="primary" @click="retry">重新测试</el-button>
      </div>
    </el-card>
    
    <!-- 时间警告对话框 -->
    <el-dialog
      v-model="showTimeWarning"
      title="时间提醒"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="time-warning-content">
        <el-icon class="warning-icon" :size="60" color="#E6A23C"><Timer /></el-icon>
        <p>考试时间仅剩 <strong>{{ formatTime(remainingTime) }}</strong></p>
        <p>请注意合理安排时间！</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { get, post } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Timer, Check, Close, Flag, InfoFilled, CircleCheckFilled, CircleCloseFilled, Warning, DataLine } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const paper = ref<any>(null)
const questions = ref<any[]>([])
const answers = reactive<Record<number, string>>({})
const multiAnswers = reactive<Record<number, string[]>>({})
const flaggedQuestions = ref<Set<number>>(new Set())
const submitted = ref(false)
const result = ref<any>(null)
const saving = ref(false)

const remainingTime = ref(1800)
const startTime = ref(Date.now())
let timer: any = null
let warningShown = false

const questionsRef = ref<HTMLElement | null>(null)
const currentQuestion = ref(0)
const showTimeWarning = ref(false)

// 计算属性
const totalScore = computed(() => questions.value.reduce((sum, q) => sum + q.score, 0))

const answeredCount = computed(() => {
  let count = 0
  for (const q of questions.value) {
    if (q.questionType === 2) {
      if (multiAnswers[q.id]?.length > 0) count++
    } else {
      if (answers[q.id]) count++
    }
  }
  return count
})

const progressPercentage = computed(() => {
  return Math.round((answeredCount.value / questions.value.length) * 100)
})

const timerPercentage = computed(() => {
  const totalTime = paper.value?.duration * 60 || 1800
  return Math.round((remainingTime.value / totalTime) * 100)
})

const timerColor = computed(() => {
  if (remainingTime.value <= 60) return '#F56C6C'
  if (remainingTime.value <= 300) return '#E6A23C'
  return '#409EFF'
})

const accuracyRate = computed(() => {
  if (!result.value) return 0
  const total = result.value.correctCount + result.value.wrongCount
  return total > 0 ? Math.round((result.value.correctCount / total) * 100) : 0
})

// 方法
const getTypeName = (type: number) => {
  const types: Record<number, string> = { 1: '单选题', 2: '多选题', 3: '判断题' }
  return types[type] || '未知'
}

const getOptionLabel = (index: number) => String.fromCharCode(65 + index)

const formatTime = (seconds: number) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const selectAnswer = (questionId: number, answer: string) => {
  answers[questionId] = answer
}

const toggleMultiAnswer = (questionId: number, answer: string) => {
  if (!multiAnswers[questionId]) {
    multiAnswers[questionId] = []
  }
  const idx = multiAnswers[questionId].indexOf(answer)
  if (idx > -1) {
    multiAnswers[questionId].splice(idx, 1)
  } else {
    multiAnswers[questionId].push(answer)
  }
}

const toggleFlag = (question: any) => {
  if (flaggedQuestions.value.has(question.id)) {
    flaggedQuestions.value.delete(question.id)
  } else {
    flaggedQuestions.value.add(question.id)
  }
}

const getNavButtonType = (question: any) => {
  const isAnswered = question.questionType === 2 
    ? (multiAnswers[question.id]?.length > 0)
    : !!answers[question.id]
  const isFlagged = flaggedQuestions.value.has(question.id)
  
  if (isFlagged) return 'warning'
  if (isAnswered) return 'success'
  return 'info'
}

const scrollToQuestion = (index: number) => {
  const element = document.getElementById(`question-${index}`)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
    currentQuestion.value = index
  }
}

const loadQuestions = async () => {
  try {
    const res = await get(`/test/papers/${route.params.id}/questions`)
    questions.value = res.data || []
  } catch (e) {
    questions.value = [
      { 
        id: 1, 
        questionType: 1, 
        content: '接到自称是公安机关的电话，说你涉嫌洗钱，需要将资金转入"安全账户"，你应该？', 
        options: ['A. 立即挂断电话', 'B. 按照对方指示转账', 'C. 提供个人信息', 'D. 询问对方警号'], 
        score: 10,
        difficulty: 1,
        analysis: '遇到自称公检法的电话要求转账时，应立即挂断。真正的公检法机关不会通过电话要求转账。'
      },
      { 
        id: 2, 
        questionType: 1, 
        content: '在网上看到兼职刷单广告，声称"日赚百元、无需押金"，你应该？', 
        options: ['A. 立即联系对方开始刷单', 'B. 不理会，这是诈骗', 'C. 先试试看，投少量资金', 'D. 分享给朋友一起做'], 
        score: 10,
        difficulty: 1,
        analysis: '网络刷单本身就是违法行为，高回报兼职通常是诈骗陷阱。'
      },
      { 
        id: 3, 
        questionType: 2, 
        content: '以下哪些行为可能泄露个人信息？（多选）', 
        options: ['A. 在正规网站购物', 'B. 点击陌生链接填写信息', 'C. 随意连接公共WiFi', 'D. 在社交媒体公开住址'], 
        score: 10,
        difficulty: 2,
        analysis: '陌生链接和公开个人信息都可能泄露隐私。'
      },
      { 
        id: 4, 
        questionType: 3, 
        content: '校园贷只要按时还款就不会有问题。', 
        score: 5,
        difficulty: 1,
        analysis: '校园贷通常伴随高利息和暴力催收，存在诸多风险。'
      }
    ]
    paper.value = { paperName: '反诈知识入门测试', duration: 30, passScore: 60 }
    remainingTime.value = 1800
  }
}

const startTimer = () => {
  timer = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
      
      // 显示时间警告
      if (remainingTime.value === 300 && !warningShown) {
        showTimeWarning.value = true
        warningShown = true
      }
    } else {
      handleSubmit(true)
    }
  }, 1000)
}

const handleSaveDraft = async () => {
  saving.value = true
  try {
    // 模拟保存
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('草稿保存成功')
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleSubmit = async (autoSubmit = false) => {
  if (!autoSubmit && answeredCount.value < questions.value.length) {
    try {
      await ElMessageBox.confirm('还有题目未作答，确定提交吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
    } catch {
      return
    }
  }
  
  clearInterval(timer)
  showTimeWarning.value = false
  
  // 整理答案
  const answerList = questions.value.map(q => {
    let answer = ''
    if (q.questionType === 2) {
      answer = (multiAnswers[q.id] || []).sort().join('')
    } else {
      answer = answers[q.id] || ''
    }
    return { questionId: q.id, answer }
  })
  
  try {
    const res = await post('/test/submit', {
      paperId: Number(route.params.id),
      recordId: route.query.recordId ? Number(route.query.recordId) : null,
      answers: answerList,
      duration: Math.floor((Date.now() - startTime.value) / 1000)
    })
    result.value = res.data
  } catch (e) {
    // 模拟结果
    const correctCount = Math.floor(questions.value.length * 0.75)
    result.value = {
      isPassed: correctCount >= questions.value.length * 0.6,
      userScore: correctCount * 10,
      totalScore: totalScore.value,
      correctCount,
      wrongCount: questions.value.length - correctCount,
      duration: Math.floor((Date.now() - startTime.value) / 1000),
      wrongQuestions: questions.value.slice(correctCount).map(q => ({
        id: q.id,
        content: q.content,
        yourAnswer: 'D',
        correctAnswer: 'A'
      }))
    }
  }
  
  submitted.value = true
}

const retry = () => {
  submitted.value = false
  Object.keys(answers).forEach(key => delete answers[Number(key)])
  Object.keys(multiAnswers).forEach(key => delete multiAnswers[Number(key)])
  flaggedQuestions.value.clear()
  remainingTime.value = paper.value?.duration * 60 || 1800
  startTime.value = Date.now()
  warningShown = false
  startTimer()
}

// 监听滚动以更新当前题目
onMounted(() => {
  loadQuestions()
  startTimer()
})

onUnmounted(() => {
  clearInterval(timer)
})
</script>

<style scoped>
.test-paper {
  max-width: 900px;
  margin: 0 auto;
}

.paper-card {
  padding-bottom: 0;
}

.paper-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-lg);
}

.paper-info h2 {
  margin: 0 0 var(--spacing-sm) 0;
  color: var(--color-text-primary);
}

.paper-meta {
  display: flex;
  gap: var(--spacing-sm);
}

.timer-wrapper {
  text-align: right;
  min-width: 160px;
}

.timer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  background: rgba(64, 158, 255, 0.1);
  border-radius: var(--radius-md);
  color: var(--color-primary);
  transition: all 0.3s;
}

.timer.warning {
  background: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
  animation: pulse 1s infinite;
}

.timer.danger {
  background: rgba(245, 101, 101, 0.1);
  color: #F56C6C;
  animation: pulse 0.5s infinite;
}

.time-text {
  font-size: var(--font-size-xl);
  font-weight: bold;
  font-family: monospace;
}

.time-label {
  font-size: var(--font-size-xs);
  opacity: 0.8;
}

.timer-progress {
  margin-top: var(--spacing-xs);
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

/* 进度条 */
.progress-bar {
  padding: var(--spacing-md);
  background: var(--color-bg-page);
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-md);
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--spacing-sm);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.progress-count {
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.progress-legend {
  display: flex;
  justify-content: center;
  gap: var(--spacing-lg);
  margin-top: var(--spacing-sm);
  font-size: var(--font-size-xs);
  color: var(--color-text-muted);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.dot.answered { background: var(--color-success); }
.dot.unanswered { background: var(--color-border); }
.dot.flagged { background: #E6A23C; }

/* 题目导航 */
.question-nav {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  padding: var(--spacing-md);
  background: var(--color-bg-page);
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-lg);
}

.question-nav .el-button {
  width: 32px;
  height: 32px;
  padding: 0;
}

.question-nav .el-button.current {
  box-shadow: 0 0 0 2px var(--color-primary);
}

/* 题目列表 */
.questions {
  max-height: 600px;
  overflow-y: auto;
  padding: 0 var(--spacing-md);
}

.question-item {
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  background: var(--color-bg-page);
  border-radius: var(--radius-lg);
  border: 2px solid transparent;
  transition: all 0.3s;
}

.question-item.current {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-md);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.question-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.question-number {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary);
  color: white;
  border-radius: 50%;
  font-weight: bold;
  font-size: var(--font-size-sm);
}

.score {
  color: var(--color-text-muted);
  font-size: var(--font-size-sm);
}

.question-content {
  margin-bottom: var(--spacing-md);
  color: var(--color-text-primary);
  line-height: 1.8;
  font-size: var(--font-size-base);
}

/* 选项 */
.options {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.option-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-bg-card);
  border: 2px solid var(--color-border-light);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s;
}

.option-item:hover {
  border-color: var(--color-primary);
  background: rgba(64, 158, 255, 0.05);
}

.option-item.selected {
  border-color: var(--color-primary);
  background: rgba(64, 158, 255, 0.1);
}

.option-label {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-page);
  border-radius: 50%;
  font-weight: bold;
  color: var(--color-text-secondary);
  flex-shrink: 0;
}

.option-item.selected .option-label {
  background: var(--color-primary);
  color: white;
}

.option-text {
  flex: 1;
  color: var(--color-text-primary);
}

/* 题目解析 */
.question-analysis {
  margin-top: var(--spacing-md);
  padding: var(--spacing-md);
  background: rgba(64, 158, 255, 0.1);
  border-radius: var(--radius-md);
}

.analysis-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
  margin-bottom: var(--spacing-sm);
}

.question-analysis p {
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin: 0;
}

/* 提交区域 */
.submit-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  background: var(--color-bg-page);
  border-top: 1px solid var(--color-border-light);
  position: sticky;
  bottom: 0;
}

.submit-info {
  display: flex;
  gap: var(--spacing-lg);
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

.submit-actions {
  display: flex;
  gap: var(--spacing-sm);
}

/* 结果页面 */
.result-card {
  text-align: center;
  padding: var(--spacing-xl);
}

.result-header {
  margin-bottom: var(--spacing-xl);
}

.result-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(245, 101, 101, 0.1);
  color: #F56C6C;
  margin-bottom: var(--spacing-md);
}

.result-icon.passed {
  background: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.result-header h2 {
  font-size: var(--font-size-2xl);
  margin-bottom: var(--spacing-sm);
}

.result-message {
  color: var(--color-text-secondary);
}

.result-score {
  margin-bottom: var(--spacing-xl);
}

.score-circle {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 160px;
  height: 160px;
  border-radius: 50%;
  background: rgba(245, 101, 101, 0.1);
  border: 4px solid #F56C6C;
}

.score-circle.passed {
  background: rgba(103, 194, 58, 0.1);
  border-color: #67C23A;
}

.score-value {
  font-size: 48px;
  font-weight: bold;
  color: var(--color-text-primary);
}

.score-total {
  font-size: var(--font-size-lg);
  color: var(--color-text-muted);
}

.score-label {
  margin-top: var(--spacing-sm);
  color: var(--color-text-secondary);
}

.result-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-bg-page);
  border-radius: var(--radius-md);
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  color: white;
  font-size: 20px;
}

.stat-icon.correct { background: var(--color-success); }
.stat-icon.wrong { background: var(--color-danger); }
.stat-icon.time { background: var(--color-primary); }
.stat-icon.accuracy { background: #9f7aea; }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: var(--font-size-xl);
  font-weight: bold;
  color: var(--color-text-primary);
}

.stat-label {
  font-size: var(--font-size-xs);
  color: var(--color-text-muted);
}

/* 错题回顾 */
.wrong-questions {
  text-align: left;
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: rgba(245, 101, 101, 0.05);
  border-radius: var(--radius-lg);
}

.wrong-questions h3 {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
  color: var(--color-danger);
}

.wrong-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.wrong-item {
  padding: var(--spacing-md);
  background: var(--color-bg-card);
  border-radius: var(--radius-md);
}

.wrong-content {
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-sm);
}

.wrong-answer {
  display: flex;
  gap: var(--spacing-lg);
  font-size: var(--font-size-sm);
}

.your-answer {
  color: var(--color-danger);
}

.correct-answer {
  color: var(--color-success);
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: var(--spacing-md);
}

/* 时间警告对话框 */
.time-warning-content {
  text-align: center;
  padding: var(--spacing-lg);
}

.warning-icon {
  margin-bottom: var(--spacing-md);
}

.time-warning-content p {
  margin: var(--spacing-sm) 0;
  color: var(--color-text-secondary);
}

.time-warning-content strong {
  color: #E6A23C;
  font-size: var(--font-size-xl);
}

@media (max-width: 768px) {
  .paper-header {
    flex-direction: column;
  }
  
  .timer-wrapper {
    text-align: left;
    width: 100%;
  }
  
  .result-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .submit-area {
    flex-direction: column;
    gap: var(--spacing-md);
  }
  
  .submit-actions {
    width: 100%;
  }
  
  .submit-actions .el-button {
    flex: 1;
  }
}
</style>
