<template>
  <div class="qa-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>在线答疑</span>
          <el-button type="primary" @click="showAskDialog = true">
            <el-icon><Plus /></el-icon> 我要提问
          </el-button>
        </div>
      </template>
      
      <!-- 问题列表 -->
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
    </el-card>
    
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

const loadQuestions = async () => {
  try {
    const res = await get('/qa/questions', { page: page.value, size: size.value })
    questionList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    questionList.value = [
      { id: 1, title: '如何识别冒充公检法的诈骗电话？', askerName: '张三', status: 1, statusName: '已回答', viewCount: 256, answerCount: 2, createTime: '2024-01-15 10:30:00' },
      { id: 2, title: '校园贷有哪些常见套路？', askerName: '李四', status: 0, statusName: '待回答', viewCount: 128, answerCount: 0, createTime: '2024-01-14 15:20:00' },
      { id: 3, title: '网上兼职刷单可信吗？', askerName: '王五', status: 1, statusName: '已回答', viewCount: 389, answerCount: 3, createTime: '2024-01-13 09:15:00' }
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
        { id: 1, content: '您好！冒充公检法的诈骗电话有以下几个特点：1. 要求转账到"安全账户"；2. 要求保密，不许告诉他人；3. 通过伪造警官证、通缉令等恐吓受害者。遇到这类电话，请立即挂断并拨打110核实。', answererName: '王专家', answererTitle: '反诈专家', isAccepted: true, likeCount: 25, createTime: '2024-01-15 11:00:00' },
        { id: 2, content: '补充一点：真正的公检法机关不会通过电话办案，更不会要求转账。', answererName: '李老师', answererTitle: '', isAccepted: false, likeCount: 12, createTime: '2024-01-15 14:30:00' }
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

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.qa-page {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.question-detail h3 {
  margin: 0 0 10px;
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
</style>
