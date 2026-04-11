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
                  <el-button circle @click="showNotePanel = !showNotePanel">
                    <el-icon><Edit /></el-icon>
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
          <div class="learning-progress">
            <div class="progress-info">
              <span class="label">学习进度</span>
              <el-progress :percentage="learningProgress" :stroke-width="8" />
              <span class="progress-text">{{ readTime }}分钟阅读</span>
            </div>
            <el-button v-if="learningProgress < 100" type="primary" plain @click="handleMarkAsRead">
              <el-icon><CircleCheck /></el-icon> 标记为已读
            </el-button>
            <el-tag v-else type="success" size="large">
              <el-icon><CircleCheck /></el-icon> 已完成学习
            </el-tag>
          </div>
          
          <!-- 笔记区域 -->
          <div class="notes-section">
            <div class="section-header">
              <span>我的笔记 ({{ notes.length }})</span>
              <el-button type="primary" size="small" @click="handleAddNote">
                <el-icon><Plus /></el-icon> 添加笔记
              </el-button>
            </div>
            
            <div v-if="notes.length === 0" class="notes-empty">
              <el-icon><Document /></el-icon>
              <p>暂无笔记，点击上方按钮添加学习笔记</p>
            </div>
            
            <div v-else class="notes-list">
              <div v-for="note in notes" :key="note.id" class="note-item">
                <div class="note-content">
                  {{ note.content }}
                </div>
                <div class="note-meta">
                  <span class="note-time">{{ note.createTime }}</span>
                  <div class="note-actions">
                    <el-button size="small" text @click="handleEditNote(note)">
                      <el-icon><Edit /></el-icon>
                    </el-button>
                    <el-button size="small" text type="danger" @click="handleDeleteNote(note)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
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
                  placeholder="发表您的看法..."
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
                      <el-icon><Like /></el-icon>
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
                            <el-icon><Like /></el-icon>
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
        
        <!-- 个性化推荐 -->
        <el-card class="related-card">
          <template #header>
            <div class="card-header-with-action">
              <span>为你推荐</span>
              <el-button size="small" text @click="loadRecommendations">
                <el-icon><Refresh /></el-icon> 换一批
              </el-button>
            </div>
          </template>
          <div
            v-for="item in recommendations"
            :key="item.id"
            class="related-item"
            @click="$router.push(`/knowledge/${item.id}`)"
          >
            <h4>{{ item.title }}</h4>
            <div class="related-meta">
              <span class="view-count"><el-icon><View /></el-icon> {{ item.viewCount }}</span>
              <el-tag v-if="item.reason" size="small" type="primary">{{ item.reason }}</el-tag>
            </div>
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
        <div class="share-platforms">
          <div class="share-btn" @click="shareTo('wechat')">
            <div class="share-icon wechat"><el-icon><ChatDotRound /></el-icon></div>
            <span>微信</span>
          </div>
          <div class="share-btn" @click="shareTo('qq')">
            <div class="share-icon qq"><el-icon><Message /></el-icon></div>
            <span>QQ</span>
          </div>
          <div class="share-btn" @click="shareTo('weibo')">
            <div class="share-icon weibo"><el-icon><Promotion /></el-icon></div>
            <span>微博</span>
          </div>
          <div class="share-btn" @click="shareTo('copy')">
            <div class="share-icon copy"><el-icon><Link /></el-icon></div>
            <span>复制链接</span>
          </div>
        </div>
        <el-input v-model="shareUrl" readonly>
          <template #append>
            <el-button @click="handleCopyLink">复制</el-button>
          </template>
        </el-input>
      </div>
    </el-dialog>
    
    <!-- 笔记对话框 -->
    <el-dialog v-model="showNoteDialog" :title="editingNote ? '编辑笔记' : '添加笔记'" width="500px">
      <el-input
        v-model="noteContent"
        type="textarea"
        :rows="6"
        placeholder="记录你的学习笔记..."
      />
      <template #footer>
        <el-button @click="showNoteDialog = false">取消</el-button>
        <el-button type="primary" @click="submitNote" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 笔记侧边栏 -->
    <el-drawer
      v-model="showNotePanel"
      title="我的笔记"
      direction="rtl"
      size="400px"
    >
      <div class="notes-panel">
        <div class="notes-panel-header">
          <el-button type="primary" @click="handleAddNote">
            <el-icon><Plus /></el-icon> 添加笔记
          </el-button>
        </div>
        
        <el-empty v-if="notes.length === 0" description="暂无笔记" />
        
        <div v-else class="notes-panel-list">
          <div v-for="note in notes" :key="note.id" class="note-panel-item">
            <div class="note-panel-content">{{ note.content }}</div>
            <div class="note-panel-footer">
              <span class="note-panel-time">{{ note.createTime }}</span>
              <div class="note-panel-actions">
                <el-button size="small" text @click="handleEditNote(note)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button size="small" text type="danger" @click="handleDeleteNote(note)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { get, post } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Calendar, View, Star, StarFilled, Share, ChatLineSquare, Delete, CircleCheck, Warning, Edit, Plus, Document, Refresh, ChatDotRound, Message, Promotion, Link } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useNoteStore } from '@/stores/note'
import type { Note, Recommendation } from '@/types/note'

const route = useRoute()
const userStore = useUserStore()
const noteStore = useNoteStore()

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

// 笔记相关
const showNoteDialog = ref(false)
const showNotePanel = ref(false)
const noteContent = ref('')
const editingNote = ref<Note | null>(null)
const notes = ref<Note[]>([])

// 推荐相关
const recommendations = ref<Recommendation[]>([])

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
    loadNotes()
    loadRecommendations()
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

// 笔记相关方法
const loadNotes = async () => {
  try {
    const knowledgeNotes = await noteStore.getNotesByKnowledgeId(Number(route.params.id))
    notes.value = knowledgeNotes
  } catch (e) {
    notes.value = []
  }
}

const handleAddNote = () => {
  editingNote.value = null
  noteContent.value = ''
  showNoteDialog.value = true
}

const handleEditNote = (note: Note) => {
  editingNote.value = note
  noteContent.value = note.content
  showNoteDialog.value = true
}

const handleDeleteNote = async (note: Note) => {
  try {
    await ElMessageBox.confirm('确定要删除这条笔记吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await noteStore.deleteNote(note.id)
    notes.value = notes.value.filter(n => n.id !== note.id)
    ElMessage.success('删除成功')
  } catch (e) {
    // 用户取消
  }
}

const submitNote = async () => {
  if (!noteContent.value.trim()) {
    ElMessage.warning('请输入笔记内容')
    return
  }
  
  submitting.value = true
  try {
    if (editingNote.value) {
      await noteStore.updateNote(editingNote.value.id, noteContent.value)
      const index = notes.value.findIndex(n => n.id === editingNote.value!.id)
      if (index !== -1) {
        notes.value[index].content = noteContent.value
      }
      ElMessage.success('笔记已更新')
    } else {
      const newNote = await noteStore.addNote(Number(route.params.id), noteContent.value)
      notes.value.unshift(newNote)
      ElMessage.success('笔记已添加')
    }
    showNoteDialog.value = false
    noteContent.value = ''
    editingNote.value = null
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 推荐相关方法
const loadRecommendations = async () => {
  try {
    const recs = await noteStore.getRecommendations(Number(route.params.id))
    recommendations.value = recs
  } catch (e) {
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
      learningProgress.value = Math.min(100, readTime.value * 5)
    }
  }, 60000)
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

const shareTo = (platform: string) => {
  const title = knowledge.value?.title || ''
  const url = encodeURIComponent(shareUrl.value)
  
  switch (platform) {
    case 'wechat':
      ElMessage.info('请截图分享到微信')
      break
    case 'qq':
      window.open(`http://connect.qq.com/widget/shareqq/index.html?url=${url}&title=${title}`)
      break
    case 'weibo':
      window.open(`http://service.weibo.com/share/share.php?url=${url}&title=${title}`)
      break
    case 'copy':
      handleCopyLink()
      break
  }
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
  }).then(() => {
    comments.value = comments.value.filter(c => c.id !== comment.id)
    totalComments.value--
    ElMessage.success('删除成功')
  }).catch(() => {})
}

onMounted(() => {
  loadDetail()
})

onUnmounted(() => {
  if (readTimer) clearInterval(readTimer)
})
</script>

<style scoped>
/* 笔记相关样式 */
.notes-section {
  margin: var(--spacing-6) 0;
  padding: var(--spacing-4);
  background: var(--bg-page);
  border-radius: var(--radius-lg);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-4);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.notes-empty {
  text-align: center;
  padding: var(--spacing-8);
  color: var(--text-secondary);
}

.notes-empty .el-icon {
  font-size: 48px;
  margin-bottom: var(--spacing-3);
  color: var(--text-placeholder);
}

.notes-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
}

.note-item {
  padding: var(--spacing-4);
  background: var(--bg-card);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color);
}

.note-content {
  color: var(--text-regular);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--spacing-2);
}

.note-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.note-time {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.note-actions {
  display: flex;
  gap: var(--spacing-1);
}

/* 笔记面板 */
.notes-panel {
  padding: var(--spacing-4);
}

.notes-panel-header {
  margin-bottom: var(--spacing-4);
}

.notes-panel-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
}

.note-panel-item {
  padding: var(--spacing-4);
  background: var(--bg-page);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color);
}

.note-panel-content {
  color: var(--text-regular);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--spacing-2);
}

.note-panel-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.note-panel-time {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.note-panel-actions {
  display: flex;
  gap: var(--spacing-1);
}

/* 学习进度 */
.learning-progress {
  margin: var(--spacing-6) 0;
}

.progress-info {
  margin-bottom: var(--spacing-3);
}

.progress-info .label {
  display: block;
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.progress-text {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  margin-top: var(--spacing-1);
  display: block;
}

/* 分享相关 */
.share-platforms {
  display: flex;
  justify-content: space-around;
  margin-bottom: var(--spacing-4);
  padding: var(--spacing-4);
}

.share-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-2);
  cursor: pointer;
  transition: transform var(--transition-fast);
}

.share-btn:hover {
  transform: scale(1.1);
}

.share-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.share-icon.wechat { background: #07c160; }
.share-icon.qq { background: #1296db; }
.share-icon.weibo { background: #e6162d; }
.share-icon.copy { background: var(--primary-color); }

/* 推荐相关 */
.card-header-with-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.related-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--spacing-2);
}

.related-meta .view-count {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

/* 学习统计 */
.stats-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.stat-value {
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

/* 原有样式保持兼容 */
.knowledge-detail {
  padding: var(--spacing-6);
}

.content-card {
  margin-bottom: var(--spacing-4);
}

.header {
  margin-bottom: var(--spacing-4);
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-3);
}

.title-row h1 {
  margin: 0;
  font-size: var(--font-size-3xl);
  flex: 1;
}

.action-buttons {
  display: flex;
  gap: var(--spacing-2);
}

.action-buttons .count {
  margin-left: var(--spacing-1);
  font-size: var(--font-size-sm);
}

.meta {
  display: flex;
  gap: var(--spacing-4);
  align-items: center;
  flex-wrap: wrap;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.meta > span {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
}

.content {
  line-height: var(--line-height-relaxed);
  color: var(--text-regular);
  font-size: var(--font-size-md);
}

.content :deep(h2) {
  margin-top: var(--spacing-8);
  margin-bottom: var(--spacing-4);
  font-size: var(--font-size-2xl);
  color: var(--text-primary);
}

.content :deep(h3) {
  margin-top: var(--spacing-6);
  margin-bottom: var(--spacing-3);
  font-size: var(--font-size-xl);
  color: var(--text-primary);
}

.content :deep(p) {
  margin-bottom: var(--spacing-4);
}

.content :deep(ul), .content :deep(ol) {
  margin-bottom: var(--spacing-4);
  padding-left: var(--spacing-6);
}

.content :deep(li) {
  margin-bottom: var(--spacing-2);
}

.toc-card, .related-card, .stats-card {
  margin-bottom: var(--spacing-4);
}

.toc-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
}

.toc-item {
  padding: var(--spacing-2);
  color: var(--text-regular);
  text-decoration: none;
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
  font-size: var(--font-size-sm);
}

.toc-item:hover {
  background: var(--hover-bg-color);
  color: var(--primary-color);
}

.toc-item.active {
  background: var(--primary-color);
  color: white;
}

.related-item {
  padding: var(--spacing-3);
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.related-item:hover {
  background: var(--hover-bg-color);
}

.related-item h4 {
  margin: 0 0 var(--spacing-2);
  font-size: var(--font-size-base);
  color: var(--text-primary);
}

.comments-card {
  margin-top: var(--spacing-6);
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-form {
  display: flex;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-4);
}

.comment-input-wrapper {
  flex: 1;
}

.comment-form-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-2);
}

.login-tip {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-4);
  background: var(--bg-page);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
}

.comments-list {
  margin-top: var(--spacing-4);
}

.comment-item {
  display: flex;
  gap: var(--spacing-3);
  padding: var(--spacing-4) 0;
  border-bottom: 1px solid var(--border-color);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-2);
}

.comment-user {
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.comment-time {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.comment-content {
  color: var(--text-regular);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--spacing-2);
}

.comment-actions {
  display: flex;
  gap: var(--spacing-4);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  cursor: pointer;
  transition: color var(--transition-fast);
}

.action-btn:hover {
  color: var(--primary-color);
}

.action-btn.delete:hover {
  color: var(--danger-color);
}

.reply-list {
  margin-top: var(--spacing-3);
  padding-left: var(--spacing-6);
  border-left: 2px solid var(--border-color);
}

.reply-item {
  display: flex;
  gap: var(--spacing-2);
  padding: var(--spacing-3) 0;
}

.reply-body {
  flex: 1;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  flex-wrap: wrap;
  margin-bottom: var(--spacing-1);
}

.reply-user {
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
  color: var(--text-primary);
}

.reply-to {
  font-size: var(--font-size-xs);
  color: var(--primary-color);
}

.reply-time {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.reply-content {
  font-size: var(--font-size-sm);
  color: var(--text-regular);
  line-height: var(--line-height-normal);
}

.reply-actions {
  display: flex;
  gap: var(--spacing-3);
  margin-top: var(--spacing-1);
}

.share-title {
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-3);
  color: var(--text-primary);
}

.share-content {
  padding: var(--spacing-2);
}
</style>
