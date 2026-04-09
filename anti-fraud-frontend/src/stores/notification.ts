import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Notification, NotificationSettings } from '@/types'
import { get, post } from '@/utils/request'

export const useNotificationStore = defineStore('notification', () => {
  // 状态
  const notifications = ref<Notification[]>([])
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
  const loading = ref(false)
  const unreadCount = ref(0)

  // 计算属性
  const unreadNotifications = computed(() => 
    notifications.value.filter(n => !n.isRead)
  )
  
  const systemNotifications = computed(() =>
    notifications.value.filter(n => n.type === 'system')
  )
  
  const warningNotifications = computed(() =>
    notifications.value.filter(n => n.type === 'warning')
  )

  // 获取通知列表
  async function fetchNotifications() {
    loading.value = true
    try {
      const res = await get<Notification[]>('/notification/list')
      notifications.value = res.data || []
      updateUnreadCount()
    } catch (error) {
      console.error('获取通知失败:', error)
      // 使用模拟数据
      notifications.value = getMockNotifications()
      updateUnreadCount()
    } finally {
      loading.value = false
    }
  }

  // 标记已读
  async function markAsRead(id: number) {
    try {
      await post('/notification/read', { id })
      const notification = notifications.value.find(n => n.id === id)
      if (notification) {
        notification.isRead = true
        updateUnreadCount()
      }
    } catch (error) {
      console.error('标记已读失败:', error)
      // 本地更新
      const notification = notifications.value.find(n => n.id === id)
      if (notification) {
        notification.isRead = true
        updateUnreadCount()
      }
    }
  }

  // 全部标记已读
  async function markAllAsRead() {
    try {
      await post('/notification/read-all', {})
      notifications.value.forEach(n => n.isRead = true)
      updateUnreadCount()
    } catch (error) {
      console.error('标记全部已读失败:', error)
      notifications.value.forEach(n => n.isRead = true)
      updateUnreadCount()
    }
  }

  // 删除通知
  async function deleteNotification(id: number) {
    try {
      await post('/notification/delete', { id })
      const index = notifications.value.findIndex(n => n.id === id)
      if (index > -1) {
        const wasUnread = !notifications.value[index].isRead
        notifications.value.splice(index, 1)
        if (wasUnread) updateUnreadCount()
      }
    } catch (error) {
      console.error('删除通知失败:', error)
      const index = notifications.value.findIndex(n => n.id === id)
      if (index > -1) {
        const wasUnread = !notifications.value[index].isRead
        notifications.value.splice(index, 1)
        if (wasUnread) updateUnreadCount()
      }
    }
  }

  // 获取通知设置
  async function fetchSettings() {
    try {
      const res = await get<NotificationSettings>('/notification/settings')
      settings.value = res.data || settings.value
    } catch (error) {
      console.error('获取通知设置失败:', error)
    }
  }

  // 更新通知设置
  async function updateSettings(newSettings: Partial<NotificationSettings>) {
    try {
      await post('/notification/settings', newSettings)
      settings.value = { ...settings.value, ...newSettings }
    } catch (error) {
      console.error('更新通知设置失败:', error)
      settings.value = { ...settings.value, ...newSettings }
    }
  }

  // 更新未读数
  function updateUnreadCount() {
    unreadCount.value = notifications.value.filter(n => !n.isRead).length
  }

  // 模拟数据
  function getMockNotifications(): Notification[] {
    return [
      {
        id: 1,
        type: 'warning',
        title: '新型刷单诈骗预警',
        content: '近期出现新型刷单诈骗，请提高警惕...',
        isRead: false,
        createdAt: new Date().toISOString(),
        priority: 'high'
      },
      {
        id: 2,
        type: 'system',
        title: '系统升级通知',
        content: '平台将于今晚进行系统升级...',
        isRead: false,
        createdAt: new Date(Date.now() - 3600000).toISOString(),
        priority: 'normal'
      },
      {
        id: 3,
        type: 'achievement',
        title: '成就解锁',
        content: '恭喜您解锁"反诈达人"成就！',
        isRead: true,
        createdAt: new Date(Date.now() - 86400000).toISOString(),
        priority: 'low'
      },
      {
        id: 4,
        type: 'test',
        title: '新试卷上线',
        content: '《防诈骗知识测试》试卷已上线，快来挑战吧！',
        isRead: false,
        createdAt: new Date(Date.now() - 7200000).toISOString(),
        priority: 'normal'
      },
      {
        id: 5,
        type: 'points',
        title: '积分变动',
        content: '您的账户获得 +50 积分奖励',
        isRead: true,
        createdAt: new Date(Date.now() - 172800000).toISOString(),
        priority: 'low'
      }
    ]
  }

  return {
    notifications,
    settings,
    loading,
    unreadCount,
    unreadNotifications,
    systemNotifications,
    warningNotifications,
    fetchNotifications,
    markAsRead,
    markAllAsRead,
    deleteNotification,
    fetchSettings,
    updateSettings
  }
})
