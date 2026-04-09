/**
 * 作品模块 API
 */
import { get, post } from './request'

// 获取作品列表
export function getWorkList(params?: {
  page?: number
  pageSize?: number
  category?: string
  keyword?: string
}) {
  return get('/works/list', params)
}

// 获取作品详情
export function getWorkDetail(id: number) {
  return get(`/works/${id}`)
}

// 提交作品
export function submitWork(data: {
  title: string
  content: string
  category: string
  tags?: string[]
}) {
  return post('/works/submit', data)
}

// 更新作品
export function updateWork(id: number, data: {
  title?: string
  content?: string
  tags?: string[]
}) {
  return post(`/works/update/${id}`, data)
}

// 删除作品
export function deleteWork(id: number) {
  return post('/works/delete', { id })
}

// 点赞作品
export function likeWork(id: number) {
  return post('/works/like', { id })
}

// 取消点赞
export function unlikeWork(id: number) {
  return post('/works/unlike', { id })
}
