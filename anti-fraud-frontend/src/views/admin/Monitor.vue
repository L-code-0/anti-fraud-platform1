<template>
  <div class="system-monitor-page">
    <!-- 概览卡片 -->
    <div class="overview-cards">
      <div class="overview-card">
        <div class="card-header">
          <span class="card-title">CPU 使用率</span>
          <el-icon class="card-icon blue"><Cpu /></el-icon>
        </div>
        <div class="card-value">{{ systemMetrics.cpu }}%</div>
        <div class="card-chart">
          <el-progress :percentage="systemMetrics.cpu" :stroke-width="8" :show-text="false" />
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-header">
          <span class="card-title">内存使用率</span>
          <el-icon class="card-icon green"><Monitor /></el-icon>
        </div>
        <div class="card-value">{{ systemMetrics.memory }}%</div>
        <div class="card-chart">
          <el-progress :percentage="systemMetrics.memory" :stroke-width="8" :show-text="false" color="#67c23a" />
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-header">
          <span class="card-title">磁盘使用率</span>
          <el-icon class="card-icon orange"><Folder /></el-icon>
        </div>
        <div class="card-value">{{ systemMetrics.disk }}%</div>
        <div class="card-chart">
          <el-progress :percentage="systemMetrics.disk" :stroke-width="8" :show-text="false" color="#e6a23c" />
        </div>
      </div>
      
      <div class="overview-card">
        <div class="card-header">
          <span class="card-title">网络IO</span>
          <el-icon class="card-icon purple"><Connection /></el-icon>
        </div>
        <div class="card-value">{{ systemMetrics.networkIn }} / {{ systemMetrics.networkOut }}</div>
        <div class="card-label">输入 / 输出 (MB/s)</div>
      </div>
    </div>

    <!-- 实时监控图表 -->
    <div class="monitor-grid">
      <div class="monitor-card">
        <h3>CPU 使用趋势</h3>
        <div ref="cpuChartRef" class="chart"></div>
      </div>
      
      <div class="monitor-card">
        <h3>内存使用趋势</h3>
        <div ref="memoryChartRef" class="chart"></div>
      </div>
      
      <div class="monitor-card">
        <h3>请求量统计</h3>
        <div ref="requestChartRef" class="chart"></div>
      </div>
      
      <div class="monitor-card">
        <h3>响应时间</h3>
        <div ref="responseChartRef" class="chart"></div>
      </div>
    </div>

    <!-- 服务状态 -->
    <div class="services-card">
      <h3>服务状态</h3>
      <el-table :data="services" stripe>
        <el-table-column prop="name" label="服务名称" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'running' ? 'success' : 'danger'" size="small">
              {{ row.status === 'running' ? '运行中' : '已停止' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="uptime" label="运行时长" width="120" />
        <el-table-column prop="requests" label="请求数" width="100" />
        <el-table-column prop="errors" label="错误数" width="100">
          <template #default="{ row }">
            <span :class="{ 'error-count': row.errors > 0 }">{{ row.errors }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="cpu" label="CPU" width="80">
          <template #default="{ row }">
            {{ row.cpu }}%
          </template>
        </el-table-column>
        <el-table-column prop="memory" label="内存" width="80">
          <template #default="{ row }">
            {{ row.memory }}MB
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'running'" 
              text 
              type="danger" 
              size="small"
              @click="handleStopService(row)"
            >
              停止
            </el-button>
            <el-button 
              v-else 
              text 
              type="success" 
              size="small"
              @click="handleStartService(row)"
            >
              启动
            </el-button>
            <el-button text type="primary" size="small" @click="handleRestartService(row)">
              重启
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 系统信息 -->
    <div class="system-info-card">
      <h3>系统信息</h3>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="服务器名称">{{ systemInfo.hostname }}</el-descriptions-item>
        <el-descriptions-item label="操作系统">{{ systemInfo.os }}</el-descriptions-item>
        <el-descriptions-item label="运行时间">{{ systemInfo.uptime }}</el-descriptions-item>
        <el-descriptions-item label="Java版本">{{ systemInfo.javaVersion }}</el-descriptions-item>
        <el-descriptions-item label="应用版本">{{ systemInfo.appVersion }}</el-descriptions-item>
        <el-descriptions-item label="数据库连接">{{ systemInfo.dbConnections }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Cpu, Monitor, Folder, Connection } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const cpuChartRef = ref<HTMLElement>()
const memoryChartRef = ref<HTMLElement>()
const requestChartRef = ref<HTMLElement>()
const responseChartRef = ref<HTMLElement>()

let cpuChart: echarts.ECharts | null = null
let memoryChart: echarts.ECharts | null = null
let requestChart: echarts.ECharts | null = null
let responseChart: echarts.ECharts | null = null
let updateTimer: number | null = null

const systemMetrics = reactive({
  cpu: 35,
  memory: 62,
  disk: 45,
  networkIn: 12.5,
  networkOut: 8.3
})

const services = ref([
  { name: 'API服务', status: 'running', uptime: '5天 12小时', requests: 15420, errors: 2, cpu: 15, memory: 256 },
  { name: '数据库服务', status: 'running', uptime: '15天 3小时', requests: 8560, errors: 0, cpu: 25, memory: 512 },
  { name: '缓存服务', status: 'running', uptime: '15天 3小时', requests: 12300, errors: 0, cpu: 8, memory: 128 },
  { name: '文件服务', status: 'running', uptime: '10天 8小时', requests: 3200, errors: 1, cpu: 5, memory: 64 },
  { name: '任务调度', status: 'running', uptime: '15天 3小时', requests: 890, errors: 0, cpu: 3, memory: 32 }
])

const systemInfo = reactive({
  hostname: 'prod-server-01',
  os: 'CentOS 7.9',
  uptime: '15天 3小时 42分钟',
  javaVersion: 'JDK 17.0.5',
  appVersion: 'v2.3.1',
  dbConnections: '25 / 100'
})

const cpuData = ref<number[]>([])
const memoryData = ref<number[]>([])
const requestData = ref<number[]>([])
const responseData = ref<number[]>([])
const timeLabels = ref<string[]>([])

function initCharts() {
  // 初始化图表
  if (cpuChartRef.value) {
    cpuChart = echarts.init(cpuChartRef.value)
  }
  if (memoryChartRef.value) {
    memoryChart = echarts.init(memoryChartRef.value)
  }
  if (requestChartRef.value) {
    requestChart = echarts.init(requestChartRef.value)
  }
  if (responseChartRef.value) {
    responseChart = echarts.init(responseChartRef.value)
  }
  
  updateCharts()
}

function updateCharts() {
  const baseOptions = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: timeLabels.value,
      boundaryGap: false
    },
    yAxis: {
      type: 'value',
      boundaryGap: [0, '100%']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    }
  }
  
  if (cpuChart) {
    cpuChart.setOption({
      ...baseOptions,
      series: [{
        type: 'line',
        data: cpuData.value,
        smooth: true,
        areaStyle: { color: 'rgba(64, 158, 255, 0.3)' },
        lineStyle: { color: '#409eff', width: 2 },
        itemStyle: { color: '#409eff' }
      }]
    })
  }
  
  if (memoryChart) {
    memoryChart.setOption({
      ...baseOptions,
      series: [{
        type: 'line',
        data: memoryData.value,
        smooth: true,
        areaStyle: { color: 'rgba(103, 194, 58, 0.3)' },
        lineStyle: { color: '#67c23a', width: 2 },
        itemStyle: { color: '#67c23a' }
      }]
    })
  }
  
  if (requestChart) {
    requestChart.setOption({
      ...baseOptions,
      series: [{
        type: 'bar',
        data: requestData.value,
        itemStyle: { color: '#e6a23c' }
      }]
    })
  }
  
  if (responseChart) {
    responseChart.setOption({
      ...baseOptions,
      series: [{
        type: 'line',
        data: responseData.value,
        smooth: true,
        lineStyle: { color: '#9c27b0', width: 2 },
        itemStyle: { color: '#9c27b0' }
      }]
    })
  }
}

function generateNewData() {
  const now = new Date()
  const time = `${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`
  
  timeLabels.value.push(time)
  if (timeLabels.value.length > 20) {
    timeLabels.value.shift()
  }
  
  cpuData.value.push(Math.random() * 40 + 20)
  if (cpuData.value.length > 20) cpuData.value.shift()
  
  memoryData.value.push(Math.random() * 20 + 50)
  if (memoryData.value.length > 20) memoryData.value.shift()
  
  requestData.value.push(Math.floor(Math.random() * 100 + 50))
  if (requestData.value.length > 20) requestData.value.shift()
  
  responseData.value.push(Math.random() * 30 + 10)
  if (responseData.value.length > 20) responseData.value.shift()
  
  // 更新指标
  systemMetrics.cpu = Math.round(cpuData.value[cpuData.value.length - 1])
  systemMetrics.memory = Math.round(memoryData.value[memoryData.value.length - 1])
  systemMetrics.networkIn = +(Math.random() * 20 + 5).toFixed(1)
  systemMetrics.networkOut = +(Math.random() * 15 + 3).toFixed(1)
  
  updateCharts()
}

function handleStopService(service: any) {
  ElMessage.warning(`正在停止 ${service.name}...`)
  setTimeout(() => {
    service.status = 'stopped'
    ElMessage.success(`${service.name} 已停止`)
  }, 1000)
}

function handleStartService(service: any) {
  ElMessage.success(`正在启动 ${service.name}...`)
  setTimeout(() => {
    service.status = 'running'
    ElMessage.success(`${service.name} 已启动`)
  }, 1000)
}

function handleRestartService(service: any) {
  ElMessage.warning(`正在重启 ${service.name}...`)
  setTimeout(() => {
    ElMessage.success(`${service.name} 已重启`)
  }, 2000)
}

function handleResize() {
  cpuChart?.resize()
  memoryChart?.resize()
  requestChart?.resize()
  responseChart?.resize()
}

onMounted(() => {
  initCharts()
  // 初始化数据
  for (let i = 0; i < 10; i++) {
    generateNewData()
  }
  // 定时更新
  updateTimer = window.setInterval(generateNewData, 3000)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (updateTimer) {
    clearInterval(updateTimer)
  }
  window.removeEventListener('resize', handleResize)
  cpuChart?.dispose()
  memoryChart?.dispose()
  requestChart?.dispose()
  responseChart?.dispose()
})
</script>

<style scoped>
.system-monitor-page {
  padding: 24px;
}

/* 概览卡片 */
.overview-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.overview-card {
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.card-title {
  font-size: 14px;
  color: #666;
}

.card-icon {
  font-size: 24px;
}

.card-icon.blue { color: #409eff; }
.card-icon.green { color: #67c23a; }
.card-icon.orange { color: #e6a23c; }
.card-icon.purple { color: #9c27b0; }

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
}

.card-label {
  font-size: 12px;
  color: #999;
}

/* 监控图表 */
.monitor-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.monitor-card {
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.monitor-card h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #333;
}

.chart {
  height: 200px;
}

/* 服务状态 */
.services-card,
.system-info-card {
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
}

.services-card h3,
.system-info-card h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #333;
}

.error-count {
  color: #f56c6c;
  font-weight: 600;
}

@media (max-width: 1024px) {
  .overview-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .monitor-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .system-monitor-page {
    padding: 16px;
  }
  
  .overview-cards {
    grid-template-columns: 1fr;
  }
}
</style>
