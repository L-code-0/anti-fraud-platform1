<template>
  <div class="simulation-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
      </div>
      <div class="header-content">
        <h1>情景演练中心</h1>
        <p>模拟真实诈骗场景，实战提升防骗能力</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 演练类型 -->
      <div class="simulation-types">
        <div class="type-card" v-for="type in types" :key="type.id">
          <div class="type-icon" :style="{ background: type.gradient }">
            <el-icon><component :is="type.icon" /></el-icon>
          </div>
          <div class="type-info">
            <h3>{{ type.title }}</h3>
            <p>{{ type.desc }}</p>
          </div>
          <div class="type-stats">
            <span>{{ type.count }}个场景</span>
          </div>
        </div>
      </div>

      <!-- 热门场景 -->
      <div class="scenes-section">
        <div class="section-header">
          <h2>热门演练场景</h2>
          <span class="scene-count">共{{ scenes.length }}个场景</span>
        </div>

        <div class="scenes-grid">
          <div
            class="scene-card"
            v-for="scene in scenes"
            :key="scene.id"
            @click="startSimulation(scene)"
          >
            <div class="scene-image">
              <img :src="scene.cover" :alt="scene.title" />
              <div class="scene-overlay">
                <el-button type="primary" size="large">
                  <el-icon><VideoPlay /></el-icon>
                  开始演练
                </el-button>
              </div>
              <div class="scene-difficulty" :class="'level-' + scene.level">
                {{ scene.levelName }}
              </div>
            </div>
            <div class="scene-content">
              <h3>{{ scene.title }}</h3>
              <p>{{ scene.desc }}</p>
              <div class="scene-meta">
                <div class="meta-left">
                  <span><el-icon><User /></el-icon> {{ scene.tried }}人演练</span>
                  <span><el-icon><Star /></el-icon> {{ scene.rating }}</span>
                </div>
                <span class="scene-duration">
                  <el-icon><Clock /></el-icon> {{ scene.duration }}分钟
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 演练流程 -->
      <div class="process-section">
        <div class="section-header">
          <h2>演练流程</h2>
          <p>了解完整的演练过程</p>
        </div>

        <div class="process-timeline">
          <div class="process-step" v-for="(step, index) in processSteps" :key="step.title">
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-content">
              <h4>{{ step.title }}</h4>
              <p>{{ step.desc }}</p>
            </div>
            <div class="step-arrow" v-if="index < processSteps.length - 1">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 演练对话框 -->
    <el-dialog
      v-model="showSimulation"
      :title="currentScene?.title"
      width="700px"
      :close-on-click-modal="false"
      class="simulation-dialog"
    >
      <div class="simulation-content" v-if="currentScene">
        <div class="simulation-chat">
          <div class="chat-header">
            <div class="chat-avatar">
              <img :src="currentScene.avatar" alt="诈骗者" />
            </div>
            <div class="chat-info">
              <span class="chat-name">{{ currentScene.scammerName }}</span>
              <span class="chat-status">
                <span class="status-dot"></span> 在线
              </span>
            </div>
          </div>

          <div class="chat-messages" ref="chatMessagesRef">
            <div
              class="message"
              :class="msg.type"
              v-for="(msg, index) in currentMessages"
              :key="index"
            >
              <div class="message-content">{{ msg.content }}</div>
            </div>
          </div>

          <div class="chat-input">
            <el-input
              v-model="userInput"
              placeholder="输入你的回复..."
              @keyup.enter="sendMessage"
            >
              <template #append>
                <el-button @click="sendMessage" :disabled="!userInput.trim()">
                  <el-icon><Promotion /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
        </div>

        <div class="simulation-sidebar">
          <h4>选择策略</h4>
          <div class="strategy-list">
            <div
              class="strategy-item"
              v-for="strategy in strategies"
              :key="strategy.id"
              :class="{ active: selectedStrategy === strategy.id }"
              @click="selectStrategy(strategy.id)"
            >
              <el-icon><component :is="strategy.icon" /></el-icon>
              <span>{{ strategy.name }}</span>
            </div>
          </div>

          <div class="tips-section">
            <h5>
              <el-icon><InfoFilled /></el-icon>
              防骗提示
            </h5>
            <p>{{ currentTip }}</p>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  VideoPlay, User, Star, Clock, ArrowRight, Promotion,
  Phone, Message, ShoppingCart, Money, Gift, InfoFilled,
  CircleCheck, WarnTriangleFilled, Comment
} from '@element-plus/icons-vue'

const types = [
  { id: 1, icon: 'Phone', title: '电话诈骗', desc: '模拟冒充客服、公检法等电话诈骗', count: 12, gradient: 'var(--gradient-primary)' },
  { id: 2, icon: 'Message', title: '社交诈骗', desc: '模拟杀猪盘、网络交友等诈骗', count: 8, gradient: 'var(--gradient-success)' },
  { id: 3, icon: 'ShoppingCart', title: '网购诈骗', desc: '模拟虚假购物、退款诈骗等', count: 10, gradient: 'var(--gradient-warning)' },
  { id: 4, icon: 'Money', title: '金融诈骗', desc: '模拟投资理财、贷款诈骗等', count: 6, gradient: 'var(--gradient-danger)' }
]

const scenes = ref([
  {
    id: 1, title: '冒充客服注销会员诈骗', desc: '学习如何识别假冒客服的诈骗电话',
    cover: 'https://picsum.photos/seed/scene1/400/250',
    level: 1, levelName: '入门',
    duration: 8, tried: 1234, rating: 4.8,
    scammerName: '电商客服-小王',
    avatar: 'https://picsum.photos/seed/avatar1/100/100'
  },
  {
    id: 2, title: '杀猪盘诈骗情景模拟', desc: '深度体验杀猪盘的完整诈骗流程',
    cover: 'https://picsum.photos/seed/scene2/400/250',
    level: 3, levelName: '进阶',
    duration: 15, tried: 856, rating: 4.9,
    scammerName: '知心爱人',
    avatar: 'https://picsum.photos/seed/avatar2/100/100'
  },
  {
    id: 3, title: '虚假购物诈骗演练', desc: '识别网购中的各种陷阱',
    cover: 'https://picsum.photos/seed/scene3/400/250',
    level: 2, levelName: '基础',
    duration: 10, tried: 967, rating: 4.6,
    scammerName: '良心卖家',
    avatar: 'https://picsum.photos/seed/avatar3/100/100'
  },
  {
    id: 4, title: '投资理财诈骗体验', desc: '警惕高回报投资背后的陷阱',
    cover: 'https://picsum.photos/seed/scene4/400/250',
    level: 3, levelName: '进阶',
    duration: 12, tried: 654, rating: 4.7,
    scammerName: '专业导师',
    avatar: 'https://picsum.photos/seed/avatar4/100/100'
  }
])

const processSteps = [
  { title: '选择场景', desc: '选择想要演练的诈骗类型' },
  { title: '开始对话', desc: '模拟与诈骗者对话' },
  { title: '做出选择', desc: '根据提示做出判断' },
  { title: '获得反馈', desc: '了解正确应对方式' },
  { title: '总结学习', desc: '回顾演练要点' }
]

const strategies = [
  { id: 1, name: '直接拒绝', icon: 'CircleCheck' },
  { id: 2, name: '谨慎询问', icon: 'Comment' },
  { id: 3, name: '保持冷静', icon: 'WarnTriangleFilled' },
  { id: 4, name: '挂断举报', icon: 'Phone' }
]

const tips = [
  '正规客服不会要求转账到指定账户',
  '不要轻易透露验证码和密码',
  '遇到紧急情况要先核实身份',
  '保持冷静，不要被对方催促'
]

const showSimulation = ref(false)
const currentScene = ref<any>(null)
const userInput = ref('')
const currentMessages = ref<any[]>([])
const selectedStrategy = ref(1)
const currentTip = ref(tips[0])

const currentMessagesData: Record<number, any[]> = {
  1: [
    { type: 'received', content: '您好，我是XX电商客服，您在我们平台开通了会员，现在可以帮助您注销会员，否则会影响您的征信...' },
    { type: 'received', content: '请告诉我您的身份证号和银行卡号，我帮您操作...' }
  ],
  2: [
    { type: 'received', content: '嗨，你好呀~最近工作压力大吗？' },
    { type: 'received', content: '我最近在做一个投资理财项目，收益很不错，要不要一起？' }
  ]
}

const startSimulation = (scene: any) => {
  currentScene.value = scene
  currentMessages.value = currentMessagesData[scene.id] || [
    { type: 'received', content: '你好，我是...' }
  ]
  showSimulation.value = true
  currentTip.value = tips[Math.floor(Math.random() * tips.length)]
}

const sendMessage = () => {
  if (!userInput.value.trim()) return

  currentMessages.value.push({
    type: 'sent',
    content: userInput.value
  })

  setTimeout(() => {
    currentMessages.value.push({
      type: 'received',
      content: getAutoReply()
    })
  }, 1000)

  userInput.value = ''
}

const getAutoReply = () => {
  const replies = [
    '请您配合我们的操作，否则后果自负...',
    '这个是为了保护您的账户安全...',
    '请您尽快处理，否则会有更大损失...'
  ]
  return replies[Math.floor(Math.random() * replies.length)]
}

const selectStrategy = (id: number) => {
  selectedStrategy.value = id
}
</script>

<style scoped>
.simulation-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

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
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 50%, #c084fc 100%);
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

.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6) var(--spacing-12);
}

/* 演练类型 */
.simulation-types {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-4);
  margin-top: calc(-1 * var(--spacing-10));
  margin-bottom: var(--spacing-10);
}

.type-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-5);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

.type-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
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
  margin-bottom: var(--spacing-3);
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
  margin-bottom: var(--spacing-3);
}

.type-stats span {
  padding: var(--spacing-1) var(--spacing-3);
  background: var(--bg-secondary);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

/* 场景区域 */
.scenes-section {
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

.scene-count {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.scenes-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-6);
}

.scene-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.scene-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

.scene-card:hover .scene-overlay {
  opacity: 1;
}

.scene-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.scene-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.scene-card:hover .scene-image img {
  transform: scale(1.1);
}

.scene-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.scene-difficulty {
  position: absolute;
  top: var(--spacing-3);
  right: var(--spacing-3);
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: white;
}

.scene-difficulty.level-1 { background: var(--success-color); }
.scene-difficulty.level-2 { background: var(--info-color); }
.scene-difficulty.level-3 { background: var(--warning-color); }

.scene-content {
  padding: var(--spacing-4);
}

.scene-content h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.scene-content p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-3);
}

.scene-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta-left {
  display: flex;
  gap: var(--spacing-4);
}

.meta-left span,
.scene-duration {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

/* 流程区域 */
.process-section {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-8);
  box-shadow: var(--shadow-md);
}

.process-section .section-header {
  margin-bottom: var(--spacing-8);
}

.process-section .section-header p {
  color: var(--text-secondary);
}

.process-timeline {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.process-step {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.step-number {
  width: 48px;
  height: 48px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: white;
  margin-bottom: var(--spacing-3);
  box-shadow: var(--shadow-primary);
}

.step-content {
  text-align: center;
}

.step-content h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.step-content p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.step-arrow {
  position: absolute;
  top: 24px;
  right: -10%;
  color: var(--primary-color);
  font-size: 20px;
}

/* 演练对话框 */
.simulation-content {
  display: grid;
  grid-template-columns: 1fr 240px;
  gap: var(--spacing-4);
  height: 500px;
}

.simulation-chat {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  padding: var(--spacing-4);
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-primary);
}

.chat-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
}

.chat-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.chat-info {
  display: flex;
  flex-direction: column;
}

.chat-name {
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.chat-status {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.status-dot {
  width: 8px;
  height: 8px;
  background: var(--success-color);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.chat-messages {
  flex: 1;
  padding: var(--spacing-4);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
}

.message {
  display: flex;
  max-width: 80%;
}

.message.received {
  align-self: flex-start;
}

.message.sent {
  align-self: flex-end;
}

.message-content {
  padding: var(--spacing-3) var(--spacing-4);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  line-height: 1.5;
}

.message.received .message-content {
  background: var(--bg-primary);
  color: var(--text-primary);
  border-bottom-left-radius: var(--radius-sm);
}

.message.sent .message-content {
  background: var(--gradient-primary);
  color: white;
  border-bottom-right-radius: var(--radius-sm);
}

.chat-input {
  padding: var(--spacing-3);
  background: var(--bg-primary);
  border-top: 1px solid var(--border-primary);
}

.simulation-sidebar {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  padding: var(--spacing-4);
}

.simulation-sidebar h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-3);
}

.strategy-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-5);
}

.strategy-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-3);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.strategy-item:hover {
  background: var(--bg-hover);
  color: var(--primary-color);
}

.strategy-item.active {
  background: var(--primary-bg);
  color: var(--primary-color);
  font-weight: var(--font-weight-medium);
}

.tips-section {
  background: var(--warning-bg);
  border-radius: var(--radius-md);
  padding: var(--spacing-3);
}

.tips-section h5 {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--warning-color);
  margin-bottom: var(--spacing-2);
}

.tips-section p {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  line-height: 1.5;
}

/* 响应式 */
@media (max-width: 1024px) {
  .simulation-types {
    grid-template-columns: repeat(2, 1fr);
  }

  .simulation-content {
    grid-template-columns: 1fr;
    height: auto;
  }

  .simulation-sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: var(--spacing-10) var(--spacing-4);
  }

  .header-content h1 {
    font-size: var(--font-size-2xl);
  }

  .simulation-types {
    grid-template-columns: 1fr;
    margin-top: 0;
  }

  .scenes-grid {
    grid-template-columns: 1fr;
  }

  .process-timeline {
    flex-direction: column;
    gap: var(--spacing-4);
  }

  .step-arrow {
    display: none;
  }
}
</style>
