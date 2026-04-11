<template>
  <div class="profile-page">
    <!-- 页面头部 -->
    <div class="profile-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
        <div class="bg-pattern"></div>
      </div>
      <div class="header-content">
        <div class="user-info-section">
          <div class="avatar-wrapper">
            <el-avatar :size="100" src="https://picsum.photos/seed/avatar/200/200" />
            <div class="avatar-level">Lv.{{ userInfo.level }}</div>
          </div>
          <div class="user-details">
            <h1>{{ userInfo.name }}</h1>
            <p class="user-school">{{ userInfo.school }} · {{ userInfo.department }}</p>
            <div class="user-tags">
              <span class="tag">
                <el-icon><Star /></el-icon>
                {{ userInfo.title }}
              </span>
              <span class="tag">
                <el-icon><Timer /></el-icon>
                {{ userInfo.streak }}天连续学习
              </span>
            </div>
          </div>
          <el-button type="primary" @click="showEditDialog = true">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>
      </div>
    </div>

    <div class="page-container">
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card" v-for="stat in statsData" :key="stat.label">
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
          <div class="card">
            <div class="card-header">
              <h3>学习进度</h3>
              <el-button text @click="router.push('/knowledge')">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="card-body">
              <div class="progress-list">
                <div class="progress-item" v-for="item in learningProgress" :key="item.id">
                  <div class="progress-header">
                    <span class="progress-title">{{ item.title }}</span>
                    <span class="progress-percent">{{ item.percent }}%</span>
                  </div>
                  <el-progress
                    :percentage="item.percent"
                    :stroke-width="8"
                    :show-text="false"
                    :color="item.color"
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
          <div class="card">
            <div class="card-header">
              <h3>最近学习</h3>
            </div>
            <div class="card-body">
              <div class="recent-list">
                <div class="recent-item" v-for="item in recentLearning" :key="item.id">
                  <div class="recent-icon" :style="{ background: item.color }">
                    <el-icon><component :is="item.icon" /></el-icon>
                  </div>
                  <div class="recent-content">
                    <h4>{{ item.title }}</h4>
                    <p>{{ item.time }}</p>
                  </div>
                  <el-button type="primary" text size="small">
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
          <div class="card points-card">
            <div class="card-header">
              <h3>我的积分</h3>
              <el-button text @click="router.push('/points')">
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
                <el-button type="primary" size="small">
                  <el-icon><Plus /></el-icon>
                  赚取积分
                </el-button>
                <el-button size="small">
                  <el-icon><ShoppingCart /></el-icon>
                  积分商城
                </el-button>
              </div>
              <div class="points-history">
                <h4>积分记录</h4>
                <div class="history-list">
                  <div class="history-item" v-for="item in pointsHistory" :key="item.id">
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
          <div class="card">
            <div class="card-header">
              <h3>成就徽章</h3>
              <el-button text @click="router.push('/achievement')">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="card-body">
              <div class="badges-grid">
                <div
                  class="badge-item"
                  :class="{ locked: !badge.unlocked }"
                  v-for="badge in badges"
                  :key="badge.id"
                >
                  <div class="badge-icon">
                    <el-icon><component :is="badge.icon" /></el-icon>
                  </div>
                  <span>{{ badge.name }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 快速入口 -->
          <div class="card">
            <div class="card-header">
              <h3>快捷功能</h3>
            </div>
            <div class="card-body">
              <div class="quick-actions">
                <div class="quick-item" @click="router.push('/test')">
                  <div class="quick-icon" style="background: var(--gradient-primary)">
                    <el-icon><EditPen /></el-icon>
                  </div>
                  <span>在线测试</span>
                </div>
                <div class="quick-item" @click="router.push('/simulation')">
                  <div class="quick-icon" style="background: var(--gradient-success)">
                    <el-icon><Coordinate /></el-icon>
                  </div>
                  <span>情景演练</span>
                </div>
                <div class="quick-item" @click="router.push('/report')">
                  <div class="quick-icon" style="background: var(--gradient-danger)">
                    <el-icon><WarnTriangleFilled /></el-icon>
                  </div>
                  <span>举报线索</span>
                </div>
                <div class="quick-item" @click="router.push('/wrongbook')">
                  <div class="quick-icon" style="background: var(--gradient-warning)">
                    <el-icon><Document /></el-icon>
                  </div>
                  <span>错题本</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑资料" width="500px">
      <el-form :model="editForm" label-position="top">
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
          <el-input v-model="editForm.name" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="学校">
          <el-input v-model="editForm.school" placeholder="请输入学校" />
        </el-form-item>
        <el-form-item label="院系">
          <el-input v-model="editForm.department" placeholder="请输入院系" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input
            v-model="editForm.bio"
            type="textarea"
            :rows="3"
            placeholder="介绍一下自己吧..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import {
  Star, Timer, Edit, ArrowRight, Plus, ShoppingCart, EditPen,
  Coordinate, WarnTriangleFilled, Document, Top, Bottom, Trophy
} from '@element-plus/icons-vue'

const router = useRouter()
const showEditDialog = ref(false)

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

const saveProfile = () => {
  userInfo.name = editForm.name || userInfo.name
  userInfo.school = editForm.school || userInfo.school
  userInfo.department = editForm.department || userInfo.department
  showEditDialog.value = false
}
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
}

.avatar-wrapper {
  position: relative;
}

.avatar-wrapper :deep(.el-avatar) {
  border: 4px solid white;
  box-shadow: var(--shadow-lg);
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
}

.user-details {
  flex: 1;
  color: white;
}

.user-details h1 {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-2);
}

.user-school {
  font-size: var(--font-size-base);
  opacity: 0.9;
  margin-bottom: var(--spacing-3);
}

.user-tags {
  display: flex;
  gap: var(--spacing-3);
}

.user-tags .tag {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-1);
  padding: var(--spacing-1) var(--spacing-3);
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
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
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-4) var(--spacing-5);
  border-bottom: 1px solid var(--border-primary);
}

.card-header h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
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
  padding: var(--spacing-3);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
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
}

.recent-item:hover {
  background: var(--bg-hover);
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

/* 积分卡片 */
.points-card {
  background: linear-gradient(180deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
}

.points-main {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-4);
}

.points-value {
  font-size: var(--font-size-5xl);
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-light) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
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
}

.history-change.add {
  color: var(--success-color);
}

.history-change.sub {
  color: var(--danger-color);
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
}

.badge-item.locked {
  opacity: 0.5;
}

.badge-item:not(.locked):hover {
  background: var(--primary-bg);
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
}

.badge-item.locked .badge-icon {
  background: var(--bg-tertiary);
  color: var(--text-muted);
}

.badge-item span {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
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
}

.quick-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
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
}

.quick-item span {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

/* 编辑资料 */
.avatar-uploader {
  display: flex;
  justify-content: center;
}

.avatar-uploader :deep(.el-upload) {
  border: 2px dashed var(--border-primary);
  border-radius: 50%;
  cursor: pointer;
  transition: border-color var(--transition-fast);
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--primary-color);
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
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
}
</style>
