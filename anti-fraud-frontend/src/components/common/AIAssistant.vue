<template>
  <div class="ai-assistant">
    <!-- 聊天窗口 -->
    <div class="chat-window" v-if="showChat">
      <div class="chat-header">
        <div class="header-content">
          <div class="avatar">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="title">
            <h3>防骗AI助手</h3>
            <p>智能解答防骗知识</p>
          </div>
        </div>
        <el-button 
          circle 
          size="small" 
          @click="showChat = false"
          class="close-btn"
        >
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
      
      <div class="chat-body">
        <!-- 欢迎消息 -->
        <div v-if="messages.length === 0" class="welcome-message">
          <el-icon class="welcome-icon"><ChatLineRound /></el-icon>
          <h4>您好！我是防骗AI助手</h4>
          <p>有什么防骗问题可以随时问我</p>
          <div class="quick-questions">
            <el-button 
              v-for="(question, index) in quickQuestions" 
              :key="index"
              size="small"
              @click="handleQuickQuestion(question)"
            >
              {{ question }}
            </el-button>
          </div>
        </div>
        
        <!-- 聊天记录 -->
        <div v-else class="messages">
          <div 
            v-for="(message, index) in messages" 
            :key="index"
            :class="['message-item', message.type]"
          >
            <div class="message-avatar">
              <el-icon v-if="message.type === 'user'">
                <UserFilled />
              </el-icon>
              <el-icon v-else>
                <ChatDotRound />
              </el-icon>
            </div>
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
          <div v-if="loading" class="loading-message">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>思考中...</span>
          </div>
        </div>
      </div>
      
      <div class="chat-footer">
        <el-input
          v-model="inputMessage"
          placeholder="请输入您的问题..."
          @keyup.enter="handleSend"
          :disabled="loading"
        >
          <template #append>
            <el-button 
              @click="handleSend" 
              :loading="loading"
              :disabled="!inputMessage.trim() || loading"
            >
              发送
            </el-button>
          </template>
        </el-input>
        <div class="chat-actions">
          <el-button 
            size="small" 
            @click="clearMessages"
            v-if="messages.length > 0"
          >
            清空
          </el-button>
          <el-button 
            size="small" 
            @click="showHistory = !showHistory"
          >
            {{ showHistory ? '隐藏历史' : '历史记录' }}
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 历史记录面板 -->
    <div class="history-panel" v-if="showHistory">
      <div class="history-header">
        <h4>历史记录</h4>
        <el-button 
          circle 
          size="small" 
          @click="showHistory = false"
        >
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
      <div class="history-list">
        <el-empty v-if="history.length === 0" description="暂无历史记录" />
        <div 
          v-else 
          v-for="(item, index) in history" 
          :key="index"
          class="history-item"
        >
          <div class="history-question">{{ item.question }}</div>
          <div class="history-answer">{{ item.answer }}</div>
          <div class="history-time">{{ item.createTime }}</div>
        </div>
      </div>
    </div>
    
    <!-- 悬浮按钮 -->
    <div class="floating-btn" @click="showChat = !showChat">
      <el-icon :class="{ active: showChat }"><ChatDotRound /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { post, get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Close, UserFilled, Loading, ChatLineRound } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 聊天状态
const showChat = ref(false)
const showHistory = ref(false)
const inputMessage = ref('')
const loading = ref(false)
const messages = ref<any[]>([])
const history = ref<any[]>([])

// 快速问题
const quickQuestions = [
  '什么是电话诈骗？',
  '如何防范网络诈骗？',
  '遭遇诈骗后怎么办？',
  '常见的诈骗类型有哪些？'
]

// 发送消息
const handleSend = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  
  const question = inputMessage.value.trim()
  
  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: question,
    time: new Date().toLocaleTimeString()
  })
  
  inputMessage.value = ''
  loading.value = true
  
  try {
    const res = await post('/ai/ask', {
      question: question,
      category: '反诈知识'
    })
    
    if (res.code === 200 && res.data) {
      // 添加AI回复
      messages.value.push({
        type: 'ai',
        content: res.data.answer,
        time: new Date().toLocaleTimeString()
      })
    } else {
      messages.value.push({
        type: 'ai',
        content: '抱歉，我暂时无法回答这个问题，请稍后再试。',
        time: new Date().toLocaleTimeString()
      })
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    messages.value.push({
      type: 'ai',
      content: '网络错误，请稍后再试。',
      time: new Date().toLocaleTimeString()
    })
  } finally {
    loading.value = false
  }
}

// 处理快速问题
const handleQuickQuestion = (question: string) => {
  inputMessage.value = question
  handleSend()
}

// 清空消息
const clearMessages = () => {
  messages.value = []
}

// 获取历史记录
const loadHistory = async () => {
  if (!userStore.isLoggedIn) return
  
  try {
    const res = await get('/ai/history')
    if (res.code === 200 && res.data) {
      history.value = res.data
    }
  } catch (error) {
    console.error('获取历史记录失败:', error)
  }
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
}

/* 悬浮按钮 */
.floating-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: var(--color-primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: var(--shadow-lg);
  transition: all 0.3s ease;
  font-size: 24px;
}

.floating-btn:hover,
.floating-btn.active {
  transform: scale(1.1);
  background: var(--color-primary-light);
}

/* 聊天窗口 */
.chat-window {
  position: absolute;
  bottom: 70px;
  right: 0;
  width: 400px;
  height: 500px;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 聊天头部 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md);
  background: var(--color-primary);
  color: white;
  border-bottom: 1px solid var(--color-border);
}

.header-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.title h3 {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-bold);
}

.title p {
  margin: 0;
  font-size: var(--font-size-sm);
  opacity: 0.8;
}

.close-btn {
  color: white;
  border: none;
}

/* 聊天主体 */
.chat-body {
  flex: 1;
  padding: var(--spacing-md);
  overflow-y: auto;
  background: var(--color-bg-page);
}

/* 欢迎消息 */
.welcome-message {
  text-align: center;
  padding: var(--spacing-xl) var(--spacing-md);
  color: var(--color-text-secondary);
}

.welcome-icon {
  font-size: 48px;
  color: var(--color-primary);
  margin-bottom: var(--spacing-md);
}

.welcome-message h4 {
  margin: 0 0 var(--spacing-sm);
  color: var(--color-text-primary);
}

.welcome-message p {
  margin: 0 0 var(--spacing-lg);
}

.quick-questions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  align-items: center;
}

.quick-questions .el-button {
  width: 100%;
  max-width: 200px;
  text-align: left;
}

/* 消息列表 */
.messages {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.message-item {
  display: flex;
  gap: var(--spacing-sm);
  max-width: 80%;
}

.message-item.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--color-bg-page);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 16px;
}

.message-item.user .message-avatar {
  background: var(--color-primary);
  color: white;
}

.message-content {
  flex: 1;
}

.message-text {
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-lg);
  line-height: 1.5;
}

.message-item.user .message-text {
  background: var(--color-primary);
  color: white;
  border-bottom-right-radius: var(--radius-sm);
}

.message-item.ai .message-text {
  background: white;
  color: var(--color-text-primary);
  border: 1px solid var(--color-border);
  border-bottom-left-radius: var(--radius-sm);
}

.message-time {
  font-size: var(--font-size-xs);
  color: var(--color-text-muted);
  margin-top: 4px;
  text-align: right;
}

.message-item.ai .message-time {
  text-align: left;
}

/* 加载消息 */
.loading-message {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

/* 聊天底部 */
.chat-footer {
  padding: var(--spacing-md);
  border-top: 1px solid var(--color-border);
  background: white;
}

.chat-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-sm);
}

/* 历史记录面板 */
.history-panel {
  position: absolute;
  bottom: 70px;
  right: 420px;
  width: 300px;
  height: 500px;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md);
  background: var(--color-primary);
  color: white;
  border-bottom: 1px solid var(--color-border);
}

.history-header h4 {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-bold);
}

.history-list {
  flex: 1;
  padding: var(--spacing-md);
  overflow-y: auto;
  background: var(--color-bg-page);
}

.history-item {
  padding: var(--spacing-sm);
  background: white;
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-sm);
  border: 1px solid var(--color-border);
}

.history-question {
  font-weight: var(--font-weight-medium);
  margin-bottom: var(--spacing-xs);
  color: var(--color-text-primary);
}

.history-answer {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-xs);
  line-height: 1.4;
}

.history-time {
  font-size: var(--font-size-xs);
  color: var(--color-text-muted);
  text-align: right;
}

/* 响应式 */
@media (max-width: 768px) {
  .chat-window {
    width: 300px;
    height: 400px;
  }
  
  .history-panel {
    right: 0;
    bottom: 480px;
    width: 300px;
    height: 300px;
  }
}
</style>