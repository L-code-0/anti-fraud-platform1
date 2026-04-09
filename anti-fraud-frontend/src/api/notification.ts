/**
 * 通知模块 API
 */
import { get, post } from './request'

// 获取通知列表
export function getNotificationList(params?: {
  page?: number
  pageSize?: number
  type?: string
}) {
  return get('/notification/list', params)
}

// 标记通知已读
export function markNotificationRead(id: number) {
  return post('/notification/read', { id })
}

// 标记全部已读
export function markAllNotificationRead() {
  return post('/notification/read-all')
}

// 删除通知
export function deleteNotification(id: number) {
  return post('/notification/delete', { id })
}

// 获取通知设置
export function getNotificationSettings() {
  return get('/notification/settings')
}

// 更新通知设置
export function updateNotificationSettings(settings: {
  system?: boolean
  warning?: boolean
  report?: boolean
  test?: boolean
  points?: boolean
  achievement?: boolean
  social?: boolean
  activity?: boolean
  emailNotify?: boolean
  pushNotify?: boolean
}) {
  return post('/notification/settings', settings)
}
