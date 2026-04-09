<template>
  <div class="activity-page">
    <div class="page-header">
      <h2>活动中心</h2>
      <p class="subtitle">参与反诈宣传活动，提升安全意识</p>
    </div>
    
    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="filters.type" placeholder="活动类型" clearable @change="loadActivities">
            <el-option label="全部" value="" />
            <el-option label="知识竞赛" :value="1" />
            <el-option label="主题讲座" :value="2" />
            <el-option label="线下活动" :value="3" />
            <el-option label="线上活动" :value="4" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filters.status" placeholder="活动状态" clearable @change="loadActivities">
            <el-option label="全部" value="" />
            <el-option label="报名中" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已结束" :value="3" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input v-model="filters.keyword" placeholder="搜索活动名称" clearable @keyup.enter="loadActivities">
            <template #append>
              <el-button @click="loadActivities">搜索</el-button>
            </template>
          </el-input>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 活动列表 -->
    <el-row :gutter="20" class="activity-list">
      <el-col :span="8" v-for="activity in activityList" :key="activity.id">
        <el-card class="activity-card" shadow="hover" @click="$router.push(`/activity/${activity.id}`)">
          <div class="activity-cover">
            <img :src="activity.coverImage || defaultCover" />
            <div class="activity-status" :class="getStatusClass(activity.status)">
              {{ getStatusText(activity.status) }}
            </div>
            <div class="activity-type">
              <el-tag size="small" effect="dark">{{ getTypeText(activity.activityType) }}</el-tag>
            </div>
          </div>
          <div class="activity-info">
            <h3 class="activity-title">{{ activity.activityName }}</h3>
            <div class="activity-meta">
              <span><el-icon><Calendar /></el-icon> {{ formatDate(activity.startTime) }}</span>
              <span><el-icon><Location /></el-icon> {{ activity.location || '线上' }}</span>
            </div>
            <div class="activity-participants">
              <el-progress 
                :percentage="getParticipantPercent(activity)" 
                :format="() => `${activity.currentParticipants || 0}/${activity.maxParticipants || '不限'}`"
              />
            </div>
            <div class="activity-points" v-if="activity.pointsReward">
              <el-icon><Star /></el-icon>
              完成奖励 {{ activity.pointsReward }} 积分
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 空状态 -->
    <el-empty v-if="activityList.length === 0 && !loading" description="暂无活动" />
    
    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[9, 18, 36]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadActivities"
        @size-change="loadActivities"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get } from '@/utils/request'

interface Activity {
  id: number
  activityName: string
  activityType: number
  coverImage: string
  description: string
  startTime: string
  endTime: string
  location: string
  maxParticipants: number
  currentParticipants: number
  pointsReward: number
  status: number
  isRegistered?: boolean
}

const defaultCover = 'https://images.unsplash.com/photo-1557804506-669a67965ba0?w=400&h=250&fit=crop'

const loading = ref(false)
const activityList = ref<Activity[]>([])
const page = ref(1)
const size = ref(9)
const total = ref(0)

const filters = reactive({
  type: '',
  status: '',
  keyword: ''
})

const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    1: '报名中',
    2: '进行中',
    3: '已结束'
  }
  return statusMap[status] || '未知'
}

const getStatusClass = (status: number) => {
  const classMap: Record<number, string> = {
    1: 'status-registering',
    2: 'status-ongoing',
    3: 'status-ended'
  }
  return classMap[status] || ''
}

const getTypeText = (type: number) => {
  const typeMap: Record<number, string> = {
    1: '知识竞赛',
    2: '主题讲座',
    3: '线下活动',
    4: '线上活动'
  }
  return typeMap[type] || '其他'
}

const formatDate = (date: string) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const getParticipantPercent = (activity: Activity) => {
  if (!activity.maxParticipants) return 0
  return Math.round((activity.currentParticipants / activity.maxParticipants) * 100)
}

const loadActivities = async () => {
  loading.value = true
  try {
    const res = await get('/activity/list', {
      page: page.value,
      size: size.value,
      type: filters.type || undefined,
      status: filters.status || undefined,
      keyword: filters.keyword || undefined
    })
    activityList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e: any) {
    // 接口失败时显示空状态，不使用模拟数据
    activityList.value = []
    total.value = 0
    console.error('加载活动列表失败:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.activity-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 28px;
  color: #1a365d;
  margin-bottom: 8px;
}

.page-header .subtitle {
  color: #718096;
  font-size: 15px;
}

.filter-card {
  margin-bottom: 24px;
}

.filter-card :deep(.el-select),
.filter-card :deep(.el-input) {
  width: 100%;
}

.activity-list {
  margin-bottom: 24px;
}

.activity-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

.activity-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.activity-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.activity-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.activity-card:hover .activity-cover img {
  transform: scale(1.05);
}

.activity-status {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.activity-status.status-registering {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
  color: white;
}

.activity-status.status-ongoing {
  background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%);
  color: white;
}

.activity-status.status-ended {
  background: #a0aec0;
  color: white;
}

.activity-type {
  position: absolute;
  bottom: 12px;
  left: 12px;
}

.activity-info {
  padding: 16px;
}

.activity-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a365d;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.activity-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #718096;
}

.activity-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.activity-participants {
  margin-bottom: 8px;
}

.activity-points {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #d69e2e;
  font-size: 13px;
  font-weight: 500;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
</style>
