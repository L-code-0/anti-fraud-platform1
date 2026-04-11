import CryptoJS from 'crypto-js'

// ============================================
// XSS防护：过滤用户输入的内容
// ============================================

/**
 * XSS防护：过滤用户输入的内容
 * 将危险字符转换为HTML实体
 */
export function sanitizeInput(input: string): string {
  if (!input) return ''
  return input
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;')
}

/**
 * 解码HTML实体
 */
export function decodeHtmlEntities(text: string): string {
  const textarea = document.createElement('textarea')
  textarea.innerHTML = text
  return textarea.value
}

/**
 * 移除所有HTML标签
 */
export function stripHtmlTags(input: string): string {
  if (!input) return ''
  return input.replace(/<[^>]*>/g, '')
}

// ============================================
// 密码安全
// ============================================

/**
 * 弱密码黑名单
 */
const WEAK_PASSWORD_BLACKLIST = new Set([
  'password', '123456', '12345678', 'qwerty', 'abc123', 'monkey', '1234567',
  'letmein', 'trustno1', 'dragon', 'baseball', 'iloveyou', 'master', 'sunshine',
  'ashley', 'bailey', 'shadow', '123123', '654321', 'superman', 'qazwsx',
  'michael', 'football', 'password1', 'password123', 'welcome', 'welcome1',
  'admin', 'admin123', 'admin888', 'root', 'root123', 'pass', 'pass123',
  'test', 'test123', 'guest', 'guest123', 'login', 'login123',
  '123456789', '1234567890', '000000', '111111', '222222', '333333',
  'passw0rd', 'p@ssword', 'p@ssw0rd', 'qwer1234', 'asdf1234', 'zxcv1234',
  'iloveyou', '5201314', '1314520', 'woaini', 'zhongguo'
])

/**
 * 密码强度等级
 */
export interface PasswordStrength {
  score: number // 0-4
  level: 'very-weak' | 'weak' | 'fair' | 'strong' | 'very-strong'
  color: string
  label: string
}

/**
 * 检查密码强度
 */
export function checkPasswordStrength(password: string): PasswordStrength {
  let score = 0
  
  // 长度检查
  if (password.length >= 8) score++
  if (password.length >= 12) score++
  if (password.length >= 16) score++

  // 字符类型检查
  if (/[A-Z]/.test(password)) score++
  if (/[a-z]/.test(password)) score++
  if (/[0-9]/.test(password)) score++
  if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) score++

  // 混合检查
  const typeCount = [
    /[A-Z]/.test(password),
    /[a-z]/.test(password),
    /[0-9]/.test(password),
    /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)
  ].filter(Boolean).length

  if (typeCount >= 3) score++
  if (typeCount >= 4) score++

  // 转换为等级
  let level: PasswordStrength['level']
  let color: string
  let label: string

  if (score <= 2) {
    level = 'very-weak'
    color = '#c53030'
    label = '极弱'
  } else if (score <= 4) {
    level = 'weak'
    color = '#dd6b20'
    label = '弱'
  } else if (score <= 6) {
    level = 'fair'
    color = '#d69e2e'
    label = '一般'
  } else if (score <= 8) {
    level = 'strong'
    color = '#38a169'
    label = '较强'
  } else {
    level = 'very-strong'
    color = '#2f855a'
    label = '强'
  }

  return { score, level, color, label }
}

/**
 * 检查是否为弱密码
 */
export function isWeakPassword(password: string): boolean {
  const lowerPassword = password.toLowerCase()
  return WEAK_PASSWORD_BLACKLIST.has(lowerPassword)
}

/**
 * 检查密码是否包含键盘序列
 */
export function containsKeyboardPattern(password: string): boolean {
  const patterns = [
    '123456', '654321', 'qwerty', 'asdfgh', 'zxcvbn',
    'qazwsx', '1qaz', '2wsx', '3edc', 'abcdef'
  ]
  const lower = password.toLowerCase()
  return patterns.some(pattern => lower.includes(pattern))
}

/**
 * 检查密码是否包含用户名
 */
export function containsUsername(password: string, username: string): boolean {
  if (!username) return false
  return password.toLowerCase().includes(username.toLowerCase())
}

/**
 * 详细的密码检查结果
 */
export interface PasswordCheckResult {
  passed: boolean
  message: string
}

export function getPasswordCheckResults(password: string, username?: string): PasswordCheckResult[] {
  const results: PasswordCheckResult[] = []

  // 长度检查
  results.push({
    passed: password.length >= 8,
    message: '至少8个字符'
  })

  // 大写字母检查
  results.push({
    passed: /[A-Z]/.test(password),
    message: '包含大写字母'
  })

  // 小写字母检查
  results.push({
    passed: /[a-z]/.test(password),
    message: '包含小写字母'
  })

  // 数字检查
  results.push({
    passed: /[0-9]/.test(password),
    message: '包含数字'
  })

  // 弱密码检查
  results.push({
    passed: !isWeakPassword(password),
    message: '不在弱密码黑名单中'
  })

  // 键盘序列检查
  results.push({
    passed: !containsKeyboardPattern(password),
    message: '不包含键盘序列'
  })

  // 用户名检查
  if (username) {
    results.push({
      passed: !containsUsername(password, username),
      message: '不包含用户名'
    })
  }

  return results
}

/**
 * 验证密码格式（用于注册）
 */
export function validatePasswordFormat(password: string): { valid: boolean; message: string } {
  if (!password) {
    return { valid: false, message: '密码不能为空' }
  }
  
  if (password.length < 8) {
    return { valid: false, message: '密码长度至少8位' }
  }

  if (password.length > 32) {
    return { valid: false, message: '密码长度不能超过32位' }
  }

  if (isWeakPassword(password)) {
    return { valid: false, message: '密码太常见，请使用更复杂的密码' }
  }

  if (!/[A-Z]/.test(password)) {
    return { valid: false, message: '密码必须包含大写字母' }
  }

  if (!/[a-z]/.test(password)) {
    return { valid: false, message: '密码必须包含小写字母' }
  }

  if (!/[0-9]/.test(password)) {
    return { valid: false, message: '密码必须包含数字' }
  }

  return { valid: true, message: '密码格式正确' }
}

// ============================================
// 敏感信息加密存储
// ============================================

export class SecureStorage {
  private secretKey: string

  constructor() {
    // 生产环境应从安全的地方获取密钥
    this.secretKey = import.meta.env.VITE_APP_SECRET_KEY || 'anti-fraud-platform-default-key'
  }

  /**
   * 加密数据
   */
  encrypt(data: any): string {
    const jsonString = JSON.stringify(data)
    return CryptoJS.AES.encrypt(jsonString, this.secretKey).toString()
  }

  /**
   * 解密数据
   */
  decrypt(encryptedData: string): any {
    try {
      const bytes = CryptoJS.AES.decrypt(encryptedData, this.secretKey)
      const jsonString = bytes.toString(CryptoJS.enc.Utf8)
      return JSON.parse(jsonString)
    } catch (error) {
      console.error('解密失败:', error)
      return null
    }
  }

  /**
   * 存储加密数据
   */
  setItem(key: string, value: any): void {
    try {
      const encryptedValue = this.encrypt(value)
      localStorage.setItem(key, encryptedValue)
    } catch (error) {
      console.error('存储加密数据失败:', error)
    }
  }

  /**
   * 获取解密数据
   */
  getItem<T = any>(key: string): T | null {
    try {
      const encryptedValue = localStorage.getItem(key)
      if (!encryptedValue) return null
      return this.decrypt(encryptedValue) as T
    } catch (error) {
      console.error('获取解密数据失败:', error)
      return null
    }
  }

  /**
   * 删除数据
   */
  removeItem(key: string): void {
    localStorage.removeItem(key)
  }

  /**
   * 清空所有数据
   */
  clear(): void {
    localStorage.clear()
  }
}

export const secureStorage = new SecureStorage()

// ============================================
// CSRF Token管理
// ============================================

/**
 * 生成CSRF token
 */
export function generateCsrfToken(): string {
  return CryptoJS.lib.WordArray.random(16).toString()
}

/**
 * 存储CSRF token
 */
export function setCsrfToken(): string {
  const token = generateCsrfToken()
  // 存储到Cookie
  document.cookie = `XSRF-TOKEN=${token}; path=/; SameSite=strict; Secure`
  // 同时存储到sessionStorage作为备份
  sessionStorage.setItem('csrfToken', token)
  return token
}

/**
 * 获取CSRF token
 */
export function getCsrfToken(): string {
  // 优先从Cookie获取
  const cookies = document.cookie.split(';')
  for (const cookie of cookies) {
    const [name, value] = cookie.trim().split('=')
    if (name === 'XSRF-TOKEN') {
      return value
    }
  }
  // 备选：从sessionStorage获取
  return sessionStorage.getItem('csrfToken') || ''
}

/**
 * 验证CSRF token
 */
export function validateCsrfToken(token: string): boolean {
  const storedToken = getCsrfToken()
  return token === storedToken && token.length > 0
}

// ============================================
// 设备指纹
// ============================================

/**
 * 生成设备指纹
 */
export function generateDeviceFingerprint(): string {
  const components = [
    navigator.userAgent,
    navigator.language,
    screen.width,
    screen.height,
    screen.colorDepth,
    new Date().getTimezoneOffset(),
    navigator.hardwareConcurrency || 'unknown',
    navigator.platform
  ]
  
  const fingerprint = components.join('|')
  return CryptoJS.SHA256(fingerprint).toString()
}

/**
 * 获取或创建设备ID
 */
export function getDeviceId(): string {
  const DEVICE_ID_KEY = 'device_id'
  let deviceId = sessionStorage.getItem(DEVICE_ID_KEY)
  
  if (!deviceId) {
    deviceId = generateDeviceFingerprint()
    sessionStorage.setItem(DEVICE_ID_KEY, deviceId)
  }
  
  return deviceId
}

// ============================================
// 输入验证
// ============================================

/**
 * 验证用户名格式
 */
export function isValidUsername(username: string): boolean {
  if (!username || username.length < 3 || username.length > 20) {
    return false
  }
  // 只允许字母、数字、下划线
  return /^[a-zA-Z0-9_]+$/.test(username)
}

/**
 * 验证手机号格式
 */
export function isValidPhone(phone: string): boolean {
  if (!phone) return false
  return /^1[3-9]\d{9}$/.test(phone)
}

/**
 * 验证邮箱格式
 */
export function isValidEmail(email: string): boolean {
  if (!email) return false
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return emailRegex.test(email)
}

/**
 * 验证验证码格式
 */
export function isValidCaptchaCode(code: string, length: number = 4): boolean {
  if (!code) return false
  return new RegExp(`^\\d{${length}}$`).test(code)
}

// ============================================
// 敏感信息脱敏
// ============================================

/**
 * 脱敏手机号
 */
export function maskPhone(phone: string): string {
  if (!phone || phone.length < 11) return phone
  return phone.substring(0, 3) + '****' + phone.substring(7)
}

/**
 * 脱敏邮箱
 */
export function maskEmail(email: string): string {
  if (!email || !email.includes('@')) return email
  const [prefix, suffix] = email.split('@')
  if (prefix.length <= 3) {
    return prefix.charAt(0) + '***@' + suffix
  }
  return prefix.substring(0, 3) + '***@' + suffix
}

/**
 * 脱敏姓名
 */
export function maskName(name: string): string {
  if (!name || name.length < 2) return name
  return name.charAt(0) + '*'.repeat(name.length - 1)
}

/**
 * 脱敏身份证号
 */
export function maskIdCard(idCard: string): string {
  if (!idCard || idCard.length < 10) return idCard
  return idCard.substring(0, 6) + '********' + idCard.substring(14)
}

// ============================================
// 安全工具函数
// ============================================

/**
 * 安全地解析JSON
 */
export function safeJsonParse<T = any>(jsonString: string): T | null {
  try {
    return JSON.parse(jsonString)
  } catch (error) {
    console.error('JSON解析失败:', error)
    return null
  }
}

/**
 * 清除敏感数据
 */
export function clearSensitiveData(): void {
  secureStorage.clear()
  sessionStorage.clear()
  document.cookie.split(';').forEach(cookie => {
    const [name] = cookie.trim().split('=')
    if (name) {
      document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`
    }
  })
}

/**
 * 检测是否在iframe中
 */
export function isInIframe(): boolean {
  try {
    return window.self !== window.top
  } catch (e) {
    return true
  }
}

/**
 * 检测是否为移动设备
 */
export function isMobileDevice(): boolean {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

/**
 * 获取客户端IP（通过第三方服务）
 */
export async function getClientIP(): Promise<string | null> {
  try {
    const response = await fetch('https://api.ipify.org?format=json')
    if (response.ok) {
      const data = await response.json()
      return data.ip
    }
  } catch (error) {
    console.error('获取客户端IP失败:', error)
  }
  return null
}
