// 测试考试相关类型定义
export interface TestQuestion {
  id: number
  content: string
  questionType: number // 1-单选 2-多选 3-判断 4-简答
  options?: string[]
  answer: string | string[]
  analysis?: string
  score: number
  difficulty: number // 1-简单 2-中等 3-困难
  knowledgePoint?: string // 知识点
}

export interface TestPaper {
  id: number
  paperName: string
  duration: number // 分钟
  totalScore: number
  passScore: number
  questions: TestQuestion[]
}

export interface TestResult {
  id: number
  paperId: number
  userId: number
  userScore: number
  totalScore: number
  correctCount: number
  wrongCount: number
  duration: number // 秒
  isPassed: boolean
  wrongQuestions: WrongQuestion[]
  createdAt: string
}

export interface WrongQuestion {
  id: number
  content: string
  yourAnswer: string
  correctAnswer: string
  analysis?: string
  knowledgePoint?: string
}

export interface ExamConfig {
  enableAntiCheat: boolean // 防作弊
  enableQuestionShuffle: boolean // 题目乱序
  enableOptionShuffle: boolean // 选项乱序
  enableTimerWarning: boolean // 时间提醒
  enableFlag: boolean // 标记功能
  enableAutoSubmit: boolean // 时间到自动提交
}

export interface KnowledgeAnalysis {
  name: string
  score: number
  total: number
  rate: number // 掌握率
}

export interface ExamStats {
  totalExams: number
  passedExams: number
  averageScore: number
  averageDuration: number
  averageAccuracy: number
  bestScore: number
  knowledgeAnalysis: KnowledgeAnalysis[]
}
