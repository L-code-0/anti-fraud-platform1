<template>
  <div class="admin-users">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="showDialog = true">
            <el-icon><Plus /></el-icon> 添加用户
          </el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchParams.keyword" placeholder="用户名/姓名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchParams.roleId" placeholder="全部" clearable>
            <el-option :value="1" label="学生" />
            <el-option :value="2" label="教师" />
            <el-option :value="3" label="管理员" />
            <el-option :value="4" label="专家" />
            <el-option :value="5" label="系统管理员" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadUsers">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 用户列表 -->
      <el-table :data="userList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="roleId" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.roleId)">{{ getRoleName(row.roleId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80" />
        <el-table-column prop="level" label="等级" width="80">
          <template #default="{ row }">Lv.{{ row.level }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button text type="primary" @click="editUser(row)">编辑</el-button>
            <el-button 
              text 
              :type="row.status === 1 ? 'danger' : 'success'" 
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
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
          @current-change="loadUsers"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, put } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const userList = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const showDialog = ref(false)

const searchParams = reactive({
  keyword: '',
  roleId: null as number | null
})

const getRoleName = (roleId: number) => {
  const roles: Record<number, string> = {
    1: '学生',
    2: '教师',
    3: '管理员',
    4: '专家',
    5: '系统管理员'
  }
  return roles[roleId] || '未知'
}

const getRoleType = (roleId: number) => {
  const types: Record<number, string> = {
    1: '',
    2: 'success',
    3: 'warning',
    4: 'info',
    5: 'danger'
  }
  return types[roleId] || ''
}

const loadUsers = async () => {
  try {
    const res = await get('/user/list', {
      page: page.value,
      size: size.value,
      ...searchParams
    })
    userList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    userList.value = [
      { id: 1, username: 'student1', realName: '张三', phone: '13800138001', roleId: 1, points: 100, level: 2, status: 1, createTime: '2026-01-10' },
      { id: 2, username: 'teacher1', realName: '李老师', phone: '13800138002', roleId: 2, points: 200, level: 3, status: 1, createTime: '2026-01-09' },
      { id: 3, username: 'expert1', realName: '王专家', phone: '13800138003', roleId: 4, points: 500, level: 4, status: 1, createTime: '2026-01-08' },
      { id: 4, username: 'admin', realName: '系统管理员', phone: '13800138000', roleId: 5, points: 0, level: 1, status: 1, createTime: '2026-01-01' }
    ]
    total.value = 4
  }
}

const editUser = (row: any) => {
  ElMessage.info('编辑功能开发中')
}

const toggleStatus = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '禁用' : '启用'}该用户吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    
    await put(`/user/${row.id}/status`, null, {
      params: { status: row.status === 1 ? 0 : 1 }
    })
    
    ElMessage.success('操作成功')
    loadUsers()
  } catch (e) {}
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users {
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
