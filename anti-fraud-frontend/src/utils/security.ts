import CryptoJS from 'crypto-js'

// XSS 防护：过滤用户输入的内容
export function sanitizeInput(input: string): string {
  if (!input) return ''
  return input
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;')
}

// 敏感信息加密存储
export class SecureStorage {
  private static secretKey: string = 'anti-fraud-platform-secret-key'

  // 加密数据
  static encrypt(data: any): string {
    const jsonString = JSON.stringify(data)
    return CryptoJS.AES.encrypt(jsonString, this.secretKey).toString()
  }

  // 解密数据
  static decrypt(encryptedData: string): any {
    try {
      const bytes = CryptoJS.AES.decrypt(encryptedData, this.secretKey)
      const jsonString = bytes.toString(CryptoJS.enc.Utf8)
      return JSON.parse(jsonString)
    } catch (error) {
      console.error('解密失败:', error)
      return null
    }
  }

  // 存储加密数据
  static setItem(key: string, value: any): void {
    try {
      const encryptedValue = this.encrypt(value)
      localStorage.setItem(key, encryptedValue)
    } catch (error) {
      console.error('存储加密数据失败:', error)
    }
  }

  // 获取解密数据
  static getItem(key: string): any {
    try {
      const encryptedValue = localStorage.getItem(key)
      if (!encryptedValue) return null
      return this.decrypt(encryptedValue)
    } catch (error) {
      console.error('获取解密数据失败:', error)
      return null
    }
  }

  // 删除数据
  static removeItem(key: string): void {
    localStorage.removeItem(key)
  }

  // 清空所有数据
  static clear(): void {
    localStorage.clear()
  }
}

// 生成 CSRF token
export function generateCsrfToken(): string {
  return CryptoJS.lib.WordArray.random(16).toString()
}

// 存储 CSRF token
export function setCsrfToken(): void {
  const token = generateCsrfToken()
  SecureStorage.setItem('csrfToken', token)
  document.cookie = `XSRF-TOKEN=${token}; path=/; SameSite=strict`
}

// 获取 CSRF token
export function getCsrfToken(): string {
  return SecureStorage.getItem('csrfToken') || ''
}

// 密码强度检测
export function checkPasswordStrength(password: string): {
  strength: number // 0-4，0表示弱，4表示强
  message: string
} {
  let strength = 0
  let message = ''

  // 长度检查
  if (password.length >= 8) strength++
  if (password.length >= 12) strength++

  // 包含数字
  if (/\d/.test(password)) strength++

  // 包含字母
  if (/[a-zA-Z]/.test(password)) strength++

  // 包含特殊字符
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) strength++

  switch (strength) {
    case 0:
    case 1:
      message = '密码强度：弱'
      break
    case 2:
      message = '密码强度：一般'
      break
    case 3:
      message = '密码强度：良好'
      break
    case 4:
      message = '密码强度：强'
      break
    default:
      message = '密码强度：未知'
  }

  return { strength, message }
}

// 验证邮箱格式
export function validateEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

// 验证手机号格式
export function validatePhone(phone: string): boolean {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}
