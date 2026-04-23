<template>
  <div class="community">
    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>社区互助反诈</h1>
          <div class="differentiator">
            <span class="tag">积分激励</span>
            <span class="tag">全民参与</span>
            <span class="competitor">对比：岳塘飞机坪社区</span>
          </div>
        </div>
        <div class="header-features">
          <div class="feature-item">
            <span class="feature-icon">🏆</span>
            <span class="feature-text">积分排行榜</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🎯</span>
            <span class="feature-text">每日挑战</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">👥</span>
            <span class="feature-text">全民互动</span>
          </div>
        </div>
      </div>
    </div>

    <div class="community-content">
      <div class="main-area">
        <div class="create-section">
          <el-card shadow="hover" class="create-card">
            <div class="create-header">
              <img :src="userInfo.avatar" alt="avatar" class="user-avatar-small" />
              <el-input
                v-model="newPost.content"
                type="textarea"
                :rows="3"
                placeholder="分享您的反诈经验，帮助更多人..."
                maxlength="500"
                show-word-limit
              />
            </div>
            <div class="create-actions">
              <div class="action-left">
                <el-select v-model="newPost.category" placeholder="选择分类" size="small">
                  <el-option value="experience" label="经验分享" />
                  <el-option value="case" label="案例分析" />
                  <el-option value="question" label="问题咨询" />
                  <el-option value="activity" label="活动参与" />
                </el-select>
                <el-input
                  v-model="newPost.title"
                  placeholder="帖子标题"
                  size="small"
                  maxlength="50"
                  style="width: 200px"
                />
              </div>
              <div class="action-right">
                <span class="points-hint">
                  <el-icon><Star /></el-icon>
                  发布帖子 +10积分
                </span>
                <el-button type="primary" @click="createPost" :loading="isCreating">
                  发布
                </el-button>
              </div>
            </div>
          </el-card>
        </div>

        <div class="filter-tabs">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="推荐" name="recommend" />
            <el-tab-pane label="最新" name="latest" />
            <el-tab-pane label="热议" name="hot" />
            <el-tab-pane label="我的关注" name="following" />
          </el-tabs>
          <div class="tab-actions">
            <el-button size="small" @click="refreshPosts">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>
        </div>

        <div class="posts-list">
          <el-card
            v-for="post in posts"
            :key="post.id"
            shadow="hover"
            class="post-card"
            :class="{ featured: post.featured }"
          >
            <div class="post-wrapper">
              <div class="post-vote" v-if="post.featured">
                <div class="vote-badge">
                  <el-icon><Star /></el-icon>
                  精华
                </div>
              </div>

              <div class="post-main">
                <div class="post-header">
                  <div class="author-info">
                    <img :src="post.author.avatar" alt="avatar" class="author-avatar" />
                    <div class="author-details">
                      <div class="author-row">
                        <span class="author-name">{{ post.author.name }}</span>
                        <el-tag size="small" :type="getLevelType(post.author.level)" effect="plain">
                          Lv.{{ post.author.level }}
                        </el-tag>
                        <span class="author-badge" v-if="post.author.badge">
                          {{ post.author.badge }}
                        </span>
                      </div>
                      <span class="post-time">{{ formatTime(post.createdAt) }}</span>
                    </div>
                  </div>
                  <el-tag :type="getCategoryType(post.category)" size="small">
                    {{ getCategoryText(post.category) }}
                  </el-tag>
                </div>

                <div class="post-body">
                  <h3 class="post-title">{{ post.title }}</h3>
                  <p class="post-content">{{ post.content }}</p>

                  <div class="post-images" v-if="post.images && post.images.length > 0">
                    <img
                      v-for="(img, index) in post.images"
                      :key="index"
                      :src="img"
                      alt="图片"
                      class="post-image"
                      @click="previewImage(img)"
                    />
                  </div>
                </div>

                <div class="post-footer">
                  <div class="interaction-bar">
                    <div
                      class="interaction-item"
                      :class="{ active: post.isLiked }"
                      @click="toggleLike(post)"
                    >
                      <el-icon><Star /></el-icon>
                      <span>{{ post.likes }}</span>
                    </div>
                    <div class="interaction-item" @click="showComments(post)">
                      <el-icon><ChatDotRound /></el-icon>
                      <span>{{ post.commentCount }}</span>
                    </div>
                    <div class="interaction-item" @click="sharePost(post)">
                      <el-icon><Share /></el-icon>
                      <span>分享</span>
                    </div>
                    <div class="interaction-item collect" :class="{ active: post.isCollected }" @click="toggleCollect(post)">
                      <el-icon><Collection /></el-icon>
                      <span>{{ post.collects }}</span>
                    </div>
                  </div>

                  <div class="reward-info" v-if="post.rewardPoints > 0">
                    <el-icon><Coin /></el-icon>
                    <span>+{{ post.rewardPoints }}积分</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="post-comments-preview" v-if="post.comments && post.comments.length > 0 && showCommentsPreview">
              <div
                v-for="(comment, index) in post.comments.slice(0, 2)"
                :key="index"
                class="comment-preview"
              >
                <span class="comment-author">{{ comment.author.name }}:</span>
                <span class="comment-text">{{ comment.content }}</span>
              </div>
              <div class="view-more" @click="showComments(post)" v-if="post.commentCount > 2">
                查看全部 {{ post.commentCount }} 条评论
              </div>
            </div>
          </el-card>
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>

      <div class="side-area">
        <el-card shadow="hover" class="user-card">
          <template #header>
            <div class="card-header">
              <span>我的社区</span>
              <el-tag type="warning" size="small">活跃度 {{ userInfo.activity }}</el-tag>
            </div>
          </template>
          <div class="user-profile">
            <div class="profile-header">
              <img :src="userInfo.avatar" alt="avatar" class="profile-avatar" />
              <div class="profile-badge" v-if="userInfo.title">
                {{ userInfo.title }}
              </div>
            </div>
            <h3 class="profile-name">{{ userInfo.name }}</h3>
            <div class="profile-level">
              <span class="level-badge">Lv.{{ userInfo.level }}</span>
              <el-progress
                :percentage="userInfo.levelProgress"
                :show-text="false"
                :color="levelProgressColor"
                style="width: 100px"
              />
              <span class="level-tip">再 {{ userInfo.nextLevelPoints }} 积分升级</span>
            </div>

            <div class="points-display">
              <div class="points-main">
                <span class="points-icon">🪙</span>
                <span class="points-value">{{ userInfo.points }}</span>
                <span class="points-label">积分</span>
              </div>
              <div class="points-stats">
                <div class="stat-item">
                  <span class="stat-value">{{ userInfo.posts }}</span>
                  <span class="stat-label">帖子</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ userInfo.comments }}</span>
                  <span class="stat-label">评论</span>
                </div>
                <div class="stat-item">
                  <span class="stat-value">{{ userInfo.likes }}</span>
                  <span class="stat-label">获赞</span>
                </div>
              </div>
            </div>

            <div class="daily-tasks">
              <h4>
                <el-icon><Coin /></el-icon>
                每日任务
              </h4>
              <div class="task-list">
                <div
                  v-for="task in dailyTasks"
                  :key="task.id"
                  class="task-item"
                  :class="{ completed: task.completed }"
                >
                  <div class="task-info">
                    <span class="task-name">{{ task.name }}</span>
                    <span class="task-reward">+{{ task.reward }}积分</span>
                  </div>
                  <el-button
                    v-if="!task.completed"
                    size="small"
                    type="primary"
                    @click="completeTask(task)"
                  >
                    去完成
                  </el-button>
                  <el-icon v-else class="task-done"><Check /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="ranking-card">
          <template #header>
            <div class="card-header">
              <span>积分排行榜</span>
              <el-link type="primary" :underline="false">查看全部</el-link>
            </div>
          </template>
          <div class="ranking-tabs">
            <span
              :class="{ active: rankingType === 'week' }"
              @click="rankingType = 'week'"
            >
              本周
            </span>
            <span
              :class="{ active: rankingType === 'month' }"
              @click="rankingType = 'month'"
            >
              本月
            </span>
            <span
              :class="{ active: rankingType === 'all' }"
              @click="rankingType = 'all'"
            >
              总榜
            </span>
          </div>
          <div class="ranking-list">
            <div
              v-for="(user, index) in rankingList"
              :key="user.id"
              :class="['ranking-item', { 'top-three': index < 3 }]"
            >
              <div class="ranking-position">
                <span v-if="index === 0" class="medal gold">🥇</span>
                <span v-else-if="index === 1" class="medal silver">🥈</span>
                <span v-else-if="index === 2" class="medal bronze">🥉</span>
                <span v-else class="position-number">{{ index + 1 }}</span>
              </div>
              <img :src="user.avatar" alt="avatar" class="ranking-avatar" />
              <div class="ranking-info">
                <span class="ranking-name">{{ user.name }}</span>
                <span class="ranking-detail">{{ user.posts }}帖子 · {{ user.likes }}获赞</span>
              </div>
              <span class="ranking-points">{{ user.points }}积分</span>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="activity-card">
          <template #header>
            <div class="card-header">
              <span>社区活动</span>
              <el-tag type="success" size="small" effect="plain">进行中</el-tag>
            </div>
          </template>
          <div class="activity-list">
            <div
              v-for="activity in activities"
              :key="activity.id"
              class="activity-item"
              :class="{ joined: activity.joined }"
            >
              <div class="activity-banner" :style="{ backgroundImage: `url(${activity.banner})` }">
                <span class="activity-tag">{{ activity.tag }}</span>
              </div>
              <div class="activity-content">
                <h4>{{ activity.title }}</h4>
                <p>{{ activity.description }}</p>
                <div class="activity-meta">
                  <span>
                    <el-icon><User /></el-icon>
                    {{ activity.participants }}人参与
                  </span>
                  <span>
                    <el-icon><Coin /></el-icon>
                    {{ activity.reward }}积分
                  </span>
                </div>
                <el-button
                  v-if="!activity.joined"
                  type="primary"
                  size="small"
                  @click="joinActivity(activity)"
                >
                  立即参与
                </el-button>
                <el-tag v-else type="success" size="small">已参与</el-tag>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="hot-topics-card">
          <template #header>
            <div class="card-header">
              <span>热门话题</span>
            </div>
          </template>
          <div class="hot-topics">
            <div
              v-for="(topic, index) in hotTopics"
              :key="topic.id"
              class="topic-item"
              @click="viewTopic(topic)"
            >
              <span class="topic-rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
              <div class="topic-info">
                <span class="topic-name">{{ topic.name }}</span>
                <span class="topic-count">{{ topic.count }}讨论</span>
              </div>
              <el-icon class="topic-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog v-model="commentsVisible" :title="currentPost?.title" width="700px" class="comments-dialog">
      <div class="comments-wrapper" v-if="currentPost">
        <div class="original-post">
          <div class="post-author">
            <img :src="currentPost.author.avatar" alt="avatar" class="author-avatar" />
            <div class="author-details">
              <span class="author-name">{{ currentPost.author.name }}</span>
              <span class="post-time">{{ formatTime(currentPost.createdAt) }}</span>
            </div>
          </div>
          <p class="post-text">{{ currentPost.content }}</p>
        </div>

        <div class="comments-section">
          <h4>评论 ({{ currentPost.commentCount }})</h4>
          <div class="comment-input-wrapper">
            <el-input
              v-model="newComment"
              type="textarea"
              :rows="3"
              placeholder="发表您的看法..."
              maxlength="200"
              show-word-limit
            />
            <el-button type="primary" @click="submitComment" :loading="isSubmitting">
              发表评论
            </el-button>
          </div>

          <div class="comments-list">
            <div
              v-for="(comment, index) in currentPost.comments"
              :key="index"
              class="comment-item"
            >
              <img :src="comment.author.avatar" alt="avatar" class="comment-avatar" />
              <div class="comment-body">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.author.name }}</span>
                  <el-tag size="small" effect="plain">Lv.{{ comment.author.level }}</el-tag>
                  <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-actions">
                  <span class="action-item" @click="likeComment(comment)">
                    <el-icon><Star /></el-icon>
                    {{ comment.likes }}
                  </span>
                  <span class="action-item" @click="replyComment(comment)">
                    <el-icon><ChatDotRound /></el-icon>
                    回复
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Star,
  ChatDotRound,
  Share,
  Collection,
  Coin,
  Refresh,
  Check,
  User,
  ArrowRight
} from '@element-plus/icons-vue'

interface Author {
  name: string
  avatar: string
  level: number
  badge?: string
}

interface Comment {
  author: Author
  content: string
  createdAt: number
  likes: number
}

interface Post {
  id: number
  title: string
  content: string
  author: Author
  category: string
  createdAt: number
  likes: number
  commentCount: number
  collects: number
  views: number
  images?: string[]
  featured?: boolean
  rewardPoints: number
  isLiked?: boolean
  isCollected?: boolean
  comments: Comment[]
}

interface Task {
  id: number
  name: string
  reward: number
  completed: boolean
}

interface Activity {
  id: number
  title: string
  description: string
  banner: string
  tag: string
  participants: number
  reward: number
  joined: boolean
}

interface Topic {
  id: number
  name: string
  count: number
}

const userInfo = ref({
  name: '反诈达人',
  avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20friendly%20person&image_size=square',
  level: 5,
  title: '反诈标兵',
  points: 2850,
  posts: 28,
  comments: 156,
  likes: 892,
  activity: 85,
  levelProgress: 65,
  nextLevelPoints: 150
})

const newPost = reactive({
  title: '',
  content: '',
  category: 'experience'
})

const isCreating = ref(false)
const activeTab = ref('recommend')
const rankingType = ref('week')
const showCommentsPreview = ref(true)
const posts = ref<Post[]>([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 100
})

const commentsVisible = ref(false)
const currentPost = ref<Post | null>(null)
const newComment = ref('')
const isSubmitting = ref(false)

const dailyTasks = ref<Task[]>([
  { id: 1, name: '每日登录', reward: 5, completed: true },
  { id: 2, name: '发布帖子', reward: 10, completed: false },
  { id: 3, name: '评论他人', reward: 5, completed: false },
  { id: 4, name: '点赞帖子', reward: 2, completed: true }
])

const rankingList = ref([
  { id: 1, name: '防诈先锋', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20avatar%20person1&image_size=square', points: 12580, posts: 256, likes: 3890 },
  { id: 2, name: '正义使者', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20avatar%20person2&image_size=square', points: 9830, posts: 189, likes: 2650 },
  { id: 3, name: '社区守护者', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20avatar%20person3&image_size=square', points: 7650, posts: 145, likes: 1980 },
  { id: 4, name: '反诈新手', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20avatar%20person4&image_size=square', points: 5200, posts: 98, likes: 1250 },
  { id: 5, name: '警惕者', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20avatar%20person5&image_size=square', points: 4580, posts: 87, likes: 980 }
])

const activities = ref<Activity[]>([
  {
    id: 1,
    title: '防诈知识竞赛',
    description: '参与答题竞赛，赢取丰厚积分奖励',
    banner: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=quiz%20competition%20banner&image_size=landscape',
    tag: '竞赛',
    participants: 1256,
    reward: 100,
    joined: false
  },
  {
    id: 2,
    title: '反诈案例征集',
    description: '分享您的真实经历，帮助更多人',
    banner: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=community%20share%20banner&image_size=landscape',
    tag: '征集',
    participants: 856,
    reward: 50,
    joined: true
  }
])

const hotTopics = ref<Topic[]>([
  { id: 1, name: '最新诈骗手法曝光', count: 2580 },
  { id: 2, name: 'AI换脸诈骗防范', count: 1860 },
  { id: 3, name: '老年人防诈指南', count: 1520 },
  { id: 4, name: '校园贷危害警示', count: 1280 },
  { id: 5, name: '网络交友防骗', count: 980 }
])

const levelProgressColor = computed(() => {
  const progress = userInfo.value.levelProgress
  if (progress < 30) return '#f56c6c'
  if (progress < 70) return '#e6a23c'
  return '#67c23a'
})

function getCategoryType(category: string): string {
  const typeMap: Record<string, string> = {
    experience: 'success',
    case: 'warning',
    question: 'primary',
    activity: 'danger'
  }
  return typeMap[category] || 'info'
}

function getCategoryText(category: string): string {
  const textMap: Record<string, string> = {
    experience: '经验分享',
    case: '案例分析',
    question: '问题咨询',
    activity: '活动参与'
  }
  return textMap[category] || category
}

function getLevelType(level: number): string {
  if (level >= 10) return 'danger'
  if (level >= 5) return 'warning'
  return 'primary'
}

function formatTime(timestamp: number): string {
  const now = Date.now()
  const diff = now - timestamp
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  return new Date(timestamp).toLocaleDateString()
}

function handleTabChange(tab: string) {
  ElMessage.success(`已切换到${tab === 'recommend' ? '推荐' : tab === 'latest' ? '最新' : tab === 'hot' ? '热议' : '我的关注'}`)
  loadPosts()
}

function handleSizeChange(size: number) {
  pagination.pageSize = size
  loadPosts()
}

function handleCurrentChange(page: number) {
  pagination.currentPage = page
  loadPosts()
}

function loadPosts() {
  const mockPosts: Post[] = [
    {
      id: 1,
      title: '刚刚差点被骗！大家要小心这种新型诈骗',
      content: '今天收到一个电话，说是我网购的客服，说商品有问题要给我退款。一开始我还信了，后来对方让我提供银行卡信息，我才发现不对劲。大家一定要记住，正规客服不会索要银行卡密码！',
      author: { name: '警惕之心', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20person%20cautious&image_size=square', level: 8, badge: '防诈达人' },
      category: 'case',
      createdAt: Date.now() - 1800000,
      likes: 256,
      commentCount: 45,
      collects: 89,
      views: 1520,
      featured: true,
      rewardPoints: 20,
      isLiked: false,
      isCollected: false,
      comments: [
        { author: { name: '热心网友', avatar: '', level: 3 }, content: '还好你警惕性高！现在骗子手段越来越多了', createdAt: Date.now() - 900000, likes: 12 },
        { author: { name: '反诈志愿者', avatar: '', level: 12 }, content: '感谢分享！这种诈骗确实很常见，大家都要小心', createdAt: Date.now() - 600000, likes: 8 }
      ]
    },
    {
      id: 2,
      title: '分享一个识别钓鱼网站的方法',
      content: '很多人不知道怎么识别钓鱼网站，其实很简单：1.看域名是否正确 2.看网站是否有安全证书 3.看网址是否有https://前缀。记住这三点，基本能避免大部分钓鱼网站！',
      author: { name: '网络安全员', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20person%20tech&image_size=square', level: 10, badge: '技术大牛' },
      category: 'experience',
      createdAt: Date.now() - 7200000,
      likes: 189,
      commentCount: 32,
      collects: 67,
      views: 980,
      rewardPoints: 15,
      isLiked: true,
      isCollected: false,
      comments: []
    },
    {
      id: 3,
      title: '老年人如何防范电信诈骗？',
      content: '作为子女，我们有责任帮助父母防范诈骗。我的经验是：1.给父母普及常见的诈骗类型 2.给父母设置手机安全防护 3.经常关心父母的生活。希望大家都能保护好家里的老人！',
      author: { name: '孝顺儿女', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20person%20caring&image_size=square', level: 6 },
      category: 'experience',
      createdAt: Date.now() - 86400000,
      likes: 425,
      commentCount: 78,
      collects: 156,
      views: 2580,
      featured: true,
      rewardPoints: 25,
      isLiked: false,
      isCollected: true,
      comments: []
    },
    {
      id: 4,
      title: '这个刷单兼职是不是骗局？',
      content: '今天看到一个刷单兼职，说是日赚300元。需要先垫付资金，然后返还本金和佣金。请问这种是真的吗？我感觉有点不对劲，但又说不上来哪里有问题...',
      author: { name: '疑惑新人', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20person%20confused&image_size=square', level: 2 },
      category: 'question',
      createdAt: Date.now() - 172800000,
      likes: 56,
      commentCount: 23,
      collects: 12,
      views: 450,
      rewardPoints: 0,
      isLiked: false,
      isCollected: false,
      comments: []
    }
  ]
  posts.value = mockPosts
}

function createPost() {
  if (!newPost.content.trim()) {
    ElMessage.warning('请输入帖子内容')
    return
  }
  isCreating.value = true
  setTimeout(() => {
    ElMessage.success('帖子发布成功！+10积分')
    userInfo.value.points += 10
    userInfo.value.posts++
    newPost.content = ''
    newPost.title = ''
    isCreating.value = false
    loadPosts()
  }, 1000)
}

function refreshPosts() {
  ElMessage.success('已刷新')
  loadPosts()
}

function toggleLike(post: Post) {
  post.isLiked = !post.isLiked
  post.likes += post.isLiked ? 1 : -1
  if (post.isLiked) {
    ElMessage.success('点赞成功！+2积分')
    userInfo.value.points += 2
    userInfo.value.likes++
  }
}

function toggleCollect(post: Post) {
  post.isCollected = !post.isCollected
  post.collects += post.isCollected ? 1 : -1
  ElMessage.success(post.isCollected ? '已收藏帖子' : '已取消收藏')
}

function showComments(post: Post) {
  currentPost.value = post
  commentsVisible.value = true
}

function sharePost(post: Post) {
  ElMessage.success('分享链接已复制到剪贴板！+3积分')
  userInfo.value.points += 3
}

function submitComment() {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  isSubmitting.value = true
  setTimeout(() => {
    if (currentPost.value) {
      currentPost.value.comments.unshift({
        author: { name: userInfo.value.name, avatar: userInfo.value.avatar, level: userInfo.value.level },
        content: newComment.value,
        createdAt: Date.now(),
        likes: 0
      })
      currentPost.value.commentCount++
    }
    ElMessage.success('评论成功！+5积分')
    userInfo.value.points += 5
    userInfo.value.comments++
    newComment.value = ''
    isSubmitting.value = false
  }, 1000)
}

function likeComment(comment: Comment) {
  comment.likes++
  ElMessage.success('点赞成功！')
}

function replyComment(comment: Comment) {
  newComment.value = `@${comment.author.name} `
  ElMessage.info('请输入回复内容')
}

function previewImage(img: string) {
  ElMessage.info('图片预览功能开发中')
}

function completeTask(task: Task) {
  task.completed = true
  userInfo.value.points += task.reward
  ElMessage.success(`任务完成！+${task.reward}积分`)
}

function joinActivity(activity: Activity) {
  activity.joined = true
  activity.participants++
  ElMessage.success(`已成功参与活动！+${activity.reward}积分`)
  userInfo.value.points += activity.reward
}

function viewTopic(topic: Topic) {
  ElMessage.info(`正在加载话题：${topic.name}`)
}

onMounted(() => {
  loadPosts()
  ElMessage.success({
    message: '社区互助反诈已优化完成，积分激励全民参与',
    duration: 3000
  })
})
</script>

<style scoped>
.community {
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f0f3f7 0%, #e8ecf1 100%);
  min-height: calc(100vh - 60px);
}

.header {
  margin-bottom: 24px;
}

.header-content {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-radius: 16px;
  padding: 32px;
  color: white;
  box-shadow: 0 8px 32px rgba(64, 158, 255, 0.3);
}

.header-text h1 {
  font-size: 32px;
  margin: 0 0 12px 0;
}

.differentiator {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.tag {
  background: rgba(255, 255, 255, 0.2);
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  backdrop-filter: blur(10px);
}

.competitor {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  margin-left: 8px;
}

.header-features {
  display: flex;
  gap: 32px;
  margin-top: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.feature-icon {
  font-size: 24px;
}

.feature-text {
  font-size: 14px;
  opacity: 0.9;
}

.community-content {
  display: flex;
  gap: 24px;
}

.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.side-area {
  width: 380px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.create-card {
  background: white;
}

.create-header {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.user-avatar-small {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  flex-shrink: 0;
}

.create-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-left {
  display: flex;
  gap: 12px;
}

.action-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.points-hint {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.filter-tabs {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 12px 20px;
  border-radius: 12px;
}

.tab-actions {
  display: flex;
  gap: 8px;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.post-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.post-card.featured {
  border-left: 4px solid #409eff;
}

.post-wrapper {
  display: flex;
}

.post-vote {
  width: 48px;
  background: linear-gradient(180deg, #fef0f9, #fff);
  display: flex;
  justify-content: center;
  padding-top: 16px;
}

.vote-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #f56c6c;
  font-size: 12px;
}

.post-main {
  flex: 1;
  padding: 20px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.author-info {
  display: flex;
  gap: 12px;
}

.author-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.author-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  font-weight: bold;
  color: #333;
  font-size: 15px;
}

.author-badge {
  font-size: 11px;
  color: #f56c6c;
  background: #fef0f0;
  padding: 2px 8px;
  border-radius: 8px;
}

.post-time {
  font-size: 12px;
  color: #909399;
}

.post-body {
  margin-bottom: 16px;
}

.post-title {
  margin: 0 0 8px 0;
  font-size: 17px;
  color: #333;
  font-weight: bold;
}

.post-content {
  margin: 0;
  font-size: 15px;
  color: #606266;
  line-height: 1.7;
}

.post-images {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.post-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.post-image:hover {
  transform: scale(1.05);
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.interaction-bar {
  display: flex;
  gap: 24px;
}

.interaction-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #909399;
  cursor: pointer;
  transition: color 0.3s ease;
}

.interaction-item:hover {
  color: #409eff;
}

.interaction-item.active {
  color: #f56c6c;
}

.interaction-item.collect.active {
  color: #e6a23c;
}

.reward-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #e6a23c;
}

.post-comments-preview {
  background: #fafafa;
  padding: 12px 20px;
  border-top: 1px solid #ebeef5;
}

.comment-preview {
  font-size: 13px;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-preview:last-child {
  margin-bottom: 0;
}

.comment-author {
  color: #409eff;
  font-weight: 500;
}

.comment-text {
  color: #606266;
  margin-left: 4px;
}

.view-more {
  font-size: 13px;
  color: #909399;
  cursor: pointer;
  margin-top: 8px;
}

.view-more:hover {
  color: #409eff;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.user-card {
  background: white;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-profile {
  text-align: center;
}

.profile-header {
  position: relative;
  display: inline-block;
  margin-bottom: 12px;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 3px solid #409eff;
}

.profile-badge {
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #f56c6c, #e65454);
  color: white;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 8px;
  white-space: nowrap;
}

.profile-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.profile-level {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  margin-bottom: 20px;
}

.level-badge {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
  padding: 4px 16px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: bold;
}

.level-tip {
  font-size: 11px;
  color: #909399;
}

.points-display {
  background: linear-gradient(135deg, #f0f9eb, #e8f5e9);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
}

.points-main {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
}

.points-icon {
  font-size: 24px;
}

.points-value {
  font-size: 32px;
  font-weight: bold;
  color: #67c23a;
}

.points-label {
  font-size: 14px;
  color: #67c23a;
}

.points-stats {
  display: flex;
  justify-content: space-around;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 11px;
  color: #909399;
}

.daily-tasks {
  text-align: left;
}

.daily-tasks h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 6px;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: #f5f7fa;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.task-item:hover {
  background: #e4e8f0;
}

.task-item.completed {
  opacity: 0.6;
}

.task-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.task-name {
  font-size: 13px;
  color: #333;
}

.task-reward {
  font-size: 11px;
  color: #67c23a;
}

.task-done {
  color: #67c23a;
  font-size: 18px;
}

.ranking-card {
  background: white;
}

.ranking-tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.ranking-tabs span {
  font-size: 13px;
  color: #909399;
  cursor: pointer;
  padding-bottom: 4px;
  border-bottom: 2px solid transparent;
  transition: all 0.3s ease;
}

.ranking-tabs span.active {
  color: #409eff;
  border-color: #409eff;
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
  border-radius: 10px;
  transition: background 0.3s ease;
}

.ranking-item:hover {
  background: #f5f7fa;
}

.ranking-item.top-three {
  background: linear-gradient(135deg, #fff9f9, #fff);
}

.ranking-position {
  width: 28px;
  text-align: center;
}

.medal {
  font-size: 20px;
}

.position-number {
  font-size: 14px;
  color: #909399;
}

.ranking-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
}

.ranking-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.ranking-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.ranking-detail {
  font-size: 11px;
  color: #909399;
}

.ranking-points {
  font-size: 13px;
  color: #e6a23c;
  font-weight: 500;
}

.activity-card {
  background: white;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.activity-item:hover {
  border-color: #409eff;
}

.activity-item.joined {
  border-color: #67c23a;
}

.activity-banner {
  height: 80px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.activity-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  background: linear-gradient(135deg, #f56c6c, #e65454);
  color: white;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 8px;
}

.activity-content {
  padding: 12px;
}

.activity-content h4 {
  margin: 0 0 6px 0;
  font-size: 15px;
  color: #333;
}

.activity-content p {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #909399;
}

.activity-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #606266;
  margin-bottom: 10px;
}

.activity-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.hot-topics-card {
  background: white;
}

.hot-topics {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.topic-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  background: #f5f7fa;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.topic-item:hover {
  background: #e4e8f0;
}

.topic-rank {
  width: 20px;
  font-size: 14px;
  font-weight: bold;
  color: #909399;
  text-align: center;
}

.topic-rank.top {
  color: #f56c6c;
}

.topic-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.topic-name {
  font-size: 14px;
  color: #333;
}

.topic-count {
  font-size: 11px;
  color: #909399;
}

.topic-arrow {
  color: #909399;
  font-size: 14px;
}

.comments-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.comments-wrapper {
  max-height: 60vh;
  overflow-y: auto;
}

.original-post {
  padding: 20px;
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
}

.original-post .post-author {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.original-post .post-text {
  margin: 0;
  font-size: 15px;
  color: #606266;
  line-height: 1.7;
}

.comments-section {
  padding: 20px;
}

.comments-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
}

.comment-input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.comment-author {
  font-weight: 500;
  font-size: 14px;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-text {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  cursor: pointer;
}

.action-item:hover {
  color: #409eff;
}

@media (max-width: 1200px) {
  .community-content {
    flex-direction: column;
  }

  .side-area {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .side-area > * {
    flex: 1;
    min-width: 300px;
  }
}

@media (max-width: 768px) {
  .community {
    padding: 16px;
  }

  .header-content {
    padding: 20px;
  }

  .header-text h1 {
    font-size: 24px;
  }

  .header-features {
    flex-wrap: wrap;
    gap: 16px;
  }

  .filter-tabs {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .interaction-bar {
    flex-wrap: wrap;
    gap: 12px;
  }

  .ranking-tabs {
    gap: 12px;
  }
}
</style>
