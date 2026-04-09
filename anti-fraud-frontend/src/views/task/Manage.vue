<template>
  <div class="task-manage-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <el-icon><Management /></el-icon>
          任务管理
        </h1>
        <p>创建和管理班级学习任务，追踪学生完成情况</p>
      </div>
      <el-button type="primary" size="large" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        创建任务
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #3b82f6;">
            <el-icon><List /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.total }}</span>
            <span class="stat-label">总任务数</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #10b981;">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.ongoing }}</span>
            <span class="stat-label">进行中</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #f59e0b;">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.pending }}</span>
            <span class="stat-label">未开始</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #6b7280;">
            <el-icon><Finished /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.finished }}</span>
            <span class="stat-label">已结束</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 任务列表 -->
    <el-card class="task-list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>我发布的任务</h3>
        </div>
      </template>

      <el-table :data="taskList" style="width: 100%">
        <el-table-column prop="taskName" label="任务名称" min-width="180" />
        <el-table-column prop="taskTypeName" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getTaskTypeTag(scope.row.taskType)" size="small">
              {{ scope.row.taskTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="150" />
        <el-table-column label="时间" width="200">
          <template #default="scope">
            <div class="time-cell">
              <span>{{ formatTime(scope.row.startTime) }}</span>
              <span>至 {{ formatTime(scope.row.endTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="完成情况" width="180">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.completionRate || 0"
              :stroke-width="6"
              :format="(p: number) => `${p}%`"
            />
            <span class="completion-text">{{ scope.row.completedStudents }}/{{ scope.row.totalStudents }} 人</span>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ scope.row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleViewDetail(scope.row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[10, 20, 30]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadTasks"
          @current-change="loadTasks"
        />
      </div>
    </el-card>

    <!-- 创建任务对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建学习任务"
      width="600px"
    >
      <el-form :model="taskForm" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="taskForm.taskName" placeholder="请输入任务名称" />
        </el-form-item>

        <el-form-item label="任务类型" prop="taskType">
          <el-select v-model="taskForm.taskType" placeholder="请选择任务类型" style="width: 100%">
            <el-option label="视频学习" value="VIDEO" />
            <el-option label="知识学习" value="KNOWLEDGE" />
            <el-option label="在线测试" value="TEST" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属班级" prop="classId">
          <el-select v-model="taskForm.classId" placeholder="请选择班级" style="width: 100%">
            <el-option
              v-for="cls in classList"
              :key="cls.id"
              :label="cls.className"
              :value="cls.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="关联资源" prop="relatedId">
          <el-select v-model="taskForm.relatedId" placeholder="请选择关联的视频/知识/测试" style="width: 100%">
            <el-option label="反诈视频教程" :value="1" />
            <el-option label="网络安全知识" :value="2" />
            <el-option label="防骗测试卷" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="时间范围" prop="timeRange">
          <el-date-picker
            v-model="taskForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="奖励积分" prop="points">
          <el-input-number v-model="taskForm.points" :min="0" :max="100" />
        </el-form-item>

        <el-form-item label="任务描述">
          <el-input
            v-model="taskForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入任务描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">创建任务</el-button>
      </template>
    </el-dialog>

    <!-- 任务完成情况对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="任务完成情况"
      width="800px"
    >
      <div v-if="currentTask" class="task-detail">
        <el-descriptions :column="2" border style="margin-bottom: 20px;">
          <el-descriptions-item label="任务名称">{{ currentTask.taskName }}</el-descriptions-item>
          <el-descriptions-item label="任务类型">{{ currentTask.taskTypeName }}</el-descriptions-item>
          <el-descriptions-item label="所属班级">{{ currentTask.className }}</el-descriptions-item>
          <el-descriptions-item label="完成率">{{ (currentTask.completionRate || 0).toFixed(1) }}%</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin-bottom: 12px;">学生完成列表</h4>
        <el-table :data="completionList" style="width: 100%">
          <el-table-column prop="studentName" label="学生姓名" width="120" />
          <el-table-column prop="studentNo" label="学号" width="150" />
          <el-table-column label="完成状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
                {{ scope.row.status === 1 ? '已完成' : '未完成' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="100">
            <template #default="scope">
              {{ scope.row.score !== null ? scope.row.score + ' 分' : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="completeTime" label="完成时间">
            <template #default="scope">
              {{ scope.row.completeTime || '-' }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post, del } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  Management, Plus, List, Check, Clock, Finished,
  View, Delete
} from '@element-plus/icons-vue'

// 统计数据
const stats = ref({
  total: 0,
  ongoing: 0,
  pending: 0,
  finished: 0
})

// 任务列表
const taskList = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 班级列表
const classList = ref<any[]>([])

// 对话框
const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const currentTask = ref<any>(null)
const completionList = ref<any[]>([])

// 表单
const formRef = ref<FormInstance>()
const taskForm = reactive({
  taskName: '',
  taskType: '',
  classId: null as number | null,
  relatedId: null as number | null,
  timeRange: [] as Date[],
  points: 10,
  description: ''
})

const formRules: FormRules = {
  taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  taskType: [{ required: true, message: '请选择任务类型', trigger: 'change' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  timeRange: [{ required: true, message: '请选择时间范围', trigger: 'change' }]
}

// 加载任务列表
const loadTasks = async () => {
  try {
    const res = await get('/task/my', { page: page.value, size: size.value })
    taskList.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0

    // 统计
    stats.value = {
      total: total.value,
      ongoing: taskList.value.filter(t => t.status === 1).length,
      pending: taskList.value.filter(t => t.status === 0).length,
      finished: taskList.value.filter(t => t.status === 2).length
    }
  } catch (e) {
    // 模拟数据
    taskList.value = Array.from({ length: 10 }, (_, i) => ({
      id: i + 1,
      taskName: ['电信诈骗防范学习', '网络安全知识测试', '反诈视频观看', '案例分析学习'][i % 4],
      taskType: ['VIDEO', 'TEST', 'KNOWLEDGE', 'VIDEO'][i % 4],
      taskTypeName: ['视频学习', '在线测试', '知识学习', '视频学习'][i % 4],
      className: `反诈安全 ${i % 3 + 1} 班`,
      startTime: new Date(Date.now() - i * 86400000).toISOString().slice(0, 16).replace('T', ' '),
      endTime: new Date(Date.now() + (7 - i) * 86400000).toISOString().slice(0, 16).replace('T', ' '),
      totalStudents: 45,
      completedStudents: Math.floor(Math.random() * 30) + 10,
      completionRate: Math.floor(Math.random() * 60) + 20,
      status: i < 3 ? 1 : (i < 6 ? 0 : 2),
      statusName: i < 3 ? '进行中' : (i < 6 ? '未开始' : '已结束')
    }))
    total.value = 10
    stats.value = { total: 10, ongoing: 3, pending: 3, finished: 4 }
  }
}

// 加载班级列表
const loadClasses = async () => {
  try {
    const res = await get('/class/list', { size: 100 })
    classList.value = res.data?.records || res.data || []
  } catch (e) {
    classList.value = [
      { id: 1, className: '反诈安全 1 班' },
      { id: 2, className: '反诈安全 2 班' },
      { id: 3, className: '反诈安全 3 班' }
    ]
  }
}

// 创建任务
const handleCreate = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const data = {
        taskName: taskForm.taskName,
        taskType: taskForm.taskType,
        classId: taskForm.classId,
        relatedId: taskForm.relatedId,
        startTime: taskForm.timeRange[0]?.toISOString(),
        endTime: taskForm.timeRange[1]?.toISOString(),
        points: taskForm.points,
        description: taskForm.description
      }
      await post('/task', data)
      ElMessage.success('创建成功')
      showCreateDialog.value = false
      resetForm()
      loadTasks()
    } catch (e) {
      ElMessage.error('创建失败')
    }
  })
}

// 重置表单
const resetForm = () => {
  taskForm.taskName = ''
  taskForm.taskType = ''
  taskForm.classId = null
  taskForm.relatedId = null
  taskForm.timeRange = []
  taskForm.points = 10
  taskForm.description = ''
}

// 查看详情
const handleViewDetail = async (task: any) => {
  currentTask.value = task
  try {
    const res = await get(`/task/${task.id}/completions`)
    completionList.value = res.data?.records || res.data || []
  } catch (e) {
    completionList.value = Array.from({ length: 10 }, (_, i) => ({
      studentName: `学生${i + 1}`,
      studentNo: `STU00${i + 1}`,
      status: i % 3 === 0 ? 1 : 0,
      score: i % 3 === 0 ? Math.floor(Math.random() * 40) + 60 : null,
      completeTime: i % 3 === 0 ? new Date().toISOString().slice(0, 16).replace('T', ' ') : null
    }))
  }
  showDetailDialog.value = true
}

// 删除任务
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个任务吗？', '删除确认', {
      type: 'warning'
    })
    await del(`/task/${id}`)
    ElMessage.success('删除成功')
    loadTasks()
  } catch (e) {
    // 取消删除
  }
}

// 获取任务类型标签
const getTaskTypeTag = (type: string) => {
  const types: Record<string, string> = {
    'VIDEO': 'primary',
    'TEST': 'warning',
    'KNOWLEDGE': 'success'
  }
  return types[type] || 'info'
}

// 获取状态类型
const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  return time.replace('T', ' ').slice(0, 16)
}

onMounted(() => {
  loadTasks()
  loadClasses()
})
</script>

<style scoped>
.task-manage-page {
  padding: 0 0 40px;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  padding: 40px 32px;
  margin-bottom: 24px;
  border-radius: 16px;
  color: white;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-header p {
  margin: 0;
  opacity: 0.9;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 任务列表 */
.task-list-card {
  border-radius: 12px;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
}

.time-cell {
  display: flex;
  flex-direction: column;
  font-size: 13px;
  color: var(--text-secondary);
}

.completion-text {
  font-size: 12px;
  color: var(--text-secondary);
}

/* 分页 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 任务详情 */
.task-detail h4 {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
}
</style>
