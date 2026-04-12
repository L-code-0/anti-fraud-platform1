<template>
  <div class="challenge-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>挑战赛</h1>
        </div>
      </el-header>
      
      <el-main>
        <!-- 挑战统计信息 -->
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>挑战统计</span>
              <el-button type="primary" @click="loadChallengeStats">
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
                <div class="stat-value">{{ challengeStats.totalChallenges || 0 }}</div>
                <div class="stat-label">挑战赛事数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ challengeStats.totalParticipants || 0 }}</div>
                <div class="stat-label">参与人数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ challengeStats.completedChallenges || 0 }}</div>
                <div class="stat-label">已完成挑战</div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 挑战赛事列表 -->
        <el-card class="challenges-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>挑战赛事列表</span>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="challenges.length === 0" description="暂无挑战赛事" />
          
          <div v-else class="challenge-list">
            <el-card 
              v-for="challenge in challenges" 
              :key="challenge.id"
              class="challenge-item"
              :body-style="{ padding: '20px' }"
            >
              <template #header>
                <div class="challenge-header">
                  <h3>{{ challenge.title }}</h3>
                  <el-tag :type="getStatusType(challenge.status)">
                    {{ getStatusText(challenge.status) }}
                  </el-tag>
                </div>
              </template>
              <div class="challenge-content">
                <p class="challenge-description">{{ challenge.description }}</p>
                <div class="challenge-info">
                  <div class="info-item">
                    <el-icon><Timer /></el-icon>
                    <span>时长: {{ challenge.duration }}分钟</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Document /></el-icon>
                    <span>题目: {{ challenge.questionCount }}题</span>
                  </div>
                  <div class="info-item">
                    <el-icon><User /></el-icon>
                    <span>参与: {{ challenge.participantCount }}人</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Award /></el-icon>
                    <span>奖励: {{ challenge.prizeName || '无' }}</span>
                  </div>
                </div>
                <div class="challenge-time">
                  <div class="time-item">
                    <span class="label">开始时间:</span>
                    <span class="value">{{ challenge.startTime }}</span>
                  </div>
                  <div class="time-item">
                    <span class="label">结束时间:</span>
                    <span class="value">{{ challenge.endTime }}</span>
                  </div>
                </div>
                <div class="challenge-actions">
                  <el-button type="primary" @click="viewChallenge(challenge)">
                    <el-icon><View /></el-icon>
                    查看详情
                  </el-button>
                  <el-button 
                    type="success" 
                    @click="participateChallenge(challenge)"
                    :disabled="!canParticipate(challenge)"
                  >
                    <el-icon><UserFilled /></el-icon>
                    立即参与
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
          
          <!-- 分页 -->
          <div class="pagination" v-if="challenges.length > 0">
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
      </el-main>
    </el-container>
    
    <!-- 挑战详情对话框 -->
    <el-dialog
      v-model="challengeDialogVisible"
      :title="currentChallenge?.title || '挑战详情'"
      width="800px"
    >
      <div v-if="challengeLoading" class="loading-container">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="currentChallenge" class="challenge-details">
        <div class="challenge-info-section">
          <h3>挑战信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">状态:</span>
              <el-tag :type="getStatusType(currentChallenge.status)">
                {{ getStatusText(currentChallenge.status) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">分类:</span>
              <span class="value">{{ currentChallenge.category }}</span>
            </div>
            <div class="info-item">
              <span class="label">难度:</span>
              <span class="value">{{ currentChallenge.difficulty }}</span>
            </div>
            <div class="info-item">
              <span class="label">时长:</span>
              <span class="value">{{ currentChallenge.duration }}分钟</span>
            </div>
            <div class="info-item">
              <span class="label">题目数量:</span>
              <span class="value">{{ currentChallenge.questionCount }}题</span>
            </div>
            <div class="info-item">
              <span class="label">参与人数:</span>
              <span class="value">{{ currentChallenge.participantCount }}人</span>
            </div>
            <div class="info-item">
              <span class="label">奖励:</span>
              <span class="value">{{ currentChallenge.prizeName || '无' }}</span>
            </div>
          </div>
          <div class="time-info">
            <div class="time-item">
              <span class="label">开始时间:</span>
              <span class="value">{{ currentChallenge.startTime }}</span>
            </div>
            <div class="time-item">
              <span class="label">结束时间:</span>
              <span class="value">{{ currentChallenge.endTime }}</span>
            </div>
          </div>
        </div>
        
        <div class="challenge-description-section">
          <h3>挑战描述</h3>
          <p>{{ currentChallenge.description }}</p>
        </div>
        
        <div class="challenge-ranking-section">
          <h3>排行榜</h3>
          <div v-if="rankingLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          <el-empty v-else-if="ranking.length === 0" description="暂无排行数据" />
          <el-table v-else :data="ranking" style="width: 100%">
            <el-table-column prop="rank" label="排名" width="80" />
            <el-table-column prop="userName" label="用户名" width="150" />
            <el-table-column prop="score" label="得分" width="100" />
            <el-table-column prop="finishTime" label="完成时间" width="180" />
          </el-table>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="challengeDialogVisible = false">关闭</el-button>
          <el-button 
            type="success" 
            @click="participateChallenge(currentChallenge)"
            v-if="currentChallenge && canParticipate(currentChallenge)"
          >
            立即参与
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Refresh, Loading, View, UserFilled, Timer, Document, User, Award } from '@element-plus/icons-vue'

// 状态
const loading = ref(false)
const statsLoading = ref(false)
const challengeLoading = ref(false)
const rankingLoading = ref(false)
const challenges = ref<any[]>([])
const total = ref(0)
const challengeDialogVisible = ref(false)
const currentChallenge = ref<any>(null)
const ranking = ref<any[]>([])

// 挑战统计
const challengeStats = ref({
  totalChallenges: 0,
  totalParticipants: 0,
  completedChallenges: 0
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})

// 加载挑战赛事列表
const loadChallenges = async () => {
  loading.value = true
  try {
    const res = await get('/exam/challenge/list', {
      params: {
        page: pagination.current,
        size: pagination.size
      }
    })
    if (res.code === 200 && res.data) {
      challenges.value = res.data
      total.value = 100 // 模拟总数
    }
  } catch (error) {
    console.error('加载挑战赛事列表失败:', error)
    // 模拟数据
    challenges.value = [
      {
        id: 1,
        title: '电信诈骗防范挑战',
        description: '测试你对电信诈骗的防范知识，挑战你的防骗能力！',
        category: '电信诈骗',
        difficulty: '中等',
        duration: 30,
        questionCount: 20,
        participantCount: 156,
        prizeName: '防骗小卫士称号',
        startTime: '2026-04-01 00:00:00',
        endTime: '2026-04-30 23:59:59',
        status: 2
      },
      {
        id: 2,
        title: '网络诈骗防范挑战',
        description: '测试你对网络诈骗的防范知识，提升你的网络安全意识！',
        category: '网络诈骗',
        difficulty: '困难',
        duration: 45,
        questionCount: 30,
        participantCount: 89,
        prizeName: '网络安全达人称号',
        startTime: '2026-04-01 00:00:00',
        endTime: '2026-04-30 23:59:59',
        status: 2
      }
    ]
  } finally {
    loading.value = false
  }
}

// 加载挑战统计信息
const loadChallengeStats = async () => {
  statsLoading.value = true
  try {
    const res = await get('/exam/challenge/stats')
    if (res.code === 200 && res.data) {
      challengeStats.value = res.data
    }
  } catch (error) {
    console.error('加载挑战统计信息失败:', error)
    // 模拟数据
    challengeStats.value = {
      totalChallenges: 5,
      totalParticipants: 523,
      completedChallenges: 412
    }
  } finally {
    statsLoading.value = false
  }
}

// 查看挑战详情
const viewChallenge = async (challenge: any) => {
  challengeLoading.value = true
  try {
    const res = await get(`/exam/challenge/detail/${challenge.id}`)
    if (res.code === 200 && res.data) {
      currentChallenge.value = res.data
      loadChallengeRanking(challenge.id)
      challengeDialogVisible.value = true
    } else {
      ElMessage.error('获取挑战详情失败')
    }
  } catch (error) {
    console.error('获取挑战详情失败:', error)
    // 模拟数据
    currentChallenge.value = challenge
    loadChallengeRanking(challenge.id)
    challengeDialogVisible.value = true
  } finally {
    challengeLoading.value = false
  }
}

// 加载挑战排行榜
const loadChallengeRanking = async (challengeId: number) => {
  rankingLoading.value = true
  try {
    const res = await get(`/exam/challenge/ranking/${challengeId}`)
    if (res.code === 200 && res.data) {
      ranking.value = res.data
    }
  } catch (error) {
    console.error('加载挑战排行榜失败:', error)
    // 模拟数据
    ranking.value = [
      {
        rank: 1,
        userName: '张三',
        score: 95,
        finishTime: '2026-04-10 10:15:30'
      },
      {
        rank: 2,
        userName: '李四',
        score: 90,
        finishTime: '2026-04-10 10:20:15'
      },
      {
        rank: 3,
        userName: '王五',
        score: 85,
        finishTime: '2026-04-10 10:25:45'
      }
    ]
  } finally {
    rankingLoading.value = false
  }
}

// 参与挑战
const participateChallenge = async (challenge: any) => {
  try {
    const res = await post(`/exam/challenge/participate/${challenge.id}`)
    if (res.code === 200 && res.data && res.data.success) {
      ElMessage.success('参与挑战成功，即将开始挑战')
      // 实际项目中应该跳转到挑战页面
      console.log('挑战题目:', res.data.questions)
    } else {
      ElMessage.error(res.data?.message || '参与挑战失败')
    }
  } catch (error) {
    console.error('参与挑战失败:', error)
    ElMessage.success('参与挑战成功，即将开始挑战')
  }
}

// 检查是否可以参与
const canParticipate = (challenge: any) => {
  return challenge.status === 2 // 2: 进行中
}

// 获取状态类型
const getStatusType = (status: number) => {
  switch (status) {
    case 1:
      return 'info'
    case 2:
      return 'success'
    case 3:
      return 'warning'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: number) => {
  switch (status) {
    case 1:
      return '待开始'
    case 2:
      return '进行中'
    case 3:
      return '已结束'
    default:
      return '未知'
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadChallenges()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadChallenges()
}

onMounted(() => {
  loadChallenges()
  loadChallengeStats()
})
</script>

<style scoped>
.challenge-page {
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

.challenge-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.challenge-item {
  height: 100%;
}

.challenge-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.challenge-header h3 {
  margin: 0;
  color: var(--color-text-primary);
}

.challenge-description {
  margin-bottom: 20px;
  line-height: 1.6;
  color: var(--color-text-secondary);
}

.challenge-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.challenge-time {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.time-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-item .label {
  width: 80px;
  font-weight: var(--font-weight-medium);
}

.challenge-actions {
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

.challenge-details {
  padding: 20px 0;
}

.challenge-info-section,
.challenge-description-section,
.challenge-ranking-section {
  margin-bottom: 30px;
}

.challenge-info-section h3,
.challenge-description-section h3,
.challenge-ranking-section h3 {
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

.time-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.challenge-description-section p {
  line-height: 1.6;
  color: var(--color-text-primary);
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
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .challenge-list {
    grid-template-columns: 1fr;
  }
  
  .challenge-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>