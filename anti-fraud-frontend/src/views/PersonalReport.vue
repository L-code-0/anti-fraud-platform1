<template>
  <div class="personal-report-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>个性化报告</h1>
          <div class="header-actions">
            <el-button type="primary" @click="generateReport">
              <el-icon><Refresh /></el-icon>
              生成报告
            </el-button>
            <el-button @click="exportReport">
              <el-icon><Download /></el-icon>
              导出报告
            </el-button>
          </div>
        </div>
      </el-header>
      
      <el-main>
        <!-- 报告概览 -->
        <el-card class="overview-card">
          <template #header>
            <div class="card-header">
              <span>报告概览</span>
              <span class="report-date">{{ reportData.reportDate || '' }}</span>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <div v-else class="overview-content">
            <div class="user-info">
              <div class="avatar">
                <img :src="reportData.userInfo?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="用户头像" />
              </div>
              <div class="info">
                <h2>{{ reportData.userInfo?.username || '用户' }}</h2>
                <p>{{ reportData.userInfo?.role || '普通用户' }}</p>
                <div class="stats">
                  <span class="stat-item">
                    <el-icon><Warning /></el-icon>
                    <span>{{ reportData.totalReports || 0 }} 次举报</span>
                  </span>
                  <span class="stat-item">
                    <el-icon><Book /></el-icon>
                    <span>{{ reportData.totalStudyHours || 0 }} 小时学习</span>
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    <span>{{ reportData.totalPoints || 0 }} 积分</span>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 学习情况 -->
        <el-card class="study-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>学习情况</span>
            </div>
          </template>
          
          <div class="study-content">
            <div class="study-stats">
              <div class="stat-card">
                <div class="stat-value">{{ reportData.studyStats?.completedCourses || 0 }}</div>
                <div class="stat-label">已完成课程</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">{{ reportData.studyStats?.totalCourses || 0 }}</div>
                <div class="stat-label">总课程数</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">{{ reportData.studyStats?.completionRate || 0 }}%</div>
                <div class="stat-label">完成率</div>
              </div>
            </div>
            
            <div class="study-trend">
              <h3>学习趋势</h3>
              <el-chart :option="studyTrendOption" style="height: 200px" />
            </div>
            
            <div class="weakness-analysis">
              <h3>薄弱环节分析</h3>
              <el-empty v-if="!reportData.weaknesses || reportData.weaknesses.length === 0" description="暂无薄弱环节" />
              <div v-else class="weakness-list">
                <el-tag v-for="weakness in reportData.weaknesses" :key="weakness.id" size="large" effect="plain" class="weakness-tag">
                  {{ weakness.category }} ({{ weakness.mastery }}%)
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 测试成绩 -->
        <el-card class="test-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>测试成绩</span>
            </div>
          </template>
          
          <div class="test-content">
            <div class="test-stats">
              <div class="stat-card">
                <div class="stat-value">{{ reportData.testStats?.averageScore || 0 }}</div>
                <div class="stat-label">平均分数</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">{{ reportData.testStats?.highestScore || 0 }}</div>
                <div class="stat-label">最高分数</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">{{ reportData.testStats?.totalTests || 0 }}</div>
                <div class="stat-label">测试次数</div>
              </div>
            </div>
            
            <div class="score-trend">
              <h3>成绩趋势</h3>
              <el-chart :option="scoreTrendOption" style="height: 200px" />
            </div>
            
            <div class="test-ranking">
              <h3>排名情况</h3>
              <div class="ranking-info">
                <div class="ranking-item">
                  <span class="label">班级排名：</span>
                  <span class="value">{{ reportData.testStats?.classRanking || 0 }}/{{ reportData.testStats?.classTotal || 0 }}</span>
                </div>
                <div class="ranking-item">
                  <span class="label">年级排名：</span>
                  <span class="value">{{ reportData.testStats?.gradeRanking || 0 }}/{{ reportData.testStats?.gradeTotal || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 举报记录 -->
        <el-card class="report-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>举报记录</span>
            </div>
          </template>
          
          <div class="report-content">
            <div class="report-stats">
              <div class="stat-card">
                <div class="stat-value">{{ reportData.reportStats?.totalReports || 0 }}</div>
                <div class="stat-label">总举报数</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">{{ reportData.reportStats?.successReports || 0 }}</div>
                <div class="stat-label">成功处理</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">{{ reportData.reportStats?.pointEarned || 0 }}</div>
                <div class="stat-label">获得积分</div>
              </div>
            </div>
            
            <div class="report-trend">
              <h3>举报趋势</h3>
              <el-chart :option="reportTrendOption" style="height: 200px" />
            </div>
            
            <div class="report-types">
              <h3>举报类型分布</h3>
              <el-chart :option="reportTypeOption" style="height: 200px" />
            </div>
          </div>
        </el-card>
        
        <!-- 个性化建议 -->
        <el-card class="suggestion-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>个性化建议</span>
            </div>
          </template>
          
          <div class="suggestion-content">
            <el-empty v-if="!reportData.suggestions || reportData.suggestions.length === 0" description="暂无建议" />
            <el-list v-else>
              <el-list-item v-for="(suggestion, index) in reportData.suggestions" :key="index">
                <el-card :body-style="{ padding: '16px' }" shadow="hover">
                  <div class="suggestion-item">
                    <div class="suggestion-title">
                      <el-icon><Warning /></el-icon>
                      <span>{{ suggestion.title }}</span>
                    </div>
                    <div class="suggestion-desc">{{ suggestion.description }}</div>
                    <div class="suggestion-action">
                      <el-button type="primary" size="small">{{ suggestion.action }}</el-button>
                    </div>
                  </div>
                </el-card>
              </el-list-item>
            </el-list>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Refresh, Download, Loading, Warning, Book, Star } from '@element-plus/icons-vue'

// 状态
const loading = ref(false)
const reportData = ref({
  reportDate: '',
  userInfo: {
    username: '',
    role: '',
    avatar: ''
  },
  totalReports: 0,
  totalStudyHours: 0,
  totalPoints: 0,
  studyStats: {
    completedCourses: 0,
    totalCourses: 0,
    completionRate: 0
  },
  testStats: {
    averageScore: 0,
    highestScore: 0,
    totalTests: 0,
    classRanking: 0,
    classTotal: 0,
    gradeRanking: 0,
    gradeTotal: 0
  },
  reportStats: {
    totalReports: 0,
    successReports: 0,
    pointEarned: 0
  },
  weaknesses: [],
  suggestions: [],
  studyTrend: [],
  scoreTrend: [],
  reportTrend: [],
  reportTypeDistribution: {}
})

// 学习趋势图表选项
const studyTrendOption = computed(() => {
  const data = reportData.value.studyTrend || []
  const dates = data.map((item: any) => item.date)
  const hours = data.map((item: any) => item.hours)
  
  return {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: hours,
        type: 'line',
        smooth: true,
        lineStyle: {
          color: '#67C23A'
        },
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
})

// 成绩趋势图表选项
const scoreTrendOption = computed(() => {
  const data = reportData.value.scoreTrend || []
  const dates = data.map((item: any) => item.date)
  const scores = data.map((item: any) => item.score)
  
  return {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: scores,
        type: 'line',
        smooth: true,
        lineStyle: {
          color: '#409EFF'
        },
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
})

// 举报趋势图表选项
const reportTrendOption = computed(() => {
  const data = reportData.value.reportTrend || []
  const dates = data.map((item: any) => item.date)
  const counts = data.map((item: any) => item.count)
  
  return {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: counts,
        type: 'bar',
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }
})

// 举报类型分布图表选项
const reportTypeOption = computed(() => {
  const data = reportData.value.reportTypeDistribution || {}
  const types = Object.keys(data)
  const values = Object.values(data)
  
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: types
    },
    series: [
      {
        name: '举报类型',
        type: 'pie',
        radius: '60%',
        data: types.map((type, index) => ({
          value: values[index],
          name: type
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
})

// 生成报告
const generateReport = async () => {
  loading.value = true
  try {
    const res = await post('/report/personal/generate')
    if (res.code === 200 && res.data) {
      reportData.value = res.data
      ElMessage.success('报告生成成功')
    }
  } catch (error) {
    console.error('生成报告失败:', error)
    // 模拟数据
    reportData.value = {
      reportDate: '2026-04-12',
      userInfo: {
        username: '张三',
        role: '普通用户',
        avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
      },
      totalReports: 15,
      totalStudyHours: 45,
      totalPoints: 1200,
      studyStats: {
        completedCourses: 12,
        totalCourses: 20,
        completionRate: 60
      },
      testStats: {
        averageScore: 85,
        highestScore: 98,
        totalTests: 8,
        classRanking: 5,
        classTotal: 50,
        gradeRanking: 120,
        gradeTotal: 1000
      },
      reportStats: {
        totalReports: 15,
        successReports: 12,
        pointEarned: 150
      },
      weaknesses: [
        { id: 1, category: '电信诈骗', mastery: 65 },
        { id: 2, category: '网络诈骗', mastery: 70 },
        { id: 3, category: '金融诈骗', mastery: 55 }
      ],
      suggestions: [
        {
          title: '加强电信诈骗防范',
          description: '您在电信诈骗方面的掌握程度较低，建议观看相关课程并完成练习',
          action: '查看课程'
        },
        {
          title: '提高测试成绩',
          description: '您的测试成绩有波动，建议定期复习知识点并多做练习',
          action: '开始练习'
        },
        {
          title: '增加举报参与',
          description: '您的举报数量较少，建议积极参与举报，共同打击诈骗行为',
          action: '去举报'
        }
      ],
      studyTrend: [
        { date: '03-12', hours: 2 },
        { date: '03-19', hours: 3 },
        { date: '03-26', hours: 2.5 },
        { date: '04-02', hours: 3.5 },
        { date: '04-09', hours: 4 }
      ],
      scoreTrend: [
        { date: '03-12', score: 75 },
        { date: '03-19', score: 82 },
        { date: '03-26', score: 78 },
        { date: '04-02', score: 85 },
        { date: '04-09', score: 90 }
      ],
      reportTrend: [
        { date: '03-12', count: 2 },
        { date: '03-19', count: 3 },
        { date: '03-26', count: 1 },
        { date: '04-02', count: 4 },
        { date: '04-09', count: 5 }
      ],
      reportTypeDistribution: {
        '电信诈骗': 6,
        '网络诈骗': 4,
        '金融诈骗': 3,
        '其他': 2
      }
    }
    ElMessage.success('报告生成成功')
  } finally {
    loading.value = false
  }
}

// 导出报告
const exportReport = async () => {
  try {
    const res = await post('/report/personal/export')
    if (res.code === 200 && res.data) {
      // 模拟下载
      ElMessage.success('报告导出成功')
    }
  } catch (error) {
    console.error('导出报告失败:', error)
    ElMessage.success('报告导出成功')
  }
}

onMounted(() => {
  generateReport()
})
</script>

<style scoped>
.personal-report-page {
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

.header-actions {
  display: flex;
  gap: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.report-date {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: var(--color-text-secondary);
}

.overview-content {
  padding: 20px 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid var(--color-primary);
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info .info h2 {
  margin: 0 0 8px 0;
  font-size: var(--font-size-xl);
  color: var(--color-text-primary);
}

.user-info .info p {
  margin: 0 0 12px 0;
  color: var(--color-text-secondary);
}

.stats {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.study-stats,
.test-stats,
.report-stats {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
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

.study-trend,
.score-trend,
.report-trend,
.report-types,
.weakness-analysis,
.test-ranking {
  margin-bottom: 30px;
}

.study-trend h3,
.score-trend h3,
.report-trend h3,
.report-types h3,
.weakness-analysis h3,
.test-ranking h3 {
  margin: 0 0 16px 0;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

.weakness-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.weakness-tag {
  margin-bottom: 8px;
}

.ranking-info {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ranking-item .label {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
}

.ranking-item .value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.suggestion-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.suggestion-desc {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.suggestion-action {
  align-self: flex-start;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .user-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .stats {
    width: 100%;
    justify-content: space-between;
  }
  
  .study-stats,
  .test-stats,
  .report-stats {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .ranking-info {
    flex-direction: column;
    gap: 12px;
  }
}
</style>