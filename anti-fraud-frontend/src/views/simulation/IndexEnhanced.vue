<template>
  <div class="simulation-index">
    <el-card class="page-header-card">
      <div class="header-content">
        <div class="header-info">
          <h1>演练模拟中心</h1>
          <p>通过真实场景模拟，提升您的防骗应对能力</p>
        </div>
        <el-button type="primary" @click="$router.push('/simulation/history')">
          <el-icon><Clock /></el-icon> 演练历史
        </el-button>
      </div>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-icon blue">
            <el-icon><Collection /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ totalScenes }}</span>
            <span class="stat-label">演练场景</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-icon green">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ completedScenes }}</span>
            <span class="stat-label">已完成</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-icon purple">
            <el-icon><Trophy /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ passRate }}%</span>
            <span class="stat-label">通过率</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-icon orange">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ averageScore }}</span>
            <span class="stat-label">平均得分</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card class="filter-card">
      <div class="filter-row">
        <el-input v-model="searchQuery" placeholder="搜索演练场景..." clearable class="search-input">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="selectedCategory" placeholder="全部分类" clearable>
          <el-option v-for="cat in categories" :key="cat.value" :label="cat.label" :value="cat.value" />
        </el-select>
        <el-select v-model="selectedDifficulty" placeholder="全部难度" clearable>
          <el-option label="入门" :value="1" />
          <el-option label="进阶" :value="2" />
          <el-option label="专家" :value="3" />
        </el-select>
        <el-select v-model="selectedStatus" placeholder="全部状态" clearable>
          <el-option label="未开始" :value="0" />
          <el-option label="进行中" :value="1" />
          <el-option label="已通过" :value="2" />
        </el-select>
      </div>
      <div class="category-tags">
        <el-tag v-for="cat in categories" :key="cat.value"
          :type="selectedCategory === cat.value ? 'primary' : 'info'"
          @click="selectedCategory = selectedCategory === cat.value ? null : cat.value">
          {{ cat.label }}
        </el-tag>
      </div>
    </el-card>

    <div class="scenes-grid" v-loading="loading">
      <el-card v-for="scene in filteredScenes" :key="scene.id" class="scene-card" @click="handleStartScene(scene)">
        <div class="scene-cover">
          <img :src="scene.coverImage || '/scene-default.jpg'" :alt="scene.title" />
          <div class="scene-overlay">
            <el-button type="primary" circle size="large">
              <el-icon><VideoPlay /></el-icon>
            </el-button>
          </div>
          <el-tag class="category-tag" :type="getCategoryType(scene.category)">{{ scene.categoryName }}</el-tag>
          <el-tag v-if="scene.status === 2" class="status-tag success">已通过</el-tag>
          <el-tag v-else-if="scene.status === 1" class="status-tag warning">进行中</el-tag>
        </div>
        <div class="scene-content">
          <h3 class="scene-title">{{ scene.title }}</h3>
          <p class="scene-description">{{ scene.description }}</p>
          <div class="scene-meta">
            <span><el-icon><Timer /></el-icon> {{ scene.duration }}分钟</span>
            <span><el-icon><Star /></el-icon> {{ scene.score }}分</span>
            <span><el-icon><User /></el-icon> {{ scene.participantCount }}人参与</span>
          </div>
          <div v-if="scene.progress" class="scene-progress">
            <span class="progress-label">进度</span>
            <el-progress :percentage="scene.progress" :show-text="false" :stroke-width="6" />
          </div>
          <div class="scene-tags">
            <el-tag v-for="tag in scene.tags?.slice(0, 3)" :key="tag" size="small">{{ tag }}</el-tag>
          </div>
        </div>
      </el-card>
    </div>

    <el-empty v-if="!loading && filteredScenes.length === 0" description="暂无符合条件的演练场景" />

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="totalCount"
        :page-sizes="[8, 16, 24, 32]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Clock, VideoPlay, Timer, Star, User, Collection, CircleCheck, Trophy } from '@element-plus/icons-vue'

const router = useRouter()

const loading = ref(true)
const searchQuery = ref('')
const selectedCategory = ref<number | null>(null)
const selectedDifficulty = ref<number | null>(null)
const selectedStatus = ref<number | null>(null)
const currentPage = ref(1)
const pageSize = ref(8)
const totalCount = ref(0)

const totalScenes = ref(20)
const completedScenes = ref(8)
const passRate = ref(75)
const averageScore = ref(82)

const categories = [
  { value: 1, label: '电话诈骗' },
  { value: 2, label: '网络诈骗' },
  { value: 3, label: '情感诈骗' },
  { value: 4, label: '投资诈骗' },
  { value: 5, label: '游戏诈骗' },
  { value: 6, label: '冒充诈骗' }
]

const scenes = ref<any[]>([])

const filteredScenes = computed(() => {
  let result = scenes.value
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(s => s.title.toLowerCase().includes(query) || s.description.toLowerCase().includes(query))
  }
  if (selectedCategory.value) result = result.filter(s => s.category === selectedCategory.value)
  if (selectedDifficulty.value) result = result.filter(s => s.difficulty === selectedDifficulty.value)
  if (selectedStatus.value !== null) result = result.filter(s => s.status === selectedStatus.value)
  return result
})

const getCategoryType = (category: number) => {
  const types: Record<number, string> = { 1: 'danger', 2: 'warning', 3: 'success', 4: 'primary', 5: 'info', 6: '' }
  return types[category] || 'info'
}

const handleStartScene = (scene: any) => {
  router.push(`/simulation/${scene.id}`)
}

const handleSizeChange = (size: number) => { pageSize.value = size }
const handlePageChange = (page: number) => { currentPage.value = page }

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    scenes.value = [
      { id: 1, title: '冒充客服诈骗演练', description: '学习如何识别和应对假冒电商客服的诈骗电话', coverImage: '/scene1.jpg', category: 1, categoryName: '电话诈骗', difficulty: 1, duration: 15, score: 100, participantCount: 1256, status: 2, progress: 100, tags: ['客服诈骗', '电话识别'] },
      { id: 2, title: '杀猪盘情感诈骗', description: '模拟网络交友中可能遇到的杀猪盘诈骗场景', coverImage: '/scene2.jpg', category: 3, categoryName: '情感诈骗', difficulty: 3, duration: 25, score: 100, participantCount: 896, status: 1, progress: 60, tags: ['杀猪盘', '情感分析'] },
      { id: 3, title: '刷单返利陷阱', description: '识破刷单诈骗的高额返利诱惑', coverImage: '/scene3.jpg', category: 2, categoryName: '网络诈骗', difficulty: 2, duration: 20, score: 100, participantCount: 2340, status: 0, progress: 0, tags: ['刷单', '返利'] },
      { id: 4, title: '投资理财诈骗', description: '识别虚假投资平台和非法集资', coverImage: '/scene4.jpg', category: 4, categoryName: '投资诈骗', difficulty: 2, duration: 20, score: 100, participantCount: 1567, status: 0, progress: 0, tags: ['投资', '理财'] },
      { id: 5, title: '游戏账号交易诈骗', description: '游戏交易中的常见欺诈手法防范', coverImage: '/scene5.jpg', category: 5, categoryName: '游戏诈骗', difficulty: 1, duration: 12, score: 100, participantCount: 3456, status: 2, progress: 100, tags: ['游戏', '交易'] },
      { id: 6, title: '冒充公检法诈骗', description: '学习应对冒充公安机关的诈骗电话', coverImage: '/scene6.jpg', category: 6, categoryName: '冒充诈骗', difficulty: 3, duration: 25, score: 100, participantCount: 1890, status: 0, progress: 0, tags: ['公检法', '恐吓'] },
      { id: 7, title: '网络购物退款诈骗', description: '防范假冒卖家发起的退款诈骗', coverImage: '/scene7.jpg', category: 2, categoryName: '网络诈骗', difficulty: 1, duration: 15, score: 100, participantCount: 2789, status: 2, progress: 100, tags: ['网购', '退款'] },
      { id: 8, title: '钓鱼网站识别', description: '学习识别和防范钓鱼网站', coverImage: '/scene8.jpg', category: 2, categoryName: '网络诈骗', difficulty: 2, duration: 18, score: 100, participantCount: 2134, status: 1, progress: 40, tags: ['钓鱼', '网站'] }
    ]
    totalCount.value = scenes.value.length
    loading.value = false
  }, 500)
}

onMounted(() => { loadData() })
</script>

<style scoped>
.simulation-index { max-width: 1440px; margin: 0 auto; }
.page-header-card { margin-bottom: var(--spacing-6); }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.header-info h1 { margin: 0 0 var(--spacing-2); font-size: var(--font-size-3xl); }
.header-info p { margin: 0; color: var(--text-secondary); }
.stats-row { margin-bottom: var(--spacing-6); }
.stat-card { display: flex; align-items: center; gap: var(--spacing-4); padding: var(--spacing-4); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-sm); }
.stat-icon { width: 56px; height: 56px; border-radius: var(--radius-xl); display: flex; align-items: center; justify-content: center; font-size: 24px; color: white; }
.stat-icon.blue { background: linear-gradient(135deg, #667eea, #764ba2); }
.stat-icon.green { background: linear-gradient(135deg, #11998e, #38ef7d); }
.stat-icon.purple { background: linear-gradient(135deg, #7c3aed, #a855f7); }
.stat-icon.orange { background: linear-gradient(135deg, #f97316, #fbbf24); }
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: var(--font-size-2xl); font-weight: var(--font-weight-bold); }
.stat-label { font-size: var(--font-size-sm); color: var(--text-secondary); }
.filter-card { margin-bottom: var(--spacing-6); }
.filter-row { display: flex; gap: var(--spacing-3); flex-wrap: wrap; margin-bottom: var(--spacing-3); }
.search-input { width: 240px; }
.category-tags { display: flex; gap: var(--spacing-2); flex-wrap: wrap; }
.category-tags .el-tag { cursor: pointer; }
.scenes-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: var(--spacing-6); margin-bottom: var(--spacing-6); }
.scene-card { cursor: pointer; transition: all var(--transition-normal); }
.scene-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-lg); }
.scene-cover { position: relative; height: 180px; border-radius: var(--radius-md); overflow: hidden; margin-bottom: var(--spacing-4); }
.scene-cover img { width: 100%; height: 100%; object-fit: cover; }
.scene-overlay { position: absolute; inset: 0; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; opacity: 0; transition: opacity var(--transition-fast); }
.scene-card:hover .scene-overlay { opacity: 1; }
.category-tag { position: absolute; top: 8px; left: 8px; }
.status-tag { position: absolute; top: 8px; right: 8px; }
.status-tag.success { background: var(--success-color); border-color: var(--success-color); }
.status-tag.warning { background: var(--warning-color); border-color: var(--warning-color); }
.scene-content { padding: 0 var(--spacing-2); }
.scene-title { margin: 0 0 var(--spacing-2); font-size: var(--font-size-lg); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.scene-description { color: var(--text-secondary); font-size: var(--font-size-sm); margin-bottom: var(--spacing-3); overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.scene-meta { display: flex; gap: var(--spacing-4); color: var(--text-secondary); font-size: var(--font-size-xs); margin-bottom: var(--spacing-3); }
.scene-meta span { display: flex; align-items: center; gap: 4px; }
.scene-progress { display: flex; align-items: center; gap: var(--spacing-2); margin-bottom: var(--spacing-3); }
.progress-label { font-size: var(--font-size-xs); color: var(--text-secondary); white-space: nowrap; }
.scene-progress .el-progress { flex: 1; }
.scene-tags { display: flex; gap: var(--spacing-1); flex-wrap: wrap; }
.pagination-wrapper { display: flex; justify-content: center; margin-top: var(--spacing-6); }
@media (max-width: 768px) {
  .header-content { flex-direction: column; gap: var(--spacing-4); text-align: center; }
  .filter-row { flex-direction: column; }
  .search-input { width: 100%; }
  .scenes-grid { grid-template-columns: 1fr; }
}
</style>
