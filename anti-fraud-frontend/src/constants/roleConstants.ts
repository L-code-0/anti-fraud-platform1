/**
 * 角色常量定义
 * 用于替代魔法数字，提高代码可读性
 * 
 * 角色ID对应关系：
 * 1 - 学生 (STUDENT)
 * 2 - 教师 (TEACHER)
 * 3 - 管理员 (ADMIN)
 * 4 - 反诈专家 (EXPERT)
 * 5 - 系统管理员 (SUPER_ADMIN)
 */

/** 学生角色ID */
export const STUDENT = 1

/** 教师角色ID */
export const TEACHER = 2

/** 管理员角色ID */
export const ADMIN = 3

/** 反诈专家角色ID */
export const EXPERT = 4

/** 系统管理员角色ID */
export const SUPER_ADMIN = 5

/**
 * 角色ID常量集合
 */
export const RoleIds = {
  STUDENT,
  TEACHER,
  ADMIN,
  EXPERT,
  SUPER_ADMIN
} as const

/**
 * 角色名称映射
 */
export const RoleNames: Record<number, string> = {
  [STUDENT]: '学生',
  [TEACHER]: '教师',
  [ADMIN]: '管理员',
  [EXPERT]: '反诈专家',
  [SUPER_ADMIN]: '系统管理员'
}

/**
 * 检查是否为管理员或更高权限（管理员、专家、系统管理员）
 * @param roleId 角色ID
 */
export function isAdminOrHigher(roleId: number): boolean {
  return roleId >= ADMIN
}

/**
 * 检查是否为专家或更高权限（专家、系统管理员）
 * @param roleId 角色ID
 */
export function isExpertOrHigher(roleId: number): boolean {
  return roleId === EXPERT || roleId === SUPER_ADMIN
}

/**
 * 检查是否为教师或更高权限（教师、管理员、专家、系统管理员）
 * @param roleId 角色ID
 */
export function isTeacherOrHigher(roleId: number): boolean {
  return roleId >= TEACHER
}
