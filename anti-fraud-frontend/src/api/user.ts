/**
 * 用户模块 API
 */
import { get, post, put, del } from './request'

// 获取用户列表
export function getUserList(params?: {
  page?: number
  pageSize?: number
  keyword?: string
  roleId?: number
}) {
  return get('/user/list', params)
}

// 获取用户收藏
export function getUserFavorites(params?: {
  page?: number
  pageSize?: number
}) {
  return get('/user/favorites', params)
}

// 添加收藏
export function addFavorite(id: number, type: string) {
  return post('/user/favorites/like', { id, type })
}

// 取消收藏
export function removeFavorite(id: number, type: string) {
  return post('/user/favorites/remove', { id, type })
}

// 获取学习进度
export function getLearningProgress(knowledgeId?: number) {
  return get('/user/learning/progress', { knowledgeId })
}

// 获取错题列表
export function getWrongQuestions(params?: {
  page?: number
  pageSize?: number
}) {
  return get('/user/wrong-questions', params)
}

// 删除错题
export function deleteWrongQuestion(id: number) {
  return post('/user/wrong-questions/delete', { id })
}

// 标记错题已掌握
export function markQuestionMastered(id: number) {
  return post('/user/wrong-questions/mastered', { id })
}

// 获取错题统计
export function getWrongQuestionStats() {
  return get('/user/wrong-questions/stats')
}
