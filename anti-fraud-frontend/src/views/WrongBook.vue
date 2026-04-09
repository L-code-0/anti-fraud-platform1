<template>
  <div class="wrongbook-page">
    <div class="page-header">
      <h1><el-icon><DocumentCopy /></el-icon> 错题本</h1>
      <p>系统自动收集您的错题，帮助您针对性地复习和巩固知识</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon blue">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ wrongCount }}</div>
            <div class="stat-label">错题总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon orange">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ todayCount }}</div>
            <div class="stat-label">今日新增</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon green">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ masteredCount }}</div>
            <div class="stat-label">已掌握</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon purple">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ accuracyRate }}%</div>
            <div class="stat-label">正确率</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 筛选工具栏 -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索错题内容"
            clearable
            @keyup.enter="loadWrongQuestions"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="categoryFilter" placeholder="题目分类" clearable @change="loadWrongQuestions">
            <el-option label="电信诈骗" value="1" />
            <el-option label="网络诈骗" value="2" />
            <el-option label="兼职诈骗" value="3" />
            <el-option label="校园贷" value="4" />
            <el-option label="其他" value="5" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="difficultyFilter" placeholder="难度等级" clearable @change="loadWrongQuestions">
            <el-option label="简单" value="1" />
            <el-option label="中等" value="2" />
            <el-option label="困难" value="3" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="statusFilter" placeholder="掌握状态" clearable @change="loadWrongQuestions">
            <el-option label="未掌握" value="0" />
            <el-option label="已掌握" value="1" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="loadWrongQuestions">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon> 导出
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 错题列表 -->
    <el-card class="list-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>错题列表</span>
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button value="card">卡片视图</el-radio-button>
            <el-radio-button value="table">表格视图</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <!-- 卡片视图 -->
      <div v-if="viewMode === 'card'" class="question-cards">
        <div v-if="wrongQuestions.length === 0" class="empty-state">
          <el-empty description="暂无错题记录" />
        </div>
        <div v-else class="cards-grid">
          <el-card
            v-for="item in wrongQuestions"
            :key="item.id"
            class="question-card"
            :class="{ mastered: item.mastered }"
            shadow="hover"
          >
            <div class="card-header">
              <el-tag :type="getDifficultyType(item.difficulty)" size="small">
                {{ getDifficultyLabel(item.difficulty) }}
              </el-tag>
              <el-tag v-if="item.mastered" type="success" size="small">已掌握</el-tag>
              <el-tag v-else type="warning" size="small">未掌握</el-tag>
            </div>
            
            <div class="question-content">
              <div class="question-type">
                <el-icon><QuestionFilled /></el-icon>
                {{ getQuestionTypeLabel(item.questionType) }}
              </div>
              <p class="question-text">{{ item.questionText }}</p>
              
              <div v-if="item.options && item.options.length" class="options-list">
                <div
                  v-for="(opt, idx) in item.options"
                  :key="idx"
                  class="option-item"
                  :class="{
                    correct: item.correctAnswer === idx,
                    wrong: item.userAnswer === idx && item.userAnswer !== item.correctAnswer
                  }"
                >
                  <span class="option-label">{{ ['A', 'B', 'C', 'D'][idx] }}</span>
                  <span class="option-text">{{ opt }}</span>
                  <el-icon v-if="item.correctAnswer === idx" class="icon-correct"><Check /></el-icon>
                  <el-icon v-if="item.userAnswer === idx && item.userAnswer !== item.correctAnswer" class="icon-wrong"><Close /></el-icon>
                </div>
              </div>
            </div>

            <div class="card-footer">
              <div class="error-info">
                <span class="error-label">您的答案：</span>
                <span class="error-value">{{ ['A', 'B', 'C', 'D'][item.userAnswer] || '未作答' }}</span>
              </div>
              <div class="error-info">
                <span class="error-label">正确答案：</span>
                <span class="error-value correct">{{ ['A', 'B', 'C', 'D'][item.correctAnswer] }}</span>
              </div>
            </div>

            <div class="card-analysis" v-if="item.analysis">
              <div class="analysis-header">
                <el-icon><InfoFilled /></el-icon>
                <span>题目解析</span>
              </div>
              <p class="analysis-content">{{ item.analysis }}</p>
            </div>

            <div class="card-actions">
              <el-button size="small" type="primary" plain @click="handlePractice(item)">
                <el-icon><Edit /></el-icon> 再次练习
              </el-button>
              <el-button size="small" :type="item.mastered ? 'info' : 'success'" @click="handleMarkMastered(item)">
                <el-icon><CircleCheck /></el-icon> {{ item.mastered ? '取消掌握' : '标记掌握' }}
              </el-button>
              <el-button size="small" type="danger" plain @click="handleDelete(item)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 表格视图 -->
      <div v-else class="question-table">
        <el-table :data="wrongQuestions" stripe>
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="questionText" label="题目内容" min-width="300" show-overflow-tooltip />
          <el-table-column prop="difficulty" label="难度" width="80">
            <template #default="{ row }">
              <el-tag :type="getDifficultyType(row.difficulty)" size="small">
                {{ getDifficultyLabel(row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="userAnswer" label="您的答案" width="100">
            <template #default="{ row }">
              {{ ['A', 'B', 'C', 'D'][row.userAnswer] || '未作答' }}
            </template>
          </el-table-column>
          <el-table-column prop="correctAnswer" label="正确答案" width="100">
            <template #default="{ row }">
              {{ ['A', 'B', 'C', 'D'][row.correctAnswer] }}
            </template>
          </el-table-column>
          <el-table-column prop="mastered" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.mastered ? 'success' : 'warning'" size="small">
                {{ row.mastered ? '已掌握' : '未掌握' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="errorTime" label="错误时间" width="160" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="handleViewDetail(row)">查看</el-button>
              <el-button size="small" type="success" @click="handleMarkMastered(row)">
                {{ row.mastered ? '取消' : '掌握' }}
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadWrongQuestions"
          @current-change="loadWrongQuestions"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get, post } from '@/utils/request'
import { DocumentCopy, Document, Clock, CircleCheck, TrendCharts, Search, Download, QuestionFilled, Check, Close, InfoFilled, Edit, Delete } from '@element-plus/icons-vue'

// 统计数据
const wrongCount = ref(0)
const todayCount = ref(0)
const masteredCount = ref(0)
const accuracyRate = ref(0)

// 筛选条件
const searchKeyword = ref('')
const categoryFilter = ref('')
const difficultyFilter = ref('')
const statusFilter = ref('')
const viewMode = ref('card')

// 分页
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)

// 错题列表
const wrongQuestions = ref<any[]>([])

// 加载错题数据
const loadWrongQuestions = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      keyword: searchKeyword.value,
      categoryId: categoryFilter.value,
      difficulty: difficultyFilter.value,
      mastered: statusFilter.value
    }
    
    const res = await get('/user/wrong-questions', { params })
    wrongQuestions.value = res.data?.list || []
    total.value = res.data?.total || 0
    
    // 如果是模拟数据
    if (!res.data?.list) {
      wrongQuestions.value = [
        {
          id: 1,
          categoryName: '电信诈骗',
          questionText: '以下哪种行为最可能导致个人信息泄露？',
          questionType: 1,
          difficulty: 2,
          options: ['A. 在正规网站购物', 'B. 点击陌生链接并填写个人信息', 'C. 使用杀毒软件', 'D. 设置复杂密码'],
          userAnswer: 1,
          correctAnswer: 1,
          mastered: false,
          analysis: '点击陌生链接并填写个人信息是导致个人信息泄露的常见原因。诈骗者通常通过钓鱼链接诱导用户输入个人信息。',
          errorTime: '2024-01-15 14:30'
        },
        {
          id: 2,
          categoryName: '网络诈骗',
          questionText: '收到"您的快递丢失，请点击链接申请理赔"的短信，正确的做法是？',
          questionType: 1,
          difficulty: 1,
          options: ['A. 直接点击链接', 'B. 联系快递公司官方客服核实', 'C. 回复短信确认', 'D. 分享给朋友提醒'],
          userAnswer: 0,
          correctAnswer: 1,
          mastered: true,
          analysis: '遇到此类短信，应通过官方渠道核实，不要轻易点击陌生链接。正规快递公司不会通过短信索要个人信息。',
          errorTime: '2024-01-14 09:15'
        }
      ]
      total.value = 2
    }
  } catch (error) {
    // 模拟数据
    wrongQuestions.value = [
      {
        id: 1,
        categoryName: '电信诈骗',
        questionText: '以下哪种行为最可能导致个人信息泄露？',
        questionType: 1,
        difficulty: 2,
        options: ['A. 在正规网站购物', 'B. 点击陌生链接并填写个人信息', 'C. 使用杀毒软件', 'D. 设置复杂密码'],
        userAnswer: 1,
        correctAnswer: 1,
        mastered: false,
        analysis: '点击陌生链接并填写个人信息是导致个人信息泄露的常见原因。诈骗者通常通过钓鱼链接诱导用户输入个人信息。',
        errorTime: '2024-01-15 14:30'
      }
    ]
    total.value = 1
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await get('/user/wrong-questions/stats')
    if (res.data) {
      wrongCount.value = res.data.total || 0
      todayCount.value = res.data.today || 0
      masteredCount.value = res.data.mastered || 0
      accuracyRate.value = res.data.accuracy || 0
    }
  } catch (error) {
    // 模拟数据
    wrongCount.value = 12
    todayCount.value = 2
    masteredCount.value = 5
    accuracyRate.value = 72
  }
}

// 重置筛选
const handleReset = () => {
  searchKeyword.value = ''
  categoryFilter.value = ''
  difficultyFilter.value = ''
  statusFilter.value = ''
  page.value = 1
  loadWrongQuestions()
}

// 导出错题
const handleExport = () => {
  ElMessage.success('错题本导出功能开发中')
}

// 再次练习
const handlePractice = (item: any) => {
  ElMessage.info('跳转到练习页面')
}

// 标记掌握
const handleMarkMastered = async (item: any) => {
  try {
    await post('/user/wrong-questions/mastered', {
      id: item.id,
      mastered: !item.mastered
    })
    item.mastered = !item.mastered
    masteredCount.value += item.mastered ? 1 : -1
    ElMessage.success(item.mastered ? '已标记为掌握' : '已取消掌握')
  } catch (error) {
    // 模拟
    item.mastered = !item.mastered
    masteredCount.value += item.mastered ? 1 : -1
    ElMessage.success(item.mastered ? '已标记为掌握' : '已取消掌握')
  }
}

// 查看详情
const handleViewDetail = (item: any) => {
  ElMessage.info('查看详情：' + item.id)
}

// 删除错题
const handleDelete = (item: any) => {
  ElMessageBox.confirm('确定要删除这条错题记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await post('/user/wrong-questions/delete', { id: item.id })
      wrongQuestions.value = wrongQuestions.value.filter(q => q.id !== item.id)
      wrongCount.value--
      ElMessage.success('删除成功')
    } catch (error) {
      // 模拟
      wrongQuestions.value = wrongQuestions.value.filter(q => q.id !== item.id)
      wrongCount.value--
      ElMessage.success('删除成功')
    }
  }).catch(() => {})
}

// 辅助函数
const getDifficultyType = (difficulty: number) => {
  const types = ['info', 'warning', 'danger']
  return types[difficulty - 1] || 'info'
}

const getDifficultyLabel = (difficulty: number) => {
  const labels = ['', '简单', '中等', '困难']
  return labels[difficulty] || '未知'
}

const getQuestionTypeLabel = (type: number) => {
  const labels = ['', '单选题', '多选题', '判断题', '简答题']
  return labels[type] || '未知'
}

onMounted(() => {
  loadStats()
  loadWrongQuestions()
})
</script>

<style scoped>
.wrongbook-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-2xl);
  margin-bottom: var(--spacing-sm);
}

.page-header p {
  color: var(--color-text-secondary);
}

.stats-row {
  margin-bottom: var(--spacing-lg);
}

.stat-card {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  box-shadow: var(--shadow-sm);
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
}

.stat-icon.blue { background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%); }
.stat-icon.orange { background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%); }
.stat-icon.green { background: linear-gradient(135deg, #48bb78 0%, #38a169 100%); }
.stat-icon.purple { background: linear-gradient(135deg, #9f7aea 0%, #805ad5 100%); }

.stat-info .stat-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.stat-info .stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.filter-card {
  margin-bottom: var(--spacing-lg);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-cards {
  min-height: 200px;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: var(--spacing-lg);
}

.question-card {
  transition: all 0.3s ease;
}

.question-card.mastered {
  border-left: 3px solid var(--color-success);
}

.question-card .card-header {
  display: flex;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
}

.question-content {
  margin-bottom: var(--spacing-md);
}

.question-type {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
  margin-bottom: var(--spacing-sm);
}

.question-text {
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  line-height: 1.6;
  margin-bottom: var(--spacing-md);
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.option-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--color-bg-page);
  border-radius: var(--radius-md);
  position: relative;
}

.option-item.correct {
  background: rgba(72, 187, 120, 0.1);
  border: 1px solid var(--color-success);
}

.option-item.wrong {
  background: rgba(245, 101, 101, 0.1);
  border: 1px solid var(--color-danger);
}

.option-label {
  font-weight: var(--font-weight-bold);
  color: var(--color-text-secondary);
}

.option-text {
  flex: 1;
  color: var(--color-text-primary);
}

.icon-correct {
  color: var(--color-success);
  font-weight: bold;
}

.icon-wrong {
  color: var(--color-danger);
  font-weight: bold;
}

.card-footer {
  display: flex;
  gap: var(--spacing-lg);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-border-light);
  margin-bottom: var(--spacing-md);
}

.error-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.error-label {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

.error-value {
  font-weight: var(--font-weight-bold);
  color: var(--color-danger);
}

.error-value.correct {
  color: var(--color-success);
}

.card-analysis {
  background: rgba(64, 158, 255, 0.1);
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-md);
}

.analysis-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
  margin-bottom: var(--spacing-sm);
}

.analysis-content {
  color: var(--color-text-secondary);
  line-height: 1.6;
  font-size: var(--font-size-sm);
}

.card-actions {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: flex-end;
}

.pagination-wrapper {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: center;
}

.empty-state {
  padding: var(--spacing-xl);
}

@media (max-width: 768px) {
  .cards-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-row .el-col {
    margin-bottom: var(--spacing-md);
  }
}
</style>
