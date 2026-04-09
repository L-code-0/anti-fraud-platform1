<template>
  <div class="admin-warnings">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预警管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 发布预警
          </el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="类型">
          <el-select v-model="searchParams.warningType" placeholder="全部" clearable>
            <el-option :value="1" label="高危预警" />
            <el-option :value="2" label="典型案例" />
            <el-option :value="3" label="安全提醒" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="searchParams.level" placeholder="全部" clearable>
            <el-option :value="1" label="一般" />
            <el-option :value="2" label="重要" />
            <el-option :value="3" label="紧急" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadWarnings">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 预警列表 -->
      <el-table :data="warningList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="title-cell">
              <span>{{ row.title }}</span>
              <el-tag v-if="row.level === 3" type="danger" size="small">紧急</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="warningType" label="类型" width="100">
          <template #default="{ row }">
            {{ ['高危预警', '典型案例', '安全提醒'][row.warningType - 1] }}
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">
              {{ ['一般', '重要', '紧急'][row.level - 1] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button 
              v-if="row.status === 0" 
              text 
              type="success" 
              @click="publishWarning(row)"
            >发布</el-button>
            <el-button text type="danger" @click="deleteWarning(row)">删除</el-button>
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
          @current-change="loadWarnings"
        />
      </div>
    </el-card>
    
    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑预警' : '发布预警'" width="700px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="预警类型">
          <el-radio-group v-model="form.warningType">
            <el-radio :value="1">高危预警</el-radio>
            <el-radio :value="2">典型案例</el-radio>
            <el-radio :value="3">安全提醒</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="预警级别">
          <el-radio-group v-model="form.level">
            <el-radio :value="1">一般</el-radio>
            <el-radio :value="2">重要</el-radio>
            <el-radio :value="3">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="诈骗类型">
          <el-input v-model="form.fraudType" placeholder="请输入诈骗类型" />
        </el-form-item>
        <el-form-item label="预警内容">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入预警内容" />
        </el-form-item>
        <el-form-item label="防范建议">
          <el-input v-model="form.suggestion" type="textarea" :rows="3" placeholder="请输入防范建议" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="info" @click="submitForm(false)">保存草稿</el-button>
        <el-button type="primary" @click="submitForm(true)">立即发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post, put, del } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const warningList = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const editId = ref<number | null>(null)

const searchParams = reactive({
  warningType: null as number | null,
  level: null as number | null
})

const form = reactive({
  title: '',
  warningType: 1,
  level: 2,
  fraudType: '',
  content: '',
  suggestion: ''
})

const getLevelType = (level: number) => {
  const types: Record<number, string> = { 1: 'info', 2: 'warning', 3: 'danger' }
  return types[level] || 'info'
}

const loadWarnings = async () => {
  try {
    const res = await get('/admin/warning/list', {
      page: page.value,
      size: size.value,
      ...searchParams
    })
    warningList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    warningList.value = [
      { id: 1, title: '冒充公检法诈骗高危预警', warningType: 1, level: 3, status: 1, viewCount: 3256, publishTime: '2024-01-15' },
      { id: 2, title: '近期网络兼职诈骗案例通报', warningType: 2, level: 2, status: 1, viewCount: 2180, publishTime: '2024-01-14' },
      { id: 3, title: '寒假期间安全提醒', warningType: 3, level: 1, status: 1, viewCount: 1560, publishTime: '2024-01-13' }
    ]
    total.value = 3
  }
}

const openDialog = (row?: any) => {
  editId.value = row?.id || null
  if (row) {
    Object.assign(form, {
      title: row.title,
      warningType: row.warningType,
      level: row.level,
      fraudType: row.fraudType || '',
      content: row.content || '',
      suggestion: row.suggestion || ''
    })
  } else {
    Object.assign(form, {
      title: '',
      warningType: 1,
      level: 2,
      fraudType: '',
      content: '',
      suggestion: ''
    })
  }
  dialogVisible.value = true
}

const submitForm = async (publish: boolean) => {
  if (!form.title || !form.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    const data = { ...form, status: publish ? 1 : 0 }
    if (editId.value) {
      await put(`/admin/warning/${editId.value}`, data)
      ElMessage.success('更新成功')
    } else {
      await post('/admin/warning', data)
      ElMessage.success(publish ? '发布成功' : '已保存草稿')
    }
    dialogVisible.value = false
    loadWarnings()
  } catch (e) {}
}

const publishWarning = async (row: any) => {
  try {
    await put(`/admin/warning/${row.id}/publish`)
    ElMessage.success('发布成功')
    loadWarnings()
  } catch (e) {}
}

const deleteWarning = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除该预警吗？', '提示', { type: 'warning' })
    await del(`/admin/warning/${row.id}`)
    ElMessage.success('删除成功')
    loadWarnings()
  } catch (e) {}
}

onMounted(() => {
  loadWarnings()
})
</script>

<style scoped>
.admin-warnings {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
