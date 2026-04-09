/**
 * 积分模块 API
 */
import { get } from './request'

// 获取积分详情
export function getPointsDetail() {
  return get('/points/detail')
}

// 获取积分历史
export function getPointsHistory(params?: {
  page?: number
  pageSize?: number
}) {
  return get('/points/history', params)
}

// 获取积分规则
export function getPointsRules() {
  return get('/points/rules')
}
