<template>
  <div class="test-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <el-icon><DocumentCopy /></el-icon>
          测试考试
        </h1>
        <p>通过测试检验反诈知识掌握程度，提升安全意识</p>
        <div class="header-stats">
          <div class="stat-item">
            <el-icon><Document /></el-icon>
            <span>{{ paperList.length }} 套试卷</span>
          </div>
          <div class="stat-item">
            <el-icon><UserFilled /></el-icon>
            <span>{{ testRecords.length }} 次测试</span>
          </div>
          <div class="stat-item">
            <el-icon><Trophy /></el-icon>
            <span>{{ avgScore }} 分平均成绩</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 试卷列表 -->
    <el-card class="paper-list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>试卷列表</h3>
          <span class="count-tag">{{ paperList.length }} 套试卷</span>
        </div>
      </template>

      <div class="paper-grid">
        <el-card
          v-for="paper in paperList"
          :key="paper.id"
          class="paper-card"
          shadow="hover"
        >
          <div class="paper-header">
            <div class="paper-type">
              <el-tag :type="paper.paperType === 1 ? 'warning' : 'primary'">
                {{ paper.paperType === 1 ? '随机组卷' : '固定试卷' }}
              </el-tag>
            </div>
          </div>
          <h4>{{ paper.paperName }}</h4>
          <div class="paper-info">
            <div class="info-item">
              <el-icon><Timer /></el-icon>
              <span>{{ paper.duration }}分钟</span>
            </div>
            <div class="info-item">
              <el-icon><Document /></el-icon>
              <span>{{ paper.questionCount }}题</span>
            </div>
            <div class="info-item">
              <el-icon><Trophy /></el-icon>
              <span>{{ paper.totalScore }}分</span>
            </div>
            <div class="info-item">
              <el-icon><Check /></el-icon>
              <span>{{ paper.passScore }}分及格</span>
            </div>
          </div>
          <div class="paper-footer">
            <el-button type="primary" size="large" @click="startTest(paper.id)" class="start-button">
              <el-icon><VideoPlay /></el-icon>
              开始测试
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-if="paperList.length === 0" 
        description="暂无试卷信息"
        :image-size="200"
      />
    </el-card>

    <!-- 测试记录 -->
    <el-card class="records-card" shadow="hover" v-if="testRecords.length > 0">
      <template #header>
        <div class="card-header">
          <h3>我的测试记录</h3>
          <span class="count-tag">{{ testRecords.length }} 次测试</span>
        </div>
      </template>

      <el-table :data="testRecords" style="width: 100%" stripe>
        <el-table-column prop="paperName" label="试卷名称" min-width="200" />
        <el-table-column prop="userScore" label="得分" width="120">
          <template #default="{ row }">
            <el-tag :type="row.isPassed ? 'success' : 'danger'" size="large">
              {{ row.userScore }}分
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="用时" width="120">
          <template #default="{ row }">
            <span class="duration">{{ Math.floor(row.duration / 60) }}分{{ row.duration % 60 }}秒</span>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="完成时间" width="200" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewResult(row.recordId)">
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { 
  DocumentCopy, Timer, Document, Trophy, View, 
  UserFilled, Check 
} from '@element-plus/icons-vue'

const router = useRouter()

const paperList = ref<any[]>([])
const testRecords = ref<any[]>([])

// 计算平均成绩
const avgScore = computed(() => {
  if (testRecords.value.length === 0) return 0
  const total = testRecords.value.reduce((sum, record) => sum + (record.userScore || 0), 0)
  return Math.round(total / testRecords.value.length)
})

const loadPapers = async () => {
  try {
    const res = await get('/test/papers')
    paperList.value = res.data || []
  } catch (e) {
    paperList.value = [
      { id: 1, paperName: '反诈知识入门测试', paperType: 2, totalScore: 100, passScore: 60, duration: 30, questionCount: 10 },
      { id: 2, paperName: '反诈知识进阶测试', paperType: 2, totalScore: 100, passScore: 70, duration: 45, questionCount: 20 },
      { id: 3, paperName: '电信诈骗专项测试', paperType: 1, totalScore: 100, passScore: 60, duration: 30, questionCount: 15 }
    ]
  }
}

const loadRecords = async () => {
  try {
    const res = await get('/test/my-records')
    testRecords.value = res.data?.records || []
  } catch (e) {
    // 模拟测试记录
    testRecords.value = [
      { 
        recordId: 1, 
        paperName: '反诈知识入门测试', 
        userScore: 85, 
        duration: 1800, 
        submitTime: '2024-01-15 14:30:00',
        isPassed: true
      },
      { 
        recordId: 2, 
        paperName: '反诈知识进阶测试', 
        userScore: 72, 
        duration: 2400, 
        submitTime: '2024-01-10 10:15:00',
        isPassed: true
      },
      { 
        recordId: 3, 
        paperName: '电信诈骗专项测试', 
        userScore: 58, 
        duration: 1500, 
        submitTime: '2024-01-05 09:45:00',
        isPassed: false
      }
    ]
  }
}

const startTest = async (paperId: number) => {
  try {
    const res = await post(`/test/papers/${paperId}/start`)
    router.push(`/test/${paperId}?recordId=${res.data}`)
  } catch (e) {
    router.push(`/test/${paperId}`)
  }
}

const viewResult = (recordId: number) => {
  ElMessage.info('测试详情功能开发中')
}

onMounted(() => {
  loadPapers()
  loadRecords()
})
</script>

<style scoped>
.test-page {
  padding: 0 0 40px;
  min-height: calc(100vh - 160px);
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
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

/* 试卷列表卡片 */
.paper-list-card {
  border-radius: 12px;
  margin-bottom: 32px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.paper-list-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0;
  margin: -20px -20px 24px;
  padding: 20px;
  background: var(--el-color-success-light-9);
  border-radius: 12px 12px 0 0;
}

.card-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #065f46;
  margin: 0;
}

.count-tag {
  background: var(--el-color-success);
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  box-shadow: 0 1px 4px rgba(16, 185, 129, 0.2);
}

/* 试卷网格 */
.paper-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

.paper-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color);
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.paper-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: var(--el-color-success-light-5);
}

.paper-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--el-border-color);
}

.paper-card h4 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  color: var(--el-text-color-primary);
  flex: 1;
}

.paper-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 24px;
  flex: 1;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  padding: 8px 12px;
  background: var(--el-color-success-light-5);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.info-item:hover {
  background: var(--el-color-success-light-3);
  transform: translateY(-2px);
}

.info-item span {
  font-weight: 500;
}

.paper-footer {
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px solid var(--el-border-color);
}

.start-button {
  width: 100%;
  transition: all 0.3s ease;
}

.start-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

/* 测试记录 */
.records-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.records-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.records-card .card-header {
  background: var(--el-color-primary-light-9);
}

.records-card .card-header h3 {
  color: #1a1a2e;
}

.records-card .count-tag {
  background: var(--el-color-primary);
  box-shadow: 0 1px 4px rgba(66, 153, 225, 0.2);
}

.duration {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

/* 响应式 */
@media (max-width: 768px) {
  .header-stats {
    flex-direction: column;
    gap: 16px;
  }
  
  .stat-item {
    width: 200px;
  }
  
  .paper-grid {
    grid-template-columns: 1fr;
  }
  
  .paper-info {
    grid-template-columns: 1fr;
  }
  
  .header-content h1 {
    font-size: 32px;
  }
  
  .page-header {
    padding: 40px 16px;
  }
}
</style>
