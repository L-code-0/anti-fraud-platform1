// 笔记相关类型定义
export interface Note {
  id: number
  knowledgeId: number
  userId: number
  content: string
  position?: string // 笔记位置，可以是段落索引或锚点
  createTime: string
  updateTime: string
}

export interface LearningProgress {
  knowledgeId: number
  progress: number // 0-100
  lastReadPosition: number // 最后阅读位置
  readDuration: number // 阅读时长（秒）
  isCompleted: boolean // 是否完成学习
  completedAt?: string // 完成时间
}

export interface ShareConfig {
  title: string
  description?: string
  url?: string
  image?: string
}

export type SharePlatform = 'wechat' | 'weibo' | 'qq' | 'copy'

export interface Recommendation {
  id: number
  title: string
  category: string
  viewCount: number
  score: number // 推荐分数
  reason?: string // 推荐原因
}

// 学习行为数据
export interface LearningBehavior {
  totalStudyTime: number
  knowledgeCount: number
  completedCount: number
  averageProgress: number
  favoriteCategory?: string
  activeHours: number[]
}
