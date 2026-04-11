/**
 * 动态菜单生成器
 * 根据用户权限动态生成菜单
 */
import type { RouteRecordRaw } from 'vue-router'
import { getUserMenus } from '@/api/permission'
import { ADMIN, EXPERT, SUPER_ADMIN, TEACHER } from '@/constants/roleConstants'

/**
 * 菜单配置映射
 * 将权限编码映射到路由配置
 */
const menuRouteMap: Record<string, Partial<RouteRecordRaw>> = {
  // 学习中心
  'STUDY': {
    path: '/knowledge',
    name: 'Knowledge',
    component: () => import('@/views/knowledge/Index.vue'),
    meta: { title: '知识学习' }
  },
  'STUDY_FAVORITES': {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/Favorites.vue'),
    meta: { title: '我的收藏' }
  },
  'STUDY_WRONGBOOK': {
    path: '/wrongbook',
    name: 'WrongBook',
    component: () => import('@/views/WrongBook.vue'),
    meta: { title: '错题本' }
  },
  'STUDY_REPORT': {
    path: '/analysis',
    name: 'Analysis',
    component: () => import('@/views/analysis/Report.vue'),
    meta: { title: '学习报告' }
  },
  
  // 考试中心
  'EXAM_TEST': {
    path: '/test',
    name: 'Test',
    component: () => import('@/views/test/Index.vue'),
    meta: { title: '在线测试' }
  },
  'EXAM_SCORE': {
    path: '/score',
    name: 'Score',
    component: () => import('@/views/score/Index.vue'),
    meta: { title: '成绩查询' }
  },
  
  // 演练中心
  'SIMULATION_SCENE': {
    path: '/simulation',
    name: 'Simulation',
    component: () => import('@/views/simulation/Index.vue'),
    meta: { title: '场景演练' }
  },
  
  // 举报中心
  'REPORT_SUBMIT': {
    path: '/report/submit',
    name: 'ReportSubmit',
    component: () => import('@/views/report/Submit.vue'),
    meta: { title: '提交举报' }
  },
  'REPORT_LIST': {
    path: '/report',
    name: 'Report',
    component: () => import('@/views/report/Index.vue'),
    meta: { title: '举报列表' }
  },
  
  // 活动中心
  'ACTIVITY_LIST': {
    path: '/activity',
    name: 'Activity',
    component: () => import('@/views/activity/Index.vue'),
    meta: { title: '活动列表' }
  },
  
  // 答疑中心
  'QA_LIST': {
    path: '/qa',
    name: 'QA',
    component: () => import('@/views/qa/Index.vue'),
    meta: { title: '在线答疑' }
  },
  
  // 班级管理
  'CLASS_LIST': {
    path: '/class',
    name: 'Class',
    component: () => import('@/views/class/Index.vue'),
    meta: { title: '班级列表', requiresTeacher: true }
  },
  
  // 后台管理
  'ADMIN': {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { title: '后台管理', requiresAdmin: true }
  },
  
  // 专家中心
  'EXPERT': {
    path: '/expert',
    name: 'Expert',
    component: () => import('@/views/expert/Layout.vue'),
    meta: { title: '专家中心', requiresExpert: true }
  },
  
  // 个人中心
  'PROFILE_INFO': {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人资料' }
  },
  'PROFILE_POINTS': {
    path: '/points',
    name: 'Points',
    component: () => import('@/views/points/Index.vue'),
    meta: { title: '积分中心' }
  },
  'PROFILE_RANK': {
    path: '/leaderboard',
    name: 'Leaderboard',
    component: () => import('@/views/Leaderboard.vue'),
    meta: { title: '排行榜' }
  },
  'PROFILE_ACHIEVEMENT': {
    path: '/achievement',
    name: 'Achievement',
    component: () => import('@/views/Achievement.vue'),
    meta: { title: '成就中心' }
  },
  'PROFILE_MESSAGE': {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('@/views/Notifications.vue'),
    meta: { title: '消息中心' }
  },
  'PROFILE_FEEDBACK': {
    path: '/feedback',
    name: 'Feedback',
    component: () => import('@/views/Feedback.vue'),
    meta: { title: '意见反馈' }
  }
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
 * 从权限列表生成路由
 */
export function generateRoutesFromPermissions(permissions: string[]): RouteRecordRaw[] {
  const routes: RouteRecordRaw[] = []
  
  for (const permCode of permissions) {
    const routeConfig = menuRouteMap[permCode]
    if (routeConfig) {
      routes.push(routeConfig as RouteRecordRaw)
    }
  }
  
  return routes
}

/**
 * 根据用户角色获取默认菜单权限
 */
export function getDefaultPermissionsByRole(): string[] {
  const userInfo = getUserInfo()
  const roleId = userInfo.roleId || 1
  
  // 基础权限（所有用户都有）
  const basePermissions = [
    'STUDY', 'STUDY_KNOWLEDGE', 'STUDY_FAVORITES', 'STUDY_WRONGBOOK', 'STUDY_REPORT',
    'EXAM', 'EXAM_TEST', 'EXAM_SCORE',
    'SIMULATION', 'SIMULATION_SCENE',
    'REPORT', 'REPORT_SUBMIT',
    'ACTIVITY', 'ACTIVITY_LIST',
    'QA', 'QA_LIST',
    'PROFILE', 'PROFILE_INFO', 'PROFILE_POINTS', 'PROFILE_RANK', 'PROFILE_ACHIEVEMENT', 'PROFILE_MESSAGE', 'PROFILE_FEEDBACK'
  ]
  
  // 教师权限
  const teacherPermissions = [
    'CLASS', 'CLASS_LIST',
    'CLASS_CREATE', 'CLASS_EDIT', 'CLASS_DELETE', 'CLASS_ADD_STUDENT',
    'WORK_AUDIT', 'WORK_REJECT'
  ]
  
  // 管理员权限
  const adminPermissions = [
    'ADMIN', 'ADMIN_DASHBOARD', 'ADMIN_USER', 'ADMIN_KNOWLEDGE', 'ADMIN_QUESTION',
    'ADMIN_PAPER', 'ADMIN_SCENE', 'ADMIN_REPORT', 'ADMIN_ACTIVITY', 'ADMIN_TASK',
    'ADMIN_WORK', 'ADMIN_STATS', 'ADMIN_LOG', 'ADMIN_MONITOR'
  ]
  
  // 专家权限
  const expertPermissions = [
    'EXPERT', 'EXPERT_HOME', 'EXPERT_ANALYSIS', 'EXPERT_ADVICE',
    'REPORT_LIST', 'REPORT_AUDIT', 'REPORT_HANDLE'
  ]
  
  // 超级管理员权限
  const superAdminPermissions = [
    'ADMIN_SYSTEM_CONFIG',
    'ADMIN_PERMISSION_MANAGE',
    'ADMIN_ROLE_MANAGE'
  ]
  
  let permissions = [...basePermissions]
  
  if (roleId >= TEACHER) {
    permissions = [...permissions, ...teacherPermissions]
  }
  
  if (roleId >= ADMIN) {
    permissions = [...permissions, ...adminPermissions]
  }
  
  if (roleId === EXPERT) {
    permissions = [...permissions, ...expertPermissions]
  }
  
  if (roleId === SUPER_ADMIN) {
    permissions = [...permissions, ...superAdminPermissions]
  }
  
  return permissions
}

/**
 * 获取用户菜单配置
 */
export async function fetchUserMenus(): Promise<RouteRecordRaw[]> {
  try {
    const res = await getUserMenus()
    if (res.data && res.data.length > 0) {
      return generateRoutesFromMenus(res.data)
    }
  } catch (error) {
    console.error('获取用户菜单失败，使用默认权限')
  }
  
  // 获取默认权限
  const defaultPermissions = getDefaultPermissionsByRole()
  return generateRoutesFromPermissions(defaultPermissions)
}

/**
 * 从菜单数据生成路由
 */
function generateRoutesFromMenus(menus: any[]): RouteRecordRaw[] {
  const routes: RouteRecordRaw[] = []
  
  for (const menu of menus) {
    if (menu.path) {
      // 优先使用映射表中的配置
      const mappedRoute = menuRouteMap[menu.code || menu.permission]
      
      if (mappedRoute) {
        routes.push({
          path: menu.path,
          name: menu.name || menu.code,
          component: mappedRoute.component,
          meta: {
            title: menu.name,
            icon: menu.icon,
            requiresAuth: true
          }
        } as RouteRecordRaw)
      } else if (menu.component) {
        // 如果映射表中没有，尝试使用静态导入
        // 使用 @vite-ignore 抑制警告
        routes.push({
          path: menu.path,
          name: menu.name || menu.code,
          component: () => import(/* @vite-ignore */ `@/${menu.component}.vue`),
          meta: {
            title: menu.name,
            icon: menu.icon,
            requiresAuth: true
          }
        } as RouteRecordRaw)
      }
    }
    
    // 递归处理子菜单
    if (menu.children && menu.children.length > 0) {
      const childRoutes = generateRoutesFromMenus(menu.children)
      routes.push(...childRoutes)
    }
  }
  
  return routes
}

export default {
  generateRoutesFromPermissions,
  getDefaultPermissionsByRole,
  fetchUserMenus
}
