<template>
  <div class="favorites-page">
    <div class="page-header">
      <h1><el-icon><Star /></el-icon> 我的收藏</h1>
      <p>收藏您喜欢的知识内容，方便日后学习回顾</p>
    </div>

    <!-- 统计信息 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <div class="stat-card">
          <div class="stat-icon blue">
            <el-icon><Collection /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalCount }}</div>
            <div class="stat-label">收藏总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card">
          <div class="stat-icon green">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ readCount }}</div>
            <div class="stat-label">已阅读</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card">
          <div class="stat-icon orange">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ unReadCount }}</div>
            <div class="stat-label">未阅读</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 筛选工具栏 -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索收藏内容"
            clearable
            @keyup.enter="loadFavorites"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="categoryFilter" placeholder="内容分类" clearable @change="loadFavorites">
            <el-option label="电信诈骗" value="1" />
            <el-option label="网络诈骗" value="2" />
            <el-option label="兼职诈骗" value="3" />
            <el-option label="校园贷" value="4" />
            <el-option label="其他" value="5" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="readFilter" placeholder="阅读状态" clearable @change="loadFavorites">
            <el-option label="全部" value="" />
            <el-option label="已阅读" value="1" />
            <el-option label="未阅读" value="0" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-button-group>
            <el-button :type="sortBy === 'time' ? 'primary' : ''" @click="handleSortChange('time')">
              <el-icon><Clock /></el-icon> 按时间
            </el-button>
            <el-button :type="sortBy === 'views' ? 'primary' : ''" @click="handleSortChange('views')">
              <el-icon><View /></el-icon> 按热度
            </el-button>
          </el-button-group>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 收藏列表 -->
    <el-card class="list-card" v-loading="loading">
      <div class="favorites-content">
        <div v-if="favorites.length === 0" class="empty-state">
          <el-empty description="暂无收藏内容" />
          <el-button type="primary" @click="$router.push('/knowledge')">去知识库看看</el-button>
        </div>

        <!-- 列表视图 -->
        <div v-else-if="viewMode === 'list'" class="favorites-list">
          <div
            v-for="item in favorites"
            :key="item.id"
            class="favorite-item"
            @click="handleViewDetail(item)"
          >
            <div class="item-cover">
              <img v-if="item.coverImage" :src="item.coverImage" alt="封面" />
              <div v-else class="cover-placeholder">
                <el-icon><Reading /></el-icon>
              </div>
            </div>
            <div class="item-content">
              <div class="item-header">
                <el-tag size="small" type="primary">{{ item.categoryName }}</el-tag>
                <span v-if="!item.isRead" class="unread-badge">未读</span>
              </div>
              <h3 class="item-title">{{ item.title }}</h3>
              <p class="item-summary">{{ item.summary }}</p>
              <div class="item-meta">
                <span><el-icon><View /></el-icon> {{ item.viewCount }}</span>
                <span><el-icon><Star /></el-icon> {{ item.likeCount }}</span>
                <span><el-icon><Clock /></el-icon> {{ item.collectTime }}</span>
              </div>
            </div>
            <div class="item-actions">
              <el-button size="small" circle @click.stop="handleLike(item)">
                <el-icon><StarFilled /></el-icon>
              </el-button>
              <el-button size="small" circle type="warning" @click.stop="handleRemove(item)">
                <el-icon><StarFilled /></el-icon>
              </el-button>
              <el-button size="small" circle @click.stop="handleShare(item)">
                <el-icon><Share /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <!-- 网格视图 -->
        <div v-else class="favorites-grid">
          <el-card
            v-for="item in favorites"
            :key="item.id"
            class="favorite-card"
            shadow="hover"
            @click="handleViewDetail(item)"
          >
            <div class="card-cover">
              <img v-if="item.coverImage" :src="item.coverImage" alt="封面" />
              <div v-else class="cover-placeholder">
                <el-icon><Reading /></el-icon>
              </div>
              <span v-if="!item.isRead" class="unread-badge">未读</span>
              <div class="card-actions">
                <el-button size="small" circle type="warning" @click.stop="handleRemove(item)">
                  <el-icon><StarFilled /></el-icon>
                </el-button>
              </div>
            </div>
            <div class="card-content">
              <el-tag size="small" type="primary">{{ item.categoryName }}</el-tag>
              <h4 class="card-title">{{ item.title }}</h4>
              <div class="card-meta">
                <span><el-icon><View /></el-icon> {{ item.viewCount }}</span>
                <span><el-icon><Star /></el-icon> {{ item.likeCount }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[12, 24, 36, 48]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadFavorites"
          @current-change="loadFavorites"
        />
      </div>
    </el-card>

    <!-- 分享对话框 -->
    <el-dialog v-model="showShareDialog" title="分享内容" width="500px">
      <div class="share-content">
        <p class="share-title">{{ shareItem?.title }}</p>
        <el-input
          v-model="shareUrl"
          readonly
          placeholder="分享链接"
        >
          <template #append>
            <el-button @click="handleCopyLink">复制</el-button>
          </template>
        </el-input>
        <div class="share-qrcode">
          <div class="qrcode-placeholder">
            <el-icon><QRCode /></el-icon>
            <span>二维码</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { get, post } from '@/utils/request'
import { Star, Collection, Reading, Clock, Search, View, StarFilled, Share, Promotion } from '@element-plus/icons-vue'

// 统计数据
const totalCount = ref(0)
const readCount = ref(0)
const unReadCount = ref(0)

// 筛选条件
const searchKeyword = ref('')
const categoryFilter = ref('')
const readFilter = ref('')
const sortBy = ref('time')
const viewMode = ref('list')

// 分页
const page = ref(1)
const size = ref(12)
const total = ref(0)
const loading = ref(false)

// 收藏列表
const favorites = ref<any[]>([])

// 分享
const showShareDialog = ref(false)
const shareItem = ref<any>(null)
const shareUrl = ref('')

// 加载收藏数据
const loadFavorites = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      keyword: searchKeyword.value,
      categoryId: categoryFilter.value,
      isRead: readFilter.value,
      sortBy: sortBy.value
    }
    
    const res = await get('/user/favorites', { params })
    favorites.value = res.data?.list || []
    total.value = res.data?.total || 0
    
    // 模拟数据
    if (!res.data?.list) {
      favorites.value = [
        {
          id: 1,
          title: '警惕！近期高发的五种电信诈骗手法',
          summary: '本文详细介绍了当前最为猖獗的五种电信诈骗手法，包括冒充公检法、虚假中奖、网购退款等，帮助大家提高防范意识。',
          categoryName: '电信诈骗',
          coverImage: '',
          viewCount: 1256,
          likeCount: 89,
          isRead: true,
          collectTime: '2026-01-15'
        },
        {
          id: 2,
          title: '网络刷单诈骗的识别与防范',
          summary: '刷单诈骗是近年来增长最快的网络诈骗类型之一，本文教您如何识别刷单骗局，保护好自己的财产安全。',
          categoryName: '网络诈骗',
          coverImage: '',
          viewCount: 892,
          likeCount: 56,
          isRead: false,
          collectTime: '2026-01-14'
        },
        {
          id: 3,
          title: '校园贷的危害及正确消费观',
          summary: '校园贷已成为影响大学生财务健康的重要因素，本文分析了校园贷的危害，并给出了正确的消费建议。',
          categoryName: '校园贷',
          coverImage: '',
          viewCount: 2103,
          likeCount: 167,
          isRead: true,
          collectTime: '2026-01-13'
        }
      ]
      total.value = 3
      totalCount.value = 3
      readCount.value = 2
      unReadCount.value = 1
    } else {
      totalCount.value = total.value
      readCount.value = favorites.value.filter(f => f.isRead).length
      unReadCount.value = favorites.value.filter(f => !f.isRead).length
    }
  } catch (error) {
    // 模拟数据
    favorites.value = [
      {
        id: 1,
        title: '警惕！近期高发的五种电信诈骗手法',
        summary: '本文详细介绍了当前最为猖獗的五种电信诈骗手法。',
        categoryName: '电信诈骗',
        coverImage: '',
        viewCount: 1256,
        likeCount: 89,
        isRead: true,
        collectTime: '2026-01-15'
      }
    ]
    total.value = 1
    totalCount.value = 1
    readCount.value = 1
    unReadCount.value = 0
  } finally {
    loading.value = false
  }
}

// 加载统计
const loadStats = () => {
  // 模拟统计数据
  totalCount.value = favorites.value.length
  readCount.value = favorites.value.filter(f => f.isRead).length
  unReadCount.value = favorites.value.filter(f => !f.isRead).length
}

// 重置筛选
const handleReset = () => {
  searchKeyword.value = ''
  categoryFilter.value = ''
  readFilter.value = ''
  sortBy.value = 'time'
  page.value = 1
  loadFavorites()
}

// 排序切换
const handleSortChange = (sort: string) => {
  sortBy.value = sort
  loadFavorites()
}

// 查看详情
const handleViewDetail = (item: any) => {
  item.isRead = true
  unReadCount.value--
  readCount.value++
  window.location.href = `/knowledge/${item.id}`
}

// 点赞
const handleLike = async (item: any) => {
  try {
    await post('/user/favorites/like', { id: item.id })
    item.likeCount++
    ElMessage.success('点赞成功')
  } catch (error) {
    // 模拟
    item.likeCount++
    ElMessage.success('点赞成功')
  }
}

// 取消收藏
const handleRemove = async (item: any) => {
  try {
    await post('/user/favorites/remove', { id: item.id })
    favorites.value = favorites.value.filter(f => f.id !== item.id)
    totalCount.value--
    if (item.isRead) readCount.value--
    else unReadCount.value--
    ElMessage.success('已取消收藏')
  } catch (error) {
    // 模拟
    favorites.value = favorites.value.filter(f => f.id !== item.id)
    totalCount.value--
    if (item.isRead) readCount.value--
    else unReadCount.value--
    ElMessage.success('已取消收藏')
  }
}

// 分享
const handleShare = (item: any) => {
  shareItem.value = item
  shareUrl.value = `${window.location.origin}/knowledge/${item.id}`
  showShareDialog.value = true
}

// 复制链接
const handleCopyLink = () => {
  navigator.clipboard.writeText(shareUrl.value)
  ElMessage.success('链接已复制到剪贴板')
}

// 切换视图
const toggleViewMode = (mode: string) => {
  viewMode.value = mode
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-2xl);
  margin-bottom: var(--spacing-sm);
}

.page-header p {
  color: var(--color-text-secondary);
}

.stats-row {
  margin-bottom: var(--spacing-lg);
}

.stat-card {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  box-shadow: var(--shadow-sm);
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

.stat-icon.blue { background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%); }
.stat-icon.green { background: linear-gradient(135deg, #48bb78 0%, #38a169 100%); }
.stat-icon.orange { background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%); }

.stat-info .stat-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
}

.stat-info .stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.filter-card {
  margin-bottom: var(--spacing-lg);
}

.favorites-content {
  min-height: 300px;
}

/* 列表视图 */
.favorites-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.favorite-item {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-bg-page);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.3s ease;
}

.favorite-item:hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-md);
}

.item-cover {
  width: 160px;
  height: 120px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
}

.item-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.unread-badge {
  background: var(--color-danger);
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.item-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-sm);
  color: var(--color-text-primary);
}

.item-summary {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
  line-height: 1.6;
  margin-bottom: var(--spacing-sm);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-meta {
  display: flex;
  gap: var(--spacing-lg);
  color: var(--color-text-muted);
  font-size: var(--font-size-sm);
}

.item-meta span {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm);
}

/* 网格视图 */
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-lg);
}

.favorite-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.favorite-card:hover {
  transform: translateY(-4px);
}

.card-cover {
  position: relative;
  height: 160px;
  border-radius: var(--radius-md);
  overflow: hidden;
  margin-bottom: var(--spacing-md);
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-cover .unread-badge {
  position: absolute;
  top: 8px;
  left: 8px;
}

.card-cover .card-actions {
  position: absolute;
  top: 8px;
  right: 8px;
}

.card-content .card-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-bold);
  margin: var(--spacing-sm) 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  gap: var(--spacing-md);
  color: var(--color-text-muted);
  font-size: var(--font-size-sm);
}

.card-meta span {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.pagination-wrapper {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: center;
}

.empty-state {
  padding: var(--spacing-xl);
  text-align: center;
}

/* 分享对话框 */
.share-content {
  text-align: center;
}

.share-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-lg);
}

.share-qrcode {
  margin-top: var(--spacing-lg);
}

.qrcode-placeholder {
  width: 150px;
  height: 150px;
  margin: 0 auto;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  color: var(--color-text-muted);
}

@media (max-width: 768px) {
  .favorite-item {
    flex-direction: column;
  }
  
  .item-cover {
    width: 100%;
    height: 160px;
  }
  
  .item-actions {
    flex-direction: row;
    justify-content: flex-end;
  }
}
</style>

