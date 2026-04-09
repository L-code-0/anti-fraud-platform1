/**
 * 模拟演练模块 API
 */
import { get, post } from './request'

// 获取演练场景列表
export function getSimulationList(params?: {
  page?: number
  pageSize?: number
  category?: string
}) {
  return get('/simulation/list', params)
}

// 获取演练场景详情
export function getSimulationDetail(id: number) {
  return get(`/simulation/${id}`)
}

// 开始演练
export function startSimulation(id: number) {
  return post('/simulation/start', { id })
}

// 完成演练
export function completeSimulation(id: number, result: any) {
  return post('/simulation/complete', { id, result })
}

// 获取演练结果
export function getSimulationResult(id: number) {
  return get(`/simulation/result/${id}`)
}
