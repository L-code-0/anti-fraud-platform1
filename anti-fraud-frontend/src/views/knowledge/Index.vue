<template>
  <div class="knowledge-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
      </div>
      <div class="header-content">
        <h1>知识学习中心</h1>
        <p>深入了解各类诈骗手法，提升防骗能力</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 搜索和筛选 -->
      <div class="search-section">
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索知识文章..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>

        <div class="filter-tabs">
          <button
            v-for="tab in categoryTabs"
            :key="tab.id"
            class="filter-tab"
            :class="{ active: activeCategory === tab.id }"
            @click="activeCategory = tab.id"
          >
            <el-icon><component :is="tab.icon" /></el-icon>
            {{ tab.name }}
          </button>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card" v-for="stat in statsData" :key="stat.label">
          <div class="stat-icon" :style="{ background: stat.gradient }">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
        </div>
      </div>

      <!-- 知识列表 -->
      <div class="knowledge-content">
        <div class="content-header">
          <h2>热门文章</h2>
          <div class="view-toggle">
            <button :class="{ active: viewMode === 'grid' }" @click="viewMode = 'grid'">
              <el-icon><Grid /></el-icon>
            </button>
            <button :class="{ active: viewMode === 'list' }" @click="viewMode = 'list'">
              <el-icon><List /></el-icon>
            </button>
          </div>
        </div>

        <div class="knowledge-grid" :class="{ 'list-view': viewMode === 'list' }">
          <div
            class="knowledge-card"
            v-for="(item, index) in filteredKnowledge"
            :key="item.id"
            :style="{ animationDelay: index * 0.05 + 's' }"
            @click="goToDetail(item.id)"
          >
            <div class="card-image">
              <img :src="item.cover" :alt="item.title" />
              <div class="card-badge" :class="'badge-' + item.categoryType">
                {{ item.category }}
              </div>
              <div class="card-overlay">
                <el-button type="primary" size="large">
                  <el-icon><View /></el-icon>
                  阅读全文
                </el-button>
              </div>
            </div>
            <div class="card-content">
              <h3>{{ item.title }}</h3>
              <p class="card-desc">{{ item.desc }}</p>
              <div class="card-meta">
                <div class="meta-left">
                  <span class="meta-item">
                    <el-icon><View /></el-icon>
                    {{ item.views }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Star /></el-icon>
                    {{ item.likes }}
                  </span>
                  <span class="meta-item">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ item.comments }}
                  </span>
                </div>
                <span class="meta-time">{{ item.time }}</span>
              </div>
              <div class="card-tags">
                <span class="tag" v-for="tag in item.tags" :key="tag">{{ tag }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            :background="true"
            layout="prev, pager, next, jumper"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  Search, Grid, List, View, Star, ChatDotRound,
  Phone, Monitor, Message, Money, Gift, Document, VideoCamera
} from '@element-plus/icons-vue'

const router = useRouter()
const searchKeyword = ref('')
const activeCategory = ref('all')
const viewMode = ref<'grid' | 'list'>('grid')
const currentPage = ref(1)
const pageSize = 12
const total = ref(50)

const categoryTabs = [
  { id: 'all', name: '全部', icon: 'Document' },
  { id: 'phone', name: '电话诈骗', icon: 'Phone' },
  { id: 'online', name: '网络诈骗', icon: 'Monitor' },
  { id: 'social', name: '社交诈骗', icon: 'Message' },
  { id: 'finance', name: '金融诈骗', icon: 'Money' },
  { id: 'gift', name: '中奖诈骗', icon: 'Gift' }
]

const statsData = [
  { icon: 'Document', value: '500+', label: '知识文章', gradient: 'var(--gradient-primary)' },
  { icon: 'User', value: '10,000+', label: '学习人数', gradient: 'var(--gradient-success)' },
  { icon: 'Reading', value: '50,000+', label: '学习次数', gradient: 'var(--gradient-warning)' },
  { icon: 'Star', value: '98%', label: '用户好评', gradient: 'var(--gradient-info)' }
]

const knowledgeList = ref([
  {
    id: 1,
    title: '揭秘冒充公检法诈骗完整套路',
    desc: '详细解析冒充公检法诈骗的常见套路和识别方法，教你如何识别真警察和假警察',
    category: '电话诈骗',
    categoryType: 'phone',
    tags: ['防骗指南', '识别技巧'],
    views: '2.3k',
    likes: 328,
    comments: 56,
    time: '2024-01-15',
    cover: 'https://picsum.photos/seed/k1/400/250'
  },
  {
    id: 2,
    title: '刷单返利诈骗防范指南',
    desc: '网络刷单本身就是违法行为，警惕各种返利诱惑，避免落入诈骗陷阱',
    category: '网络诈骗',
    categoryType: 'online',
    tags: ['刷单诈骗', '返利陷阱'],
    views: '1.8k',
    likes: 256,
    comments: 42,
    time: '2024-01-14',
    cover: 'https://picsum.photos/seed/k2/400/250'
  },
  {
    id: 3,
    title: '杀猪盘诈骗完整解析',
    desc: '深度剖析杀猪盘诈骗的全过程，从相识到被骗的完整链条，提高防范意识',
    category: '社交诈骗',
    categoryType: 'social',
    tags: ['杀猪盘', '情感诈骗'],
    views: '3.1k',
    likes: 456,
    comments: 89,
    time: '2024-01-13',
    cover: 'https://picsum.photos/seed/k3/400/250'
  },
  {
    id: 4,
    title: '虚假贷款诈骗识别技巧',
    desc: '远离非法网贷，警惕各种贷款诈骗陷阱，保护个人财产安全',
    category: '金融诈骗',
    categoryType: 'finance',
    tags: ['贷款诈骗', '金融安全'],
    views: '1.5k',
    likes: 198,
    comments: 34,
    time: '2024-01-12',
    cover: 'https://picsum.photos/seed/k4/400/250'
  },
  {
    id: 5,
    title: '中奖诈骗防范手册',
    desc: '警惕各类中奖信息，认清天上不会掉馅饼的本质，避免财产损失',
    category: '中奖诈骗',
    categoryType: 'gift',
    tags: ['中奖诈骗', '防范手册'],
    views: '1.2k',
    likes: 167,
    comments: 28,
    time: '2024-01-11',
    cover: 'https://picsum.photos/seed/k5/400/250'
  },
  {
    id: 6,
    title: '网络购物诈骗全攻略',
    desc: '全面解析网络购物中的各类诈骗手法，包括虚假客服、退款诈骗等',
    category: '网络诈骗',
    categoryType: 'online',
    tags: ['网购诈骗', '客服诈骗'],
    views: '2.0k',
    likes: 289,
    comments: 51,
    time: '2024-01-10',
    cover: 'https://picsum.photos/seed/k6/400/250'
  },
  {
    id: 7,
    title: 'AI换脸诈骗新手法',
    desc: '深度解析AI换脸技术在诈骗中的应用，提高警惕防止被骗',
    category: '网络诈骗',
    categoryType: 'online',
    tags: ['AI诈骗', '新技术'],
    views: '2.8k',
    likes: 412,
    comments: 78,
    time: '2024-01-09',
    cover: 'https://picsum.photos/seed/k7/400/250'
  },
  {
    id: 8,
    title: '冒充领导诈骗识别',
    desc: '企业财务人员必看，如何识别冒充领导的诈骗电话和信息',
    category: '电话诈骗',
    categoryType: 'phone',
    tags: ['领导诈骗', '财务安全'],
    views: '1.6k',
    likes: 234,
    comments: 45,
    time: '2024-01-08',
    cover: 'https://picsum.photos/seed/k8/400/250'
  }
])

const filteredKnowledge = computed(() => {
  let result = knowledgeList.value

  if (activeCategory.value !== 'all') {
    result = result.filter(item => item.categoryType === activeCategory.value)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(item =>
      item.title.toLowerCase().includes(keyword) ||
      item.desc.toLowerCase().includes(keyword)
    )
  }

  return result
})

const handleSearch = () => {
  currentPage.value = 1
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToDetail = (id: number) => {
  router.push(`/knowledge/detail/${id}`)
}
</script>

<style scoped>
.knowledge-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

/* 页面头部 */
.page-header {
  position: relative;
  padding: var(--spacing-16) var(--spacing-6);
  overflow: hidden;
}

.header-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.header-bg .bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1e3a5f 0%, #0f172a 50%, #1e293b 100%);
}

.header-content {
  position: relative;
  z-index: 1;
  max-width: 1280px;
  margin: 0 auto;
  text-align: center;
  color: white;
}

.header-content h1 {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-4);
}

.header-content p {
  font-size: var(--font-size-lg);
  opacity: 0.8;
}

/* 页面容器 */
.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6) var(--spacing-12);
}

/* 搜索区域 */
.search-section {
  margin-top: calc(-1 * var(--spacing-10));
  position: relative;
  z-index: 10;
  margin-bottom: var(--spacing-8);
}

.search-bar {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-6);
  box-shadow: var(--shadow-xl);
  margin-bottom: var(--spacing-4);
}

.search-bar :deep(.el-input__wrapper) {
  border-radius: var(--radius-md);
  padding: var(--spacing-3) var(--spacing-4);
}

.filter-tabs {
  display: flex;
  gap: var(--spacing-3);
  flex-wrap: wrap;
}

.filter-tab {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-4);
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.filter-tab:hover {
  background: var(--bg-hover);
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.filter-tab.active {
  background: var(--gradient-primary);
  border-color: var(--primary-color);
  color: white;
  box-shadow: var(--shadow-primary);
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-8);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-5);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

/* 知识内容 */
.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
}

.content-header h2 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.view-toggle {
  display: flex;
  gap: var(--spacing-2);
  background: var(--bg-primary);
  padding: var(--spacing-1);
  border-radius: var(--radius-md);
}

.view-toggle button {
  padding: var(--spacing-2);
  background: transparent;
  border: none;
  border-radius: var(--radius-sm);
  color: var(--text-muted);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.view-toggle button:hover {
  color: var(--primary-color);
}

.view-toggle button.active {
  background: var(--primary-color);
  color: white;
}

/* 知识网格 */
.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-6);
}

.knowledge-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  cursor: pointer;
  transition: all var(--transition-normal);
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.knowledge-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
}

.knowledge-card:hover .card-overlay {
  opacity: 1;
}

.knowledge-card:hover .card-image img {
  transform: scale(1.1);
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.card-badge {
  position: absolute;
  top: var(--spacing-3);
  left: var(--spacing-3);
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: white;
}

.badge-phone { background: var(--danger-color); }
.badge-online { background: var(--warning-color); }
.badge-social { background: var(--success-color); }
.badge-finance { background: var(--info-color); }
.badge-gift { background: var(--purple-color); }

.card-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.card-content {
  padding: var(--spacing-4);
}

.card-content h3 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-desc {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.5;
  margin-bottom: var(--spacing-3);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-3);
}

.meta-left {
  display: flex;
  gap: var(--spacing-4);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.meta-time {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.card-tags {
  display: flex;
  gap: var(--spacing-2);
  flex-wrap: wrap;
}

.card-tags .tag {
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--bg-secondary);
  color: var(--text-tertiary);
  font-size: var(--font-size-xs);
  border-radius: var(--radius-sm);
}

/* 列表视图 */
.knowledge-grid.list-view {
  grid-template-columns: 1fr;
}

.knowledge-grid.list-view .knowledge-card {
  display: flex;
  flex-direction: row;
}

.knowledge-grid.list-view .card-image {
  width: 300px;
  height: auto;
  flex-shrink: 0;
}

.knowledge-grid.list-view .card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: var(--spacing-10);
}

.pagination-wrapper :deep(.el-pagination) {
  background: var(--bg-primary);
  padding: var(--spacing-4);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
}

.pagination-wrapper :deep(.el-pagination.is-background .el-pager li) {
  border-radius: var(--radius-md);
}

.pagination-wrapper :deep(.el-pagination.is-background .el-pager li.is-active) {
  background: var(--gradient-primary);
}

/* 响应式 */
@media (max-width: 1024px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .knowledge-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: var(--spacing-10) var(--spacing-4);
  }

  .header-content h1 {
    font-size: var(--font-size-2xl);
  }

  .page-container {
    padding: 0 var(--spacing-4) var(--spacing-8);
  }

  .search-section {
    margin-top: calc(-1 * var(--spacing-6));
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .knowledge-grid {
    grid-template-columns: 1fr;
  }

  .knowledge-grid.list-view .knowledge-card {
    flex-direction: column;
  }

  .knowledge-grid.list-view .card-image {
    width: 100%;
    height: 200px;
  }

  .content-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-3);
  }
}
</style>
