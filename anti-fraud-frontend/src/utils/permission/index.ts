/**
 * 权限管理工具类
 * 提供前端权限相关的工具函数
 */
import { useUserStore } from '@/stores/user'
import { ADMIN, EXPERT, SUPER_ADMIN, TEACHER, STUDENT } from '@/constants/roleConstants'

/**
 * 权限编码常量
 */
export const PermissionCode = {
  // 学习中心
  STUDY: 'STUDY',
  STUDY_KNOWLEDGE: 'STUDY_KNOWLEDGE',
  STUDY_FAVORITES: 'STUDY_FAVORITES',
  STUDY_WRONGBOOK: 'STUDY_WRONGBOOK',
  STUDY_REPORT: 'STUDY_REPORT',

  // 考试中心
  EXAM: 'EXAM',
  EXAM_TEST: 'EXAM_TEST',
  EXAM_SCORE: 'EXAM_SCORE',

  // 演练中心
  SIMULATION: 'SIMULATION',
  SIMULATION_SCENE: 'SIMULATION_SCENE',

  // 举报中心
  REPORT: 'REPORT',
  REPORT_SUBMIT: 'REPORT_SUBMIT',
  REPORT_LIST: 'REPORT_LIST',
  REPORT_AUDIT: 'REPORT_AUDIT',
  REPORT_HANDLE: 'REPORT_HANDLE',

  // 活动中心
  ACTIVITY: 'ACTIVITY',
  ACTIVITY_LIST: 'ACTIVITY_LIST',
  ACTIVITY_CREATE: 'ACTIVITY_CREATE',
  ACTIVITY_EDIT: 'ACTIVITY_EDIT',
  ACTIVITY_DELETE: 'ACTIVITY_DELETE',

  // 答疑中心
  QA: 'QA',
  QA_LIST: 'QA_LIST',

  // 班级管理
  CLASS: 'CLASS',
  CLASS_LIST: 'CLASS_LIST',
  CLASS_CREATE: 'CLASS_CREATE',
  CLASS_EDIT: 'CLASS_EDIT',
  CLASS_DELETE: 'CLASS_DELETE',
  CLASS_ADD_STUDENT: 'CLASS_ADD_STUDENT',

  // 后台管理
  ADMIN: 'ADMIN',
  ADMIN_DASHBOARD: 'ADMIN_DASHBOARD',
  ADMIN_USER: 'ADMIN_USER',
  ADMIN_KNOWLEDGE: 'ADMIN_KNOWLEDGE',
  ADMIN_QUESTION: 'ADMIN_QUESTION',
  ADMIN_PAPER: 'ADMIN_PAPER',
  ADMIN_SCENE: 'ADMIN_SCENE',
  ADMIN_REPORT: 'ADMIN_REPORT',
  ADMIN_ACTIVITY: 'ADMIN_ACTIVITY',
  ADMIN_TASK: 'ADMIN_TASK',
  ADMIN_WORK: 'ADMIN_WORK',
  ADMIN_STATS: 'ADMIN_STATS',
  ADMIN_LOG: 'ADMIN_LOG',
  ADMIN_MONITOR: 'ADMIN_MONITOR',

  // 专家中心
  EXPERT: 'EXPERT',
  EXPERT_HOME: 'EXPERT_HOME',
  EXPERT_ANALYSIS: 'EXPERT_ANALYSIS',
  EXPERT_ADVICE: 'EXPERT_ADVICE',

  // 个人中心
  PROFILE: 'PROFILE',
  PROFILE_INFO: 'PROFILE_INFO',
  PROFILE_POINTS: 'PROFILE_POINTS',
  PROFILE_RANK: 'PROFILE_RANK',
  PROFILE_ACHIEVEMENT: 'PROFILE_ACHIEVEMENT',
  PROFILE_MESSAGE: 'PROFILE_MESSAGE',
  PROFILE_FEEDBACK: 'PROFILE_FEEDBACK',

  // 按钮权限
  KNOWLEDGE_ADD: 'KNOWLEDGE_ADD',
  KNOWLEDGE_EDIT: 'KNOWLEDGE_EDIT',
  KNOWLEDGE_DELETE: 'KNOWLEDGE_DELETE',
  KNOWLEDGE_PUBLISH: 'KNOWLEDGE_PUBLISH',
  USER_ADD: 'USER_ADD',
  USER_EDIT: 'USER_EDIT',
  USER_DELETE: 'USER_DELETE',
  USER_RESET_PWD: 'USER_RESET_PWD',
  USER_ASSIGN_ROLE: 'USER_ASSIGN_ROLE',
  WORK_AUDIT: 'WORK_AUDIT',
  WORK_REJECT: 'WORK_REJECT',
} as const

/**
 * 权限工具类
 */
export const permissionUtils = {
  /**
   * 检查用户是否拥有指定权限
   * @param permissionCode 权限编码
   * @returns 是否拥有
   */
  hasPermission(permissionCode: string): boolean {
    const userStore = useUserStore()
    if (!userStore.userInfo || !userStore.userInfo.permissions) {
      return false
    }
    return userStore.userInfo.permissions.includes(permissionCode)
  },

  /**
   * 检查用户是否拥有所有指定权限
   * @param permissionCodes 权限编码数组
   * @returns 是否拥有全部
   */
  hasAllPermissions(permissionCodes: string[]): boolean {
    return permissionCodes.every(code => this.hasPermission(code))
  },

  /**
   * 检查用户是否拥有任一指定权限
   * @param permissionCodes 权限编码数组
   * @returns 是否拥有任一
   */
  hasAnyPermission(permissionCodes: string[]): boolean {
    return permissionCodes.some(code => this.hasPermission(code))
  },

  /**
   * 检查用户角色级别是否满足要求
   * @param requiredLevel 需要的最低级别
   * @returns 是否满足
   */
  checkRoleLevel(requiredLevel: number): boolean {
    const userStore = useUserStore()
    if (!userStore.userInfo || !userStore.userInfo.roleId) {
      return false
    }
    return userStore.userInfo.roleId >= requiredLevel
  },

  /**
   * 检查是否为学生
   */
  isStudent(): boolean {
    const userStore = useUserStore()
    return userStore.userInfo.roleId === STUDENT
  },

  /**
   * 检查是否为教师或更高
   */
  isTeacherOrAbove(): boolean {
    const userStore = useUserStore()
    return userStore.userInfo.roleId >= TEACHER
  },

  /**
   * 检查是否为管理员或更高
   */
  isAdminOrAbove(): boolean {
    const userStore = useUserStore()
    return userStore.userInfo.roleId >= ADMIN
  },

  /**
   * 检查是否为专家或更高
   */
  isExpertOrAbove(): boolean {
    const userStore = useUserStore()
    return userStore.userInfo.roleId >= EXPERT
  },

  /**
   * 检查是否为超级管理员
   */
  isSuperAdmin(): boolean {
    const userStore = useUserStore()
    return userStore.userInfo.roleId === SUPER_ADMIN
  },

  /**
   * 获取用户角色名称
   */
  getRoleName(): string {
    const userStore = useUserStore()
    if (!userStore.userInfo || !userStore.userInfo.roleId) {
      return ''
    }
    const roleNames: Record<number, string> = {
      [STUDENT]: '学生',
      [TEACHER]: '教师',
      [ADMIN]: '管理员',
      [EXPERT]: '反诈专家',
      [SUPER_ADMIN]: '系统管理员'
    }
    return roleNames[userStore.userInfo.roleId] || '未知'
  }
}

/**
 * 角色级别映射
 */
export const RoleLevel = {
  STUDENT: 1,
  TEACHER: 2,
  ADMIN: 3,
  EXPERT: 4,
  SUPER_ADMIN: 5
} as const

/**
 * 角色级别名称映射
 */
export const RoleLevelNames: Record<number, string> = {
  [RoleLevel.STUDENT]: '学生',
  [RoleLevel.TEACHER]: '教师',
  [RoleLevel.ADMIN]: '管理员',
  [RoleLevel.EXPERT]: '反诈专家',
  [RoleLevel.SUPER_ADMIN]: '系统管理员'
}

export default permissionUtils
