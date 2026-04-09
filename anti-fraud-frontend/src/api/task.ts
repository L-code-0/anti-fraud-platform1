/**
 * 任务模块 API
 */
import { get, post } from './request'

// 获取任务列表
export function getTaskList(params?: {
  page?: number
  pageSize?: number
  status?: string
  type?: string
}) {
  return get('/task/list', params)
}

// 获取我的任务
export function getMyTaskList(params?: {
  page?: number
  pageSize?: number
  status?: string
}) {
  return get('/task/my-list', params)
}

// 获取任务详情
export function getTaskDetail(id: number) {
  return get(`/task/${id}`)
}

// 领取任务
export function claimTask(id: number) {
  return post('/task/claim', { id })
}

// 提交任务
export function submitTask(id: number, data: any) {
  return post('/task/submit', { id, ...data })
}

// 完成任务
export function completeTask(id: number) {
  return post('/task/complete', { id })
}
