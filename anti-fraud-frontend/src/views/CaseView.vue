<template>
  <div class="case-view-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>真实案例回放</h1>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索案例"
            prefix-icon="Search"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          />
        </div>
      </el-header>
      
      <el-main>
        <!-- 案例统计信息 -->
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>案例统计</span>
              <el-button type="primary" @click="loadCaseStats">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          
          <div v-if="statsLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <div v-else class="stats-content">
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-value">{{ caseStats.totalCases || 0 }}</div>
                <div class="stat-label">案例总数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ caseStats.publishedCases || 0 }}</div>
                <div class="stat-label">已发布案例</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ caseStats.totalViews || 0 }}</div>
                <div class="stat-label">总浏览次数</div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 筛选条件 -->
        <el-card class="filter-card" style="margin-top: 20px">
          <div class="filter-content">
            <el-select
              v-model="filter.category"
              placeholder="案例分类"
              style="width: 150px; margin-right: 10px"
              @change="loadCases"
            >
              <el-option label="全部" value="" />
              <el-option label="电信诈骗" value="电信诈骗" />
              <el-option label="网络诈骗" value="网络诈骗" />
              <el-option label="金融诈骗" value="金融诈骗" />
              <el-option label="其他诈骗" value="其他诈骗" />
            </el-select>
            
            <el-select
              v-model="filter.caseType"
              placeholder="案例类型"
              style="width: 150px; margin-right: 10px"
              @change="loadCases"
            >
              <el-option label="全部" value="" />
              <el-option label="视频案例" value="视频" />
              <el-option label="音频案例" value="音频" />
              <el-option label="图文案例" value="图文" />
            </el-select>
            
            <el-select
              v-model="filter.difficulty"
              placeholder="难度级别"
              style="width: 150px; margin-right: 10px"
              @change="loadCases"
            >
              <el-option label="全部" value="" />
              <el-option label="简单" value="简单" />
              <el-option label="中等" value="中等" />
              <el-option label="困难" value="困难" />
            </el-select>
            
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
        </el-card>
        
        <!-- 案例列表 -->
        <el-card class="cases-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>案例列表</span>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="cases.length === 0" description="暂无案例" />
          
          <div v-else class="case-list">
            <el-card 
              v-for="caseItem in cases" 
              :key="caseItem.id"
              class="case-item"
              :body-style="{ padding: '20px' }"
              @click="viewCase(caseItem)"
            >
              <template #header>
                <div class="case-header">
                  <h3>{{ caseItem.title }}</h3>
                  <el-tag :type="getCategoryType(caseItem.category)">
                    {{ caseItem.category }}
                  </el-tag>
                </div>
              </template>
              <div class="case-content">
                <p class="case-description">{{ caseItem.description }}</p>
                <div class="case-info">
                  <div class="info-item">
                    <el-icon><Timer /></el-icon>
                    <span>时长: {{ caseItem.duration }}分钟</span>
                  </div>
                  <div class="info-item">
                    <el-icon><User /></el-icon>
                    <span>浏览: {{ caseItem.viewCount }}次</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Star /></el-icon>
                    <span>点赞: {{ caseItem.likeCount }}次</span>
                  </div>
                  <div class="info-item">
                    <el-icon><ChatLineRound /></el-icon>
                    <span>评论: {{ caseItem.commentCount }}条</span>
                  </div>
                </div>
                <div class="case-tags">
                  <el-tag size="small" effect="plain">{{ caseItem.caseType }}</el-tag>
                  <el-tag size="small" effect="plain">{{ caseItem.difficulty }}</el-tag>
                </div>
              </div>
            </el-card>
          </div>
          
          <!-- 分页 -->
          <div class="pagination" v-if="cases.length > 0">
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
        
        <!-- 热门案例 -->
        <el-card class="hot-cases-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>热门案例</span>
            </div>
          </template>
          
          <div v-if="hotCasesLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="hotCases.length === 0" description="暂无热门案例" />
          
          <div v-else class="hot-case-list">
            <div 
              v-for="(hotCase, index) in hotCases" 
              :key="hotCase.id"
              class="hot-case-item"
              @click="viewCase(hotCase)"
            >
              <div class="hot-case-rank">{{ index + 1 }}</div>
              <div class="hot-case-info">
                <h4>{{ hotCase.title }}</h4>
                <div class="hot-case-stats">
                  <span>浏览: {{ hotCase.viewCount }}次</span>
                  <span>点赞: {{ hotCase.likeCount }}次</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>
    
    <!-- 案例详情对话框 -->
    <el-dialog
      v-model="caseDialogVisible"
      :title="currentCase?.title || '案例详情'"
      width="800px"
      custom-class="case-dialog"
    >
      <div v-if="caseLoading" class="loading-container">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="currentCase" class="case-details">
        <!-- 案例媒体内容 -->
        <div class="case-media">
          <div v-if="currentCase.videoUrl" class="video-container">
            <video
              :src="currentCase.videoUrl"
              controls
              width="100%"
              height="400px"
            />
          </div>
          <div v-else-if="currentCase.audioUrl" class="audio-container">
            <audio
              :src="currentCase.audioUrl"
              controls
              width="100%"
            />
          </div>
          <div v-else-if="currentCase.imageUrl" class="image-container">
            <el-image
              :src="currentCase.imageUrl"
              fit="cover"
              style="width: 100%; height: 400px"
            />
          </div>
          <div v-else class="no-media">
            <el-empty description="暂无媒体内容" />
          </div>
        </div>
        
        <!-- 案例信息 -->
        <div class="case-info-section">
          <h3>案例信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">分类:</span>
              <el-tag :type="getCategoryType(currentCase.category)">
                {{ currentCase.category }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">类型:</span>
              <el-tag size="small" effect="plain">{{ currentCase.caseType }}</el-tag>
            </div>
            <div class="info-item">
              <span class="label">难度:</span>
              <el-tag size="small" effect="plain">{{ currentCase.difficulty }}</el-tag>
            </div>
            <div class="info-item">
              <span class="label">时长:</span>
              <span class="value">{{ currentCase.duration }}分钟</span>
            </div>
            <div class="info-item">
              <span class="label">浏览:</span>
              <span class="value">{{ currentCase.viewCount }}次</span>
            </div>
            <div class="info-item">
              <span class="label">点赞:</span>
              <span class="value">{{ currentCase.likeCount }}次</span>
            </div>
            <div class="info-item">
              <span class="label">评论:</span>
              <span class="value">{{ currentCase.commentCount }}条</span>
            </div>
          </div>
        </div>
        
        <!-- 案例描述 -->
        <div class="case-description-section">
          <h3>案例描述</h3>
          <p>{{ currentCase.description }}</p>
        </div>
        
        <!-- 案例数据 -->
        <div v-if="currentCase.caseData" class="case-data-section">
          <h3>案例数据</h3>
          <pre>{{ currentCase.caseData }}</pre>
        </div>
        
        <!-- 案例操作 -->
        <div class="case-actions">
          <el-button type="primary" @click="handleLike(currentCase.id)">
            <el-icon><Star /></el-icon>
            点赞
          </el-button>
          <el-button type="success" @click="handleComment(currentCase.id)">
            <el-icon><ChatLineRound /></el-icon>
            评论
          </el-button>
          <el-button @click="shareCase(currentCase)">
            <el-icon><Share /></el-icon>
            分享
          </el-button>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="caseDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Refresh, Loading, Search, Star, ChatLineRound, Timer, User, Share } from '@element-plus/icons-vue'

// 状态
const loading = ref(false)
const statsLoading = ref(false)
const hotCasesLoading = ref(false)
const caseLoading = ref(false)
const cases = ref<any[]>([])
const hotCases = ref<any[]>([])
const total = ref(0)
const caseDialogVisible = ref(false)
const currentCase = ref<any>(null)
const searchKeyword = ref('')

// 案例统计
const caseStats = ref({
  totalCases: 0,
  publishedCases: 0,
  totalViews: 0
})

// 筛选条件
const filter = reactive({
  category: '',
  caseType: '',
  difficulty: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})

// 加载案例列表
const loadCases = async () => {
  loading.value = true
  try {
    const res = await get('/case/list', {
      params: {
        category: filter.category,
        caseType: filter.caseType,
        difficulty: filter.difficulty,
        page: pagination.current,
        size: pagination.size
      }
    })
    if (res.code === 200 && res.data) {
      cases.value = res.data
      total.value = 100 // 模拟总数
    }
  } catch (error) {
    console.error('加载案例列表失败:', error)
    // 模拟数据
    cases.value = [
      {
        id: 1,
        title: '电信诈骗案例：冒充公检法诈骗',
        description: '本案例讲述了一位市民如何被冒充公检法的诈骗分子骗取了大量财产，通过详细的案例分析，帮助用户了解此类诈骗的作案手法和防范措施。',
        category: '电信诈骗',
        caseType: '视频',
        difficulty: '中等',
        duration: 15,
        viewCount: 1256,
        likeCount: 89,
        commentCount: 23,
        videoUrl: 'https://example.com/video1.mp4'
      },
      {
        id: 2,
        title: '网络诈骗案例：网络兼职刷单诈骗',
        description: '本案例讲述了一位大学生如何被网络兼职刷单诈骗，通过真实的聊天记录和转账记录，展示了此类诈骗的完整流程和防范方法。',
        category: '网络诈骗',
        caseType: '图文',
        difficulty: '简单',
        duration: 10,
        viewCount: 987,
        likeCount: 67,
        commentCount: 18,
        imageUrl: 'https://example.com/image1.jpg'
      },
      {
        id: 3,
        title: '金融诈骗案例：投资理财诈骗',
        description: '本案例讲述了一位退休老人如何被投资理财诈骗，通过详细的案例分析，帮助用户了解此类诈骗的作案手法和防范措施。',
        category: '金融诈骗',
        caseType: '音频',
        difficulty: '困难',
        duration: 20,
        viewCount: 765,
        likeCount: 54,
        commentCount: 15,
        audioUrl: 'https://example.com/audio1.mp3'
      }
    ]
  } finally {
    loading.value = false
  }
}

// 加载案例统计信息
const loadCaseStats = async () => {
  statsLoading.value = true
  try {
    const res = await get('/case/stats')
    if (res.code === 200 && res.data) {
      caseStats.value = res.data
    }
  } catch (error) {
    console.error('加载案例统计信息失败:', error)
    // 模拟数据
    caseStats.value = {
      totalCases: 15,
      publishedCases: 12,
      totalViews: 8543
    }
  } finally {
    statsLoading.value = false
  }
}

// 加载热门案例
const loadHotCases = async () => {
  hotCasesLoading.value = true
  try {
    const res = await get('/case/hot')
    if (res.code === 200 && res.data) {
      hotCases.value = res.data
    }
  } catch (error) {
    console.error('加载热门案例失败:', error)
    // 模拟数据
    hotCases.value = [
      {
        id: 1,
        title: '电信诈骗案例：冒充公检法诈骗',
        viewCount: 1256,
        likeCount: 89
      },
      {
        id: 2,
        title: '网络诈骗案例：网络兼职刷单诈骗',
        viewCount: 987,
        likeCount: 67
      },
      {
        id: 3,
        title: '金融诈骗案例：投资理财诈骗',
        viewCount: 765,
        likeCount: 54
      },
      {
        id: 4,
        title: '其他诈骗案例：冒充客服诈骗',
        viewCount: 654,
        likeCount: 43
      },
      {
        id: 5,
        title: '电信诈骗案例：中奖诈骗',
        viewCount: 543,
        likeCount: 32
      }
    ]
  } finally {
    hotCasesLoading.value = false
  }
}

// 查看案例详情
const viewCase = async (caseItem: any) => {
  caseLoading.value = true
  try {
    const res = await get(`/case/detail/${caseItem.id}`)
    if (res.code === 200 && res.data) {
      currentCase.value = res.data
      caseDialogVisible.value = true
    } else {
      ElMessage.error('获取案例详情失败')
    }
  } catch (error) {
    console.error('获取案例详情失败:', error)
    // 模拟数据
    currentCase.value = caseItem
    caseDialogVisible.value = true
  } finally {
    caseLoading.value = false
  }
}

// 搜索案例
const handleSearch = async () => {
  if (searchKeyword.value) {
    loading.value = true
    try {
      const res = await get('/case/search', {
        params: {
          keyword: searchKeyword.value,
          page: pagination.current,
          size: pagination.size
        }
      })
      if (res.code === 200 && res.data) {
        cases.value = res.data
        total.value = 100 // 模拟总数
      }
    } catch (error) {
      console.error('搜索案例失败:', error)
      ElMessage.error('搜索案例失败')
    } finally {
      loading.value = false
    }
  } else {
    loadCases()
  }
}

// 点赞案例
const handleLike = async (caseId: number) => {
  try {
    const res = await post(`/case/like/${caseId}`)
    if (res.code === 200) {
      ElMessage.success('点赞成功')
      if (currentCase.value && currentCase.value.id === caseId) {
        currentCase.value.likeCount++
      }
    } else {
      ElMessage.error('点赞失败')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.success('点赞成功')
    if (currentCase.value && currentCase.value.id === caseId) {
      currentCase.value.likeCount++
    }
  }
}

// 评论案例
const handleComment = (caseId: number) => {
  ElMessage.info('评论功能待实现')
}

// 分享案例
const shareCase = (caseItem: any) => {
  ElMessage.info('分享功能待实现')
}

// 获取分类类型
const getCategoryType = (category: string) => {
  switch (category) {
    case '电信诈骗':
      return 'danger'
    case '网络诈骗':
      return 'warning'
    case '金融诈骗':
      return 'info'
    default:
      return 'success'
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadCases()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadCases()
}

onMounted(() => {
  loadCases()
  loadCaseStats()
  loadHotCases()
})
</script>

<style scoped>
.case-view-page {
  min-height: 100vh;
  background: var(--color-bg-page);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-content h1 {
  margin: 0;
  font-size: var(--font-size-2xl);
  color: var(--color-text-primary);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: var(--color-text-secondary);
}

.stats-content {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
}

.stat-item {
  background: var(--color-bg-container);
  padding: 20px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  text-align: center;
}

.stat-value {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  margin-bottom: 8px;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.filter-content {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.case-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.case-item {
  height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;
}

.case-item:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-md);
}

.case-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.case-header h3 {
  margin: 0;
  color: var(--color-text-primary);
}

.case-description {
  margin-bottom: 20px;
  line-height: 1.6;
  color: var(--color-text-secondary);
}

.case-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.case-tags {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.hot-case-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-case-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: var(--color-bg-container);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.3s ease;
}

.hot-case-item:hover {
  background: var(--color-bg-container-hover);
  transform: translateX(5px);
}

.hot-case-rank {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary);
  color: white;
  border-radius: 50%;
}

.hot-case-info {
  flex: 1;
}

.hot-case-info h4 {
  margin: 0 0 8px 0;
  color: var(--color-text-primary);
}

.hot-case-stats {
  display: flex;
  gap: 16px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.case-dialog {
  max-height: 90vh;
  overflow-y: auto;
}

.case-details {
  padding: 20px 0;
}

.case-media {
  margin-bottom: 30px;
}

.video-container,
.audio-container,
.image-container {
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
}

.no-media {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-container);
  border-radius: var(--radius-lg);
}

.case-info-section,
.case-description-section,
.case-data-section {
  margin-bottom: 30px;
}

.case-info-section h3,
.case-description-section h3,
.case-data-section h3 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.info-grid .info-item {
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}

.info-grid .info-item .label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.info-grid .info-item .value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.case-description-section p {
  line-height: 1.6;
  color: var(--color-text-primary);
}

.case-data-section pre {
  background: var(--color-bg-container);
  padding: 20px;
  border-radius: var(--radius-lg);
  overflow-x: auto;
  font-size: var(--font-size-sm);
  line-height: 1.4;
  color: var(--color-text-primary);
}

.case-actions {
  display: flex;
  gap: 12px;
  margin-top: 30px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .filter-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .case-list {
    grid-template-columns: 1fr;
  }
  
  .case-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>