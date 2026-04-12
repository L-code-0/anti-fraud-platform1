<template>
  <div class="data-export-page">
    <!-- 头部 -->
    <div class="header">
      <h1>数据导入导出</h1>
    </div>

    <!-- 导入选项 -->
    <div class="import-options">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>数据导入</span>
          </div>
        </template>
        
        <el-form :model="importForm" label-width="120px">
          <el-form-item label="导入类型">
            <el-radio-group v-model="importForm.type">
              <el-radio value="users">用户数据</el-radio>
              <el-radio value="knowledge">知识内容</el-radio>
              <el-radio value="questions">题库数据</el-radio>
              <el-radio value="reports">举报记录</el-radio>
              <el-radio value="activities">活动数据</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="上传文件">
            <el-upload
              class="upload-demo"
              action=""
              :auto-upload="false"
              :on-change="handleFileChange"
              :limit="1"
              accept=".xlsx,.xls,.csv"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon>
                选择文件
              </el-button>
              <template #tip>
                <div class="el-upload__tip">
                  支持 .xlsx, .xls, .csv 格式文件
                </div>
              </template>
            </el-upload>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleImport" :loading="importing" :disabled="!importForm.file">
              <el-icon><Upload /></el-icon>
              导入数据
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 导出选项 -->
    <div class="export-options">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>选择导出内容</span>
          </div>
        </template>
        
        <el-form :model="exportForm" label-width="120px">
          <el-form-item label="导出类型">
            <el-radio-group v-model="exportForm.type">
              <el-radio value="users">用户数据</el-radio>
              <el-radio value="knowledge">知识内容</el-radio>
              <el-radio value="questions">题库数据</el-radio>
              <el-radio value="testRecords">考试记录</el-radio>
              <el-radio value="reports">举报记录</el-radio>
              <el-radio value="statistics">统计报表</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="导出格式">
            <el-radio-group v-model="exportForm.format">
              <el-radio value="xlsx">Excel (.xlsx)</el-radio>
              <el-radio value="csv">CSV (.csv)</el-radio>
              <el-radio value="pdf">PDF (.pdf)</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="exportForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item label="字段选择">
            <el-checkbox-group v-model="exportForm.fields">
              <el-checkbox value="id">ID</el-checkbox>
              <el-checkbox value="username">用户名</el-checkbox>
              <el-checkbox value="realName">姓名</el-checkbox>
              <el-checkbox value="email">邮箱</el-checkbox>
              <el-checkbox value="phone">电话</el-checkbox>
              <el-checkbox value="role">角色</el-checkbox>
              <el-checkbox value="createTime">创建时间</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleExport" :loading="exporting">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 导入记录 -->
    <div class="import-history">
      <h3>导入记录</h3>
      <el-table :data="importHistory" stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="type" label="导入类型" width="120">
          <template #default="{ row }">
            {{ getTypeText(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="fileName" label="文件名" width="200" />
        <el-table-column prop="fileSize" label="文件大小" width="100" />
        <el-table-column prop="totalCount" label="总记录" width="80" />
        <el-table-column prop="successCount" label="成功" width="80">
          <template #default="{ row }">
            <span style="color: #67c23a;">{{ row.successCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="errorCount" label="失败" width="80">
          <template #default="{ row }">
            <span style="color: #f56c6c;">{{ row.errorCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'warning'" size="small">
              {{ row.status === 'completed' ? '完成' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="导入时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button text type="danger" size="small" @click="handleDeleteImport(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 导出记录 -->
    <div class="export-history">
      <h3>导出记录</h3>
      <el-table :data="exportHistory" stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="type" label="导出类型" width="120">
          <template #default="{ row }">
            {{ getTypeText(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="format" label="格式" width="100" />
        <el-table-column prop="fileSize" label="文件大小" width="120" />
        <el-table-column prop="recordCount" label="记录数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'warning'" size="small">
              {{ row.status === 'completed' ? '完成' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="导出时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'completed' " 
              text 
              type="primary" 
              size="small"
              @click="handleDownload(row)"
            >
              下载
            </el-button>
            <el-button text type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 批量导出 -->
    <div class="batch-export">
      <h3>批量定时导出</h3>
      <el-table :data="scheduledExports" stripe>
        <el-table-column prop="name" label="任务名称" width="180" />
        <el-table-column prop="type" label="导出类型" width="120">
          <template #default="{ row }">
            {{ getTypeText(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="schedule" label="执行周期" width="120" />
        <el-table-column prop="nextRun" label="下次执行" width="180">
          <template #default="{ row }">
            {{ formatTime(row.nextRun) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch 
              v-model="row.enabled"
              @change="handleToggleSchedule(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleEditSchedule(row)">
              编辑
            </el-button>
            <el-button text type="danger" size="small" @click="handleDeleteSchedule(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="batch-actions">
        <el-button type="primary" @click="handleAddSchedule">
          <el-icon><Plus /></el-icon>
          添加定时任务
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Plus, Upload } from '@element-plus/icons-vue'

const exporting = ref(false)
const importing = ref(false)

const exportForm = reactive({
  type: 'users',
  format: 'xlsx',
  dateRange: [] as string[],
  fields: ['id', 'username', 'realName', 'createTime']
})

const importForm = reactive({
  type: 'users',
  file: null as any
})

const exportHistory = ref([
  { id: 1, type: 'users', format: 'xlsx', fileSize: '2.5MB', recordCount: 1250, status: 'completed', createdAt: new Date(Date.now() - 3600000).toISOString() },
  { id: 2, type: 'knowledge', format: 'xlsx', fileSize: '1.8MB', recordCount: 580, status: 'completed', createdAt: new Date(Date.now() - 86400000).toISOString() },
  { id: 3, type: 'testRecords', format: 'csv', fileSize: '856KB', recordCount: 3200, status: 'processing', createdAt: new Date().toISOString() }
])

const importHistory = ref([
  { id: 1, type: 'users', fileName: 'users.xlsx', fileSize: '1.2MB', totalCount: 500, successCount: 498, errorCount: 2, status: 'completed', createdAt: new Date(Date.now() - 7200000).toISOString() },
  { id: 2, type: 'knowledge', fileName: 'knowledge.xlsx', fileSize: '850KB', totalCount: 200, successCount: 200, errorCount: 0, status: 'completed', createdAt: new Date(Date.now() - 14400000).toISOString() }
])

const scheduledExports = ref([
  { id: 1, name: '每日用户统计', type: 'users', schedule: '每天 00:00', nextRun: new Date(Date.now() + 86400000).toISOString(), enabled: true },
  { id: 2, name: '每周考试报表', type: 'testRecords', schedule: '每周一 00:00', nextRun: new Date(Date.now() + 604800000).toISOString(), enabled: true },
  { id: 3, name: '月度举报汇总', type: 'reports', schedule: '每月 1日 00:00', nextRun: new Date(Date.now() + 2592000000).toISOString(), enabled: false }
])

function getTypeText(type: string): string {
  const textMap: Record<string, string> = {
    users: '用户数据',
    knowledge: '知识内容',
    questions: '题库数据',
    testRecords: '考试记录',
    reports: '举报记录',
    statistics: '统计报表',
    activities: '活动数据'
  }
  return textMap[type] || type
}

function formatTime(timeStr: string): string {
  return new Date(timeStr).toLocaleString()
}

async function handleExport() {
  exporting.value = true
  ElMessage.info('正在导出数据，请稍候...')
  
  setTimeout(() => {
    exporting.value = false
    ElMessage.success('导出成功！')
    exportHistory.value.unshift({
      id: Date.now(),
      type: exportForm.type,
      format: exportForm.format,
      fileSize: '1.2MB',
      recordCount: 850,
      status: 'completed',
      createdAt: new Date().toISOString()
    })
  }, 2000)
}

function handleDownload(row: any) {
  ElMessage.success(`正在下载 ${getTypeText(row.type)}...`)
}

function handleDelete(row: any) {
  const index = exportHistory.value.findIndex(item => item.id === row.id)
  if (index > -1) {
    exportHistory.value.splice(index, 1)
    ElMessage.success('删除成功')
  }
}

function handleToggleSchedule(row: any) {
  ElMessage.success(`定时任务已${row.enabled ? '启用' : '禁用'}`)
}

function handleEditSchedule(row: any) {
  ElMessage.info('编辑定时任务')
}

function handleDeleteSchedule(row: any) {
  const index = scheduledExports.value.findIndex(item => item.id === row.id)
  if (index > -1) {
    scheduledExports.value.splice(index, 1)
    ElMessage.success('删除成功')
  }
}

function handleAddSchedule() {
  ElMessage.info('添加定时任务')
}

function handleFileChange(file: any) {
  importForm.file = file.raw
}

async function handleImport() {
  if (!importForm.file) {
    ElMessage.warning('请选择要导入的文件')
    return
  }
  
  importing.value = true
  ElMessage.info('正在导入数据，请稍候...')
  
  setTimeout(() => {
    importing.value = false
    ElMessage.success('导入成功！')
    importHistory.value.unshift({
      id: Date.now(),
      type: importForm.type,
      fileName: importForm.file.name,
      fileSize: (importForm.file.size / 1024 / 1024).toFixed(2) + 'MB',
      totalCount: 500,
      successCount: 498,
      errorCount: 2,
      status: 'completed',
      createdAt: new Date().toISOString()
    })
    importForm.file = null
  }, 2000)
}

function handleDeleteImport(row: any) {
  const index = importHistory.value.findIndex(item => item.id === row.id)
  if (index > -1) {
    importHistory.value.splice(index, 1)
    ElMessage.success('删除成功')
  }
}
</script>

<style scoped>
.data-export-page {
  padding: 24px;
}

.header {
  margin-bottom: 24px;
}

.header h1 {
  margin: 0;
  font-size: 24px;
}

.import-options,
.export-options {
  margin-bottom: 32px;
}

.card-header {
  font-weight: 600;
}

.import-history,
.export-history,
.batch-export {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.export-history h3,
.batch-export h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #333;
}

.batch-actions {
  margin-top: 16px;
  text-align: right;
}

@media (max-width: 768px) {
  .data-export-page {
    padding: 16px;
  }
}
</style>
