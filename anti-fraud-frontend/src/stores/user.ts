import { defineStore } from 'pinia'
import { get, post } from '@/utils/request'
import { SecureStorage, secureStorage, setCsrfToken } from '@/utils/security'
import { ADMIN, EXPERT, SUPER_ADMIN, TEACHER, STUDENT } from '@/constants/roleConstants'
import { getDefaultPermissionsByRole } from '@/utils/menuGenerator'

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
  permissions?: string[]  // 用户权限编码列表
  menus?: any[]  // 用户菜单权限树
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
    const storedUserInfo = secureStorage.getItem<User>('userInfo')
    const storedToken = secureStorage.getItem<string>('token')
    return {
      token: storedToken || '',
      userInfo: storedUserInfo || {} as User
    }
  },
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.userInfo.roleId >= ADMIN,
    isExpert: (state) => state.userInfo.roleId === EXPERT || state.userInfo.roleId === SUPER_ADMIN,
    isTeacher: (state) => state.userInfo.roleId === TEACHER,
    isStudent: (state) => state.userInfo.roleId === STUDENT,
    isSuperAdmin: (state) => state.userInfo.roleId === SUPER_ADMIN,
    
    /**
     * 获取用户权限列表
     */
    permissions: (state) => {
      // 优先使用存储的权限
      if (state.userInfo.permissions && state.userInfo.permissions.length > 0) {
        return state.userInfo.permissions
      }
      // 如果没有，则根据角色生成默认权限
      return getDefaultPermissionsByRole()
    },
    
    /**
     * 获取用户菜单
     */
    menus: (state) => {
      return state.userInfo.menus || []
    }
  },
  
  actions: {
    /**
     * 检查用户是否拥有指定权限
     */
    hasPermission(permissionCode: string): boolean {
      const permissions = this.permissions
      return permissions.includes(permissionCode)
    },
    
    /**
     * 检查用户是否拥有任一指定权限
     */
    hasAnyPermission(permissionCodes: string[]): boolean {
      return permissionCodes.some(code => this.hasPermission(code))
    },
    
    /**
     * 检查用户是否拥有所有指定权限
     */
    hasAllPermissions(permissionCodes: string[]): boolean {
      return permissionCodes.every(code => this.hasPermission(code))
    },
    
    /**
     * 检查用户角色级别是否满足要求
     */
    checkRoleLevel(requiredLevel: number): boolean {
      return this.userInfo.roleId >= requiredLevel
    },
    
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
        level: loginData.level || 1,
        permissions: loginData.permissions || getDefaultPermissionsByRole(),
        menus: loginData.menus || []
      }
      
      console.log('保存的用户信息:', this.userInfo)
      console.log('保存的token:', this.token)
      
      // 使用secureStorage存储token和用户信息
      secureStorage.setItem('token', this.token)
      secureStorage.setItem('userInfo', this.userInfo)
      console.log('Token和用户信息已保存到secureStorage')
      
      // 生成并存储 CSRF token
      setCsrfToken()
      console.log('CSRF token已生成并存储')
      
      return res
    },
    
    async register(params: RegisterParams) {
      const res = await post('/auth/register', params)
      return res
    },
    
    async getUserInfo() {
      const res = await get<User>('/auth/info')
      this.userInfo = {
        ...this.userInfo,
        ...res.data,
        permissions: res.data.permissions || getDefaultPermissionsByRole()
      }
      secureStorage.setItem('userInfo', this.userInfo)
      return res
    },
    
    /**
     * 获取并更新用户权限
     */
    async fetchPermissions() {
      try {
        const res = await get<{ permissions: string[], menus: any[] }>('/permission/user/menus')
        if (res.data) {
          this.userInfo.permissions = res.data.permissions
          this.userInfo.menus = res.data.menus
          secureStorage.setItem('userInfo', this.userInfo)
        }
      } catch (error) {
        console.error('获取权限失败，使用默认权限', error)
        // 使用默认权限
        this.userInfo.permissions = getDefaultPermissionsByRole()
      }
    },
    
    /**
     * 设置用户权限（用于外部初始化）
     */
    setPermissions(permissions: string[]) {
      this.userInfo.permissions = permissions
      secureStorage.setItem('userInfo', this.userInfo)
    },
    
    /**
     * 设置用户菜单
     */
    setMenus(menus: any[]) {
      this.userInfo.menus = menus
      secureStorage.setItem('userInfo', this.userInfo)
    },
    
    /**
     * 设置token
     */
    setToken(token: string) {
      this.token = token
      secureStorage.setItem('token', token)
    },
    
    /**
     * 设置用户信息
     */
    setUserInfo(userInfo: any) {
      this.userInfo = {
        ...this.userInfo,
        ...userInfo
      }
      secureStorage.setItem('userInfo', this.userInfo)
    },
    
    logout() {
      // 使用 $patch 确保状态更新被正确触发
      this.$patch({
        token: '',
        userInfo: {} as User
      })
      
      // 清除secureStorage中的数据
      try {
        secureStorage.removeItem('token')
        secureStorage.removeItem('userInfo')
        secureStorage.removeItem('csrfToken')
        console.log('已清除本地存储的 token、用户信息和 CSRF token')
      } catch (error) {
        console.error('清除本地存储失败:', error)
      }
    }
  }
})




