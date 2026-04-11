<template>
  <div class="admin-activities">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>活动管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 创建活动
          </el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="全部" clearable>
            <el-option :value="0" label="未开始" />
            <el-option :value="1" label="进行中" />
            <el-option :value="2" label="已结束" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchParams.activityType" placeholder="全部" clearable>
            <el-option :value="1" label="线上活动" />
            <el-option :value="2" label="线下活动" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadActivities">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 活动列表 -->
      <el-table :data="activityList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="activityName" label="活动名称" min-width="180" />
        <el-table-column prop="activityType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.activityType === 1 ? 'success' : 'warning'">
              {{ row.activityType === 1 ? '线上' : '线下' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column prop="registrationCount" label="报名人数" width="100">
          <template #default="{ row }">
            {{ row.registrationCount }} / {{ row.maxParticipants }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ ['未开始', '进行中', '已结束'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button text type="info" @click="viewRegistrations(row)">报名</el-button>
            <el-button text type="danger" @click="deleteActivity(row)">删除</el-button>
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
          @current-change="loadActivities"
        />
      </div>
    </el-card>
    
    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑活动' : '创建活动'" width="700px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动名称">
          <el-input v-model="form.activityName" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动类型">
          <el-radio-group v-model="form.activityType">
            <el-radio :value="1">线上活动</el-radio>
            <el-radio :value="2">线下活动</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="活动时间">
          <el-date-picker
            v-model="form.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="form.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input-number v-model="form.maxParticipants" :min="1" :max="10000" />
        </el-form-item>
        <el-form-item label="活动积分">
          <el-input-number v-model="form.points" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="活动描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入活动描述" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="请输入封面图URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 报名列表对话框 -->
    <el-dialog v-model="registrationVisible" title="报名列表" width="800px">
      <el-table :data="registrationList" style="width: 100%">
        <el-table-column prop="userName" label="姓名" width="100" />
        <el-table-column prop="userPhone" label="手机号" width="130" />
        <el-table-column prop="department" label="院系" width="150" />
        <el-table-column prop="registrationTime" label="报名时间" width="160" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post, put, del } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const activityList = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const registrationVisible = ref(false)
const editId = ref<number | null>(null)
const registrationList = ref<any[]>([])

const searchParams = reactive({
  status: null as number | null,
  activityType: null as number | null
})

const form = reactive({
  activityName: '',
  activityType: 1,
  timeRange: [] as string[],
  location: '',
  maxParticipants: 100,
  points: 10,
  description: '',
  coverImage: ''
})

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

const loadActivities = async () => {
  try {
    const res = await get('/admin/activity/list', {
      page: page.value,
      size: size.value,
      ...searchParams
    })
    activityList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    activityList.value = [
      { id: 1, activityName: '反诈知识竞赛', activityType: 1, startTime: '2026-01-20 09:00:00', endTime: '2026-01-20 12:00:00', registrationCount: 156, maxParticipants: 200, status: 0 },
      { id: 2, activityName: '反诈宣传讲座', activityType: 2, startTime: '2026-01-18 14:00:00', endTime: '2026-01-18 16:00:00', registrationCount: 80, maxParticipants: 100, status: 1 },
      { id: 3, activityName: '防诈骗主题班会', activityType: 2, startTime: '2026-01-15 10:00:00', endTime: '2026-01-15 11:30:00', registrationCount: 50, maxParticipants: 50, status: 2 }
    ]
    total.value = 3
  }
}

const openDialog = (row?: any) => {
  editId.value = row?.id || null
  if (row) {
    Object.assign(form, {
      activityName: row.activityName,
      activityType: row.activityType,
      timeRange: [row.startTime, row.endTime],
      location: row.location || '',
      maxParticipants: row.maxParticipants,
      points: row.points || 10,
      description: row.description || '',
      coverImage: row.coverImage || ''
    })
  } else {
    Object.assign(form, {
      activityName: '',
      activityType: 1,
      timeRange: [],
      location: '',
      maxParticipants: 100,
      points: 10,
      description: '',
      coverImage: ''
    })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.activityName || form.timeRange.length !== 2) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    const data = {
      ...form,
      startTime: form.timeRange[0],
      endTime: form.timeRange[1]
    }
    
    if (editId.value) {
      await put(`/admin/activity/${editId.value}`, data)
      ElMessage.success('更新成功')
    } else {
      await post('/admin/activity', data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadActivities()
  } catch (e) {}
}

const viewRegistrations = (row: any) => {
  registrationList.value = [
    { userName: '张三', userPhone: '13800138001', department: '计算机学院', registrationTime: '2026-01-16 10:30:00', status: 0 },
    { userName: '李四', userPhone: '13800138002', department: '经济学院', registrationTime: '2026-01-16 11:20:00', status: 1 },
    { userName: '王五', userPhone: '13800138003', department: '法学院', registrationTime: '2026-01-16 14:15:00', status: 0 }
  ]
  registrationVisible.value = true
}

const deleteActivity = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除该活动吗？', '提示', { type: 'warning' })
    await del(`/admin/activity/${row.id}`)
    ElMessage.success('删除成功')
    loadActivities()
  } catch (e) {}
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.admin-activities {
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

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
