<template>
  <div class="ai-assistant">
    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>AI智能问答</h1>
          <div class="differentiator">
            <span class="tag">多轮对话</span>
            <span class="tag">情景演练联动</span>
            <span class="competitor">对比：厦门防骗AI助手</span>
          </div>
        </div>
        <div class="header-features">
          <div class="feature-item">
            <span class="feature-icon">🎯</span>
            <span class="feature-text">智能识别诈骗类型</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🔄</span>
            <span class="feature-text">多轮深度对话</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🎭</span>
            <span class="feature-text">情景实战演练</span>
          </div>
        </div>
      </div>
    </div>

    <div class="function-area">
      <div class="chat-section">
        <div class="chat-header">
          <div class="chat-title">
            <h2>智能对话</h2>
            <span class="context-indicator" v-if="conversationContext">
              当前上下文：{{ conversationContext }}
            </span>
          </div>
          <div class="chat-actions">
            <el-button @click="clearChat" text>
              <el-icon><Delete /></el-icon>
              清空对话
            </el-button>
          </div>
        </div>

        <div class="chat-body" ref="chatBody">
          <div v-if="messages.length === 0" class="empty-state">
            <div class="empty-icon">🤖</div>
            <h3>开始智能对话</h3>
            <p>向我提问反诈相关问题，或点击下方按钮进行情景演练</p>
          </div>

          <div
            v-for="(message, index) in messages"
            :key="index"
            :class="['message-item', message.role]"
          >
            <div class="message-avatar">
              <div class="avatar-circle" :class="message.role">
                {{ message.role === 'user' ? '👤' : '🤖' }}
              </div>
            </div>
            <div class="message-content">
              <div class="message-bubble">
                <div class="message-text" v-html="formatMessage(message.content)"></div>
                <div class="message-meta">
                  <span class="message-time">{{ formatTime(message.timestamp) }}</span>
                  <span v-if="message.relatedScenario" class="related-scenario" @click="openRelatedScenario(message.relatedScenario)">
                    🎭 相关情景演练
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="isSending" class="message-item assistant">
            <div class="message-avatar">
              <div class="avatar-circle assistant">🤖</div>
            </div>
            <div class="message-content">
              <div class="message-bubble loading">
                <div class="loading-dots">
                  <span></span><span></span><span></span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="chat-input">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="2"
              :placeholder="'请输入您的问题，例如：' + getContextualPlaceholder()"
              resize="none"
              @keyup.enter.exact.prevent="sendMessage"
            />
            <div class="input-actions">
              <div class="action-left">
                <el-tooltip content="切换情景演练">
                  <el-button @click="showScenarioSelector = true" circle>
                    <el-icon><Connection /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
              <div class="action-right">
                <el-button @click="sendMessage" type="primary" :loading="isSending" :disabled="!inputMessage.trim()">
                  <el-icon><Send /></el-icon>
                  发送
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="side-panel">
        <div class="quick-questions">
          <h3>
            <el-icon><QuestionFilled /></el-icon>
            常见问题
          </h3>
          <div class="question-list">
            <el-button
              v-for="question in quickQuestions"
              :key="question.id"
              class="question-btn"
              @click="quickQuestion(question)"
            >
              <span class="question-icon">{{ question.icon }}</span>
              <span class="question-text">{{ question.text }}</span>
            </el-button>
          </div>
        </div>

        <div class="scenario-preview" v-if="recommendedScenario">
          <h3>
            <el-icon><MagicStick /></el-icon>
            推荐演练
          </h3>
          <div class="scenario-card" @click="openScenario(recommendedScenario)">
            <div class="scenario-icon">{{ recommendedScenario.icon }}</div>
            <div class="scenario-info">
              <h4>{{ recommendedScenario.title }}</h4>
              <p>{{ recommendedScenario.description }}</p>
            </div>
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="stats-card">
          <h3>
            <el-icon><DataAnalysis /></el-icon>
            对话统计
          </h3>
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-value">{{ messages.filter(m => m.role === 'user').length }}</span>
              <span class="stat-label">提问次数</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ scenarioDrillCount }}</span>
              <span class="stat-label">演练次数</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="scenarioVisible" title="情景演练" width="900px" class="scenario-dialog">
      <div class="scenario-content" v-if="currentScenario">
        <div class="scenario-header">
          <div class="scenario-badge" :class="currentScenario.difficulty">
            {{ getDifficultyText(currentScenario.difficulty) }}
          </div>
          <h2>{{ currentScenario.title }}</h2>
          <p>{{ currentScenario.description }}</p>
        </div>

        <div class="scenario-progress">
          <el-steps :active="currentStep" finish-status="success" align-center>
            <el-step title="情景阅读" />
            <el-step title="做出选择" />
            <el-step title="结果分析" />
          </el-steps>
        </div>

        <div class="scenario-body">
          <div class="scenario-narrative" v-if="currentStep === 0">
            <div class="narrative-icon">📖</div>
            <div class="narrative-text">{{ currentScenario.narrative }}</div>
            <el-button type="primary" size="large" @click="currentStep = 1">
              我已了解情况，开始选择
            </el-button>
          </div>

          <div class="scenario-options" v-if="currentStep === 1">
            <div class="options-intro">
              <p>面对这种情况，您会怎么做？</p>
            </div>
            <div class="options-grid">
              <el-button
                v-for="(option, index) in currentScenario.options"
                :key="index"
                class="option-btn"
                @click="selectOption(index)"
              >
                <span class="option-letter">{{ String.fromCharCode(65 + index) }}</span>
                <span class="option-text">{{ option.text }}</span>
              </el-button>
            </div>
          </div>

          <div class="scenario-result" v-if="currentStep === 2">
            <div class="result-header" :class="selectedOutcome?.result.toLowerCase()">
              <div class="result-icon">{{ selectedOutcome?.result === '正确' ? '✅' : selectedOutcome?.result === '危险' ? '❌' : '⚠️' }}</div>
              <h3>{{ selectedOutcome?.result }}</h3>
            </div>
            <div class="result-message">
              <p>{{ selectedOutcome?.message }}</p>
            </div>
            <div class="result-suggestion">
              <h4>💡 防范建议</h4>
              <p>{{ selectedOutcome?.suggestion }}</p>
            </div>
            <div class="result-actions">
              <el-button @click="restartScenario">再试一次</el-button>
              <el-button type="primary" @click="tryAnotherScenario">尝试其他情景</el-button>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <span class="footer-tip">
            <el-icon><InfoFilled /></el-icon>
            情景演练旨在提高您的防骗意识，不代表真实法律建议
          </span>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="showScenarioSelector" title="选择情景演练" width="700px">
      <div class="scenario-selector">
        <div
          v-for="scenario in scenarios"
          :key="scenario.id"
          class="scenario-option"
          :class="{ active: selectedScenarioId === scenario.id }"
          @click="selectScenarioAndStart(scenario)"
        >
          <div class="scenario-option-icon">{{ scenario.icon }}</div>
          <div class="scenario-option-info">
            <h4>{{ scenario.title }}</h4>
            <p>{{ scenario.description }}</p>
            <div class="scenario-meta">
              <span class="difficulty-badge" :class="scenario.difficulty">
                {{ getDifficultyText(scenario.difficulty) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Send, Delete, Connection, QuestionFilled, MagicStick, DataAnalysis, ArrowRight, InfoFilled } from '@element-plus/icons-vue'
import { ai } from '@/api'

interface Message {
  role: 'user' | 'assistant'
  content: string
  timestamp: number
  relatedScenario?: any
}

interface QuickQuestion {
  id: string
  text: string
  icon: string
  context: string
}

interface Scenario {
  id: number
  title: string
  description: string
  icon: string
  difficulty: 'easy' | 'medium' | 'hard'
  narrative: string
  options: { text: string }[]
  outcomes: { result: string; message: string; suggestion: string }[]
  keywords: string[]
}

const messages = ref<Message[]>([])
const inputMessage = ref('')
const isSending = ref(false)
const chatBody = ref<HTMLElement | null>(null)
const scenarioVisible = ref(false)
const showScenarioSelector = ref(false)
const selectedScenarioId = ref<number | null>(null)
const currentScenario = ref<Scenario | null>(null)
const currentStep = ref(0)
const selectedOutcome = ref<{ result: string; message: string; suggestion: string } | null>(null)
const scenarioDrillCount = ref(0)
const conversationContext = ref('')

const quickQuestions: QuickQuestion[] = [
  { id: '1', text: '如何识别电信诈骗？', icon: '📞', context: '电信诈骗' },
  { id: '2', text: '遇到网络诈骗怎么办？', icon: '🌐', context: '网络诈骗' },
  { id: '3', text: '什么是杀猪盘诈骗？', icon: '💔', context: '杀猪盘' },
  { id: '4', text: '如何防范校园贷诈骗？', icon: '🎓', context: '校园贷' },
  { id: '5', text: '兼职刷单是诈骗吗？', icon: '💼', context: '刷单诈骗' },
  { id: '6', text: '游戏充值诈骗如何识别？', icon: '🎮', context: '游戏诈骗' }
]

const scenarios: Scenario[] = [
  {
    id: 1,
    title: '电信诈骗 - 公检法骗局',
    description: '模拟接到自称公检法电话的情景',
    icon: '📞',
    difficulty: 'medium',
    narrative: '你接到一个自称是公安局的电话，对方准确报出你的身份证号码，说你涉及一起洗钱案件，需要你立即提供银行卡信息进行调查，否则将被逮捕。',
    options: [
      { text: '提供银行卡信息' },
      { text: '要求对方出示工作证' },
      { text: '挂断电话并报警' },
      { text: '询问具体案件详情' }
    ],
    outcomes: [
      {
        result: '危险',
        message: '您选择了提供银行卡信息，这是最危险的做法！骗子会立即盗取您的资金。',
        suggestion: '公检法机关不会通过电话要求转账或提供银行卡信息。遇到此类情况，应立即挂断并拨打110核实。'
      },
      {
        result: '谨慎',
        message: '您要求对方出示证件，有一定的警惕性。但骗子可能会伪造证件。',
        suggestion: '即使对方能出示证件，也不能证明其真实性。最佳做法是挂断电话，通过官方渠道核实。'
      },
      {
        result: '正确',
        message: '您选择了挂断电话并报警，这是最安全的做法！',
        suggestion: '记住：真警察不会电话办案。遇到可疑电话，直接挂断并报警是最明智的选择。'
      },
      {
        result: '危险',
        message: '您选择与对方纠缠，这很容易被对方的话术逐步洗脑。',
        suggestion: '骗子擅长心理操控，与他们交谈越久越容易落入陷阱。遇到可疑电话，应立即挂断。'
      }
    ],
    keywords: ['电信诈骗', '公检法', '电话', '银行卡']
  },
  {
    id: 2,
    title: '网络兼职 - 刷单骗局',
    description: '模拟遇到刷单兼职的情景',
    icon: '💼',
    difficulty: 'easy',
    narrative: '你在社交媒体上看到一个刷单兼职广告，声称"动动手指，日赚百元"，客服说需要先垫付资金刷单，完成任务后本金和佣金一起返还。前几单你确实收到了小额返利。',
    options: [
      { text: '继续加大投入' },
      { text: '立即提现退出' },
      { text: '搜索相关信息核实' },
      { text: '将广告分享给朋友一起赚钱' }
    ],
    outcomes: [
      {
        result: '危险',
        message: '骗子会先用小利让你尝到甜头，等你加大投入后就会卷款跑路。',
        suggestion: '刷单本身就是违法行为，且绝大多数刷单都是诈骗。不要相信任何形式的刷单广告。'
      },
      {
        result: '谨慎',
        message: '您选择在骗子行骗初期退出，保住了本金。这是一种相对明智的选择。',
        suggestion: '但要注意，骗子可能会以各种理由拒绝提现。发现被骗后应立即报警。'
      },
      {
        result: '正确',
        message: '您选择先核实信息的真实性，这是非常谨慎的做法！',
        suggestion: '遇到可疑兼职，应先搜索了解。刷单本身就是违法行为，正规平台不会以此招揽人员。'
      },
      {
        result: '危险',
        message: '这是骗子的惯用伎俩，让你成为他们的帮凶，扩散诈骗范围。',
        suggestion: '不要转发可疑广告，这不仅可能害了朋友，还可能涉嫌违法。'
      }
    ],
    keywords: ['刷单', '兼职', '网络', '日赚']
  },
  {
    id: 3,
    title: '校园贷 - 套路贷陷阱',
    description: '模拟遇到校园贷的情景',
    icon: '🎓',
    difficulty: 'hard',
    narrative: '你急需用钱交学费，看到校园里贴着"无抵押、低利息、秒到账"的小广告。联系后，对方说只需要学生证和手机通讯录就能借款，但你发现实际利息比宣传的高出很多，而且逾期一天就要支付巨额违约金。',
    options: [
      { text: '签订合同借款' },
      { text: '向学校老师求助' },
      { text: '向家人朋友借钱' },
      { text: '向12345热线咨询' }
    ],
    outcomes: [
      {
        result: '危险',
        message: '这是典型的"套路贷"！一旦签订合同，就会陷入高利贷陷阱，甚至可能被暴力催收。',
        suggestion: '校园贷往往隐藏高利贷陷阱。遇到资金困难，应通过正规渠道解决，不要轻信民间借贷。'
      },
      {
        result: '正确',
        message: '您选择向学校老师求助，这是非常明智的选择！',
        suggestion: '学校有专门的学生资助中心，会为您提供正规的助学贷款或助学金。遇到困难，找学校是最安全的。'
      },
      {
        result: '正确',
        message: '您选择向家人朋友求助，这是解决资金困难的安全方式。',
        suggestion: '虽然可能需要一些时间，但向熟人借款没有利息压力，也不会陷入诈骗陷阱。'
      },
      {
        result: '谨慎',
        message: '您选择咨询官方渠道，展现了很好的防范意识。',
        suggestion: '12345热线可以提供合法的咨询信息，帮助您了解相关法律法规，避免上当受骗。'
      }
    ],
    keywords: ['校园贷', '贷款', '利息', '押金']
  },
  {
    id: 4,
    title: '杀猪盘 - 网恋骗局',
    description: '模拟网恋遇到杀猪盘的情景',
    icon: '💔',
    difficulty: 'hard',
    narrative: '你在网上认识了一位"高富帅"网友，对方每天嘘寒问暖，几天后就声称发现了一个投资平台的漏洞，可以带你一起赚钱。对方发来了盈利截图，并说已经投入了50万。',
    options: [
      { text: '跟随一起投资' },
      { text: '先自己注册查看' },
      { text: '举报该账号' },
      { text: '询问对方更多信息' }
    ],
    outcomes: [
      {
        result: '危险',
        message: '这是典型的"杀猪盘"诈骗！所谓的高富帅和投资漏洞都是假的，目的是骗光你的钱财。',
        suggestion: '网恋不谈钱，谈钱必诈骗。不要相信任何网友推荐的投资平台，这都是诈骗分子的陷阱。'
      },
      {
        result: '危险',
        message: '即使你自己查看，骗子也会伪造数据让你相信。等你投入资金后就会无法提现。',
        suggestion: '这类平台的数据都是骗子在后台操控的。发现类似情况应立即举报，不要尝试。'
      },
      {
        result: '正确',
        message: '您选择举报该账号，这是正确的防范措施！',
        suggestion: '发现诈骗线索应及时向平台举报，也可以向公安机关报案，避免更多人受害。'
      },
      {
        result: '危险',
        message: '骗子会继续用甜言蜜语迷惑你，最终诱导你转账。',
        suggestion: '杀猪盘的骗子极具耐心，会花很长时间建立感情。不要轻信网友的任何赚钱提议。'
      }
    ],
    keywords: ['杀猪盘', '网恋', '投资', '赌博']
  },
  {
    id: 5,
    title: '游戏充值 - 虚假交易',
    description: '模拟游戏充值遇到诈骗的情景',
    icon: '🎮',
    difficulty: 'easy',
    narrative: '你在玩网络游戏时，有玩家私信你说可以低价充值游戏点券，1元可以换100点券，比官方价格低很多。他发来了一个交易平台的链接，看起来很正规。',
    options: [
      { text: '立即充值' },
      { text: '到官方平台充值' },
      { text: '让对方先充一点试试' },
      { text: '举报该玩家' }
    ],
    outcomes: [
      {
        result: '危险',
        message: '这些链接通常是钓鱼网站，会窃取你的账号密码。即使充值成功，也会遭遇后续诈骗。',
        suggestion: '游戏充值应通过官方渠道，不要贪图便宜。低价充值99%是诈骗。'
      },
      {
        result: '正确',
        message: '您选择官方充值，这是最安全的做法！',
        suggestion: '官方平台有保障，即使出现问题也能申请退款。不要相信任何第三方低价充值。'
      },
      {
        result: '危险',
        message: '骗子会先用小利诱惑你，等你放松警惕后再骗取大额资金。',
        suggestion: '这类诈骗通常先给甜头，等你投入更多后就会无法提现或直接消失。'
      },
      {
        result: '正确',
        message: '您选择举报该玩家，避免了潜在的诈骗风险，也为其他玩家排除了隐患。',
        suggestion: '发现可疑玩家应及时向游戏官方举报，帮助构建安全的游戏环境。'
      }
    ],
    keywords: ['游戏', '充值', '点券', '低价']
  }
]

const recommendedScenario = computed(() => {
  if (!conversationContext.value) return scenarios[0]
  const context = conversationContext.value.toLowerCase()
  return scenarios.find(s =>
    s.keywords.some(k => context.includes(k)) ||
    s.title.toLowerCase().includes(context)
  ) || scenarios[0]
})

function getContextualPlaceholder(): string {
  const placeholders: Record<string, string> = {
    '电信诈骗': '我收到了自称公检法的电话，应该怎么办？',
    '网络诈骗': '我在网上被骗了，怎么挽回损失？',
    '杀猪盘': '网上认识的人带我投资，能信吗？',
    '校园贷': '急用钱时，除了校园贷还有什么办法？',
    '刷单诈骗': '网上看到刷单兼职，能做吗？',
    '游戏诈骗': '游戏里有人卖低价点券，靠谱吗？'
  }
  return placeholders[conversationContext.value] || '如何识别和防范各种诈骗手段？'
}

function formatTime(timestamp: number): string {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

function formatMessage(content: string): string {
  return content
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>')
    .replace(/(\d+)\./g, '<span class="num">$1.</span>')
}

function scrollToBottom() {
  nextTick(() => {
    if (chatBody.value) {
      chatBody.value.scrollTop = chatBody.value.scrollHeight
    }
  })
}

function getAIResponse(question: string): { response: string; relatedScenario?: Scenario } {
  const responses: Record<string, { response: string; context: string; scenario?: Scenario }> = {
    '如何识别电信诈骗？': {
      response: '识别电信诈骗的关键要点：<br><br>' +
        '**1. 警惕来电显示**<br>' +
        '- 警惕+86、+87等开头的国际来电<br>' +
        '- 公检法机关不会使用境外电话<br><br>' +
        '**2. 不轻信身份**<br>' +
        '- 即使对方能报出你的个人信息，也不能轻信<br>' +
        '- 骗子通过非法渠道获取个人信息进行诈骗<br><br>' +
        '**3. 绝不转账**<br>' +
        '- 公检法机关不会要求转账到"安全账户"<br>' +
        '- 任何转账要求都是诈骗<br><br>' +
        '💡 <strong>建议</strong>：我为您准备了一个情景演练，可以模拟接听诈骗电话的情况，',
      context: '电信诈骗',
      scenario: scenarios[0]
    },
    '遇到网络诈骗怎么办？': {
      response: '遇到网络诈骗，请按以下步骤处理：<br><br>' +
        '**1. 立即止损**<br>' +
        '- 停止与骗子的一切联系<br>' +
        '- 保留所有聊天记录和转账凭证<br>' +
        '- 第一时间联系银行冻结账户<br><br>' +
        '**2. 报警处理**<br>' +
        '- 拨打110或到就近派出所报案<br>' +
        '- 提供详细的转账记录和聊天截图<br>' +
        '- 配合警方提供骗子的账户信息<br><br>' +
        '**3. 通知亲友**<br>' +
        '- 防止骗子利用你的账号继续诈骗他人<br>' +
        '- 提醒亲友注意防范类似骗局<br><br>' +
        '⚠️ <strong>注意</strong>：保留证据非常重要，不要删除任何聊天记录！',
      context: '网络诈骗'
    },
    '什么是杀猪盘诈骗？': {
      response: '杀猪盘是一种极其恶劣的网络诈骗手段：<br><br>' +
        '**诈骗流程**<br>' +
        '1️⃣ **找猪**：在婚恋网站、社交平台寻找目标<br>' +
        '2️⃣ **养猪」：用甜言蜜语培养感情，通常持续数周甚至数月<br>' +
        '3️⃣ **杀猪」：诱导受害者参与虚假投资，最后卷款跑路<br><br>' +
        '**典型特征**<br>' +
        '- 网恋对象是高富帅/白富美，却对你异常热情<br>' +
        '- 声称发现投资漏洞，带你一起赚钱<br>' +
        '- 各种理由拒绝见面或视频<br>' +
        '- 要求你转账到陌生账户<br><br>' +
        '🎯 <strong>防范要点</strong>：网恋不谈钱，谈钱必诈骗！',
      context: '杀猪盘',
      scenario: scenarios[3]
    },
    '如何防范校园贷诈骗？': {
      response: '防范校园贷，请记住以下几点：<br><br>' +
        '**认清校园贷的危害**<br>' +
        '❌ 利息极高，逾期后违约金惊人<br>' +
        '❌ 暴力催收，影响学业和生活<br>' +
        '❌ 可能利滚利，债务像滚雪球<br><br>' +
        '**正规借款渠道**<br>' +
        '✅ 国家助学贷款（免息或低息）<br>' +
        '✅ 学校学生资助中心<br>' +
        '✅ 银行正规信用卡<br>' +
        '✅ 向家人朋友借款<br><br>' +
        '**应急解决方案**<br>' +
        '📚 申请奖学金或助学金<br>' +
        '👨‍👩‍👧 向家人坦白，寻求帮助<br>' +
        '🏫 参加校内勤工俭学<br><br>' +
        '💡 <strong>记住</strong>：没有抵押的学生贷都是陷阱！',
      context: '校园贷',
      scenario: scenarios[2]
    },
    '兼职刷单是诈骗吗？': {
      response: '明确回答：<strong>刷单就是诈骗，而且违法！</strong><br><br>' +
        '**刷单诈骗的套路**<br>' +
        '1️⃣ 骗子在社交媒体发布"日赚百元"广告<br>' +
        '2️⃣ 前几单给你小额返利，让你尝到甜头<br>' +
        '3️⃣ 等你投入大额资金后，以各种理由拒绝返款<br>' +
        '4️⃣ 最后你会发现资金无法提现，骗子消失<br><br>' +
        '**为什么刷单违法？**<br>' +
        '📖 刷单破坏了公平竞争的市场秩序<br>' +
        '📖 违反了《反不正当竞争法》<br>' +
        '📖 情节严重可构成非法经营罪<br><br>' +
        '**遇到刷单广告怎么办？**<br>' +
        '🚫 不要相信<br>' +
        '🚫 不要参与<br>' +
        '📢 积极举报<br><br>' +
        '⚠️ <strong>警示</strong>：参与刷单不仅会被骗，还可能涉嫌违法！',
      context: '刷单诈骗',
      scenario: scenarios[1]
    }
  }

  const lowerQuestion = question.toLowerCase()
  for (const [key, value] of Object.entries(responses)) {
    if (lowerQuestion.includes(key.toLowerCase())) {
      return {
        response: value.response + (value.scenario ? `点击<strong>相关情景演练</strong>体验。` : ''),
        relatedScenario: value.scenario
      }
    }
  }

  const defaultResponse = `感谢您的提问。关于反诈知识，我可以为您提供专业的解答。<br><br>` +
    `您可以问我以下问题：<br>` +
    `<span class="num">1.</span> 如何识别电信诈骗？<br>` +
    `<span class="num">2.</span> 遇到网络诈骗怎么办？<br>` +
    `<span class="num">3.</span> 什么是杀猪盘诈骗？<br>` +
    `<span class="num">4.</span> 如何防范校园贷诈骗？<br>` +
    `<span class="num">5.</span> 兼职刷单是诈骗吗？<br><br>` +
    `或者点击右侧的<strong>推荐演练</strong>，进行情景模拟训练。`

  return { response: defaultResponse }
}

function sendMessage() {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入问题')
    return
  }

  const userMessage: Message = {
    role: 'user',
    content: inputMessage.value,
    timestamp: Date.now()
  }
  messages.value.push(userMessage)
  const question = inputMessage.value
  inputMessage.value = ''
  scrollToBottom()

  isSending.value = true

  // 调用后端API获取AI回答
  ai.askQuestion(question, conversationContext.value)
    .then((res) => {
      if (res.code === 200 && res.data) {
        const response = res.data.answer || '抱歉，我暂时无法回答这个问题，请稍后再试。'
        
        // 查找相关的情景演练（基于问题内容）
        let relatedScenario = null
        const lowerQuestion = question.toLowerCase()
        for (const scenario of scenarios) {
          if (scenario.keywords.some(keyword => lowerQuestion.includes(keyword))) {
            relatedScenario = scenario
            break
          }
        }

        const aiMessage: Message = {
          role: 'assistant',
          content: response,
          timestamp: Date.now(),
          relatedScenario
        }
        messages.value.push(aiMessage)
        
        if (relatedScenario) {
          ElMessage.success({
            message: '我为您找到了相关的情景演练，点击即可体验',
            duration: 3000
          })
        }
      } else {
        const aiMessage: Message = {
          role: 'assistant',
          content: '抱歉，我暂时无法回答这个问题，请稍后再试。',
          timestamp: Date.now()
        }
        messages.value.push(aiMessage)
      }
    })
    .catch((error) => {
      console.error('AI问答失败:', error)
      const aiMessage: Message = {
        role: 'assistant',
        content: '抱歉，我暂时无法回答这个问题，请稍后再试。',
        timestamp: Date.now()
      }
      messages.value.push(aiMessage)
    })
    .finally(() => {
      isSending.value = false
      scrollToBottom()
    })
}

function quickQuestion(question: QuickQuestion) {
  inputMessage.value = question.text
  conversationContext.value = question.context
  sendMessage()
}

function clearChat() {
  messages.value = []
  conversationContext.value = ''
  ElMessage.success('对话已清空')
}

function openScenario(scenario?: Scenario) {
  currentScenario.value = scenario || recommendedScenario.value
  currentStep.value = 0
  selectedOutcome.value = null
  scenarioVisible.value = true
  showScenarioSelector.value = false
}

function selectOption(index: number) {
  if (!currentScenario.value) return

  selectedOutcome.value = currentScenario.value.outcomes[index]
  currentStep.value = 2
  scenarioDrillCount.value++

  const resultType = selectedOutcome.value?.result
  if (resultType === '正确') {
    ElMessage.success({
      message: '恭喜您做出了正确选择！',
      duration: 3000
    })
  } else if (resultType === '危险') {
    ElMessage.error({
      message: '这是危险选择，请注意防范！',
      duration: 3000
    })
  } else {
    ElMessage.warning({
      message: '这个选择需要更加谨慎',
      duration: 3000
    })
  }
}

function restartScenario() {
  if (currentScenario.value) {
    const currentId = currentScenario.value.id
    const sameScenario = scenarios.find(s => s.id === currentId)
    if (sameScenario) {
      currentScenario.value = { ...sameScenario }
      currentStep.value = 1
      selectedOutcome.value = null
    }
  }
}

function tryAnotherScenario() {
  const randomScenario = scenarios[Math.floor(Math.random() * scenarios.length)]
  currentScenario.value = { ...randomScenario }
  currentStep.value = 0
  selectedOutcome.value = null
}

function selectScenarioAndStart(scenario: Scenario) {
  selectedScenarioId.value = scenario.id
  setTimeout(() => {
    openScenario(scenario)
  }, 300)
}

function openRelatedScenario(scenario: Scenario) {
  currentScenario.value = scenario
  currentStep.value = 0
  selectedOutcome.value = null
  scenarioVisible.value = true
}

function getDifficultyText(difficulty: string): string {
  const map: Record<string, string> = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  }
  return map[difficulty] || difficulty
}

watch(conversationContext, (newContext) => {
  if (newContext) {
    const quickQuestion = quickQuestions.find(q => q.context === newContext)
    if (quickQuestion && messages.value.length === 0) {
      const welcomeMsg: Message = {
        role: 'assistant',
        content: `您好！您想了解<strong>${newContext}</strong>相关的问题。让我为您详细解答。`,
        timestamp: Date.now()
      }
      messages.value.push(welcomeMsg)
    }
  }
})

onMounted(() => {
  const welcomeMessage: Message = {
    role: 'assistant',
    content: '您好！我是AI智能问答助手。<br><br>' +
      '🎯 <strong>我的优势</strong>：<br>' +
      '• 支持多轮对话，深入解答各类问题<br>' +
      '• 根据您的问题智能推荐情景演练<br>' +
      '• 帮助您全面提升防骗能力<br><br>' +
      '请随时向我提问，或选择右侧的<strong>常见问题</strong>快速开始。',
    timestamp: Date.now()
  }
  messages.value.push(welcomeMessage)
})
</script>

<style scoped>
.ai-assistant {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
  min-height: calc(100vh - 60px);
}

.header {
  margin-bottom: 24px;
}

.header-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
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

.function-area {
  display: flex;
  gap: 24px;
}

.chat-section {
  flex: 1;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 250px);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e4e7ed;
  background: #fafafa;
}

.chat-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.chat-title h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.context-indicator {
  font-size: 12px;
  color: #909399;
  background: #f0f0f0;
  padding: 2px 8px;
  border-radius: 4px;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  color: #606266;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  margin: 0 12px;
}

.avatar-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background: #f0f2f5;
}

.avatar-circle.user {
  background: linear-gradient(135deg, #409eff, #66b1ff);
}

.avatar-circle.assistant {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.message-content {
  flex: 1;
  max-width: 75%;
}

.message-bubble {
  padding: 16px;
  border-radius: 16px;
  position: relative;
}

.message-item.user .message-bubble {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-item.assistant .message-bubble {
  background: #f5f7fa;
  color: #333;
  border-bottom-left-radius: 4px;
}

.message-text {
  line-height: 1.6;
  word-wrap: break-word;
}

.message-text :deep(.num) {
  color: #667eea;
  font-weight: bold;
}

.message-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
  opacity: 0.7;
}

.related-scenario {
  cursor: pointer;
  color: #409eff;
  display: flex;
  align-items: center;
  gap: 4px;
}

.related-scenario:hover {
  text-decoration: underline;
}

.message-bubble.loading {
  background: transparent;
  padding: 8px 16px;
}

.loading-dots {
  display: flex;
  gap: 4px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.chat-input {
  padding: 20px 24px;
  border-top: 1px solid #e4e7ed;
  background: #fafafa;
}

.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-left, .action-right {
  display: flex;
  gap: 8px;
}

.side-panel {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.quick-questions, .scenario-preview, .stats-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.quick-questions h3, .scenario-preview h3, .stats-card h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.question-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  height: auto;
  text-align: left;
  border: 1px solid #e4e7ed;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.question-btn:hover {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
  transform: translateX(4px);
}

.question-icon {
  font-size: 20px;
}

.question-text {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.scenario-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa, #e8edf5);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.scenario-card:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.scenario-icon {
  font-size: 32px;
}

.scenario-info {
  flex: 1;
}

.scenario-info h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #333;
}

.scenario-info p {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.arrow {
  color: #c0c4cc;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa, #e8edf5);
  border-radius: 12px;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: bold;
  color: #667eea;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.scenario-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.scenario-content {
  padding: 32px;
}

.scenario-header {
  text-align: center;
  margin-bottom: 24px;
}

.scenario-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  margin-bottom: 12px;
}

.scenario-badge.easy { background: #e1f3d8; color: #67c23a; }
.scenario-badge.medium { background: #faecd8; color: #e6a23c; }
.scenario-badge.hard { background: #fde2e2; color: #f56c6c; }

.scenario-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.scenario-header p {
  margin: 0;
  color: #909399;
}

.scenario-progress {
  margin-bottom: 32px;
  padding: 0 40px;
}

.scenario-body {
  min-height: 300px;
}

.scenario-narrative, .scenario-options, .scenario-result {
  animation: fadeIn 0.3s ease;
}

.narrative-icon {
  font-size: 48px;
  text-align: center;
  margin-bottom: 16px;
}

.narrative-text {
  background: linear-gradient(135deg, #f5f7fa, #e8edf5);
  padding: 24px;
  border-radius: 12px;
  line-height: 1.8;
  font-size: 16px;
  margin-bottom: 24px;
  border-left: 4px solid #667eea;
}

.scenario-narrative .el-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
}

.options-intro {
  text-align: center;
  margin-bottom: 24px;
}

.options-intro p {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.options-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.option-btn {
  height: auto;
  padding: 20px;
  text-align: left;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  transition: all 0.3s ease;
}

.option-btn:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.option-letter {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
}

.option-text {
  flex: 1;
  font-size: 14px;
  line-height: 1.5;
}

.result-header {
  text-align: center;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.result-header.correct {
  background: linear-gradient(135deg, #e1f3d8, #d4edda);
}

.result-header.dangerous {
  background: linear-gradient(135deg, #fde2e2, #fadbd8);
}

.result-header.cautious {
  background: linear-gradient(135deg, #faecd8, #f5e6c8);
}

.result-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.result-header h3 {
  margin: 0;
  font-size: 24px;
}

.result-message {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
}

.result-message p {
  margin: 0;
  line-height: 1.6;
}

.result-suggestion {
  background: linear-gradient(135deg, #fff9e6, #fff3cd);
  padding: 20px;
  border-radius: 12px;
  border-left: 4px solid #e6a23c;
}

.result-suggestion h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
}

.result-suggestion p {
  margin: 0;
  line-height: 1.6;
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.dialog-footer {
  text-align: center;
}

.footer-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #909399;
  font-size: 14px;
}

.scenario-selector {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
  padding: 12px;
}

.scenario-option {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.scenario-option:hover, .scenario-option.active {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.scenario-option-icon {
  font-size: 32px;
}

.scenario-option-info {
  flex: 1;
}

.scenario-option-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #333;
}

.scenario-option-info p {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #909399;
}

.difficulty-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 12px;
}

.difficulty-badge.easy { background: #e1f3d8; color: #67c23a; }
.difficulty-badge.medium { background: #faecd8; color: #e6a23c; }
.difficulty-badge.hard { background: #fde2e2; color: #f56c6c; }

@media (max-width: 1024px) {
  .function-area {
    flex-direction: column;
  }

  .side-panel {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .quick-questions, .scenario-preview, .stats-card {
    flex: 1;
    min-width: 280px;
  }
}

@media (max-width: 768px) {
  .ai-assistant {
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

  .chat-section {
    max-height: none;
  }

  .chat-body {
    min-height: 300px;
  }

  .message-content {
    max-width: 85%;
  }

  .options-grid {
    grid-template-columns: 1fr;
  }
}
</style>
