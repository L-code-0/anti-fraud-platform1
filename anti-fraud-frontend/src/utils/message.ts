/**
 * 统一消息提示封装
 * 提供友好的错误信息展示和统一处理
 */
import { ElMessage, ElNotification, ElMessageBox } from 'element-plus'
import type { MessageOptions, NotificationParams } from 'element-plus'

// 错误码映射（将技术错误转换为用户友好信息）
const ERROR_MESSAGES: Record<string, string> = {
  'NETWORK_ERROR': '网络连接失败，请检查网络设置',
  'TIMEOUT': '请求超时，请稍后重试',
  'SERVER_ERROR': '服务器开小差啦，请稍后再试',
  'UNAUTHORIZED': '登录已过期，请重新登录',
  'FORBIDDEN': '您没有权限进行此操作',
  'NOT_FOUND': '请求的资源不存在',
  'VALIDATION_ERROR': '输入信息有误，请检查后重试',
  'UNKNOWN': '操作失败，请稍后重试'
}

// 友好错误消息映射
const FRIENDLY_ERRORS: Record<string, string> = {
  '401': '登录已过期，请重新登录',
  '403': '您没有权限进行此操作',
  '404': '请求的资源不存在',
  '500': '服务器开小差啦，请稍后重试',
  '502': '服务器正在维护，请稍后重试',
  '503': '服务暂时不可用，请稍后重试',
  'ECONNREFUSED': '无法连接到服务器',
  'ETIMEDOUT': '请求超时，请检查网络'
}

// 提取错误信息
export function extractErrorMessage(error: unknown): string {
  if (!error) return ERROR_MESSAGES.UNKNOWN
  
  // 处理字符串错误
  if (typeof error === 'string') {
    return error
  }
  
  // 处理 Error 对象
  if (error instanceof Error) {
    const message = error.message
    
    // 检查是否为 HTTP 状态码错误
    for (const [code, msg] of Object.entries(FRIENDLY_ERRORS)) {
      if (message.includes(code)) {
        return msg
      }
    }
    
    // 检查是否为已知错误类型
    for (const [type, msg] of Object.entries(ERROR_MESSAGES)) {
      if (message.toUpperCase().includes(type)) {
        return msg
      }
    }
    
    // 返回原始错误消息
    return message
  }
  
  // 处理响应对象 { message, code, msg }
  if (typeof error === 'object') {
    const err = error as Record<string, unknown>
    
    // 优先级: message > msg > code > 默认
    if (typeof err.message === 'string' && err.message) {
      return extractErrorMessage(err.message)
    }
    if (typeof err.msg === 'string' && err.msg) {
      return err.msg as string
    }
    if (typeof err.code === 'string' && err.code) {
      return ERROR_MESSAGES[err.code.toUpperCase()] || ERROR_MESSAGES.UNKNOWN
    }
    
    // 检查后端返回的 data 字段
    if (err.data && typeof err.data === 'object') {
      const data = err.data as Record<string, unknown>
      if (typeof data.message === 'string') {
        return data.message
      }
    }
  }
  
  return ERROR_MESSAGES.UNKNOWN
}

// 统一消息提示服务
export const MessageService = {
  // 成功提示
  success(message: string, options?: MessageOptions) {
    return ElMessage({
      message,
      type: 'success',
      duration: 3000,
      showClose: true,
      ...options
    })
  },
  
  // 警告提示
  warning(message: string, options?: MessageOptions) {
    return ElMessage({
      message,
      type: 'warning',
      duration: 4000,
      showClose: true,
      ...options
    })
  },
  
  // 错误提示
  error(messageOrError: string | unknown, options?: MessageOptions) {
    const message = typeof messageOrError === 'string' 
      ? messageOrError 
      : extractErrorMessage(messageOrError)
    
    return ElMessage({
      message,
      type: 'error',
      duration: 5000,
      showClose: true,
      ...options
    })
  },
  
  // 信息提示
  info(message: string, options?: MessageOptions) {
    return ElMessage({
      message,
      type: 'info',
      duration: 3000,
      showClose: true,
      ...options
    })
  },
  
  // 成功通知（带图标和更多内容）
  notifySuccess(params: NotificationParams) {
    return ElNotification({
      type: 'success',
      title: '操作成功',
      duration: 4500,
      ...params
    })
  },
  
  // 错误通知
  notifyError(params: NotificationParams) {
    return ElNotification({
      type: 'error',
      title: '操作失败',
      duration: 0, // 错误不自动关闭
      ...params
    })
  },
  
  // 警告通知
  notifyWarning(params: NotificationParams) {
    return ElNotification({
      type: 'warning',
      title: '注意',
      duration: 5000,
      ...params
    })
  },
  
  // 确认对话框
  confirm(options: {
    title?: string
    message: string
    confirmText?: string
    cancelText?: string
    type?: 'warning' | 'error' | 'info' | 'success'
  }): Promise<'confirm' | 'cancel'> {
    return ElMessageBox.confirm(options.message, options.title || '确认操作', {
      confirmButtonText: options.confirmText || '确定',
      cancelButtonText: options.cancelText || '取消',
      type: options.type || 'warning'
    }).then(() => 'confirm').catch(() => 'cancel')
  },
  
  // 包装异步函数，自动处理错误
  async wrap<T>(
    fn: () => Promise<T>,
    options?: {
      successMessage?: string
      errorMessage?: string
      showError?: boolean
      showSuccess?: boolean
    }
  ): Promise<T | undefined> {
    try {
      const result = await fn()
      if (options?.showSuccess && options?.successMessage) {
        MessageService.success(options.successMessage)
      }
      return result
    } catch (error) {
      if (options?.showError !== false) {
        MessageService.error(options?.errorMessage || error)
      }
      throw error
    }
  }
}

// 错误边界组件的错误处理钩子
export function useErrorHandler() {
  const handleError = (error: unknown, context?: string) => {
    const message = extractErrorMessage(error)
    console.error(`[Error${context ? ` in ${context}` : ''}]:`, error)
    
    // 根据错误类型选择通知方式
    if (String(error).includes('401')) {
      MessageService.warning('登录已过期，请重新登录')
      // 可以在这里添加跳转登录页的逻辑
    } else if (String(error).includes('403')) {
      MessageService.warning('您没有权限进行此操作')
    } else if (String(error).includes('NETWORK') || String(error).includes('ECONNREFUSED')) {
      MessageService.error('网络连接失败，请检查网络设置')
    } else {
      MessageService.error(message)
    }
  }
  
  return { handleError }
}

export default MessageService
