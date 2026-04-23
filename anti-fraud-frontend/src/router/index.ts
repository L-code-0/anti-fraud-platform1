import * as VueRouter from 'vue-router'
import type { RouteRecordRaw, NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ADMIN, EXPERT, SUPER_ADMIN, TEACHER } from '@/constants/roleConstants'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  // 知识学习
  {
    path: '/knowledge',
    name: 'Knowledge',
    component: () => import('@/views/knowledge/Index.vue'),
    meta: { title: '反诈知识' }
  },
  {
    path: '/note',
    name: 'Note',
    component: () => import('@/views/Note.vue'),
    meta: { title: '学习笔记' }
  },
  {
    path: '/checkin',
    name: 'Checkin',
    component: () => import('@/views/Checkin.vue'),
    meta: { title: '学习打卡' }
  },
  {
    path: '/offline',
    name: 'OfflineDownload',
    component: () => import('@/views/OfflineDownload.vue'),
    meta: { title: '离线下载' }
  },
  {
    path: '/questions',
    name: 'QuestionManagement',
    component: () => import('@/views/QuestionManagement.vue'),
    meta: { title: '题目管理' }
  },
  {
    path: '/adaptive-test',
    name: 'AdaptiveTest',
    component: () => import('@/views/AdaptiveTest.vue'),
    meta: { title: '自适应测试' }
  },
  {
    path: '/test-report',
    name: 'TestReport',
    component: () => import('@/views/TestReport.vue'),
    meta: { title: '测试报告' }
  },
  {
    path: '/challenge',
    name: 'Challenge',
    component: () => import('@/views/Challenge.vue'),
    meta: { title: '挑战赛' }
  },
  {
    path: '/case',
    name: 'CaseView',
    component: () => import('@/views/CaseView.vue'),
    meta: { title: '真实案例回放' }
  },
  {
    path: '/fraud-script',
    name: 'FraudScript',
    component: () => import('@/views/FraudScript.vue'),
    meta: { title: '诈骗话术库' }
  },
  {
    path: '/roleplay',
    name: 'RolePlay',
    component: () => import('@/views/RolePlay.vue'),
    meta: { title: '角色扮演' }
  },
  {
    path: '/analysis',
    name: 'Analysis',
    component: () => import('@/views/Analysis.vue'),
    meta: { title: '演练对比分析' }
  },
  {
    path: '/team',
    name: 'Team',
    component: () => import('@/views/Team.vue'),
    meta: { title: '团队协作演练' }
  },
  {
    path: '/vr-drill',
    name: 'VrDrill',
    component: () => import('@/views/VrDrill.vue'),
    meta: { title: 'VR/沉浸式演练' }
  },
  {
    path: '/report',
    name: 'Report',
    component: () => import('@/views/Report.vue'),
    meta: { title: '举报中心' }
  },
  {
    path: '/report-analysis',
    name: 'ReportAnalysis',
    component: () => import('@/views/ReportAnalysis.vue'),
    meta: { title: '举报数据分析' }
  },
  {
    path: '/report-points',
    name: 'ReportPoints',
    component: () => import('@/views/ReportPoints.vue'),
    meta: { title: '举报积分奖励' }
  },
  {
    path: '/dashboard',
    name: 'DataDashboard',
    component: () => import('@/views/DataDashboard.vue'),
    meta: { title: '实时数据大屏' }
  },
  {
    path: '/personal-report',
    name: 'PersonalReport',
    component: () => import('@/views/PersonalReport.vue'),
    meta: { title: '个性化报告' }
  },
  {
    path: '/risk-assessment',
    name: 'RiskAssessment',
    component: () => import('@/views/RiskAssessment.vue'),
    meta: { title: '风险评估模型' }
  },
  {
    path: '/prediction-analysis',
    name: 'PredictionAnalysis',
    component: () => import('@/views/PredictionAnalysis.vue'),
    meta: { title: '预测分析' }
  },
  {
    path: '/knowledge/:id',
    name: 'KnowledgeDetail',
    component: () => import('@/views/knowledge/Detail.vue'),
    meta: { title: '知识详情', requiresAuth: true }
  },
  // 测试考试
  {
    path: '/test',
    name: 'Test',
    component: () => import('@/views/test/Index.vue'),
    meta: { title: '测试考试', requiresAuth: true }
  },
  {
    path: '/test/:id',
    name: 'TestPaper',
    component: () => import('@/views/test/Paper.vue'),
    meta: { title: '答题', requiresAuth: true }
  },
  // 演练模拟
  {
    path: '/simulation',
    name: 'Simulation',
    component: () => import('@/views/simulation/Index.vue'),
    meta: { title: '演练模拟', requiresAuth: true }
  },
  {
    path: '/simulation/:id',
    name: 'SimulationDetail',
    component: () => import('@/views/simulation/Detail.vue'),
    meta: { title: '演练详情', requiresAuth: true }
  },
  // 举报预警
  {
    path: '/report',
    name: 'Report',
    component: () => import('@/views/report/Index.vue'),
    meta: { title: '举报预警', requiresAuth: true }
  },
  {
    path: '/report/submit',
    name: 'ReportSubmit',
    component: () => import('@/views/report/Submit.vue'),
    meta: { title: '提交举报', requiresAuth: true }
  },
  // 积分中心
  {
    path: '/points',
    name: 'Points',
    component: () => import('@/views/points/Index.vue'),
    meta: { title: '积分中心', requiresAuth: true }
  },
  // 活动
  {
    path: '/activity',
    name: 'Activity',
    component: () => import('@/views/activity/Index.vue'),
    meta: { title: '活动中心', requiresAuth: true }
  },
  {
    path: '/activity/:id',
    name: 'ActivityDetail',
    component: () => import('@/views/activity/Detail.vue'),
    meta: { title: '活动详情', requiresAuth: true }
  },
  // 在线答疑
  {
    path: '/qa',
    name: 'QA',
    component: () => import('@/views/qa/Index.vue'),
    meta: { title: '在线答疑', requiresAuth: true }
  },
  // 学习报告
  {
    path: '/analysis',
    name: 'Analysis',
    component: () => import('@/views/analysis/Report.vue'),
    meta: { title: '学习报告', requiresAuth: true }
  },
  // 数据分析中心
  {
    path: '/analysis/data',
    name: 'DataAnalysis',
    component: () => import('@/views/analysis/DataAnalysis.vue'),
    meta: { title: '数据分析', requiresAuth: true }
  },
  // 个人中心
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  // 错题本
  {
    path: '/wrongbook',
    name: 'WrongBook',
    component: () => import('@/views/WrongBook.vue'),
    meta: { title: '错题本', requiresAuth: true }
  },
  // 我的收藏
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/Favorites.vue'),
    meta: { title: '我的收藏', requiresAuth: true }
  },
  // 帮助中心
  {
    path: '/help',
    name: 'Help',
    component: () => import('@/views/Help.vue'),
    meta: { title: '帮助中心' }
  },
  // 消息中心
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('@/views/Notifications.vue'),
    meta: { title: '消息中心', requiresAuth: true }
  },
  // 排行榜
  {
    path: '/leaderboard',
    name: 'Leaderboard',
    component: () => import('@/views/Leaderboard.vue'),
    meta: { title: '排行榜', requiresAuth: true }
  },
  // 成就中心
  {
    path: '/achievement',
    name: 'Achievement',
    component: () => import('@/views/Achievement.vue'),
    meta: { title: '成就中心', requiresAuth: true }
  },
  // 意见反馈
  {
    path: '/feedback',
    name: 'Feedback',
    component: () => import('@/views/Feedback.vue'),
    meta: { title: '意见反馈', requiresAuth: true }
  },
  // 特色功能
  {
    path: '/ai-assistant',
    name: 'AIAssistant',
    component: () => import('@/views/AIAssistant.vue'),
    meta: { title: 'AI智能问答', requiresAuth: true }
  },
  {
    path: '/adaptive-learning',
    name: 'AdaptiveLearning',
    component: () => import('@/views/AdaptiveLearning.vue'),
    meta: { title: '自适应学习路径', requiresAuth: true }
  },
  {
    path: '/scenario-replay',
    name: 'ScenarioReplay',
    component: () => import('@/views/ScenarioReplay.vue'),
    meta: { title: '情景沉浸回放', requiresAuth: true }
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('@/views/Community.vue'),
    meta: { title: '社区互助反诈', requiresAuth: true }
  },
  {
    path: '/vr-training',
    name: 'VRTraining',
    component: () => import('@/views/VRTraining.vue'),
    meta: { title: 'VR演练体验', requiresAuth: true }
  },
  {
    path: '/risk-profile',
    name: 'RiskProfile',
    component: () => import('@/views/RiskProfile.vue'),
    meta: { title: '智能风险画像', requiresAuth: true }
  },
  // 班级管理 - 教师和管理员可访问
  {
    path: '/class',
    name: 'Class',
    component: () => import('@/views/class/Index.vue'),
    meta: { title: '班级管理', requiresAuth: true, requiresTeacher: true }
  },
  {
    path: '/class/:id',
    name: 'ClassDetail',
    component: () => import('@/views/class/Detail.vue'),
    meta: { title: '班级详情', requiresAuth: true, requiresTeacher: true }
  },
  // 智能预警
  {
    path: '/warning',
    name: 'Warning',
    component: () => import('@/views/warning/Index.vue'),
    meta: { title: '智能预警', requiresAuth: true }
  },
  // 后台管理
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { title: '后台管理', requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'knowledge',
        name: 'AdminKnowledge',
        component: () => import('@/views/admin/Knowledge.vue'),
        meta: { title: '知识管理' }
      },
      {
        path: 'questions',
        name: 'AdminQuestions',
        component: () => import('@/views/admin/Questions.vue'),
        meta: { title: '题库管理' }
      },
      {
        path: 'papers',
        name: 'AdminPapers',
        component: () => import('@/views/admin/Papers.vue'),
        meta: { title: '试卷管理' }
      },
      {
        path: 'scenes',
        name: 'AdminScenes',
        component: () => import('@/views/admin/Scenes.vue'),
        meta: { title: '演练管理' }
      },
      {
        path: 'reports',
        name: 'AdminReports',
        component: () => import('@/views/admin/Reports.vue'),
        meta: { title: '举报管理' }
      },
      {
        path: 'warnings',
        name: 'AdminWarnings',
        component: () => import('@/views/admin/Warnings.vue'),
        meta: { title: '预警管理' }
      },
      {
        path: 'activities',
        name: 'AdminActivities',
        component: () => import('@/views/admin/Activities.vue'),
        meta: { title: '活动管理' }
      },
      {
        path: 'tasks',
        name: 'AdminTasks',
        component: () => import('@/views/admin/Tasks.vue'),
        meta: { title: '班级任务' }
      },
      {
        path: 'works',
        name: 'AdminWorks',
        component: () => import('@/views/admin/Works.vue'),
        meta: { title: '作品管理' }
      },
      {
        path: 'stats',
        name: 'AdminStats',
        component: () => import('@/views/admin/Stats.vue'),
        meta: { title: '数据统计' }
      },
      {
        path: 'operation-log',
        name: 'AdminOperationLog',
        component: () => import('@/views/admin/OperationLog.vue'),
        meta: { title: '操作日志' }
      },
      {
        path: 'monitor',
        name: 'AdminMonitor',
        component: () => import('@/views/admin/Monitor.vue'),
        meta: { title: '系统监控' }
      },
      {
        path: 'data-export',
        name: 'AdminDataExport',
        component: () => import('@/views/admin/DataExport.vue'),
        meta: { title: '数据导出' }
      },
      {
        path: 'data-analysis',
        name: 'AdminDataAnalysis',
        component: () => import('@/views/admin/DataAnalysis.vue'),
        meta: { title: '数据分析' }
      }
    ]
  },
  // 专家中心
  {
    path: '/expert',
    name: 'Expert',
    component: () => import('@/views/expert/Layout.vue'),
    meta: { title: '专家中心', requiresAuth: true, requiresExpert: true },
    children: [
      {
        path: '',
        name: 'ExpertDashboard',
        component: () => import('@/views/expert/Dashboard.vue'),
        meta: { title: '专家首页' }
      },
      {
        path: 'analysis',
        name: 'ExpertAnalysis',
        component: () => import('@/views/expert/Analysis.vue'),
        meta: { title: '案例分析' }
      },
      {
        path: 'advice',
        name: 'ExpertAdvice',
        component: () => import('@/views/expert/Advice.vue'),
        meta: { title: '专家建议' }
      }
    ]
  }
]

const router = VueRouter.createRouter({
  history: VueRouter.createWebHistory(),
  routes
})

// 路由守卫 - 权限验证和页面加载状态
router.beforeEach((to: RouteLocationNormalized, _from: RouteLocationNormalized, next: NavigationGuardNext) => {
  // 设置页面标题
  document.title = `${to.meta.title || '反诈平台'} - 高校反诈安全知识普及平台`
  
  const token = localStorage.getItem('token')
  const userInfoStr = localStorage.getItem('userInfo')
  let userInfo = {}
  if (userInfoStr && userInfoStr !== 'undefined' && userInfoStr !== 'null') {
    try {
      userInfo = JSON.parse(userInfoStr)
    } catch {
      userInfo = {}
    }
  }
  
  // 需要登录但未登录
  if (to.meta.requiresAuth && !token) {
    ElMessage.warning('请先登录后再访问')
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  
  // 需要管理员权限但权限不足
  if (to.meta.requiresAdmin) {
    const roleId = (userInfo as any).roleId || (userInfo as any).role
    // 管理员及以上才能访问后台
    if (!roleId || roleId < ADMIN) {
      ElMessage.error('权限不足，无法访问管理后台')
      next('/')
      return
    }
  }
  
  // 需要专家权限但权限不足
  if (to.meta.requiresExpert) {
    const roleId = (userInfo as any).roleId || (userInfo as any).role
    // 只有专家和系统管理员能访问专家中心
    if (roleId !== EXPERT && roleId !== SUPER_ADMIN) {
      ElMessage.error('权限不足，无法访问专家中心')
      next('/')
      return
    }
  }
  
  // 需要教师权限但权限不足
  if (to.meta.requiresTeacher) {
    const roleId = (userInfo as any).roleId || (userInfo as any).role
    // 教师、管理员、专家和系统管理员可访问
    if (!roleId || roleId < TEACHER) {
      ElMessage.error('权限不足，无法访问班级管理')
      next('/')
      return
    }
  }
  
  next()
})

// 路由后置守卫 - 页面滚动到顶部
router.afterEach(() => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
})

export default router









