<template>
  <div class="class-tasks">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>班级任务</span>
          <el-button type="primary" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon> 发布任务
          </el-button>
        </div>
      </template>
      
      <!-- 任务列表 -->
      <el-table :data="taskList" style="width: 100%">
        <el-table-column prop="taskName" label="任务名称" min-width="180" />
        <el-table-column prop="taskTypeName" label="任务类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.taskTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="relatedName" label="关联内容" width="150" />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="完成率" width="150">
          <template #default="{ row }">
            <el-progress :percentage="row.completionRate || 0" :format="() => `${row.completedStudents || 0}/${row.totalStudents || 0}`" />
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button text type="primary" @click="viewDetail(row)">查看</el-button>
            <el-button text type="info" @click="viewCompletions(row)">完成情况</el-button>
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
          @current-change="loadTasks"
        />
      </div>
    </el-card>
    
    <!-- 发布任务对话框 -->
    <el-dialog v-model="showCreateDialog" title="发布任务" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="任务名称">
          <el-input v-model="form.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务类型">
          <el-select v-model="form.taskType" placeholder="请选择">
            <el-option value="VIDEO" label="视频学习" />
            <el-option value="TEST" label="在线测试" />
            <el-option value="KNOWLEDGE" label="知识学习" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="form.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="任务时间">
          <el-date-picker
            v-model="form.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="关联内容">
          <el-input v-model="form.relatedName" placeholder="请输入关联的视频/测试/知识名称" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入任务描述" />
        </el-form-item>
        <el-form-item label="奖励积分">
          <el-input-number v-model="form.points" :min="0" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createTask">发布</el-button>
      </template>
    </el-dialog>
    
    <!-- 完成情况对话框 -->
    <el-dialog v-model="showCompletionsDialog" title="任务完成情况" width="800px">
      <el-table :data="completions" style="width: 100%">
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已完成' : '未完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="得分" width="80" />
        <el-table-column prop="completeTime" label="完成时间" width="180" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'

const taskList = ref<any[]>([])
const completions = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const showCreateDialog = ref(false)
const showCompletionsDialog = ref(false)

const form = reactive({
  taskName: '',
  taskType: 'VIDEO',
  className: '',
  timeRange: [] as string[],
  relatedName: '',
  description: '',
  points: 10
})

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

const loadTasks = async () => {
  try {
    const res = await get('/task/my', { page: page.value, size: size.value })
    taskList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    taskList.value = [
      { id: 1, taskName: '观看反诈视频', taskTypeName: '视频学习', className: '计科2201', relatedName: '冒充公检法诈骗案例分析', startTime: '2026-01-15', endTime: '2026-01-20', status: 1, statusName: '进行中', completionRate: 75, completedStudents: 30, totalStudents: 40 },
      { id: 2, taskName: '完成反诈测试', taskTypeName: '在线测试', className: '计科2201', relatedName: '电信诈骗防范测试', startTime: '2026-01-16', endTime: '2026-01-22', status: 1, statusName: '进行中', completionRate: 60, completedStudents: 24, totalStudents: 40 },
      { id: 3, taskName: '学习校园贷知识', taskTypeName: '知识学习', className: '计科2202', relatedName: '校园贷陷阱识别指南', startTime: '2026-01-14', endTime: '2026-01-18', status: 2, statusName: '已结束', completionRate: 90, completedStudents: 36, totalStudents: 40 }
    ]
    total.value = 3
  }
}

const createTask = async () => {
  if (!form.taskName || !form.taskType || form.timeRange.length !== 2) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    await post('/task', {
      ...form,
      startTime: form.timeRange[0],
      endTime: form.timeRange[1]
    })
    ElMessage.success('发布成功')
    showCreateDialog.value = false
    loadTasks()
  } catch (e) {}
}

const viewDetail = (row: any) => {
  ElMessage.info('任务详情功能开发中')
}

const viewCompletions = (row: any) => {
  completions.value = [
    { studentName: '张三', status: 1, score: 95, completeTime: '2026-01-16 10:30:00' },
    { studentName: '李四', status: 1, score: 88, completeTime: '2026-01-16 14:20:00' },
    { studentName: '王五', status: 0, score: null, completeTime: null },
    { studentName: '赵六', status: 1, score: 92, completeTime: '2026-01-17 09:15:00' }
  ]
  showCompletionsDialog.value = true
}

onMounted(() => {
  loadTasks()
})
</script>

<style scoped>
.class-tasks {
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
