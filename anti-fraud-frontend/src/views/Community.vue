<template>
  <div class="community">
    <!-- 头部 -->
    <div class="header">
      <h1>社区互助反诈</h1>
      <p>岳塘飞机坪社区 - 积分激励+全民参与</p>
    </div>

    <!-- 社区功能区 -->
    <div class="community-content">
      <!-- 左侧：帖子列表 -->
      <div class="posts-section">
        <!-- 发布帖子 -->
        <div class="post-create">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>发布帖子</span>
              </div>
            </template>
            <div class="post-form">
              <el-input
                v-model="newPost.title"
                placeholder="请输入帖子标题"
                maxlength="50"
              />
              <el-input
                v-model="newPost.content"
                type="textarea"
                :rows="4"
                placeholder="请分享您的反诈经验、案例或疑问..."
                maxlength="500"
              />
              <div class="post-actions">
                <el-select v-model="newPost.category" placeholder="选择分类">
                  <el-option value="experience" label="经验分享" />
                  <el-option value="case" label="案例分析" />
                  <el-option value="question" label="问题咨询" />
                  <el-option value="activity" label="活动参与" />
                </el-select>
                <el-button type="primary" @click="createPost" :loading="isCreating">发布</el-button>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 帖子列表 -->
        <div class="posts-list">
          <el-card 
            v-for="post in posts" 
            :key="post.id"
            shadow="hover"
            class="post-card"
          >
            <template #header>
              <div class="post-header">
                <div class="post-author">
                  <img :src="post.author.avatar" alt="Avatar" class="author-avatar" />
                  <div class="author-info">
                    <span class="author-name">{{ post.author.name }}</span>
                    <span class="post-time">{{ formatTime(post.createdAt) }}</span>
                  </div>
                </div>
                <el-tag :type="getCategoryType(post.category)">{{ getCategoryText(post.category) }}</el-tag>
              </div>
            </template>
            <div class="post-content">
              <h3 class="post-title">{{ post.title }}</h3>
              <p class="post-text">{{ post.content }}</p>
              <div class="post-attachments" v-if="post.attachments && post.attachments.length > 0">
                <img 
                  v-for="(attachment, index) in post.attachments" 
                  :key="index"
                  :src="attachment" 
                  alt="Attachment"
                  class="post-image"
                />
              </div>
            </div>
            <div class="post-footer">
              <div class="post-stats">
                <span class="stat-item" @click="likePost(post)">
                  <el-icon><Star /></el-icon>
                  {{ post.likes }}
                </span>
                <span class="stat-item" @click="showComments(post)">
                  <el-icon><ChatLineRound /></el-icon>
                  {{ post.comments.length }}
                </span>
                <span class="stat-item">
                  <el-icon><View /></el-icon>
                  {{ post.views }}
                </span>
              </div>
              <div class="post-actions">
                <el-button type="text" @click="likePost(post)">点赞</el-button>
                <el-button type="text" @click="showComments(post)">评论</el-button>
                <el-button type="text" @click="sharePost(post)">分享</el-button>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>

      <!-- 右侧：社区信息 -->
      <div class="community-info">
        <!-- 用户信息 -->
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="card-header">
              <span>用户信息</span>
            </div>
          </template>
          <div class="user-info">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20friendly%20person&image_size=square" alt="User Avatar" class="user-avatar" />
            <div class="user-details">
              <h3>{{ userInfo.name }}</h3>
              <p class="user-level">Lv.{{ userInfo.level }}</p>
              <div class="user-stats">
                <div class="stat-item">
                  <span class="stat-value">{{ userInfo.points }}</span>
                  <span class="stat-label">积分</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ userInfo.posts }}</span>
                  <span class="stat-label">帖子</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ userInfo.comments }}</span>
                  <span class="stat-label">评论</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 积分排行榜 -->
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="card-header">
              <span>积分排行榜</span>
            </div>
          </template>
          <div class="ranking-list">
            <div 
              v-for="(user, index) in rankingList" 
              :key="user.id"
              :class="['ranking-item', { 'top-three': index < 3 }]"
            >
              <div class="ranking-number">{{ index + 1 }}</div>
              <img :src="user.avatar" alt="Avatar" class="ranking-avatar" />
              <div class="ranking-info">
                <span class="ranking-name">{{ user.name }}</span>
                <span class="ranking-points">{{ user.points }}积分</span>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 社区活动 -->
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="card-header">
              <span>社区活动</span>
            </div>
          </template>
          <div class="activities-list">
            <div 
              v-for="activity in activities" 
              :key="activity.id"
              class="activity-item"
            >
              <div class="activity-header">
                <h4>{{ activity.title }}</h4>
                <el-tag :type="getActivityType(activity.status)">{{ activity.status }}</el-tag>
              </div>
              <p class="activity-desc">{{ activity.description }}</p>
              <div class="activity-meta">
                <span class="meta-item">{{ activity.startTime }} - {{ activity.endTime }}</span>
                <span class="meta-item">{{ activity.participants }}人参与</span>
              </div>
              <el-button 
                v-if="activity.status === '进行中'"
                type="primary" 
                size="small" 
                @click="joinActivity(activity)"
              >
                参与活动
              </el-button>
            </div>
          </div>
        </el-card>

        <!-- 积分规则 -->
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="card-header">
              <span>积分规则</span>
            </div>
          </template>
          <div class="points-rules">
            <div class="rule-item">
              <span class="rule-action">发布帖子</span>
              <span class="rule-points">+10积分</span>
            </div>
            <div class="rule-item">
              <span class="rule-action">发布评论</span>
              <span class="rule-points">+5积分</span>
            </div>
            <div class="rule-item">
              <span class="rule-action">帖子被点赞</span>
              <span class="rule-points">+2积分</span>
            </div>
            <div class="rule-item">
              <span class="rule-action">参与活动</span>
              <span class="rule-points">+20积分</span>
            </div>
            <div class="rule-item">
              <span class="rule-action">分享帖子</span>
              <span class="rule-points">+3积分</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 评论对话框 -->
    <el-dialog v-model="commentsVisible" :title="currentPost?.title + ' - 评论'" width="600px">
      <div class="comments-container">
        <!-- 评论输入 -->
        <div class="comment-input">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="请输入评论..."
            maxlength="200"
          />
          <div class="comment-actions">
            <el-button type="primary" @click="submitComment" :loading="isSubmitting">提交评论</el-button>
          </div>
        </div>

        <!-- 评论列表 -->
        <div class="comments-list">
          <div 
            v-for="(comment, index) in currentPost?.comments" 
            :key="index"
            class="comment-item"
          >
            <img :src="comment.author.avatar" alt="Avatar" class="comment-avatar" />
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.author.name }}</span>
                <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
              </div>
              <p class="comment-text">{{ comment.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Star, ChatLineRound, View } from '@element-plus/icons-vue'

const userInfo = ref({
  name: '用户123',
  level: 3,
  points: 250,
  posts: 12,
  comments: 35
})

const newPost = reactive({
  title: '',
  content: '',
  category: 'experience'
})

const isCreating = ref(false)
const posts = ref<any[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const rankingList = ref<any[]>([
  { id: 1, name: '用户A', points: 1200, avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%201&image_size=square' },
  { id: 2, name: '用户B', points: 980, avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%202&image_size=square' },
  { id: 3, name: '用户C', points: 850, avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%203&image_size=square' },
  { id: 4, name: '用户D', points: 720, avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%204&image_size=square' },
  { id: 5, name: '用户E', points: 650, avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%205&image_size=square' }
])

const activities = ref<any[]>([
  {
    id: 1,
    title: '反诈知识竞赛',
    description: '参与反诈知识竞赛，赢取积分奖励',
    startTime: '2026-04-15',
    endTime: '2026-04-30',
    status: '进行中',
    participants: 125
  },
  {
    id: 2,
    title: '反诈案例征集',
    description: '分享您遇到的反诈案例，赢取积分奖励',
    startTime: '2026-04-10',
    endTime: '2026-04-25',
    status: '进行中',
    participants: 89
  },
  {
    id: 3,
    title: '反诈宣传志愿者',
    description: '成为反诈宣传志愿者，参与社区宣传活动',
    startTime: '2026-04-01',
    endTime: '2026-04-10',
    status: '已结束',
    participants: 56
  }
])

const commentsVisible = ref(false)
const currentPost = ref<any>(null)
const newComment = ref('')
const isSubmitting = ref(false)

function formatTime(timeStr: string): string {
  return new Date(timeStr).toLocaleString()
}

function getCategoryType(category: string): string {
  const typeMap: Record<string, string> = {
    'experience': 'success',
    'case': 'warning',
    'question': 'info',
    'activity': 'primary'
  }
  return typeMap[category] || 'info'
}

function getCategoryText(category: string): string {
  const textMap: Record<string, string> = {
    'experience': '经验分享',
    'case': '案例分析',
    'question': '问题咨询',
    'activity': '活动参与'
  }
  return textMap[category] || category
}

function getActivityType(status: string): string {
  const typeMap: Record<string, string> = {
    '进行中': 'success',
    '已结束': 'info'
  }
  return typeMap[status] || 'info'
}

function createPost() {
  if (!newPost.title || !newPost.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }

  isCreating.value = true
  // 模拟发布帖子
  setTimeout(() => {
    const newPostItem = {
      id: Date.now(),
      title: newPost.title,
      content: newPost.content,
      category: newPost.category,
      author: {
        name: userInfo.value.name,
        avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20friendly%20person&image_size=square'
      },
      createdAt: new Date().toISOString(),
      likes: 0,
      views: 0,
      comments: []
    }
    posts.value.unshift(newPostItem)
    pagination.total++
    // 增加积分
    userInfo.value.points += 10
    userInfo.value.posts++
    // 重置表单
    newPost.title = ''
    newPost.content = ''
    newPost.category = 'experience'
    isCreating.value = false
    ElMessage.success('帖子发布成功，获得10积分')
  }, 1000)
}

function likePost(post: any) {
  post.likes++
  ElMessage.success('点赞成功')
}

function showComments(post: any) {
  currentPost.value = post
  commentsVisible.value = true
}

function submitComment() {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  isSubmitting.value = true
  // 模拟提交评论
  setTimeout(() => {
    const newCommentItem = {
      author: {
        name: userInfo.value.name,
        avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20friendly%20person&image_size=square'
      },
      content: newComment.value,
      createdAt: new Date().toISOString()
    }
    currentPost.value.comments.push(newCommentItem)
    // 增加积分
    userInfo.value.points += 5
    userInfo.value.comments++
    // 重置表单
    newComment.value = ''
    isSubmitting.value = false
    ElMessage.success('评论成功，获得5积分')
  }, 1000)
}

function sharePost(post: any) {
  // 模拟分享
  userInfo.value.points += 3
  ElMessage.success('分享成功，获得3积分')
}

function joinActivity(activity: any) {
  // 模拟参与活动
  activity.participants++
  userInfo.value.points += 20
  ElMessage.success('参与活动成功，获得20积分')
}

function handleSizeChange(size: number) {
  pagination.pageSize = size
  fetchPosts()
}

function handleCurrentChange(page: number) {
  pagination.currentPage = page
  fetchPosts()
}

function fetchPosts() {
  // 模拟获取帖子列表
  setTimeout(() => {
    posts.value = [
      {
        id: 1,
        title: '分享一次被电信诈骗的经历',
        content: '上周我接到一个自称是公安局的电话，对方说我涉及一起洗钱案件，需要配合调查。我一开始很紧张，但想起之前学习的反诈知识，就挂断了电话并报警。后来警方确认这是一起典型的电信诈骗案件。',
        category: 'experience',
        author: {
          name: '用户A',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%201&image_size=square'
        },
        createdAt: new Date(Date.now() - 86400000).toISOString(),
        likes: 45,
        views: 230,
        comments: [
          {
            author: {
              name: '用户B',
              avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%202&image_size=square'
            },
            content: '做得对！遇到此类电话一定要保持警惕。',
            createdAt: new Date(Date.now() - 72000000).toISOString()
          },
          {
            author: {
              name: '用户C',
              avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%203&image_size=square'
            },
            content: '感谢分享，这对大家都有帮助。',
            createdAt: new Date(Date.now() - 36000000).toISOString()
          }
        ]
      },
      {
        id: 2,
        title: '分析一起网络兼职诈骗案例',
        content: '最近看到一起网络兼职诈骗案例，骗子以刷单为名，要求受害者先垫付资金，然后以各种理由拒绝返款。受害者最终损失了数千元。提醒大家，任何要求先垫付资金的兼职都是诈骗。',
        category: 'case',
        author: {
          name: '用户D',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%204&image_size=square'
        },
        createdAt: new Date(Date.now() - 172800000).toISOString(),
        likes: 32,
        views: 180,
        comments: [
          {
            author: {
              name: '用户E',
              avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%205&image_size=square'
            },
            content: '刷单本身就是违法行为，大家一定要远离。',
            createdAt: new Date(Date.now() - 144000000).toISOString()
          }
        ]
      },
      {
        id: 3,
        title: '如何识别校园贷诈骗？',
        content: '最近有同学遇到了校园贷诈骗，想了解如何识别和防范校园贷诈骗。希望大家能分享一些经验和建议。',
        category: 'question',
        author: {
          name: '用户F',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%206&image_size=square'
        },
        createdAt: new Date(Date.now() - 259200000).toISOString(),
        likes: 28,
        views: 150,
        comments: [
          {
            author: {
              name: '用户G',
              avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%207&image_size=square'
            },
            content: '校园贷诈骗通常以无抵押、低利息为诱饵，大家要提高警惕。',
            createdAt: new Date(Date.now() - 230400000).toISOString()
          },
          {
            author: {
              name: '用户H',
              avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%208&image_size=square'
            },
            content: '遇到资金困难，应该向学校或正规金融机构寻求帮助。',
            createdAt: new Date(Date.now() - 216000000).toISOString()
          }
        ]
      }
    ]
    pagination.total = posts.value.length
  }, 1000)
}

onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.community {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 32px;
}

.header h1 {
  font-size: 32px;
  margin-bottom: 8px;
  color: #333;
}

.header p {
  font-size: 16px;
  color: #666;
}

.community-content {
  display: flex;
  gap: 24px;
}

.posts-section {
  flex: 1;
}

.community-info {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-card {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.post-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.posts-list {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.post-card {
  transition: all 0.3s ease;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-weight: 500;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #909399;
}

.post-content {
  margin: 16px 0;
}

.post-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #333;
}

.post-text {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.post-attachments {
  margin-top: 16px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.post-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.post-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  cursor: pointer;
  transition: color 0.3s ease;
}

.stat-item:hover {
  color: #409eff;
}

.post-actions {
  display: flex;
  gap: 12px;
}

.pagination {
  margin-top: 32px;
  text-align: right;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details {
  flex: 1;
}

.user-details h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #333;
}

.user-level {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
}

.user-stats {
  display: flex;
  gap: 16px;
}

.user-stats .stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-value {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #606266;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.ranking-item:hover {
  background: #f5f7fa;
}

.ranking-item.top-three {
  background: #f0f9eb;
}

.ranking-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.ranking-item.top-three .ranking-number {
  background: #67c23a;
  color: white;
}

.ranking-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.ranking-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ranking-name {
  font-size: 14px;
  color: #333;
}

.ranking-points {
  font-size: 12px;
  color: #606266;
}

.activities-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.activity-header h4 {
  margin: 0;
  font-size: 14px;
  color: #333;
}

.activity-desc {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.activity-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.points-rules {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rule-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 8px;
}

.rule-action {
  font-size: 14px;
  color: #333;
}

.rule-points {
  font-size: 14px;
  font-weight: bold;
  color: #67c23a;
}

.comments-container {
  padding: 20px 0;
}

.comment-input {
  margin-bottom: 24px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-author {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-text {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

@media (max-width: 768px) {
  .community {
    padding: 16px;
  }
  
  .community-content {
    flex-direction: column;
  }
  
  .community-info {
    width: 100%;
  }
  
  .post-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .post-stats {
    gap: 16px;
  }
  
  .user-info {
    flex-direction: column;
    text-align: center;
  }
  
  .user-stats {
    justify-content: space-around;
  }
  
  .ranking-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>