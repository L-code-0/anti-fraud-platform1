/**
 * 举报模块 API
 */
import { get, post } from './request'

// 获取举报列表
export function getReportList(params?: {
  page?: number
  pageSize?: number
  status?: string
}) {
  return get('/report/list', params)
}

// 获取我的举报
export function getMyReportList(params?: {
  page?: number
  pageSize?: number
}) {
  return get('/report/my-list', params)
}

// 提交举报
export function submitReport(data: {
  type: string
  content: string
  evidence?: string
  contact?: string
}) {
  return post('/report/submit', data)
}

// 获取举报详情
export function getReportDetail(id: number) {
  return get(`/report/${id}`)
}
