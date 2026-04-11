/**
 * 用户认证模块 API
 */
import { get, post } from './request'

// ============================================
// 登录相关
// ============================================

/**
 * 登录
 */
export function login(data: {
  username: string
  password: string
  captchaKey?: string
  captchaCode?: string
}) {
  return post('/auth/login', data)
}

/**
 * 获取登录安全状态
 */
export function getLoginSecurityStatus(username: string) {
  return get('/auth/login-security-status', { username })
}

/**
 * 刷新Token
 */
export function refreshToken(refreshToken: string) {
  return post('/auth/refresh', { refreshToken })
}

/**
 * 强制所有设备登出
 */
export function logoutAllDevices() {
  return post('/auth/logout-all-devices')
}

// ============================================
// 注册相关
// ============================================

/**
 * 注册
 */
export function register(data: {
  username: string
  password: string
  confirmPassword: string
  realName: string
  phone: string
  email?: string
  userNo?: string
  departmentId?: number
  classId?: number
  emailCode?: string
  agreement?: boolean
}) {
  return post('/auth/register', data)
}

/**
 * 验证用户名是否可用
 */
export function checkUsername(username: string) {
  return get('/auth/check-username', { username })
}

// ============================================
// 登出
// ============================================

/**
 * 登出
 */
export function logout() {
  return post('/auth/logout')
}

// ============================================
// 用户信息
// ============================================

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return get('/auth/info')
}

// ============================================
// 验证码
// ============================================

/**
 * 获取图形验证码
 */
export function getCaptcha() {
  return get('/auth/captcha')
}

/**
 * 发送短信验证码
 */
export function sendSmsCode(phone: string) {
  return post('/auth/sms-code', { phone })
}

/**
 * 发送邮箱验证码
 */
export function sendEmailCode(email: string) {
  return post('/auth/email-code', { email })
}

/**
 * 验证邮箱验证码
 */
export function verifyEmailCode(email: string, code: string) {
  return post('/auth/verify-email-code', { email, code })
}

// ============================================
// 密码安全
// ============================================

/**
 * 检查密码强度
 */
export function checkPasswordStrength(password: string, username?: string) {
  return post('/auth/check-password', { password, username })
}
