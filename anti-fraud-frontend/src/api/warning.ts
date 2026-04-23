/**
 * 预警模块 API
 */
import { get, post } from './request'

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

// 获取最新的预警信息
export function getLatestWarnings(limit: number) {
  return get('/warning/latest', { limit })
}

// 根据预警类型获取预警信息
export function getWarningsByType(type: number, limit: number) {
  return get('/warning/by-type', { warningType: type, limit })
}

// 根据来源获取预警信息
export function getWarningsBySource(source: string, limit: number) {
  return get('/warning/by-source', { source, limit })
}

// 获取反诈预警信息详情
export function getExternalWarningDetail(id: number) {
  return get(`/warning/detail/${id}`)
}

// 手动同步预警信息
export function manualSyncWarnings(source: string) {
  return post('/warning/sync', { source })
}

// 同步所有预警信息
export function syncAllWarnings() {
  return post('/warning/sync-all')
}

// 获取预警信息统计
export function getExternalWarningStatistics() {
  return get('/warning/statistics')
}

// 测试预警信息接口连接
export function testConnection(source: string) {
  return get('/warning/test-connection', { source })
}

// 清理过期预警信息
export function cleanExpiredWarnings() {
  return post('/warning/clean-expired')
}
