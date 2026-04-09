/**
 * 用户认证模块 API
 */
import { get, post } from './request'

// 登录
export function login(username: string, password: string, captchaKey?: string, captchaCode?: string) {
  return post('/auth/login', { username, password, captchaKey, captchaCode })
}

// 注册
export function register(data: {
  username: string
  password: string
  realName: string
  phone: string
  roleId: number
}) {
  return post('/auth/register', data)
}

// 登出
export function logout() {
  return post('/auth/logout')
}

// 获取当前用户信息
export function getUserInfo() {
  return get('/auth/info')
}

// 获取图形验证码
export function getCaptcha() {
  return get('/auth/captcha')
}
