<template>
  <div class="operation-log-page">
    <!-- 头部 -->
    <div class="header">
      <h1>操作日志</h1>
      <div class="header-actions">
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出日志
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="filterForm.actionType" placeholder="请选择" clearable>
            <el-option value="create" label="创建" />
            <el-option value="update" label="更新" />
            <el-option value="delete" label="删除" />
            <el-option value="login" label="登录" />
            <el-option value="logout" label="登出" />
            <el-option value="export" label="导出" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作用户">
          <el-input v-model="filterForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 日志列表 -->
    <div class="log-list" v-loading="loading">
      <el-table :data="logs" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="操作用户" width="120" />
        <el-table-column prop="actionType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getActionTypeColor(row.actionType)" size="small">
              {{ getActionTypeText(row.actionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="description" label="操作描述" min-width="200" />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="userAgent" label="设备信息" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleViewDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="600px">
      <el-descriptions :column="2" border v-if="currentLog">
        <el-descriptions-item label="日志ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作用户">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getActionTypeColor(currentLog.actionType)" size="small">
            {{ getActionTypeText(currentLog.actionType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作模块">{{ currentLog.module }}</el-descriptions-item>
        <el-descriptions-item label="IP地址" :span="2">{{ currentLog.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">{{ formatTime(currentLog.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="操作描述" :span="2">{{ currentLog.description }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre class="json-content">{{ JSON.stringify(currentLog.params, null, 2) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="设备信息" :span="2">{{ currentLog.userAgent }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Refresh } from '@element-plus/icons-vue'
import { getOperationLogList, getOperationLogDetail, exportOperationLogs } from '@/api/log'

const loading = ref(false)
const detailVisible = ref(false)
const currentLog = ref<any>(null)

const filterForm = reactive({
  dateRange: [] as string[],
  actionType: '',
  username: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

const logs = ref<any[]>([])

function getActionTypeColor(type: string): string {
  const colorMap: Record<string, string> = {
    create: 'success',
    update: 'warning',
    delete: 'danger',
    login: 'primary',
    logout: 'info',
    export: 'primary',
    execute: 'info',
    error: 'danger'
  }
  return colorMap[type] || 'info'
}

function getActionTypeText(type: string): string {
  const textMap: Record<string, string> = {
    create: '创建',
    update: '更新',
    delete: '删除',
    login: '登录',
    logout: '登出',
    export: '导出',
    execute: '执行',
    error: '异常'
  }
  return textMap[type] || type
}

function formatTime(timeStr: string): string {
  return new Date(timeStr).toLocaleString()
}

function handleSearch() {
  pagination.currentPage = 1
  fetchLogs()
}

function handleReset() {
  filterForm.dateRange = []
  filterForm.actionType = ''
  filterForm.username = ''
  pagination.currentPage = 1
  fetchLogs()
}

function handleRefresh() {
  fetchLogs()
  ElMessage.success('刷新成功')
}

function handleExport() {
  loading.value = true
  exportOperationLogs({
    username: filterForm.username,
    actionType: filterForm.actionType,
    startTime: filterForm.dateRange[0] || '',
    endTime: filterForm.dateRange[1] || ''
  }).then(() => {
    ElMessage.success('日志导出成功')
  }).catch(() => {
    ElMessage.error('日志导出失败')
  }).finally(() => {
    loading.value = false
  })
}

function handleViewDetail(row: any) {
  loading.value = true
  getOperationLogDetail(row.id).then(response => {
    currentLog.value = response.data
    detailVisible.value = true
  }).catch(() => {
    ElMessage.error('获取日志详情失败')
  }).finally(() => {
    loading.value = false
  })
}

function handleSizeChange(size: number) {
  pagination.pageSize = size
  fetchLogs()
}

function handleCurrentChange(page: number) {
  pagination.currentPage = page
  fetchLogs()
}

function fetchLogs() {
  loading.value = true
  getOperationLogList({
    page: pagination.currentPage,
    size: pagination.pageSize,
    username: filterForm.username,
    actionType: filterForm.actionType,
    startTime: filterForm.dateRange[0] || '',
    endTime: filterForm.dateRange[1] || ''
  }).then(response => {
    logs.value = response.data.records
    pagination.total = response.data.total
  }).catch(() => {
    ElMessage.error('获取操作日志失败')
  }).finally(() => {
    loading.value = false
  })
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.operation-log-page {
  padding: 24px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header h1 {
  margin: 0;
  font-size: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-bar {
  padding: 20px;
  background: white;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.log-list {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

.json-content {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  font-size: 12px;
  max-height: 200px;
  overflow: auto;
  margin: 0;
}

@media (max-width: 768px) {
  .operation-log-page {
    padding: 16px;
  }
  
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>
