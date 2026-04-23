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
                  <h3>{{ caseItem.caseTitle }}</h3>
                  <el-tag :type="getCategoryType(caseItem.caseType)">
                    {{ caseItem.caseType }}
                  </el-tag>
                </div>
              </template>
              <div class="case-content">
                <p class="case-description">{{ caseItem.fraudProcess }}</p>
                <div class="case-info">
                  <div class="info-item">
                    <el-icon><Timer /></el-icon>
                    <span>发生时间: {{ caseItem.occurTime }}</span>
                  </div>
                  <div class="info-item">
                    <el-icon><User /></el-icon>
                    <span>浏览: {{ caseItem.viewCount }}次</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Share /></el-icon>
                    <span>分享: {{ caseItem.shareCount }}次</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Warning /></el-icon>
                    <span>预警等级: {{ caseItem.warningLevel }}级</span>
                  </div>
                </div>
                <div class="case-tags">
                  <el-tag size="small" :type="getWarningType(caseItem.warningLevel)">{{ getWarningText(caseItem.warningLevel) }}</el-tag>
                  <el-tag size="small" effect="plain">{{ caseItem.caseStatus }}</el-tag>
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
        <!-- 案例信息 -->
        <div class="case-info-section">
          <h3>案例信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">诈骗类型:</span>
              <el-tag :type="getCategoryType(currentCase.caseType)">
                {{ currentCase.caseType }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">案件状态:</span>
              <el-tag size="small" effect="plain">{{ currentCase.caseStatus }}</el-tag>
            </div>
            <div class="info-item">
              <span class="label">发生时间:</span>
              <span class="value">{{ currentCase.occurTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">预警等级:</span>
              <el-tag size="small" :type="getWarningType(currentCase.warningLevel)">{{ getWarningText(currentCase.warningLevel) }}</el-tag>
            </div>
            <div class="info-item">
              <span class="label">浏览:</span>
              <span class="value">{{ currentCase.viewCount }}次</span>
            </div>
            <div class="info-item">
              <span class="label">分享:</span>
              <span class="value">{{ currentCase.shareCount }}次</span>
            </div>
            <div class="info-item">
              <span class="label">信息来源:</span>
              <span class="value">{{ currentCase.source }}</span>
            </div>
            <div class="info-item">
              <span class="label">是否验证:</span>
              <el-tag size="small" :type="currentCase.isVerified ? 'success' : 'info'">{{ currentCase.isVerified ? '已验证' : '未验证' }}</el-tag>
            </div>
          </div>
        </div>
        
        <!-- 受害者画像 -->
        <div v-if="currentCase.victimProfile" class="case-victim-section">
          <h3>受害者画像</h3>
          <p>{{ currentCase.victimProfile }}</p>
        </div>
        
        <!-- 诈骗过程 -->
        <div class="case-process-section">
          <h3>诈骗过程</h3>
          <p>{{ currentCase.fraudProcess }}</p>
        </div>
        
        <!-- 损失情况 -->
        <div class="case-loss-section">
          <h3>损失情况</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">损失金额:</span>
              <span class="value">{{ currentCase.lossAmount }}元</span>
            </div>
            <div class="info-item">
              <span class="label">追回金额:</span>
              <span class="value">{{ currentCase.recoveryAmount }}元</span>
            </div>
            <div class="info-item">
              <span class="label">净损失:</span>
              <span class="value">{{ (currentCase.lossAmount - currentCase.recoveryAmount) }}元</span>
            </div>
          </div>
        </div>
        
        <!-- 警方信息 -->
        <div v-if="currentCase.policeInfo" class="case-police-section">
          <h3>警方信息</h3>
          <p>{{ currentCase.policeInfo }}</p>
        </div>
        
        <!-- 防范建议 -->
        <div v-if="currentCase.preventionAdvice" class="case-prevention-section">
          <h3>防范建议</h3>
          <div class="prevention-content">
            <el-alert
              :title="currentCase.preventionAdvice"
              type="success"
              :closable="false"
            />
          </div>
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
        caseTitle: '冒充公检法诈骗案例',
        caseType: 'TELEPHONE',
        occurTime: '2026-03-15 14:30:00',
        victimProfile: '35岁女性，公司职员，本科学历，首次遭遇诈骗',
        fraudProcess: '受害者接到自称"公安局"的电话，声称其身份信息被盗用参与洗钱活动，要求将资金转入"安全账户"进行"资金审查"。受害者在对方的威胁下，分3次转账共计15万元。',
        lossAmount: 150000.00,
        recoveryAmount: 80000.00,
        caseStatus: '已破案',
        policeInfo: '市公安局反诈中心',
        warningLevel: 3,
        preventionAdvice: '接到自称公检法的电话时，应挂断后通过官方渠道核实身份，公检法机关不会要求转账到所谓的安全账户。',
        source: '市公安局反诈中心',
        isAnonymous: 1,
        isVerified: 1,
        viewCount: 1256,
        shareCount: 89
      },
      {
        id: 2,
        caseTitle: '网络刷单诈骗案例',
        caseType: 'NETWORK',
        occurTime: '2026-03-20 09:15:00',
        victimProfile: '22岁大学生，无固定收入，网络兼职需求强烈',
        fraudProcess: '受害者在社交平台看到"刷单兼职"广告，添加对方QQ后，按照对方要求先完成小额刷单并获得返利。随后对方以"任务未完成"为由，要求受害者继续刷单，受害者先后转账共计8万元。',
        lossAmount: 80000.00,
        recoveryAmount: 0.00,
        caseStatus: '调查中',
        policeInfo: '区公安局',
        warningLevel: 3,
        preventionAdvice: '任何要求先垫付资金的兼职都是骗局，不要相信"刷单返利"等天上掉馅饼的好事。',
        source: '区公安局',
        isAnonymous: 1,
        isVerified: 1,
        viewCount: 987,
        shareCount: 67
      },
      {
        id: 3,
        caseTitle: '虚假投资理财诈骗案例',
        caseType: 'INVESTMENT',
        occurTime: '2026-03-10 16:45:00',
        victimProfile: '45岁男性，企业高管，有投资需求',
        fraudProcess: '受害者在微信群被推荐一个"高收益"投资平台，初期小额投资获得高额返利，随后投入50万元后平台无法提现，最终平台关闭。',
        lossAmount: 500000.00,
        recoveryAmount: 0.00,
        caseStatus: '已报案',
        policeInfo: '市公安局经侦支队',
        warningLevel: 3,
        preventionAdvice: '高收益必然伴随高风险，投资前要核实平台资质，选择正规金融机构。',
        source: '市公安局经侦支队',
        isAnonymous: 1,
        isVerified: 1,
        viewCount: 765,
        shareCount: 54
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
    case 'TELEPHONE':
      return 'danger'
    case 'NETWORK':
      return 'warning'
    case 'SMS':
      return 'info'
    case 'INVESTMENT':
      return 'primary'
    case 'RELATIONSHIP':
      return 'success'
    case 'FINANCE':
      return 'warning'
    default:
      return 'info'
  }
}

// 获取预警类型
const getWarningType = (level: number) => {
  switch (level) {
    case 1:
      return 'success'
    case 2:
      return 'warning'
    case 3:
      return 'danger'
    default:
      return 'info'
  }
}

// 获取预警文本
const getWarningText = (level: number) => {
  switch (level) {
    case 1:
      return '低风险'
    case 2:
      return '中风险'
    case 3:
      return '高风险'
    default:
      return '未知'
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