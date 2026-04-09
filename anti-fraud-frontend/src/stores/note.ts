import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Note, LearningProgress, Recommendation } from '@/types/note'
import { get, post } from '@/utils/request'

export const useNoteStore = defineStore('note', () => {
  // 笔记列表
  const notes = ref<Note[]>([])
  const loading = ref(false)
  
  // 学习进度
  const learningProgress = ref<Map<number, LearningProgress>>(new Map())
  
  // 推荐列表
  const recommendations = ref<Recommendation[]>([])
  
  // 获取某知识的笔记列表
  const getNotesByKnowledgeId = async (knowledgeId: number) => {
    loading.value = true
    try {
      const res = await get<Note[]>(`/knowledge/${knowledgeId}/notes`)
      notes.value = res.data || []
      return notes.value
    } catch (e) {
      // 模拟数据
      notes.value = []
      return notes.value
    } finally {
      loading.value = false
    }
  }
  
  // 添加笔记
  const addNote = async (knowledgeId: number, content: string, position?: string) => {
    try {
      const res = await post<Note>(`/knowledge/${knowledgeId}/notes`, {
        content,
        position
      })
      notes.value.unshift(res.data)
      return res.data
    } catch (e) {
      // 模拟添加
      const mockNote: Note = {
        id: Date.now(),
        knowledgeId,
        userId: 1,
        content,
        position,
        createTime: new Date().toISOString(),
        updateTime: new Date().toISOString()
      }
      notes.value.unshift(mockNote)
      return mockNote
    }
  }
  
  // 更新笔记
  const updateNote = async (noteId: number, content: string) => {
    try {
      await post(`/knowledge/notes/${noteId}`, { content })
      const note = notes.value.find(n => n.id === noteId)
      if (note) {
        note.content = content
        note.updateTime = new Date().toISOString()
      }
    } catch (e) {
      const note = notes.value.find(n => n.id === noteId)
      if (note) {
        note.content = content
        note.updateTime = new Date().toISOString()
      }
    }
  }
  
  // 删除笔记
  const deleteNote = async (noteId: number) => {
    try {
      await post(`/knowledge/notes/${noteId}/delete`)
      notes.value = notes.value.filter(n => n.id !== noteId)
    } catch (e) {
      notes.value = notes.value.filter(n => n.id !== noteId)
    }
  }
  
  // 获取学习进度
  const getProgress = async (knowledgeId: number): Promise<LearningProgress | null> => {
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
  const updateProgress = async (knowledgeId: number, data: Partial<LearningProgress>) => {
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
    await updateProgress(knowledgeId, {
      progress: 100,
      isCompleted: true,
      completedAt: new Date().toISOString()
    })
  }
  
  // 获取推荐列表
  const getRecommendations = async (knowledgeId?: number) => {
    try {
      const res = await get<Recommendation[]>(`/knowledge/recommendations`, {
        params: { exclude: knowledgeId }
      })
      recommendations.value = res.data || []
      return recommendations.value
    } catch (e) {
      // 模拟推荐数据
      recommendations.value = [
        {
          id: 2,
          title: '网络购物诈骗防范指南',
          category: '网络诈骗',
          viewCount: 1890,
          score: 95,
          reason: '基于您的学习偏好'
        },
        {
          id: 3,
          title: '杀猪盘诈骗深度解析',
          category: '情感诈骗',
          viewCount: 2105,
          score: 88,
          reason: '热门推荐内容'
        },
        {
          id: 4,
          title: '冒充客服诈骗实战案例',
          category: '电信诈骗',
          viewCount: 1650,
          score: 82,
          reason: '同类型诈骗手法'
        }
      ]
      return recommendations.value
    }
  }
  
  return {
    notes,
    loading,
    learningProgress,
    recommendations,
    getNotesByKnowledgeId,
    addNote,
    updateNote,
    deleteNote,
    getProgress,
    updateProgress,
    markAsCompleted,
    getRecommendations
  }
})
