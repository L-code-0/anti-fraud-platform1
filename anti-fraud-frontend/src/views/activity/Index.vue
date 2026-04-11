<template>
  <div class="activity-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
      </div>
      <div class="header-content">
        <h1>活动中心</h1>
        <p>参与精彩活动，赢取丰富奖励</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 活动类型筛选 -->
      <div class="filter-tabs">
        <button
          v-for="tab in typeTabs"
          :key="tab.id"
          class="tab-btn"
          :class="{ active: activeType === tab.id }"
          @click="activeType = tab.id"
        >
          <el-icon><component :is="tab.icon" /></el-icon>
          {{ tab.name }}
        </button>
      </div>

      <!-- 热门活动 -->
      <div class="featured-section">
        <div class="featured-card" v-if="featuredActivity">
          <div class="featured-image">
            <img :src="featuredActivity.cover" :alt="featuredActivity.title" />
            <div class="featured-badge">
              <el-icon><Star /></el-icon>
              热门活动
            </div>
            <div class="featured-overlay">
              <div class="countdown">
                <span class="countdown-label">距离结束</span>
                <div class="countdown-time">
                  <span class="time-block">{{ countdown.days }}</span>
                  <span class="time-separator">:</span>
                  <span class="time-block">{{ countdown.hours }}</span>
                  <span class="time-separator">:</span>
                  <span class="time-block">{{ countdown.minutes }}</span>
                  <span class="time-separator">:</span>
                  <span class="time-block">{{ countdown.seconds }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="featured-content">
            <h2>{{ featuredActivity.title }}</h2>
            <p>{{ featuredActivity.desc }}</p>
            <div class="featured-meta">
              <span><el-icon><Calendar /></el-icon> {{ featuredActivity.date }}</span>
              <span><el-icon><Location /></el-icon> {{ featuredActivity.location }}</span>
              <span><el-icon><User /></el-icon> {{ featuredActivity.participants }}人参与</span>
            </div>
            <el-button type="primary" size="large" @click="joinActivity(featuredActivity)">
              立即报名
            </el-button>
          </div>
        </div>
      </div>

      <!-- 活动列表 -->
      <div class="activities-section">
        <div class="section-header">
          <h2>全部活动</h2>
          <span class="activity-count">共{{ activities.length }}个活动</span>
        </div>

        <div class="activities-grid">
          <div
            class="activity-card"
            v-for="activity in activities"
            :key="activity.id"
            @click="goToDetail(activity)"
          >
            <div class="card-image">
              <img :src="activity.cover" :alt="activity.title" />
              <div class="card-status" :class="'status-' + activity.status">
                {{ activity.statusName }}
              </div>
              <div class="card-reward" v-if="activity.reward">
                <el-icon><Present /></el-icon>
                {{ activity.reward }}
              </div>
            </div>
            <div class="card-content">
              <h3>{{ activity.title }}</h3>
              <p>{{ activity.desc }}</p>
              <div class="card-info">
                <span><el-icon><Calendar /></el-icon> {{ activity.date }}</span>
                <span><el-icon><Location /></el-icon> {{ activity.location }}</span>
              </div>
              <div class="card-footer">
                <div class="participants">
                  <div class="avatar-stack">
                    <el-avatar v-for="(avatar, i) in activity.avatarList" :key="i" :size="24" :src="avatar" />
                  </div>
                  <span>{{ activity.participants }}人</span>
                </div>
                <el-button
                  type="primary"
                  size="small"
                  :disabled="activity.status === 'ended'"
                  @click.stop="joinActivity(activity)"
                >
                  {{ activity.status === 'ongoing' ? '立即报名' : activity.status === 'ended' ? '已结束' : '即将开始' }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 往期活动 -->
      <div class="past-section">
        <div class="section-header">
          <h2>往期精彩</h2>
        </div>

        <div class="past-grid">
          <div class="past-card" v-for="past in pastActivities" :key="past.id">
            <div class="past-image">
              <img :src="past.cover" :alt="past.title" />
            </div>
            <div class="past-content">
              <span class="past-date">{{ past.date }}</span>
              <h4>{{ past.title }}</h4>
              <p>{{ past.summary }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 报名对话框 -->
    <el-dialog v-model="showJoinDialog" title="活动报名" width="500px">
      <div class="join-form" v-if="selectedActivity">
        <div class="activity-summary">
          <img :src="selectedActivity.cover" :alt="selectedActivity.title" />
          <div class="summary-info">
            <h4>{{ selectedActivity.title }}</h4>
            <p>{{ selectedActivity.date }}</p>
          </div>
        </div>

        <el-form :model="joinForm" label-position="top">
          <el-form-item label="姓名">
            <el-input v-model="joinForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="joinForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="备注（选填）">
            <el-input v-model="joinForm.note" type="textarea" :rows="2" placeholder="有什么想说的？" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showJoinDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmJoin">确认报名</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as VueRouter from 'vue-router'
const useRouter = VueRouter.useRouter
import { ElMessage } from 'element-plus'
import {
  Star, Calendar, Location, User, Present
} from '@element-plus/icons-vue'

const router = useRouter()
const activeType = ref('all')
const showJoinDialog = ref(false)
const selectedActivity = ref<any>(null)
const countdown = reactive({ days: '00', hours: '00', minutes: '00', seconds: '00' })

const typeTabs = [
  { id: 'all', name: '全部', icon: 'Grid' },
  { id: 'competition', name: '竞赛', icon: 'Trophy' },
  { id: 'seminar', name: '讲座', icon: 'Microphone' },
  { id: 'volunteer', name: '志愿活动', icon: 'Flag' },
  { id: 'workshop', name: '工作坊', icon: 'Tools' }
]

const featuredActivity = ref({
  id: 1,
  title: '2026年度反诈知识大赛',
  desc: '年度盛典，丰厚奖品等你来拿！前50名可获得精美礼品，前10名可获得荣誉证书和奖金',
  cover: 'https://picsum.photos/seed/featured/800/400',
  date: '2026-02-01 至 2026-02-28',
  location: '线上+线下',
  participants: 1234,
  endTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)
})

const activities = ref([
  {
    id: 1,
    title: '反诈知识线上挑战赛',
    desc: '每天答题，积分兑换好礼',
    cover: 'https://picsum.photos/seed/act1/400/250',
    date: '长期活动',
    location: '线上',
    participants: 2345,
    status: 'ongoing',
    statusName: '进行中',
    reward: '积分+勋章',
    avatarList: [
      'https://picsum.photos/seed/u1/50/50',
      'https://picsum.photos/seed/u2/50/50',
      'https://picsum.photos/seed/u3/50/50'
    ]
  },
  {
    id: 2,
    title: '校园反诈宣传活动',
    desc: '志愿者招募中',
    cover: 'https://picsum.photos/seed/act2/400/250',
    date: '2026-02-15',
    location: '大学生活动中心',
    participants: 156,
    status: 'upcoming',
    statusName: '即将开始',
    reward: '志愿时长+证书',
    avatarList: [
      'https://picsum.photos/seed/u4/50/50',
      'https://picsum.photos/seed/u5/50/50'
    ]
  },
  {
    id: 3,
    title: '反诈专家讲座',
    desc: '邀请反诈中心专家现场分享',
    cover: 'https://picsum.photos/seed/act3/400/250',
    date: '2026-02-20',
    location: '图书馆报告厅',
    participants: 300,
    status: 'upcoming',
    statusName: '即将开始',
    reward: '精美礼品',
    avatarList: [
      'https://picsum.photos/seed/u6/50/50'
    ]
  },
  {
    id: 4,
    title: '防骗情景剧大赛',
    desc: '以情景剧形式展示防骗技巧',
    cover: 'https://picsum.photos/seed/act4/400/250',
    date: '2026-01-15',
    location: '线上',
    participants: 89,
    status: 'ended',
    statusName: '已结束',
    reward: null,
    avatarList: []
  }
])

const pastActivities = ref([
  {
    id: 1,
    title: '2023年度反诈知识竞赛',
    date: '2023-12',
    cover: 'https://picsum.photos/seed/past1/300/200',
    summary: '吸引了全校3000+学生参与，评选出100名反诈达人'
  },
  {
    id: 2,
    title: '反诈宣传月活动',
    date: '2023-11',
    cover: 'https://picsum.photos/seed/past2/300/200',
    summary: '开展30余场次活动，覆盖全校所有院系'
  },
  {
    id: 3,
    title: '新生反诈入学教育',
    date: '2023-09',
    cover: 'https://picsum.photos/seed/past3/300/200',
    summary: '为5000+新生提供反诈知识培训'
  }
])

const joinForm = reactive({
  name: '',
  phone: '',
  note: ''
})

let countdownTimer: number | null = null

const updateCountdown = () => {
  const end = featuredActivity.value.endTime
  const now = new Date()
  const diff = end.getTime() - now.getTime()

  if (diff <= 0) {
    countdown.days = '00'
    countdown.hours = '00'
    countdown.minutes = '00'
    countdown.seconds = '00'
    return
  }

  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)

  countdown.days = String(days).padStart(2, '0')
  countdown.hours = String(hours).padStart(2, '0')
  countdown.minutes = String(minutes).padStart(2, '0')
  countdown.seconds = String(seconds).padStart(2, '0')
}

onMounted(() => {
  updateCountdown()
  countdownTimer = window.setInterval(updateCountdown, 1000)
})

onUnmounted(() => {
  if (countdownTimer) clearInterval(countdownTimer)
})

const goToDetail = (activity: any) => {
  router.push(`/activity/detail/${activity.id}`)
}

const joinActivity = (activity: any) => {
  selectedActivity.value = activity
  showJoinDialog.value = true
}

const confirmJoin = () => {
  if (!joinForm.name || !joinForm.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }

  ElMessage.success('报名成功！')
  showJoinDialog.value = false
}
</script>

<style scoped>
.activity-page {
  min-height: 100vh;
  background: var(--bg-secondary);
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
  background: linear-gradient(135deg, #059669 0%, #10b981 50%, #34d399 100%);
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
}

.header-content p {
  font-size: var(--font-size-lg);
  opacity: 0.9;
}

.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6) var(--spacing-12);
}

/* 筛选标签 */
.filter-tabs {
  display: flex;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-8);
  flex-wrap: wrap;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-4);
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.tab-btn:hover {
  background: var(--bg-hover);
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.tab-btn.active {
  background: var(--gradient-primary);
  border-color: var(--primary-color);
  color: white;
  box-shadow: var(--shadow-primary);
}

/* 热门活动 */
.featured-section {
  margin-bottom: var(--spacing-10);
}

.featured-card {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  background: var(--bg-primary);
  border-radius: var(--radius-2xl);
  overflow: hidden;
  box-shadow: var(--shadow-xl);
  transition: all var(--transition-normal);
}

.featured-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-2xl);
}

.featured-image {
  position: relative;
  height: 350px;
  overflow: hidden;
}

.featured-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.featured-card:hover .featured-image img {
  transform: scale(1.05);
}

.featured-badge {
  position: absolute;
  top: var(--spacing-4);
  left: var(--spacing-4);
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  padding: var(--spacing-2) var(--spacing-3);
  background: var(--gradient-warning);
  color: white;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  border-radius: var(--radius-full);
}

.featured-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: var(--spacing-6);
  background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, transparent 100%);
}

.countdown {
  text-align: center;
  color: white;
}

.countdown-label {
  font-size: var(--font-size-sm);
  opacity: 0.8;
  display: block;
  margin-bottom: var(--spacing-2);
}

.countdown-time {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-2);
}

.time-block {
  padding: var(--spacing-2) var(--spacing-3);
  background: rgba(255,255,255,0.2);
  border-radius: var(--radius-md);
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  min-width: 50px;
}

.time-separator {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
}

.featured-content {
  padding: var(--spacing-8);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.featured-content h2 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-3);
}

.featured-content p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: var(--spacing-5);
}

.featured-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-6);
}

.featured-meta span {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

/* 活动列表 */
.activities-section {
  margin-bottom: var(--spacing-10);
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
}

.activity-count {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-6);
}

.activity-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.activity-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.activity-card:hover .card-image img {
  transform: scale(1.1);
}

.card-status {
  position: absolute;
  top: var(--spacing-3);
  right: var(--spacing-3);
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: white;
}

.card-status.status-ongoing { background: var(--success-color); }
.card-status.status-upcoming { background: var(--warning-color); }
.card-status.status-ended { background: var(--text-muted); }

.card-reward {
  position: absolute;
  bottom: var(--spacing-3);
  left: var(--spacing-3);
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  padding: var(--spacing-1) var(--spacing-2);
  background: rgba(0,0,0,0.7);
  color: white;
  font-size: var(--font-size-xs);
  border-radius: var(--radius-sm);
}

.card-content {
  padding: var(--spacing-4);
}

.card-content h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.card-content p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-3);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-info {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-3);
}

.card-info span {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-3);
  border-top: 1px solid var(--border-secondary);
}

.participants {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.avatar-stack {
  display: flex;
}

.avatar-stack :deep(.el-avatar) {
  margin-left: -8px;
  border: 2px solid white;
}

.avatar-stack :deep(.el-avatar:first-child) {
  margin-left: 0;
}

/* 往期活动 */
.past-section {
  margin-bottom: var(--spacing-8);
}

.past-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-4);
}

.past-card {
  display: flex;
  gap: var(--spacing-3);
  padding: var(--spacing-3);
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  transition: all var(--transition-fast);
}

.past-card:hover {
  background: var(--bg-hover);
}

.past-image {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
}

.past-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.past-content {
  flex: 1;
}

.past-date {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.past-content h4 {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.past-content p {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 报名表单 */
.join-form {
  padding: var(--spacing-2);
}

.activity-summary {
  display: flex;
  gap: var(--spacing-4);
  padding: var(--spacing-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  margin-bottom: var(--spacing-5);
}

.activity-summary img {
  width: 100px;
  height: 60px;
  border-radius: var(--radius-md);
  object-fit: cover;
}

.summary-info h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.summary-info p {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

/* 响应式 */
@media (max-width: 1024px) {
  .featured-card {
    grid-template-columns: 1fr;
  }

  .featured-image {
    height: 250px;
  }

  .activities-grid {
    grid-template-columns: 1fr;
  }

  .past-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: var(--spacing-10) var(--spacing-4);
  }

  .header-content h1 {
    font-size: var(--font-size-2xl);
  }

  .page-container {
    padding: 0 var(--spacing-4) var(--spacing-8);
  }

  .filter-tabs {
    overflow-x: auto;
    flex-wrap: nowrap;
    padding-bottom: var(--spacing-2);
  }
}
</style>
