import { post, get, del } from './request'

/**
 * AI智能问答助手API
 */
export const ai = {
  /**
   * 智能问答
   * @param question 问题
   * @param category 分类
   * @returns 回答结果
   */
  askQuestion: (question: string, category: string) => {
    return post('/ai/ask', { question, category })
  },

  /**
   * 获取问答历史
   * @param page 页码
   * @param size 每页大小
   * @returns 问答历史列表
   */
  getQuestionHistory: (page: number = 1, size: number = 10) => {
    return get('/ai/history', { params: { page, size } })
  },

  /**
   * 获取热门问题
   * @param limit 数量限制
   * @returns 热门问题列表
   */
  getHotQuestions: (limit: number = 10) => {
    return get('/ai/hot', { params: { limit } })
  },

  /**
   * 标记问题为公开
   * @param id 问题ID
   * @returns 是否成功
   */
  markAsPublic: (id: number) => {
    return post(`/ai/question/${id}/public`)
  },

  /**
   * 标记问题为私有
   * @param id 问题ID
   * @returns 是否成功
   */
  markAsPrivate: (id: number) => {
    return post(`/ai/question/${id}/private`)
  },

  /**
   * 删除问题
   * @param id 问题ID
   * @returns 是否成功
   */
  deleteQuestion: (id: number) => {
    return del(`/ai/question/${id}`)
  }
}

export default ai
