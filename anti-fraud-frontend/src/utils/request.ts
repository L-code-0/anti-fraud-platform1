/**
 * API 请求模块入口
 * 
 * @deprecated 请使用 @/api/index 导入
 * 此文件仅为向后兼容保留，建议迁移到新的 API 模块结构
 */
export { 
  request, 
  get, 
  post, 
  put, 
  del, 
  getWithCache,
  default as service 
} from '@/api/request'

export type { ApiResponse } from '@/api/request'
