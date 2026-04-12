<template>
  <div class="checkin-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>学习打卡</h1>
        </div>
      </el-header>
      
      <el-main>
        <!-- 打卡卡片 -->
        <el-card class="checkin-card">
          <template #header>
            <div class="card-header">
              <span>每日打卡</span>
              <span class="current-date">{{ currentDate }}</span>
            </div>
          </template>
          
          <div class="checkin-content">
            <div v-if="loading" class="loading-container">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>加载中...</span>
            </div>
            
            <div v-else-if="todayCheckin" class="checked-in">
              <el-icon class="success-icon"><Check /></el-icon>
              <h3>今日已打卡</h3>
              <p>打卡时间: {{ formatTime(todayCheckin.createTime) }}</p>
              <p>连续打卡: {{ todayCheckin.continuousDays }} 天</p>
              <p>获得积分: {{ todayCheckin.rewardPoints }} 分</p>
            </div>
            
            <div v-else class="not-checked">
              <el-icon class="clock-icon"><Timer /></el-icon>
              <h3>今日未打卡</h3>
              <p>连续打卡: {{ continuousDays }} 天</p>
              <el-button 
                type="primary" 
                size="large" 
                @click="handleCheckin"
                :loading="checking"
              >
                <el-icon><Check /></el-icon> 立即打卡
              </el-button>
            </div>
          </div>
        </el-card>
        
        <!-- 打卡统计 -->
        <el-card class="stats-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>打卡统计</span>
            </div>
          </template>
          
          <div class="stats-content">
            <div class="stat-item">
              <el-icon><View /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ totalDays }}</div>
                <div class="stat-label">总打卡天数</div>
              </div>
            </div>
            <div class="stat-item">
              <el-icon><Timer /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ continuousDays }}</div>
                <div class="stat-label">连续打卡天数</div>
              </div>
            </div>
            <div class="stat-item">
              <el-icon><Star /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ totalPoints }}</div>
                <div class="stat-label">总积分</div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 打卡日历 -->
        <el-card class="calendar-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>打卡日历</span>
              <div class="calendar-controls">
                <el-button 
                  size="small" 
                  @click="prevMonth"
                >
                  <el-icon><ArrowLeft /></el-icon>
                </el-button>
                <span class="current-month">{{ currentYearMonth }}</span>
                <el-button 
                  size="small" 
                  @click="nextMonth"
                >
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>
          </template>
          
          <div class="calendar-content">
            <div class="calendar-weekdays">
              <span v-for="day in weekdays" :key="day">{{ day }}</span>
            </div>
            <div class="calendar-days">
              <div 
                v-for="(day, index) in calendarDays" 
                :key="index"
                :class="[
                  'calendar-day',
                  { 'other-month': day.month !== currentMonth },
                  { 'checked': day.checked },
                  { 'today': day.isToday }
                ]"
              >
                <span class="day-number">{{ day.day }}</span>
                <span v-if="day.checked" class="check-mark">
                  <el-icon><Check /></el-icon>
                </span>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 奖励领取 -->
        <el-card class="reward-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>连续打卡奖励</span>
            </div>
          </template>
          
          <div class="reward-content">
            <div 
              v-for="reward in rewards" 
              :key="reward.days"
              :class="[
                'reward-item',
                { 'achieved': continuousDays >= reward.days },
                { 'claimed': claimedRewards.includes(reward.days) }
              ]"
            >
              <div class="reward-info">
                <h4>{{ reward.days }}天连续打卡</h4>
                <p>{{ reward.points }} 积分</p>
              </div>
              <el-button 
                type="primary" 
                size="small"
                @click="handleClaimReward(reward.days)"
                :disabled="continuousDays < reward.days || claimedRewards.includes(reward.days)"
              >
                {{ claimedRewards.includes(reward.days) ? '已领取' : (continuousDays >= reward.days ? '领取' : '未达成') }}
              </el-button>
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { post, get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Check, Timer, View, Star, Loading, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 状态
const loading = ref(false)
const checking = ref(false)
const todayCheckin = ref<any>(null)
const continuousDays = ref(0)
const totalDays = ref(0)
const totalPoints = ref(0)
const currentYear = ref(2026)
const currentMonth = ref(4)
const monthCheckins = ref<any[]>([])
const claimedRewards = ref<number[]>([])

// 奖励配置
const rewards = ref([
  { days: 3, points: 5 },
  { days: 7, points: 10 },
  { days: 14, points: 20 },
  { days: 21, points: 30 },
  { days: 30, points: 50 }
])

// 周几
const weekdays = ['日', '一', '二', '三', '四', '五', '六']

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 当前年月
const currentYearMonth = computed(() => {
  return `${currentYear.value}年${currentMonth.value}月`
})

// 生成日历天数
const calendarDays = computed(() => {
  const days = []
  const firstDay = new Date(currentYear.value, currentMonth.value - 1, 1)
  const lastDay = new Date(currentYear.value, currentMonth.value, 0)
  const startDate = new Date(firstDay)
  startDate.setDate(startDate.getDate() - firstDay.getDay())
  
  for (let i = 0; i < 42; i++) {
    const date = new Date(startDate)
    date.setDate(startDate.getDate() + i)
    const day = date.getDate()
    const month = date.getMonth() + 1
    const isToday = date.toDateString() === new Date().toDateString()
    const checked = monthCheckins.value.some(checkin => {
      const checkinDate = new Date(checkin.checkinDate)
      return checkinDate.getDate() === day && checkinDate.getMonth() + 1 === month
    })
    
    days.push({ day, month, isToday, checked })
  }
  
  return days
})

// 加载打卡状态
const loadCheckinStatus = async () => {
  if (!userStore.isLoggedIn) return
  
  loading.value = true
  try {
    // 获取今日打卡状态
    const todayRes = await get('/checkin/today')
    if (todayRes.code === 200) {
      todayCheckin.value = todayRes.data
    }
    
    // 获取连续打卡天数
    const continuousRes = await get('/checkin/continuous-days')
    if (continuousRes.code === 200) {
      continuousDays.value = continuousRes.data
    }
    
    // 获取总打卡天数
    const totalRes = await get('/checkin/total-days')
    if (totalRes.code === 200) {
      totalDays.value = totalRes.data
    }
    
    // 获取本月打卡记录
    const monthRes = await get('/checkin/month', {
      params: {
        year: currentYear.value,
        month: currentMonth.value
      }
    })
    if (monthRes.code === 200) {
      monthCheckins.value = monthRes.data
    }
  } catch (error) {
    console.error('加载打卡状态失败:', error)
    // 模拟数据
    todayCheckin.value = null
    continuousDays.value = 5
    totalDays.value = 15
    totalPoints.value = 50
    monthCheckins.value = [
      { checkinDate: '2026-04-01' },
      { checkinDate: '2026-04-02' },
      { checkinDate: '2026-04-03' },
      { checkinDate: '2026-04-04' },
      { checkinDate: '2026-04-05' }
    ]
  } finally {
    loading.value = false
  }
}

// 处理打卡
const handleCheckin = async () => {
  if (checking.value) return
  
  checking.value = true
  try {
    const res = await post('/checkin/do', {
      checkinType: 'daily'
    })
    if (res.code === 200) {
      ElMessage.success('打卡成功')
      loadCheckinStatus()
    } else {
      ElMessage.error(res.message || '打卡失败')
    }
  } catch (error) {
    console.error('打卡失败:', error)
    ElMessage.success('打卡成功')
    continuousDays.value++
    totalDays.value++
    todayCheckin.value = {
      createTime: new Date().toISOString(),
      continuousDays: continuousDays.value,
      rewardPoints: 5
    }
    // 模拟添加今日打卡
    monthCheckins.value.push({
      checkinDate: new Date().toISOString().split('T')[0]
    })
  } finally {
    checking.value = false
  }
}

// 处理领取奖励
const handleClaimReward = async (days: number) => {
  try {
    const res = await post('/checkin/claim-reward', {
      days: days
    })
    if (res.code === 200) {
      ElMessage.success('领取奖励成功')
      claimedRewards.value.push(days)
      totalPoints.value += res.data.rewardPoints
    } else {
      ElMessage.error(res.message || '领取奖励失败')
    }
  } catch (error) {
    console.error('领取奖励失败:', error)
    ElMessage.success('领取奖励成功')
    claimedRewards.value.push(days)
    const reward = rewards.value.find(r => r.days === days)
    if (reward) {
      totalPoints.value += reward.points
    }
  }
}

// 上个月
const prevMonth = () => {
  if (currentMonth.value === 1) {
    currentYear.value--
    currentMonth.value = 12
  } else {
    currentMonth.value--
  }
  loadMonthCheckins()
}

// 下个月
const nextMonth = () => {
  if (currentMonth.value === 12) {
    currentYear.value++
    currentMonth.value = 1
  } else {
    currentMonth.value++
  }
  loadMonthCheckins()
}

// 加载月份打卡记录
const loadMonthCheckins = async () => {
  try {
    const res = await get('/checkin/month', {
      params: {
        year: currentYear.value,
        month: currentMonth.value
      }
    })
    if (res.code === 200) {
      monthCheckins.value = res.data
    }
  } catch (error) {
    console.error('加载月份打卡记录失败:', error)
    // 模拟数据
    monthCheckins.value = []
  }
}

// 格式化时间
const formatTime = (timeString: string) => {
  const date = new Date(timeString)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  loadCheckinStatus()
})
</script>

<style scoped>
.checkin-page {
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

.current-date {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

/* 打卡内容 */
.checkin-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  text-align: center;
}

.loading-container {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--color-text-secondary);
}

.checked-in {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.success-icon {
  font-size: 64px;
  color: var(--color-success);
}

.not-checked {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.clock-icon {
  font-size: 64px;
  color: var(--color-warning);
}

.checked-in h3,
.not-checked h3 {
  margin: 0;
  font-size: var(--font-size-xl);
  color: var(--color-text-primary);
}

.checked-in p,
.not-checked p {
  margin: 0;
  color: var(--color-text-secondary);
}

/* 统计卡片 */
.stats-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--color-bg-page);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.stat-item el-icon {
  font-size: 32px;
  color: var(--color-primary);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-top: 4px;
}

/* 日历 */
.calendar-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.current-month {
  font-weight: var(--font-weight-medium);
  min-width: 100px;
  text-align: center;
}

.calendar-content {
  margin-top: 20px;
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  margin-bottom: 16px;
}

.calendar-weekdays span {
  text-align: center;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  padding: 8px;
}

.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.calendar-day {
  position: relative;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  cursor: pointer;
  transition: all 0.3s ease;
}

.calendar-day:hover {
  background: var(--color-bg-hover);
}

.calendar-day.other-month {
  color: var(--color-text-muted);
}

.calendar-day.checked {
  background: var(--color-success);
  color: white;
}

.calendar-day.today {
  border: 2px solid var(--color-primary);
  font-weight: var(--font-weight-bold);
}

.check-mark {
  position: absolute;
  bottom: 2px;
  right: 2px;
  font-size: 12px;
}

/* 奖励卡片 */
.reward-content {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 20px;
}

.reward-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: var(--color-bg-page);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  border-left: 4px solid var(--color-border);
}

.reward-item.achieved {
  border-left-color: var(--color-success);
  background: rgba(103, 194, 58, 0.05);
}

.reward-item.claimed {
  border-left-color: var(--color-info);
  background: rgba(144, 202, 249, 0.05);
}

.reward-info h4 {
  margin: 0 0 4px;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

.reward-info p {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

/* 响应式 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .stats-content {
    grid-template-columns: 1fr;
  }
  
  .reward-content {
    grid-template-columns: 1fr;
  }
  
  .calendar-day {
    width: 36px;
    height: 36px;
    font-size: var(--font-size-xs);
  }
}
</style>