<template>
  <div class="external-warning-page" :class="{ 'visible': isVisible }">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
        </div>
      </div>
      <div class="header-content" data-aos="fade-up" data-aos-duration="1000">
        <h1 data-aos="fade-up" data-aos-delay="100">官方反诈通报</h1>
        <p data-aos="fade-up" data-aos-delay="200">获取最新的官方反诈预警信息，让您第一时间了解最新的诈骗手段</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 预警统计 -->
      <div class="stats-section">
        <div class="stat-card" v-for="(stat, index) in stats" :key="stat.label" data-aos="fade-up" :data-aos-delay="100 * index" data-aos-duration="800">
          <div class="stat-icon" :style="{ background: stat.gradient }">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
        </div>
      </div>

      <!-- 预警筛选 -->
      <div class="filter-section" data-aos="fade-up" data-aos-delay="300" data-aos-duration="800">
        <div class="filter-card">
          <div class="filter-header">
            <h3>预警筛选</h3>
            <el-button type="primary" @click="syncAllWarnings" :loading="syncLoading" class="sync-button">
              <el-icon><Refresh /></el-icon>
              同步预警信息
            </el-button>
          </div>
          <div class="filter-content">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-select v-model="filterForm.type" placeholder="预警类型" class="w-full">
                  <el-option label="全部" value="" />
                  <el-option label="电信诈骗" :value="1" />
                  <el-option label="网络诈骗" :value="2" />
                  <el-option label="金融诈骗" :value="3" />
                  <el-option label="其他" :value="4" />
                </el-select>
              </el-col>
              <el-col :span="8">
                <el-select v-model="filterForm.source" placeholder="预警来源" class="w-full">
                  <el-option label="全部" value="" />
                  <el-option label="公安部刑侦局" value="公安部刑侦局" />
                  <el-option label="国家反诈中心" value="国家反诈中心" />
                  <el-option label="12381预警系统" value="12381预警系统" />
                </el-select>
              </el-col>
              <el-col :span="8">
                <el-button type="primary" @click="filterWarnings" class="filter-button">
                  <el-icon><Search /></el-icon>
                  筛选
                </el-button>
                <el-button @click="resetFilter" class="reset-button">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>

      <!-- 最新预警 -->
      <div class="warnings-section" data-aos="fade-up" data-aos-delay="400" data-aos-duration="800">
        <div class="section-header">
          <h2>最新官方预警</h2>
          <el-dropdown>
            <el-button type="primary" class="sync-source-button">
              <el-icon><Plus /></el-icon>
              同步来源
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="manualSync('公安部刑侦局')">公安部刑侦局</el-dropdown-item>
                <el-dropdown-item @click="manualSync('国家反诈中心')">国家反诈中心</el-dropdown-item>
                <el-dropdown-item @click="manualSync('12381预警系统')">12381预警系统</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div class="warnings-list">
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>
          <div v-else-if="warnings.length === 0" class="empty-container">
            <el-empty description="暂无预警信息" />
          </div>
          <div v-else class="warning-item" v-for="(warning, index) in warnings" :key="warning.id" @click="viewWarning(warning)" data-aos="fade-up" :data-aos-delay="100 * (index % 3)" data-aos-duration="600">
            <div class="warning-source" :class="getSourceClass(warning.source)">
              {{ warning.source }}
            </div>
            <div class="warning-icon" :class="getTypeClass(warning.warningType)">
              <el-icon><component :is="getTypeIcon(warning.warningType)" /></el-icon>
            </div>
            <div class="warning-content">
              <div class="warning-header">
                <span class="warning-type">{{ warning.typeName }}</span>
                <span class="warning-time">{{ formatTime(warning.publishTime) }}</span>
              </div>
              <h4>{{ warning.title }}</h4>
              <p>{{ warning.content }}</p>
              <div class="warning-meta">
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ warning.viewCount || 0 }} 浏览
                </span>
                <span class="meta-item">
                  <el-icon><Share /></el-icon>
                  {{ warning.shareCount || 0 }} 分享
                </span>
              </div>
            </div>
            <div class="warning-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="!loading && warnings.length > 0" data-aos="fade-up" data-aos-delay="500" data-aos-duration="800">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 预警详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="selectedWarning?.title || '预警详情'"
      width="800px"
      :close-on-click-modal="false"
      custom-class="warning-detail-dialog"
    >
      <div v-if="selectedWarning" class="warning-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <span class="source-tag" :class="getSourceClass(selectedWarning.source)">
              {{ selectedWarning.source }}
            </span>
            <span class="type-tag">{{ selectedWarning.typeName }}</span>
            <span class="time-tag">{{ formatTime(selectedWarning.publishTime) }}</span>
          </div>
        </div>
        <div class="detail-content">
          <p v-for="(paragraph, index) in selectedWarning.content.split('\n')" :key="index" class="content-paragraph">
            {{ paragraph }}
          </p>
        </div>
        <div class="detail-footer">
          <div class="detail-meta">
            <span class="meta-item">
              <el-icon><View /></el-icon>
              {{ selectedWarning.viewCount || 0 }} 浏览
            </span>
            <span class="meta-item">
              <el-icon><Share /></el-icon>
              {{ selectedWarning.shareCount || 0 }} 分享
            </span>
            <span class="meta-item">
              <el-icon><Link /></el-icon>
              <a :href="selectedWarning.sourceUrl" target="_blank" class="source-link">
                查看原文
              </a>
            </span>
          </div>
          <div class="share-actions">
            <el-button type="primary" @click="shareWarning(selectedWarning)" class="share-button">
              <el-icon><Share /></el-icon>
              分享
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import * as VueRouter from 'vue-router'
const useRouter = VueRouter.useRouter
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  View, Plus, Refresh, Search, ArrowDown, ArrowRight, Share, Link
} from '@element-plus/icons-vue'
import {
  getLatestWarnings,
  getWarningsByType,
  getWarningsBySource,
  getExternalWarningDetail,
  manualSyncWarnings,
  syncAllWarnings as syncAllWarningsApi,
  getExternalWarningStatistics
} from '@/api/warning'

const router = useRouter()
const showDetailDialog = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)
const loading = ref(false)
const syncLoading = ref(false)
const isVisible = ref(false)

const filterForm = reactive({
  type: '',
  source: ''
})

const selectedWarning = ref<any>(null)

const stats = ref([
  { icon: 'Bell', value: '0', label: '最新预警', gradient: 'var(--gradient-danger)' },
  { icon: 'Calendar', value: '0', label: '本周预警', gradient: 'var(--gradient-warning)' },
  { icon: 'Message', value: '0', label: '本月预警', gradient: 'var(--gradient-info)' },
  { icon: 'Flag', value: '0', label: '预警来源', gradient: 'var(--gradient-success)' }
])

const warnings = ref<any[]>([])

// 页面加载时获取数据
onMounted(() => {
  isVisible.value = true
  loadStatistics()
  loadLatestWarnings()
})

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await getExternalWarningStatistics()
    if (response.code === 200 && response.data) {
      const data = response.data
      stats.value = [
        { icon: 'Bell', value: (data.totalWarnings || 0).toString(), label: '最新预警', gradient: 'var(--gradient-danger)' },
        { icon: 'Calendar', value: (data.weekWarnings || 0).toString(), label: '本周预警', gradient: 'var(--gradient-warning)' },
        { icon: 'Message', value: (data.monthWarnings || 0).toString(), label: '本月预警', gradient: 'var(--gradient-info)' },
        { icon: 'Flag', value: (data.sourceCount || 0).toString(), label: '预警来源', gradient: 'var(--gradient-success)' }
      ]
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载最新预警信息
const loadLatestWarnings = async () => {
  loading.value = true
  try {
    const response = await getLatestWarnings(50) // 加载50条最新预警
    if (response.code === 200 && response.data) {
      warnings.value = response.data
      total.value = response.data.length
    }
  } catch (error) {
    console.error('加载预警信息失败:', error)
    ElMessage.error('加载预警信息失败')
  } finally {
    loading.value = false
  }
}

// 筛选预警信息
const filterWarnings = async () => {
  loading.value = true
  try {
    if (filterForm.type) {
      const response = await getWarningsByType(filterForm.type, 50)
      if (response.code === 200 && response.data) {
        warnings.value = response.data
        total.value = response.data.length
      }
    } else if (filterForm.source) {
      const response = await getWarningsBySource(filterForm.source, 50)
      if (response.code === 200 && response.data) {
        warnings.value = response.data
        total.value = response.data.length
      }
    } else {
      await loadLatestWarnings()
    }
  } catch (error) {
    console.error('筛选预警信息失败:', error)
    ElMessage.error('筛选预警信息失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选条件
const resetFilter = () => {
  filterForm.type = ''
  filterForm.source = ''
  loadLatestWarnings()
}

// 手动同步预警信息
const manualSync = async (source: string) => {
  try {
    syncLoading.value = true
    const response = await manualSyncWarnings(source)
    if (response.code === 200) {
      ElMessage.success(`同步${source}预警信息成功，共同步 ${response.data} 条`)
      await loadStatistics()
      await loadLatestWarnings()
    } else {
      ElMessage.error(`同步${source}预警信息失败`)
    }
  } catch (error) {
    console.error('手动同步预警信息失败:', error)
    ElMessage.error('手动同步预警信息失败')
  } finally {
    syncLoading.value = false
  }
}

// 同步所有预警信息
const syncAllWarnings = async () => {
  try {
    syncLoading.value = true
    const response = await syncAllWarningsApi()
    if (response.code === 200) {
      ElMessage.success(`同步所有预警信息成功，共同步 ${response.data} 条`)
      await loadStatistics()
      await loadLatestWarnings()
    } else {
      ElMessage.error('同步所有预警信息失败')
    }
  } catch (error) {
    console.error('同步所有预警信息失败:', error)
    ElMessage.error('同步所有预警信息失败')
  } finally {
    syncLoading.value = false
  }
}

// 查看预警详情
const viewWarning = async (warning: any) => {
  try {
    const response = await getExternalWarningDetail(warning.id)
    if (response.code === 200 && response.data) {
      selectedWarning.value = response.data
      showDetailDialog.value = true
    }
  } catch (error) {
    console.error('获取预警详情失败:', error)
    ElMessage.error('获取预警详情失败')
  }
}

// 分享预警信息
const shareWarning = (warning: any) => {
  // 这里可以调用分享功能
  ElMessage.info('分享功能开发中')
}

// 处理分页
const handleSizeChange = (size: number) => {
  pageSize.value = size
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取来源样式类
const getSourceClass = (source: string) => {
  if (source.includes('公安部')) {
    return 'source-mps'
  } else if (source.includes('国家反诈中心')) {
    return 'source-nfac'
  } else if (source.includes('12381')) {
    return 'source-12381'
  }
  return 'source-other'
}

// 获取类型样式类
const getTypeClass = (type: number) => {
  switch (type) {
    case 1: return 'type-phone'
    case 2: return 'type-online'
    case 3: return 'type-finance'
    case 4: return 'type-other'
    default: return 'type-other'
  }
}

// 获取类型图标
const getTypeIcon = (type: number) => {
  switch (type) {
    case 1: return 'Phone'
    case 2: return 'Monitor'
    case 3: return 'Wallet'
    case 4: return 'WarnTriangleFilled'
    default: return 'WarnTriangleFilled'
  }
}
</script>

<style scoped>
.external-warning-page {
  min-height: 100vh;
  background: var(--bg-secondary);
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.6s ease-out, transform 0.6s ease-out;
}

.external-warning-page.visible {
  opacity: 1;
  transform: translateY(0);
}

.page-header {
  position: relative;
  padding: var(--spacing-16) var(--spacing-6);
  overflow: hidden;
}

.header-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.header-bg .bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #3b82f6 0%, #60a5fa 50%, #93c5fd 100%);
}

.floating-shapes {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 60px;
  height: 60px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 80px;
  height: 80px;
  bottom: 10%;
  left: 30%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.header-content {
  position: relative;
  z-index: 1;
  max-width: 1280px;
  margin: 0 auto;
  text-align: center;
  color: white;
}

.header-content h1 {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-4);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content p {
  font-size: var(--font-size-lg);
  opacity: 0.9;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6) var(--spacing-12);
}

/* 统计 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-4);
  margin-top: calc(-1 * var(--spacing-10));
  margin-bottom: var(--spacing-8);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-5);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  background: rgba(255, 255, 255, 1);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--gradient-danger);
  opacity: 0.8;
}

.stat-card:nth-child(2)::before {
  background: var(--gradient-warning);
}

.stat-card:nth-child(3)::before {
  background: var(--gradient-info);
}

.stat-card:nth-child(4)::before {
  background: var(--gradient-success);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

/* 筛选 */
.filter-section {
  margin-bottom: var(--spacing-8);
}

.filter-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-5);
  box-shadow: var(--shadow-md);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all var(--transition-normal);
}

.filter-card:hover {
  box-shadow: var(--shadow-lg);
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-4);
}

.filter-header h3 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.sync-button, .filter-button, .reset-button, .sync-source-button, .share-button {
  transition: all var(--transition-normal);
  border-radius: var(--radius-lg);
}

.sync-button:hover, .filter-button:hover, .sync-source-button:hover, .share-button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.reset-button:hover {
  background: var(--bg-secondary);
  transform: translateY(-2px);
}

/* 预警列表 */
.warnings-section {
  margin-bottom: var(--spacing-8);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
}

.section-header h2 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  position: relative;
}

.section-header h2::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 60px;
  height: 3px;
  background: var(--gradient-primary);
  border-radius: 2px;
}

.warnings-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-6);
}

.loading-container {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-5);
  box-shadow: var(--shadow-md);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.empty-container {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-12);
  box-shadow: var(--shadow-md);
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.warning-item {
  display: flex;
  gap: var(--spacing-4);
  padding: var(--spacing-5);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.warning-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--gradient-primary);
  opacity: 0.8;
}

.warning-item:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 1);
}

.warning-source {
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  color: white;
  flex-shrink: 0;
  align-self: flex-start;
  margin-top: 8px;
  transition: all var(--transition-normal);
}

.warning-source.source-mps {
  background: #1890ff;
}

.warning-source.source-nfac {
  background: #52c41a;
}

.warning-source.source-12381 {
  background: #faad14;
}

.warning-source.source-other {
  background: #722ed1;
}

.warning-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all var(--transition-normal);
}

.warning-item:hover .warning-icon {
  transform: scale(1.1);
}

.warning-icon.type-phone { background: var(--danger-color); }
.warning-icon.type-online { background: var(--warning-color); }
.warning-icon.type-finance { background: var(--info-color); }
.warning-icon.type-other { background: var(--success-color); }

.warning-content {
  flex: 1;
}

.warning-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-2);
}

.warning-type {
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--info-bg);
  color: var(--info-color);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-sm);
  transition: all var(--transition-normal);
}

.warning-item:hover .warning-type {
  background: var(--info-color);
  color: white;
}

.warning-time {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.warning-content h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
  transition: all var(--transition-normal);
}

.warning-item:hover .warning-content h4 {
  color: var(--primary-color);
}

.warning-content p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-3);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.5;
}

.warning-meta {
  display: flex;
  gap: var(--spacing-4);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-xs);
  color: var(--text-muted);
  transition: all var(--transition-normal);
}

.warning-item:hover .meta-item {
  color: var(--text-secondary);
}

.warning-arrow {
  display: flex;
  align-items: center;
  color: var(--text-muted);
  flex-shrink: 0;
  transition: all var(--transition-normal);
}

.warning-item:hover .warning-arrow {
  color: var(--primary-color);
  transform: translateX(5px);
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: var(--spacing-6);
}

/* 预警详情 */
.warning-detail {
  padding: var(--spacing-2);
}

.warning-detail-dialog {
  border-radius: var(--radius-xl) !important;
  overflow: hidden;
}

.warning-detail-dialog .el-dialog__header {
  background: var(--gradient-primary);
  color: white;
  padding: var(--spacing-5);
}

.warning-detail-dialog .el-dialog__title {
  color: white;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.warning-detail-dialog .el-dialog__headerbtn .el-icon {
  color: white;
}

.detail-header {
  margin-bottom: var(--spacing-4);
}

.detail-meta {
  display: flex;
  gap: var(--spacing-2);
  flex-wrap: wrap;
  margin-bottom: var(--spacing-4);
}

.source-tag {
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  color: white;
}

.type-tag {
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--info-bg);
  color: var(--info-color);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-sm);
}

.time-tag {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.detail-content {
  margin-bottom: var(--spacing-4);
  line-height: 1.6;
  padding: var(--spacing-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
}

.content-paragraph {
  margin-bottom: var(--spacing-2);
  color: var(--text-primary);
  text-align: justify;
}

.detail-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-4);
  border-top: 1px solid var(--border-color);
}

.source-link {
  color: var(--info-color);
  text-decoration: underline;
  transition: all var(--transition-normal);
}

.source-link:hover {
  color: var(--info-color-light);
  text-decoration: none;
}

.share-actions {
  display: flex;
  gap: var(--spacing-2);
}

/* 响应式 */
@media (max-width: 1024px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: var(--spacing-10) var(--spacing-4);
  }

  .header-content h1 {
    font-size: var(--font-size-2xl);
  }

  .stats-section {
    grid-template-columns: 1fr;
    margin-top: 0;
  }

  .warning-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .warning-arrow {
    align-self: flex-end;
    margin-top: var(--spacing-2);
  }

  .detail-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-2);
  }

  .filter-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-2);
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-2);
  }

  .section-header h2::after {
    bottom: -4px;
  }
}
</style>
