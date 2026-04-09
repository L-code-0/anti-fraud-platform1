/**
 * 测验模块 API
 */
import { get, post } from './request'

// 获取试卷列表
export function getPaperList(params?: {
  page?: number
  pageSize?: number
  category?: string
  difficulty?: string
}) {
  return get('/test/paper-list', params)
}

// 获取试卷详情
export function getPaperDetail(id: number) {
  return get(`/test/paper/${id}`)
}

// 开始测验
export function startTest(paperId: number) {
  return post('/test/start', { paperId })
}

// 提交测验答案
export function submitTest(testId: number, answers: any[]) {
  return post('/test/submit', { testId, answers })
}

// 获取测验结果
export function getTestResult(testId: number) {
  return get(`/test/result/${testId}`)
}

// 获取我的测验历史
export function getMyTestHistory(params?: {
  page?: number
  pageSize?: number
}) {
  return get('/test/history', params)
}
