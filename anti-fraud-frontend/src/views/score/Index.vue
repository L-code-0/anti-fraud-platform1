<template>
  <div class="score-container">
    <el-card class="search-card">
      <template #header>
        <div class="card-header">
          <span>成绩查询</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="考试类型">
          <el-select v-model="searchForm.testType" placeholder="请选择" clearable>
            <el-option label="全部" value="" />
            <el-option label="知识测试" value="knowledge" />
            <el-option label="模拟演练" value="simulation" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="scoreList" v-loading="loading" stripe>
        <el-table-column prop="testName" label="考试名称" min-width="180" />
        <el-table-column prop="testType" label="考试类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.testType === 'knowledge' ? 'primary' : 'success'">
              {{ row.testType === 'knowledge' ? '知识测试' : '模拟演练' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="得分" width="100">
          <template #default="{ row }">
            <span :class="getScoreClass(row.score, row.totalScore)">
              {{ row.score }}/{{ row.totalScore }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="correctRate" label="正确率" width="120">
          <template #default="{ row }">
            <el-progress 
              :percentage="row.correctRate" 
              :color="getProgressColor(row.correctRate)"
              :stroke-width="10"
            />
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="用时" width="100">
          <template #default="{ row }">
            {{ formatDuration(row.duration) }}
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">详情</el-button>
            <el-button type="primary" link @click="handleReview(row)">回顾</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="成绩详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="考试名称">{{ currentRecord.testName }}</el-descriptions-item>
        <el-descriptions-item label="考试类型">
          <el-tag :type="currentRecord.testType === 'knowledge' ? 'primary' : 'success'">
            {{ currentRecord.testType === 'knowledge' ? '知识测试' : '模拟演练' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="得分">{{ currentRecord.score }}/{{ currentRecord.totalScore }}</el-descriptions-item>
        <el-descriptions-item label="正确率">{{ currentRecord.correctRate }}%</el-descriptions-item>
        <el-descriptions-item label="用时">{{ formatDuration(currentRecord.duration) }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentRecord.submitTime }}</el-descriptions-item>
      </el-descriptions>
      
      <div class="answer-detail" v-if="currentRecord.answers">
        <h4>答题详情</h4>
        <div v-for="(answer, index) in currentRecord.answers" :key="index" class="answer-item">
          <div class="question-text">
            <span :class="answer.isCorrect ? 'correct' : 'wrong'">
              {{ answer.isCorrect ? '✓' : '✗' }}
            </span>
            {{ index + 1 }}. {{ answer.questionText }}
          </div>
          <div class="answer-text">
            你的答案：<span :class="answer.isCorrect ? 'correct' : 'wrong'">{{ answer.userAnswer }}</span>
            <span v-if="!answer.isCorrect" class="correct-answer">
              正确答案：{{ answer.correctAnswer }}
            </span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

interface ScoreRecord {
  id: number
  testName: string
  testType: string
  score: number
  totalScore: number
  correctRate: number
  duration: number
  submitTime: string
  answers?: any[]
}

const loading = ref(false)
const detailVisible = ref(false)
const scoreList = ref<ScoreRecord[]>([])
const currentRecord = ref<ScoreRecord>({} as ScoreRecord)

const searchForm = reactive({
  testType: '',
  dateRange: null as any
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 模拟数据
const mockData: ScoreRecord[] = [
  {
    id: 1,
    testName: '电信诈骗防范知识测试',
    testType: 'knowledge',
    score: 85,
    totalScore: 100,
    correctRate: 85,
    duration: 1200,
    submitTime: '2026-01-15 14:30:00'
  },
  {
    id: 2,
    testName: '网络诈骗模拟演练',
    testType: 'simulation',
    score: 90,
    totalScore: 100,
    correctRate: 90,
    duration: 1800,
    submitTime: '2026-01-14 10:00:00'
  },
  {
    id: 3,
    testName: '金融诈骗防范测试',
    testType: 'knowledge',
    score: 72,
    totalScore: 100,
    correctRate: 72,
    duration: 900,
    submitTime: '2026-01-13 16:45:00'
  }
]

const loadData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    scoreList.value = mockData
    pagination.total = mockData.length
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.testType = ''
  searchForm.dateRange = null
  handleSearch()
}

const handleSizeChange = () => {
  loadData()
}

const handlePageChange = () => {
  loadData()
}

const handleViewDetail = (row: ScoreRecord) => {
  currentRecord.value = { ...row }
  detailVisible.value = true
}

const handleReview = (row: ScoreRecord) => {
  ElMessage.info('功能开发中')
}

const getScoreClass = (score: number, total: number) => {
  const rate = (score / total) * 100
  if (rate >= 80) return 'score-excellent'
  if (rate >= 60) return 'score-good'
  return 'score-poor'
}

const getProgressColor = (percentage: number) => {
  if (percentage >= 80) return '#67c23a'
  if (percentage >= 60) return '#e6a23c'
  return '#f56c6c'
}

const formatDuration = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}分${secs}秒`
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.score-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .search-form {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .table-card {
    .pagination {
      margin-top: 20px;
      justify-content: flex-end;
    }
  }

  .score-excellent {
    color: #67c23a;
    font-weight: bold;
  }

  .score-good {
    color: #e6a23c;
    font-weight: bold;
  }

  .score-poor {
    color: #f56c6c;
    font-weight: bold;
  }

  .answer-detail {
    margin-top: 20px;

    h4 {
      margin-bottom: 15px;
      color: #303133;
    }

    .answer-item {
      padding: 10px;
      margin-bottom: 10px;
      background: #f5f7fa;
      border-radius: 4px;

      .question-text {
        margin-bottom: 8px;
        font-weight: 500;
      }

      .answer-text {
        font-size: 14px;
        color: #606266;

        .correct {
          color: #67c23a;
        }

        .wrong {
          color: #f56c6c;
        }

        .correct-answer {
          margin-left: 15px;
          color: #67c23a;
        }
      }
    }
  }
}
</style>
