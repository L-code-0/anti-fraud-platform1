/**
 * 权限注册指令
 * 用于在应用启动时全局注册权限指令
 */
import type { App } from 'vue'
import { permission, permissionAll, permissionRole } from './permission'

/**
 * 注册权限指令到Vue应用
 * @param app Vue应用实例
 */
export function setupPermissionDirective(app: App) {
  // 注册 v-permission 指令
  app.directive('permission', permission)
  
  // 注册 v-permission-all 指令
  app.directive('permission-all', permissionAll)
  
  // 注册 v-permission-role 指令
  app.directive('permission-role', permissionRole)
}

export { permission, permissionAll, permissionRole }
