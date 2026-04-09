<template>
  <div class="simulation-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <el-icon><VideoPlay /></el-icon>
          演练模拟
        </h1>
        <p>通过真实场景模拟，提升反诈实战能力</p>
        <div class="header-stats">
          <div class="stat-item">
            <el-icon><VideoCamera /></el-icon>
            <span>{{ sceneList.length }} 个场景</span>
          </div>
          <div class="stat-item">
            <el-icon><UserFilled /></el-icon>
            <span>{{ totalPlayCount }} 次参与</span>
          </div>
          <div class="stat-item">
            <el-icon><Trophy /></el-icon>
            <span>{{ avgSceneScore }} 分平均成绩</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="hover">
      <div class="filter-bar">
        <el-select v-model="selectedType" placeholder="场景类型" clearable class="filter-item">
          <el-option label="全部" value="" />
          <el-option label="电信诈骗" value="电信诈骗" />
          <el-option label="网络诈骗" value="网络诈骗" />
          <el-option label="金融诈骗" value="金融诈骗" />
          <el-option label="校园诈骗" value="校园诈骗" />
        </el-select>
        
        <el-select v-model="selectedDifficulty" placeholder="难度等级" clearable class="filter-item">
          <el-option label="全部" value="" />
          <el-option label="简单" value="1" />
          <el-option label="普通" value="2" />
          <el-option label="困难" value="3" />
        </el-select>
        
        <el-input
          v-model="searchKeyword"
          placeholder="搜索场景名称..."
          prefix-icon="Search"
          clearable
          class="filter-item search-input"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
      </div>
    </el-card>

    <!-- 场景列表 -->
    <el-card class="scene-list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>模拟场景</h3>
          <span class="count-tag">{{ filteredScenes.length }} 个场景</span>
        </div>
      </template>

      <div class="scene-grid">
        <el-card
          v-for="scene in filteredScenes"
          :key="scene.id"
          class="scene-card"
          shadow="hover"
          @click="handleSceneClick(scene.id)"
        >
          <div class="scene-cover">
            <LazyImage 
              :src="scene.coverImage || defaultCover" 
              class="cover" 
              width="100%" 
              height="200px"
            />
            <div class="cover-overlay">
              <el-button type="primary" size="small" class="play-button">
                <el-icon><VideoPlay /></el-icon>
                开始演练
              </el-button>
            </div>
          </div>
          <div class="scene-info">
            <div class="scene-header">
              <h4>{{ scene.sceneName }}</h4>
              <el-tag :type="getDifficultyType(scene.difficulty)" size="small">
                {{ getDifficultyText(scene.difficulty) }}
              </el-tag>
            </div>
            <div class="scene-meta">
              <span class="scene-type">{{ scene.sceneType }}</span>
              <span class="scene-duration">{{ scene.duration || 10 }}分钟</span>
            </div>
            <div class="scene-stats">
              <div class="stat-item">
                <el-icon><User /></el-icon>
                <span>{{ scene.playCount }}人参与</span>
              </div>
              <div class="stat-item">
                <el-icon><TrendCharts /></el-icon>
                <span>{{ scene.avgScore }}分</span>
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span>{{ scene.rating || 4.5 }}分</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-if="filteredScenes.length === 0" 
        description="暂无符合条件的场景"
        :image-size="200"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/utils/request'
import { 
  VideoPlay, VideoCamera, UserFilled, Trophy, 
  Search, User, TrendCharts, Star 
} from '@element-plus/icons-vue'
import LazyImage from '@/components/common/LazyImage.vue'

const router = useRouter()
const defaultCover = 'https://picsum.photos/400/250'

const sceneList = ref<any[]>([])
const selectedType = ref('')
const selectedDifficulty = ref('')
const searchKeyword = ref('')

// 计算筛选后的场景
const filteredScenes = computed(() => {
  return sceneList.value.filter(scene => {
    const matchesType = !selectedType.value || scene.sceneType === selectedType.value
    const matchesDifficulty = !selectedDifficulty.value || scene.difficulty === Number(selectedDifficulty.value)
    const matchesKeyword = !searchKeyword.value || scene.sceneName.includes(searchKeyword.value)
    return matchesType && matchesDifficulty && matchesKeyword
  })
})

// 计算总参与次数
const totalPlayCount = computed(() => {
  return sceneList.value.reduce((sum, scene) => sum + (scene.playCount || 0), 0)
})

// 计算平均成绩
const avgSceneScore = computed(() => {
  if (sceneList.value.length === 0) return 0
  const total = sceneList.value.reduce((sum, scene) => sum + (scene.avgScore || 0), 0)
  return Math.round(total / sceneList.value.length)
})

const getDifficultyText = (level: number) => {
  const levels: Record<number, string> = {
    1: '简单',
    2: '普通',
    3: '困难'
  }
  return levels[level] || '未知'
}

const getDifficultyType = (level: number) => {
  const types: Record<number, string> = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return types[level] || 'info'
}

const handleSceneClick = (sceneId: number) => {
  router.push(`/simulation/${sceneId}`)
}

const handleSearch = () => {
  // 筛选逻辑已在computed中处理
}

onMounted(async () => {
  try {
    const res = await get('/simulation/list')
    sceneList.value = res.data?.records || []
  } catch (e) {
    sceneList.value = [
      { id: 1, sceneName: '冒充客服退款诈骗', sceneType: '电信诈骗', difficulty: 2, playCount: 1256, avgScore: 78, duration: 15, rating: 4.8 },
      { id: 2, sceneName: '冒充公检法诈骗', sceneType: '电信诈骗', difficulty: 3, playCount: 892, avgScore: 65, duration: 20, rating: 4.5 },
      { id: 3, sceneName: '网络兼职刷单诈骗', sceneType: '网络诈骗', difficulty: 1, playCount: 2105, avgScore: 85, duration: 10, rating: 4.9 },
      { id: 4, sceneName: '校园贷诈骗防范', sceneType: '校园诈骗', difficulty: 2, playCount: 1567, avgScore: 72, duration: 12, rating: 4.6 },
      { id: 5, sceneName: '虚假投资诈骗', sceneType: '金融诈骗', difficulty: 3, playCount: 789, avgScore: 68, duration: 18, rating: 4.4 },
      { id: 6, sceneName: '网购诈骗防范', sceneType: '网络诈骗', difficulty: 1, playCount: 1987, avgScore: 82, duration: 8, rating: 4.7 }
    ]
  }
})
</script>

<style scoped>
.simulation-page {
  padding: 0 0 40px;
  min-height: calc(100vh - 160px);
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);
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
  flex-wrap: wrap;
}

.filter-item {
  min-width: 150px;
  flex: 1;
  max-width: 200px;
}

.search-input {
  flex: 2;
  max-width: 300px;
}

/* 场景列表卡片 */
.scene-list-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.scene-list-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0;
  margin: -20px -20px 24px;
  padding: 20px;
  background: var(--el-color-purple-light-9);
  border-radius: 12px 12px 0 0;
}

.card-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #5b21b6;
  margin: 0;
}

.count-tag {
  background: var(--el-color-purple);
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  box-shadow: 0 1px 4px rgba(139, 92, 246, 0.2);
}

/* 场景网格 */
.scene-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

.scene-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color);
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.scene-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  border-color: var(--el-color-purple-light-5);
}

/* 场景封面 */
.scene-cover {
  position: relative;
  overflow: hidden;
  border-radius: 12px 12px 0 0;
  height: 200px;
}

.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.scene-card:hover .cover {
  transform: scale(1.05);
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 20px;
  opacity: 0;
  transition: all 0.3s ease;
}

.scene-card:hover .cover-overlay {
  opacity: 1;
}

.play-button {
  transform: translateY(20px);
  transition: all 0.3s ease;
}

.scene-card:hover .play-button {
  transform: translateY(0);
}

/* 场景信息 */
.scene-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.scene-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.scene-header h4 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: var(--el-text-color-primary);
  flex: 1;
  margin-right: 12px;
  line-height: 1.4;
}

.scene-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.scene-type {
  background: var(--el-color-purple-light-5);
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: 500;
}

.scene-duration {
  display: flex;
  align-items: center;
  gap: 4px;
}

.scene-stats {
  display: flex;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid var(--el-border-color);
}

.scene-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  padding: 0;
  border: none;
  backdrop-filter: none;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.scene-stats .stat-item:hover {
  transform: none;
  background: none;
}

/* 响应式 */
@media (max-width: 768px) {
  .header-stats {
    flex-direction: column;
    gap: 16px;
  }
  
  .stat-item {
    width: 200px;
  }
  
  .scene-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-item {
    max-width: none;
  }
  
  .search-input {
    max-width: none;
  }
  
  .header-content h1 {
    font-size: 32px;
  }
  
  .page-header {
    padding: 40px 16px;
  }
  
  .scene-cover {
    height: 180px;
  }
}
</style>
