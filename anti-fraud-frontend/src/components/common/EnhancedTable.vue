<template>
  <div class="enhanced-table">
    <!-- 表格工具栏 -->
    <div class="table-toolbar" v-if="showToolbar">
      <div class="toolbar-left">
        <slot name="toolbar-left" />
      </div>
      <div class="toolbar-right">
        <el-input
          v-if="searchable"
          v-model="searchText"
          placeholder="搜索..."
          prefix-icon="Search"
          clearable
          class="search-input"
        />
        <el-button-group v-if="showActions">
          <el-button :icon="Refresh" @click="handleRefresh" />
          <el-button :icon="Download" @click="handleExport" />
          <el-button :icon="Setting" @click="showColumnSettings = true" />
        </el-button-group>
        <slot name="toolbar-right" />
      </div>
    </div>
    
    <!-- 表格主体 -->
    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="filteredData"
      :stripe="stripe"
      :border="border"
      :height="height"
      :max-height="maxHeight"
      :row-key="rowKey"
      :highlight-current-row="highlightCurrentRow"
      :empty-text="emptyText"
      :default-expand-all="defaultExpandAll"
      :tree-props="treeProps"
      @selection-change="handleSelectionChange"
      @sort-change="handleSort"
      @row-click="handleRowClick"
      class="data-table"
    >
      <slot />
      
      <template #empty>
        <div class="empty-wrapper">
          <el-empty :description="emptyText" :image-size="120">
            <template #description>
              <p class="empty-text">{{ emptyText }}</p>
            </template>
            <el-button type="primary" @click="handleRefresh">刷新数据</el-button>
          </el-empty>
        </div>
      </template>
    </el-table>
    
    <!-- 分页 -->
    <div class="table-pagination" v-if="showPagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="pageSizes"
        :total="total"
        :layout="paginationLayout"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 列设置抽屉 -->
    <el-drawer
      v-model="showColumnSettings"
      title="列设置"
      direction="rtl"
      size="300px"
    >
      <div class="column-settings">
        <el-checkbox-group v-model="visibleColumns">
          <div v-for="col in allColumns" :key="col.prop" class="column-item">
            <el-checkbox :label="col.prop">{{ col.label }}</el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Refresh, Download, Setting } from '@element-plus/icons-vue'

interface Props {
  data: any[]
  loading?: boolean
  stripe?: boolean
  border?: boolean
  height?: string | number
  maxHeight?: string | number
  rowKey?: string | ((row: any) => string)
  highlightCurrentRow?: boolean
  emptyText?: string
  defaultExpandAll?: boolean
  treeProps?: any
  showToolbar?: boolean
  showPagination?: boolean
  searchable?: boolean
  showActions?: boolean
  total?: number
  page?: number
  limit?: number
  pageSizes?: number[]
  paginationLayout?: string
  columns?: { prop: string; label: string }[]
}

const props = withDefaults(defineProps<Props>(), {
  data: () => [],
  loading: false,
  stripe: true,
  border: false,
  highlightCurrentRow: true,
  emptyText: '暂无数据',
  defaultExpandAll: false,
  showToolbar: true,
  showPagination: true,
  searchable: true,
  showActions: true,
  total: 0,
  page: 1,
  limit: 10,
  pageSizes: () => [10, 20, 50, 100],
  paginationLayout: 'total, sizes, prev, pager, next, jumper',
  columns: () => []
})

const emit = defineEmits<{
  'update:page': [value: number]
  'update:limit': [value: number]
  'refresh': []
  'export': []
  'selection-change': [value: any[]]
  'sort': [value: any]
  'row-click': [value: any]
}>()

const tableRef = ref()
const searchText = ref('')
const currentPage = ref(props.page)
const pageSize = ref(props.limit)
const showColumnSettings = ref(false)
const allColumns = ref(props.columns)
const visibleColumns = ref(props.columns.map(c => c.prop))

// 过滤数据
const filteredData = computed(() => {
  if (!searchText.value) return props.data
  
  const keyword = searchText.value.toLowerCase()
  return props.data.filter(row => {
    return Object.values(row).some(val => 
      String(val).toLowerCase().includes(keyword)
    )
  })
})

// 刷新
const handleRefresh = () => {
  emit('refresh')
}

// 导出
const handleExport = () => {
  emit('export')
}

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  emit('selection-change', selection)
}

// 排序变化
const handleSort = (sort: any) => {
  emit('sort', sort)
}

// 行点击
const handleRowClick = (row: any) => {
  emit('row-click', row)
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  emit('update:limit', size)
}

// 页码变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  emit('update:page', page)
}

// 监听外部page变化
watch(() => props.page, (val) => {
  currentPage.value = val
})

watch(() => props.limit, (val) => {
  pageSize.value = val
})

// 暴露方法
defineExpose({
  clearSelection: () => tableRef.value?.clearSelection(),
  toggleRowSelection: (row: any, selected?: boolean) => tableRef.value?.toggleRowSelection(row, selected),
  setCurrentRow: (row: any) => tableRef.value?.setCurrentRow(row)
})
</script>

<style scoped>
.enhanced-table {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--color-border-light);
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.search-input {
  width: 240px;
}

.data-table {
  --el-table-border-color: var(--color-border-light);
  --el-table-header-bg-color: var(--color-bg-hover);
  --el-table-header-text-color: var(--color-text-primary);
  --el-table-tr-hover-bg-color: var(--color-primary-bg);
}

.data-table :deep(.el-table__header th) {
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.data-table :deep(.el-table__row) {
  transition: all var(--transition-fast);
}

.data-table :deep(.el-table__row:hover > td) {
  background: var(--color-primary-bg) !important;
}

.empty-wrapper {
  padding: 48px 0;
}

.empty-text {
  color: var(--color-text-muted);
  margin-bottom: 16px;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  padding: var(--spacing-md) var(--spacing-lg);
  border-top: 1px solid var(--color-border-light);
}

.column-settings {
  padding: var(--spacing-md);
}

.column-item {
  padding: var(--spacing-sm) 0;
  border-bottom: 1px solid var(--color-border-light);
}

.column-item:last-child {
  border-bottom: none;
}

/* 响应式 */
@media (max-width: 768px) {
  .table-toolbar {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: stretch;
  }
  
  .toolbar-right {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    width: 100%;
  }
}
</style>

