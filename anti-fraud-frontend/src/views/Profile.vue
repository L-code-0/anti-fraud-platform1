<template>
  <div class="profile-page">
    <!-- 页面头部 -->
    <div class="profile-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
        <div class="bg-pattern"></div>
        <div class="bg-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
        </div>
      </div>
      <div class="header-content">
        <div class="user-info-section" :class="{ 'animate-in': isVisible }">
          <div class="avatar-wrapper">
            <el-avatar :size="100" src="https://picsum.photos/seed/avatar/200/200" />
            <div class="avatar-level">Lv.{{ userInfo.level }}</div>
            <div class="avatar-glow"></div>
          </div>
          <div class="user-details">
            <h1>{{ userInfo.name }}</h1>
            <p class="user-school">{{ userInfo.school }} · {{ userInfo.department }}</p>
            <div class="user-tags">
              <span class="tag" :class="{ 'animated': isVisible }">
                <el-icon><Star /></el-icon>
                {{ userInfo.title }}
              </span>
              <span class="tag" :class="{ 'animated': isVisible }">
                <el-icon><Timer /></el-icon>
                {{ userInfo.streak }}天连续学习
              </span>
            </div>
          </div>
          <el-button type="primary" @click="showEditDialog = true" class="edit-btn">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>
      </div>
    </div>

    <div class="page-container">
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card" v-for="(stat, index) in statsData" :key="stat.label" :style="{ animationDelay: `${index * 0.1}s` }">
          <div class="stat-icon" :style="{ background: stat.gradient }">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
          <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'" v-if="stat.trend !== undefined">
            <el-icon><component :is="stat.trend > 0 ? 'Top' : 'Bottom'" /></el-icon>
            {{ Math.abs(stat.trend) }}%
          </div>
        </div>
      </div>

      <div class="content-grid">
        <!-- 左侧 -->
        <div class="left-column">
          <!-- 学习进度 -->
          <div class="card animate-card">
            <div class="card-header">
              <h3>学习进度</h3>
              <el-button text @click="router.push('/knowledge')" class="card-action">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="card-body">
              <div class="progress-list">
                <div class="progress-item" v-for="(item, index) in learningProgress" :key="item.id" :style="{ animationDelay: `${index * 0.15}s` }">
                  <div class="progress-header">
                    <span class="progress-title">{{ item.title }}</span>
                    <span class="progress-percent">{{ item.percent }}%</span>
                  </div>
                  <el-progress
                    :percentage="item.percent"
                    :stroke-width="8"
                    :show-text="false"
                    :color="item.color"
                    class="custom-progress"
                  />
                  <div class="progress-meta">
                    <span>{{ item.completed }}/{{ item.total }}章节</span>
                    <span>{{ item.hours }}小时</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 最近学习 -->
          <div class="card animate-card">
            <div class="card-header">
              <h3>最近学习</h3>
            </div>
            <div class="card-body">
              <div class="recent-list">
                <div class="recent-item" v-for="(item, index) in recentLearning" :key="item.id" :style="{ animationDelay: `${index * 0.15}s` }">
                  <div class="recent-icon" :style="{ background: item.color }">
                    <el-icon><component :is="item.icon" /></el-icon>
                  </div>
                  <div class="recent-content">
                    <h4>{{ item.title }}</h4>
                    <p>{{ item.time }}</p>
                  </div>
                  <el-button type="primary" text size="small" class="continue-btn">
                    继续学习
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧 -->
        <div class="right-column">
          <!-- 积分详情 -->
          <div class="card points-card animate-card">
            <div class="card-header">
              <h3>我的积分</h3>
              <el-button text @click="router.push('/points')" class="card-action">
                积分商城
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="card-body">
              <div class="points-main">
                <span class="points-value">{{ userInfo.points }}</span>
                <span class="points-unit">积分</span>
              </div>
              <div class="points-rule">
                <el-button type="primary" size="small" class="point-btn">
                  <el-icon><Plus /></el-icon>
                  赚取积分
                </el-button>
                <el-button size="small" class="point-btn">
                  <el-icon><ShoppingCart /></el-icon>
                  积分商城
                </el-button>
              </div>
              <div class="points-history">
                <h4>积分记录</h4>
                <div class="history-list">
                  <div class="history-item" v-for="(item, index) in pointsHistory" :key="item.id" :style="{ animationDelay: `${index * 0.1}s` }">
                    <div class="history-content">
                      <span class="history-title">{{ item.title }}</span>
                      <span class="history-time">{{ item.time }}</span>
                    </div>
                    <span class="history-change" :class="item.type">
                      {{ item.type === 'add' ? '+' : '-' }}{{ item.change }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 成就徽章 -->
          <div class="card animate-card">
            <div class="card-header">
              <h3>成就徽章</h3>
              <el-button text @click="router.push('/achievement')" class="card-action">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="card-body">
              <div class="badges-grid">
                <div
                  class="badge-item"
                  :class="{ locked: !badge.unlocked }"
                  v-for="(badge, index) in badges"
                  :key="badge.id"
                  :style="{ animationDelay: `${index * 0.1}s` }"
                >
                  <div class="badge-icon" :class="{ 'unlocked': badge.unlocked }">
                    <el-icon><component :is="badge.icon" /></el-icon>
                  </div>
                  <span>{{ badge.name }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 快速入口 -->
          <div class="card animate-card">
            <div class="card-header">
              <h3>快捷功能</h3>
            </div>
            <div class="card-body">
              <div class="quick-actions">
                <div class="quick-item" @click="router.push('/test')" v-for="(action, index) in quickActions" :key="action.name" :style="{ animationDelay: `${index * 0.1}s` }">
                  <div class="quick-icon" :style="{ background: action.gradient }">
                    <el-icon><component :is="action.icon" /></el-icon>
                  </div>
                  <span>{{ action.name }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑资料" width="500px" class="edit-dialog">
      <el-form :model="editForm" label-position="top" class="edit-form">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
          >
            <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.name" placeholder="请输入昵称" class="form-input" />
        </el-form-item>
        <el-form-item label="学校">
          <el-input v-model="editForm.school" placeholder="请输入学校" class="form-input" />
        </el-form-item>
        <el-form-item label="院系">
          <el-input v-model="editForm.department" placeholder="请输入院系" class="form-input" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input
            v-model="editForm.bio"
            type="textarea"
            :rows="3"
            placeholder="介绍一下自己吧..."
            class="form-textarea"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false" class="dialog-btn">取消</el-button>
        <el-button type="primary" @click="saveProfile" class="dialog-btn">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Star, Timer, Edit, ArrowRight, Plus, ShoppingCart, EditPen,
  Coordinate, WarnTriangleFilled, Document, Top, Bottom, Trophy,
  Message, Lock, Medal, Clock, Reading
} from '@element-plus/icons-vue'

const router = useRouter()
const showEditDialog = ref(false)
const isVisible = ref(false)

const userInfo = reactive({
  name: '张三',
  school: 'XX大学',
  department: '计算机学院',
  level: 8,
  title: '反诈达人',
  streak: 15,
  points: 2350,
  avatar: ''
})

const editForm = reactive({
  name: '',
  school: '',
  department: '',
  bio: '',
  avatar: ''
})

const statsData = [
  { icon: 'Trophy', value: '2350', label: '总积分', gradient: 'var(--gradient-primary)', trend: 12 },
  { icon: 'Reading', value: '48', label: '学习文章', gradient: 'var(--gradient-success)', trend: 8 },
  { icon: 'EditPen', value: '23', label: '完成测试', gradient: 'var(--gradient-warning)', trend: -5 },
  { icon: 'Clock', value: '36.5', label: '学习时长(h)', gradient: 'var(--gradient-info)', trend: 15 }
]

const learningProgress = [
  { id: 1, title: '电信诈骗防范', percent: 85, completed: 17, total: 20, hours: 12, color: '#3b82f6' },
  { id: 2, title: '网络购物安全', percent: 60, completed: 12, total: 20, hours: 8, color: '#10b981' },
  { id: 3, title: '金融诈骗识别', percent: 35, completed: 7, total: 20, hours: 5, color: '#f59e0b' }
]

const recentLearning = [
  { id: 1, title: '揭秘冒充公检法诈骗', time: '2小时前', icon: 'Reading', color: 'var(--primary-color)' },
  { id: 2, title: '刷单返利诈骗防范', time: '1天前', icon: 'Document', color: 'var(--success-color)' },
  { id: 3, title: '杀猪盘诈骗解析', time: '2天前', icon: 'Message', color: 'var(--warning-color)' }
]

const pointsHistory = [
  { id: 1, title: '完成测试获得', time: '1小时前', change: 50, type: 'add' },
  { id: 2, title: '每日签到', time: '今天', change: 10, type: 'add' },
  { id: 3, title: '兑换徽章', time: '昨天', change: 100, type: 'sub' }
]

const badges = [
  { id: 1, name: '初学者', icon: 'Star', unlocked: true },
  { id: 2, name: '学习达人', icon: 'Reading', unlocked: true },
  { id: 3, name: '测试高手', icon: 'EditPen', unlocked: true },
  { id: 4, name: '反诈专家', icon: 'Trophy', unlocked: true },
  { id: 5, name: '防骗达人', icon: 'Lock', unlocked: false },
  { id: 6, name: '金牌会员', icon: 'Medal', unlocked: false }
]

const quickActions = [
  { name: '在线测试', icon: 'EditPen', gradient: 'var(--gradient-primary)', route: '/test' },
  { name: '情景演练', icon: 'Coordinate', gradient: 'var(--gradient-success)', route: '/simulation' },
  { name: '举报线索', icon: 'WarnTriangleFilled', gradient: 'var(--gradient-danger)', route: '/report' },
  { name: '错题本', icon: 'Document', gradient: 'var(--gradient-warning)', route: '/wrongbook' }
]

const saveProfile = () => {
  userInfo.name = editForm.name || userInfo.name
  userInfo.school = editForm.school || userInfo.school
  userInfo.department = editForm.department || userInfo.department
  showEditDialog.value = false
}

onMounted(() => {
  // 页面加载时触发动画
  setTimeout(() => {
    isVisible.value = true
  }, 100)
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

/* 头部 */
.profile-header {
  position: relative;
  padding: var(--spacing-12) var(--spacing-6);
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
  background: linear-gradient(135deg, #1e3a5f 0%, #2563eb 50%, #3b82f6 100%);
}

.header-bg .bg-pattern {
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.header-bg .bg-shapes {
  position: absolute;
  inset: 0;
  overflow: hidden;
  z-index: 0;
}

.header-bg .shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
  animation: floatShape 25s ease-in-out infinite;
}

.header-bg .shape-1 {
  width: 400px;
  height: 400px;
  background: var(--primary-light);
  top: -100px;
  left: -100px;
}

.header-bg .shape-2 {
  width: 300px;
  height: 300px;
  background: var(--success-color);
  bottom: -50px;
  right: -50px;
  animation-delay: -8s;
}

.header-content {
  position: relative;
  z-index: 1;
  max-width: 1280px;
  margin: 0 auto;
}

.user-info-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-6);
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease;
}

.user-info-section.animate-in {
  opacity: 1;
  transform: translateY(0);
}

.avatar-wrapper {
  position: relative;
}

.avatar-wrapper :deep(.el-avatar) {
  border: 4px solid white;
  box-shadow: var(--shadow-lg);
  transition: all 0.3s ease;
}

.avatar-wrapper:hover :deep(.el-avatar) {
  transform: scale(1.05);
  box-shadow: var(--shadow-xl);
}

.avatar-glow {
  position: absolute;
  inset: -8px;
  background: var(--gradient-primary);
  border-radius: 50%;
  filter: blur(20px);
  opacity: 0.5;
  animation: pulseGlow 2s ease-in-out infinite;
}

.avatar-level {
  position: absolute;
  bottom: 0;
  right: 0;
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--gradient-warning);
  color: white;
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  border-radius: var(--radius-full);
  box-shadow: var(--shadow-md);
}

.user-details {
  flex: 1;
  color: white;
}

.user-details h1 {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-2);
  background: linear-gradient(135deg, #fff 0%, rgba(255, 255, 255, 0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.user-school {
  font-size: var(--font-size-base);
  opacity: 0.9;
  margin-bottom: var(--spacing-3);
}

.user-tags {
  display: flex;
  gap: var(--spacing-3);
  flex-wrap: wrap;
}

.user-tags .tag {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-1);
  padding: var(--spacing-1) var(--spacing-3);
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateY(10px);
}

.user-tags .tag.animated {
  opacity: 1;
  transform: translateY(0);
  animation: fadeInUp 0.5s ease forwards;
}

.user-tags .tag:nth-child(1) {
  animation-delay: 0.2s;
}

.user-tags .tag:nth-child(2) {
  animation-delay: 0.3s;
}

.user-tags .tag:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.edit-btn {
  background: var(--gradient-primary);
  border: none;
  border-radius: var(--radius-lg);
  padding: var(--spacing-3) var(--spacing-6);
  font-weight: var(--font-weight-medium);
  transition: all 0.3s ease;
  box-shadow: var(--shadow-primary);
}

.edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

/* 容器 */
.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6) var(--spacing-12);
  margin-top: calc(-1 * var(--spacing-8));
  position: relative;
  z-index: 10;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-6);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-5);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  transition: all var(--transition-normal);
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.6s ease forwards;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
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
  box-shadow: var(--shadow-md);
}

.stat-content {
  flex: 1;
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

.stat-trend {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.stat-trend.up {
  background: var(--success-bg);
  color: var(--success-color);
}

.stat-trend.down {
  background: var(--danger-bg);
  color: var(--danger-color);
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: var(--spacing-6);
}

.left-column,
.right-column {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6);
}

/* 卡片 */
.card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  overflow: hidden;
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.6s ease forwards;
}

.card.animate-card {
  animation-delay: 0.2s;
}

.card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-4) var(--spacing-5);
  border-bottom: 1px solid var(--border-primary);
  background: var(--bg-secondary);
}

.card-header h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.card-action {
  color: var(--primary-color);
  font-size: var(--font-size-sm);
  transition: all 0.3s ease;
}

.card-action:hover {
  color: var(--primary-dark);
  transform: translateX(2px);
}

.card-body {
  padding: var(--spacing-5);
}

/* 学习进度 */
.progress-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-5);
}

.progress-item {
  padding: var(--spacing-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateX(-20px);
  animation: fadeInLeft 0.5s ease forwards;
}

.progress-item:hover {
  background: var(--bg-hover);
  transform: translateX(-5px);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--spacing-2);
}

.progress-title {
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

.progress-percent {
  font-weight: var(--font-weight-semibold);
  color: var(--primary-color);
}

.custom-progress {
  margin: var(--spacing-2) 0;
}

.progress-meta {
  display: flex;
  justify-content: space-between;
  margin-top: var(--spacing-2);
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

/* 最近学习 */
.recent-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
}

.recent-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  padding: var(--spacing-3);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  transition: all var(--transition-fast);
  opacity: 0;
  transform: translateX(-20px);
  animation: fadeInLeft 0.5s ease forwards;
}

.recent-item:hover {
  background: var(--bg-hover);
  transform: translateX(-5px);
}

.recent-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  box-shadow: var(--shadow-sm);
}

.recent-content {
  flex: 1;
}

.recent-content h4 {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.recent-content p {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.continue-btn {
  color: var(--primary-color);
  font-size: var(--font-size-xs);
  transition: all 0.3s ease;
}

.continue-btn:hover {
  color: var(--primary-dark);
  transform: translateX(2px);
}

/* 积分卡片 */
.points-card {
  background: linear-gradient(180deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
}

.points-main {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-4);
  padding: var(--spacing-4);
  background: rgba(66, 153, 225, 0.1);
  border-radius: var(--radius-lg);
  border: 1px solid var(--primary-light);
}

.points-value {
  font-size: var(--font-size-5xl);
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-light) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: pulse 2s ease-in-out infinite;
}

.points-unit {
  font-size: var(--font-size-lg);
  color: var(--text-muted);
}

.points-rule {
  display: flex;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-5);
}

.point-btn {
  flex: 1;
  border-radius: var(--radius-md);
  transition: all 0.3s ease;
}

.point-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.points-history h4 {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-3);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-2) 0;
  border-bottom: 1px solid var(--border-secondary);
  opacity: 0;
  transform: translateY(10px);
  animation: fadeInUp 0.4s ease forwards;
}

.history-item:last-child {
  border-bottom: none;
}

.history-content {
  display: flex;
  flex-direction: column;
}

.history-title {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
}

.history-time {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.history-change {
  font-weight: var(--font-weight-semibold);
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-full);
}

.history-change.add {
  color: var(--success-color);
  background: var(--success-bg);
}

.history-change.sub {
  color: var(--danger-color);
  background: var(--danger-bg);
}

/* 徽章 */
.badges-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-3);
}

.badge-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-3);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  transition: all var(--transition-fast);
  opacity: 0;
  transform: scale(0.9);
  animation: fadeInScale 0.4s ease forwards;
}

.badge-item.locked {
  opacity: 0.5;
}

.badge-item:not(.locked):hover {
  background: var(--primary-bg);
  transform: translateY(-2px) scale(1.05);
  box-shadow: var(--shadow-md);
}

.badge-icon {
  width: 48px;
  height: 48px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
}

.badge-icon.unlocked {
  animation: badgeGlow 2s ease-in-out infinite;
}

.badge-item.locked .badge-icon {
  background: var(--bg-tertiary);
  color: var(--text-muted);
}

.badge-item span {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  text-align: center;
}

/* 快捷功能 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-3);
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  opacity: 0;
  transform: scale(0.9);
  animation: fadeInScale 0.4s ease forwards;
}

.quick-item:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: var(--shadow-md);
  background: var(--bg-hover);
}

.quick-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  box-shadow: var(--shadow-sm);
}

.quick-item span {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
  text-align: center;
}

/* 编辑资料 */
.avatar-uploader {
  display: flex;
  justify-content: center;
  margin-bottom: var(--spacing-4);
}

.avatar-uploader :deep(.el-upload) {
  border: 2px dashed var(--border-primary);
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 120px;
  height: 120px;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--primary-color);
  transform: scale(1.05);
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
}

.edit-dialog {
  border-radius: var(--radius-xl);
  overflow: hidden;
}

.edit-form :deep(.el-form-item) {
  margin-bottom: var(--spacing-4);
}

.form-input,
.form-textarea {
  border-radius: var(--radius-md);
  border: 1px solid var(--border-primary);
  transition: all 0.3s ease;
}

.form-input:focus,
.form-textarea:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px var(--primary-bg);
}

.dialog-btn {
  padding: var(--spacing-2) var(--spacing-4);
  border-radius: var(--radius-md);
  transition: all 0.3s ease;
}

.dialog-btn:hover {
  transform: translateY(-1px);
}

/* 动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes pulseGlow {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.1);
  }
}

@keyframes badgeGlow {
  0%, 100% {
    box-shadow: 0 0 10px rgba(66, 153, 225, 0.5);
  }
  50% {
    box-shadow: 0 0 20px rgba(66, 153, 225, 0.8);
  }
}

@keyframes floatShape {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(30px, -30px) scale(1.1);
  }
}

/* 响应式 */
@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .profile-header {
    padding: var(--spacing-8) var(--spacing-4);
  }

  .user-info-section {
    flex-direction: column;
    text-align: center;
  }

  .user-tags {
    justify-content: center;
    flex-wrap: wrap;
  }

  .page-container {
    padding: 0 var(--spacing-4) var(--spacing-8);
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .badges-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
