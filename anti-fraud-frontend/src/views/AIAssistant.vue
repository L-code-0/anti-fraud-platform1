<template>
  <div class="ai-assistant">
    <!-- 头部 -->
    <div class="header">
      <h1>AI智能问答</h1>
      <p>厦门防骗AI助手 - 支持多轮对话+情景演练联动</p>
    </div>

    <!-- 功能区域 -->
    <div class="function-area">
      <!-- 对话区域 -->
      <div class="chat-section">
        <div class="chat-header">
          <h2>智能对话</h2>
          <el-button type="primary" @click="clearChat">清空对话</el-button>
        </div>
        <div class="chat-body" ref="chatBody">
          <div 
            v-for="(message, index) in messages" 
            :key="index"
            :class="['message-item', message.role]"
          >
            <div class="message-avatar">
              <img 
                v-if="message.role === 'user'" 
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20icon%20friendly%20person&image_size=square" 
                alt="User"
              />
              <img 
                v-else 
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=AI%20assistant%20avatar%20icon%20robot%20friendly&image_size=square" 
                alt="AI"
              />
            </div>
            <div class="message-content">
              <div class="message-text" v-html="message.content"></div>
              <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入您的问题，例如：如何识别电信诈骗？"
            @keyup.enter.exact="sendMessage"
          />
          <div class="input-actions">
            <el-button @click="sendMessage" type="primary" :loading="isSending">
              <el-icon><Send /></el-icon>
              发送
            </el-button>
            <el-button @click="openScenario">
              <el-icon><PlayCircle /></el-icon>
              情景演练
            </el-button>
          </div>
        </div>
      </div>

      <!-- 快捷问题 -->
      <div class="quick-questions">
        <h3>常见问题</h3>
        <el-button 
          v-for="question in quickQuestions" 
          :key="question"
          type="info"
          plain
          @click="quickQuestion(question)"
        >
          {{ question }}
        </el-button>
      </div>
    </div>

    <!-- 情景演练对话框 -->
    <el-dialog v-model="scenarioVisible" title="情景演练" width="800px">
      <div class="scenario-content">
        <div class="scenario-header">
          <h3>{{ currentScenario?.title }}</h3>
          <p>{{ currentScenario?.description }}</p>
        </div>
        <div class="scenario-body">
          <div class="scenario-narrative">{{ currentScenario?.narrative }}</div>
          <div class="scenario-options">
            <el-button 
              v-for="(option, index) in currentScenario?.options" 
              :key="index"
              type="primary"
              plain
              @click="selectOption(index)"
            >
              {{ option }}
            </el-button>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scenarioVisible = false">关闭</el-button>
          <el-button type="primary" @click="restartScenario">重新开始</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Send, PlayCircle } from '@element-plus/icons-vue'

const messages = ref<any[]>([])
const inputMessage = ref('')
const isSending = ref(false)
const chatBody = ref<HTMLElement | null>(null)
const scenarioVisible = ref(false)

const quickQuestions = [
  '如何识别电信诈骗？',
  '遇到网络诈骗怎么办？',
  '什么是杀猪盘诈骗？',
  '如何防范校园贷诈骗？',
  '兼职刷单是诈骗吗？'
]

const scenarios = [
  {
    id: 1,
    title: '电信诈骗',
    description: '模拟接到自称公检法电话的情景',
    narrative: '你接到一个自称是公安局的电话，对方说你涉及一起洗钱案件，需要你提供银行卡信息进行调查。',
    options: [
      '提供银行卡信息',
      '要求对方出示证件',
      '挂断电话并报警',
      '询问具体案件详情'
    ],
    outcomes: [
      {
        result: '危险',
        message: '你提供了银行卡信息，可能导致资金被盗。正确做法是挂断电话并报警。',
        suggestion: '遇到自称公检法的电话，不要轻信，应挂断后通过官方渠道核实。'
      },
      {
        result: '谨慎',
        message: '要求对方出示证件是正确的，但对方可能会伪造证件。',
        suggestion: '即使对方出示证件，也不要轻信，应通过官方渠道核实。'
      },
      {
        result: '正确',
        message: '挂断电话并报警是最安全的做法。',
        suggestion: '遇到可疑电话，应及时报警，不要与对方纠缠。'
      },
      {
        result: '危险',
        message: '与对方过多纠缠可能会被对方话术迷惑。',
        suggestion: '遇到可疑电话，应直接挂断并报警。'
      }
    ]
  },
  {
    id: 2,
    title: '网络兼职诈骗',
    description: '模拟遇到刷单兼职的情景',
    narrative: '你在网上看到一个刷单兼职的广告，声称只要完成任务就能获得高额报酬。',
    options: [
      '立即报名参加',
      '查看详细要求',
      '搜索相关信息',
      '直接忽略'
    ],
    outcomes: [
      {
        result: '危险',
        message: '立即报名参加可能会陷入刷单诈骗的陷阱。',
        suggestion: '刷单本身就是违法行为，遇到此类广告应直接忽略。'
      },
      {
        result: '危险',
        message: '查看详细要求可能会被对方的话术迷惑。',
        suggestion: '刷单兼职都是诈骗，不要相信任何形式的刷单广告。'
      },
      {
        result: '正确',
        message: '搜索相关信息可以了解到这是一种常见的诈骗手段。',
        suggestion: '遇到可疑兼职，应先搜索相关信息，了解其真实性。'
      },
      {
        result: '正确',
        message: '直接忽略是最安全的做法。',
        suggestion: '遇到可疑广告，应直接忽略，不要点击任何链接。'
      }
    ]
  },
  {
    id: 3,
    title: '校园贷诈骗',
    description: '模拟遇到校园贷的情景',
    narrative: '你急需用钱，看到校园里的贷款广告，声称无抵押、低利息。',
    options: [
      '联系对方办理贷款',
      '咨询学校老师',
      '向家人朋友借钱',
      '忽略广告'
    ],
    outcomes: [
      {
        result: '危险',
        message: '联系对方办理贷款可能会陷入校园贷的陷阱。',
        suggestion: '校园贷往往存在高额利息和暴力催收，应避免接触。'
      },
      {
        result: '正确',
        message: '咨询学校老师可以获得正确的建议。',
        suggestion: '遇到资金困难，应向学校或正规金融机构寻求帮助。'
      },
      {
        result: '正确',
        message: '向家人朋友借钱是相对安全的方式。',
        suggestion: '遇到资金困难，应优先向家人朋友求助。'
      },
      {
        result: '正确',
        message: '忽略广告是最安全的做法。',
        suggestion: '遇到可疑贷款广告，应直接忽略。'
      }
    ]
  }
]

const currentScenario = ref<any>(null)

function formatTime(timestamp: number): string {
  return new Date(timestamp).toLocaleTimeString()
}

function scrollToBottom() {
  nextTick(() => {
    if (chatBody.value) {
      chatBody.value.scrollTop = chatBody.value.scrollHeight
    }
  })
}

function sendMessage() {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入问题')
    return
  }

  const userMessage = {
    role: 'user',
    content: inputMessage.value,
    timestamp: Date.now()
  }
  messages.value.push(userMessage)
  inputMessage.value = ''
  scrollToBottom()

  isSending.value = true
  // 模拟AI回复
  setTimeout(() => {
    const aiMessage = {
      role: 'assistant',
      content: getAIResponse(userMessage.content),
      timestamp: Date.now()
    }
    messages.value.push(aiMessage)
    isSending.value = false
    scrollToBottom()
  }, 1000)
}

function getAIResponse(question: string): string {
  const responses: Record<string, string> = {
    '如何识别电信诈骗？': '识别电信诈骗的方法包括：1. 注意来电显示，警惕陌生号码；2. 不要轻信自称公检法的电话；3. 不要向陌生人透露个人信息和银行卡信息；4. 不要点击陌生链接；5. 遇到可疑情况及时报警。',
    '遇到网络诈骗怎么办？': '遇到网络诈骗应立即：1. 停止与对方的一切联系；2. 保存相关证据，如聊天记录、转账记录等；3. 立即报警；4. 联系银行冻结账户；5. 通知亲友，防止二次诈骗。',
    '什么是杀猪盘诈骗？': '杀猪盘是一种新型网络诈骗方式，骗子通过网络交友获取信任，然后诱导受害者参与网络赌博、投资等活动，最终骗取钱财。防范措施包括：不要轻信网络交友，不要参与陌生的投资活动，不要向陌生人转账。',
    '如何防范校园贷诈骗？': '防范校园贷诈骗的方法包括：1. 树立正确的消费观念，避免过度消费；2. 遇到资金困难向学校或正规金融机构寻求帮助；3. 不要相信无抵押、低利息的贷款广告；4. 了解相关法律法规，增强法律意识。',
    '兼职刷单是诈骗吗？': '是的，兼职刷单是一种常见的诈骗手段。骗子以高额报酬为诱饵，要求受害者先垫付资金，然后以各种理由拒绝返款。刷单本身也是违法行为，遇到此类广告应直接忽略。'
  }

  return responses[question] || '感谢您的提问。关于反诈知识，我可以为您提供专业的解答。您可以问我如何识别诈骗、遇到诈骗怎么办等问题。'
}

function quickQuestion(question: string) {
  inputMessage.value = question
  sendMessage()
}

function clearChat() {
  messages.value = []
}

function openScenario() {
  currentScenario.value = scenarios[0]
  scenarioVisible.value = true
}

function selectOption(index: number) {
  const outcome = currentScenario.value?.outcomes[index]
  if (outcome) {
    ElMessage[outcome.result === '正确' ? 'success' : 'error']({
      title: outcome.result,
      message: outcome.message,
      description: outcome.suggestion,
      duration: 5000
    })
  }
}

function restartScenario() {
  currentScenario.value = scenarios[Math.floor(Math.random() * scenarios.length)]
}

onMounted(() => {
  // 初始欢迎消息
  const welcomeMessage = {
    role: 'assistant',
    content: '您好！我是厦门防骗AI助手，很高兴为您服务。您可以问我关于反诈的任何问题，也可以参与情景演练来提高防范意识。',
    timestamp: Date.now()
  }
  messages.value.push(welcomeMessage)
})
</script>

<style scoped>
.ai-assistant {
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

.function-area {
  display: flex;
  gap: 24px;
}

.chat-section {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #f5f7fa;
}

.chat-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.chat-body {
  height: 400px;
  overflow-y: auto;
  padding: 20px;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 12px;
  flex-shrink: 0;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.message-text {
  padding: 12px 16px;
  border-radius: 16px;
  line-height: 1.5;
  word-wrap: break-word;
}

.message-item.user .message-text {
  background: #409eff;
  color: white;
  border-bottom-right-radius: 4px;
}

.message-item.assistant .message-text {
  background: #f5f7fa;
  color: #333;
  border-bottom-left-radius: 4px;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  text-align: right;
}

.message-item.assistant .message-time {
  text-align: left;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}

.quick-questions {
  width: 300px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 20px;
}

.quick-questions h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
}

.quick-questions .el-button {
  display: block;
  width: 100%;
  margin-bottom: 8px;
  text-align: left;
}

.scenario-content {
  padding: 20px 0;
}

.scenario-header {
  margin-bottom: 24px;
}

.scenario-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.scenario-header p {
  margin: 0;
  color: #666;
}

.scenario-narrative {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  line-height: 1.5;
}

.scenario-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .ai-assistant {
    padding: 16px;
  }
  
  .function-area {
    flex-direction: column;
  }
  
  .quick-questions {
    width: 100%;
  }
  
  .chat-body {
    height: 300px;
  }
  
  .message-content {
    max-width: 80%;
  }
}
</style>