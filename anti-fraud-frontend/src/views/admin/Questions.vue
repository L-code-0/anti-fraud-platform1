<template>
  <div class="admin-questions">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>题库管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 添加题目
          </el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="题型">
          <el-select v-model="searchParams.questionType" placeholder="全部" clearable>
            <el-option :value="1" label="单选题" />
            <el-option :value="2" label="多选题" />
            <el-option :value="3" label="判断题" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchParams.difficulty" placeholder="全部" clearable>
            <el-option :value="1" label="简单" />
            <el-option :value="2" label="中等" />
            <el-option :value="3" label="困难" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchParams.category" placeholder="全部" clearable>
            <el-option label="电信诈骗" value="电信诈骗" />
            <el-option label="网络诈骗" value="网络诈骗" />
            <el-option label="校园贷" value="校园贷" />
            <el-option label="兼职诈骗" value="兼职诈骗" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadQuestions">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 题目列表 -->
      <el-table :data="questionList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="questionText" label="题目" min-width="250">
          <template #default="{ row }">
            <div class="question-cell">{{ row.questionText }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="questionType" label="题型" width="80">
          <template #default="{ row }">
            <el-tag>{{ ['单选', '多选', '判断'][row.questionType - 1] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="getDifficultyType(row.difficulty)">
              {{ ['简单', '中等', '困难'][row.difficulty - 1] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="score" label="分值" width="60" />
        <el-table-column prop="useCount" label="使用次数" width="100" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button text type="danger" @click="deleteQuestion(row)">删除</el-button>
          </template>
        </el-table-column>
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
    
    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑题目' : '添加题目'" width="700px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="题目类型">
          <el-radio-group v-model="form.questionType">
            <el-radio :value="1">单选题</el-radio>
            <el-radio :value="2">多选题</el-radio>
            <el-radio :value="3">判断题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="题目内容">
          <el-input v-model="form.questionText" type="textarea" :rows="2" placeholder="请输入题目" />
        </el-form-item>
        <el-form-item v-if="form.questionType !== 3" label="选项">
          <div v-for="(opt, index) in form.options" :key="index" class="option-item">
            <span class="option-label">{{ ['A', 'B', 'C', 'D'][index] }}.</span>
            <el-input v-model="form.options[index]" :placeholder="`选项${['A', 'B', 'C', 'D'][index]}`" />
          </div>
        </el-form-item>
        <el-form-item label="正确答案">
          <el-select v-model="form.correctAnswer" placeholder="请选择" :multiple="form.questionType === 2">
            <el-option v-if="form.questionType !== 3" label="A" value="A" />
            <el-option v-if="form.questionType !== 3" label="B" value="B" />
            <el-option v-if="form.questionType !== 3" label="C" value="C" />
            <el-option v-if="form.questionType !== 3" label="D" value="D" />
            <el-option v-if="form.questionType === 3" label="正确" value="A" />
            <el-option v-if="form.questionType === 3" label="错误" value="B" />
          </el-select>
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" :rows="2" placeholder="请输入解析" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="难度">
          <el-radio-group v-model="form.difficulty">
            <el-radio :value="1">简单</el-radio>
            <el-radio :value="2">中等</el-radio>
            <el-radio :value="3">困难</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分值">
          <el-input-number v-model="form.score" :min="1" :max="20" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post, put, del } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const questionList = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const editId = ref<number | null>(null)

const searchParams = reactive({
  questionType: null as number | null,
  difficulty: null as number | null,
  category: ''
})

const form = reactive({
  questionType: 1,
  questionText: '',
  options: ['', '', '', ''],
  correctAnswer: '' as string | string[],
  analysis: '',
  category: '',
  difficulty: 2,
  score: 5
})

const getDifficultyType = (difficulty: number) => {
  const types: Record<number, string> = { 1: 'success', 2: 'warning', 3: 'danger' }
  return types[difficulty] || 'info'
}

const loadQuestions = async () => {
  try {
    const res = await get('/admin/test/questions', {
      page: page.value,
      size: size.value,
      ...searchParams
    })
    questionList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    questionList.value = [
      { id: 1, questionText: '接到自称公安机关的电话，要求转账到"安全账户"配合调查，应该怎么做？', questionType: 1, difficulty: 1, category: '电信诈骗', score: 5, useCount: 156 },
      { id: 2, questionText: '以下哪些是网络兼职诈骗的常见特征？', questionType: 2, difficulty: 2, category: '兼职诈骗', score: 10, useCount: 98 },
      { id: 3, questionText: '校园贷只要按时还款就是安全的。', questionType: 3, difficulty: 1, category: '校园贷', score: 3, useCount: 210 }
    ]
    total.value = 3
  }
}

const openDialog = (row?: any) => {
  editId.value = row?.id || null
  if (row) {
    Object.assign(form, {
      questionType: row.questionType,
      questionText: row.questionText,
      options: row.options || ['', '', '', ''],
      correctAnswer: row.correctAnswer,
      analysis: row.analysis || '',
      category: row.category,
      difficulty: row.difficulty,
      score: row.score
    })
  } else {
    Object.assign(form, {
      questionType: 1,
      questionText: '',
      options: ['', '', '', ''],
      correctAnswer: '',
      analysis: '',
      category: '',
      difficulty: 2,
      score: 5
    })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.questionText || !form.correctAnswer) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    if (editId.value) {
      await put(`/admin/test/questions/${editId.value}`, form)
      ElMessage.success('更新成功')
    } else {
      await post('/admin/test/questions', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadQuestions()
  } catch (e) {}
}

const deleteQuestion = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除该题目吗？', '提示', { type: 'warning' })
    await del(`/admin/test/questions/${row.id}`)
    ElMessage.success('删除成功')
    loadQuestions()
  } catch (e) {}
}

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.admin-questions {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.question-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.option-label {
  width: 30px;
  font-weight: bold;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
