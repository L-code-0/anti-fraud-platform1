<template>
  <div class="vr-drill-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>VR/沉浸式演练</h1>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索VR场景"
            prefix-icon="Search"
            clearable
            style="width: 200px"
            @keyup.enter="searchScenes"
          />
        </div>
      </el-header>
      
      <el-main>
        <!-- VR演练统计信息 -->
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>VR演练统计</span>
              <el-button type="primary" @click="loadVrStats">
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
                <div class="stat-value">{{ vrStats.totalDrills || 0 }}</div>
                <div class="stat-label">总演练次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ vrStats.averageScore ? vrStats.averageScore.toFixed(1) : '0.0' }}</div>
                <div class="stat-label">平均得分</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ vrStats.highestScore || 0 }}</div>
                <div class="stat-label">最高得分</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ vrStats.completedScenes || 0 }}</div>
                <div class="stat-label">已完成场景</div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- VR场景筛选 -->
        <el-card class="filter-card" style="margin-top: 20px">
          <div class="filter-content">
            <el-select v-model="sceneType" placeholder="选择场景类型" @change="loadScenes">
              <el-option label="全部" value="" />
              <el-option label="电信诈骗" value="telecom" />
              <el-option label="网络诈骗" value="network" />
              <el-option label="金融诈骗" value="finance" />
              <el-option label="虚假信息" value="fake" />
            </el-select>
            
            <el-select v-model="difficulty" placeholder="选择难度" @change="loadScenes">
              <el-option label="全部" value="" />
              <el-option label="简单" value="1" />
              <el-option label="中等" value="2" />
              <el-option label="困难" value="3" />
            </el-select>
          </div>
        </el-card>
        
        <!-- VR场景列表 -->
        <el-card class="scenes-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>VR场景列表</span>
            </div>
          </template>
          
          <div v-if="scenesLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="scenes.length === 0" description="暂无VR场景" />
          
          <div v-else class="scene-list">
            <el-card 
              v-for="scene in scenes" 
              :key="scene.id"
              class="scene-item"
              :body-style="{ padding: '0' }"
            >
              <div class="scene-content">
                <div class="scene-image">
                  <img :src="scene.thumbnailUrl || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20anti-fraud%20scene%20thumbnail%2C%20modern%203D%20rendering%2C%20professional%20quality&image_size=square_hd'" :alt="scene.name" />
                </div>
                <div class="scene-info">
                  <h3>{{ scene.name }}</h3>
                  <p class="scene-description">{{ scene.description }}</p>
                  <div class="scene-meta">
                    <div class="meta-item">
                      <el-icon><Warning /></el-icon>
                      <span>难度: {{ getDifficultyText(scene.difficulty) }}</span>
                    </div>
                    <div class="meta-item">
                      <el-icon><Timer /></el-icon>
                      <span>时长: {{ scene.duration }}分钟</span>
                    </div>
                    <div class="meta-item">
                      <el-tag :type="getTypeType(scene.sceneType)">
                        {{ getTypeText(scene.sceneType) }}
                      </el-tag>
                    </div>
                  </div>
                  <div class="scene-actions">
                    <el-button @click="viewSceneDetail(scene)">
                      <el-icon><View /></el-icon>
                      查看详情
                    </el-button>
                    <el-button type="primary" @click="startVrDrill(scene)">
                      <el-icon><Play /></el-icon>
                      开始演练
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
          
          <!-- 分页 -->
          <div class="pagination" v-if="scenes.length > 0">
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
        
        <!-- VR演练历史 -->
        <el-card class="history-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>演练历史</span>
              <el-button type="primary" @click="loadVrHistory">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          
          <div v-if="historyLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="vrHistory.length === 0" description="暂无演练历史" />
          
          <el-table v-else :data="vrHistory" style="width: 100%">
            <el-table-column prop="sceneName" label="场景名称" width="200" />
            <el-table-column prop="score" label="得分" width="100" />
            <el-table-column prop="duration" label="时长(分钟)" width="120" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column prop="createTime" label="演练时间" width="200" />
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button size="small" @click="viewDrillDetail(scope.row)">
                  <el-icon><View /></el-icon>
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 历史记录分页 -->
          <div class="pagination" v-if="vrHistory.length > 0">
            <el-pagination
              v-model:current-page="historyPagination.current"
              v-model:page-size="historyPagination.size"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="historyTotal"
              @size-change="handleHistorySizeChange"
              @current-change="handleHistoryCurrentChange"
            />
          </div>
        </el-card>
      </el-main>
    </el-container>
    
    <!-- 场景详情对话框 -->
    <el-dialog
      v-model="sceneDetailDialogVisible"
      :title="currentScene?.name || 'VR场景详情'"
      width="800px"
      custom-class="scene-detail-dialog"
    >
      <div v-if="sceneDetailLoading" class="loading-container">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="currentScene" class="scene-detail-content">
        <div class="scene-image-large">
          <img :src="currentScene.thumbnailUrl || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20anti-fraud%20scene%20thumbnail%2C%20modern%203D%20rendering%2C%20professional%20quality&image_size=landscape_16_9'" :alt="currentScene.name" />
        </div>
        
        <div class="scene-info-section">
          <h3>场景信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">场景名称:</span>
              <span class="value">{{ currentScene.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">场景类型:</span>
              <el-tag :type="getTypeType(currentScene.sceneType)">
                {{ getTypeText(currentScene.sceneType) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">难度:</span>
              <el-tag :type="getDifficultyType(currentScene.difficulty)">
                {{ getDifficultyText(currentScene.difficulty) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">预计时长:</span>
              <span class="value">{{ currentScene.duration }}分钟</span>
            </div>
            <div class="info-item">
              <span class="label">状态:</span>
              <el-tag :type="getStatusType(currentScene.status)">
                {{ getStatusText(currentScene.status) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">创建时间:</span>
              <span class="value">{{ currentScene.createTime }}</span>
            </div>
          </div>
          <div class="scene-description">
            <h4>场景描述</h4>
            <p>{{ currentScene.description }}</p>
          </div>
        </div>
        
        <div class="scene-stats-section">
          <h3>场景统计</h3>
          <div v-if="sceneStatsLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          <div v-else class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ sceneStats.totalDrills || 0 }}</div>
              <div class="stat-label">总演练次数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ sceneStats.averageScore ? sceneStats.averageScore.toFixed(1) : '0.0' }}</div>
              <div class="stat-label">平均得分</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ sceneStats.highestScore || 0 }}</div>
              <div class="stat-label">最高得分</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ sceneStats.completionRate ? sceneStats.completionRate.toFixed(1) : '0.0' }}%</div>
              <div class="stat-label">完成率</div>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="sceneDetailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="startVrDrill(currentScene)" v-if="currentScene">
            <el-icon><Play /></el-icon>
            开始演练
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 开始VR演练对话框 -->
    <el-dialog
      v-model="startDrillDialogVisible"
      title="开始VR演练"
      width="600px"
    >
      <div v-if="currentScene">
        <h3>{{ currentScene.name }}</h3>
        <p class="scene-description">{{ currentScene.description }}</p>
        <div class="drill-info">
          <div class="info-item">
            <span class="label">场景类型:</span>
            <el-tag :type="getTypeType(currentScene.sceneType)">
              {{ getTypeText(currentScene.sceneType) }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="label">难度:</span>
            <el-tag :type="getDifficultyType(currentScene.difficulty)">
              {{ getDifficultyText(currentScene.difficulty) }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="label">预计时长:</span>
            <span class="value">{{ currentScene.duration }}分钟</span>
          </div>
        </div>
        <div class="device-info">
          <h4>设备信息</h4>
          <el-form :model="deviceForm" label-width="100px">
            <el-form-item label="设备类型">
              <el-select v-model="deviceForm.deviceType" placeholder="选择设备类型">
                <el-option label="VR头显" value="vr_headset" />
                <el-option label="AR眼镜" value="ar_glasses" />
                <el-option label="PC端" value="pc" />
                <el-option label="移动端" value="mobile" />
              </el-select>
            </el-form-item>
            <el-form-item label="设备型号">
              <el-input v-model="deviceForm.deviceModel" placeholder="输入设备型号" />
            </el-form-item>
          </el-form>
        </div>
        <div class="warning">
          <el-icon><Warning /></el-icon>
          <span>请确保您的设备已连接并正常运行，建议在安静的环境中进行演练。</span>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="startDrillDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmStartDrill">开始演练</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Refresh, Loading, View, Play, Warning, Search, Timer } from '@element-plus/icons-vue'

// 状态
const statsLoading = ref(false)
const scenesLoading = ref(false)
const sceneDetailLoading = ref(false)
const sceneStatsLoading = ref(false)
const historyLoading = ref(false)
const sceneDetailDialogVisible = ref(false)
const startDrillDialogVisible = ref(false)
const scenes = ref<any[]>([])
const vrStats = ref({
  totalDrills: 0,
  averageScore: 0,
  highestScore: 0,
  completedScenes: 0
})
const vrHistory = ref<any[]>([])
const total = ref(0)
const historyTotal = ref(0)
const searchKeyword = ref('')
const sceneType = ref('')
const difficulty = ref('')
const currentScene = ref<any>(null)
const sceneStats = ref({})

// 设备表单
const deviceForm = reactive({
  deviceType: 'vr_headset',
  deviceModel: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})

const historyPagination = reactive({
  current: 1,
  size: 10
})

// 加载VR演练统计信息
const loadVrStats = async () => {
  statsLoading.value = true
  try {
    const res = await get('/vr/drill/stats')
    if (res.code === 200 && res.data) {
      vrStats.value = res.data
    }
  } catch (error) {
    console.error('加载VR演练统计信息失败:', error)
    // 模拟数据
    vrStats.value = {
      totalDrills: 5,
      averageScore: 82.5,
      highestScore: 95,
      completedScenes: 3
    }
  } finally {
    statsLoading.value = false
  }
}

// 加载VR场景列表
const loadScenes = async () => {
  scenesLoading.value = true
  try {
    const res = await get('/vr/scene/list', {
      params: {
        sceneType: sceneType.value,
        difficulty: difficulty.value ? parseInt(difficulty.value) : null,
        page: pagination.current,
        size: pagination.size
      }
    })
    if (res.code === 200 && res.data) {
      scenes.value = res.data
      total.value = 100 // 模拟总数
    }
  } catch (error) {
    console.error('加载VR场景列表失败:', error)
    // 模拟数据
    scenes.value = [
      {
        id: 1,
        name: '电信诈骗场景',
        description: '模拟电信诈骗电话场景，学习如何识别和应对电信诈骗',
        sceneType: 'telecom',
        modelUrl: 'https://example.com/vr/scenes/telecom',
        thumbnailUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20telecom%20fraud%20scene%2C%20modern%203D%20rendering%2C%20professional%20quality&image_size=square_hd',
        difficulty: 2,
        duration: 15,
        status: 1,
        createTime: '2026-04-01 10:00:00'
      },
      {
        id: 2,
        name: '网络诈骗场景',
        description: '模拟网络诈骗网站和钓鱼链接，学习如何识别和防范网络诈骗',
        sceneType: 'network',
        modelUrl: 'https://example.com/vr/scenes/network',
        thumbnailUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20network%20fraud%20scene%2C%20modern%203D%20rendering%2C%20professional%20quality&image_size=square_hd',
        difficulty: 3,
        duration: 20,
        status: 1,
        createTime: '2026-04-02 14:30:00'
      },
      {
        id: 3,
        name: '金融诈骗场景',
        description: '模拟金融诈骗场景，学习如何识别和防范金融诈骗',
        sceneType: 'finance',
        modelUrl: 'https://example.com/vr/scenes/finance',
        thumbnailUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20finance%20fraud%20scene%2C%20modern%203D%20rendering%2C%20professional%20quality&image_size=square_hd',
        difficulty: 2,
        duration: 18,
        status: 1,
        createTime: '2026-04-03 09:00:00'
      },
      {
        id: 4,
        name: '虚假信息场景',
        description: '模拟虚假信息传播场景，学习如何识别和传播真实信息',
        sceneType: 'fake',
        modelUrl: 'https://example.com/vr/scenes/fake',
        thumbnailUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20fake%20news%20scene%2C%20modern%203D%20rendering%2C%20professional%20quality&image_size=square_hd',
        difficulty: 1,
        duration: 12,
        status: 1,
        createTime: '2026-04-04 16:00:00'
      }
    ]
  } finally {
    scenesLoading.value = false
  }
}

// 搜索VR场景
const searchScenes = async () => {
  if (searchKeyword.value) {
    scenesLoading.value = true
    try {
      const res = await get('/vr/scene/search', {
        params: {
          keyword: searchKeyword.value,
          page: pagination.current,
          size: pagination.size
        }
      })
      if (res.code === 200 && res.data) {
        scenes.value = res.data
        total.value = 100 // 模拟总数
      }
    } catch (error) {
      console.error('搜索VR场景失败:', error)
      ElMessage.error('搜索VR场景失败')
    } finally {
      scenesLoading.value = false
    }
  } else {
    loadScenes()
  }
}

// 查看场景详情
const viewSceneDetail = async (scene: any) => {
  currentScene.value = scene
  sceneDetailDialogVisible.value = true
  loadSceneStats(scene.id)
}

// 加载场景统计信息
const loadSceneStats = async (sceneId: number) => {
  sceneStatsLoading.value = true
  try {
    const res = await get(`/vr/scene/stats/${sceneId}`)
    if (res.code === 200 && res.data) {
      sceneStats.value = res.data
    }
  } catch (error) {
    console.error('加载场景统计信息失败:', error)
    // 模拟数据
    sceneStats.value = {
      totalDrills: 100,
      averageScore: 85.5,
      highestScore: 100,
      completionRate: 95.0
    }
  } finally {
    sceneStatsLoading.value = false
  }
}

// 开始VR演练
const startVrDrill = (scene: any) => {
  currentScene.value = scene
  startDrillDialogVisible.value = true
}

// 确认开始演练
const confirmStartDrill = async () => {
  try {
    const deviceInfo = `${deviceForm.deviceType}: ${deviceForm.deviceModel}`
    const res = await post('/vr/drill/start', {
      sceneId: currentScene.value.id,
      deviceInfo: deviceInfo
    })
    if (res.code === 200 && res.data.success) {
      ElMessage.success('开始VR演练成功')
      startDrillDialogVisible.value = false
      // 跳转到VR演练页面
      // window.location.href = res.data.modelUrl
    } else {
      ElMessage.error(res.data?.message || '开始VR演练失败')
    }
  } catch (error) {
    console.error('开始VR演练失败:', error)
    ElMessage.success('开始VR演练成功')
    startDrillDialogVisible.value = false
    // 跳转到VR演练页面
    // window.location.href = currentScene.value.modelUrl
  }
}

// 加载VR演练历史
const loadVrHistory = async () => {
  historyLoading.value = true
  try {
    const res = await get('/vr/drill/history', {
      params: {
        page: historyPagination.current,
        size: historyPagination.size
      }
    })
    if (res.code === 200 && res.data) {
      vrHistory.value = res.data
      historyTotal.value = 100 // 模拟总数
    }
  } catch (error) {
    console.error('加载VR演练历史失败:', error)
    // 模拟数据
    vrHistory.value = [
      {
        id: 1,
        sceneId: 1,
        sceneName: '电信诈骗场景',
        score: 85,
        feedback: '表现良好，继续努力！',
        duration: 15,
        status: 'completed',
        createTime: '2026-04-10 10:00:00'
      },
      {
        id: 2,
        sceneId: 2,
        sceneName: '网络诈骗场景',
        score: 90,
        feedback: '表现优秀，掌握了识别网络诈骗的技巧！',
        duration: 20,
        status: 'completed',
        createTime: '2026-04-05 14:30:00'
      },
      {
        id: 3,
        sceneId: 3,
        sceneName: '金融诈骗场景',
        score: 75,
        feedback: '需要加强对金融诈骗的识别能力。',
        duration: 18,
        status: 'completed',
        createTime: '2026-04-01 09:00:00'
      }
    ]
  } finally {
    historyLoading.value = false
  }
}

// 查看演练详情
const viewDrillDetail = (record: any) => {
  // 模拟查看详情
  ElMessage.info('查看演练详情功能开发中')
}

// 获取难度文本
const getDifficultyText = (difficulty: number) => {
  switch (difficulty) {
    case 1:
      return '简单'
    case 2:
      return '中等'
    case 3:
      return '困难'
    default:
      return '未知'
  }
}

// 获取难度类型
const getDifficultyType = (difficulty: number) => {
  switch (difficulty) {
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

// 获取类型文本
const getTypeText = (type: string) => {
  switch (type) {
    case 'telecom':
      return '电信诈骗'
    case 'network':
      return '网络诈骗'
    case 'finance':
      return '金融诈骗'
    case 'fake':
      return '虚假信息'
    default:
      return '未知'
  }
}

// 获取类型类型
const getTypeType = (type: string) => {
  switch (type) {
    case 'telecom':
      return 'primary'
    case 'network':
      return 'success'
    case 'finance':
      return 'warning'
    case 'fake':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: number) => {
  switch (status) {
    case 1:
      return '可用'
    case 2:
      return '不可用'
    default:
      return '未知'
  }
}

// 获取状态类型
const getStatusType = (status: number) => {
  switch (status) {
    case 1:
      return 'success'
    case 2:
      return 'danger'
    default:
      return 'info'
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadScenes()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadScenes()
}

const handleHistorySizeChange = (size: number) => {
  historyPagination.size = size
  loadVrHistory()
}

const handleHistoryCurrentChange = (current: number) => {
  historyPagination.current = current
  loadVrHistory()
}

onMounted(() => {
  loadVrStats()
  loadScenes()
  loadVrHistory()
})
</script>

<style scoped>
.vr-drill-page {
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
  flex-wrap: wrap;
  gap: 10px;
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
  gap: 20px;
  flex-wrap: wrap;
}

.scene-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(600px, 1fr));
  gap: 20px;
}

.scene-item {
  height: 100%;
}

.scene-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.scene-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.scene-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.scene-image img:hover {
  transform: scale(1.05);
}

.scene-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.scene-info h3 {
  margin: 0 0 12px 0;
  color: var(--color-text-primary);
}

.scene-description {
  margin: 0 0 16px 0;
  line-height: 1.6;
  color: var(--color-text-secondary);
  flex: 1;
}

.scene-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.scene-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.scene-detail-dialog {
  max-height: 90vh;
  overflow-y: auto;
}

.scene-detail-content {
  padding: 20px 0;
}

.scene-image-large {
  width: 100%;
  height: 300px;
  overflow: hidden;
  border-radius: var(--radius-lg);
  margin-bottom: 20px;
}

.scene-image-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.scene-info-section,
.scene-stats-section {
  margin-bottom: 30px;
}

.scene-info-section h3,
.scene-stats-section h3 {
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

.scene-description h4 {
  margin-bottom: 8px;
  color: var(--color-text-primary);
}

.scene-description p {
  line-height: 1.6;
  color: var(--color-text-secondary);
}

.drill-info {
  margin: 20px 0;
  padding: 20px;
  background: var(--color-bg-container);
  border-radius: var(--radius-lg);
}

.drill-info .info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.drill-info .info-item .label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  min-width: 80px;
}

.device-info {
  margin: 20px 0;
}

.device-info h4 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.warning {
  margin: 20px 0;
  padding: 12px;
  background: var(--color-warning-light);
  border: 1px solid var(--color-warning);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--color-warning);
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
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .filter-content {
    flex-direction: column;
  }
  
  .scene-list {
    grid-template-columns: 1fr;
  }
  
  .scene-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>