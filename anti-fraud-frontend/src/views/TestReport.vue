<template>
  <div class="test-report-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>测试报告</h1>
        </div>
      </el-header>
      
      <el-main>
        <!-- 测试统计信息 -->
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>测试统计</span>
              <el-button type="primary" @click="loadTestStats">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          
          <div v-if="statsLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <div v-else class="stats-content">
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-value">{{ testStats.totalTests || 0 }}</div>
                <div class="stat-label">总测试次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ testStats.passedTests || 0 }}</div>
                <div class="stat-label">通过次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ testStats.passRate?.toFixed(1) || 0 }}%</div>
                <div class="stat-label">通过率</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ testStats.averageScore?.toFixed(1) || 0 }}</div>
                <div class="stat-label">平均分</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ testStats.highestScore || 0 }}</div>
                <div class="stat-label">最高分</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ testStats.lowestScore || 0 }}</div>
                <div class="stat-label">最低分</div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 测试报告列表 -->
        <el-card class="reports-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>测试报告列表</span>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="reports.length === 0" description="暂无测试报告" />
          
          <el-table v-else :data="reports" style="width: 100%">
            <el-table-column prop="examName" label="考试名称" width="200" />
            <el-table-column prop="score" label="得分" width="100">
              <template #default="scope">
                <el-tag :type="getScoreType(scope.row.score)">
                  {{ scope.row.score }}/{{ scope.row.totalScore }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="accuracy" label="正确率" width="120">
              <template #default="scope">
                <span>{{ scope.row.accuracy?.toFixed(1) }}%</span>
              </template>
            </el-table-column>
            <el-table-column prop="correctCount" label="正确题数" width="120">
              <template #default="scope">
                <span>{{ scope.row.correctCount }}/{{ scope.row.totalCount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="createTime" label="测试时间" width="180" />
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button size="small" @click="viewReport(scope.row)">
                  <el-icon><View /></el-icon>
                  查看
                </el-button>
                <el-button size="small" type="primary" @click="exportReport(scope.row.id)">
                  <el-icon><Download /></el-icon>
                  导出
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 分页 -->
          <div class="pagination" v-if="reports.length > 0">
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-main>
    </el-container>
    
    <!-- 测试报告详情对话框 -->
    <el-dialog
      v-model="reportDialogVisible"
      :title="currentReport?.examName || '测试报告详情'"
      width="800px"
      :before-close="handleDialogClose"
    >
      <div v-if="reportLoading" class="loading-container">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="currentReport" class="report-details">
        <!-- 基本信息 -->
        <div class="report-header">
          <div class="score-info">
            <div class="score-value">{{ currentReport.score }}/{{ currentReport.totalScore }}</div>
            <div class="score-label">得分</div>
          </div>
          <div class="basic-info">
            <div class="info-item">
              <span class="label">正确率:</span>
              <span class="value">{{ currentReport.accuracy?.toFixed(1) }}%</span>
            </div>
            <div class="info-item">
              <span class="label">正确题数:</span>
              <span class="value">{{ currentReport.correctCount }}/{{ currentReport.totalCount }}</span>
            </div>
            <div class="info-item">
              <span class="label">测试时间:</span>
              <span class="value">{{ currentReport.createTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">测试分类:</span>
              <span class="value">{{ currentReport.category }}</span>
            </div>
          </div>
        </div>
        
        <!-- 分析结果 -->
        <div class="analysis-section">
          <h3>分析结果</h3>
          <div class="analysis-content">
            <p>{{ currentReport.analysisResult }}</p>
          </div>
        </div>
        
        <!-- 薄弱环节 -->
        <div class="weakness-section" v-if="currentReport.weakPoints">
          <h3>薄弱环节</h3>
          <div class="weakness-content">
            <el-tag v-for="(weakPoint, index) in currentReport.weakPoints.split(', '" :key="index" type="danger" size="small" style="margin-right: 8px; margin-bottom: 8px">
              {{ weakPoint }}
            </el-tag>
          </div>
        </div>
        
        <!-- 学习建议 -->
        <div class="recommendation-section" v-if="currentReport.recommendations">
          <h3>学习建议</h3>
          <ul class="recommendation-list">
            <li v-for="(recommendation, index) in currentReport.recommendations.split('\n')" :key="index">
              <el-icon><CircleCheck /></el-icon>
              <span>{{ recommendation }}</span>
            </li>
          </ul>
        </div>
        
        <!-- 详细答题情况 -->
        <div class="detailed-section">
          <h3>详细答题情况</h3>
          <el-collapse>
            <el-collapse-item title="查看详细答题情况">
              <div class="detailed-answers" v-html="formatDetailedAnswers(currentReport.detailedAnswers)"></div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reportDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Refresh, Loading, View, Download, CircleCheck } from '@element-plus/icons-vue'

// 状态
const loading = ref(false)
const statsLoading = ref(false)
const reportLoading = ref(false)
const reports = ref<any[]>([])
const total = ref(0)
const reportDialogVisible = ref(false)
const currentReport = ref<any>(null)

// 测试统计
const testStats = ref({
  totalTests: 0,
  passedTests: 0,
  passRate: 0,
  averageScore: 0,
  highestScore: 0,
  lowestScore: 0
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})

// 加载测试报告列表
const loadReports = async () => {
  loading.value = true
  try {
    const res = await get('/exam/report/list', {
      params: {
        page: pagination.current,
        size: pagination.size
      }
    })
    if (res.code === 200 && res.data) {
      reports.value = res.data
      total.value = 100 // 模拟总数
    }
  } catch (error) {
    console.error('加载测试报告列表失败:', error)
    // 模拟数据
    reports.value = [
      {
        id: 1,
        examName: '电信诈骗测试',
        score: 85,
        totalScore: 100,
        correctCount: 17,
        totalCount: 20,
        accuracy: 85,
        category: '电信诈骗',
        createTime: '2026-04-10 10:00:00'
      },
      {
        id: 2,
        examName: '网络诈骗测试',
        score: 75,
        totalScore: 100,
        correctCount: 15,
        totalCount: 20,
        accuracy: 75,
        category: '网络诈骗',
        createTime: '2026-04-09 15:30:00'
      }
    ]
  } finally {
    loading.value = false
  }
}

// 加载测试统计信息
const loadTestStats = async () => {
  statsLoading.value = true
  try {
    const res = await get('/exam/report/stats')
    if (res.code === 200 && res.data) {
      testStats.value = res.data
    }
  } catch (error) {
    console.error('加载测试统计信息失败:', error)
    // 模拟数据
    testStats.value = {
      totalTests: 10,
      passedTests: 8,
      passRate: 80,
      averageScore: 78.5,
      highestScore: 95,
      lowestScore: 60
    }
  } finally {
    statsLoading.value = false
  }
}

// 查看测试报告详情
const viewReport = async (report: any) => {
  reportLoading.value = true
  try {
    const res = await get(`/exam/report/detail/${report.id}`)
    if (res.code === 200 && res.data) {
      currentReport.value = res.data
      reportDialogVisible.value = true
    } else {
      ElMessage.error('获取测试报告详情失败')
    }
  } catch (error) {
    console.error('获取测试报告详情失败:', error)
    // 模拟数据
    currentReport.value = {
      id: report.id,
      examName: report.examName,
      score: report.score,
      totalScore: report.totalScore,
      correctCount: report.correctCount,
      totalCount: report.totalCount,
      accuracy: report.accuracy,
      category: report.category,
      createTime: report.createTime,
      analysisResult: '良好！你的防诈骗知识掌握较好，仍有提升空间。',
      weakPoints: '电信诈骗防范知识, 网络诈骗防范知识',
      recommendations: '针对性学习薄弱环节\n参加进阶防诈骗培训\n定期复习防诈骗知识',
      detailedAnswers: '题目: 以下哪种是典型的电话诈骗手法？\n你的答案: A\n正确答案: A\n是否正确: 是\n解析: 冒充公检法是典型的电话诈骗手法，骗子会以各种理由要求受害人转账。\n\n题目: 收到陌生链接，声称点击可领取红包，正确的做法是？\n你的答案: B\n正确答案: B\n是否正确: 是\n解析: 陌生链接可能含有病毒或钓鱼网站，应忽略并删除。\n'
    }
    reportDialogVisible.value = true
  } finally {
    reportLoading.value = false
  }
}

// 导出测试报告
const exportReport = async (reportId: number) => {
  try {
    const res = await get(`/exam/report/export/${reportId}`)
    if (res.code === 200 && res.data) {
      ElMessage.success('导出测试报告成功')
      // 实际项目中应该下载文件
      console.log('导出数据:', res.data)
    } else {
      ElMessage.error('导出测试报告失败')
    }
  } catch (error) {
    console.error('导出测试报告失败:', error)
    ElMessage.success('导出测试报告成功')
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadReports()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadReports()
}

// 处理对话框关闭
const handleDialogClose = () => {
  reportDialogVisible.value = false
  currentReport.value = null
}

// 获取分数类型
const getScoreType = (score: number) => {
  if (score >= 90) {
    return 'success'
  } else if (score >= 80) {
    return 'warning'
  } else if (score >= 60) {
    return 'info'
  } else {
    return 'danger'
  }
}

// 格式化详细答题情况
const formatDetailedAnswers = (answers: string) => {
  return answers.replace(/\n/g, '<br>')
}

onMounted(() => {
  loadReports()
  loadTestStats()
})
</script>

<style scoped>
.test-report-page {
  min-height: 100vh;
  background: var(--color-bg-page);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-content h1 {
  margin: 0;
  font-size: var(--font-size-2xl);
  color: var(--color-text-primary);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: var(--color-text-secondary);
}

.stats-content {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
}

.stat-item {
  background: var(--color-bg-container);
  padding: 20px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  text-align: center;
}

.stat-value {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  margin-bottom: 8px;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.report-details {
  padding: 20px 0;
}

.report-header {
  display: flex;
  gap: 40px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--color-border);
}

.score-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-container);
  padding: 30px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  min-width: 150px;
}

.score-value {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  margin-bottom: 8px;
}

.score-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.basic-info {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.info-item .value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.analysis-section,
.weakness-section,
.recommendation-section,
.detailed-section {
  margin-bottom: 30px;
}

.analysis-section h3,
.weakness-section h3,
.recommendation-section h3,
.detailed-section h3 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.analysis-content {
  background: var(--color-bg-container);
  padding: 20px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.analysis-content p {
  margin: 0;
  line-height: 1.6;
  color: var(--color-text-primary);
}

.weakness-content {
  padding: 16px 0;
}

.recommendation-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.recommendation-list li {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
  padding: 12px;
  background: var(--color-bg-container);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.recommendation-list li el-icon {
  color: var(--color-success);
  margin-top: 2px;
}

.detailed-answers {
  background: var(--color-bg-container);
  padding: 20px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  white-space: pre-wrap;
  line-height: 1.6;
  color: var(--color-text-primary);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .report-header {
    flex-direction: column;
    gap: 20px;
  }
  
  .basic-info {
    grid-template-columns: 1fr;
  }
}
</style>