<template>
  <div class="social-interaction">
    <!-- 互动统计 -->
    <div class="interaction-stats">
      <div class="stat-item" @click="toggleLike">
        <el-icon :class="{ active: isLiked }" :size="20"><Star /></el-icon>
        <span>{{ likeCount }}</span>
      </div>
      <div class="stat-item" @click="toggleCollect">
        <el-icon :class="{ active: isCollected }" :size="20"><Collection /></el-icon>
        <span>{{ collectCount }}</span>
      </div>
      <div class="stat-item" @click="showCommentDialog = true">
        <el-icon :size="20"><ChatLineRound /></el-icon>
        <span>{{ commentCount }}</span>
      </div>
      <div class="stat-item" @click="handleShare">
        <el-icon :size="20"><Share /></el-icon>
        <span>分享</span>
      </div>
    </div>

    <!-- 评论对话框 -->
    <el-dialog
      v-model="showCommentDialog"
      title="评论"
      width="600px"
    >
      <!-- 评论输入 -->
      <div class="comment-input-section">
        <el-avatar :size="40" :src="userAvatar" />
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          class="comment-input"
        />
        <el-button type="primary" @click="handleSubmitComment" :disabled="!commentContent.trim()">
          发表
        </el-button>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <el-avatar :size="32" :src="comment.avatar" />
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-author">{{ comment.author }}</span>
              <span class="comment-time">{{ comment.time }}</span>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
            <div class="comment-actions">
              <span class="action-item" @click="handleReply(comment.id)">
                <el-icon><ChatDotRound /></el-icon>
                回复
              </span>
              <span class="action-item" @click="handleCommentLike(comment.id)">
                <el-icon :class="{ active: comment.isLiked }"><Star /></el-icon>
                {{ comment.likes }}
              </span>
            </div>
            <!-- 回复列表 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
              <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <el-avatar :size="24" :src="reply.avatar" />
                <div class="reply-content">
                  <div class="reply-header">
                    <span class="reply-author">{{ reply.author }}</span>
                    <span class="reply-time">{{ reply.time }}</span>
                  </div>
                  <div class="reply-text">{{ reply.content }}</div>
                </div>
              </div>
            </div>
            <!-- 回复输入 -->
            <div v-if="replyingTo === comment.id" class="reply-input-section">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="2"
                placeholder="写下你的回复..."
                class="reply-input"
              />
              <div class="reply-actions">
                <el-button @click="replyingTo = null">取消</el-button>
                <el-button type="primary" @click="handleSubmitReply(comment.id)">
                  回复
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-if="comments.length === 0" 
        description="暂无评论，快来发表第一条评论吧！"
        :image-size="100"
      />

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="comments.length > 0">
        <el-pagination
          v-model:current-page="commentPage"
          v-model:page-size="commentSize"
          :page-sizes="[10, 20, 30]"
          :total="totalComments"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleCommentSizeChange"
          @current-change="handleCommentPageChange"
        />
      </div>
    </el-dialog>

    <!-- 分享对话框 -->
    <el-dialog
      v-model="showShareDialog"
      title="分享"
      width="400px"
    >
      <div class="share-content">
        <div class="share-link">
          <h4>分享链接</h4>
          <div class="link-box">
            <span>{{ shareLink }}</span>
            <el-button type="primary" size="small" @click="copyLink">
              <el-icon><DocumentCopy /></el-icon>
              复制
            </el-button>
          </div>
        </div>
        <div class="share-platforms">
          <h4>分享到</h4>
          <div class="platforms">
            <div class="platform-item" @click="shareTo('wechat')">
              <el-icon class="platform-icon wechat"><ChatLineSquare /></el-icon>
              <span>微信</span>
            </div>
            <div class="platform-item" @click="shareTo('weibo')">
              <el-icon class="platform-icon weibo"><Share /></el-icon>
              <span>微博</span>
            </div>
            <div class="platform-item" @click="shareTo('qq')">
              <el-icon class="platform-icon qq"><ChatDotSquare /></el-icon>
              <span>QQ</span>
            </div>
            <div class="platform-item" @click="shareTo('copy')">
              <el-icon class="platform-icon copy"><DocumentCopy /></el-icon>
              <span>复制链接</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Star, Collection, ChatLineRound, Share, 
  ChatDotRound, DocumentCopy, ChatLineSquare, 
  ChatDotSquare 
} from '@element-plus/icons-vue'

// Props
const props = defineProps({
  targetId: {
    type: String,
    required: true
  },
  targetType: {
    type: String,
    default: 'knowledge'
  }
})

// 状态
const isLiked = ref(false)
const isCollected = ref(false)
const likeCount = ref(0)
const collectCount = ref(0)
const commentCount = ref(0)
const totalComments = ref(0)

// 评论相关
const showCommentDialog = ref(false)
const commentContent = ref('')
const replyContent = ref('')
const replyingTo = ref<string | null>(null)
const comments = ref<any[]>([])
const commentPage = ref(1)
const commentSize = ref(10)

// 分享相关
const showShareDialog = ref(false)
const shareLink = ref('')

// 用户信息
const userAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')

// 计算属性
const fullShareLink = computed(() => {
  return `${window.location.origin}/${props.targetType}/${props.targetId}`
})

// 加载互动数据
const loadInteractionData = async () => {
  try {
    // 模拟数据
    isLiked.value = false
    isCollected.value = false
    likeCount.value = Math.floor(Math.random() * 100) + 10
    collectCount.value = Math.floor(Math.random() * 50) + 5
    commentCount.value = Math.floor(Math.random() * 30) + 2
    totalComments.value = commentCount.value
  } catch (e) {
    console.error('加载互动数据失败', e)
  }
}

// 加载评论
const loadComments = async () => {
  try {
    // 模拟数据
    comments.value = Array.from({ length: 5 }, (_, i) => ({
      id: i + 1,
      author: ['张三', '李四', '王五', '赵六', '钱七'][i],
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      content: `这是一条评论内容 ${i + 1}，非常有价值的信息，感谢分享！`,
      time: '2024-01-15 10:30',
      likes: Math.floor(Math.random() * 20),
      isLiked: false,
      replies: i < 2 ? Array.from({ length: 2 }, (_, j) => ({
        id: `${i}-${j}`,
        author: ['孙八', '周九'][j],
        avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        content: `这是回复 ${j + 1}`,
        time: '2024-01-15 11:00'
      })) : []
    }))
  } catch (e) {
    console.error('加载评论失败', e)
  }
}

// 点赞/取消点赞
const toggleLike = async () => {
  try {
    isLiked.value = !isLiked.value
    likeCount.value += isLiked.value ? 1 : -1
    ElMessage.success(isLiked.value ? '点赞成功' : '取消点赞')
  } catch (e) {
    isLiked.value = !isLiked.value
    likeCount.value += isLiked.value ? 1 : -1
    ElMessage.error('操作失败')
  }
}

// 收藏/取消收藏
const toggleCollect = async () => {
  try {
    isCollected.value = !isCollected.value
    collectCount.value += isCollected.value ? 1 : -1
    ElMessage.success(isCollected.value ? '收藏成功' : '取消收藏')
  } catch (e) {
    isCollected.value = !isCollected.value
    collectCount.value += isCollected.value ? 1 : -1
    ElMessage.error('操作失败')
  }
}

// 提交评论
const handleSubmitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  try {
    const newComment = {
      id: Date.now(),
      author: '当前用户',
      avatar: userAvatar.value,
      content: commentContent.value,
      time: new Date().toLocaleString(),
      likes: 0,
      isLiked: false,
      replies: []
    }
    comments.value.unshift(newComment)
    commentCount.value++
    totalComments.value++
    commentContent.value = ''
    ElMessage.success('评论发表成功')
  } catch (e) {
    ElMessage.error('评论发表失败')
  }
}

// 回复评论
const handleReply = (commentId: string) => {
  replyingTo.value = replyingTo.value === commentId ? null : commentId
  replyContent.value = ''
}

// 提交回复
const handleSubmitReply = async (commentId: string) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    const comment = comments.value.find(c => c.id === commentId)
    if (comment) {
      const newReply = {
        id: `${commentId}-${Date.now()}`,
        author: '当前用户',
        avatar: userAvatar.value,
        content: replyContent.value,
        time: new Date().toLocaleString()
      }
      if (!comment.replies) {
        comment.replies = []
      }
      comment.replies.push(newReply)
      replyContent.value = ''
      replyingTo.value = null
      ElMessage.success('回复发表成功')
    }
  } catch (e) {
    ElMessage.error('回复发表失败')
  }
}

// 评论点赞
const handleCommentLike = (commentId: string) => {
  const comment = comments.value.find(c => c.id === commentId)
  if (comment) {
    comment.isLiked = !comment.isLiked
    comment.likes += comment.isLiked ? 1 : -1
  }
}

// 分享
const handleShare = () => {
  shareLink.value = fullShareLink.value
  showShareDialog.value = true
}

// 复制链接
const copyLink = () => {
  navigator.clipboard.writeText(shareLink.value)
  ElMessage.success('链接已复制')
}

// 分享到平台
const shareTo = (platform: string) => {
  switch (platform) {
    case 'wechat':
      ElMessage.info('请打开微信扫描二维码分享')
      break
    case 'weibo':
      ElMessage.info('跳转到微博分享')
      break
    case 'qq':
      ElMessage.info('跳转到QQ分享')
      break
    case 'copy':
      copyLink()
      break
  }
  showShareDialog.value = false
}

// 评论分页
const handleCommentPageChange = () => {
  loadComments()
}

const handleCommentSizeChange = () => {
  commentPage.value = 1
  loadComments()
}

onMounted(() => {
  loadInteractionData()
  loadComments()
})
</script>

<style scoped>
.social-interaction {
  width: 100%;
}

/* 互动统计 */
.interaction-stats {
  display: flex;
  gap: 32px;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 20px;
  color: var(--el-text-color-secondary);
}

.stat-item:hover {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  transform: translateY(-2px);
}

.stat-item .el-icon.active {
  color: var(--el-color-primary);
}

/* 评论对话框 */
.comment-input-section {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--el-border-color);
}

.comment-input {
  flex: 1;
  border-radius: 8px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-height: 400px;
  overflow-y: auto;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: var(--el-color-primary-light-9);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.comment-item:hover {
  background: var(--el-color-primary-light-8);
}

.comment-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-author {
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.comment-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.comment-text {
  color: var(--el-text-color-primary);
  line-height: 1.5;
}

.comment-actions {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.action-item {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.action-item:hover {
  color: var(--el-color-primary);
}

.action-item .el-icon.active {
  color: var(--el-color-primary);
}

/* 回复列表 */
.reply-list {
  margin-top: 12px;
  padding-left: 44px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reply-item {
  display: flex;
  gap: 8px;
  padding: 12px;
  background: white;
  border-radius: 6px;
  border-left: 3px solid var(--el-color-primary-light-5);
}

.reply-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.reply-author {
  font-weight: 500;
  color: var(--el-text-color-primary);
  font-size: 14px;
}

.reply-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.reply-text {
  color: var(--el-text-color-primary);
  line-height: 1.4;
  font-size: 14px;
}

/* 回复输入 */
.reply-input-section {
  margin-top: 12px;
  padding-left: 44px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reply-input {
  border-radius: 6px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--el-border-color);
}

/* 分享对话框 */
.share-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.share-link h4,
.share-platforms h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
}

.link-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: var(--el-color-primary-light-9);
  border-radius: 6px;
}

.link-box span {
  font-size: 14px;
  color: var(--el-text-color-primary);
  word-break: break-all;
  flex: 1;
  margin-right: 12px;
}

.platforms {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.platform-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: var(--el-color-primary-light-9);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.platform-item:hover {
  background: var(--el-color-primary-light-8);
  transform: translateY(-2px);
}

.platform-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.platform-icon.wechat {
  color: #42b883;
}

.platform-icon.weibo {
  color: #ef4444;
}

.platform-icon.qq {
  color: #3b82f6;
}

.platform-icon.copy {
  color: #8b5cf6;
}

.platform-item span {
  font-size: 14px;
  color: var(--el-text-color-primary);
}

/* 响应式 */
@media (max-width: 768px) {
  .interaction-stats {
    gap: 16px;
    flex-wrap: wrap;
  }
  
  .stat-item {
    padding: 6px 12px;
  }
  
  .platforms {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>