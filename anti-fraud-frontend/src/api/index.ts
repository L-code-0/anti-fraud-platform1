/**
 * API 模块统一导出
 * 
 * 使用方式：
 * import { get, post } from '@/api/request'  // 直接使用请求方法
 * import { login, getUserInfo } from '@/api/auth'  // 使用具体模块API
 * import * as Api from '@/api'  // 导入所有API
 */

// 导出请求核心方法
export {
  request,
  get,
  post,
  put,
  del,
  getWithCache,
  default as service
} from './request'

export type { ApiResponse } from './request'

// 导出各模块API
export * as auth from './auth'
export * as user from './user'
export * as knowledge from './knowledge'
export * as feedback from './feedback'
export * as achievement from './achievement'
export * as notification from './notification'
export * as activity from './activity'
export * as warning from './warning'
export * as points from './points'
export * as qa from './qa'
export * as report from './report'
export * as simulation from './simulation'
export * as task from './task'
export * as test from './test'
export * as classApi from './class'
export * as works from './works'
export * as analysis from './analysis'
export * as expert from './expert'
export * as admin from './admin'
export * as ai from './ai'
