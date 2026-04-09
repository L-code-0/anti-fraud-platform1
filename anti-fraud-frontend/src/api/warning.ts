/**
 * 预警模块 API
 */
import { get } from './request'

// 获取预警列表
export function getWarningList(params?: {
  page?: number
  pageSize?: number
  type?: string
  level?: string
}) {
  return get('/warning/list', params)
}

// 获取预警详情
export function getWarningDetail(id: number) {
  return get(`/warning/${id}`)
}

// 获取预警统计
export function getWarningStats() {
  return get('/warning/stats')
}
