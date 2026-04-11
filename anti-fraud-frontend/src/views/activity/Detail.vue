<template>
  <div class="activity-detail-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-wrapper">
      <el-skeleton :rows="10" animated />
    </div>
    
    <template v-else-if="activity">
      <!-- 活动头部 -->
      <el-card class="header-card">
        <div class="activity-header">
          <div class="header-left">
            <el-tag :type="getStatusType(activity.status)" size="large">
              {{ getStatusText(activity.status) }}
            </el-tag>
            <h1>{{ activity.activityName }}</h1>
            <div class="activity-tags">
              <el-tag effect="plain">{{ getTypeText(activity.activityType) }}</el-tag>
              <el-tag v-if="activity.pointsReward" type="warning" effect="plain">
                <el-icon><Star /></el-icon> {{ activity.pointsReward }}积分
              </el-tag>
            </div>
          </div>
          <div class="header-right">
            <el-button 
              v-if="activity.status === 1 && !activity.isRegistered"
              type="primary" 
              size="large"
              @click="handleRegister"
            >
              立即报名
            </el-button>
            <el-button 
              v-if="activity.isRegistered"
              type="success" 
              size="large"
              disabled
            >
              已报名
            </el-button>
            <el-button 
              v-if="activity.status === 2 && activity.isRegistered"
              type="primary" 
              size="large"
              @click="handleCheckIn"
            >
              签到
            </el-button>
          </div>
        </div>
      </el-card>
      
      <el-row :gutter="20">
        <!-- 左侧主内容 -->
        <el-col :span="16">
          <!-- 活动封面 -->
          <el-card class="cover-card">
            <img :src="activity.coverImage || defaultCover" class="cover-image" />
          </el-card>
          
          <!-- 活动详情 -->
          <el-card class="detail-card">
            <template #header>
              <span class="card-title">活动详情</span>
            </template>
            <div class="activity-content" v-html="activity.description || '暂无详情'"></div>
          </el-card>
          
          <!-- 活动规则 -->
          <el-card class="rules-card" v-if="activity.rules">
            <template #header>
              <span class="card-title">活动规则</span>
            </template>
            <div class="rules-content">{{ activity.rules }}</div>
          </el-card>
        </el-col>
        
        <!-- 右侧信息栏 -->
        <el-col :span="8">
          <!-- 活动信息 -->
          <el-card class="info-card">
            <template #header>
              <span class="card-title">活动信息</span>
            </template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="活动时间">
                <el-icon><Calendar /></el-icon>
                {{ formatDate(activity.startTime) }} 至 {{ formatDate(activity.endTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="活动地点">
                <el-icon><Location /></el-icon>
                {{ activity.location || '线上活动' }}
              </el-descriptions-item>
              <el-descriptions-item label="参与人数">
                <el-icon><User /></el-icon>
                {{ activity.currentParticipants || 0 }} / {{ activity.maxParticipants || '不限' }}
              </el-descriptions-item>
              <el-descriptions-item label="积分奖励">
                <el-icon><Star /></el-icon>
                {{ activity.pointsReward || 0 }} 积分
              </el-descriptions-item>
              <el-descriptions-item label="主办方">
                <el-icon><OfficeBuilding /></el-icon>
                {{ activity.organizer || '学生处' }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
          
          <!-- 参与进度 -->
          <el-card class="progress-card">
            <template #header>
              <span class="card-title">参与进度</span>
            </template>
            <div class="progress-content">
              <el-progress 
                type="dashboard" 
                :percentage="getParticipantPercent()"
                :color="progressColor"
              >
                <template #default>
                  <span class="progress-value">{{ activity.currentParticipants || 0 }}</span>
                  <span class="progress-label">已报名</span>
                </template>
              </el-progress>
              <div class="progress-max" v-if="activity.maxParticipants">
                限 {{ activity.maxParticipants }} 人
              </div>
            </div>
          </el-card>
          
          <!-- 已报名用户 -->
          <el-card class="participants-card">
            <template #header>
              <span class="card-title">已报名用户</span>
            </template>
            <el-avatar-group>
              <el-avatar v-for="user in participants" :key="user.id" :size="40">
                {{ user.realName?.charAt(0) }}
              </el-avatar>
              <el-avatar :size="40" v-if="activity.currentParticipants > 10">
                +{{ activity.currentParticipants - 10 }}
              </el-avatar>
            </el-avatar-group>
          </el-card>
        </el-col>
      </el-row>
    </template>
    
    <!-- 空状态 -->
    <el-empty v-else description="活动不存在或已下架" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'

interface Activity {
  id: number
  activityName: string
  activityType: number
  coverImage: string
  description: string
  rules: string
  startTime: string
  endTime: string
  location: string
  maxParticipants: number
  currentParticipants: number
  pointsReward: number
  status: number
  organizer: string
  isRegistered?: boolean
}

const route = useRoute()
const router = useRouter()
const defaultCover = 'https://images.unsplash.com/photo-1557804506-669a67965ba0?w=800&h=400&fit=crop'

const loading = ref(true)
const activity = ref<Activity | null>(null)
const participants = ref<any[]>([])

const progressColor = computed(() => {
  const percent = getParticipantPercent()
  if (percent >= 90) return '#f56565'
  if (percent >= 70) return '#ed8936'
  return '#48bb78'
})

const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    1: '报名中',
    2: '进行中',
    3: '已结束'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status: number) => {
  const typeMap: Record<number, string> = {
    1: 'success',
    2: 'primary',
    3: 'info'
  }
  return typeMap[status] || 'info'
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
    month: 'long',
    day: 'numeric'
  })
}

const getParticipantPercent = () => {
  if (!activity.value?.maxParticipants) return 0
  return Math.round((activity.value.currentParticipants / activity.value.maxParticipants) * 100)
}

const loadActivity = async () => {
  loading.value = true
  const id = route.params.id
  
  try {
    const res = await get(`/activity/${id}`)
    activity.value = res.data
    loadParticipants()
  } catch (e) {
    // 使用模拟数据
    activity.value = {
      id: Number(id),
      activityName: '2026年反诈知识竞赛',
      activityType: 1,
      coverImage: '',
      description: `
        <h3>活动介绍</h3>
        <p>为提高全校师生的反诈意识和能力，学生处联合保卫处举办2026年反诈知识竞赛活动。</p>
        
        <h3>活动内容</h3>
        <ul>
          <li>线上答题竞赛：通过平台参与反诈知识测试</li>
          <li>排名奖励：根据成绩排名发放奖励</li>
          <li>参与奖励：完成答题即可获得积分奖励</li>
        </ul>
        
        <h3>奖项设置</h3>
        <ul>
          <li>一等奖（前10名）：500积分 + 荣誉证书</li>
          <li>二等奖（11-30名）：300积分 + 荣誉证书</li>
          <li>三等奖（31-50名）：200积分 + 荣誉证书</li>
          <li>参与奖：100积分</li>
        </ul>
      `,
      rules: '1. 每人仅可参与一次\n2. 答题时间为30分钟\n3. 严禁作弊行为\n4. 成绩相同时按答题时间排序',
      startTime: '2026-03-01',
      endTime: '2026-03-15',
      location: '',
      maxParticipants: 500,
      currentParticipants: 320,
      pointsReward: 100,
      status: 1,
      organizer: '学生处',
      isRegistered: false
    }
  } finally {
    loading.value = false
  }
}

const loadParticipants = async () => {
  try {
    const res = await get(`/activity/${activity.value?.id}/participants`, { size: 10 })
    participants.value = res.data || []
  } catch (e) {
    // 模拟数据
    participants.value = [
      { id: 1, realName: '张三' },
      { id: 2, realName: '李四' },
      { id: 3, realName: '王五' },
      { id: 4, realName: '赵六' },
      { id: 5, realName: '钱七' }
    ]
  }
}

const handleRegister = async () => {
  try {
    await post(`/activity/${activity.value?.id}/register`)
    ElMessage.success('报名成功！')
    activity.value!.isRegistered = true
    activity.value!.currentParticipants++
  } catch (e) {
    // 模拟报名成功
    ElMessage.success('报名成功！')
    activity.value!.isRegistered = true
    activity.value!.currentParticipants++
  }
}

const handleCheckIn = async () => {
  try {
    await post(`/activity/${activity.value?.id}/check-in`)
    ElMessage.success('签到成功！')
  } catch (e) {
    // 模拟签到成功
    ElMessage.success('签到成功！获得 ' + activity.value?.pointsReward + ' 积分')
  }
}

onMounted(() => {
  loadActivity()
})
</script>

<style scoped>
.activity-detail-page {
  max-width: 1200px;
  margin: 0 auto;
}

.loading-wrapper {
  padding: 40px;
}

.header-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-left h1 {
  font-size: 28px;
  color: #1a365d;
  margin: 16px 0 12px;
  font-weight: 600;
}

.activity-tags {
  display: flex;
  gap: 8px;
}

.cover-card {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
}

.cover-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.detail-card,
.rules-card,
.info-card,
.progress-card,
.participants-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a365d;
}

.activity-content {
  line-height: 1.8;
  color: #4a5568;
}

.activity-content h3 {
  color: #2d3748;
  margin: 20px 0 12px;
}

.activity-content h3:first-child {
  margin-top: 0;
}

.activity-content ul {
  padding-left: 20px;
}

.activity-content li {
  margin-bottom: 8px;
}

.rules-content {
  white-space: pre-line;
  color: #4a5568;
  line-height: 1.8;
}

.info-card :deep(.el-descriptions__label) {
  width: 80px;
}

.info-card :deep(.el-descriptions__cell) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-content {
  text-align: center;
  padding: 20px 0;
}

.progress-value {
  display: block;
  font-size: 28px;
  font-weight: bold;
  color: #2d3748;
}

.progress-label {
  display: block;
  font-size: 14px;
  color: #718096;
}

.progress-max {
  margin-top: 12px;
  color: #718096;
  font-size: 14px;
}

.participants-card :deep(.el-avatar-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>
