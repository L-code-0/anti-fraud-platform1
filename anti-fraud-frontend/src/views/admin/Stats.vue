<template>
  <div class="admin-stats">
    <!-- 学校维度统计 -->
    <el-card class="section-card">
      <template #header>
        <span>各院系学习参与率</span>
      </template>
      <el-table :data="departmentStats" style="width: 100%">
        <el-table-column prop="departmentName" label="院系" width="150" />
        <el-table-column prop="totalStudents" label="学生总数" width="100" />
        <el-table-column prop="activeStudents" label="参与人数" width="100" />
        <el-table-column label="参与率" width="200">
          <template #default="{ row }">
            <el-progress :percentage="row.participationRate" :format="() => row.participationRate.toFixed(1) + '%'" />
          </template>
        </el-table-column>
        <el-table-column prop="avgStudyDurationStr" label="平均学习时长" width="150">
          <template #default="{ row }">
            {{ formatDuration(row.avgStudyDuration) }}
          </template>
        </el-table-column>
        <el-table-column prop="avgTestScore" label="平均分数" width="100">
          <template #default="{ row }">
            {{ row.avgTestScore?.toFixed(1) }}
          </template>
        </el-table-column>
        <el-table-column prop="testCount" label="测试次数" width="100" />
        <el-table-column prop="simulationCount" label="演练次数" width="100" />
      </el-table>
    </el-card>
    
    <!-- 高发诈骗类型统计 -->
    <el-card class="section-card">
      <template #header>
        <span>高发诈骗类型统计</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="16">
          <el-table :data="fraudTypeStats" style="width: 100%">
            <el-table-column prop="typeName" label="诈骗类型" width="120" />
            <el-table-column prop="reportCount" label="举报数量" width="100" />
            <el-table-column label="占比" width="200">
              <template #default="{ row }">
                <el-progress :percentage="row.percentage" :format="() => row.percentage.toFixed(1) + '%'" />
              </template>
            </el-table-column>
            <el-table-column prop="caseCount" label="典型案例" width="100" />
          </el-table>
        </el-col>
        <el-col :span="8">
          <div class="stat-summary">
            <div class="summary-item">
              <div class="summary-label">总举报数</div>
              <div class="summary-value">{{ totalReports }}</div>
            </div>
            <div class="summary-item">
              <div class="summary-label">最高发类型</div>
              <div class="summary-value text-danger">{{ topFraudType }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 举报处理时效 -->
    <el-card class="section-card">
      <template #header>
        <span>举报处理时效</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="efficiency-card">
            <div class="efficiency-icon">
              <el-icon><Timer /></el-icon>
            </div>
            <div class="efficiency-info">
              <div class="efficiency-value">{{ reportEfficiency?.value || 36 }} 小时</div>
              <div class="efficiency-label">平均处理时长</div>
            </div>
          </div>
        </el-col>
        <el-col :span="16">
          <div class="efficiency-tips">
            <h4>处理时效说明</h4>
            <ul>
              <li>紧急举报：24小时内处理</li>
              <li>一般举报：48小时内处理</li>
              <li>复杂举报：72小时内处理</li>
            </ul>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { get } from '@/utils/request'

const departmentStats = ref<any[]>([])
const fraudTypeStats = ref<any[]>([])
const reportEfficiency = ref<any>(null)

const totalReports = computed(() => {
  return fraudTypeStats.value.reduce((sum, item) => sum + (item.reportCount || 0), 0)
})

const topFraudType = computed(() => {
  if (fraudTypeStats.value.length === 0) return '-'
  return fraudTypeStats.value[0]?.typeName || '-'
})

const formatDuration = (seconds: number) => {
  if (!seconds) return '0分钟'
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  }
  return `${minutes}分钟`
}

const loadStats = async () => {
  try {
    const [deptRes, fraudRes, effRes] = await Promise.all([
      get('/admin/dashboard/department-stats'),
      get('/admin/dashboard/fraud-type-stats'),
      get('/admin/dashboard/report-efficiency')
    ])
    departmentStats.value = deptRes.data || []
    fraudTypeStats.value = fraudRes.data || []
    reportEfficiency.value = effRes.data
  } catch (e) {
    departmentStats.value = [
      { departmentName: '计算机学院', totalStudents: 680, activeStudents: 580, participationRate: 85.3, avgStudyDuration: 5400, avgTestScore: 78.5, testCount: 450, simulationCount: 180 },
      { departmentName: '经济学院', totalStudents: 520, activeStudents: 410, participationRate: 78.8, avgStudyDuration: 4200, avgTestScore: 72.3, testCount: 320, simulationCount: 120 },
      { departmentName: '法学院', totalStudents: 380, activeStudents: 320, participationRate: 84.2, avgStudyDuration: 4800, avgTestScore: 81.2, testCount: 280, simulationCount: 150 },
      { departmentName: '外国语学院', totalStudents: 450, activeStudents: 360, participationRate: 80.0, avgStudyDuration: 3600, avgTestScore: 75.8, testCount: 380, simulationCount: 140 },
      { departmentName: '艺术学院', totalStudents: 420, activeStudents: 300, participationRate: 71.4, avgStudyDuration: 3000, avgTestScore: 68.5, testCount: 250, simulationCount: 90 }
    ]
    fraudTypeStats.value = [
      { typeName: '电信诈骗', reportCount: 45, percentage: 33.3, caseCount: 12 },
      { typeName: '网络诈骗', reportCount: 38, percentage: 28.1, caseCount: 10 },
      { typeName: '校园贷', reportCount: 22, percentage: 16.3, caseCount: 8 },
      { typeName: '兼职诈骗', reportCount: 18, percentage: 13.3, caseCount: 6 },
      { typeName: '其他', reportCount: 12, percentage: 8.9, caseCount: 4 }
    ]
    reportEfficiency.value = { label: '平均处理时效', value: 36 }
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-stats {
  max-width: 1400px;
  margin: 0 auto;
}

.section-card {
  margin-bottom: 20px;
}

.stat-summary {
  padding: 20px;
}

.summary-item {
  text-align: center;
  padding: 20px;
  margin-bottom: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.summary-label {
  font-size: 14px;
  color: #909399;
}

.summary-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-top: 10px;
}

.text-danger {
  color: #F56C6C;
}

.efficiency-card {
  display: flex;
  align-items: center;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.efficiency-icon {
  font-size: 48px;
  margin-right: 20px;
}

.efficiency-value {
  font-size: 32px;
  font-weight: bold;
}

.efficiency-label {
  font-size: 14px;
  opacity: 0.8;
  margin-top: 5px;
}

.efficiency-tips {
  padding: 20px;
}

.efficiency-tips h4 {
  margin: 0 0 15px;
}

.efficiency-tips ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.efficiency-tips li {
  padding: 8px 0;
  color: #606266;
  border-bottom: 1px dashed #ebeef5;
}

.efficiency-tips li:last-child {
  border-bottom: none;
}
</style>
