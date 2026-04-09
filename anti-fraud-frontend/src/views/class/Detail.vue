<template>
  <div class="class-detail-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1>
            <el-icon><School /></el-icon>
            {{ className }}
          </h1>
          <div class="class-info">
            <span class="info-item">
              <el-icon><UserFilled /></el-icon>
              班主任：{{ teacherName }}
            </span>
            <span class="info-item">
              <el-icon><ChatLineRound /></el-icon>
              班级码：{{ classCode }}
            </span>
            <span class="info-item">
              <el-icon><User /></el-icon>
              {{ studentCount }} 名学生
            </span>
          </div>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="handleInviteStudent">
            <el-icon><UserFilled /></el-icon>
            邀请学生
          </el-button>
          <el-button type="success" @click="handleExportData">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- 班级统计 -->
    <el-row :gutter="32">
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-header">
              <el-icon class="stat-icon"><TrendCharts /></el-icon>
              <h3>学习进度</h3>
            </div>
            <div class="stat-value">{{ learningProgress }}%</div>
            <el-progress 
              :percentage="learningProgress" 
              :stroke-width="12"
              :format="() => ''"
              :color="progressColor"
            />
            <div class="stat-desc">班级整体学习进度</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-header">
              <el-icon class="stat-icon"><Document /></el-icon>
              <h3>知识掌握</h3>
            </div>
            <div class="stat-value">{{ masteryRate }}%</div>
            <el-progress 
              :percentage="masteryRate" 
              :stroke-width="12"
              :format="() => ''"
              :color="masteryColor"
            />
            <div class="stat-desc">知识点掌握程度</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-header">
              <el-icon class="stat-icon"><Star /></el-icon>
              <h3>考试成绩</h3>
            </div>
            <div class="stat-value">{{ averageScore }} 分</div>
            <el-progress 
              :percentage="scorePercentage" 
              :stroke-width="12"
              :format="() => ''"
              :color="scoreColor"
            />
            <div class="stat-desc">班级平均分</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学习数据图表 -->
    <el-card class="chart-card" shadow="hover" style="margin-top: 32px;">
      <template #header>
        <div class="card-header">
          <h3>学习数据统计</h3>
        </div>
      </template>
      <div class="chart-container">
        <div class="chart-item">
          <h4>学习进度趋势</h4>
          <LineChart :data="progressData" />
        </div>
        <div class="chart-item">
          <h4>知识类别掌握情况</h4>
          <PieChart :data="categoryData" />
        </div>
      </div>
    </el-card>

    <!-- 学生列表 -->
    <el-card class="student-card" shadow="hover" style="margin-top: 32px;">
      <template #header>
        <div class="card-header">
          <h3>学生列表</h3>
          <span class="count-tag">{{ studentCount }} 名学生</span>
        </div>
      </template>

      <div class="student-filter">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索学生姓名或学号..."
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
      </div>

      <el-table :data="studentList" style="width: 100%">
        <el-table-column prop="studentId" label="学号" width="180" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="learningProgress" label="学习进度" width="180">
          <template #default="scope">
            <div class="progress-cell">
              <span class="progress-text">{{ scope.row.learningProgress }}%</span>
              <el-progress 
                :percentage="scope.row.learningProgress" 
                :stroke-width="6"
                :format="() => ''"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="平均分" width="120" />
        <el-table-column prop="lastLogin" label="最后登录" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleStudentDetail(scope.row.id)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 邀请学生对话框 -->
    <el-dialog
      v-model="showInviteDialog"
      title="邀请学生加入班级"
      width="500px"
    >
      <div class="invite-content">
        <div class="invite-code">
          <h4>班级邀请码</h4>
          <div class="code-box">
            <span>{{ classCode }}</span>
            <el-button type="primary" size="small" @click="copyCode">
              <el-icon><DocumentCopy /></el-icon>
              复制
            </el-button>
          </div>
          <p class="code-desc">学生可以通过邀请码在注册或个人中心加入班级</p>
        </div>
        <div class="invite-link">
          <h4>邀请链接</h4>
          <div class="link-box">
            <span>{{ inviteLink }}</span>
            <el-button type="primary" size="small" @click="copyLink">
              <el-icon><DocumentCopy /></el-icon>
              复制
            </el-button>
          </div>
          <p class="link-desc">直接分享链接给学生，点击即可加入班级</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showInviteDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { 
  School, UserFilled, ChatLineRound, User, Download, 
  Search, View, DocumentCopy, TrendCharts, Document, Star 
} from '@element-plus/icons-vue'
import LineChart from '@/components/charts/LineChart.vue'
import PieChart from '@/components/charts/PieChart.vue'

const route = useRoute()
const router = useRouter()

// 班级信息
const classId = computed(() => route.params.id as string)
const className = ref('反诈安全 1 班')
const teacherName = ref('张老师')
const classCode = ref('CLASS0001')
const studentCount = ref(45)
const learningProgress = ref(68)
const masteryRate = ref(72)
const averageScore = ref(85)

// 搜索和分页
const searchKeyword = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 学生列表
const studentList = ref<any[]>([])

// 对话框状态
const showInviteDialog = ref(false)

// 计算属性
const progressColor = computed(() => {
  if (learningProgress.value >= 80) return '#42b883'
  if (learningProgress.value >= 60) return '#3b82f6'
  return '#f59e0b'
})

const masteryColor = computed(() => {
  if (masteryRate.value >= 80) return '#42b883'
  if (masteryRate.value >= 60) return '#3b82f6'
  return '#f59e0b'
})

const scorePercentage = computed(() => {
  return (averageScore.value / 100) * 100
})

const scoreColor = computed(() => {
  if (averageScore.value >= 85) return '#42b883'
  if (averageScore.value >= 70) return '#3b82f6'
  return '#f59e0b'
})

const inviteLink = computed(() => {
  return `${window.location.origin}/register?classCode=${classCode.value}`
})

// 图表数据
const progressData = ref({
  labels: ['第1周', '第2周', '第3周', '第4周', '第5周', '第6周'],
  datasets: [{
    label: '学习进度',
    data: [15, 28, 42, 55, 62, 68],
    borderColor: '#3b82f6',
    backgroundColor: 'rgba(59, 130, 246, 0.1)'
  }]
})

const categoryData = ref({
  labels: ['电信诈骗', '网络诈骗', '校园贷', '兼职诈骗'],
  datasets: [{
    data: [85, 78, 65, 72],
    backgroundColor: ['#3b82f6', '#42b883', '#f59e0b', '#ef4444']
  }]
})

// 加载班级详情
const loadClassDetail = async () => {
  try {
    const res = await get(`/class/${classId.value}`)
    const data = res.data
    className.value = data.className
    teacherName.value = data.teacherName
    classCode.value = data.classCode
    studentCount.value = data.studentCount
    learningProgress.value = data.learningProgress
    masteryRate.value = data.masteryRate
    averageScore.value = data.averageScore
  } catch (e) {
    // 使用模拟数据
  }
}

// 加载学生列表
const loadStudents = async () => {
  try {
    const params = {
      page: page.value,
      size: size.value,
      keyword: searchKeyword.value,
      classId: classId.value
    }
    const res = await get('/class/students', params)
    studentList.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
  } catch (e) {
    // 模拟数据
    studentList.value = Array.from({ length: 10 }, (_, i) => ({
      id: i + 1,
      studentId: `2024${String(i + 1).padStart(4, '0')}`,
      name: ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十', '郑一', '王二'][i],
      learningProgress: Math.floor(Math.random() * 100),
      score: Math.floor(Math.random() * 40) + 60,
      lastLogin: '2024-01-15 10:30'
    }))
    total.value = 45
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  loadStudents()
}

// 分页
const handlePageChange = () => {
  loadStudents()
}

const handleSizeChange = () => {
  page.value = 1
  loadStudents()
}

// 查看学生详情
const handleStudentDetail = (studentId: string) => {
  router.push(`/profile/${studentId}`)
}

// 邀请学生
const handleInviteStudent = () => {
  showInviteDialog.value = true
}

// 导出数据
const handleExportData = () => {
  ElMessage.success('数据导出功能开发中')
}

// 复制邀请码
const copyCode = () => {
  navigator.clipboard.writeText(classCode.value)
  ElMessage.success('邀请码已复制')
}

// 复制邀请链接
const copyLink = () => {
  navigator.clipboard.writeText(inviteLink.value)
  ElMessage.success('邀请链接已复制')
}

onMounted(() => {
  loadClassDetail()
  loadStudents()
})
</script>

<style scoped>
.class-detail-page {
  padding: 0 0 40px;
  min-height: calc(100vh - 160px);
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #3b82f6 0%, #1e40af 100%);
  padding: 40px 24px;
  margin-bottom: 40px;
  border-radius: 16px;
  color: white;
  position: relative;
  overflow: hidden;
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
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 32px;
}

.header-left h1 {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.class-info {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  opacity: 0.9;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 统计卡片 */
.stat-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 24px;
  text-align: center;
}

.stat-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.stat-icon {
  font-size: 24px;
  color: var(--el-color-primary);
}

.stat-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: var(--el-color-primary);
  line-height: 1;
}

.stat-desc {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-top: 8px;
}

/* 图表卡片 */
.chart-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
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

.chart-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
}

.chart-item h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 16px;
}

/* 学生列表 */
.student-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.student-filter {
  margin-bottom: 24px;
}

.search-input {
  max-width: 400px;
  border-radius: 8px;
  overflow: hidden;
}

.search-input .el-input__wrapper {
  border-radius: 8px;
}

.progress-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-text {
  font-size: 14px;
  font-weight: 500;
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

/* 邀请对话框 */
.invite-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.invite-code h4,
.invite-link h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
}

.code-box,
.link-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: var(--el-color-primary-light-9);
  border-radius: 8px;
  margin-bottom: 8px;
}

.code-box span,
.link-box span {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-color-primary);
  word-break: break-all;
}

.code-desc,
.link-desc {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin: 0;
}

/* 响应式 */
@media (max-width: 1200px) {
  .chart-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-actions {
    justify-content: flex-start;
  }
  
  .class-info {
    flex-direction: column;
    gap: 8px;
  }
  
  .stat-card {
    margin-bottom: 16px;
  }
}
</style>