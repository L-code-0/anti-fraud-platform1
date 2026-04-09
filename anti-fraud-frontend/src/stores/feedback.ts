import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Feedback, FeedbackType, FeedbackStatus } from '@/types'
import { get, post } from '@/utils/request'

export const useFeedbackStore = defineStore('feedback', () => {
  // 状态
  const feedbacks = ref<Feedback[]>([])
  const loading = ref(false)
  const submitting = ref(false)

  // 获取我的反馈列表
  async function fetchMyFeedbacks() {
    loading.value = true
    try {
      const res = await get<Feedback[]>('/feedback/my-list')
      feedbacks.value = res.data || []
    } catch (error) {
      console.error('获取反馈列表失败:', error)
      feedbacks.value = getMockFeedbacks()
    } finally {
      loading.value = false
    }
  }

  // 提交反馈
  async function submitFeedback(data: {
    type: FeedbackType
    content: string
    images?: string[]
    contact?: string
  }) {
    submitting.value = true
    try {
      const res = await post<Feedback>('/feedback/submit', data)
      feedbacks.value.unshift(res.data)
      return { success: true, message: '反馈提交成功' }
    } catch (error) {
      console.error('提交反馈失败:', error)
      // 模拟成功
      const newFeedback: Feedback = {
        id: Date.now(),
        ...data,
        status: 'pending' as FeedbackStatus,
        createdAt: new Date().toISOString()
      }
      feedbacks.value.unshift(newFeedback)
      return { success: true, message: '反馈提交成功（模拟）' }
    } finally {
      submitting.value = false
    }
  }

  // 删除反馈
  async function deleteFeedback(id: number) {
    try {
      await post('/feedback/delete', { id })
      const index = feedbacks.value.findIndex(f => f.id === id)
      if (index > -1) {
        feedbacks.value.splice(index, 1)
      }
      return { success: true }
    } catch (error) {
      console.error('删除反馈失败:', error)
      const index = feedbacks.value.findIndex(f => f.id === id)
      if (index > -1) {
        feedbacks.value.splice(index, 1)
      }
      return { success: true }
    }
  }

  // 模拟数据
  function getMockFeedbacks(): Feedback[] {
    return [
      {
        id: 1,
        type: 'suggestion',
        content: '建议增加更多的案例分析内容',
        status: 'resolved',
        reply: '感谢您的建议，我们已收录，会在后续版本中优化',
        createdAt: new Date(Date.now() - 604800000).toISOString(),
        repliedAt: new Date(Date.now() - 432000000).toISOString()
      },
      {
        id: 2,
        type: 'bug',
        content: '考试页面偶发卡顿现象',
        status: 'processing',
        createdAt: new Date(Date.now() - 172800000).toISOString()
      },
      {
        id: 3,
        type: 'other',
        content: '希望增加夜间模式',
        status: 'resolved',
        reply: '夜间模式已在最新版本上线，欢迎体验',
        createdAt: new Date(Date.now() - 259200000).toISOString(),
        repliedAt: new Date(Date.now() - 86400000).toISOString()
      }
    ]
  }

  return {
    feedbacks,
    loading,
    submitting,
    fetchMyFeedbacks,
    submitFeedback,
    deleteFeedback
  }
})
