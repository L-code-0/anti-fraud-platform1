/**
 * 活动模块 API
 */
import { get, post } from './request'

// 获取活动列表
export function getActivityList(params?: {
  page?: number
  pageSize?: number
  status?: string
  keyword?: string
}) {
  return get('/activity/list', params)
}

// 获取活动详情
export function getActivityDetail(id: number) {
  return get(`/activity/${id}`)
}

// 活动报名
export function registerActivity(id: number) {
  return post('/activity/register', { id })
}

// 取消报名
export function cancelActivityRegistration(id: number) {
  return post('/activity/cancel', { id })
}
