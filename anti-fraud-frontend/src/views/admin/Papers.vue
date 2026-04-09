<template>
  <div class="admin-papers">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>试卷管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 创建试卷
          </el-button>
        </div>
      </template>
      
      <!-- 试卷列表 -->
      <el-table :data="paperList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="paperName" label="试卷名称" min-width="200" />
        <el-table-column prop="paperType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ ['练习卷', '考试卷', '模拟卷'][row.paperType - 1] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column prop="questionCount" label="题数" width="80" />
        <el-table-column prop="duration" label="时长(分)" width="80" />
        <el-table-column prop="passScore" label="及格分" width="80" />
        <el-table-column prop="attemptCount" label="参与次数" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button text type="info" @click="manageQuestions(row)">题目</el-button>
            <el-button text type="danger" @click="deletePaper(row)">删除</el-button>
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
          @current-change="loadPapers"
        />
      </div>
    </el-card>
    
    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑试卷' : '创建试卷'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="试卷名称">
          <el-input v-model="form.paperName" placeholder="请输入试卷名称" />
        </el-form-item>
        <el-form-item label="试卷类型">
          <el-radio-group v-model="form.paperType">
            <el-radio :value="1">练习卷</el-radio>
            <el-radio :value="2">考试卷</el-radio>
            <el-radio :value="3">模拟卷</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="总分">
          <el-input-number v-model="form.totalScore" :min="10" :max="200" />
        </el-form-item>
        <el-form-item label="考试时长">
          <el-input-number v-model="form.duration" :min="10" :max="180" />
          <span style="margin-left: 10px">分钟</span>
        </el-form-item>
        <el-form-item label="及格分数">
          <el-input-number v-model="form.passScore" :min="0" :max="form.totalScore" />
        </el-form-item>
        <el-form-item label="试卷说明">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入试卷说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 题目管理对话框 -->
    <el-dialog v-model="questionVisible" title="试卷题目管理" width="800px">
      <div class="question-manage">
        <el-button type="primary" @click="addQuestions" style="margin-bottom: 15px">
          <el-icon><Plus /></el-icon> 添加题目
        </el-button>
        <el-table :data="paperQuestions" style="width: 100%">
          <el-table-column prop="orderNum" label="序号" width="60" />
          <el-table-column prop="questionText" label="题目" min-width="250">
            <template #default="{ row }">
              {{ row.questionText?.substring(0, 50) }}...
            </template>
          </el-table-column>
          <el-table-column prop="questionType" label="题型" width="80">
            <template #default="{ row }">
              {{ ['单选', '多选', '判断'][row.questionType - 1] }}
            </template>
          </el-table-column>
          <el-table-column prop="score" label="分值" width="80" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button text type="danger" @click="removeQuestion(row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post, put, del } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const paperList = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const questionVisible = ref(false)
const editId = ref<number | null>(null)
const currentPaperId = ref<number | null>(null)
const paperQuestions = ref<any[]>([])

const form = reactive({
  paperName: '',
  paperType: 1,
  totalScore: 100,
  duration: 60,
  passScore: 60,
  description: ''
})

const loadPapers = async () => {
  try {
    const res = await get('/admin/test/papers', { page: page.value, size: size.value })
    paperList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    paperList.value = [
      { id: 1, paperName: '反诈基础知识测试', paperType: 1, totalScore: 100, questionCount: 20, duration: 30, passScore: 60, attemptCount: 256, status: 1 },
      { id: 2, paperName: '电信诈骗防范考试', paperType: 2, totalScore: 100, questionCount: 50, duration: 60, passScore: 70, attemptCount: 180, status: 1 },
      { id: 3, paperName: '网络安全知识竞赛', paperType: 3, totalScore: 150, questionCount: 100, duration: 90, passScore: 90, attemptCount: 98, status: 1 }
    ]
    total.value = 3
  }
}

const openDialog = (row?: any) => {
  editId.value = row?.id || null
  if (row) {
    Object.assign(form, {
      paperName: row.paperName,
      paperType: row.paperType,
      totalScore: row.totalScore,
      duration: row.duration,
      passScore: row.passScore,
      description: row.description || ''
    })
  } else {
    Object.assign(form, {
      paperName: '',
      paperType: 1,
      totalScore: 100,
      duration: 60,
      passScore: 60,
      description: ''
    })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.paperName) {
    ElMessage.warning('请填写试卷名称')
    return
  }
  
  try {
    if (editId.value) {
      await put(`/admin/test/papers/${editId.value}`, form)
      ElMessage.success('更新成功')
    } else {
      await post('/admin/test/papers', form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadPapers()
  } catch (e) {}
}

const manageQuestions = (row: any) => {
  currentPaperId.value = row.id
  paperQuestions.value = [
    { id: 1, orderNum: 1, questionText: '接到自称公安机关的电话，要求转账到"安全账户"配合调查，应该怎么做？', questionType: 1, score: 5 },
    { id: 2, orderNum: 2, questionText: '以下哪些是网络兼职诈骗的常见特征？', questionType: 2, score: 5 },
    { id: 3, orderNum: 3, questionText: '校园贷只要按时还款就是安全的。', questionType: 3, score: 5 }
  ]
  questionVisible.value = true
}

const addQuestions = () => {
  ElMessage.info('题目选择功能开发中')
}

const removeQuestion = (row: any) => {
  const index = paperQuestions.value.findIndex(q => q.id === row.id)
  if (index > -1) {
    paperQuestions.value.splice(index, 1)
  }
}

const deletePaper = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除该试卷吗？', '提示', { type: 'warning' })
    await del(`/admin/test/papers/${row.id}`)
    ElMessage.success('删除成功')
    loadPapers()
  } catch (e) {}
}

onMounted(() => {
  loadPapers()
})
</script>

<style scoped>
.admin-papers {
  max-width: 1400px;
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
</style>
