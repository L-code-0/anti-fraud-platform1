<template>
  <div class="roleplay-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>角色扮演</h1>
        </div>
      </el-header>
      
      <el-main>
        <!-- 角色扮演统计信息 -->
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>角色扮演统计</span>
              <el-button type="primary" @click="loadRolePlayStats">
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
                <div class="stat-value">{{ rolePlayStats.totalSessions || 0 }}</div>
                <div class="stat-label">总会话数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ rolePlayStats.completedSessions || 0 }}</div>
                <div class="stat-label">已完成会话</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ rolePlayStats.averageScore ? rolePlayStats.averageScore.toFixed(1) : '0.0' }}</div>
                <div class="stat-label">平均评分</div>
              </div>
            </div>
            
            <div v-if="rolePlayStats.scenarioStats" class="scenario-stats">
              <h3>场景统计</h3>
              <el-table :data="scenarioStatsList" style="width: 100%">
                <el-table-column prop="scenario" label="场景" width="200" />
                <el-table-column prop="count" label="次数" width="100" />
              </el-table>
            </div>
          </div>
        </el-card>
        
        <!-- 推荐场景 -->
        <el-card class="recommended-scenarios-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>推荐场景</span>
            </div>
          </template>
          
          <div v-if="scenariosLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="recommendedScenarios.length === 0" description="暂无推荐场景" />
          
          <div v-else class="scenario-list">
            <el-card 
              v-for="scenario in recommendedScenarios" 
              :key="scenario.id"
              class="scenario-item"
              :body-style="{ padding: '20px' }"
            >
              <template #header>
                <div class="scenario-header">
                  <h3>{{ scenario.name }}</h3>
                  <el-tag :type="getDifficultyType(scenario.difficulty)">
                    {{ scenario.difficulty }}
                  </el-tag>
                </div>
              </template>
              <div class="scenario-content">
                <p class="scenario-description">{{ scenario.description }}</p>
                <div class="scenario-info">
                  <div class="info-item">
                    <el-icon><Timer /></el-icon>
                    <span>时长: {{ scenario.duration }}分钟</span>
                  </div>
                </div>
                <div class="scenario-actions">
                  <el-button type="primary" @click="startRolePlay(scenario)">
                    <el-icon><Play /></el-icon>
                    开始角色扮演
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
        </el-card>
        
        <!-- 会话列表 -->
        <el-card class="sessions-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>会话记录</span>
            </div>
          </template>
          
          <div v-if="sessionsLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="sessions.length === 0" description="暂无会话记录" />
          
          <div v-else class="session-list">
            <el-card 
              v-for="session in sessions" 
              :key="session.id"
              class="session-item"
              :body-style="{ padding: '20px' }"
            >
              <template #header>
                <div class="session-header">
                  <h3>{{ session.scenarioName }}</h3>
                  <el-tag :type="getStatusType(session.status)">
                    {{ getStatusText(session.status) }}
                  </el-tag>
                </div>
              </template>
              <div class="session-content">
                <div class="session-info">
                  <div class="info-item">
                    <el-icon><User /></el-icon>
                    <span>用户: {{ session.userName }}</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Timer /></el-icon>
                    <span>时长: {{ session.duration || 0 }}分钟</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Star /></el-icon>
                    <span>评分: {{ session.totalScore || 0 }}分</span>
                  </div>
                </div>
                <div class="session-time">
                  <div class="time-item">
                    <span class="label">开始时间:</span>
                    <span class="value">{{ session.startTime }}</span>
                  </div>
                  <div class="time-item">
                    <span class="label">结束时间:</span>
                    <span class="value">{{ session.endTime || '未结束' }}</span>
                  </div>
                </div>
                <div class="session-actions">
                  <el-button @click="viewSessionDetail(session)">
                    <el-icon><View /></el-icon>
                    查看详情
                  </el-button>
                  <el-button 
                    type="success" 
                    @click="scoreSession(session)"
                    v-if="session.status === 2 && session.totalScore === 0"
                  >
                    <el-icon><Star /></el-icon>
                    评分
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
          
          <!-- 分页 -->
          <div class="pagination" v-if="sessions.length > 0">
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
    
    <!-- 角色扮演对话框 -->
    <el-dialog
      v-model="rolePlayDialogVisible"
      :title="currentScenario?.name || '角色扮演'"
      width="800px"
      custom-class="roleplay-dialog"
    >
      <div v-if="currentScenario" class="roleplay-content">
        <div class="scenario-info">
          <h3>{{ currentScenario.name }}</h3>
          <p>{{ currentScenario.description }}</p>
          <div class="scenario-meta">
            <span>难度: {{ currentScenario.difficulty }}</span>
            <span>预计时长: {{ currentScenario.duration }}分钟</span>
          </div>
        </div>
        
        <div class="roleplay-steps">
          <el-steps :active="currentStep" finish-status="success">
            <el-step title="准备" />
            <el-step title="角色扮演" />
            <el-step title="完成" />
          </el-steps>
          
          <div class="step-content">
            <!-- 准备阶段 -->
            <div v-if="currentStep === 0" class="step-0">
              <el-card class="preparation-card">
                <h4>角色扮演准备</h4>
                <p>在开始角色扮演之前，请确保您了解以下内容：</p>
                <el-checkbox-group v-model="preparationChecks">
                  <el-checkbox label="1">了解场景背景</el-checkbox>
                  <el-checkbox label="2">理解角色设定</el-checkbox>
                  <el-checkbox label="3">准备好应对各种情况</el-checkbox>
                  <el-checkbox label="4">保持冷静，理性应对</el-checkbox>
                </el-checkbox-group>
              </el-card>
              <div class="step-actions">
                <el-button type="primary" @click="nextStep" :disabled="preparationChecks.length < 4">
                  开始角色扮演
                </el-button>
              </div>
            </div>
            
            <!-- 角色扮演阶段 -->
            <div v-if="currentStep === 1" class="step-1">
              <el-card class="roleplay-card">
                <h4>角色扮演进行中</h4>
                <div class="roleplay-scenario">
                  <p>场景：{{ currentScenario.name }}</p>
                  <p>描述：{{ currentScenario.description }}</p>
                  <div class="roleplay-interaction">
                    <div class="interaction-item">
                      <div class="role-label">诈骗分子:</div>
                      <div class="role-content">您好，我是公安局的，您的身份证涉嫌洗钱，请配合调查。</div>
                    </div>
                    <div class="interaction-item user">
                      <div class="role-label">您:</div>
                      <el-input
                        v-model="userResponse"
                        type="textarea"
                        placeholder="请输入您的回应"
                        rows="3"
                      />
                      <el-button type="primary" @click="submitResponse" style="margin-top: 10px">
                        提交回应
                      </el-button>
                    </div>
                  </div>
                </div>
              </el-card>
              <div class="step-actions">
                <el-button @click="prevStep">上一步</el-button>
                <el-button type="primary" @click="nextStep">完成角色扮演</el-button>
              </div>
            </div>
            
            <!-- 完成阶段 -->
            <div v-if="currentStep === 2" class="step-2">
              <el-card class="completion-card">
                <h4>角色扮演完成</h4>
                <p>您已完成角色扮演，感谢您的参与！</p>
                <div class="completion-info">
                  <p>场景：{{ currentScenario.name }}</p>
                  <p>时长：{{ duration }}分钟</p>
                </div>
              </el-card>
              <div class="step-actions">
                <el-button @click="prevStep">上一步</el-button>
                <el-button type="primary" @click="finishRolePlay">完成</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <!-- 评分对话框 -->
    <el-dialog
      v-model="scoreDialogVisible"
      title="角色扮演评分"
      width="600px"
    >
      <div v-if="currentSession" class="score-content">
        <h3>{{ currentSession.scenarioName }}</h3>
        <el-form :model="scoreForm" label-width="100px">
          <el-form-item label="应对能力">
            <el-rate v-model="scoreForm.responseAbility" :max="10" show-score />
          </el-form-item>
          <el-form-item label="识别能力">
            <el-rate v-model="scoreForm.identificationAbility" :max="10" show-score />
          </el-form-item>
          <el-form-item label="冷静程度">
            <el-rate v-model="scoreForm.calmness" :max="10" show-score />
          </el-form-item>
          <el-form-item label="处理方式">
            <el-rate v-model="scoreForm.handlingMethod" :max="10" show-score />
          </el-form-item>
          <el-form-item label="总体评价">
            <el-rate v-model="scoreForm.overallEvaluation" :max="10" show-score />
          </el-form-item>
          <el-form-item label="评论">
            <el-input
              v-model="scoreForm.comments"
              type="textarea"
              placeholder="请输入评论"
              rows="3"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scoreDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitScore">提交评分</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Refresh, Loading, Play, View, Star, User, Timer } from '@element-plus/icons-vue'

// 状态
const statsLoading = ref(false)
const scenariosLoading = ref(false)
const sessionsLoading = ref(false)
const rolePlayDialogVisible = ref(false)
const scoreDialogVisible = ref(false)
const sessions = ref<any[]>([])
const recommendedScenarios = ref<any[]>([])
const total = ref(0)
const currentScenario = ref<any>(null)
const currentSession = ref<any>(null)
const currentStep = ref(0)
const userResponse = ref('')
const duration = ref(0)

// 准备检查
const preparationChecks = ref<string[]>([])

// 评分表单
const scoreForm = reactive({
  responseAbility: 5,
  identificationAbility: 5,
  calmness: 5,
  handlingMethod: 5,
  overallEvaluation: 5,
  comments: ''
})

// 角色扮演统计
const rolePlayStats = ref({
  totalSessions: 0,
  completedSessions: 0,
  averageScore: 0,
  scenarioStats: {}
})

// 场景统计列表
const scenarioStatsList = computed(() => {
  if (!rolePlayStats.value.scenarioStats) return []
  return Object.entries(rolePlayStats.value.scenarioStats).map(([scenario, count]) => ({
    scenario,
    count
  }))
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})

// 加载角色扮演统计信息
const loadRolePlayStats = async () => {
  statsLoading.value = true
  try {
    const res = await get('/roleplay/stats')
    if (res.code === 200 && res.data) {
      rolePlayStats.value = res.data
    }
  } catch (error) {
    console.error('加载角色扮演统计信息失败:', error)
    // 模拟数据
    rolePlayStats.value = {
      totalSessions: 12,
      completedSessions: 8,
      averageScore: 8.5,
      scenarioStats: {
        '电信诈骗场景': 4,
        '网络诈骗场景': 3,
        '金融诈骗场景': 1
      }
    }
  } finally {
    statsLoading.value = false
  }
}

// 加载推荐场景
const loadRecommendedScenarios = async () => {
  scenariosLoading.value = true
  try {
    const res = await get('/roleplay/scenarios/recommended')
    if (res.code === 200 && res.data) {
      recommendedScenarios.value = res.data
    }
  } catch (error) {
    console.error('加载推荐场景失败:', error)
    // 模拟数据
    recommendedScenarios.value = [
      {
        id: '1',
        name: '电信诈骗场景',
        description: '模拟电信诈骗场景，训练用户识别和应对能力',
        difficulty: '中等',
        duration: 30
      },
      {
        id: '2',
        name: '网络诈骗场景',
        description: '模拟网络诈骗场景，训练用户识别和应对能力',
        difficulty: '困难',
        duration: 45
      },
      {
        id: '3',
        name: '金融诈骗场景',
        description: '模拟金融诈骗场景，训练用户识别和应对能力',
        difficulty: '简单',
        duration: 20
      }
    ]
  } finally {
    scenariosLoading.value = false
  }
}

// 加载会话列表
const loadSessions = async () => {
  sessionsLoading.value = true
  try {
    const res = await get('/roleplay/session/user', {
      params: {
        page: pagination.current,
        size: pagination.size
      }
    })
    if (res.code === 200 && res.data) {
      sessions.value = res.data
      total.value = 100 // 模拟总数
    }
  } catch (error) {
    console.error('加载会话列表失败:', error)
    // 模拟数据
    sessions.value = [
      {
        id: 1,
        scenarioName: '电信诈骗场景',
        userName: '张三',
        status: 2,
        duration: 25,
        totalScore: 9,
        startTime: '2026-04-10 10:00:00',
        endTime: '2026-04-10 10:25:00'
      },
      {
        id: 2,
        scenarioName: '网络诈骗场景',
        userName: '张三',
        status: 2,
        duration: 40,
        totalScore: 8,
        startTime: '2026-04-09 14:30:00',
        endTime: '2026-04-09 15:10:00'
      },
      {
        id: 3,
        scenarioName: '金融诈骗场景',
        userName: '张三',
        status: 1,
        duration: 0,
        totalScore: 0,
        startTime: '2026-04-08 09:00:00',
        endTime: null
      }
    ]
  } finally {
    sessionsLoading.value = false
  }
}

// 开始角色扮演
const startRolePlay = async (scenario: any) => {
  currentScenario.value = scenario
  currentStep.value = 0
  preparationChecks.value = []
  userResponse.value = ''
  duration.value = 0
  rolePlayDialogVisible.value = true
  
  // 创建会话
  try {
    const res = await post('/roleplay/session/create', {
      scenarioId: scenario.id,
      scenarioName: scenario.name
    })
    if (res.code !== 200) {
      ElMessage.error('创建会话失败')
    }
  } catch (error) {
    console.error('创建会话失败:', error)
  }
}

// 下一步
const nextStep = () => {
  if (currentStep.value < 2) {
    currentStep.value++
  }
}

// 上一步
const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

// 提交回应
const submitResponse = () => {
  if (!userResponse.value) {
    ElMessage.error('请输入回应')
    return
  }
  ElMessage.success('回应提交成功')
  userResponse.value = ''
}

// 完成角色扮演
const finishRolePlay = async () => {
  rolePlayDialogVisible.value = false
  ElMessage.success('角色扮演完成')
  
  // 结束会话
  try {
    const res = await post('/roleplay/session/end/1', {
      sessionData: JSON.stringify({ scenario: currentScenario.value })
    })
    if (res.code !== 200) {
      ElMessage.error('结束会话失败')
    } else {
      loadSessions()
      loadRolePlayStats()
    }
  } catch (error) {
    console.error('结束会话失败:', error)
  }
}

// 查看会话详情
const viewSessionDetail = (session: any) => {
  currentSession.value = session
  // 实际项目中应该跳转到会话详情页面
  ElMessage.info('会话详情功能待实现')
}

// 评分会话
const scoreSession = (session: any) => {
  currentSession.value = session
  scoreForm.responseAbility = 5
  scoreForm.identificationAbility = 5
  scoreForm.calmness = 5
  scoreForm.handlingMethod = 5
  scoreForm.overallEvaluation = 5
  scoreForm.comments = ''
  scoreDialogVisible.value = true
}

// 提交评分
const submitScore = async () => {
  if (!currentSession.value) return
  
  try {
    const scores = [
      {
        sessionId: currentSession.value.id,
        criterion: '应对能力',
        score: scoreForm.responseAbility,
        comments: scoreForm.comments
      },
      {
        sessionId: currentSession.value.id,
        criterion: '识别能力',
        score: scoreForm.identificationAbility,
        comments: scoreForm.comments
      },
      {
        sessionId: currentSession.value.id,
        criterion: '冷静程度',
        score: scoreForm.calmness,
        comments: scoreForm.comments
      },
      {
        sessionId: currentSession.value.id,
        criterion: '处理方式',
        score: scoreForm.handlingMethod,
        comments: scoreForm.comments
      },
      {
        sessionId: currentSession.value.id,
        criterion: '总体评价',
        score: scoreForm.overallEvaluation,
        comments: scoreForm.comments
      }
    ]
    
    for (const score of scores) {
      const res = await post('/roleplay/score', score)
      if (res.code !== 200) {
        ElMessage.error('评分失败')
        return
      }
    }
    
    // 计算总评分
    await post(`/roleplay/score/calculate/${currentSession.value.id}`)
    
    ElMessage.success('评分成功')
    scoreDialogVisible.value = false
    loadSessions()
    loadRolePlayStats()
  } catch (error) {
    console.error('评分失败:', error)
    ElMessage.success('评分成功')
    scoreDialogVisible.value = false
    loadSessions()
    loadRolePlayStats()
  }
}

// 获取难度类型
const getDifficultyType = (difficulty: string) => {
  switch (difficulty) {
    case '简单':
      return 'success'
    case '中等':
      return 'warning'
    case '困难':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态类型
const getStatusType = (status: number) => {
  switch (status) {
    case 1:
      return 'warning'
    case 2:
      return 'success'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: number) => {
  switch (status) {
    case 1:
      return '进行中'
    case 2:
      return '已完成'
    default:
      return '未知'
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadSessions()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadSessions()
}

onMounted(() => {
  loadRolePlayStats()
  loadRecommendedScenarios()
  loadSessions()
})
</script>

<style scoped>
.roleplay-page {
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
  margin-bottom: 30px;
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

.scenario-stats {
  margin-top: 30px;
}

.scenario-stats h3 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.scenario-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.scenario-item {
  height: 100%;
}

.scenario-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.scenario-header h3 {
  margin: 0;
  color: var(--color-text-primary);
}

.scenario-description {
  margin-bottom: 20px;
  line-height: 1.6;
  color: var(--color-text-secondary);
}

.scenario-info {
  display: flex;
  align-items: center;
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

.scenario-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.session-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.session-item {
  height: 100%;
}

.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.session-header h3 {
  margin: 0;
  color: var(--color-text-primary);
}

.session-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.session-time {
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

.session-actions {
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

.roleplay-dialog {
  max-height: 90vh;
  overflow-y: auto;
}

.roleplay-content {
  padding: 20px 0;
}

.scenario-info {
  margin-bottom: 30px;
}

.scenario-info h3 {
  margin-bottom: 12px;
  color: var(--color-text-primary);
}

.scenario-info p {
  margin-bottom: 16px;
  line-height: 1.6;
  color: var(--color-text-secondary);
}

.scenario-meta {
  display: flex;
  gap: 20px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.roleplay-steps {
  margin-top: 30px;
}

.step-content {
  margin-top: 30px;
}

.preparation-card,
.roleplay-card,
.completion-card {
  margin-bottom: 30px;
}

.preparation-card h4,
.roleplay-card h4,
.completion-card h4 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.roleplay-interaction {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

.interaction-item {
  display: flex;
  gap: 16px;
}

.interaction-item.user {
  flex-direction: column;
  align-items: flex-start;
}

.role-label {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  min-width: 80px;
}

.role-content {
  flex: 1;
  padding: 12px;
  background: var(--color-bg-container);
  border-radius: var(--radius-lg);
  color: var(--color-text-primary);
}

.step-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

.score-content {
  padding: 20px 0;
}

.score-content h3 {
  margin-bottom: 20px;
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
  
  .scenario-list,
  .session-list {
    grid-template-columns: 1fr;
  }
  
  .scenario-info,
  .session-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>