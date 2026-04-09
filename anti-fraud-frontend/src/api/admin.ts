/**
 * 后台管理模块 API
 */
import { get, post, put, del } from './request'

// ========== 用户管理 ==========
export function adminGetUserList(params?: any) {
  return get('/admin/user/list', params)
}

export function adminGetUserDetail(id: number) {
  return get(`/admin/user/${id}`)
}

export function adminUpdateUser(id: number, data: any) {
  return put(`/admin/user/${id}`, data)
}

export function adminDeleteUser(id: number) {
  return del(`/admin/user/${id}`)
}

export function adminDisableUser(id: number) {
  return post('/admin/user/disable', { id })
}

// ========== 知识库管理 ==========
export function adminGetKnowledgeList(params?: any) {
  return get('/admin/knowledge/list', params)
}

export function adminCreateKnowledge(data: any) {
  return post('/admin/knowledge/create', data)
}

export function adminUpdateKnowledge(id: number, data: any) {
  return put(`/admin/knowledge/${id}`, data)
}

export function adminDeleteKnowledge(id: number) {
  return del(`/admin/knowledge/${id}`)
}

// ========== 活动管理 ==========
export function adminGetActivityList(params?: any) {
  return get('/admin/activity/list', params)
}

export function adminCreateActivity(data: any) {
  return post('/admin/activity/create', data)
}

export function adminUpdateActivity(id: number, data: any) {
  return put(`/admin/activity/${id}`, data)
}

export function adminDeleteActivity(id: number) {
  return del(`/admin/activity/${id}`)
}

// ========== 任务管理 ==========
export function adminGetTaskList(params?: any) {
  return get('/admin/task/list', params)
}

export function adminCreateTask(data: any) {
  return post('/admin/task/create', data)
}

export function adminUpdateTask(id: number, data: any) {
  return put(`/admin/task/${id}`, data)
}

export function adminDeleteTask(id: number) {
  return del(`/admin/task/${id}`)
}

// ========== 预警管理 ==========
export function adminGetWarningList(params?: any) {
  return get('/admin/warning/list', params)
}

export function adminCreateWarning(data: any) {
  return post('/admin/warning/create', data)
}

export function adminUpdateWarning(id: number, data: any) {
  return put(`/admin/warning/${id}`, data)
}

export function adminDeleteWarning(id: number) {
  return del(`/admin/warning/${id}`)
}

// ========== 举报管理 ==========
export function adminGetReportList(params?: any) {
  return get('/admin/report/list', params)
}

export function adminProcessReport(id: number, data: any) {
  return post(`/admin/report/${id}/process`, data)
}

// ========== 测验管理 ==========
export function adminGetQuestionList(params?: any) {
  return get('/admin/questions/list', params)
}

export function adminCreateQuestion(data: any) {
  return post('/admin/questions/create', data)
}

export function adminUpdateQuestion(id: number, data: any) {
  return put(`/admin/questions/${id}`, data)
}

export function adminDeleteQuestion(id: number) {
  return del(`/admin/questions/${id}`)
}

export function adminGetPaperList(params?: any) {
  return get('/admin/papers/list', params)
}

export function adminCreatePaper(data: any) {
  return post('/admin/papers/create', data)
}

export function adminUpdatePaper(id: number, data: any) {
  return put(`/admin/papers/${id}`, data)
}

export function adminDeletePaper(id: number) {
  return del(`/admin/papers/${id}`)
}

// ========== 场景管理 ==========
export function adminGetSceneList(params?: any) {
  return get('/admin/scenes/list', params)
}

export function adminCreateScene(data: any) {
  return post('/admin/scenes/create', data)
}

export function adminUpdateScene(id: number, data: any) {
  return put(`/admin/scenes/${id}`, data)
}

export function adminDeleteScene(id: number) {
  return del(`/admin/scenes/${id}`)
}

// ========== 作品管理 ==========
export function adminGetWorkList(params?: any) {
  return get('/admin/works/list', params)
}

export function adminAuditWork(id: number, result: string) {
  return post(`/admin/works/${id}/audit`, { result })
}

export function adminDeleteWork(id: number) {
  return del(`/admin/works/${id}`)
}

// ========== 数据统计 ==========
export function adminGetStats() {
  return get('/admin/stats')
}

export function adminGetUserStats(params?: any) {
  return get('/admin/stats/user', params)
}

export function adminGetActivityStats(params?: any) {
  return get('/admin/stats/activity', params)
}

export function adminGetLearningStats(params?: any) {
  return get('/admin/stats/learning', params)
}

// ========== 仪表盘 ==========
export function adminGetDashboard() {
  return get('/admin/dashboard')
}
