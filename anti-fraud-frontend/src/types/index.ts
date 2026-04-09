// 消息通知相关类型定义
export interface Notification {
  id: number
  type: NotificationType
  title: string
  content: string
  relatedId?: number
  relatedType?: string
  isRead: boolean
  createdAt: string
  priority: 'low' | 'normal' | 'high' | 'urgent'
}

export type NotificationType = 
  | 'system'      // 系统通知
  | 'warning'     // 预警通知
  | 'report'      // 举报反馈
  | 'test'        // 考试通知
  | 'points'      // 积分变动
  | 'achievement' // 成就解锁
  | 'social'      // 社交互动
  | 'activity'    // 活动通知

export interface NotificationSettings {
  system: boolean
  warning: boolean
  report: boolean
  test: boolean
  points: boolean
  achievement: boolean
  social: boolean
  activity: boolean
  emailNotify: boolean
  pushNotify: boolean
}

// 评论相关类型
export interface Comment {
  id: number
  targetId: number
  targetType: 'knowledge' | 'qa' | 'activity'
  userId: number
  userName: string
  userAvatar?: string
  content: string
  parentId?: number
  replyTo?: {
    userId: number
    userName: string
  }
  likeCount: number
  isLiked: boolean
  createdAt: string
  replies?: Comment[]
}

export interface CommentParams {
  targetId: number
  targetType: 'knowledge' | 'qa' | 'activity'
  content: string
  parentId?: number
}

// 学习进度相关类型
export interface LearningProgress {
  id: number
  knowledgeId: number
  title: string
  progress: number // 0-100
  lastReadPosition: number
  readDuration: number // 秒
  totalDuration: number // 秒
  isCompleted: boolean
  startedAt: string
  completedAt?: string
  notes: Note[]
}

export interface Note {
  id: number
  knowledgeId: number
  content: string
  position: number
  createdAt: string
  updatedAt?: string
}

// 成就相关类型
export interface Achievement {
  id: number
  name: string
  description: string
  icon: string
  category: AchievementCategory
  condition: string
  progress: number
  maxProgress: number
  isUnlocked: boolean
  unlockedAt?: string
  points: number
}

export type AchievementCategory = 
  | 'learning'   // 学习成就
  | 'test'        // 考试成就
  | 'simulation'  // 演练成就
  | 'social'      // 社交成就
  | 'special'     // 特殊成就

// 排行榜相关类型
export interface LeaderboardEntry {
  rank: number
  userId: number
  userName: string
  userAvatar?: string
  institution: string
  points: number
  level: number
  isCurrentUser?: boolean
}

export type LeaderboardType = 'points' | 'learning' | 'test' | 'simulation'

// 意见反馈相关类型
export interface Feedback {
  id: number
  type: FeedbackType
  content: string
  images?: string[]
  contact?: string
  status: FeedbackStatus
  reply?: string
  createdAt: string
  repliedAt?: string
}

export type FeedbackType = 'suggestion' | 'bug' | 'complaint' | 'other'
export type FeedbackStatus = 'pending' | 'processing' | 'resolved' | 'rejected'

// 学习报告相关类型
export interface StudyReport {
  userId: number
  period: {
    start: string
    end: string
  }
  summary: {
    totalLearningTime: number // 分钟
    knowledgeRead: number
    testsTaken: number
    avgScore: number
    simulationsCompleted: number
    pointsEarned: number
    rank: number
  }
  knowledgeProgress: {
    categoryId: number
    categoryName: string
    progress: number
    total: number
  }[]
  testAnalysis: {
    subject: string
    score: number
    correctRate: number
    improvement: number
  }[]
  weakPoints: {
    knowledgeId: number
    title: string
    correctRate: number
  }[]
  strongPoints: {
    knowledgeId: number
    title: string
    correctRate: number
  }[]
  radarData: {
    dimension: string
    score: number
  }[]
  weeklyProgress: {
    day: string
    learningTime: number
    testsCompleted: number
    pointsEarned: number
  }[]
}

// 防作弊配置
export interface AntiCheatConfig {
  enableScreenMonitor: boolean    // 切屏检测
  enableQuestionRandom: boolean   // 题目乱序
  enableOptionRandom: boolean     // 选项乱序
  enableCopyPaste: boolean        // 允许复制粘贴
  maxTabSwitches: number          // 最大切屏次数
}

// 证书相关类型
export interface Certificate {
  id: number
  name: string
  description: string
  template: string
  issuedAt: string
  validUntil?: string
  verifyCode: string
  qrCode?: string
}
