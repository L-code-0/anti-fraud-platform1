<template>
  <div class="notifications-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
      </div>
      <div class="header-content">
        <h1>消息通知</h1>
        <p>查看系统通知、学习提醒和互动消息</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 通知统计 -->
      <div class="stats-row">
        <div class="stat-card" v-for="stat in notificationStats" :key="stat.label">
          <div class="stat-icon" :class="'icon-' + stat.type">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
        </div>
      </div>

      <!-- 通知分类 -->
      <div class="notifications-layout">
        <!-- 左侧分类 -->
        <div class="categories-sidebar">
          <div
            class="category-item"
            :class="{ active: activeCategory === 'all' }"
            @click="activeCategory = 'all'"
          >
            <el-icon><Bell /></el-icon>
            <span>全部通知</span>
            <span class="count">{{ notifications.length }}</span>
          </div>
          <div
            class="category-item"
            :class="{ active: activeCategory === 'system' }"
            @click="activeCategory = 'system'"
          >
            <el-icon><Setting /></el-icon>
            <span>系统通知</span>
            <span class="count">{{ systemCount }}</span>
          </div>
          <div
            class="category-item"
            :class="{ active: activeCategory === 'learning' }"
            @click="activeCategory = 'learning'"
          >
            <el-icon><Reading /></el-icon>
            <span>学习提醒</span>
            <span class="count">{{ learningCount }}</span>
          </div>
          <div
            class="category-item"
            :class="{ active: activeCategory === 'interaction' }"
            @click="activeCategory = 'interaction'"
          >
            <el-icon><ChatDotRound /></el-icon>
            <span>互动消息</span>
            <span class="count">{{ interactionCount }}</span>
          </div>
          <div
            class="category-item"
            :class="{ active: activeCategory === 'activity' }"
            @click="activeCategory = 'activity'"
          >
            <el-icon><Medal /></el-icon>
            <span>活动通知</span>
            <span class="count">{{ activityCount }}</span>
          </div>
        </div>

        <!-- 右侧通知列表 -->
        <div class="notifications-list">
          <div class="list-header">
            <div class="header-left">
              <el-checkbox v-model="selectAll" :indeterminate="isIndeterminate" @change="handleSelectAll">
                全选
              </el-checkbox>
              <span class="selected-count" v-if="selectedNotifications.length > 0">
                已选择 {{ selectedNotifications.length }} 项
              </span>
            </div>
            <div class="header-actions">
              <el-button
                text
                :disabled="selectedNotifications.length === 0"
                @click="handleMarkAllRead"
              >
                <el-icon><Check /></el-icon>
                全部已读
              </el-button>
              <el-button
                text
                type="danger"
                :disabled="selectedNotifications.length === 0"
                @click="handleBatchDelete"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>

          <div class="notifications">
            <div
              class="notification-item"
              v-for="notification in filteredNotifications"
              :key="notification.id"
              :class="{
                unread: !notification.read,
                selected: selectedNotifications.includes(notification.id)
              }"
              @click="handleNotificationClick(notification)"
            >
              <div class="item-checkbox">
                <el-checkbox
                  :model-value="selectedNotifications.includes(notification.id)"
                  @click.stop
                  @change="handleSelect(notification.id)"
                />
              </div>
              <div class="item-icon" :class="'type-' + notification.type">
                <el-icon><component :is="notification.icon" /></el-icon>
              </div>
              <div class="item-content">
                <div class="item-header">
                  <h4>{{ notification.title }}</h4>
                  <span class="item-time">{{ notification.time }}</span>
                </div>
                <p>{{ notification.content }}</p>
              </div>
              <div class="item-actions">
                <el-tooltip content="标为已读" placement="top">
                  <el-button
                    text
                    circle
                    @click.stop="handleMarkRead(notification)"
                    v-if="!notification.read"
                  >
                    <el-icon><Message /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button
                    text
                    circle
                    type="danger"
                    @click.stop="handleDelete(notification)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </div>

            <!-- 空状态 -->
            <div class="empty-state" v-if="filteredNotifications.length === 0">
              <div class="empty-icon">
                <el-icon><Bell /></el-icon>
              </div>
              <h3>暂无通知</h3>
              <p>您目前没有{{ activeCategory === 'all' ? '' : getCategoryName(activeCategory) }}相关通知</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="currentNotification?.title"
      width="500px"
    >
      <div class="detail-content" v-if="currentNotification">
        <div class="detail-header">
          <div class="detail-icon" :class="'type-' + currentNotification.type">
            <el-icon><component :is="currentNotification.icon" /></el-icon>
          </div>
          <div class="detail-meta">
            <span class="detail-type">{{ getTypeName(currentNotification.type) }}</span>
            <span class="detail-time">{{ currentNotification.time }}</span>
          </div>
        </div>
        <div class="detail-body">
          <p>{{ currentNotification.content }}</p>
          <div class="detail-extra" v-if="currentNotification.extra">
            <div class="extra-item" v-for="(value, key) in currentNotification.extra" :key="key">
              <span class="extra-label">{{ key }}</span>
              <span class="extra-value">{{ value }}</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="primary" v-if="currentNotification?.action" @click="handleNotificationAction">
          {{ currentNotification.actionText }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Bell, Setting, Reading, ChatDotRound, Medal, Check, Delete,
  Message, Star, Trophy, Gift, Warning, Document, VideoPlay
} from '@element-plus/icons-vue'

const activeCategory = ref('all')
const showDetailDialog = ref(false)
const currentNotification = ref<any>(null)
const selectedNotifications = ref<number[]>([])
const selectAll = ref(false)
const isIndeterminate = computed(() => {
  return selectedNotifications.value.length > 0 &&
    selectedNotifications.value.length < filteredNotifications.value.length
})

const notificationStats = [
  { type: 'all', icon: 'Bell', value: 24, label: '全部通知' },
  { type: 'system', icon: 'Setting', value: 5, label: '系统通知' },
  { type: 'learning', icon: 'Reading', value: 12, label: '学习提醒' },
  { type: 'interaction', icon: 'ChatDotRound', value: 7, label: '互动消息' }
]

const notifications = ref([
  {
    id: 1,
    type: 'learning',
    icon: 'Reading',
    title: '您有一篇新文章待学习',
    content: '《揭秘最新AI换脸诈骗手法》已更新，点击开始学习了解更多防骗技巧',
    time: '10分钟前',
    read: false,
    action: '/knowledge/1',
    actionText: '立即学习',
    extra: { '预计时长': '15分钟', '可获积分': '+20' }
  },
  {
    id: 2,
    type: 'system',
    icon: 'Trophy',
    title: '恭喜获得"反诈达人"徽章',
    content: '您已完成所有基础课程学习，获得"反诈达人"荣誉徽章，快去看看吧！',
    time: '1小时前',
    read: false,
    action: '/achievement',
    actionText: '查看成就'
  },
  {
    id: 3,
    type: 'activity',
    icon: 'Medal',
    title: '反诈知识大赛报名即将截止',
    content: '2026年度反诈知识大赛报名将于明天截止，赶快报名参加吧！丰厚奖品等着你',
    time: '2小时前',
    read: false,
    action: '/activity/1',
    actionText: '立即报名'
  },
  {
    id: 4,
    type: 'learning',
    icon: 'Document',
    title: '测试成绩已发布',
    content: '您参加的《电信诈骗防范测试》已完成，得分85分，已达到优秀水平！',
    time: '3小时前',
    read: true,
    action: '/test/result/1',
    actionText: '查看详情'
  },
  {
    id: 5,
    type: 'interaction',
    icon: 'ChatDotRound',
    title: '收到新的回复',
    content: '您的提问"如何识别冒充客服的电话？"收到了3条新回复',
    time: '5小时前',
    read: true
  },
  {
    id: 6,
    type: 'system',
    icon: 'Gift',
    title: '每日签到奖励',
    content: '恭喜您完成今日签到，获得5积分奖励！连续签到7天可获得额外奖励',
    time: '昨天',
    read: true
  },
  {
    id: 7,
    type: 'warning',
    icon: 'Warning',
    title: '新型诈骗预警',
    content: '【紧急预警】近期出现新型"屏幕共享"诈骗手法，请提高警惕！',
    time: '昨天',
    read: true,
    action: '/warning/1',
    actionText: '查看详情'
  }
])

const filteredNotifications = computed(() => {
  if (activeCategory.value === 'all') {
    return notifications.value
  }
  return notifications.value.filter(n => n.type === activeCategory.value)
})

const systemCount = computed(() => notifications.value.filter(n => n.type === 'system').length)
const learningCount = computed(() => notifications.value.filter(n => n.type === 'learning').length)
const interactionCount = computed(() => notifications.value.filter(n => n.type === 'interaction').length)
const activityCount = computed(() => notifications.value.filter(n => n.type === 'activity').length)

const getCategoryName = (category: string) => {
  const names: Record<string, string> = {
    system: '系统',
    learning: '学习',
    interaction: '互动',
    activity: '活动'
  }
  return names[category] || ''
}

const getTypeName = (type: string) => {
  const names: Record<string, string> = {
    system: '系统通知',
    learning: '学习提醒',
    interaction: '互动消息',
    activity: '活动通知',
    warning: '预警信息'
  }
  return names[type] || '通知'
}

const handleSelect = (id: number) => {
  const index = selectedNotifications.value.indexOf(id)
  if (index === -1) {
    selectedNotifications.value.push(id)
  } else {
    selectedNotifications.value.splice(index, 1)
  }
}

const handleSelectAll = (val: boolean) => {
  if (val) {
    selectedNotifications.value = filteredNotifications.value.map(n => n.id)
  } else {
    selectedNotifications.value = []
  }
}

const handleNotificationClick = (notification: any) => {
  notification.read = true
  currentNotification.value = notification
  showDetailDialog.value = true
}

const handleMarkRead = (notification: any) => {
  notification.read = true
  ElMessage.success('已标记为已读')
}

const handleMarkAllRead = () => {
  filteredNotifications.value.forEach(n => {
    n.read = true
  })
  selectedNotifications.value = []
  ElMessage.success('已全部标记为已读')
}

const handleDelete = (notification: any) => {
  const index = notifications.value.indexOf(notification)
  if (index !== -1) {
    notifications.value.splice(index, 1)
    ElMessage.success('删除成功')
  }
}

const handleBatchDelete = () => {
  notifications.value = notifications.value.filter(
    n => !selectedNotifications.value.includes(n.id)
  )
  selectedNotifications.value = []
  ElMessage.success('批量删除成功')
}

const handleNotificationAction = () => {
  if (currentNotification.value?.action) {
    showDetailDialog.value = false
    // router.push(currentNotification.value.action)
    ElMessage.success('即将跳转到目标页面')
  }
}
</script>

<style scoped>
.notifications-page {
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
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #a855f7 100%);
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

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-4);
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
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
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

.stat-icon.icon-all { background: var(--gradient-primary); }
.stat-icon.icon-system { background: var(--gradient-info); }
.stat-icon.icon-learning { background: var(--gradient-success); }
.stat-icon.icon-interaction { background: var(--gradient-warning); }

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

/* 布局 */
.notifications-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: var(--spacing-6);
}

/* 分类侧边栏 */
.categories-sidebar {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-4);
  box-shadow: var(--shadow-md);
  height: fit-content;
  position: sticky;
  top: 80px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  padding: var(--spacing-3) var(--spacing-4);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-1);
}

.category-item:hover {
  background: var(--bg-hover);
  color: var(--primary-color);
}

.category-item.active {
  background: var(--primary-bg);
  color: var(--primary-color);
  font-weight: var(--font-weight-medium);
}

.category-item .el-icon {
  font-size: 18px;
}

.category-item span:first-of-type {
  flex: 1;
}

.category-item .count {
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--bg-secondary);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.category-item.active .count {
  background: var(--primary-color);
  color: white;
}

/* 通知列表 */
.notifications-list {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-4) var(--spacing-5);
  border-bottom: 1px solid var(--border-primary);
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
}

.selected-count {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.header-actions {
  display: flex;
  gap: var(--spacing-2);
}

/* 通知项 */
.notifications {
  padding: var(--spacing-2);
  max-height: 600px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-3);
  padding: var(--spacing-4);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  margin-bottom: var(--spacing-2);
}

.notification-item:hover {
  background: var(--bg-hover);
}

.notification-item.unread {
  background: var(--primary-bg);
}

.notification-item.unread::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 40px;
  background: var(--primary-color);
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
}

.notification-item {
  position: relative;
}

.item-checkbox {
  padding-top: var(--spacing-1);
}

.item-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.item-icon.type-system { background: var(--info-color); }
.item-icon.type-learning { background: var(--success-color); }
.item-icon.type-interaction { background: var(--warning-color); }
.item-icon.type-activity { background: var(--purple-color); }
.item-icon.type-warning { background: var(--danger-color); }

.item-content {
  flex: 1;
  min-width: 0;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-1);
}

.item-content h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.item-time {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.item-content p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-actions {
  display: flex;
  gap: var(--spacing-1);
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.notification-item:hover .item-actions {
  opacity: 1;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-12);
  text-align: center;
}

.empty-icon {
  width: 80px;
  height: 80px;
  background: var(--bg-secondary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: var(--text-muted);
  margin-bottom: var(--spacing-4);
}

.empty-state h3 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.empty-state p {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

/* 详情对话框 */
.detail-content {
  padding: var(--spacing-2);
}

.detail-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding-bottom: var(--spacing-4);
  border-bottom: 1px solid var(--border-primary);
  margin-bottom: var(--spacing-4);
}

.detail-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.detail-meta {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-1);
}

.detail-type {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

.detail-time {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.detail-body p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: var(--spacing-4);
}

.detail-extra {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: var(--spacing-4);
}

.extra-item {
  display: flex;
  justify-content: space-between;
  padding: var(--spacing-2) 0;
  border-bottom: 1px solid var(--border-secondary);
}

.extra-item:last-child {
  border-bottom: none;
}

.extra-label {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.extra-value {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

/* 响应式 */
@media (max-width: 1024px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .notifications-layout {
    grid-template-columns: 1fr;
  }

  .categories-sidebar {
    position: static;
    display: flex;
    flex-wrap: wrap;
    gap: var(--spacing-2);
  }

  .category-item {
    margin-bottom: 0;
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

  .stats-row {
    grid-template-columns: 1fr;
  }

  .notification-item {
    flex-wrap: wrap;
  }

  .item-actions {
    width: 100%;
    justify-content: flex-end;
    margin-top: var(--spacing-2);
    opacity: 1;
  }
}
</style>
