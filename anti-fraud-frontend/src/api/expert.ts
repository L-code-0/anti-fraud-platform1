/**
 * 专家模块 API
 */
import { get } from './request'

// 获取专家列表
export function getExpertList(params?: {
  page?: number
  pageSize?: number
  keyword?: string
  specialty?: string
}) {
  return get('/expert/list', params)
}

// 获取专家详情
export function getExpertDetail(id: number) {
  return get(`/expert/${id}`)
}

// 获取专家文章
export function getExpertArticles(expertId: number) {
  return get(`/expert/${expertId}/articles`)
}

// 获取专家问答
export function getExpertQA(expertId: number) {
  return get(`/expert/${expertId}/qa`)
}
