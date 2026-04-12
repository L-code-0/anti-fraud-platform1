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
  'REPORT': {
    path: '/report',
    name: 'Report',
    component: () => import('@/views/Report.vue'),
    meta: { title: '举报中心' }
  },
  'REPORT_ANALYSIS': {
    path: '/report-analysis',
    name: 'ReportAnalysis',
    component: () => import('@/views/ReportAnalysis.vue'),
    meta: { title: '举报数据分析' }
  },
  'REPORT_POINTS': {
    path: '/report-points',
    name: 'ReportPoints',
    component: () => import('@/views/ReportPoints.vue'),
    meta: { title: '举报积分奖励' }
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
  
  // 数据中心
  'DASHBOARD': {
    path: '/dashboard',
    name: 'DataDashboard',
    component: () => import('@/views/DataDashboard.vue'),
    meta: { title: '实时数据大屏' }
  },
  'PERSONAL_REPORT': {
    path: '/personal-report',
    name: 'PersonalReport',
    component: () => import('@/views/PersonalReport.vue'),
    meta: { title: '个性化报告' }
  },
  'RISK_ASSESSMENT': {
    path: '/risk-assessment',
    name: 'RiskAssessment',
    component: () => import('@/views/RiskAssessment.vue'),
    meta: { title: '风险评估模型' }
  },
  'PREDICTION_ANALYSIS': {
    path: '/prediction-analysis',
    name: 'PredictionAnalysis',
    component: () => import('@/views/PredictionAnalysis.vue'),
    meta: { title: '预测分析' }
  },
  // 管理员功能
  'ADMIN_EXPORT': {
    path: '/admin/export',
    name: 'AdminExport',
    component: () => import('@/views/admin/DataExport.vue'),
    meta: { title: '数据导出', requiresAdmin: true }
  },
  'ADMIN_REVIEW': {
    path: '/admin/review',
    name: 'AdminReview',
    component: () => import('@/views/admin/ContentReview.vue'),
    meta: { title: '内容审核', requiresAdmin: true }
  },
  'ADMIN_NOTICE': {
    path: '/admin/notice',
    name: 'AdminNotice',
    component: () => import('@/views/admin/Notice.vue'),
    meta: { title: '系统公告', requiresAdmin: true }
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
  },
  // 特色功能
  'FEATURE_AI_ASSISTANT': {
    path: '/ai-assistant',
    name: 'AIAssistant',
    component: () => import('@/views/AIAssistant.vue'),
    meta: { title: 'AI智能问答' }
  },
  'FEATURE_ADAPTIVE_LEARNING': {
    path: '/adaptive-learning',
    name: 'AdaptiveLearning',
    component: () => import('@/views/AdaptiveLearning.vue'),
    meta: { title: '自适应学习路径' }
  },
  'FEATURE_SCENARIO_REPLAY': {
    path: '/scenario-replay',
    name: 'ScenarioReplay',
    component: () => import('@/views/ScenarioReplay.vue'),
    meta: { title: '情景沉浸回放' }
  },
  'FEATURE_COMMUNITY': {
    path: '/community',
    name: 'Community',
    component: () => import('@/views/Community.vue'),
    meta: { title: '社区互助反诈' }
  },
  'FEATURE_VR_TRAINING': {
    path: '/vr-training',
    name: 'VRTraining',
    component: () => import('@/views/VRTraining.vue'),
    meta: { title: 'VR演练体验' }
  },
  'FEATURE_RISK_PROFILE': {
    path: '/risk-profile',
    name: 'RiskProfile',
    component: () => import('@/views/RiskProfile.vue'),
    meta: { title: '智能风险画像' }
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
    'DASHBOARD', 'PERSONAL_REPORT', 'RISK_ASSESSMENT', 'PREDICTION_ANALYSIS',
    'PROFILE', 'PROFILE_INFO', 'PROFILE_POINTS', 'PROFILE_RANK', 'PROFILE_ACHIEVEMENT', 'PROFILE_MESSAGE', 'PROFILE_FEEDBACK',
    'FEATURE_AI_ASSISTANT', 'FEATURE_ADAPTIVE_LEARNING', 'FEATURE_SCENARIO_REPLAY', 'FEATURE_COMMUNITY', 'FEATURE_VR_TRAINING', 'FEATURE_RISK_PROFILE'
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
    'ADMIN_WORK', 'ADMIN_STATS', 'ADMIN_LOG', 'ADMIN_MONITOR', 'ADMIN_EXPORT', 'ADMIN_REVIEW', 'ADMIN_NOTICE'
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
