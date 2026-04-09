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

// 模拟数据
function getMockLogs() {
  const types = ['create', 'update', 'delete', 'login', 'logout', 'export']
  const modules = ['用户管理', '知识管理', '题库管理', '试卷管理', '演练管理', '举报管理']
  const actions = [
    '创建了新的知识文章',
    '更新了知识内容',
    '删除了用户',
    '登录系统',
    '导出了数据报表',
    '审核通过举报信息'
  ]
  
  const mockLogs = []
  for (let i = 1; i <= 50; i++) {
    const typeIndex = Math.floor(Math.random() * types.length)
    mockLogs.push({
      id: i,
      username: `admin${i % 5}`,
      actionType: types[typeIndex],
      module: modules[Math.floor(Math.random() * modules.length)],
      description: actions[typeIndex],
      ipAddress: `192.168.1.${Math.floor(Math.random() * 255)}`,
      userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
      params: { id: i, name: 'test' },
      createdAt: new Date(Date.now() - Math.random() * 604800000).toISOString()
    })
  }
  return mockLogs
}

function getActionTypeColor(type: string): string {
  const colorMap: Record<string, string> = {
    create: 'success',
    update: 'warning',
    delete: 'danger',
    login: 'primary',
    logout: 'info',
    export: 'primary'
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
    export: '导出'
  }
  return textMap[type] || type
}

function formatTime(timeStr: string): string {
  return new Date(timeStr).toLocaleString()
}

function handleSearch() {
  fetchLogs()
}

function handleReset() {
  filterForm.dateRange = []
  filterForm.actionType = ''
  filterForm.username = ''
  fetchLogs()
}

function handleRefresh() {
  fetchLogs()
  ElMessage.success('刷新成功')
}

function handleExport() {
  ElMessage.success('日志导出中...')
  // 实际导出逻辑
}

function handleViewDetail(row: any) {
  currentLog.value = row
  detailVisible.value = true
}

function handleSizeChange() {
  fetchLogs()
}

function handleCurrentChange() {
  fetchLogs()
}

function fetchLogs() {
  loading.value = true
  setTimeout(() => {
    logs.value = getMockLogs()
    pagination.total = 50
    loading.value = false
  }, 500)
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
