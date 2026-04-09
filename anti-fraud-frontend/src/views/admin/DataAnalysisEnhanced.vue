<template>
  <div class="data-analysis">
    <el-card class="page-header-card">
      <div class="header-content">
        <div class="header-info">
          <h1>数据分析中心</h1>
          <p>全面了解平台运营状况和用户学习情况</p>
        </div>
        <div class="header-actions">
          <el-select v-model="timeRange" style="width: 140px;">
            <el-option label="今日" value="today" />
            <el-option label="本周" value="week" />
            <el-option label="本月" value="month" />
            <el-option label="本年" value="year" />
          </el-select>
          <el-button @click="handleExport">
            <el-icon><Download /></el-icon> 导出数据
          </el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-trend up"><el-icon><Top /></el-icon> 12%</div>
          <div class="stat-main">
            <span class="stat-value">{{ stats.totalUsers.toLocaleString() }}</span>
            <span class="stat-label">总用户数</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-trend up"><el-icon><Top /></el-icon> 8%</div>
          <div class="stat-main">
            <span class="stat-value">{{ stats.activeUsers.toLocaleString() }}</span>
            <span class="stat-label">活跃用户</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-trend up"><el-icon><Top /></el-icon> 15%</div>
          <div class="stat-main">
            <span class="stat-value">{{ stats.learningSessions.toLocaleString() }}</span>
            <span class="stat-label">学习次数</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-trend down"><el-icon><Bottom /></el-icon> 5%</div>
          <div class="stat-main">
            <span class="stat-value">{{ stats.reportCount }}</span>
            <span class="stat-label">举报数量</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card">
          <template #header>
            <span>用户增长趋势</span>
            <el-radio-group v-model="chartType" size="small">
              <el-radio-button value="line">折线图</el-radio-button>
              <el-radio-button value="bar">柱状图</el-radio-button>
            </el-radio-group>
          </template>
          <div class="chart-container" id="userGrowthChart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card class="chart-card">
          <template #header><span>用户角色分布</span></template>
          <div class="pie-chart-container" id="roleDistChart"></div>
          <div class="legend-list">
            <div v-for="item in roleDistribution" :key="item.name" class="legend-item">
              <span class="legend-dot" :style="{ background: item.color }"></span>
              <span class="legend-name">{{ item.name }}</span>
              <span class="legend-value">{{ item.value }}%</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header><span>诈骗类型分布</span></template>
          <div class="bar-chart-container" id="fraudTypeChart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header><span>学习模块使用情况</span></template>
          <div class="progress-list">
            <div v-for="item in moduleUsage" :key="item.name" class="progress-item">
              <div class="progress-header">
                <span class="progress-name">{{ item.name }}</span>
                <span class="progress-value">{{ item.value }}%</span>
              </div>
              <el-progress :percentage="item.value" :stroke-width="10" :show-text="false" :color="item.color" />
              <div class="progress-meta">
                <span>使用次数: {{ item.count }}</span>
                <span>平均时长: {{ item.avgTime }}分钟</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="16">
        <el-card class="table-card">
          <template #header>
            <span>学习排行榜 TOP 10</span>
          </template>
          <el-table :data="leaderboard" stripe>
            <el-table-column type="index" label="排名" width="80" />
            <el-table-column label="用户" min-width="150">
              <template #default="{ row }">
                <div class="user-cell">
                  <el-avatar :size="32" :src="row.avatar"><el-icon><User /></el-icon></el-avatar>
                  <span>{{ row.nickname }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="role" label="角色" width="100">
              <template #default="{ row }">
                <el-tag size="small">{{ row.roleName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="points" label="积分" width="100" sortable />
            <el-table-column prop="knowledgeCount" label="学习知识" width="100" />
            <el-table-column prop="testScore" label="测试均分" width="100" sortable />
            <el-table-column label="等级" width="100">
              <template #default="{ row }">
                <el-tag type="warning" size="small">Lv.{{ row.level }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card class="table-card">
          <template #header><span>预警统计</span></template>
          <div class="warning-stats">
            <div class="warning-item">
              <div class="warning-icon urgent"><el-icon><Warning /></el-icon></div>
              <div class="warning-info">
                <span class="warning-count">{{ warningStats.urgent }}</span>
                <span class="warning-label">紧急预警</span>
              </div>
            </div>
            <div class="warning-item">
              <div class="warning-icon important"><el-icon><Bell /></el-icon></div>
              <div class="warning-info">
                <span class="warning-count">{{ warningStats.important }}</span>
                <span class="warning-label">重要预警</span>
              </div>
            </div>
            <div class="warning-item">
              <div class="warning-icon normal"><el-icon><InfoFilled /></el-icon></div>
              <div class="warning-info">
                <span class="warning-count">{{ warningStats.normal }}</span>
                <span class="warning-label">一般预警</span>
              </div>
            </div>
          </div>
          <el-divider />
          <div class="hot-topics">
            <h4>热门话题</h4>
            <div v-for="(topic, i) in hotTopics" :key="i" class="topic-item">
              <span class="topic-rank">{{ i + 1 }}</span>
              <span class="topic-name">{{ topic.name }}</span>
              <span class="topic-count">{{ topic.count }}次浏览</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="table-card">
          <template #header><span>地区分布统计</span></template>
          <div class="region-stats">
            <el-table :data="regionData" stripe>
              <el-table-column prop="region" label="地区" />
              <el-table-column prop="users" label="用户数" sortable />
              <el-table-column prop="learningHours" label="学习时长(小时)" sortable />
              <el-table-column prop="avgScore" label="平均得分" sortable />
              <el-table-column prop="reportCount" label="举报数" sortable />
              <el-table-column label="增长率">
                <template #default="{ row }">
                  <span :class="{ 'up': row.growth > 0, 'down': row.growth < 0 }">
                    <el-icon v-if="row.growth > 0"><Top /></el-icon>
                    <el-icon v-else><Bottom /></el-icon>
                    {{ Math.abs(row.growth) }}%
                  </span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Top, Bottom, Warning, Bell, InfoFilled, User } from '@element-plus/icons-vue'

const timeRange = ref('month')
const chartType = ref('line')

const stats = reactive({
  totalUsers: 125680,
  activeUsers: 45632,
  learningSessions: 892345,
  reportCount: 1256
})

const roleDistribution = ref([
  { name: '学生', value: 65, color: '#409eff' },
  { name: '教师', value: 20, color: '#67c23a' },
  { name: '专家', value: 8, color: '#e6a23c' },
  { name: '管理员', value: 5, color: '#f56c6c' },
  { name: '其他', value: 2, color: '#909399' }
])

const moduleUsage = ref([
  { name: '知识学习', value: 85, count: 456789, avgTime: 15, color: '#409eff' },
  { name: '在线测试', value: 72, count: 234567, avgTime: 25, color: '#67c23a' },
  { name: '演练模拟', value: 58, count: 123456, avgTime: 20, color: '#e6a23c' },
  { name: '举报预警', value: 35, count: 67890, avgTime: 5, color: '#f56c6c' }
])

const leaderboard = ref([
  { nickname: '小明同学', role: 1, roleName: '学生', points: 5680, knowledgeCount: 45, testScore: 92, level: 12, avatar: '' },
  { nickname: '小红同学', role: 1, roleName: '学生', points: 5340, knowledgeCount: 42, testScore: 89, level: 11, avatar: '' },
  { nickname: '张老师', role: 2, roleName: '教师', points: 4890, knowledgeCount: 38, testScore: 95, level: 10, avatar: '' },
  { nickname: '李同学', role: 1, roleName: '学生', points: 4560, knowledgeCount: 36, testScore: 88, level: 9, avatar: '' },
  { nickname: '王同学', role: 1, roleName: '学生', points: 4230, knowledgeCount: 34, testScore: 85, level: 8, avatar: '' }
])

const warningStats = reactive({ urgent: 5, important: 12, normal: 31 })

const hotTopics = ref([
  { name: '如何识别冒充客服诈骗', count: 12580 },
  { name: '刷单返利是真的吗', count: 9820 },
  { name: '杀猪盘诈骗全过程', count: 8760 },
  { name: '投资理财防骗指南', count: 7650 },
  { name: '网络购物安全建议', count: 6540 }
])

const regionData = ref([
  { region: '北京', users: 12560, learningHours: 45678, avgScore: 85.6, reportCount: 120, growth: 12 },
  { region: '上海', users: 11890, learningHours: 42345, avgScore: 84.2, reportCount: 98, growth: 8 },
  { region: '广东', users: 15670, learningHours: 51234, avgScore: 82.5, reportCount: 145, growth: 15 },
  { region: '浙江', users: 9876, learningHours: 38901, avgScore: 86.8, reportCount: 78, growth: 10 },
  { region: '江苏', users: 11234, learningHours: 40123, avgScore: 83.9, reportCount: 89, growth: 6 }
])

const handleExport = () => {
  ElMessage.success('数据导出功能开发中')
}

onMounted(() => {
  // 初始化图表
})
</script>

<style scoped>
.data-analysis { max-width: 1440px; margin: 0 auto; }
.page-header-card { margin-bottom: var(--spacing-6); }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.header-info h1 { margin: 0 0 var(--spacing-2); font-size: var(--font-size-3xl); }
.header-info p { margin: 0; color: var(--text-secondary); }
.header-actions { display: flex; gap: var(--spacing-3); }
.stats-row { margin-bottom: var(--spacing-6); }
.stat-card { padding: var(--spacing-4); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-sm); display: flex; flex-direction: column; gap: var(--spacing-3); }
.stat-trend { display: flex; align-items: center; gap: 4px; font-size: var(--font-size-sm); font-weight: var(--font-weight-medium); }
.stat-trend.up { color: var(--success-color); }
.stat-trend.down { color: var(--danger-color); }
.stat-main { display: flex; flex-direction: column; }
.stat-value { font-size: var(--font-size-3xl); font-weight: var(--font-weight-bold); }
.stat-label { font-size: var(--font-size-sm); color: var(--text-secondary); }
.chart-card, .table-card { margin-bottom: var(--spacing-6); }
.chart-container, .pie-chart-container, .bar-chart-container { min-height: 300px; display: flex; align-items: center; justify-content: center; color: var(--text-secondary); }
.legend-list { display: flex; flex-direction: column; gap: var(--spacing-3); padding: var(--spacing-4) 0; }
.legend-item { display: flex; align-items: center; gap: var(--spacing-2); }
.legend-dot { width: 12px; height: 12px; border-radius: 50%; }
.legend-name { flex: 1; }
.legend-value { font-weight: var(--font-weight-medium); }
.progress-list { display: flex; flex-direction: column; gap: var(--spacing-4); }
.progress-item { }
.progress-header { display: flex; justify-content: space-between; margin-bottom: var(--spacing-2); }
.progress-name { font-weight: var(--font-weight-medium); }
.progress-value { color: var(--text-secondary); }
.progress-meta { display: flex; justify-content: space-between; font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: var(--spacing-1); }
.user-cell { display: flex; align-items: center; gap: var(--spacing-2); }
.warning-stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--spacing-4); }
.warning-item { display: flex; align-items: center; gap: var(--spacing-3); padding: var(--spacing-3); background: var(--bg-page); border-radius: var(--radius-md); }
.warning-icon { width: 40px; height: 40px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; color: white; }
.warning-icon.urgent { background: var(--danger-color); }
.warning-icon.important { background: var(--warning-color); }
.warning-icon.normal { background: var(--info-color); }
.warning-info { display: flex; flex-direction: column; }
.warning-count { font-size: var(--font-size-xl); font-weight: var(--font-weight-bold); }
.warning-label { font-size: var(--font-size-xs); color: var(--text-secondary); }
.hot-topics h4 { margin: var(--spacing-4) 0 var(--spacing-3); }
.topic-item { display: flex; align-items: center; gap: var(--spacing-3); padding: var(--spacing-2) 0; border-bottom: 1px solid var(--border-color); }
.topic-rank { width: 20px; height: 20px; background: var(--bg-page); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: var(--font-size-xs); font-weight: var(--font-weight-bold); }
.topic-name { flex: 1; }
.topic-count { font-size: var(--font-size-xs); color: var(--text-secondary); }
.up { color: var(--success-color); }
.down { color: var(--danger-color); }
@media (max-width: 768px) {
  .header-content { flex-direction: column; gap: var(--spacing-4); align-items: flex-start; }
  .warning-stats { grid-template-columns: 1fr; }
}
</style>
