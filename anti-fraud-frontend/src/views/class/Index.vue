<template>
  <div class="class-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <el-icon><School /></el-icon>
          班级管理
        </h1>
        <p>管理班级信息，查看班级学习情况</p>
        <div class="header-stats">
          <div class="stat-item">
            <el-icon><UserFilled /></el-icon>
            <span>{{ totalStudents }} 名学生</span>
          </div>
          <div class="stat-item">
            <el-icon><ChatLineRound /></el-icon>
            <span>{{ totalClasses }} 个班级</span>
          </div>
          <div class="stat-item">
            <el-icon><TrendCharts /></el-icon>
            <span>{{ avgProgress }}% 平均进度</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 操作栏 -->
    <el-card class="action-card" shadow="hover">
      <div class="action-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索班级名称或班主任..."
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

        <el-button 
          v-if="userStore.isAdmin || userStore.isTeacher" 
          type="primary" 
          size="large" 
          @click="showCreateDialog = true"
        >
          <el-icon><Plus /></el-icon>
          新建班级
        </el-button>
      </div>
    </el-card>

    <!-- 班级列表 -->
    <el-card class="class-list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>班级列表</h3>
          <span class="count-tag">{{ totalClasses }} 个班级</span>
        </div>
      </template>

      <div class="class-grid">
        <el-card
          v-for="cls in classList"
          :key="cls.id"
          class="class-card"
          shadow="hover"
        >
          <div class="class-header">
            <div class="class-info">
              <h4>{{ cls.className }}</h4>
              <p class="class-teacher">班主任：{{ cls.teacherName }}</p>
              <p class="class-code">班级码：{{ cls.classCode }}</p>
            </div>
            <div class="class-actions">
              <el-button type="primary" size="small" @click="handleClassDetail(cls.id)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button 
                v-if="userStore.isAdmin || userStore.isTeacher" 
                type="success" 
                size="small" 
                @click="handleClassEdit(cls)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button 
                v-if="userStore.isAdmin" 
                type="danger" 
                size="small" 
                @click="handleClassDelete(cls.id)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
          
          <div class="class-stats">
            <div class="stat-item">
              <span class="stat-label">学生人数</span>
              <span class="stat-value">{{ cls.studentCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">学习进度</span>
              <el-progress 
                :percentage="cls.learningProgress" 
                :stroke-width="6"
                :format="() => `${cls.learningProgress}%`"
              />
            </div>
            <div class="stat-item">
              <span class="stat-label">平均分</span>
              <span class="stat-value">{{ cls.averageScore }} 分</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-if="classList.length === 0 && !loading" 
        description="暂无班级信息"
        :image-size="200"
      >
        <el-button type="primary" @click="showCreateDialog = true">创建班级</el-button>
      </el-empty>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[12, 24, 36, 48]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 创建班级对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建班级"
      width="500px"
    >
      <el-form :model="classForm" label-width="100px">
        <el-form-item label="班级名称">
          <el-input v-model="classForm.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="班主任">
          <el-input v-model="classForm.teacherName" placeholder="请输入班主任姓名" />
        </el-form-item>
        <el-form-item label="班级描述">
          <el-input
            v-model="classForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入班级描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="handleCreateClass">创建</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑班级对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑班级"
      width="500px"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="班级名称">
          <el-input v-model="editForm.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="班主任">
          <el-input v-model="editForm.teacherName" placeholder="请输入班主任姓名" />
        </el-form-item>
        <el-form-item label="班级描述">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入班级描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUpdateClass">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { get, post, del } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  School, UserFilled, ChatLineRound, TrendCharts, 
  Search, Plus, View, Edit, Delete 
} from '@element-plus/icons-vue'

const userStore = useUserStore()

const router = useRouter()

// 搜索和筛选
const searchKeyword = ref('')
const page = ref(1)
const size = ref(12)
const total = ref(0)
const loading = ref(false)

// 统计数据
const totalStudents = ref(1250)
const totalClasses = ref(48)
const avgProgress = ref(68)

// 班级列表
const classList = ref<any[]>([])

// 对话框状态
const showCreateDialog = ref(false)
const showEditDialog = ref(false)

// 表单数据
const classForm = ref({
  className: '',
  teacherName: '',
  description: ''
})

const editForm = ref({
  id: '',
  className: '',
  teacherName: '',
  description: ''
})

// 加载班级列表
const loadClasses = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      keyword: searchKeyword.value
    }
    const res = await get('/class/list', params)
    classList.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
  } catch (e) {
    // 模拟数据
    classList.value = Array.from({ length: 12 }, (_, i) => ({
      id: i + 1,
      className: `反诈安全 ${i + 1} 班`,
      teacherName: ['张老师', '李老师', '王老师', '赵老师'][i % 4],
      classCode: `CLASS${String(i + 1).padStart(4, '0')}`,
      studentCount: Math.floor(Math.random() * 30) + 20,
      learningProgress: Math.floor(Math.random() * 100),
      averageScore: Math.floor(Math.random() * 40) + 60,
      description: '反诈安全知识学习班级'
    }))
    total.value = 48
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  loadClasses()
}

// 分页
const handlePageChange = () => {
  loadClasses()
}

const handleSizeChange = () => {
  page.value = 1
  loadClasses()
}

// 查看班级详情
const handleClassDetail = (classId: string) => {
  router.push(`/class/${classId}`)
}

// 编辑班级
const handleClassEdit = (cls: any) => {
  editForm.value = {
    id: cls.id,
    className: cls.className,
    teacherName: cls.teacherName,
    description: cls.description
  }
  showEditDialog.value = true
}

// 删除班级
const handleClassDelete = async (classId: string) => {
  try {
    await ElMessageBox.confirm('确定要删除这个班级吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await del(`/class/${classId}`)
    ElMessage.success('删除成功')
    loadClasses()
  } catch (e) {
    // 取消删除
  }
}

// 创建班级
const handleCreateClass = async () => {
  if (!classForm.value.className) {
    ElMessage.warning('请输入班级名称')
    return
  }
  try {
    await post('/class/create', classForm.value)
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    classForm.value = {
      className: '',
      teacherName: '',
      description: ''
    }
    loadClasses()
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

// 更新班级
const handleUpdateClass = async () => {
  if (!editForm.value.className) {
    ElMessage.warning('请输入班级名称')
    return
  }
  try {
    await post('/class/update', editForm.value)
    ElMessage.success('更新成功')
    showEditDialog.value = false
    loadClasses()
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

onMounted(() => {
  loadClasses()
})
</script>

<style scoped>
.class-page {
  padding: 0 0 40px;
  min-height: calc(100vh - 160px);
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #3b82f6 0%, #1e40af 100%);
  padding: 60px 24px;
  margin-bottom: 40px;
  border-radius: 16px;
  color: white;
  position: relative;
  overflow: hidden;
  text-align: center;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.header-content {
  position: relative;
  z-index: 1;
}

.header-content h1 {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 40px;
  font-weight: 700;
  margin-bottom: 16px;
  justify-content: center;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.header-content p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 24px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.header-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-top: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-4px);
}

.stat-item span {
  font-size: 16px;
  font-weight: 500;
}

/* 操作栏 */
.action-card {
  border-radius: 12px;
  margin-bottom: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.action-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.action-bar {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-input {
  flex: 1;
  max-width: 500px;
  border-radius: 8px;
  overflow: hidden;
}

.search-input .el-input__wrapper {
  border-radius: 8px;
}

/* 班级列表 */
.class-list-card {
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0;
  margin: -20px -20px 24px;
  padding: 20px;
  background: var(--el-color-primary-light-9);
  border-radius: 12px 12px 0 0;
}

.card-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

.count-tag {
  background: var(--el-color-primary);
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  box-shadow: 0 1px 4px rgba(66, 184, 131, 0.2);
}

.class-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
}

.class-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color);
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.class-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: var(--el-color-primary-light-5);
}

.class-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color);
}

.class-info h4 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--el-text-color-primary);
}

.class-teacher,
.class-code {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin: 4px 0;
}

.class-actions {
  display: flex;
  gap: 8px;
}

.class-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.class-stats .stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: none;
  padding: 0;
  border: none;
  backdrop-filter: none;
}

.class-stats .stat-item:hover {
  transform: none;
  background: none;
}

.class-stats .stat-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.class-stats .stat-value {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-color-primary);
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 2px solid var(--el-color-primary-light-9);
}

/* 响应式 */
@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    max-width: none;
  }
  
  .class-grid {
    grid-template-columns: 1fr;
  }
  
  .header-stats {
    flex-direction: column;
    gap: 16px;
  }
  
  .stat-item {
    width: 200px;
  }
}
</style>