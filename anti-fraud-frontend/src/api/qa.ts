/**
 * 问答模块 API
 */
import { get, post } from './request'

// 获取问答列表
export function getQAList(params?: {
  page?: number
  pageSize?: number
  keyword?: string
  categoryId?: number
}) {
  return get('/qa/list', params)
}

// 获取问答详情
export function getQADetail(id: number) {
  return get(`/qa/${id}`)
}

// 提交问答
export function submitQuestion(data: {
  title: string
  content: string
  categoryId?: number
  tags?: string[]
}) {
  return post('/qa/submit', data)
}

// 回答问题
export function submitAnswer(questionId: number, content: string) {
  return post('/qa/answer', { questionId, content })
}
