/**
 * 增强版路由守卫
 * 支持基于权限的路由访问控制
 */
import type { RouteLocationNormalized } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ADMIN, EXPERT, SUPER_ADMIN, TEACHER } from '@/constants/roleConstants'
import { permissionUtils } from '@/utils/permission'

/**
 * 路由元信息扩展接口
 */
interface ExtendedRouteMeta {
  title?: string
  requiresAuth?: boolean
  requiresAdmin?: boolean
  requiresExpert?: boolean
  requiresTeacher?: boolean
  requiresPermission?: string | string[]
  requiresRole?: string | string[]
  requiresRoleLevel?: number
}

/**
 * 获取用户信息
 */
function getUserInfo() {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr && userInfoStr !== 'undefined' && userInfoStr !== 'null') {
    try {
      return JSON.parse(userInfoStr)
    } catch {
      return {}
    }
  }
  return {}
}

/**
 * 路由守卫配置
 */
export function setupRouteGuards(router: any) {
  router.beforeEach(async (
    to: RouteLocationNormalized,
    _from: RouteLocationNormalized,
    next: any
  ) => {
    // 设置页面标题
    document.title = `${(to.meta as ExtendedRouteMeta)?.title || '反诈平台'} - 高校反诈安全知识普及平台`
    
    const token = localStorage.getItem('token')
    const userInfo = getUserInfo()
    const roleId = userInfo.roleId || 0
    
    // ==================== 登录验证 ====================
    if ((to.meta as ExtendedRouteMeta)?.requiresAuth && !token) {
      ElMessage.warning('请先登录后再访问')
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
    
    // ==================== 角色级别验证 ====================
    const meta = to.meta as ExtendedRouteMeta
    
    // 需要超级管理员权限
    if (roleId < SUPER_ADMIN && isSuperAdminRoute(to)) {
      ElMessage.error('权限不足，仅系统管理员可访问')
      next('/')
      return
    }
    
    // 需要管理员权限
    if (meta.requiresAdmin && roleId < ADMIN) {
      ElMessage.error('权限不足，无法访问管理后台')
      next('/')
      return
    }
    
    // 需要专家权限
    if (meta.requiresExpert && roleId !== EXPERT && roleId !== SUPER_ADMIN) {
      ElMessage.error('权限不足，无法访问专家中心')
      next('/')
      return
    }
    
    // 需要教师权限
    if (meta.requiresTeacher && roleId < TEACHER) {
      ElMessage.error('权限不足，无法访问班级管理')
      next('/')
      return
    }
    
    // ==================== 自定义权限验证 ====================
    // 需要特定权限
    if (meta.requiresPermission) {
      const permissions = Array.isArray(meta.requiresPermission) 
        ? meta.requiresPermission 
        : [meta.requiresPermission]
      
      const hasPermission = permissions.some(p => permissionUtils.hasPermission(p))
      
      if (!hasPermission) {
        ElMessage.error('权限不足，无法访问该功能')
        next('/')
        return
      }
    }
    
    // ==================== 自定义角色验证 ====================
    // 需要特定角色
    if (meta.requiresRole) {
      const roles = Array.isArray(meta.requiresRole) 
        ? meta.requiresRole 
        : [meta.requiresRole]
      
      const hasRole = checkUserRole(roles)
      
      if (!hasRole) {
        ElMessage.error('角色权限不足，无法访问该功能')
        next('/')
        return
      }
    }
    
    // 需要最低角色级别
    if (meta.requiresRoleLevel && roleId < meta.requiresRoleLevel) {
      ElMessage.error('角色权限不足')
      next('/')
      return
    }
    
    next()
  })
  
  // 路由后置守卫
  router.afterEach(() => {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  })
}

/**
 * 检查用户是否拥有指定角色
 */
function checkUserRole(roles: string[]): boolean {
  const userInfo = getUserInfo()
  const roleId = userInfo.roleId || 0
  
  // 角色编码到ID的映射
  const roleCodeToId: Record<string, number> = {
    'STUDENT': 1,
    'TEACHER': 2,
    'ADMIN': 3,
    'EXPERT': 4,
    'SUPER_ADMIN': 5
  }
  
  return roles.some(role => {
    const requiredRoleId = roleCodeToId[role]
    return requiredRoleId && roleId >= requiredRoleId
  })
}

/**
 * 检查是否为仅超级管理员可访问的路由
 */
function isSuperAdminRoute(to: RouteLocationNormalized): boolean {
  const superAdminRoutes = [
    '/admin/system-config',
    '/admin/permission-manage',
    '/admin/role-manage',
    '/admin/log-view'
  ]
  
  return superAdminRoutes.some(path => to.path.startsWith(path))
}

export default setupRouteGuards
