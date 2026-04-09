/**
 * 知识库模块 API
 */
import { get, post, getWithCache } from './request'

// 获取知识列表
export function getKnowledgeList(params?: {
  page?: number
  pageSize?: number
  categoryId?: number
  keyword?: string
  contentType?: number
  sortBy?: string
  tags?: string[]
}) {
  return get('/knowledge/list', params)
}

// 获取知识详情
export function getKnowledgeDetail(id: number) {
  return get(`/knowledge/${id}`)
}

// 获取分类列表
export function getCategories() {
  return getWithCache('/knowledge/categories', undefined, 10 * 60 * 1000) // 缓存10分钟
}

// 获取标签列表
export function getTags() {
  return getWithCache('/knowledge/tags', undefined, 10 * 60 * 1000) // 缓存10分钟
}

// 获取热门知识
export function getHotKnowledge() {
  return getWithCache('/knowledge/hot', undefined, 5 * 60 * 1000) // 缓存5分钟
}

// 获取推荐知识
export function getRecommendedKnowledge() {
  return get('/knowledge/recommended')
}

// 获取个性化推荐
export function getPersonalizedRecommendation() {
  return get('/knowledge/personalized')
}

// 获取学习历史
export function getLearningHistory(params?: {
  page?: number
  pageSize?: number
}) {
  return get('/knowledge/history', params)
}

// 获取知识统计
export function getKnowledgeStats() {
  return get('/knowledge/stats')
}
