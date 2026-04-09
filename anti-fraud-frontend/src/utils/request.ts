import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { getCsrfToken, SecureStorage } from './security'

const baseURL = '/api'

const service: AxiosInstance = axios.create({
  baseURL,
  timeout: 15000 // 增加超时时间到15秒
})

// 请求缓存
interface CacheItem {
  data: any
  timestamp: number
}
const requestCache = new Map<string, CacheItem>()
const DEFAULT_CACHE_TIME = 5 * 60 * 1000 // 默认缓存5分钟

// 清除过期缓存
function cleanExpiredCache() {
  const now = Date.now()
  for (const [key, value] of requestCache.entries()) {
    if (now - value.timestamp > DEFAULT_CACHE_TIME) {
      requestCache.delete(key)
    }
  }
}

// 定期清理过期缓存
setInterval(cleanExpiredCache, 60000)

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 添加 CSRF token
    const csrfToken = getCsrfToken()
    if (csrfToken) {
      config.headers['X-CSRF-Token'] = csrfToken
    }
    
    // 确保localStorage可用
    if (typeof localStorage !== 'undefined') {
      const token = localStorage.getItem('token')
      console.log('请求拦截器 - token:', token)
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
        console.log('请求拦截器 - 添加token到请求头')
      }
    } else {
      console.error('请求拦截器 - localStorage不可用')
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    
    if (res.code !== 200) {
      // 根据错误码显示不同的提示
      const errorMessage = res.message || '请求失败'
      
      switch (res.code) {
        case 400:
          ElMessage.warning(errorMessage || '请求参数错误')
          break
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          SecureStorage.removeItem('csrfToken')
          // 延迟跳转，让用户看到提示
          setTimeout(() => {
            window.location.href = '/login'
          }, 1500)
          break
        case 403:
          ElMessage.error('权限不足，无法访问')
          break
        case 404:
          ElMessage.warning('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误，请稍后重试')
          break
        default:
          ElMessage.error(errorMessage)
      }
      
      return Promise.reject(new Error(errorMessage))
    }
    
    return res
  },
  (error) => {
    // 细化网络错误提示
    let errorMessage = '网络错误'
    
    if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      errorMessage = '请求超时，请检查网络后重试'
    } else if (error.message === 'Network Error') {
      errorMessage = '网络连接失败，请检查网络设置'
    } else if (error.response) {
      // 服务器返回了错误响应
      const status = error.response.status
      switch (status) {
        case 400:
          errorMessage = '请求参数错误'
          break
        case 401:
          errorMessage = '登录已过期，请重新登录'
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          SecureStorage.removeItem('csrfToken')
          setTimeout(() => {
            window.location.href = '/login'
          }, 1500)
          break
        case 403:
          errorMessage = '权限不足，无法访问'
          break
        case 404:
          errorMessage = '请求的资源不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        case 502:
          errorMessage = '网关错误，服务暂时不可用'
          break
        case 503:
          errorMessage = '服务暂时不可用，请稍后重试'
          break
        case 504:
          errorMessage = '网关超时，请稍后重试'
          break
        default:
          errorMessage = `请求失败 (${status})`
      }
    }
    
    ElMessage.error(errorMessage)
    console.error('[API Error]', error)
    return Promise.reject(error)
  }
)

// API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 通用请求方法
export function request<T = any>(config: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service(config)
}

// 带缓存的GET请求
export function getWithCache<T = any>(
  url: string, 
  params?: any, 
  cacheTime = DEFAULT_CACHE_TIME
): Promise<ApiResponse<T>> {
  const cacheKey = `${url}?${JSON.stringify(params)}`
  const cached = requestCache.get(cacheKey)
  
  if (cached && Date.now() - cached.timestamp < cacheTime) {
    return Promise.resolve({ code: 200, message: 'success', data: cached.data })
  }
  
  return get<T>(url, params).then(res => {
    requestCache.set(cacheKey, { data: res.data, timestamp: Date.now() })
    return res
  })
}

export function get<T = any>(url: string, params?: any): Promise<ApiResponse<T>> {
  return request({ method: 'GET', url, params })
}

export function post<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
  return request({ method: 'POST', url, data })
}

export function put<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
  return request({ method: 'PUT', url, data })
}

export function del<T = any>(url: string): Promise<ApiResponse<T>> {
  return request({ method: 'DELETE', url })
}

export default service
