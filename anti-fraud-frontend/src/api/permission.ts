/**
 * 权限管理API
 */
import { get, post } from './request'

/**
 * 权限响应结构
 */
export interface Permission {
  id: number
  name: string
  code: string
  type: number
  typeName?: string
  parentId: number
  path?: string
  component?: string
  icon?: string
  sort: number
  status: number
  visible?: number
  keepAlive?: number
  description?: string
  children?: Permission[]
}

/**
 * 角色响应结构
 */
export interface Role {
  id: number
  name: string
  code: string
  level: number
  description?: string
  status: number
  dataScope?: number
  permissionIds?: number[]
}

/**
 * 获取当前用户的菜单权限树
 */
export function getUserMenus() {
  return get<Permission[]>('/permission/user/menus')
}

/**
 * 获取所有菜单权限树（管理员用）
 */
export function getAllMenus() {
  return get<Permission[]>('/permission/menus/tree')
}

/**
 * 获取角色列表
 */
export function getRoles(params?: { pageNum?: number; pageSize?: number }) {
  return get<{ list: Role[]; total: number }>('/role/all', params)
}

/**
 * 获取所有角色
 */
export function getAllRoles() {
  return get<Role[]>('/role/all')
}

/**
 * 获取角色详情
 */
export function getRoleDetail(id: number) {
  return get<Role>(`/role/${id}`)
}

/**
 * 获取角色的权限ID列表
 */
export function getRolePermissions(roleId: number) {
  return get<number[]>(`/permission/role/${roleId}`)
}

/**
 * 分配权限给角色
 */
export function assignPermissions(roleId: number, permissionIds: number[]) {
  return post(`/role/${roleId}/permissions`, permissionIds)
}

/**
 * 检查用户是否拥有指定权限
 */
export function checkPermission(code: string) {
  return get<boolean>('/permission/check', { code })
}

/**
 * 获取用户权限编码列表
 */
export function getUserPermissions() {
  return get<string[]>('/permission/user/codes')
}

/**
 * 权限类型映射
 */
export const PermissionTypeMap = {
  1: '菜单',
  2: '按钮',
  3: '接口'
}

/**
 * 权限状态映射
 */
export const PermissionStatusMap = {
  0: '禁用',
  1: '启用'
}
