/**
 * 成就模块 API
 */
import { get } from './request'

// 获取成就列表
export function getAchievementList() {
  return get('/achievement/list')
}

// 获取排行榜
export function getLeaderboard(type?: string) {
  return get('/achievement/leaderboard', { type })
}
