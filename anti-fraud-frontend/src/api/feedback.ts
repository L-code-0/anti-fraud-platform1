/**
 * 反馈模块 API
 */
import { get, post } from './request'

// 获取我的反馈列表
export function getMyFeedbackList(params?: {
  page?: number
  pageSize?: number
}) {
  return get('/feedback/my-list', params)
}

// 提交反馈
export function submitFeedback(data: {
  type: string
  content: string
  images?: string[]
  contact?: string
}) {
  return post('/feedback/submit', data)
}

// 删除反馈
export function deleteFeedback(id: number) {
  return post('/feedback/delete', { id })
}
