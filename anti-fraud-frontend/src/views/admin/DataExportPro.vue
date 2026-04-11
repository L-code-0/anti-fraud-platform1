<template>
  <div class="data-export">
    <el-card class="page-header-card">
      <div class="header-info">
        <h1>数据导出管理</h1>
        <p>支持多种格式导出，可进行批量操作</p>
      </div>
    </el-card>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="8">
        <el-card class="export-type-card">
          <template #header><span>选择数据类型</span></template>
          <div class="data-types">
            <div v-for="type in dataTypes" :key="type.value" 
              class="data-type-item" :class="{ 'selected': selectedTypes.includes(type.value) }"
              @click="toggleType(type.value)">
              <el-icon :size="24" :color="type.color"><component :is="type.icon" /></el-icon>
              <div class="type-info">
                <span class="type-name">{{ type.label }}</span>
                <span class="type-count">约 {{ type.count }} 条</span>
              </div>
              <el-checkbox v-model="selectedTypes" :value="type.value" @click.stop />
            </div>
          </div>
          <el-divider />
          <div class="selected-summary">
            <span>已选择 {{ selectedTypes.length }} 个数据项</span>
          </div>
        </el-card>

        <el-card class="format-card">
          <template #header><span>导出格式</span></template>
          <el-radio-group v-model="exportFormat" class="format-group">
            <el-radio value="xlsx">
              <div class="format-option">
                <el-icon :size="32" color="#67c23a"><Document /></el-icon>
                <span>Excel (.xlsx)</span>
              </div>
            </el-radio>
            <el-radio value="csv">
              <div class="format-option">
                <el-icon :size="32" color="#409eff"><Tickets /></el-icon>
                <span>CSV (.csv)</span>
              </div>
            </el-radio>
            <el-radio value="json">
              <div class="format-option">
                <el-icon :size="32" color="#e6a23c"><DocumentCopy /></el-icon>
                <span>JSON (.json)</span>
              </div>
            </el-radio>
            <el-radio value="pdf">
              <div class="format-option">
                <el-icon :size="32" color="#f56c6c"><Files /></el-icon>
                <span>PDF (.pdf)</span>
              </div>
            </el-radio>
          </el-radio-group>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="16">
        <el-card class="filter-card">
          <template #header><span>筛选条件</span></template>
          <el-form :model="filterForm" label-width="100px">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-form-item label="时间范围">
                  <el-date-picker v-model="filterForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="数据类型">
                  <el-select v-model="filterForm.dataCategory" placeholder="全部" style="width: 100%">
                    <el-option label="用户数据" value="user" />
                    <el-option label="学习数据" value="learning" />
                    <el-option label="测试数据" value="test" />
                    <el-option label="举报数据" value="report" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="用户角色">
                  <el-select v-model="filterForm.role" placeholder="全部" style="width: 100%">
                    <el-option label="学生" value="student" />
                    <el-option label="教师" value="teacher" />
                    <el-option label="专家" value="expert" />
                    <el-option label="管理员" value="admin" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="状态">
                  <el-select v-model="filterForm.status" placeholder="全部" style="width: 100%">
                    <el-option label="正常" value="active" />
                    <el-option label="禁用" value="inactive" />
                    <el-option label="全部" value="all" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="关键词">
                  <el-input v-model="filterForm.keyword" placeholder="请输入关键词搜索" clearable>
                    <template #prefix><el-icon><Search /></el-icon></template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-card>

        <el-card class="batch-card">
          <template #header>
            <span>批量操作</span>
          </template>
          <div class="batch-actions">
            <el-button type="primary" @click="handleExport" :disabled="selectedTypes.length === 0">
              <el-icon><Download /></el-icon> 导出数据
            </el-button>
            <el-button @click="handleBatchDelete" :disabled="selectedRows.length === 0">
              <el-icon><Delete /></el-icon> 批量删除
            </el-button>
            <el-button @click="handleBatchUpdate" :disabled="selectedRows.length === 0">
              <el-icon><Edit /></el-icon> 批量更新
            </el-button>
            <el-button @click="handleBatchExport" :disabled="selectedRows.length === 0">
              <el-icon><Download /></el-icon> 导出选中项
            </el-button>
          </div>

          <el-table ref="tableRef" :data="tableData" stripe @selection-change="handleSelectionChange" class="export-table">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="数据名称" min-width="150" />
            <el-table-column prop="category" label="分类" width="120">
              <template #default="{ row }">
                <el-tag size="small">{{ row.category }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="size" label="大小" width="100" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '正常' ? 'success' : 'danger'" size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleExportItem(row)">导出</el-button>
                <el-button link type="primary" @click="handleEditItem(row)">编辑</el-button>
                <el-button link type="danger" @click="handleDeleteItem(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="totalCount"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next"
            />
          </div>
        </el-card>

        <el-card class="export-history-card">
          <template #header>
            <span>导出历史</span>
            <el-button size="small" @click="loadHistory">刷新</el-button>
          </template>
          <el-table :data="exportHistory" stripe>
            <el-table-column prop="id" label="编号" width="80" />
            <el-table-column prop="fileName" label="文件名" min-width="200" />
            <el-table-column prop="format" label="格式" width="80">
              <template #default="{ row }">
                <el-tag size="small">{{ row.format.toUpperCase() }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="size" label="大小" width="100" />
            <el-table-column prop="createTime" label="导出时间" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '成功' ? 'success' : 'warning'" size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleDownload(row)">下载</el-button>
                <el-button link type="danger" @click="handleDeleteHistory(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showExportDialog" title="导出确认" width="500px">
      <div class="export-confirm">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="数据类型">{{ selectedTypeNames }}</el-descriptions-item>
          <el-descriptions-item label="导出格式">{{ exportFormat.toUpperCase() }}</el-descriptions-item>
          <el-descriptions-item label="预计记录数">约 {{ estimatedRecords }} 条</el-descriptions-item>
          <el-descriptions-item label="预计文件大小">约 {{ estimatedSize }}</el-descriptions-item>
        </el-descriptions>
        <el-alert type="info" :closable="false" class="export-tip">
          导出文件将在后台生成，完成后可在"导出历史"中下载
        </el-alert>
      </div>
      <template #footer>
        <el-button @click="showExportDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmExport">确认导出</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox, type TableInstance } from 'element-plus'
import { Download, Delete, Edit, Search, Document, Tickets, DocumentCopy, Files, User, Reading, EditPen, VideoPlay, Bell } from '@element-plus/icons-vue'

const tableRef = ref<TableInstance>()
const selectedTypes = ref<string[]>(['users', 'knowledge'])
const exportFormat = ref('xlsx')
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(50)
const selectedRows = ref<any[]>([])
const showExportDialog = ref(false)

const filterForm = reactive({
  dateRange: [],
  dataCategory: '',
  role: '',
  status: '',
  keyword: ''
})

const dataTypes = [
  { value: 'users', label: '用户数据', count: 125680, icon: User, color: '#409eff' },
  { value: 'knowledge', label: '知识内容', count: 2340, icon: Reading, color: '#67c23a' },
  { value: 'tests', label: '测试记录', count: 89560, icon: EditPen, color: '#e6a23c' },
  { value: 'simulations', label: '演练记录', count: 45678, icon: VideoPlay, color: '#f56c6c' },
  { value: 'reports', label: '举报数据', count: 1256, icon: Bell, color: '#909399' }
]

const tableData = ref([
  { id: 1, name: '用户数据_2026', category: '用户数据', size: '12.5MB', createTime: '2026-01-15 14:30', status: '正常' },
  { id: 2, name: '知识内容汇总', category: '知识内容', size: '8.2MB', createTime: '2026-01-14 10:20', status: '正常' },
  { id: 3, name: '测试记录_12月', category: '测试记录', size: '25.6MB', createTime: '2026-01-13 16:45', status: '正常' },
  { id: 4, name: '演练统计', category: '演练记录', size: '5.8MB', createTime: '2026-01-12 09:00', status: '正常' },
  { id: 5, name: '举报数据', category: '举报数据', size: '2.1MB', createTime: '2026-01-11 18:30', status: '正常' }
])

const exportHistory = ref([
  { id: 1001, fileName: '用户数据_20260115.xlsx', format: 'xlsx', size: '12.5MB', createTime: '2026-01-15 14:30', status: '成功' },
  { id: 1002, fileName: '学习记录_export.csv', format: 'csv', size: '8.2MB', createTime: '2026-01-14 10:20', status: '成功' },
  { id: 1003, fileName: '测试数据_导出.json', format: 'json', size: '25.6MB', createTime: '2026-01-13 16:45', status: '成功' }
])

const selectedTypeNames = computed(() => dataTypes.filter(t => selectedTypes.value.includes(t.value)).map(t => t.label).join('、'))

const estimatedRecords = computed(() => dataTypes.filter(t => selectedTypes.value.includes(t.value)).reduce((sum, t) => sum + t.count, 0))

const estimatedSize = computed(() => {
  const sizePerRecord = exportFormat.value === 'xlsx' ? 0.5 : exportFormat.value === 'csv' ? 0.2 : exportFormat.value === 'json' ? 0.8 : 1.2
  return (estimatedRecords.value * sizePerRecord / 1024).toFixed(1) + ' MB'
})

const toggleType = (value: string) => {
  const index = selectedTypes.value.indexOf(value)
  if (index === -1) selectedTypes.value.push(value)
  else selectedTypes.value.splice(index, 1)
}

const handleSelectionChange = (rows: any[]) => { selectedRows.value = rows }

const handleExport = () => { showExportDialog.value = true }

const confirmExport = () => {
  showExportDialog.value = false
  ElMessage.success('导出任务已创建，请在"导出历史"中查看进度')
}

const handleBatchDelete = async () => {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条数据吗？`, '批量删除', { type: 'warning' })
  ElMessage.success('批量删除成功')
}

const handleBatchUpdate = () => { ElMessage.info('批量更新功能开发中') }
const handleBatchExport = () => { ElMessage.info('导出选中项功能开发中') }
const handleExportItem = (row: any) => { ElMessage.success('正在导出: ' + row.name) }
const handleEditItem = (row: any) => { ElMessage.info('编辑功能开发中') }
const handleDeleteItem = async (row: any) => {
  await ElMessageBox.confirm(`确定要删除 "${row.name}" 吗？`, '删除确认', { type: 'warning' })
  ElMessage.success('删除成功')
}
const handleDownload = (row: any) => { ElMessage.success('开始下载: ' + row.fileName) }
const handleDeleteHistory = async (row: any) => {
  await ElMessageBox.confirm(`确定要删除 "${row.fileName}" 吗？`, '删除确认', { type: 'warning' })
  ElMessage.success('删除成功')
}
const loadHistory = () => { ElMessage.success('刷新成功') }
</script>

<style scoped>
.data-export { max-width: 1440px; margin: 0 auto; }
.page-header-card { margin-bottom: var(--spacing-6); }
.header-info h1 { margin: 0 0 var(--spacing-2); font-size: var(--font-size-3xl); }
.header-info p { margin: 0; color: var(--text-secondary); }
.export-type-card, .format-card { margin-bottom: var(--spacing-6); }
.data-types { display: flex; flex-direction: column; gap: var(--spacing-2); }
.data-type-item { display: flex; align-items: center; gap: var(--spacing-3); padding: var(--spacing-3); border: 1px solid var(--border-color); border-radius: var(--radius-md); cursor: pointer; transition: all var(--transition-fast); }
.data-type-item:hover { border-color: var(--primary-color); background: var(--hover-bg-color); }
.data-type-item.selected { border-color: var(--primary-color); background: rgba(64, 158, 255, 0.08); }
.type-info { flex: 1; display: flex; flex-direction: column; }
.type-name { font-weight: var(--font-weight-medium); }
.type-count { font-size: var(--font-size-xs); color: var(--text-secondary); }
.selected-summary { color: var(--text-secondary); font-size: var(--font-size-sm); }
.format-group { display: flex; flex-direction: column; gap: var(--spacing-3); }
.format-option { display: flex; align-items: center; gap: var(--spacing-2); }
.filter-card, .batch-card, .export-history-card { margin-bottom: var(--spacing-6); }
.batch-actions { display: flex; gap: var(--spacing-3); flex-wrap: wrap; margin-bottom: var(--spacing-4); }
.pagination-wrapper { display: flex; justify-content: center; margin-top: var(--spacing-4); }
.export-confirm { }
.export-tip { margin-top: var(--spacing-4); }
@media (max-width: 768px) {
  .batch-actions { flex-direction: column; }
  .batch-actions .el-button { width: 100%; }
}
</style>
