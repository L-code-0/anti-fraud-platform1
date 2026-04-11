<template>
  <div class="knowledge-detail">
    <el-row :gutter="20">
      <el-col :span="18">
        <el-card v-if="knowledge" class="content-card">
          <template #header>
            <div class="header">
              <div class="title-row">
                <h1>{{ knowledge.title }}</h1>
                <div class="action-buttons">
                  <el-button 
                    :type="knowledge.isLiked ? 'primary' : 'default'" 
                    circle
                    @click="handleLike"
                  >
                    <el-icon><Like :style="{ color: knowledge.isLiked ? '#409EFF' : 'inherit' }" /></el-icon>
                    <span class="count">{{ knowledge.likeCount }}</span>
                  </el-button>
                  <el-button 
                    :type="knowledge.isCollected ? 'warning' : 'default'" 
                    circle
                    @click="handleCollect"
                  >
                    <el-icon><Star /></el-icon>
                  </el-button>
                  <el-button circle @click="handleShare">
                    <el-icon><Share /></el-icon>
                  </el-button>
                </div>
              </div>
              <div class="meta">
                <el-tag size="small" type="primary">{{ knowledge.categoryName }}</el-tag>
                <span><el-icon><User /></el-icon> {{ knowledge.authorName }}</span>
                <span><el-icon><Calendar /></el-icon> {{ knowledge.publishTime }}</span>
                <span><el-icon><View /></el-icon> {{ knowledge.viewCount }}</span>
                <span><el-icon><Star /></el-icon> {{ knowledge.collectCount }}人收藏</span>
              </div>
            </div>
          </template>
          
          <div class="content" v-html="knowledge.content"></div>
          
          <el-divider />
          
          <!-- 学习进度 -->
          <div class="learning-progress" v-if="knowledge.id">
            <div class="progress-info">
              <span class="label">学习进度</span>
              <el-progress :percentage="learningProgress" :stroke-width="8" />
            </div>
            <el-button v-if="learningProgress < 100" type="primary" plain @click="handleMarkAsRead">
              <el-icon><CircleCheck /></el-icon> 标记为已读
            </el-button>
            <el-tag v-else type="success" size="large">
              <el-icon><CircleCheck /></el-icon> 已完成学习
            </el-tag>
          </div>
          
          <!-- 评论区域 -->
          <el-card class="comments-card">
            <template #header>
              <div class="comments-header">
                <span>评论 ({{ totalComments }})</span>
                <el-select v-model="sortOrder" placeholder="排序" size="small" style="width: 120px">
                  <el-option label="最新优先" value="newest" />
                  <el-option label="最热优先" value="hottest" />
                </el-select>
              </div>
            </template>
            
            <!-- 发表评论 -->
            <div class="comment-form" v-if="isLoggedIn">
              <el-avatar :size="40" class="comment-avatar">
                {{ currentUser?.charAt(0) || 'U' }}
              </el-avatar>
              <div class="comment-input-wrapper">
                <el-input
                  v-model="commentContent"
                  type="textarea"
                  :rows="3"
                  placeholder="发表您的看法... (支持Markdown)"
                  resize="none"
                />
                <div class="comment-form-footer">
                  <el-button type="primary" size="small" @click="handleSubmitComment" :loading="submitting">
                    发布评论
                  </el-button>
                </div>
              </div>
            </div>
            <div v-else class="login-tip">
              <el-icon><Warning /></el-icon>
              <span>登录后可参与评论，</span>
              <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
            </div>
            
            <!-- 评论列表 -->
            <div class="comments-list">
              <el-empty v-if="comments.length === 0" description="暂无评论，快来抢沙发吧！" />
              
              <div v-else class="comment-item" v-for="comment in comments" :key="comment.id">
                <el-avatar :size="40" class="comment-avatar">
                  {{ comment.userName?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="comment-body">
                  <div class="comment-header">
                    <span class="comment-user">{{ comment.userName }}</span>
                    <el-tag v-if="comment.isAuthor" size="small" type="warning">作者</el-tag>
                    <span class="comment-time">{{ comment.createTime }}</span>
                  </div>
                  <div class="comment-content">
                    {{ comment.content }}
                  </div>
                  <div class="comment-actions">
                    <span class="action-btn" @click="handleReplyLike(comment)">
                      <el-icon><Star /></el-icon>
                      {{ comment.likeCount || 0 }}
                    </span>
                    <span class="action-btn" @click="handleReply(comment)">
                      <el-icon><ChatLineSquare /></el-icon>
                      回复
                    </span>
                    <span v-if="comment.userId === currentUserId" class="action-btn delete" @click="handleDeleteComment(comment)">
                      <el-icon><Delete /></el-icon>
                      删除
                    </span>
                  </div>
                  
                  <!-- 回复列表 -->
                  <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
                    <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                      <el-avatar :size="32" class="reply-avatar">
                        {{ reply.userName?.charAt(0) || 'U' }}
                      </el-avatar>
                      <div class="reply-body">
                        <div class="reply-header">
                          <span class="reply-user">{{ reply.userName }}</span>
                          <el-tag v-if="reply.isAuthor" size="small" type="warning">作者</el-tag>
                          <span v-if="reply.replyTo" class="reply-to">回复 @{{ reply.replyTo }}</span>
                          <span class="reply-time">{{ reply.createTime }}</span>
                        </div>
                        <div class="reply-content">
                          {{ reply.content }}
                        </div>
                        <div class="reply-actions">
                          <span class="action-btn" @click="handleReplyLike(reply)">
                            <el-icon><Star /></el-icon>
                            {{ reply.likeCount || 0 }}
                          </span>
                          <span class="action-btn" @click="handleReply(comment, reply)">
                            <el-icon><ChatLineSquare /></el-icon>
                            回复
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <!-- 目录导航 -->
        <el-card class="toc-card" v-if="tocItems.length > 0">
          <template #header>
            <span>文章目录</span>
          </template>
          <div class="toc-list">
            <a 
              v-for="item in tocItems" 
              :key="item.id"
              :href="`#${item.id}`"
              :class="['toc-item', { active: activeToc === item.id }]"
            >
              {{ item.text }}
            </a>
          </div>
        </el-card>
        
        <!-- 相关推荐 -->
        <el-card class="related-card">
          <template #header>
            <span>相关推荐</span>
          </template>
          <div
            v-for="item in relatedList"
            :key="item.id"
            class="related-item"
            @click="$router.push(`/knowledge/${item.id}`)"
          >
            <h4>{{ item.title }}</h4>
            <span class="view-count"><el-icon><View /></el-icon> {{ item.viewCount }}</span>
          </div>
        </el-card>
        
        <!-- 学习统计 -->
        <el-card class="stats-card">
          <template #header>
            <span>学习统计</span>
          </template>
          <div class="stats-content">
            <div class="stat-item">
              <span class="stat-label">阅读时长</span>
              <span class="stat-value">{{ readTime }}分钟</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">学习人数</span>
              <span class="stat-value">{{ knowledge?.viewCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">收藏人数</span>
              <span class="stat-value">{{ knowledge?.collectCount || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 回复对话框 -->
    <el-dialog v-model="showReplyDialog" title="回复评论" width="500px">
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="4"
        :placeholder="`回复 @${replyToUser}...`"
      />
      <template #footer>
        <el-button @click="showReplyDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReply" :loading="submitting">发布回复</el-button>
      </template>
    </el-dialog>
    
    <!-- 分享对话框 -->
    <el-dialog v-model="showShareDialog" title="分享文章" width="400px">
      <div class="share-content">
        <p class="share-title">{{ knowledge?.title }}</p>
        <el-input v-model="shareUrl" readonly>
          <template #append>
            <el-button @click="handleCopyLink">复制</el-button>
          </template>
        </el-input>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { User, Calendar, View, Star, Share, ChatLineSquare, Delete, CircleCheck, Warning } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const knowledge = ref<any>(null)
const relatedList = ref<any[]>([])
const comments = ref<any[]>([])
const totalComments = ref(0)
const commentContent = ref('')
const submitting = ref(false)
const sortOrder = ref('newest')

// 回复相关
const showReplyDialog = ref(false)
const replyContent = ref('')
const replyToComment = ref<any>(null)
const replyToReply = ref<any>(null)
const replyToUser = ref('')

// 分享相关
const showShareDialog = ref(false)
const shareUrl = ref('')

// 学习进度
const learningProgress = ref(0)
const readTime = ref(0)
let readTimer: number | null = null

// 目录相关
const tocItems = ref<any[]>([])
const activeToc = ref('')

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)
const currentUser = computed(() => userStore.userInfo?.realName || userStore.userInfo?.username || '')
const currentUserId = computed(() => userStore.userInfo?.id || 0)

const loadDetail = async () => {
  try {
    const res = await get(`/knowledge/${route.params.id}`)
    knowledge.value = res.data
    loadComments()
    parseToc(res.data.content)
  } catch (e) {
    knowledge.value = {
      id: route.params.id,
      title: '冒充公检法诈骗案例分析',
      authorName: '王专家',
      publishTime: '2026-01-15',
      viewCount: 2356,
      likeCount: 128,
      collectCount: 56,
      isLiked: false,
      isCollected: false,
      categoryName: '电信诈骗',
      content: `<h2>什么是冒充公检法诈骗</h2>
        <p>冒充公检法诈骗是指骗子冒充公安、检察院、法院等机关工作人员，以受害人涉嫌洗钱、非法集资等犯罪为由，要求受害人将资金转入"安全账户"的诈骗行为。</p>
        <h3>常见手法</h3>
        <ul><li><strong>电话诈骗</strong>：骗子通过电话自称是公安民警</li></ul>
        <h3>防范要点</h3>
        <ol><li>公检法机关不会通过电话办案</li></ol>`
    }
  }
  
  // 开始计时
  startReadTimer()
}

const loadComments = async () => {
  try {
    const res = await get(`/knowledge/${route.params.id}/comments`, {
      params: { sort: sortOrder.value }
    })
    comments.value = res.data?.list || []
    totalComments.value = res.data?.total || 0
  } catch (e) {
    comments.value = [
      {
        id: 1,
        userId: 1,
        userName: '张三',
        content: '这篇文章写得很好，学到了很多防骗知识！',
        createTime: '2026-01-15 14:30',
        likeCount: 12,
        isAuthor: false,
        replies: [
          {
            id: 11,
            userId: 2,
            userName: '李四',
            content: '确实，我也学到了不少',
            createTime: '2026-01-15 15:00',
            likeCount: 3,
            isAuthor: false
          }
        ]
      },
      {
        id: 2,
        userId: 3,
        userName: '王五',
        content: '希望能多一些类似的案例分析',
        createTime: '2026-01-14 10:20',
        likeCount: 8,
        isAuthor: true,
        replies: []
      }
    ]
    totalComments.value = 2
  }
}

const parseToc = (content: string) => {
  const headingRegex = /<h([2-3])[^>]*>([^<]+)<\/h[2-3]>/g
  const toc: any[] = []
  let match
  let idCounter = 0
  
  while ((match = headingRegex.exec(content)) !== null) {
    const level = match[1]
    const text = match[2]
    const id = `heading-${idCounter++}`
    toc.push({ level: parseInt(level), text, id })
  }
  
  tocItems.value = toc
}

const startReadTimer = () => {
  if (readTimer) clearInterval(readTimer)
  readTimer = window.setInterval(() => {
    readTime.value++
    if (knowledge.value) {
      // 模拟学习进度
      learningProgress.value = Math.min(100, readTime.value * 5)
    }
  }, 60000) // 每分钟更新
}

const handleLike = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await post(`/knowledge/${route.params.id}/like`)
    knowledge.value.isLiked = !knowledge.value.isLiked
    knowledge.value.likeCount += knowledge.value.isLiked ? 1 : -1
    ElMessage.success(knowledge.value.isLiked ? '点赞成功' : '取消点赞')
  } catch (e) {
    knowledge.value.isLiked = !knowledge.value.isLiked
    knowledge.value.likeCount += knowledge.value.isLiked ? 1 : -1
  }
}

const handleCollect = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await post(`/knowledge/${route.params.id}/collect`)
    knowledge.value.isCollected = !knowledge.value.isCollected
    knowledge.value.collectCount += knowledge.value.isCollected ? 1 : -1
    ElMessage.success(knowledge.value.isCollected ? '收藏成功' : '取消收藏')
  } catch (e) {
    knowledge.value.isCollected = !knowledge.value.isCollected
    knowledge.value.collectCount += knowledge.value.isCollected ? 1 : -1
  }
}

const handleShare = () => {
  shareUrl.value = window.location.href
  showShareDialog.value = true
}

const handleCopyLink = () => {
  navigator.clipboard.writeText(shareUrl.value)
  ElMessage.success('链接已复制到剪贴板')
}

const handleMarkAsRead = async () => {
  try {
    await post('/user/learning/progress', {
      contentId: knowledge.value.id,
      type: 'knowledge',
      progress: 100
    })
    learningProgress.value = 100
    ElMessage.success('已标记为已读')
  } catch (e) {
    learningProgress.value = 100
  }
}

const handleSubmitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  submitting.value = true
  try {
    await post(`/knowledge/${route.params.id}/comments`, {
      content: commentContent.value
    })
    ElMessage.success('评论发布成功')
    commentContent.value = ''
    loadComments()
  } catch (e) {
    // 模拟
    const newComment = {
      id: Date.now(),
      userId: currentUserId.value,
      userName: currentUser.value,
      content: commentContent.value,
      createTime: '刚刚',
      likeCount: 0,
      isAuthor: false,
      replies: []
    }
    comments.value.unshift(newComment)
    totalComments.value++
    commentContent.value = ''
    ElMessage.success('评论发布成功')
  } finally {
    submitting.value = false
  }
}

const handleReply = (comment: any, reply?: any) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  replyToComment.value = comment
  replyToReply.value = reply || null
  replyToUser.value = reply?.userName || comment.userName
  showReplyDialog.value = true
}

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  submitting.value = true
  try {
    await post(`/knowledge/${route.params.id}/comments/${replyToComment.value.id}/replies`, {
      content: replyContent.value,
      replyToId: replyToReply.value?.id || null
    })
    ElMessage.success('回复发布成功')
    replyContent.value = ''
    showReplyDialog.value = false
    loadComments()
  } catch (e) {
    // 模拟
    const newReply = {
      id: Date.now(),
      userId: currentUserId.value,
      userName: currentUser.value,
      content: replyContent.value,
      createTime: '刚刚',
      likeCount: 0,
      isAuthor: false,
      replyTo: replyToUser.value
    }
    if (!replyToComment.value.replies) {
      replyToComment.value.replies = []
    }
    replyToComment.value.replies.push(newReply)
    replyContent.value = ''
    showReplyDialog.value = false
    ElMessage.success('回复发布成功')
  } finally {
    submitting.value = false
  }
}

const handleReplyLike = (item: any) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  item.likeCount = (item.likeCount || 0) + 1
}

const handleDeleteComment = (comment: any) => {
  ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await post(`/knowledge/comments/${comment.id}/delete`)
      comments.value = comments.value.filter(c => c.id !== comment.id)
      totalComments.value--
      ElMessage.success('删除成功')
    } catch (e) {
      comments.value = comments.value.filter(c => c.id !== comment.id)
      totalComments.value--
    }
  }).catch(() => {})
}

onMounted(() => {
  loadDetail()
  relatedList.value = [
    { id: 2, title: '网络兼职刷单诈骗防范指南', viewCount: 1892 },
    { id: 3, title: '校园贷陷阱识别指南', viewCount: 3210 },
    { id: 4, title: '网络购物诈骗案例分析', viewCount: 1567 }
  ]
})

onUnmounted(() => {
  if (readTimer) clearInterval(readTimer)
})
</script>

<style scoped>
.knowledge-detail {
  max-width: 1400px;
  margin: 0 auto;
}

.content-card {
  min-height: 500px;
}

.header {
  padding: var(--spacing-sm) 0;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-md);
}

.title-row h1 {
  font-size: 24px;
  color: var(--color-text-primary);
  flex: 1;
}

.action-buttons {
  display: flex;
  gap: var(--spacing-xs);
}

.action-buttons .count {
  margin-left: 4px;
  font-size: 12px;
}

.meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
  flex-wrap: wrap;
}

.meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.content {
  line-height: 1.8;
  color: var(--color-text-secondary);
  padding: var(--spacing-lg) 0;
}

.content :deep(h2) {
  margin: 24px 0 12px;
  color: var(--color-text-primary);
  font-size: var(--font-size-xl);
}

.content :deep(h3) {
  margin: 20px 0 10px;
  color: var(--color-text-primary);
  font-size: var(--font-size-lg);
}

.content :deep(p) {
  margin-bottom: 12px;
}

.content :deep(ul), .content :deep(ol) {
  padding-left: 24px;
  margin-bottom: 12px;
}

.content :deep(li) {
  margin-bottom: 8px;
}

/* 学习进度 */
.learning-progress {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-md);
  background: var(--color-bg-page);
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-md);
}

.progress-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.progress-info .label {
  color: var(--color-text-secondary);
  white-space: nowrap;
}

.progress-info .el-progress {
  flex: 1;
}

/* 评论 */
.comments-card {
  margin-top: var(--spacing-lg);
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-form {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-input-wrapper {
  flex: 1;
}

.comment-form-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-sm);
}

.login-tip {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg);
  background: var(--color-bg-page);
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-lg);
  color: var(--color-text-secondary);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.comment-item {
  display: flex;
  gap: var(--spacing-md);
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.comment-user {
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.comment-time {
  color: var(--color-text-muted);
  font-size: var(--font-size-sm);
}

.comment-content {
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin-bottom: var(--spacing-sm);
}

.comment-actions {
  display: flex;
  gap: var(--spacing-md);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--color-text-muted);
  font-size: var(--font-size-sm);
  cursor: pointer;
  transition: color 0.2s;
}

.action-btn:hover {
  color: var(--color-primary);
}

.action-btn.delete:hover {
  color: var(--color-danger);
}

/* 回复列表 */
.reply-list {
  margin-top: var(--spacing-md);
  padding-left: var(--spacing-lg);
  border-left: 2px solid var(--color-border-light);
}

.reply-item {
  display: flex;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
}

.reply-avatar {
  flex-shrink: 0;
}

.reply-body {
  flex: 1;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
  margin-bottom: 4px;
}

.reply-user {
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
}

.reply-to {
  color: var(--color-primary);
  font-size: var(--font-size-sm);
}

.reply-time {
  color: var(--color-text-muted);
  font-size: var(--font-size-xs);
}

.reply-content {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
  line-height: 1.5;
}

.reply-actions {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-xs);
}

/* 目录 */
.toc-card {
  position: sticky;
  top: 80px;
  margin-bottom: var(--spacing-lg);
}

.toc-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.toc-item {
  padding: var(--spacing-xs) var(--spacing-sm);
  color: var(--color-text-secondary);
  text-decoration: none;
  border-radius: var(--radius-sm);
  transition: all 0.2s;
  font-size: var(--font-size-sm);
}

.toc-item:hover {
  background: var(--color-bg-page);
  color: var(--color-primary);
}

.toc-item.active {
  background: rgba(64, 158, 255, 0.1);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
}

/* 相关推荐 */
.related-card {
  position: sticky;
  top: calc(80px + var(--spacing-lg));
}

.related-item {
  padding: var(--spacing-sm) 0;
  border-bottom: 1px solid var(--color-border-light);
  cursor: pointer;
}

.related-item:last-child {
  border-bottom: none;
}

.related-item h4 {
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-xs);
  transition: color 0.2s;
}

.related-item:hover h4 {
  color: var(--color-primary);
}

.view-count {
  font-size: var(--font-size-xs);
  color: var(--color-text-muted);
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 学习统计 */
.stats-card {
  position: sticky;
  top: calc(80px + 300px);
}

.stats-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

.stat-value {
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

/* 分享 */
.share-content {
  text-align: center;
}

.share-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-lg);
}

@media (max-width: 768px) {
  .knowledge-detail .el-col-18,
  .knowledge-detail .el-col-6 {
    width: 100%;
  }
  
  .title-row {
    flex-direction: column;
    gap: var(--spacing-md);
  }
  
  .action-buttons {
    align-self: flex-start;
  }
}
</style>
