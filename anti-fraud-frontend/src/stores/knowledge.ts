import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { get, post } from '@/utils/request'

export interface Knowledge {
  id: number
  title: string
  summary: string
  content: string
  coverImage: string
  contentType: number
  categoryId: number
  categoryName: string
  tags: string[]
  viewCount: number
  collectCount: number
  readTime: number
  createTime: string
  updateTime: string
  isHot: boolean
  isRecommended: boolean
}

export interface Category {
  id: number
  categoryName: string
  parentId: number
  level: number
  count: number
  children?: Category[]
}

export interface Tag {
  id: number
  tagName: string
  count: number
  isHot: boolean
}

export interface Recommendation {
  id: number
  title: string
  category: string
  viewCount: number
  score: number
  reason: string
  coverImage: string
}

export interface LearningProgress {
  knowledgeId: number
  progress: number
  lastReadPosition: number
  readDuration: number
  isCompleted: boolean
  completedAt?: string
  lastReadAt?: string
}

export const useKnowledgeStore = defineStore('knowledge', () => {
  // 知识列表
  const knowledgeList = ref<Knowledge[]>([])
  const loading = ref(false)
  const total = ref(0)
  
  // 分类数据
  const categories = ref<Category[]>([])
  const activeCategory = ref('all')
  
  // 标签数据
  const tags = ref<Tag[]>([])
  const hotTags = ref<string[]>([])
  const selectedTags = ref<string[]>([])
  
  // 筛选条件
  const searchKeyword = ref('')
  const contentType = ref(0)
  const sortBy = ref('latest')
  
  // 推荐数据
  const recommendations = ref<Recommendation[]>([])
  const personalizedRecommendations = ref<Recommendation[]>([])
  
  // 学习进度
  const learningProgress = ref<Map<number, LearningProgress>>(new Map())
  
  // 统计数据
  const stats = ref({
    totalKnowledge: 0,
    totalViews: 0,
    totalCollects: 0,
    learnedCount: 0,
    learnProgress: 0,
    consecutiveDays: 0
  })
  
  // 计算属性
  const hasActiveFilters = computed(() => {
    return activeCategory.value !== 'all' || 
           contentType.value !== 0 || 
           selectedTags.value.length > 0 ||
           searchKeyword.value
  })
  
  // 加载分类
  const loadCategories = async () => {
    try {
      const res = await get<Category[]>('/knowledge/categories')
      categories.value = res.data || []
      return categories.value
    } catch (e) {
      // 模拟数据
      categories.value = [
        { id: 1, categoryName: '电信诈骗', parentId: 0, level: 1, count: 156 },
        { id: 2, categoryName: '网络诈骗', parentId: 0, level: 1, count: 203 },
        { id: 3, categoryName: '校园贷诈骗', parentId: 0, level: 1, count: 89 },
        { id: 4, categoryName: '兼职诈骗', parentId: 0, level: 1, count: 124 },
        { id: 5, categoryName: '情感诈骗', parentId: 0, level: 1, count: 78 },
        { id: 6, categoryName: '投资理财诈骗', parentId: 0, level: 1, count: 95 }
      ]
      return categories.value
    }
  }
  
  // 加载标签
  const loadTags = async () => {
    try {
      const res = await get<Tag[]>('/knowledge/tags')
      tags.value = res.data || []
      hotTags.value = tags.value
        .filter(tag => tag.isHot)
        .map(tag => tag.tagName)
        .slice(0, 10)
      return tags.value
    } catch (e) {
      // 模拟数据
      tags.value = [
        { id: 1, tagName: '电信诈骗', count: 120, isHot: true },
        { id: 2, tagName: '网络诈骗', count: 150, isHot: true },
        { id: 3, tagName: '校园贷', count: 80, isHot: true },
        { id: 4, tagName: '兼职诈骗', count: 95, isHot: true },
        { id: 5, tagName: '杀猪盘', count: 75, isHot: true },
        { id: 6, tagName: '冒充客服', count: 65, isHot: true },
        { id: 7, tagName: '钓鱼链接', count: 55, isHot: true },
        { id: 8, tagName: '刷单诈骗', count: 45, isHot: true },
        { id: 9, tagName: '投资理财', count: 60, isHot: false },
        { id: 10, tagName: '网络购物', count: 50, isHot: false }
      ]
      hotTags.value = tags.value
        .filter(tag => tag.isHot)
        .map(tag => tag.tagName)
      return tags.value
    }
  }
  
  // 加载知识列表
  const loadKnowledgeList = async (page: number = 1, size: number = 12) => {
    loading.value = true
    try {
      const params: any = {
        page,
        size,
        keyword: searchKeyword.value,
        sortBy: sortBy.value
      }
      
      if (activeCategory.value !== 'all') {
        params.categoryId = activeCategory.value
      }
      if (contentType.value) {
        params.contentType = contentType.value
      }
      if (selectedTags.value.length > 0) {
        params.tags = selectedTags.value
      }
      
      const res = await get('/knowledge/list', params)
      knowledgeList.value = res.data?.records || res.data || []
      total.value = res.data?.total || 0
      return knowledgeList.value
    } catch (e) {
      // 模拟数据
      knowledgeList.value = Array.from({ length: size }, (_, i) => ({
        id: (page - 1) * size + i + 1,
        title: ['电信诈骗防范指南', '网络购物陷阱揭秘', '校园贷的危害', '兼职刷单骗局', '杀猪盘诈骗分析', '冒充客服诈骗'][i % 6],
        summary: '详细介绍诈骗手法和防范措施，帮助大家提高警惕，远离诈骗陷阱...',
        content: '详细内容...',
        coverImage: '',
        contentType: (i % 3) + 1,
        categoryId: (i % 6) + 1,
        categoryName: ['电信诈骗', '网络诈骗', '校园贷诈骗', '兼职诈骗', '情感诈骗', '投资理财诈骗'][i % 6],
        tags: ['电信诈骗', '网络诈骗', '校园贷', '兼职诈骗', '杀猪盘', '冒充客服'].slice(0, Math.floor(Math.random() * 3) + 1),
        viewCount: Math.floor(Math.random() * 5000),
        collectCount: Math.floor(Math.random() * 200),
        readTime: Math.floor(Math.random() * 10) + 3,
        createTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
        updateTime: new Date(Date.now() - Math.random() * 15 * 24 * 60 * 60 * 1000).toISOString(),
        isHot: i < 3,
        isRecommended: i < 2
      }))
      total.value = 48
      return knowledgeList.value
    } finally {
      loading.value = false
    }
  }
  
  // 加载推荐列表
  const loadRecommendations = async () => {
    try {
      const res = await get<Recommendation[]>('/knowledge/recommend')
      recommendations.value = res.data || []
      return recommendations.value
    } catch (e) {
      // 模拟数据
      recommendations.value = [
        {
          id: 1,
          title: '冒充公检法诈骗案例深度分析',
          category: '电信诈骗',
          viewCount: 3562,
          score: 95,
          reason: '编辑推荐',
          coverImage: ''
        },
        {
          id: 2,
          title: '网络兼职刷单诈骗防范指南',
          category: '兼职诈骗',
          viewCount: 2891,
          score: 92,
          reason: '热门推荐',
          coverImage: ''
        },
        {
          id: 3,
          title: '校园贷陷阱识别与应对',
          category: '校园贷诈骗',
          viewCount: 2104,
          score: 88,
          reason: '校园热点',
          coverImage: ''
        }
      ]
      return recommendations.value
    }
  }
  
  // 加载个性化推荐
  const loadPersonalizedRecommendations = async () => {
    try {
      const res = await get<Recommendation[]>('/knowledge/personalized')
      personalizedRecommendations.value = res.data || []
      return personalizedRecommendations.value
    } catch (e) {
      // 模拟数据
      personalizedRecommendations.value = [
        {
          id: 4,
          title: '基于您的学习历史：投资理财诈骗防范',
          category: '投资理财诈骗',
          viewCount: 1890,
          score: 90,
          reason: '基于您的学习偏好',
          coverImage: ''
        },
        {
          id: 5,
          title: '网络购物诈骗新手法',
          category: '网络诈骗',
          viewCount: 2105,
          score: 85,
          reason: '热门推荐内容',
          coverImage: ''
        },
        {
          id: 6,
          title: '杀猪盘诈骗心理分析',
          category: '情感诈骗',
          viewCount: 1650,
          score: 82,
          reason: '相关主题推荐',
          coverImage: ''
        }
      ]
      return personalizedRecommendations.value
    }
  }
  
  // 加载统计数据
  const loadStats = async () => {
    try {
      const res = await get('/knowledge/stats')
      stats.value = res.data || stats.value
      return stats.value
    } catch (e) {
      // 模拟数据
      stats.value = {
        totalKnowledge: 48,
        totalViews: 125000,
        totalCollects: 8650,
        learnedCount: 24,
        learnProgress: 25,
        consecutiveDays: 7
      }
      return stats.value
    }
  }
  
  // 获取学习进度
  const getLearningProgress = async (knowledgeId: number): Promise<LearningProgress | null> => {
    try {
      const res = await get<LearningProgress>(`/knowledge/${knowledgeId}/progress`)
      learningProgress.value.set(knowledgeId, res.data)
      return res.data
    } catch (e) {
      // 模拟数据
      const mockProgress: LearningProgress = {
        knowledgeId,
        progress: 0,
        lastReadPosition: 0,
        readDuration: 0,
        isCompleted: false
      }
      learningProgress.value.set(knowledgeId, mockProgress)
      return mockProgress
    }
  }
  
  // 更新学习进度
  const updateLearningProgress = async (knowledgeId: number, data: Partial<LearningProgress>) => {
    try {
      await post(`/knowledge/${knowledgeId}/progress`, data)
      const existing = learningProgress.value.get(knowledgeId)
      if (existing) {
        learningProgress.value.set(knowledgeId, { ...existing, ...data })
      } else {
        learningProgress.value.set(knowledgeId, data as LearningProgress)
      }
    } catch (e) {
      const existing = learningProgress.value.get(knowledgeId)
      if (existing) {
        learningProgress.value.set(knowledgeId, { ...existing, ...data })
      }
    }
  }
  
  // 标记学习完成
  const markAsCompleted = async (knowledgeId: number) => {
    await updateLearningProgress(knowledgeId, {
      progress: 100,
      isCompleted: true,
      completedAt: new Date().toISOString(),
      lastReadAt: new Date().toISOString()
    })
  }
  
  // 切换标签选择
  const toggleTag = (tag: string) => {
    const index = selectedTags.value.indexOf(tag)
    if (index > -1) {
      selectedTags.value.splice(index, 1)
    } else {
      selectedTags.value.push(tag)
    }
  }
  
  // 清空筛选条件
  const clearFilters = () => {
    activeCategory.value = 'all'
    contentType.value = 0
    selectedTags.value = []
    searchKeyword.value = ''
    sortBy.value = 'latest'
  }
  
  // 初始化数据
  const initialize = async () => {
    await Promise.all([
      loadCategories(),
      loadTags(),
      loadStats(),
      loadRecommendations(),
      loadPersonalizedRecommendations()
    ])
  }
  
  return {
    // 状态
    knowledgeList,
    loading,
    total,
    categories,
    activeCategory,
    tags,
    hotTags,
    selectedTags,
    searchKeyword,
    contentType,
    sortBy,
    recommendations,
    personalizedRecommendations,
    learningProgress,
    stats,
    hasActiveFilters,
    
    // 方法
    loadCategories,
    loadTags,
    loadKnowledgeList,
    loadRecommendations,
    loadPersonalizedRecommendations,
    loadStats,
    getLearningProgress,
    updateLearningProgress,
    markAsCompleted,
    toggleTag,
    clearFilters,
    initialize
  }
})
