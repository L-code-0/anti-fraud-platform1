<template>
  <div class="task-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <el-icon><List /></el-icon>
          学习任务
        </h1>
        <p>查看和完成班级学习任务，提升防诈意识</p>
        <div class="header-stats">
          <div class="stat-item">
            <el-icon><Clock /></el-icon>
            <span>{{ pendingCount }} 待完成</span>
          </div>
          <div class="stat-item success">
            <el-icon><Check /></el-icon>
            <span>{{ completedCount }} 已完成</span>
          </div>
          <div class="stat-item warning">
            <el-icon><Timer /></el-icon>
            <span>{{ expiredCount }} 已过期</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 操作栏 -->
    <el-card class="action-card" shadow="hover">
      <div class="action-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索任务名称..."
          prefix-icon="Search"
          clearable
          size="large"
          class="search-input"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #append>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>

        <div class="filter-actions">
          <el-select v-model="taskType" placeholder="任务类型" clearable size="large" class="filter-select">
            <el-option label="全部" value="" />
            <el-option label="视频学习" value="VIDEO" />
            <el-option label="知识学习" value="KNOWLEDGE" />
            <el-option label="在线测试" value="TEST" />
          </el-select>

          <el-select v-model="taskStatus" placeholder="任务状态" clearable size="large" class="filter-select">
            <el-option label="全部" :value="-1" />
            <el-option label="进行中" :value="1" />
            <el-option label="未开始" :value="0" />
            <el-option label="已结束" :value="2" />
          </el-select>
        </div>
      </div>
    </el-card>

    <!-- 任务列表 -->
    <el-card class="task-list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>任务列表</h3>
          <span class="count-tag">{{ total }} 个任务</span>
        </div>
      </template>

      <div class="task-grid">
        <el-card
          v-for="task in taskList"
          :key="task.id"
          class="task-card"
          :class="getTaskCardClass(task)"
          shadow="hover"
        >
          <div class="task-header">
            <div class="task-info">
              <el-tag :type="getTaskTypeTag(task.taskType)" size="small">
                {{ task.taskTypeName }}
              </el-tag>
              <el-tag :type="getStatusType(task.status)" size="small" style="margin-left: 8px;">
                {{ task.statusName }}
              </el-tag>
            </div>
            <div class="task-points" v-if="task.points">
              <el-icon><Star /></el-icon>
              {{ task.points }} 积分
            </div>
          </div>

          <h4 class="task-title">{{ task.taskName }}</h4>
          <p class="task-desc">{{ task.description || '暂无描述' }}</p>

          <div class="task-meta">
            <div class="meta-item">
              <el-icon><School /></el-icon>
              <span>{{ task.className }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatTime(task.startTime) }} ~ {{ formatTime(task.endTime) }}</span>
            </div>
          </div>

          <div class="task-progress">
            <span class="progress-label">完成进度</span>
            <el-progress
              :percentage="task.completionRate || 0"
              :stroke-width="8"
              :format="(p: number) => `${p}%`"
            />
            <span class="progress-text">{{ task.completedStudents }}/{{ task.totalStudents }} 人已完成</span>
          </div>

          <div class="task-actions">
            <el-button type="primary" size="small" @click="handleTaskDetail(task.id)">
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
            <el-button
              v-if="task.myStatus === 0 && task.status === 1"
              type="success"
              size="small"
              @click="handleCompleteTask(task)"
            >
              <el-icon><Check /></el-icon>
              完成任务
            </el-button>
            <el-tag v-else-if="task.myStatus === 1" type="success" size="small">
              <el-icon><Check /></el-icon>
              已完成
            </el-tag>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <el-empty
        v-if="taskList.length === 0 && !loading"
        description="暂无学习任务"
        :image-size="200"
      />

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[9, 18, 27, 36]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 任务详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="currentTask?.taskName || '任务详情'"
      width="700px"
    >
      <div v-if="currentTask" class="task-detail">
        <div class="detail-section">
          <h4>任务信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="任务类型">{{ currentTask.taskTypeName }}</el-descriptions-item>
            <el-descriptions-item label="所属班级">{{ currentTask.className }}</el-descriptions-item>
            <el-descriptions-item label="发布者">{{ currentTask.creatorName }}</el-descriptions-item>
            <el-descriptions-item label="奖励积分">{{ currentTask.points || 0 }} 分</el-descriptions-item>
            <el-descriptions-item label="开始时间">{{ formatTime(currentTask.startTime) }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ formatTime(currentTask.endTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h4>任务描述</h4>
          <p>{{ currentTask.description || '暂无描述' }}</p>
        </div>

        <div class="detail-section">
          <h4>完成情况</h4>
          <el-progress
            :percentage="currentTask.completionRate || 0"
            :stroke-width="12"
          />
          <p class="completion-text">
            已有 {{ currentTask.completedStudents }} / {{ currentTask.totalStudents }} 人完成
          </p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDetailDialog = false">关闭</el-button>
          <el-button
            v-if="currentTask?.myStatus === 0 && currentTask?.status === 1"
            type="primary"
            @click="handleCompleteTask(currentTask)"
          >
            完成任务
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import {
  List, Clock, Check, Timer, Search, Star, School,
  Calendar, View
} from '@element-plus/icons-vue'

const router = useRouter()

// 搜索和筛选
const searchKeyword = ref('')
const taskType = ref('')
const taskStatus = ref(-1)
const page = ref(1)
const size = ref(9)
const total = ref(0)
const loading = ref(false)

// 统计数据
const pendingCount = ref(0)
const completedCount = ref(0)
const expiredCount = ref(0)

// 任务列表
const taskList = ref<any[]>([])

// 详情对话框
const showDetailDialog = ref(false)
const currentTask = ref<any>(null)

// 加载任务列表
const loadTasks = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      keyword: searchKeyword.value,
      taskType: taskType.value,
      status: taskStatus.value >= 0 ? taskStatus.value : undefined
    }
    const res = await get('/task/student', params)
    taskList.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0

    // 统计
    pendingCount.value = taskList.value.filter(t => t.myStatus === 0 && t.status === 1).length
    completedCount.value = taskList.value.filter(t => t.myStatus === 1).length
    expiredCount.value = taskList.value.filter(t => t.status === 2 && t.myStatus === 0).length
  } catch (e) {
    // 模拟数据
    taskList.value = Array.from({ length: 9 }, (_, i) => ({
      id: i + 1,
      taskName: ['电信诈骗防范学习', '网络安全知识测试', '反诈视频观看', '案例分析学习'][i % 4],
      taskType: ['VIDEO', 'TEST', 'KNOWLEDGE', 'VIDEO'][i % 4],
      taskTypeName: ['视频学习', '在线测试', '知识学习', '视频学习'][i % 4],
      className: `反诈安全 ${i % 3 + 1} 班`,
      creatorName: ['张老师', '李老师', '王老师'][i % 3],
      startTime: new Date(Date.now() - i * 86400000).toISOString().slice(0, 16).replace('T', ' '),
      endTime: new Date(Date.now() + (7 - i) * 86400000).toISOString().slice(0, 16).replace('T', ' '),
      totalStudents: 45,
      completedStudents: Math.floor(Math.random() * 30) + 10,
      completionRate: Math.floor(Math.random() * 60) + 20,
      status: i < 3 ? 1 : (i < 6 ? 0 : 2),
      statusName: i < 3 ? '进行中' : (i < 6 ? '未开始' : '已结束'),
      myStatus: i % 3 === 0 ? 1 : 0,
      description: '这是一个学习任务，请认真完成',
      points: [10, 20, 15, 30][i % 4]
    }))
    total.value = 25
    pendingCount.value = 4
    completedCount.value = 3
    expiredCount.value = 2
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  loadTasks()
}

// 分页
const handlePageChange = () => {
  loadTasks()
}

const handleSizeChange = () => {
  page.value = 1
  loadTasks()
}

// 查看任务详情
const handleTaskDetail = async (taskId: number) => {
  try {
    const res = await get(`/task/${taskId}`)
    currentTask.value = res.data
  } catch (e) {
    currentTask.value = taskList.value.find(t => t.id === taskId)
  }
  showDetailDialog.value = true
}

// 完成任务
const handleCompleteTask = async (task: any) => {
  try {
    await post(`/task/${task.id}/complete`)
    ElMessage.success('任务完成！获得 ' + (task.points || 0) + ' 积分')
    showDetailDialog.value = false
    loadTasks()
  } catch (e) {
    ElMessage.error('完成任务失败')
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

// 获取任务卡片样式
const getTaskCardClass = (task: any) => {
  if (task.myStatus === 1) return 'completed'
  if (task.status === 2) return 'expired'
  if (task.status === 1) return 'ongoing'
  return ''
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  return time.replace('T', ' ').slice(0, 16)
}

onMounted(() => {
  loadTasks()
})
</script>

<style scoped>
.task-page {
  padding: 0 0 40px;
  min-height: calc(100vh - 160px);
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  padding: 60px 24px;
  margin-bottom: 40px;
  border-radius: 16px;
  color: white;
  position: relative;
  overflow: hidden;
}

.header-content {
  position: relative;
  z-index: 1;
  text-align: center;
}

.page-header h1 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-header p {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 24px;
}

.header-stats {
  display: flex;
  justify-content: center;
  gap: 32px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 24px;
  backdrop-filter: blur(10px);
}

.stat-item.success {
  background: rgba(16, 185, 129, 0.3);
}

.stat-item.warning {
  background: rgba(245, 158, 11, 0.3);
}

/* 操作栏 */
.action-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.action-bar {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-input {
  flex: 1;
  max-width: 400px;
}

.filter-actions {
  display: flex;
  gap: 12px;
}

.filter-select {
  width: 140px;
}

/* 任务列表 */
.task-list-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.count-tag {
  color: var(--text-secondary);
  font-size: 14px;
}

.task-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

@media (max-width: 1200px) {
  .task-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .task-grid {
    grid-template-columns: 1fr;
  }
}

.task-card {
  border-radius: 12px;
  transition: all 0.3s;
}

.task-card.completed {
  border-left: 4px solid #10b981;
}

.task-card.ongoing {
  border-left: 4px solid #3b82f6;
}

.task-card.expired {
  border-left: 4px solid #ef4444;
  opacity: 0.7;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.task-points {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #f59e0b;
  font-weight: 600;
}

.task-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
  color: var(--text-primary);
}

.task-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.task-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
}

.task-progress {
  margin-bottom: 16px;
}

.progress-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-right: 8px;
}

.progress-text {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
  display: block;
}

.task-actions {
  display: flex;
  gap: 8px;
}

/* 详情对话框 */
.task-detail {
  padding: 16px 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.detail-section p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.6;
}

.completion-text {
  margin-top: 12px;
  text-align: center;
  color: var(--text-secondary);
}

/* 分页 */
.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
