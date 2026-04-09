<template>
  <div class="knowledge-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <el-icon><Reading /></el-icon>
          反诈知识库
        </h1>
        <p>系统学习各类诈骗手法，提升防范意识</p>
        <div class="header-stats">
          <div class="stat-item">
            <el-icon><Document /></el-icon>
            <span>{{ knowledgeStore.stats.totalKnowledge }} 篇知识</span>
          </div>
          <div class="stat-item">
            <el-icon><View /></el-icon>
            <span>{{ formatNumber(knowledgeStore.stats.totalViews) }} 次浏览</span>
          </div>
          <div class="stat-item">
            <el-icon><Star /></el-icon>
            <span>{{ knowledgeStore.stats.totalCollects }} 次收藏</span>
          </div>
        </div>
      </div>
    </div>

    <el-row :gutter="32">
      <!-- 左侧分类导航 -->
      <el-col :span="5">
        <div class="category-sidebar">
          <el-card class="category-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Grid /></el-icon>
                <span>知识分类</span>
              </div>
            </template>
            
            <div class="category-list">
              <div 
                class="category-item"
                :class="{ active: knowledgeStore.activeCategory === 'all' }"
                @click="handleCategorySelect('all')"
              >
                <el-icon><Document /></el-icon>
                <span>全部知识</span>
                <span class="count">{{ knowledgeStore.stats.totalKnowledge }}</span>
              </div>
              <div 
                v-for="category in knowledgeStore.categories"
                :key="category.id"
                class="category-item"
                :class="{ active: knowledgeStore.activeCategory === String(category.id) }"
                @click="handleCategorySelect(String(category.id))"
              >
                <component :is="getCategoryIcon(category.id)" />
                <span>{{ category.categoryName }}</span>
                <span class="count">{{ category.count || 0 }}</span>
              </div>
            </div>
          </el-card>

          <!-- 热门标签 -->
          <el-card class="tags-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><PriceTag /></el-icon>
                <span>热门标签</span>
              </div>
            </template>
            <div class="tags-list">
              <el-tag
                v-for="tag in knowledgeStore.hotTags"
                :key="tag"
                :type="knowledgeStore.selectedTags.includes(tag) ? 'primary' : 'info'"
                class="tag-item"
                @click="handleToggleTag(tag)"
              >
                {{ tag }}
              </el-tag>
            </div>
          </el-card>

          <!-- 学习进度 -->
          <el-card class="progress-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><TrendCharts /></el-icon>
                <span>学习进度</span>
              </div>
            </template>
            <div class="progress-content">
              <div class="progress-item">
                <span class="progress-label">已学习</span>
                <span class="progress-value">{{ knowledgeStore.stats.learnedCount }} 篇</span>
              </div>
              <div class="progress-item">
                <span class="progress-label">学习进度</span>
                <el-progress 
                  :percentage="knowledgeStore.stats.learnProgress" 
                  :stroke-width="8"
                  :format="() => `${knowledgeStore.stats.learnProgress}%`"
                />
              </div>
              <div class="progress-item">
                <span class="progress-label">连续学习</span>
                <span class="progress-value">{{ knowledgeStore.stats.consecutiveDays }} 天</span>
              </div>
            </div>
          </el-card>
        </div>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="19">
        <!-- 搜索和筛选栏 -->
        <el-card class="filter-card" shadow="hover">
          <div class="filter-bar">
            <el-input
              v-model="knowledgeStore.searchKeyword"
              placeholder="搜索知识标题、内容..."
              prefix-icon="Search"
              clearable
              size="large"
              class="search-input"
              @keyup.enter="handleSearch"
              @clear="handleSearch"
            >
              <template #append>
                <el-button type="primary" @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>

            <div class="filter-actions">
              <el-select v-model="knowledgeStore.contentType" placeholder="内容类型" clearable size="large" class="filter-select">
                <el-option label="全部" :value="0" />
                <el-option label="文章" :value="1" />
                <el-option label="视频" :value="2" />
                <el-option label="案例" :value="3" />
              </el-select>

              <el-select v-model="knowledgeStore.sortBy" placeholder="排序方式" size="large" class="filter-select">
                <el-option label="最新发布" :value="'latest'" />
                <el-option label="最多浏览" :value="'views'" />
                <el-option label="最多收藏" :value="'collects'" />
                <el-option label="推荐度" :value="'recommend'" />
              </el-select>

              <el-button-group class="view-toggle">
                <el-button :type="viewMode === 'grid' ? 'primary' : ''" @click="viewMode = 'grid'">
                  <el-icon><Grid /></el-icon>
                </el-button>
                <el-button :type="viewMode === 'list' ? 'primary' : ''" @click="viewMode = 'list'">
                  <el-icon><List /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </div>

          <!-- 已选筛选条件 -->
          <div class="selected-filters" v-if="knowledgeStore.hasActiveFilters">
            <span class="filter-label">已选条件：</span>
            <el-tag v-if="knowledgeStore.activeCategory !== 'all'" closable @close="handleCategorySelect('all')" type="info">
              {{ currentCategoryName }}
            </el-tag>
            <el-tag v-if="knowledgeStore.contentType" closable @close="knowledgeStore.contentType = 0" type="info">
              {{ getTypeName(knowledgeStore.contentType) }}
            </el-tag>
            <el-tag v-for="tag in knowledgeStore.selectedTags" :key="tag" closable @close="handleToggleTag(tag)" type="info">
              {{ tag }}
            </el-tag>
            <el-button type="primary" link size="small" @click="handleClearFilters">清空筛选</el-button>
          </div>
        </el-card>

        <!-- 编辑推荐 -->
        <div v-if="!knowledgeStore.searchKeyword && knowledgeStore.activeCategory === 'all' && knowledgeStore.recommendations.length > 0" class="section">
          <div class="section-header">
            <h3>
              <el-icon class="star-icon"><Star /></el-icon>
              编辑推荐
            </h3>
            <el-button link type="primary" @click="recommendVisible = !recommendVisible">
              {{ recommendVisible ? '收起' : '展开' }}
              <el-icon>
                <component :is="recommendVisible ? 'ArrowUp' : 'ArrowDown'" />
              </el-icon>
            </el-button>
          </div>
          
          <transition name="slide-fade">
            <div v-show="recommendVisible" class="recommend-grid">
              <div 
                v-for="(item, index) in knowledgeStore.recommendations" 
                :key="item.id"
                class="recommend-item"
                :class="{ 'featured': index === 0 }"
                @click="$router.push(`/knowledge/${item.id}`)"
              >
                <div class="recommend-cover">
                  <img :src="item.coverImage || defaultCover" :alt="item.title" />
                  <div class="cover-overlay">
                    <el-icon :size="48"><View /></el-icon>
                  </div>
                  <div class="recommend-badge">
                    <el-icon><Star /></el-icon>
                    推荐
                  </div>
                </div>
                <div class="recommend-content">
                  <h4>{{ item.title }}</h4>
                  <p>{{ item.reason }}</p>
                  <div class="recommend-meta">
                    <span><el-icon><View /></el-icon> {{ formatNumber(item.viewCount) }}</span>
                    <span><el-icon><Star /></el-icon> {{ item.score }}分</span>
                    <span class="category-tag">{{ item.category }}</span>
                  </div>
                </div>
              </div>
            </div>
          </transition>
        </div>

        <!-- 个性化推荐 -->
        <div v-if="!knowledgeStore.searchKeyword && knowledgeStore.activeCategory === 'all' && knowledgeStore.personalizedRecommendations.length > 0" class="section">
          <div class="section-header">
            <h3>
              <el-icon class="star-icon"><MagicStick /></el-icon>
              为你推荐
            </h3>
            <el-button link type="primary" @click="personalizedVisible = !personalizedVisible">
              {{ personalizedVisible ? '收起' : '展开' }}
              <el-icon>
                <component :is="personalizedVisible ? 'ArrowUp' : 'ArrowDown'" />
              </el-icon>
            </el-button>
          </div>
          
          <transition name="slide-fade">
            <div v-show="personalizedVisible" class="personalized-grid">
              <div 
                v-for="(item, index) in knowledgeStore.personalizedRecommendations" 
                :key="item.id"
                class="personalized-item"
                @click="$router.push(`/knowledge/${item.id}`)"
              >
                <div class="personalized-cover">
                  <img :src="item.coverImage || defaultCover" :alt="item.title" />
                  <div class="cover-overlay">
                    <el-icon :size="32"><View /></el-icon>
                  </div>
                  <div class="personalized-badge">
                    <el-icon><MagicStick /></el-icon>
                    个性化
                  </div>
                </div>
                <div class="personalized-content">
                  <h4>{{ item.title }}</h4>
                  <p class="reason">{{ item.reason }}</p>
                  <div class="personalized-meta">
                    <span><el-icon><View /></el-icon> {{ formatNumber(item.viewCount) }}</span>
                    <span><el-icon><Star /></el-icon> {{ item.score }}分</span>
                    <span class="category-tag">{{ item.category }}</span>
                  </div>
                </div>
              </div>
            </div>
          </transition>
        </div>

        <!-- 知识列表 -->
        <div class="section">
          <div class="section-header">
            <h3>
              <el-icon><Reading /></el-icon>
              {{ knowledgeStore.activeCategory === 'all' ? '全部知识' : currentCategoryName }}
              <span class="count-tag">{{ knowledgeStore.total }} 篇</span>
            </h3>
          </div>

          <!-- 网格视图 -->
          <div v-if="viewMode === 'grid'" class="knowledge-grid">
            <el-card
              v-for="item in knowledgeStore.knowledgeList"
              :key="item.id"
              class="knowledge-card"
              shadow="hover"
              @click="$router.push(`/knowledge/${item.id}`)"
            >
              <div class="card-cover">
                <img :src="item.coverImage || defaultCover" :alt="item.title" />
                <div class="cover-overlay">
                  <el-icon :size="32"><View /></el-icon>
                </div>
                <div class="card-type" :class="`type-${item.contentType}`">
                  {{ getTypeName(item.contentType) }}
                </div>
                <div v-if="item.isHot" class="hot-badge">
                  <el-icon><Sunny /></el-icon>
                  热门
                </div>
              </div>
              <div class="card-body">
                <h4>{{ item.title }}</h4>
                <p class="summary">{{ item.summary || '暂无简介' }}</p>
                <div class="card-tags" v-if="item.tags && item.tags.length > 0">
                  <el-tag 
                    v-for="tag in item.tags.slice(0, 3)" 
                    :key="tag" 
                    size="small" 
                    effect="plain"
                    type="info"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
                <div class="card-footer">
                  <div class="stats">
                    <span><el-icon><View /></el-icon> {{ formatNumber(item.viewCount) }}</span>
                    <span><el-icon><Star /></el-icon> {{ item.collectCount }}</span>
                    <span><el-icon><Timer /></el-icon> {{ item.readTime || 5 }}分钟</span>
                  </div>
                  <el-tag size="small" type="info">{{ item.categoryName }}</el-tag>
                </div>
              </div>
            </el-card>
          </div>

          <!-- 列表视图 -->
          <div v-else class="knowledge-list">
            <el-card
              v-for="item in knowledgeStore.knowledgeList"
              :key="item.id"
              class="knowledge-item"
              shadow="hover"
              @click="$router.push(`/knowledge/${item.id}`)"
            >
              <div class="item-cover">
                <img :src="item.coverImage || defaultCover" :alt="item.title" />
                <div v-if="item.isHot" class="hot-badge-list">
                  <el-icon><Sunny /></el-icon>
                  热门
                </div>
              </div>
              <div class="item-content">
                <div class="item-header">
                  <h4>{{ item.title }}</h4>
                  <el-tag size="small" :type="getTypeTagType(item.contentType)">
                    {{ getTypeName(item.contentType) }}
                  </el-tag>
                </div>
                <p class="summary">{{ item.summary || '暂无简介' }}</p>
                <div class="item-tags" v-if="item.tags && item.tags.length > 0">
                  <el-tag 
                    v-for="tag in item.tags.slice(0, 5)" 
                    :key="tag" 
                    size="small" 
                    effect="plain"
                    type="info"
                    class="item-tag"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
                <div class="item-footer">
                  <div class="meta">
                    <span><el-icon><View /></el-icon> {{ formatNumber(item.viewCount) }}</span>
                    <span><el-icon><Star /></el-icon> {{ item.collectCount }}</span>
                    <span><el-icon><Timer /></el-icon> {{ item.readTime || 5 }}分钟</span>
                    <span><el-icon><Calendar /></el-icon> {{ formatDate(item.createTime) }}</span>
                  </div>
                  <el-tag size="small" type="info">{{ item.categoryName }}</el-tag>
                </div>
              </div>
            </el-card>
          </div>

          <!-- 空状态 -->
          <el-empty 
            v-if="knowledgeStore.knowledgeList.length === 0 && !knowledgeStore.loading" 
            description="暂无相关知识"
            :image-size="200"
          >
            <el-button type="primary" @click="handleClearFilters">清除筛选</el-button>
            <el-button @click="handleRandomRecommend">随机推荐</el-button>
          </el-empty>

          <!-- 加载中 -->
          <div v-if="knowledgeStore.loading" class="loading-wrapper">
            <el-skeleton :rows="12" animated />
          </div>

          <!-- 分页 -->
          <div class="pagination-wrapper" v-if="knowledgeStore.total > 0">
            <el-pagination
              v-model:current-page="page"
              v-model:page-size="size"
              :page-sizes="[12, 24, 36, 48]"
              :total="knowledgeStore.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useKnowledgeStore } from '@/stores/knowledge'
import { 
  Reading, Grid, Document, Star, View, Timer, Calendar,
  Search, PriceTag, List, ArrowUp, ArrowDown, VideoCamera, Sunny,
  Phone, Monitor, Wallet, Briefcase, TrendCharts, MagicStick
} from '@element-plus/icons-vue'

const knowledgeStore = useKnowledgeStore()
const defaultCover = 'https://images.unsplash.com/photo-1557804506-669a67965ba0?w=400&h=300&fit=crop'

// 视图模式
const viewMode = ref<'grid' | 'list'>('grid')
const recommendVisible = ref(true)
const personalizedVisible = ref(true)

// 分页数据
const page = ref(1)
const size = ref(12)

// 计算当前分类名称
const currentCategoryName = computed(() => {
  if (knowledgeStore.activeCategory === 'all') {
    return '全部知识'
  }
  const category = knowledgeStore.categories.find(c => String(c.id) === knowledgeStore.activeCategory)
  return category?.categoryName || '分类知识'
})

// 获取分类图标
const getCategoryIcon = (id: number) => {
  const icons: Record<number, any> = {
    1: Phone,
    2: Monitor,
    3: Wallet,
    4: Briefcase,
    5: Briefcase,
    6: Wallet
  }
  return icons[id] || Document
}

// 获取内容类型名称
const getTypeName = (type: number) => {
  const types: Record<number, string> = {
    1: '文章',
    2: '视频',
    3: '案例'
  }
  return types[type] || '未知'
}

// 获取类型标签颜色
const getTypeTagType = (type: number) => {
  const types: Record<number, string> = {
    1: '',
    2: 'warning',
    3: 'success'
  }
  return types[type] || 'info'
}

// 格式化数字
const formatNumber = (num: number) => {
  if (!num) return 0
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num
}

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 搜索
const handleSearch = () => {
  page.value = 1
  loadKnowledgeList()
}

// 分类选择
const handleCategorySelect = (categoryId: string) => {
  knowledgeStore.activeCategory = categoryId
  page.value = 1
  loadKnowledgeList()
}

// 切换标签
const handleToggleTag = (tag: string) => {
  knowledgeStore.toggleTag(tag)
  page.value = 1
  loadKnowledgeList()
}

// 清空筛选
const handleClearFilters = () => {
  knowledgeStore.clearFilters()
  page.value = 1
  loadKnowledgeList()
}

// 随机推荐
const handleRandomRecommend = () => {
  knowledgeStore.clearFilters()
  knowledgeStore.sortBy = 'recommend'
  page.value = 1
  loadKnowledgeList()
}

// 加载知识列表
const loadKnowledgeList = async () => {
  await knowledgeStore.loadKnowledgeList(page.value, size.value)
}

// 分页
const handlePageChange = () => {
  loadKnowledgeList()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSizeChange = () => {
  page.value = 1
  loadKnowledgeList()
}

// 监听筛选变化
watch([() => knowledgeStore.contentType, () => knowledgeStore.sortBy], () => {
  page.value = 1
  loadKnowledgeList()
})

onMounted(async () => {
  await knowledgeStore.initialize()
  await loadKnowledgeList()
})
</script>

<style scoped>
.knowledge-page {
  padding: 0 0 40px;
  min-height: calc(100vh - 160px);
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #42b883 0%, #35495e 100%);
  padding: 60px 24px;
  margin-bottom: 40px;
  border-radius: 16px;
  color: white;
  position: relative;
  overflow: hidden;
  text-align: center;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.header-content {
  position: relative;
  z-index: 1;
}

.header-content h1 {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 40px;
  font-weight: 700;
  margin-bottom: 16px;
  justify-content: center;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.header-content p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 24px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.header-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-top: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-4px);
}

.stat-item span {
  font-size: 16px;
  font-weight: 500;
}

/* 侧边栏 */
.category-sidebar {
  position: sticky;
  top: 88px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.category-card,
.tags-card,
.progress-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.category-card:hover,
.tags-card:hover,
.progress-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1a1a2e;
  padding: 16px 20px;
  background: var(--el-color-primary-light-9);
  margin: -20px -20px 20px;
  border-radius: 12px 12px 0 0;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 0;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #64748b;
  border: 1px solid transparent;
}

.category-item:hover {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  transform: translateX(4px);
  border-color: var(--el-color-primary-light-7);
}

.category-item.active {
  background: var(--el-color-primary);
  color: white;
  font-weight: 600;
  border-color: var(--el-color-primary);
  box-shadow: 0 2px 8px rgba(66, 184, 131, 0.3);
}

.category-item span:first-of-type {
  flex: 1;
}

.category-item .count {
  font-size: 12px;
  color: #94a3b8;
  background: rgba(0, 0, 0, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
}

.category-item.active .count {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 0;
}

.tag-item {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 16px;
  padding: 4px 12px;
}

.tag-item:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(66, 184, 131, 0.3);
}

.progress-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 0;
}

.progress-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.progress-value {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-color-primary);
}

/* 筛选栏 */
.filter-card {
  border-radius: 12px;
  margin-bottom: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.filter-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.filter-bar {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
}

.search-input {
  flex: 1;
  max-width: 500px;
  border-radius: 8px;
  overflow: hidden;
}

.search-input .el-input__wrapper {
  border-radius: 8px;
}

.filter-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filter-select {
  min-width: 160px;
  border-radius: 8px;
}

.view-toggle {
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  overflow: hidden;
  background: white;
}

.view-toggle .el-button {
  border-radius: 0;
  border: none;
  border-right: 1px solid var(--el-border-color);
  transition: all 0.3s ease;
}

.view-toggle .el-button:hover {
  background: var(--el-color-primary-light-9);
}

.view-toggle .el-button:last-child {
  border-right: none;
}

.selected-filters {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--el-border-color);
}

.filter-label {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

/* 区块标题 */
.section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--el-color-primary-light-9);
}

.section-header h3 {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

.star-icon {
  color: #f59e0b;
  font-size: 20px;
}

.count-tag {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  margin-left: 8px;
  box-shadow: 0 1px 4px rgba(66, 184, 131, 0.2);
}

/* 推荐网格 */
.recommend-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.recommend-item {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.recommend-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: var(--el-color-primary-light-5);
}

.recommend-item.featured {
  grid-column: 1 / -1;
  display: flex;
  border-color: var(--el-color-primary);
  box-shadow: 0 4px 16px rgba(66, 184, 131, 0.2);
}

.recommend-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.recommend-item.featured .recommend-cover {
  width: 400px;
  height: auto;
  flex-shrink: 0;
}

.recommend-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.recommend-item:hover .recommend-cover img {
  transform: scale(1.05);
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: white;
  backdrop-filter: blur(2px);
}

.recommend-item:hover .cover-overlay {
  opacity: 1;
}

.recommend-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
  color: white;
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
  z-index: 1;
}

.recommend-content {
  padding: 20px;
}

.recommend-item.featured .recommend-content {
  flex: 1;
}

.recommend-content h4 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
  line-height: 1.4;
  color: var(--el-text-color-primary);
}

.recommend-content p {
  color: var(--el-text-color-secondary);
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 14px;
}

.recommend-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  align-items: center;
}

.recommend-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;
}

.recommend-item:hover .recommend-meta span {
  color: var(--el-color-primary);
}

.category-tag {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

/* 个性化推荐 */
.personalized-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.personalized-item {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.personalized-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: var(--el-color-primary-light-5);
}

.personalized-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.personalized-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.personalized-item:hover .personalized-cover img {
  transform: scale(1.05);
}

.personalized-cover .cover-overlay {
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: white;
  backdrop-filter: blur(2px);
}

.personalized-item:hover .personalized-cover .cover-overlay {
  opacity: 1;
}

.personalized-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #8b5cf6, #6366f1);
  color: white;
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.3);
  z-index: 1;
}

.personalized-content {
  padding: 16px;
}

.personalized-content h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  line-height: 1.4;
  color: var(--el-text-color-primary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.personalized-content .reason {
  color: var(--el-text-color-secondary);
  line-height: 1.5;
  margin-bottom: 12px;
  font-size: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.personalized-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  align-items: center;
}

.personalized-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;
}

.personalized-item:hover .personalized-meta span {
  color: var(--el-color-primary);
}

/* 知识网格 */
.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.knowledge-card {
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
  border: 1px solid var(--el-border-color);
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.knowledge-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: var(--el-color-primary-light-5);
}

.card-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
  flex-shrink: 0;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.knowledge-card:hover .card-cover img {
  transform: scale(1.05);
}

.card-cover .cover-overlay {
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: white;
  backdrop-filter: blur(2px);
}

.knowledge-card:hover .cover-overlay {
  opacity: 1;
}

.card-type {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(255, 255, 255, 0.9);
  color: var(--el-text-color-primary);
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1;
}

.card-type.type-2 {
  background: var(--el-color-warning-light-9);
  color: var(--el-color-warning);
}

.card-type.type-3 {
  background: var(--el-color-success-light-9);
  color: var(--el-color-success);
}

.hot-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #ff9800, #f57c00);
  color: white;
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 8px rgba(255, 152, 0, 0.3);
  z-index: 1;
}

.card-body {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-body h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: var(--el-text-color-primary);
  transition: color 0.3s ease;
}

.knowledge-card:hover .card-body h4 {
  color: var(--el-color-primary);
}

.card-body .summary {
  color: var(--el-text-color-secondary);
  line-height: 1.5;
  margin-bottom: 16px;
  font-size: 14px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color);
}

.stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  align-items: center;
  transition: color 0.3s ease;
}

.knowledge-card:hover .stats {
  color: var(--el-color-primary);
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 知识列表 */
.knowledge-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.knowledge-item {
  display: flex;
  gap: 20px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color);
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.knowledge-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border-color: var(--el-color-primary-light-5);
}

.item-cover {
  width: 200px;
  height: 120px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 8px;
  position: relative;
}

.item-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.knowledge-item:hover .item-cover img {
  transform: scale(1.05);
}

.hot-badge-list {
  position: absolute;
  top: 8px;
  right: 8px;
  background: linear-gradient(135deg, #ff9800, #f57c00);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 4px rgba(255, 152, 0, 0.3);
  z-index: 1;
}

.item-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.item-header h4 {
  font-size: 18px;
  font-weight: 600;
  flex: 1;
  margin: 0 16px 0 0;
  line-height: 1.4;
  color: var(--el-text-color-primary);
  transition: color 0.3s ease;
}

.knowledge-item:hover .item-header h4 {
  color: var(--el-color-primary);
}

.item-content .summary {
  color: var(--el-text-color-secondary);
  line-height: 1.5;
  margin-bottom: 12px;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.item-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color);
}

.item-footer .meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  align-items: center;
  transition: color 0.3s ease;
}

.knowledge-item:hover .item-footer .meta {
  color: var(--el-color-primary);
}

.item-footer .meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 加载中 */
.loading-wrapper {
  padding: 40px 0;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 2px solid var(--el-color-primary-light-9);
}

/* 动画 */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}

/* 响应式 */
@media (max-width: 1200px) {
  .recommend-grid,
  .personalized-grid {
    grid-template-columns: 1fr 1fr;
  }
  
  .recommend-item.featured {
    grid-column: 1 / -1;
  }
  
  .header-stats {
    gap: 32px;
  }
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    max-width: none;
  }
  
  .filter-actions {
    justify-content: space-between;
  }
  
  .recommend-grid,
  .personalized-grid {
    grid-template-columns: 1fr;
  }
  
  .recommend-item.featured {
    flex-direction: column;
  }
  
  .recommend-item.featured .recommend-cover {
    width: 100%;
    height: 200px;
  }
  
  .knowledge-item {
    flex-direction: column;
  }
  
  .item-cover {
    width: 100%;
    height: 180px;
  }
  
  .header-stats {
    flex-direction: column;
    gap: 16px;
  }
  
  .stat-item {
    width: 200px;
  }
}
</style>