/**
 * 班级模块 API
 */
import { get, post } from './request'

// 获取班级列表
export function getClassList(params?: {
  page?: number
  pageSize?: number
  keyword?: string
}) {
  return get('/class/list', params)
}

// 获取班级详情
export function getClassDetail(id: number) {
  return get(`/class/${id}`)
}

// 加入班级
export function joinClass(classCode: string) {
  return post('/class/join', { classCode })
}

// 退出班级
export function leaveClass(id: number) {
  return post('/class/leave', { id })
}

// 获取班级成员
export function getClassMembers(classId: number) {
  return get(`/class/${classId}/members`)
}
