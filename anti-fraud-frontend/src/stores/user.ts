import { defineStore } from 'pinia'
import { get, post } from '@/utils/request'
import { SecureStorage, setCsrfToken } from '@/utils/security'
import { ADMIN, EXPERT, SUPER_ADMIN, TEACHER, STUDENT } from '@/constants/roleConstants'

interface User {
  id: number
  userId?: number  // 后端返回的字段名
  username: string
  realName: string
  avatar: string
  phone?: string
  email?: string
  roleId: number
  roleName?: string
  points: number
  level: number
}

interface LoginParams {
  username: string
  password: string
}

interface RegisterParams {
  username: string
  password: string
  realName: string
  phone: string
  roleId: number
}

export const useUserStore = defineStore('user', {
  state: () => {
    const storedUserInfo = localStorage.getItem('userInfo')
    let userInfo: User = {} as User
    if (storedUserInfo && storedUserInfo !== 'undefined' && storedUserInfo !== 'null') {
      try {
        userInfo = JSON.parse(storedUserInfo) as User
      } catch {
        userInfo = {} as User
      }
    }
    return {
      token: localStorage.getItem('token') || '',
      userInfo
    }
  },
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.userInfo.roleId >= ADMIN,
    isExpert: (state) => state.userInfo.roleId === EXPERT || state.userInfo.roleId === SUPER_ADMIN,
    isTeacher: (state) => state.userInfo.roleId === TEACHER,
    isStudent: (state) => state.userInfo.roleId === STUDENT
  },
  
  actions: {
    async login(username: string, password: string) {
      const res = await post<any>('/auth/login', { username, password })
      console.log('登录响应:', res)
      
      // 后端返回的 data 直接是 LoginVO，包含 token 和 userId
      const loginData = res.data
      
      if (!loginData) {
        throw new Error('登录失败：未获取到用户信息')
      }
      
      // 保存 token
      this.token = loginData.token || ''
      
      // 保存用户信息
      this.userInfo = {
        id: loginData.userId || loginData.id || 0,
        username: loginData.username || '',
        realName: loginData.realName || '',
        avatar: loginData.avatar || '',
        roleId: loginData.roleId || 0,
        roleName: loginData.roleName || '',
        points: loginData.points || 0,
        level: loginData.level || 1
      }
      
      console.log('保存的用户信息:', this.userInfo)
      console.log('保存的token:', this.token)
      
      // 确保localStorage可用
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem('token', this.token)
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        console.log('Token和用户信息已保存到localStorage')
        
        // 生成并存储 CSRF token
        setCsrfToken()
        console.log('CSRF token已生成并存储')
      } else {
        console.error('localStorage不可用')
      }
      
      return res
    },
    
    async register(params: RegisterParams) {
      const res = await post('/auth/register', params)
      return res
    },
    
    async getUserInfo() {
      const res = await get<User>('/auth/info')
      this.userInfo = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
      return res
    },
    
    logout() {
      // 使用 $patch 确保状态更新被正确触发
      this.$patch({
        token: '',
        userInfo: {} as User
      })
      
      // 确保 localStorage 操作在 try-catch 中
      try {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        SecureStorage.removeItem('csrfToken')
        console.log('已清除本地存储的 token、用户信息和 CSRF token')
      } catch (error) {
        console.error('清除本地存储失败:', error)
      }
    }
  }
})



