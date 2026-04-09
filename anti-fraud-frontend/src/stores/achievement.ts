import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Achievement, LeaderboardEntry, LeaderboardType } from '@/types'
import { get, post } from '@/utils/request'

export const useAchievementStore = defineStore('achievement', () => {
  // 状态
  const achievements = ref<Achievement[]>([])
  const leaderboard = ref<LeaderboardEntry[]>([])
  const leaderboardType = ref<LeaderboardType>('points')
  const loading = ref(false)
  const recentUnlocked = ref<Achievement[]>([])

  // 计算属性
  const unlockedAchievements = computed(() =>
    achievements.value.filter(a => a.isUnlocked)
  )
  
  const lockedAchievements = computed(() =>
    achievements.value.filter(a => !a.isUnlocked)
  )
  
  const totalPoints = computed(() =>
    unlockedAchievements.value.reduce((sum, a) => sum + a.points, 0)
  )
  
  const achievementRate = computed(() => {
    if (achievements.value.length === 0) return 0
    return Math.round((unlockedAchievements.value.length / achievements.value.length) * 100)
  })

  // 获取成就列表
  async function fetchAchievements() {
    loading.value = true
    try {
      const res = await get<Achievement[]>('/achievement/list')
      achievements.value = res.data || []
    } catch (error) {
      console.error('获取成就列表失败:', error)
      achievements.value = getMockAchievements()
    } finally {
      loading.value = false
    }
  }

  // 获取排行榜
  async function fetchLeaderboard(type: LeaderboardType = 'points') {
    loading.value = true
    leaderboardType.value = type
    try {
      const res = await get<LeaderboardEntry[]>(`/leaderboard/${type}`)
      leaderboard.value = res.data || []
    } catch (error) {
      console.error('获取排行榜失败:', error)
      leaderboard.value = getMockLeaderboard()
    } finally {
      loading.value = false
    }
  }

  // 获取用户排名
  async function getUserRank(type: LeaderboardType = 'points') {
    try {
      const res = await get<{ rank: number; total: number }>(`/leaderboard/rank/${type}`)
      return res.data
    } catch (error) {
      console.error('获取用户排名失败:', error)
      return { rank: 0, total: 100 }
    }
  }

  // 模拟成就数据
  function getMockAchievements(): Achievement[] {
    return [
      {
        id: 1,
        name: '反诈新人',
        description: '完成第一篇反诈知识学习',
        icon: '🌱',
        category: 'learning',
        condition: '完成1篇文章学习',
        progress: 1,
        maxProgress: 1,
        isUnlocked: true,
        unlockedAt: new Date(Date.now() - 604800000).toISOString(),
        points: 10
      },
      {
        id: 2,
        name: '学习达人',
        description: '累计学习50篇反诈知识',
        icon: '📚',
        category: 'learning',
        condition: '累计学习50篇文章',
        progress: 35,
        maxProgress: 50,
        isUnlocked: false,
        points: 100
      },
      {
        id: 3,
        name: '防骗高手',
        description: '累计学习100篇反诈知识',
        icon: '🛡️',
        category: 'learning',
        condition: '累计学习100篇文章',
        progress: 35,
        maxProgress: 100,
        isUnlocked: false,
        points: 200
      },
      {
        id: 4,
        name: '初露锋芒',
        description: '首次考试获得60分以上',
        icon: '⭐',
        category: 'test',
        condition: '首次考试及格',
        progress: 1,
        maxProgress: 1,
        isUnlocked: true,
        unlockedAt: new Date(Date.now() - 432000000).toISOString(),
        points: 20
      },
      {
        id: 5,
        name: '学霸',
        description: '累计获得5次满分',
        icon: '🏆',
        category: 'test',
        condition: '满分次数达到5次',
        progress: 2,
        maxProgress: 5,
        isUnlocked: false,
        points: 150
      },
      {
        id: 6,
        name: '演练先锋',
        description: '完成10次演练模拟',
        icon: '🎯',
        category: 'simulation',
        condition: '完成10次演练',
        progress: 7,
        maxProgress: 10,
        isUnlocked: false,
        points: 80
      },
      {
        id: 7,
        name: '火眼金睛',
        description: '在演练中识别所有诈骗套路',
        icon: '👁️',
        category: 'simulation',
        condition: '演练识别率100%',
        progress: 0,
        maxProgress: 1,
        isUnlocked: false,
        points: 120
      },
      {
        id: 8,
        name: '社交达人',
        description: '发表10条评论',
        icon: '💬',
        category: 'social',
        condition: '发表评论10条',
        progress: 5,
        maxProgress: 10,
        isUnlocked: false,
        points: 50
      },
      {
        id: 9,
        name: '正义使者',
        description: '成功举报5个诈骗信息',
        icon: '⚔️',
        category: 'special',
        condition: '成功举报5次',
        progress: 3,
        maxProgress: 5,
        isUnlocked: false,
        points: 100
      },
      {
        id: 10,
        name: '反诈达人',
        description: '累计获得1000积分',
        icon: '💎',
        category: 'special',
        condition: '累计积分达到1000',
        progress: 680,
        maxProgress: 1000,
        isUnlocked: false,
        points: 500
      }
    ]
  }

  // 模拟排行榜数据
  function getMockLeaderboard(): LeaderboardEntry[] {
    return [
      { rank: 1, userId: 1, userName: '张三', institution: '清华大学', points: 2580, level: 12 },
      { rank: 2, userId: 2, userName: '李四', institution: '北京大学', points: 2450, level: 11 },
      { rank: 3, userId: 3, userName: '王五', institution: '复旦大学', points: 2320, level: 10 },
      { rank: 4, userId: 4, userName: '赵六', institution: '上海交通大学', points: 2180, level: 10 },
      { rank: 5, userId: 5, userName: '钱七', institution: '浙江大学', points: 2050, level: 9 },
      { rank: 6, userId: 6, userName: '孙八', institution: '南京大学', points: 1920, level: 9 },
      { rank: 7, userId: 7, userName: '周九', institution: '中国科技大学', points: 1850, level: 8 },
      { rank: 8, userId: 8, userName: '吴十', institution: '武汉大学', points: 1780, level: 8 },
      { rank: 9, userId: 9, userName: '郑一', institution: '中山大学', points: 1650, level: 7 },
      { rank: 10, userId: 10, userName: '陈二', institution: '厦门大学', points: 1520, level: 7 }
    ]
  }

  return {
    achievements,
    leaderboard,
    leaderboardType,
    loading,
    recentUnlocked,
    unlockedAchievements,
    lockedAchievements,
    totalPoints,
    achievementRate,
    fetchAchievements,
    fetchLeaderboard,
    getUserRank
  }
})
