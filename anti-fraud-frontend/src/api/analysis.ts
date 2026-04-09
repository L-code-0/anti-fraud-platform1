/**
 * 分析模块 API
 */
import { get } from './request'

// 获取数据分析概览
export function getDataAnalysisOverview() {
  return get('/analysis/overview')
}

// 获取用户学习趋势
export function getLearningTrend(params?: {
  startDate?: string
  endDate?: string
  type?: string
}) {
  return get('/analysis/learning-trend', params)
}

// 获取知识掌握度
export function getKnowledgeMastery() {
  return get('/analysis/knowledge-mastery')
}

// 获取反诈能力评估
export function getFraudAbilityAssessment() {
  return get('/analysis/fraud-ability')
}

// 获取安全知识分布
export function getKnowledgeDistribution() {
  return get('/analysis/knowledge-distribution')
}

// 获取学习时长统计
export function getLearningTimeStats(params?: {
  startDate?: string
  endDate?: string
}) {
  return get('/analysis/learning-time', params)
}
