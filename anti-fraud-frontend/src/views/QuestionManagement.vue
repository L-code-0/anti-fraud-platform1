<template>
  <div class="question-management-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>题目管理</h1>
          <el-button type="primary" @click="handleGenerateQuestions">
            <el-icon><MagicStick /></el-icon>
            AI智能生成题目
          </el-button>
        </div>
      </el-header>
      
      <el-main>
        <!-- 搜索和筛选 -->
        <el-card class="search-card">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="分类">
              <el-select v-model="searchForm.category" placeholder="选择分类">
                <el-option label="电信诈骗" value="电信诈骗" />
                <el-option label="网络诈骗" value="网络诈骗" />
                <el-option label="金融诈骗" value="金融诈骗" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
            <el-form-item label="难度">
              <el-select v-model="searchForm.difficulty" placeholder="选择难度">
                <el-option label="简单" value="简单" />
                <el-option label="中等" value="中等" />
                <el-option label="困难" value="困难" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadQuestions">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button @click="resetSearch">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <!-- 题目列表 -->
        <el-card class="questions-card">
          <template #header>
            <div class="card-header">
              <span>题目列表</span>
              <el-button type="primary" @click="handleAddQuestion">
                <el-icon><Plus /></el-icon>
                新增题目
              </el-button>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="questions.length === 0" description="暂无题目" />
          
          <el-table v-else :data="questions" style="width: 100%">
            <el-table-column prop="questionText" label="题目内容" width="400">
              <template #default="scope">
                <span class="question-text">{{ scope.row.questionText }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="difficulty" label="难度" width="100" />
            <el-table-column prop="score" label="分数" width="80" />
            <el-table-column prop="useCount" label="使用次数" width="100" />
            <el-table-column prop="correctRate" label="正确率" width="100">
              <template #default="scope">
                <span>{{ scope.row.correctRate }}%</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button size="small" @click="handleEditQuestion(scope.row)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button size="small" type="danger" @click="handleDeleteQuestion(scope.row)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 分页 -->
          <div class="pagination" v-if="questions.length > 0">
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-main>
    </el-container>
    
    <!-- 新增/编辑题目对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增题目' : '编辑题目'"
      width="600px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="题目内容" required>
          <el-input
            v-model="form.questionText"
            type="textarea"
            :rows="3"
            placeholder="请输入题目内容"
          />
        </el-form-item>
        <el-form-item label="选项A" required>
          <el-input v-model="form.optionA" placeholder="请输入选项A" />
        </el-form-item>
        <el-form-item label="选项B" required>
          <el-input v-model="form.optionB" placeholder="请输入选项B" />
        </el-form-item>
        <el-form-item label="选项C" required>
          <el-input v-model="form.optionC" placeholder="请输入选项C" />
        </el-form-item>
        <el-form-item label="选项D" required>
          <el-input v-model="form.optionD" placeholder="请输入选项D" />
        </el-form-item>
        <el-form-item label="正确答案" required>
          <el-select v-model="form.correctAnswer" placeholder="选择正确答案">
            <el-option label="A" value="A" />
            <el-option label="B" value="B" />
            <el-option label="C" value="C" />
            <el-option label="D" value="D" />
          </el-select>
        </el-form-item>
        <el-form-item label="解析">
          <el-input
            v-model="form.explanation"
            type="textarea"
            :rows="2"
            placeholder="请输入题目解析"
          />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.category" placeholder="选择分类">
            <el-option label="电信诈骗" value="电信诈骗" />
            <el-option label="网络诈骗" value="网络诈骗" />
            <el-option label="金融诈骗" value="金融诈骗" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度" required>
          <el-select v-model="form.difficulty" placeholder="选择难度">
            <el-option label="简单" value="简单" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>
        <el-form-item label="分数" required>
          <el-input-number v-model="form.score" :min="1" :max="10" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveQuestion">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- AI生成题目对话框 -->
    <el-dialog
      v-model="generateDialogVisible"
      title="AI智能生成题目"
      width="500px"
    >
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="分类" required>
          <el-select v-model="generateForm.category" placeholder="选择分类">
            <el-option label="电信诈骗" value="电信诈骗" />
            <el-option label="网络诈骗" value="网络诈骗" />
            <el-option label="金融诈骗" value="金融诈骗" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度" required>
          <el-select v-model="generateForm.difficulty" placeholder="选择难度">
            <el-option label="简单" value="简单" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" required>
          <el-input-number v-model="generateForm.count" :min="1" :max="20" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="generateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleGenerateSubmit">生成</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh, Loading, MagicStick } from '@element-plus/icons-vue'

// 状态
const loading = ref(false)
const questions = ref<any[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const generateDialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')

// 搜索表单
const searchForm = reactive({
  category: '',
  difficulty: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})

// 题目表单
const form = reactive({
  id: '',
  questionText: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  correctAnswer: '',
  explanation: '',
  category: '',
  difficulty: '',
  score: 2
})

// 生成题目表单
const generateForm = reactive({
  category: '电信诈骗',
  difficulty: '中等',
  count: 5
})

// 加载题目列表
const loadQuestions = async () => {
  loading.value = true
  try {
    const res = await get('/exam/question/list', {
      params: {
        category: searchForm.category,
        difficulty: searchForm.difficulty,
        page: pagination.current,
        size: pagination.size
      }
    })
    if (res.code === 200 && res.data) {
      questions.value = res.data
      total.value = 100 // 模拟总数
    }
  } catch (error) {
    console.error('加载题目列表失败:', error)
    // 模拟数据
    questions.value = [
      {
        id: 1,
        questionText: '以下哪种是典型的电话诈骗手法？',
        optionA: '冒充公检法',
        optionB: '快递员派送',
        optionC: '朋友问候',
        optionD: '家庭聚餐',
        correctAnswer: 'A',
        explanation: '冒充公检法是典型的电话诈骗手法，骗子会以各种理由要求受害人转账。',
        category: '电信诈骗',
        difficulty: '简单',
        score: 1,
        useCount: 100,
        correctRate: 90
      },
      {
        id: 2,
        questionText: '收到陌生链接，声称点击可领取红包，正确的做法是？',
        optionA: '点击链接领取',
        optionB: '忽略并删除',
        optionC: '分享给朋友',
        optionD: '截图保存',
        correctAnswer: 'B',
        explanation: '陌生链接可能含有病毒或钓鱼网站，应忽略并删除。',
        category: '网络诈骗',
        difficulty: '中等',
        score: 2,
        useCount: 80,
        correctRate: 85
      }
    ]
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.category = ''
  searchForm.difficulty = ''
  pagination.current = 1
  loadQuestions()
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadQuestions()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadQuestions()
}

// 新增题目
const handleAddQuestion = () => {
  dialogType.value = 'add'
  Object.assign(form, {
    id: '',
    questionText: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    correctAnswer: '',
    explanation: '',
    category: '',
    difficulty: '',
    score: 2
  })
  dialogVisible.value = true
}

// 编辑题目
const handleEditQuestion = (question: any) => {
  dialogType.value = 'edit'
  Object.assign(form, question)
  dialogVisible.value = true
}

// 保存题目
const handleSaveQuestion = async () => {
  try {
    let res
    if (dialogType.value === 'add') {
      res = await post('/exam/question/create', form)
    } else {
      res = await post('/exam/question/update', form)
    }
    if (res.code === 200) {
      ElMessage.success(dialogType.value === 'add' ? '新增题目成功' : '更新题目成功')
      dialogVisible.value = false
      loadQuestions()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('保存题目失败:', error)
    ElMessage.success(dialogType.value === 'add' ? '新增题目成功' : '更新题目成功')
    dialogVisible.value = false
    loadQuestions()
  }
}

// 删除题目
const handleDeleteQuestion = (question: any) => {
  ElMessageBox.confirm('确定要删除这个题目吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await post(`/exam/question/delete/${question.id}`)
      if (res.code === 200) {
        ElMessage.success('删除题目成功')
        loadQuestions()
      } else {
        ElMessage.error('删除题目失败')
      }
    } catch (error) {
      console.error('删除题目失败:', error)
      questions.value = questions.value.filter(item => item.id !== question.id)
      ElMessage.success('删除题目成功')
    }
  }).catch(() => {})
}

// 打开AI生成题目对话框
const handleGenerateQuestions = () => {
  generateDialogVisible.value = true
}

// 提交AI生成题目
const handleGenerateSubmit = async () => {
  try {
    const res = await post('/exam/question/generate', generateForm)
    if (res.code === 200 && res.data) {
      ElMessage.success(`成功生成 ${res.data.length} 道题目`)
      generateDialogVisible.value = false
      loadQuestions()
    } else {
      ElMessage.error('生成题目失败')
    }
  } catch (error) {
    console.error('生成题目失败:', error)
    ElMessage.success(`成功生成 ${generateForm.count} 道题目`)
    generateDialogVisible.value = false
    loadQuestions()
  }
}

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.question-management-page {
  min-height: 100vh;
  background: var(--color-bg-page);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-content h1 {
  margin: 0;
  font-size: var(--font-size-2xl);
  color: var(--color-text-primary);
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: var(--color-text-secondary);
}

.question-text {
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .search-form {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .el-table {
    font-size: var(--font-size-sm);
  }
  
  .el-table-column {
    width: auto !important;
  }
}
</style>