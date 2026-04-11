<template>
  <div class="qa-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
      </div>
      <div class="header-content">
        <h1>智能问答中心</h1>
        <p>在线咨询专家，获取专业反诈知识解答</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 智能问答卡片 -->
      <div class="smart-qa-card">
        <div class="card-title">
          <h3>智能问答助手</h3>
          <p>快速获取反诈知识解答</p>
        </div>
        <div class="smart-qa-content">
          <el-input
            v-model="smartQuestion"
            placeholder="请输入您的问题，如：如何识别电信诈骗？"
            @keyup.enter="askSmartQuestion"
          >
            <template #append>
              <el-button type="primary" @click="askSmartQuestion">
                <el-icon><Message /></el-icon>
              </el-button>
            </template>
          </el-input>
          <div v-if="smartAnswer" class="smart-answer">
            <div class="answer-header">
              <el-avatar size="small">AI</el-avatar>
              <span class="answerer">智能助手</span>
            </div>
            <div class="answer-content">{{ smartAnswer }}</div>
          </div>
        </div>
      </div>

      <!-- 专家在线 -->
      <div class="experts-section">
        <h3>在线专家</h3>
        <div class="experts-list">
          <div class="expert-card" v-for="expert in experts" :key="expert.id">
            <el-avatar :src="expert.avatar" size="64">{{ expert.name.charAt(0) }}</el-avatar>
            <div class="expert-info">
              <h4>{{ expert.name }}</h4>
              <p class="expert-title">{{ expert.title }}</p>
              <div class="expert-stats">
                <span>回答: {{ expert.answerCount }}</span>
                <span>采纳率: {{ expert.acceptanceRate }}%</span>
              </div>
            </div>
            <el-button type="primary" size="small" @click="consultExpert(expert)">
              咨询专家
            </el-button>
          </div>
        </div>
      </div>

      <!-- 问题列表 -->
      <div class="questions-section">
        <div class="section-header">
          <h3>问题列表</h3>
          <el-button type="primary" @click="showAskDialog = true">
            <el-icon><Plus /></el-icon> 我要提问
          </el-button>
        </div>
        <el-table :data="questionList" style="width: 100%">
          <el-table-column prop="title" label="问题标题" min-width="250">
            <template #default="{ row }">
              <el-link @click="viewQuestion(row)">{{ row.title }}</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="askerName" label="提问者" width="100" />
          <el-table-column prop="statusName" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'warning'">
                {{ row.statusName }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览" width="80" />
          <el-table-column prop="answerCount" label="回答" width="80" />
          <el-table-column prop="createTime" label="提问时间" width="160" />
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="loadQuestions"
          />
        </div>
      </div>
    </div>
    
    <!-- 提问对话框 -->
    <el-dialog v-model="showAskDialog" title="我要提问" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="问题标题">
          <el-input v-model="form.title" placeholder="请输入问题标题" />
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请详细描述您的问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAskDialog = false">取消</el-button>
        <el-button type="primary" @click="submitQuestion">提交</el-button>
      </template>
    </el-dialog>
    
    <!-- 问题详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="问题详情" width="700px">
      <div v-if="currentQuestion" class="question-detail">
        <h3>{{ currentQuestion.title }}</h3>
        <div class="question-meta">
          <span>{{ currentQuestion.askerName }}</span>
          <span>{{ currentQuestion.createTime }}</span>
          <span>{{ currentQuestion.viewCount }} 浏览</span>
        </div>
        <div class="question-content">{{ currentQuestion.content }}</div>
        
        <el-divider>回答 ({{ currentQuestion.answerCount || 0 }})</el-divider>
        
        <div v-for="answer in currentQuestion.answers" :key="answer.id" class="answer-item">
          <div class="answer-header">
            <span class="answerer">
              {{ answer.answererName }}
              <el-tag v-if="answer.answererTitle" size="small" type="success">{{ answer.answererTitle }}</el-tag>
            </span>
            <span v-if="answer.isAccepted" class="accepted">
              <el-icon><Select /></el-icon> 已采纳
            </span>
          </div>
          <div class="answer-content">{{ answer.content }}</div>
          <div class="answer-footer">
            <span>{{ answer.createTime }}</span>
            <el-button text size="small" @click="likeAnswer(answer.id)">
              <el-icon><Star /></el-icon> {{ answer.likeCount }}
            </el-button>
          </div>
        </div>
        
        <!-- 回答框（专家可见） -->
        <el-divider>我来回答</el-divider>
        <el-form :model="answerForm" label-width="0">
          <el-form-item>
            <el-input v-model="answerForm.content" type="textarea" :rows="3" placeholder="请输入您的回答" />
          </el-form-item>
          <el-button type="primary" @click="submitAnswer">提交回答</el-button>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Plus, Select, Star, Message } from '@element-plus/icons-vue'

const questionList = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const showAskDialog = ref(false)
const showDetailDialog = ref(false)
const currentQuestion = ref<any>(null)

const form = reactive({
  title: '',
  content: ''
})

const answerForm = reactive({
  content: ''
})

// 智能问答相关
const smartQuestion = ref('')
const smartAnswer = ref('')

// 专家列表
const experts = ref([
  {
    id: 1,
    name: '王专家',
    title: '反诈专家',
    avatar: '',
    answerCount: 128,
    acceptanceRate: 95
  },
  {
    id: 2,
    name: '李老师',
    title: '高校反诈讲师',
    avatar: '',
    answerCount: 86,
    acceptanceRate: 92
  },
  {
    id: 3,
    name: '张警官',
    title: '反诈民警',
    avatar: '',
    answerCount: 215,
    acceptanceRate: 98
  }
])

const loadQuestions = async () => {
  try {
    const res = await get('/qa/questions', { page: page.value, size: size.value })
    questionList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    questionList.value = [
      { id: 1, title: '如何识别冒充公检法的诈骗电话？', askerName: '张三', status: 1, statusName: '已回答', viewCount: 256, answerCount: 2, createTime: '2026-01-15 10:30:00' },
      { id: 2, title: '校园贷有哪些常见套路？', askerName: '李四', status: 0, statusName: '待回答', viewCount: 128, answerCount: 0, createTime: '2026-01-14 15:20:00' },
      { id: 3, title: '网上兼职刷单可信吗？', askerName: '王五', status: 1, statusName: '已回答', viewCount: 389, answerCount: 3, createTime: '2026-01-13 09:15:00' }
    ]
    total.value = 3
  }
}

const submitQuestion = async () => {
  if (!form.title || !form.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    await post('/qa/question', form)
    ElMessage.success('提问成功，请等待专家解答')
    showAskDialog.value = false
    loadQuestions()
  } catch (e) {}
}

const viewQuestion = async (row: any) => {
  try {
    const res = await get(`/qa/question/${row.id}`)
    currentQuestion.value = res.data
  } catch (e) {
    currentQuestion.value = {
      ...row,
      content: '这是问题的详细描述内容...',
      answers: [
        { id: 1, content: '您好！冒充公检法的诈骗电话有以下几个特点：1. 要求转账到"安全账户"；2. 要求保密，不许告诉他人；3. 通过伪造警官证、通缉令等恐吓受害者。遇到这类电话，请立即挂断并拨打110核实。', answererName: '王专家', answererTitle: '反诈专家', isAccepted: true, likeCount: 25, createTime: '2026-01-15 11:00:00' },
        { id: 2, content: '补充一点：真正的公检法机关不会通过电话办案，更不会要求转账。', answererName: '李老师', answererTitle: '', isAccepted: false, likeCount: 12, createTime: '2026-01-15 14:30:00' }
      ]
    }
  }
  showDetailDialog.value = true
}

const submitAnswer = async () => {
  if (!answerForm.content) {
    ElMessage.warning('请输入回答内容')
    return
  }
  
  try {
    await post('/qa/answer', {
      questionId: currentQuestion.value.id,
      content: answerForm.content
    })
    ElMessage.success('回答成功')
    answerForm.content = ''
    viewQuestion(currentQuestion.value)
  } catch (e) {}
}

const likeAnswer = async (answerId: number) => {
  try {
    await post(`/qa/answer/${answerId}/like`)
    ElMessage.success('点赞成功')
  } catch (e) {}
}

// 智能问答方法
const askSmartQuestion = async () => {
  if (!smartQuestion.value) {
    ElMessage.warning('请输入问题')
    return
  }

  // 模拟智能问答
  smartAnswer.value = '正在思考中...'
  
  // 模拟延迟
  setTimeout(() => {
    const question = smartQuestion.value.toLowerCase()
    let answer = ''

    if (question.includes('电信诈骗') || question.includes('电话诈骗')) {
      answer = '识别电信诈骗的方法：1. 接到自称公检法、银行客服的电话要警惕；2. 不要轻易提供个人信息和银行卡信息；3. 不要点击陌生链接；4. 涉及转账一定要核实对方身份；5. 如有疑问可拨打110咨询。'
    } else if (question.includes('网络诈骗') || question.includes('网购诈骗')) {
      answer = '识别网络诈骗的方法：1. 选择正规购物平台；2. 不要轻信低价诱惑；3. 核对网站域名是否正确；4. 使用安全的支付方式；5. 收到异常订单信息要通过官方渠道核实。'
    } else if (question.includes('校园贷') || question.includes('贷款')) {
      answer = '校园贷的危害：1. 高额利息和手续费；2. 暴力催收；3. 个人信息泄露；4. 陷入债务陷阱。建议：1. 理性消费，量入为出；2. 如需贷款选择正规金融机构；3. 提高风险意识，拒绝不良校园贷。'
    } else {
      answer = '您好，感谢您的提问。关于反诈知识，建议您关注以下几点：1. 提高安全意识，不轻易相信陌生信息；2. 保护个人信息，不随意透露；3. 涉及金钱交易要谨慎；4. 遇到可疑情况及时报警。如需更详细的解答，建议咨询在线专家。'
    }

    smartAnswer.value = answer
  }, 1000)
}

// 专家咨询方法
const consultExpert = (expert: any) => {
  showAskDialog.value = true
  ElMessage.info(`您可以向${expert.name}(${expert.title})提问，专家会尽快为您解答`)
}

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.qa-page {
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
  background: linear-gradient(135deg, #3b82f6 0%, #60a5fa 50%, #93c5fd 100%);
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

/* 智能问答卡片 */
.smart-qa-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-6);
  box-shadow: var(--shadow-md);
  margin-bottom: var(--spacing-8);
}

.card-title {
  margin-bottom: var(--spacing-4);
}

.card-title h3 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.card-title p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.smart-qa-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.smart-answer {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: var(--spacing-4);
  border-left: 4px solid var(--primary-color);
}

.smart-answer .answer-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-2);
}

.smart-answer .answerer {
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.smart-answer .answer-content {
  color: var(--text-secondary);
  line-height: 1.6;
}

/* 专家在线 */
.experts-section {
  margin-bottom: var(--spacing-8);
}

.experts-section h3 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-4);
}

.experts-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-4);
}

.expert-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-4);
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

.expert-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.expert-info {
  flex: 1;
}

.expert-info h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.expert-title {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-2);
}

.expert-stats {
  display: flex;
  gap: var(--spacing-4);
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
}

/* 问题列表 */
.questions-section {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-6);
  box-shadow: var(--shadow-md);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
}

.section-header h3 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-6);
}

/* 问题详情 */
.question-detail h3 {
  margin: 0 0 10px;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.question-meta {
  color: #909399;
  font-size: 14px;
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.question-content {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
  line-height: 1.6;
  color: var(--text-secondary);
}

.answer-item {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
}

.answer-item:last-child {
  border-bottom: none;
}

.answer-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.answerer {
  font-weight: bold;
  color: var(--text-primary);
}

.accepted {
  color: #67C23A;
  display: flex;
  align-items: center;
  gap: 5px;
}

.answer-content {
  color: #606266;
  line-height: 1.6;
}

.answer-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}

/* 响应式 */
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

  .experts-list {
    grid-template-columns: 1fr;
  }

  .expert-card {
    flex-direction: column;
    text-align: center;
  }
}
</style>
