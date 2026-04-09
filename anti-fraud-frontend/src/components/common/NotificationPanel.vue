<template>
  <div class="notification-panel">
    <!-- 通知按钮 -->
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
      <el-popover
        placement="bottom-end"
        :width="380"
        trigger="click"
        :show-arrow="true"
        @show="handleOpen"
      >
        <template #reference>
          <el-button :icon="Bell" circle class="notification-btn" />
        </template>
        
        <!-- 通知列表 -->
        <div class="notification-list">
          <div class="notification-header">
            <h3>通知中心</h3>
            <el-space>
              <el-button 
                v-if="unreadCount > 0" 
                text 
                size="small" 
                type="primary"
                @click="handleMarkAllRead"
              >
                全部已读
              </el-button>
              <el-button text size="small" @click="$router.push('/notifications')">
                查看全部
              </el-button>
            </el-space>
          </div>
          
          <div class="notification-tabs">
            <el-radio-group v-model="activeTab" size="small">
              <el-radio-button value="all">全部</el-radio-button>
              <el-radio-button value="unread">未读</el-radio-button>
              <el-radio-button value="system">系统</el-radio-button>
              <el-radio-button value="warning">预警</el-radio-button>
            </el-radio-group>
          </div>
          
          <div class="notification-content" v-loading="loading">
            <template v-if="filteredNotifications.length > 0">
              <div 
                v-for="notification in filteredNotifications" 
                :key="notification.id"
                class="notification-item"
                :class="{ unread: !notification.isRead, [`priority-${notification.priority}`]: true }"
                @click="handleClick(notification)"
              >
                <div class="item-icon" :class="`type-${notification.type}`">
                  <el-icon>
                    <component :is="getNotificationIcon(notification.type)" />
                  </el-icon>
                </div>
                <div class="item-content">
                  <div class="item-header">
                    <span class="item-title">{{ notification.title }}</span>
                    <span class="item-time">{{ formatTime(notification.createdAt) }}</span>
                  </div>
                  <p class="item-text">{{ notification.content }}</p>
                </div>
                <div class="item-actions">
                  <el-button 
                    v-if="!notification.isRead" 
                    text 
                    size="small" 
                    type="primary"
                    @click.stop="handleMarkRead(notification.id)"
                  >
                    已读
                  </el-button>
                  <el-button 
                    text 
                    size="small" 
                    @click.stop="handleDelete(notification.id)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </template>
            <el-empty v-else description="暂无通知" :image-size="80" />
          </div>
        </div>
      </el-popover>
    </el-badge>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, Delete, User, Warning, Trophy, Star, ChatDotRound, Present } from '@element-plus/icons-vue'
import { useNotificationStore } from '@/stores/notification'
import { useUserStore } from '@/stores/user'
import type { Notification, NotificationType } from '@/types'

const router = useRouter()
const notificationStore = useNotificationStore()
const userStore = useUserStore()

const activeTab = ref('all')

// 计算属性
const unreadCount = computed(() => notificationStore.unreadCount)
const loading = computed(() => notificationStore.loading)

const filteredNotifications = computed(() => {
  const notifications = notificationStore.notifications
  
  switch (activeTab.value) {
    case 'unread':
      return notifications.filter(n => !n.isRead)
    case 'system':
      return notifications.filter(n => n.type === 'system')
    case 'warning':
      return notifications.filter(n => n.type === 'warning')
    default:
      return notifications
  }
})

// 获取通知图标
function getNotificationIcon(type: NotificationType) {
  const iconMap = {
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

// 格式化时间
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

// 处理打开面板
function handleOpen() {
  if (userStore.isLoggedIn) {
    notificationStore.fetchNotifications()
  }
}

// 处理点击通知
function handleClick(notification: Notification) {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  if (!notification.isRead) {
    handleMarkRead(notification.id)
  }
  
  // 根据通知类型跳转到对应页面
  if (notification.relatedId) {
    switch (notification.relatedType) {
      case 'knowledge':
        router.push(`/knowledge/${notification.relatedId}`)
        break
      case 'test':
        router.push(`/test/${notification.relatedId}`)
        break
      case 'activity':
        router.push(`/activity/${notification.relatedId}`)
        break
    }
  }
}

// 标记已读
async function handleMarkRead(id: number) {
  if (userStore.isLoggedIn) {
    await notificationStore.markAsRead(id)
  }
}

// 全部标记已读
async function handleMarkAllRead() {
  if (userStore.isLoggedIn) {
    await notificationStore.markAllAsRead()
    ElMessage.success('已全部标记为已读')
  } else {
    router.push('/login')
  }
}

// 删除通知
async function handleDelete(id: number) {
  if (userStore.isLoggedIn) {
    await notificationStore.deleteNotification(id)
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    notificationStore.fetchNotifications()
  }
})
</script>

<style scoped>
.notification-panel {
  display: inline-flex;
  align-items: center;
}

.notification-btn {
  font-size: 18px;
}

.notification-list {
  margin: -12px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.notification-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.notification-tabs {
  padding: 12px 16px 0;
}

.notification-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 8px 0;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.notification-item:hover {
  background-color: var(--el-fill-color-light);
}

.notification-item.unread {
  background-color: var(--el-color-primary-light-9);
}

.notification-item.unread:hover {
  background-color: var(--el-color-primary-light-8);
}

.item-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
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
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.item-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.item-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.item-text {
  font-size: 13px;
  color: var(--el-text-color-regular);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-actions {
  display: flex;
  gap: 4px;
  margin-left: 8px;
}

.priority-high .item-icon,
.priority-urgent .item-icon {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

/* 响应式 */
@media (max-width: 768px) {
  .notification-list {
    width: calc(100vw - 32px);
    margin: -12px;
  }
  
  .notification-content {
    max-height: 60vh;
  }
}
</style>

