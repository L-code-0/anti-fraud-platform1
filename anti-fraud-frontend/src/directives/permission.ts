/**
 * 权限指令
 * 用于在模板中控制元素的显示/隐藏
 * 
 * 使用方式：
 * <template>
 *   <!-- 需要特定权限 -->
 *   <button v-permission="'USER_ADD'">新增用户</button>
 *   
 *   <!-- 需要多个权限之一 -->
 *   <button v-permission="['USER_ADD', 'USER_EDIT']">操作</button>
 *   
 *   <!-- 需要所有权限 -->
 *   <button v-permission.all="['PERMISSION_A', 'PERMISSION_B']">操作</button>
 * </template>
 */
import type { Directive, DirectiveBinding } from 'vue'
import { permissionUtils } from '@/utils/permission'

/**
 * v-permission 指令
 * 单一权限指令，满足任一权限即显示
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string[]>) {
    const { value } = binding
    
    if (!value) {
      return
    }
    
    // 支持单个权限或权限数组
    const permissions = Array.isArray(value) ? value : [value]
    
    // 检查是否有任一权限
    const hasPermission = permissionUtils.hasAnyPermission(permissions)
    
    if (!hasPermission) {
      // 移除元素或隐藏
      if (el.parentNode) {
        el.parentNode.removeChild(el)
      } else {
        el.style.display = 'none'
      }
    }
  }
}

/**
 * v-permission-all 指令
 * 需要满足所有权限才显示
 */
export const permissionAll: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string[]>) {
    const { value } = binding
    
    if (!value || !Array.isArray(value)) {
      return
    }
    
    // 检查是否拥有所有权限
    const hasAllPermissions = permissionUtils.hasAllPermissions(value)
    
    if (!hasAllPermissions) {
      if (el.parentNode) {
        el.parentNode.removeChild(el)
      } else {
        el.style.display = 'none'
      }
    }
  }
}

/**
 * v-permission-role 指令
 * 基于角色的显示控制
 */
export const permissionRole: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<{ role: string | string[], mode?: 'any' | 'all' }>) {
    const { value } = binding
    const userStore = (window as any).__pinia?.state?.value?.user
    
    if (!value || !userStore?.userInfo?.roleId) {
      el.style.display = 'none'
      return
    }
    
    const roleId = userStore.userInfo.roleId
    const roles = Array.isArray(value.role) ? value.role : [value.role]
    const mode = value.mode || 'any'
    
    let hasRole = false
    
    // 角色编码到角色ID的映射
    const roleCodeToId: Record<string, number> = {
      'STUDENT': 1,
      'TEACHER': 2,
      'ADMIN': 3,
      'EXPERT': 4,
      'SUPER_ADMIN': 5
    }
    
    if (mode === 'any') {
      hasRole = roles.some((role: string) => {
        const requiredRoleId = roleCodeToId[role]
        return requiredRoleId && roleId >= requiredRoleId
      })
    } else {
      hasRole = roles.every((role: string) => {
        const requiredRoleId = roleCodeToId[role]
        return requiredRoleId && roleId >= requiredRoleId
      })
    }
    
    if (!hasRole) {
      if (el.parentNode) {
        el.parentNode.removeChild(el)
      } else {
        el.style.display = 'none'
      }
    }
  }
}

/**
 * 导出所有权限指令
 */
export const permissionDirectives = {
  permission,
  'permission-all': permissionAll,
  'permission-role': permissionRole
}

export default permissionDirectives
