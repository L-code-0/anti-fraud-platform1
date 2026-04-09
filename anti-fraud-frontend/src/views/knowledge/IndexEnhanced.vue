<template>
  <div class="knowledge-index">
    <!-- 页面标题区 -->
    <div class="page-header">
      <div class="header-info">
        <h1>知识学习中心</h1>
        <p class="subtitle">学习反诈知识，提升防骗意识</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="$router.push('/knowledge/recommended')">
          <el-icon><MagicStick /></el-icon>智能学习路径
        </el-button>
        <el-button @click="$router.push('/knowledge/history')">
          <el-icon><Clock /></el-icon>学习历史
        </el-button>
      </div>
    </div>
    
    <!-- 学习进度总览 -->
    <el-card class="progress-overview-card">
      <div class="progress-overview">
        <div class="progress-main">
          <el-progress type="circle" :percentage="overallProgress" :width="140" :stroke-width="12">
            <template #default="{ percentage }">
              <div class="progress-circle-content">
                <span class="percentage-value">{{ percentage }}%</span>
                <span class="percentage-label">总体进度</span>
              </div>
            </template>
          </el-progress>
        </div>
        <div class="progress-details">
          <div class="detail-item">
            <div class="detail-icon knowledge">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="detail-info">
              <span class="detail-label">知识学习</span>
              <div class="detail-progress">
                <el-progress :percentage="knowledgeProgress" :show-text="false" :stroke-width="6" />
                <span class="detail-value">{{ learnedKnowledgeCount }}/{{ totalKnowledgeCount }}篇</span>
              </div>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-icon test">
              <el-icon><Edit /></el-icon>
            </div>
            <div class="detail-info">
              <span class="detail-label">测试完成</span>
              <div class="detail-progress">
                <el-progress :percentage="testProgress" :show-text="false" :stroke-width="6" />
                <span class="detail-value">{{ completedTestCount }}/{{ totalTestCount }}次</span>
              </div>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-icon simulation">
              <el-icon><VideoPlay /></el-icon>
            </div>
            <div class="detail-info">
              <span class="detail-label">演练完成</span>
              <div class="detail-progress">
                <el-progress :percentage="simulationProgress" :show-text="false" :stroke-width="6" />
                <span class="detail-value">{{ completedSimulationCount }}/{{ totalSimulationCount }}个</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 学习日历 -->
    <el-card class="learning-calendar-card">
      <template #header>
        <div class="card-header">
          <span>学习日历</span>
          <el-radio-group v-model="calendarView" size="small">
            <el-radio-button value="month">月</el-radio-button>
            <el-radio-button value="week">周</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <el-calendar v-model="selectedDate">
        <template #date-cell="{ data }">
          <div class="calendar-cell" :class="{ 'is-selected': data.isSelected }">
            <span class="date-number">{{ data.day.split('-').slice(2).join('-') }}</span>
            <div class="learning-indicators">
              <span 
                v-if="hasLearningActivity(data.day, 'knowledge')" 
                class="indicator knowledge"
                title="知识学习"
              ></span>
              <span 
                v-if="hasLearningActivity(data.day, 'test')" 
                class="indicator test"
                title="测试"
              ></span>
              <span 
                v-if="hasLearningActivity(data.day, 'simulation')" 
                class="indicator simulation"
                title="演练"
              ></span>
            </div>
          </div>
        </template>
      </el-calendar>
    </el-card>
    
    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <div class="filter-bar">
        <!-- 搜索框 -->
        <el-input
          v-model="searchQuery"
          placeholder="搜索知识内容..."
          :prefix-icon="Search"
          clearable
          class="search-input"
          @input="handleSearch"
        />
        
        <!-- 分类筛选 -->
        <el-select v-model="selectedCategory" placeholder="全部分类" clearable class="filter-select">
          <el-option
            v-for="cat in categories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
        
        <!-- 难度筛选 -->
        <el-select v-model="selectedDifficulty" placeholder="全部难度" clearable class="filter-select">
          <el-option label="简单" :value="1" />
          <el-option label="中等" :value="2" />
          <el-option label="困难" :value="3" />
        </el-select>
        
        <!-- 排序 -->
        <el-select v-model="sortBy" class="filter-select">
          <el-option label="最新发布" value="latest" />
          <el-option label="最多浏览" value="views" />
          <el-option label="最多收藏" value="collects" />
          <el-option label="学习人数" value="learners" />
        </el-select>
        
        <!-- 视图切换 -->
        <div class="view-toggle">
          <el-tooltip content="网格视图">
            <el-button :type="viewMode === 'grid' ? 'primary' : 'default'" @click="viewMode = 'grid'" circle>
              <el-icon><Grid /></el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip content="列表视图">
            <el-button :type="viewMode === 'list' ? 'primary' : 'default'" @click="viewMode = 'list'" circle>
              <el-icon><List /></el-icon>
            </el-button>
          </el-tooltip>
        </div>
      </div>
      
      <!-- 标签筛选 -->
      <div class="tag-filters">
        <el-tag
          v-for="tag in hotTags"
          :key="tag"
          :type="selectedTags.includes(tag) ? 'primary' : 'info'"
          class="filter-tag"
          @click="toggleTag(tag)"
        >
          {{ tag }}
        </el-tag>
      </div>
    </el-card>
    
    <!-- 知识列表 -->
    <div v-if="loading" class="loading-container">
      <div class="skeleton-grid">
        <el-card v-for="i in 8" :key="i" class="skeleton-card">
          <div class="skeleton skeleton-image"></div>
          <div class="skeleton skeleton-title"></div>
          <div class="skeleton skeleton-text"></div>
          <div class="skeleton skeleton-text" style="width: 60%"></div>
        </el-card>
      </div>
    </div>
    
    <div v-else-if="viewMode === 'grid'" class="knowledge-grid">
      <el-card 
        v-for="item in filteredKnowledgeList" 
        :key="item.id"
        class="knowledge-card"
        :class="{ 'has-progress': item.readProgress > 0 }"
        @click="$router.push(`/knowledge/${item.id}`)"
      >
        <!-- 封面图 -->
        <div class="card-cover">
          <img :src="item.coverImage || '/default-cover.jpg'" :alt="item.title" />
          <div class="cover-overlay">
            <el-button type="primary" size="large" circle @click.stop="$router.push(`/knowledge/${item.id}`)">
              <el-icon><VideoPlay /></el-icon>
            </el-button>
          </div>
          <div class="quick-preview-btn" @click.stop="handleQuickPreview(item)">
            <el-icon><ZoomIn /></el-icon>
            <span>快速预览</span>
          </div>
          <!-- 难度标签 -->
          <el-tag 
            class="difficulty-tag"
            :type="getDifficultyType(item.difficulty)"
            size="small"
          >
            {{ getDifficultyText(item.difficulty) }}
          </el-tag>
        </div>
        
        <!-- 内容 -->
        <div class="card-content">
          <h3 class="card-title">{{ item.title }}</h3>
          <p class="card-summary">{{ item.summary }}</p>
          
          <!-- 阅读进度 -->
          <div class="reading-progress" v-if="item.readProgress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: item.readProgress + '%' }"></div>
            </div>
            <span class="progress-text">{{ item.readProgress }}%</span>
          </div>
          
          <!-- 标签 -->
          <div class="card-tags">
            <el-tag size="small" type="primary">{{ item.categoryName }}</el-tag>
            <el-tag v-for="tag in item.tags?.slice(0, 2)" :key="tag" size="small">
              {{ tag }}
            </el-tag>
          </div>
          
          <!-- 统计信息 -->
          <div class="card-stats">
            <span class="stat-item">
              <el-icon><View /></el-icon>
              {{ formatNumber(item.viewCount) }}
            </span>
            <span class="stat-item">
              <el-icon><Star /></el-icon>
              {{ formatNumber(item.collectCount) }}
            </span>
            <span class="stat-item">
              <el-icon><Clock /></el-icon>
              {{ item.readTime }}分钟
            </span>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="card-actions">
          <el-button 
            :type="item.isCollected ? 'warning' : 'default'" 
            :icon="item.isCollected ? 'Star' : 'Star'"
            circle
            @click.stop="handleToggleCollect(item)"
          />
          <el-button 
            :type="item.isLiked ? 'primary' : 'default'" 
            :icon="item.isLiked ? 'Star' : 'Star'"
            circle
            @click.stop="handleToggleLike(item)"
          />
          <el-button icon="Share" circle @click.stop="handleShare(item)" />
        </div>
      </el-card>
    </div>
    
    <div v-else class="knowledge-list">
      <el-card 
        v-for="item in filteredKnowledgeList" 
        :key="item.id"
        class="knowledge-list-item"
        @click="$router.push(`/knowledge/${item.id}`)"
      >
        <div class="list-item-cover">
          <img :src="item.coverImage || '/default-cover.jpg'" :alt="item.title" />
          <div class="list-item-progress" v-if="item.readProgress">
            <el-progress 
              type="circle" 
              :percentage="item.readProgress" 
              :width="50"
              :stroke-width="4"
            >
              <template #default="{ percentage }">
                <span class="progress-mini">{{ percentage }}%</span>
              </template>
            </el-progress>
          </div>
        </div>
        <div class="list-item-content">
          <div class="list-item-header">
            <h3>{{ item.title }}</h3>
            <el-tag :type="getDifficultyType(item.difficulty)" size="small">
              {{ getDifficultyText(item.difficulty) }}
            </el-tag>
          </div>
          <p class="list-item-summary">{{ item.summary }}</p>
          <div class="list-item-tags">
            <el-tag size="small" type="primary">{{ item.categoryName }}</el-tag>
            <el-tag v-for="tag in item.tags?.slice(0, 3)" :key="tag" size="small">
              {{ tag }}
            </el-tag>
          </div>
        </div>
        <div class="list-item-stats">
          <div class="stat">
            <el-icon><View /></el-icon>
            <span>{{ formatNumber(item.viewCount) }}</span>
          </div>
          <div class="stat">
            <el-icon><Clock /></el-icon>
            <span>{{ item.readTime }}分钟</span>
          </div>
          <div class="stat">
            <el-icon><User /></el-icon>
            <span>{{ formatNumber(item.learnerCount) }}人学习</span>
          </div>
        </div>
        <div class="list-item-actions">
          <el-button type="primary" @click.stop="$router.push(`/knowledge/${item.id}`)">
            {{ item.readProgress > 0 ? '继续学习' : '开始学习' }}
          </el-button>
          <el-button 
            :type="item.isCollected ? 'warning' : 'default'" 
            :icon="item.isCollected ? 'Star' : 'Star'"
            @click.stop="handleToggleCollect(item)"
          />
        </div>
      </el-card>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container" v-if="totalCount > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="totalCount"
        :page-sizes="[12, 24, 36, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
    
    <!-- 快速预览对话框 -->
    <el-dialog
      v-model="showPreview"
      title="快速预览"
      width="800px"
      class="preview-dialog"
    >
      <div class="preview-content" v-if="previewItem">
        <div class="preview-header">
          <h2>{{ previewItem.title }}</h2>
          <el-tag :type="getDifficultyType(previewItem.difficulty)">
            {{ getDifficultyText(previewItem.difficulty) }}
          </el-tag>
        </div>
        <div class="preview-meta">
          <span><el-icon><User /></el-icon> {{ previewItem.authorName }}</span>
          <span><el-icon><Clock /></el-icon> {{ previewItem.readTime }}分钟</span>
          <span><el-icon><View /></el-icon> {{ previewItem.viewCount }}次浏览</span>
        </div>
        <div class="preview-summary">
          <h4>内容简介</h4>
          <p>{{ previewItem.summary }}</p>
        </div>
        <div class="preview-toc">
          <h4>目录</h4>
          <ul>
            <li v-for="(toc, index) in previewItem.toc" :key="index">{{ toc }}</li>
          </ul>
        </div>
      </div>
      <template #footer>
        <el-button @click="showPreview = false">关闭</el-button>
        <el-button type="primary" @click="$router.push(`/knowledge/${previewItem?.id}`); showPreview = false">
          开始学习
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 空状态 -->
    <el-empty 
      v-if="!loading && filteredKnowledgeList.length === 0" 
      description="暂无符合条件的知识内容"
      class="empty-state"
    >
      <el-button type="primary" @click="resetFilters">重置筛选</el-button>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Search, Grid, List, Clock, Star, View, User, 
  Reading, Edit, VideoPlay, ZoomIn, Share, MagicStick 
} from '@element-plus/icons-vue'

const router = useRouter()

// 状态
const loading = ref(true)
const searchQuery = ref('')
const selectedCategory = ref<number | null>(null)
const selectedDifficulty = ref<number | null>(null)
const selectedTags = ref<string[]>([])
const sortBy = ref('latest')
const viewMode = ref<'grid' | 'list'>('grid')
const currentPage = ref(1)
const pageSize = ref(12)
const totalCount = ref(0)

// 进度相关
const selectedDate = ref(new Date())
const calendarView = ref('month')
const learningActivities = ref<Record<string, string[]>>({})

// 预览相关
const showPreview = ref(false)
const previewItem = ref<any>(null)

// 数据
const knowledgeList = ref<any[]>([])
const categories = ref([
  { id: 1, name: '电信诈骗' },
  { id: 2, name: '网络诈骗' },
  { id: 3, name: '情感诈骗' },
  { id: 4, name: '投资诈骗' },
  { id: 5, name: '游戏诈骗' },
  { id: 6, name: '其他诈骗' }
])

const hotTags = ref(['防骗技巧', '案例分析', '最新骗术', '识别方法', '应对策略', '法律知识'])

// 计算属性
const filteredKnowledgeList = computed(() => {
  let result = [...knowledgeList.value]
  
  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(item => 
      item.title.toLowerCase().includes(query) ||
      item.summary.toLowerCase().includes(query)
    )
  }
  
  // 分类过滤
  if (selectedCategory.value) {
    result = result.filter(item => item.categoryId === selectedCategory.value)
  }
  
  // 难度过滤
  if (selectedDifficulty.value) {
    result = result.filter(item => item.difficulty === selectedDifficulty.value)
  }
  
  // 标签过滤
  if (selectedTags.value.length > 0) {
    result = result.filter(item => 
      selectedTags.value.some(tag => item.tags?.includes(tag))
    )
  }
  
  // 排序
  switch (sortBy.value) {
    case 'views':
      result.sort((a, b) => b.viewCount - a.viewCount)
      break
    case 'collects':
      result.sort((a, b) => b.collectCount - a.collectCount)
      break
    case 'learners':
      result.sort((a, b) => b.learnerCount - a.learnerCount)
      break
    default:
      result.sort((a, b) => new Date(b.publishTime).getTime() - new Date(a.publishTime).getTime())
  }
  
  return result
})

// 进度计算
const overallProgress = computed(() => {
  const total = totalKnowledgeCount.value
  if (total === 0) return 0
  const learned = (learnedKnowledgeCount.value / total * 50) + 
                  (completedTestCount.value / Math.max(totalTestCount.value, 1) * 30) +
                  (completedSimulationCount.value / Math.max(totalSimulationCount.value, 1) * 20)
  return Math.round(learned)
})

const knowledgeProgress = computed(() => {
  if (totalKnowledgeCount.value === 0) return 0
  return Math.round(learnedKnowledgeCount.value / totalKnowledgeCount.value * 100)
})

const testProgress = computed(() => {
  if (totalTestCount.value === 0) return 0
  return Math.round(completedTestCount.value / totalTestCount.value * 100)
})

const simulationProgress = computed(() => {
  if (totalSimulationCount.value === 0) return 0
  return Math.round(completedSimulationCount.value / totalSimulationCount.value * 100)
})

const learnedKnowledgeCount = ref(15)
const totalKnowledgeCount = ref(50)
const completedTestCount = ref(8)
const totalTestCount = ref(20)
const completedSimulationCount = ref(5)
const totalSimulationCount = ref(15)

// 方法
const formatNumber = (num: number) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

const getDifficultyType = (difficulty: number) => {
  switch (difficulty) {
    case 1: return 'success'
    case 2: return 'warning'
    case 3: return 'danger'
    default: return 'info'
  }
}

const getDifficultyText = (difficulty: number) => {
  switch (difficulty) {
    case 1: return '简单'
    case 2: return '中等'
    case 3: return '困难'
    default: return '未知'
  }
}

const hasLearningActivity = (date: string, type: 'knowledge' | 'test' | 'simulation') => {
  return learningActivities.value[date]?.includes(type) || false
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const toggleTag = (tag: string) => {
  const index = selectedTags.value.indexOf(tag)
  if (index === -1) {
    selectedTags.value.push(tag)
  } else {
    selectedTags.value.splice(index, 1)
  }
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = null
  selectedDifficulty.value = null
  selectedTags.value = []
  sortBy.value = 'latest'
}

const handleQuickPreview = (item: any) => {
  previewItem.value = item
  showPreview.value = true
}

const handleToggleCollect = (item: any) => {
  item.isCollected = !item.isCollected
  if (item.isCollected) {
    item.collectCount++
    ElMessage.success('已添加收藏')
  } else {
    item.collectCount--
    ElMessage.info('已取消收藏')
  }
}

const handleToggleLike = (item: any) => {
  item.isLiked = !item.isLiked
  if (item.isLiked) {
    item.likeCount++
    ElMessage.success('已点赞')
  } else {
    item.likeCount--
  }
}

const handleShare = (item: any) => {
  ElMessage.info('分享功能开发中')
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 模拟数据
    setTimeout(() => {
      knowledgeList.value = [
        {
          id: 1,
          title: '冒充公检法诈骗深度解析',
          summary: '详细讲解冒充公检法诈骗的常见手法、识别方法和防范技巧',
          coverImage: '/cover1.jpg',
          categoryId: 1,
          categoryName: '电信诈骗',
          difficulty: 2,
          viewCount: 5680,
          collectCount: 342,
          learnerCount: 1256,
          readTime: 15,
          readProgress: 60,
          isCollected: true,
          isLiked: false,
          tags: ['防骗技巧', '案例分析']
        },
        {
          id: 2,
          title: '网络购物诈骗防范指南',
          summary: '揭秘网络购物中的各种诈骗套路，教你如何安全网购',
          coverImage: '/cover2.jpg',
          categoryId: 2,
          categoryName: '网络诈骗',
          difficulty: 1,
          viewCount: 8920,
          collectCount: 521,
          learnerCount: 2340,
          readTime: 12,
          readProgress: 100,
          isCollected: false,
          isLiked: true,
          tags: ['最新骗术', '识别方法']
        },
        {
          id: 3,
          title: '杀猪盘诈骗实战案例',
          summary: '通过真实案例分析杀猪盘诈骗的全过程和心理操控手法',
          coverImage: '/cover3.jpg',
          categoryId: 3,
          categoryName: '情感诈骗',
          difficulty: 3,
          viewCount: 12500,
          collectCount: 892,
          learnerCount: 3560,
          readTime: 20,
          readProgress: 30,
          isCollected: true,
          isLiked: true,
          tags: ['案例分析', '心理分析']
        },
        {
          id: 4,
          title: '投资理财诈骗识别与防范',
          summary: '识别高收益投资陷阱，远离非法集资和庞氏骗局',
          coverImage: '/cover4.jpg',
          categoryId: 4,
          categoryName: '投资诈骗',
          difficulty: 2,
          viewCount: 6780,
          collectCount: 445,
          learnerCount: 1890,
          readTime: 18,
          readProgress: 0,
          isCollected: false,
          isLiked: false,
          tags: ['投资陷阱', '防范策略']
        },
        {
          id: 5,
          title: '游戏交易诈骗全攻略',
          summary: '游戏玩家必看的交易安全指南，避免游戏内欺诈',
          coverImage: '/cover5.jpg',
          categoryId: 5,
          categoryName: '游戏诈骗',
          difficulty: 1,
          viewCount: 4560,
          collectCount: 234,
          learnerCount: 1230,
          readTime: 10,
          readProgress: 0,
          isCollected: false,
          isLiked: false,
          tags: ['游戏安全', '交易技巧']
        },
        {
          id: 6,
          title: '冒充客服诈骗揭秘',
          summary: '电商客服诈骗的常见套路和防骗技巧',
          coverImage: '/cover6.jpg',
          categoryId: 2,
          categoryName: '网络诈骗',
          difficulty: 1,
          viewCount: 7890,
          collectCount: 567,
          learnerCount: 2100,
          readTime: 8,
          readProgress: 80,
          isCollected: true,
          isLiked: true,
          tags: ['客服诈骗', '电商安全']
        },
        {
          id: 7,
          title: '刷单返利诈骗套路分析',
          summary: '深度剖析刷单诈骗的运作模式和诱人陷阱',
          coverImage: '/cover7.jpg',
          categoryId: 2,
          categoryName: '网络诈骗',
          difficulty: 2,
          viewCount: 9200,
          collectCount: 678,
          learnerCount: 2890,
          readTime: 14,
          readProgress: 0,
          isCollected: false,
          isLiked: false,
          tags: ['刷单诈骗', '防骗技巧']
        },
        {
          id: 8,
          title: '仿冒APP诈骗识别指南',
          summary: '教你如何识别仿冒的银行、支付类APP',
          coverImage: '/cover8.jpg',
          categoryId: 2,
          categoryName: '网络诈骗',
          difficulty: 2,
          viewCount: 5670,
          collectCount: 345,
          learnerCount: 1560,
          readTime: 12,
          readProgress: 0,
          isCollected: false,
          isLiked: false,
          tags: ['APP安全', '识别方法']
        }
      ]
      totalCount.value = knowledgeList.value.length
      loading.value = false
    }, 500)
  } catch (e) {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.knowledge-index {
  max-width: 1440px;
  margin: 0 auto;
}

/* 页面标题区 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-6);
}

.header-info h1 {
  margin: 0 0 var(--spacing-2);
  font-size: var(--font-size-3xl);
}

.subtitle {
  color: var(--text-secondary);
  margin: 0;
}

.header-actions {
  display: flex;
  gap: var(--spacing-3);
}

/* 学习进度总览 */
.progress-overview-card {
  margin-bottom: var(--spacing-6);
}

.progress-overview {
  display: flex;
  gap: var(--spacing-8);
  align-items: center;
}

.progress-main {
  flex-shrink: 0;
}

.progress-circle-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.percentage-value {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--primary-color);
}

.percentage-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.progress-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.detail-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
}

.detail-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.detail-icon.knowledge { background: var(--gradient-primary); }
.detail-icon.test { background: var(--gradient-success); }
.detail-icon.simulation { background: var(--gradient-warning); }

.detail-info {
  flex: 1;
}

.detail-label {
  display: block;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-1);
}

.detail-progress {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.detail-progress .el-progress {
  flex: 1;
}

.detail-value {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  white-space: nowrap;
}

/* 学习日历 */
.learning-calendar-card {
  margin-bottom: var(--spacing-6);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.calendar-cell {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.date-number {
  font-size: var(--font-size-sm);
}

.learning-indicators {
  display: flex;
  gap: 2px;
  margin-top: 4px;
}

.indicator {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.indicator.knowledge { background: var(--primary-color); }
.indicator.test { background: var(--success-color); }
.indicator.simulation { background: var(--warning-color); }

/* 筛选栏 */
.filter-card {
  margin-bottom: var(--spacing-6);
}

.filter-bar {
  display: flex;
  gap: var(--spacing-3);
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  max-width: 400px;
}

.filter-select {
  width: 140px;
}

.view-toggle {
  display: flex;
  gap: var(--spacing-1);
}

.tag-filters {
  display: flex;
  gap: var(--spacing-2);
  margin-top: var(--spacing-3);
  flex-wrap: wrap;
}

.filter-tag {
  cursor: pointer;
}

/* 加载状态 */
.loading-container {
  min-height: 400px;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-6);
}

.skeleton-card {
  min-height: 350px;
}

/* 知识网格 */
.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-6);
}

.knowledge-card {
  cursor: pointer;
  transition: all var(--transition-normal);
  overflow: hidden;
}

.knowledge-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.knowledge-card.has-progress {
  border-left: 3px solid var(--primary-color);
}

.card-cover {
  position: relative;
  height: 160px;
  overflow: hidden;
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-4);
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.knowledge-card:hover .card-cover img {
  transform: scale(1.05);
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.knowledge-card:hover .cover-overlay {
  opacity: 1;
}

.quick-preview-btn {
  position: absolute;
  bottom: 8px;
  right: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  cursor: pointer;
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.knowledge-card:hover .quick-preview-btn {
  opacity: 1;
}

.difficulty-tag {
  position: absolute;
  top: 8px;
  left: 8px;
}

.card-content {
  padding: 0 var(--spacing-2);
}

.card-title {
  margin: 0 0 var(--spacing-2);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-summary {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: var(--spacing-3);
}

.reading-progress {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-3);
}

.reading-progress .progress-bar {
  flex: 1;
  height: 6px;
  background: var(--bg-page);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.reading-progress .progress-fill {
  height: 100%;
  background: var(--gradient-primary);
  transition: width 0.3s ease;
}

.reading-progress .progress-text {
  font-size: var(--font-size-xs);
  color: var(--primary-color);
  font-weight: var(--font-weight-medium);
}

.card-tags {
  display: flex;
  gap: var(--spacing-1);
  flex-wrap: wrap;
  margin-bottom: var(--spacing-3);
}

.card-stats {
  display: flex;
  gap: var(--spacing-4);
  color: var(--text-secondary);
  font-size: var(--font-size-xs);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-2);
  padding-top: var(--spacing-3);
  border-top: 1px solid var(--border-color);
  margin-top: var(--spacing-3);
}

/* 知识列表 */
.knowledge-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.knowledge-list-item {
  display: flex;
  gap: var(--spacing-4);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.knowledge-list-item:hover {
  box-shadow: var(--shadow-md);
}

.list-item-cover {
  position: relative;
  width: 200px;
  height: 120px;
  flex-shrink: 0;
  border-radius: var(--radius-md);
  overflow: hidden;
}

.list-item-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.list-item-progress {
  position: absolute;
  bottom: 8px;
  right: 8px;
}

.progress-mini {
  font-size: 10px;
}

.list-item-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
}

.list-item-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.list-item-header h3 {
  margin: 0;
  font-size: var(--font-size-lg);
}

.list-item-summary {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.list-item-tags {
  display: flex;
  gap: var(--spacing-1);
  flex-wrap: wrap;
}

.list-item-stats {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
  padding: 0 var(--spacing-4);
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.list-item-stats .stat {
  display: flex;
  align-items: center;
  gap: 4px;
}

.list-item-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: var(--spacing-2);
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: var(--spacing-8);
}

/* 预览对话框 */
.preview-content {
  padding: var(--spacing-4);
}

.preview-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-4);
}

.preview-header h2 {
  margin: 0;
}

.preview-meta {
  display: flex;
  gap: var(--spacing-4);
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  margin-bottom: var(--spacing-4);
}

.preview-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.preview-summary,
.preview-toc {
  margin-bottom: var(--spacing-4);
}

.preview-summary h4,
.preview-toc h4 {
  margin-bottom: var(--spacing-2);
}

.preview-summary p {
  color: var(--text-regular);
}

.preview-toc ul {
  padding-left: var(--spacing-6);
  color: var(--text-regular);
}

.preview-toc li {
  margin-bottom: var(--spacing-1);
}

/* 空状态 */
.empty-state {
  min-height: 300px;
}

/* 响应式 */
@media (max-width: 992px) {
  .progress-overview {
    flex-direction: column;
  }
  
  .progress-details {
    width: 100%;
  }
  
  .list-item-cover {
    width: 160px;
    height: 100px;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: var(--spacing-4);
  }
  
  .header-actions {
    width: 100%;
  }
  
  .header-actions .el-button {
    flex: 1;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    max-width: none;
  }
  
  .filter-select {
    width: 100%;
  }
  
  .knowledge-grid {
    grid-template-columns: 1fr;
  }
  
  .knowledge-list-item {
    flex-direction: column;
  }
  
  .list-item-cover {
    width: 100%;
    height: 160px;
  }
  
  .list-item-stats {
    flex-direction: row;
    flex-wrap: wrap;
    padding: 0;
  }
  
  .list-item-actions {
    flex-direction: row;
  }
}
</style>
