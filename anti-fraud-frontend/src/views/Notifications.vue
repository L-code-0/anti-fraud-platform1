<template>
  <div class="notification-center">
    <!-- 头部 -->
    <div class="header">
      <h1>消息中心</h1>
      <div class="header-actions">
        <el-button 
          v-if="notificationStore.unreadCount > 0"
          type="primary"
          @click="handleMarkAllRead"
        >
          全部已读
        </el-button>
        <el-button @click="handleOpenSettings">
          <el-icon><Setting /></el-icon>
          通知设置
        </el-button>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="filter-bar">
      <el-radio-group v-model="filterType">
        <el-radio-button value="all">全部通知</el-radio-button>
        <el-radio-button value="unread">未读</el-radio-button>
        <el-radio-button value="system">系统</el-radio-button>
        <el-radio-button value="warning">预警</el-radio-button>
        <el-radio-button value="achievement">成就</el-radio-button>
        <el-radio-button value="test">考试</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 通知列表 -->
    <div class="notification-list" v-loading="loading">
      <template v-if="filteredNotifications.length > 0">
        <div 
          v-for="notification in filteredNotifications" 
          :key="notification.id"
          class="notification-item"
          :class="{ unread: !notification.isRead, [`priority-${notification.priority}`]: true }"
        >
          <div class="item-icon" :class="`type-${notification.type}`">
            <el-icon>
              <component :is="getNotificationIcon(notification.type)" />
            </el-icon>
          </div>
          <div class="item-content" @click="handleClick(notification)">
            <div class="item-header">
              <span class="item-title">{{ notification.title }}</span>
              <span class="item-tag" :class="`tag-${notification.type}`">
                {{ getTypeText(notification.type) }}
              </span>
            </div>
            <p class="item-text">{{ notification.content }}</p>
            <div class="item-footer">
              <span class="item-time">{{ formatTime(notification.createdAt) }}</span>
              <span v-if="notification.priority === 'urgent'" class="urgent-tag">紧急</span>
            </div>
          </div>
          <div class="item-actions">
            <el-button 
              v-if="!notification.isRead" 
              text 
              type="primary"
              @click="handleMarkRead(notification.id)"
            >
              标记已读
            </el-button>
            <el-button text @click="handleDelete(notification.id)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </template>
      <el-empty v-else description="暂无通知" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 设置对话框 -->
    <el-dialog v-model="settingsVisible" title="通知设置" width="500px">
      <div class="settings-form">
        <div class="setting-group">
          <h4>通知类型</h4>
          <el-checkbox v-model="settings.system">系统通知</el-checkbox>
          <el-checkbox v-model="settings.warning">预警通知</el-checkbox>
          <el-checkbox v-model="settings.report">举报反馈</el-checkbox>
          <el-checkbox v-model="settings.test">考试通知</el-checkbox>
          <el-checkbox v-model="settings.points">积分变动</el-checkbox>
          <el-checkbox v-model="settings.achievement">成就解锁</el-checkbox>
          <el-checkbox v-model="settings.social">社交互动</el-checkbox>
          <el-checkbox v-model="settings.activity">活动通知</el-checkbox>
        </div>
        <div class="setting-group">
          <h4>通知方式</h4>
          <el-checkbox v-model="settings.emailNotify">邮件通知</el-checkbox>
          <el-checkbox v-model="settings.pushNotify">推送通知</el-checkbox>
        </div>
      </div>
      <template #footer>
        <el-button @click="settingsVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveSettings">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, Delete, Setting, User, Warning, Trophy, Star, Present, ChatDotRound } from '@element-plus/icons-vue'
import { useNotificationStore } from '@/stores/notification'
import type { Notification, NotificationType, NotificationSettings } from '@/types'

const router = useRouter()
const notificationStore = useNotificationStore()

const filterType = ref('all')
const currentPage = ref(1)
const pageSize = ref(20)
const settingsVisible = ref(false)
const settings = ref<NotificationSettings>({
  system: true,
  warning: true,
  report: true,
  test: true,
  points: true,
  achievement: true,
  social: true,
  activity: true,
  emailNotify: false,
  pushNotify: false
})

const loading = computed(() => notificationStore.loading)
const total = computed(() => notificationStore.notifications.length)

const filteredNotifications = computed(() => {
  const notifications = notificationStore.notifications
  let filtered = notifications
  
  switch (filterType.value) {
    case 'unread':
      filtered = notifications.filter(n => !n.isRead)
      break
    case 'system':
      filtered = notifications.filter(n => n.type === 'system')
      break
    case 'warning':
      filtered = notifications.filter(n => n.type === 'warning')
      break
    case 'achievement':
      filtered = notifications.filter(n => n.type === 'achievement')
      break
    case 'test':
      filtered = notifications.filter(n => n.type === 'test')
      break
  }
  
  return filtered.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value)
})

function getNotificationIcon(type: NotificationType) {
  const iconMap: Record<NotificationType, any> = {
    system: User,
    warning: Warning,
    report: ChatDotRound,
    test: Trophy,
    points: Star,
    achievement: Present,
    social: ChatDotRound,
    activity: Star
  }
  return iconMap[type] || Bell
}

function getTypeText(type: NotificationType): string {
  const textMap: Record<NotificationType, string> = {
    system: '系统',
    warning: '预警',
    report: '举报',
    test: '考试',
    points: '积分',
    achievement: '成就',
    social: '社交',
    activity: '活动'
  }
  return textMap[type] || '通知'
}

function formatTime(timeStr: string): string {
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return time.toLocaleDateString()
}

function handleClick(notification: Notification) {
  if (!notification.isRead) {
    handleMarkRead(notification.id)
  }
  
  if (notification.relatedId && notification.relatedType) {
    const routes: Record<string, string> = {
      knowledge: `/knowledge/${notification.relatedId}`,
      test: `/test/${notification.relatedId}`,
      activity: `/activity/${notification.relatedId}`
    }
    if (routes[notification.relatedType]) {
      router.push(routes[notification.relatedType])
    }
  }
}

async function handleMarkRead(id: number) {
  await notificationStore.markAsRead(id)
}

async function handleMarkAllRead() {
  await notificationStore.markAllAsRead()
  ElMessage.success('已全部标记为已读')
}

async function handleDelete(id: number) {
  await notificationStore.deleteNotification(id)
  ElMessage.success('已删除')
}

function handlePageChange(page: number) {
  currentPage.value = page
}

function handleOpenSettings() {
  settings.value = { ...notificationStore.settings }
  settingsVisible.value = true
}

async function handleSaveSettings() {
  await notificationStore.updateSettings(settings.value)
  settingsVisible.value = false
  ElMessage.success('设置已保存')
}

onMounted(() => {
  notificationStore.fetchNotifications()
  notificationStore.fetchSettings()
})
</script>

<style scoped>
.notification-center {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header h1 {
  margin: 0;
  font-size: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-bar {
  margin-bottom: 20px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.notification-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.unread {
  background-color: #ecf5ff;
}

.notification-item.unread:hover {
  background-color: #d9ecff;
}

.item-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}

.item-icon.type-system { background: var(--el-color-info-light-9); color: var(--el-color-info); }
.item-icon.type-warning { background: var(--el-color-danger-light-9); color: var(--el-color-danger); }
.item-icon.type-report { background: var(--el-color-success-light-9); color: var(--el-color-success); }
.item-icon.type-test { background: var(--el-color-warning-light-9); color: var(--el-color-warning); }
.item-icon.type-points { background: #fdf6ec; color: #e6a23c; }
.item-icon.type-achievement { background: var(--el-color-primary-light-9); color: var(--el-color-primary); }
.item-icon.type-social { background: #ecf5ff; color: #409eff; }
.item-icon.type-activity { background: #f0f9eb; color: #67c23a; }

.item-content {
  flex: 1;
  min-width: 0;
  cursor: pointer;
}

.item-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.item-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.tag-system { background: var(--el-color-info-light-9); color: var(--el-color-info); }
.tag-warning { background: var(--el-color-danger-light-9); color: var(--el-color-danger); }
.tag-report { background: var(--el-color-success-light-9); color: var(--el-color-success); }
.tag-test { background: var(--el-color-warning-light-9); color: var(--el-color-warning); }
.tag-achievement { background: var(--el-color-primary-light-9); color: var(--el-color-primary); }
.tag-activity { background: #f0f9eb; color: #67c23a; }

.item-text {
  margin: 0 0 8px;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.item-footer {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-time {
  font-size: 12px;
  color: #999;
}

.urgent-tag {
  font-size: 11px;
  padding: 1px 6px;
  background: #f56c6c;
  color: white;
  border-radius: 4px;
}

.item-actions {
  display: flex;
  gap: 8px;
  margin-left: 16px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

.settings-form {
  padding: 16px 0;
}

.setting-group {
  margin-bottom: 24px;
}

.setting-group h4 {
  margin: 0 0 12px;
  font-size: 14px;
  color: #333;
}

.setting-group .el-checkbox {
  display: block;
  margin: 8px 0;
}

@media (max-width: 768px) {
  .notification-center {
    padding: 16px;
  }
  
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .notification-item {
    flex-direction: column;
  }
  
  .item-icon {
    margin-bottom: 12px;
  }
  
  .item-actions {
    margin-left: 0;
    margin-top: 12px;
    align-self: flex-end;
  }
}
</style>
