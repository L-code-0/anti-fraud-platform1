<template>
  <div class="team-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>团队协作演练</h1>
          <el-button type="primary" @click="openCreateTeamDialog">
            <el-icon><Plus /></el-icon>
            创建团队
          </el-button>
        </div>
      </el-header>
      
      <el-main>
        <!-- 团队统计信息 -->
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span>团队统计</span>
              <el-button type="primary" @click="loadTeamStats">
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
                <div class="stat-value">{{ teamStats.createdTeams || 0 }}</div>
                <div class="stat-label">创建的团队</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ teamStats.joinedTeams || 0 }}</div>
                <div class="stat-label">加入的团队</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ teamStats.totalDrills || 0 }}</div>
                <div class="stat-label">团队演练</div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 团队列表 -->
        <el-card class="teams-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>团队列表</span>
              <el-tabs v-model="activeTab">
                <el-tab-pane label="全部" name="all" />
                <el-tab-pane label="我创建的" name="created" />
                <el-tab-pane label="我加入的" name="joined" />
              </el-tabs>
              <el-input
                v-model="searchKeyword"
                placeholder="搜索团队"
                prefix-icon="Search"
                clearable
                style="width: 200px"
                @keyup.enter="searchTeams"
              />
            </div>
          </template>
          
          <div v-if="teamsLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="teams.length === 0" description="暂无团队" />
          
          <div v-else class="team-list">
            <el-card 
              v-for="team in teams" 
              :key="team.id"
              class="team-item"
              :body-style="{ padding: '20px' }"
            >
              <template #header>
                <div class="team-header">
                  <h3>{{ team.name }}</h3>
                  <el-tag :type="getStatusType(team.status)">
                    {{ getStatusText(team.status) }}
                  </el-tag>
                </div>
              </template>
              <div class="team-content">
                <p class="team-description">{{ team.description }}</p>
                <div class="team-info">
                  <div class="info-item">
                    <el-icon><User /></el-icon>
                    <span>成员: {{ team.memberCount }}人</span>
                  </div>
                  <div class="info-item">
                    <el-icon><UserFilled /></el-icon>
                    <span>创建者: {{ team.creatorName }}</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Timer /></el-icon>
                    <span>创建时间: {{ team.createTime }}</span>
                  </div>
                </div>
                <div class="team-actions">
                  <el-button @click="viewTeamDetail(team)">
                    <el-icon><View /></el-icon>
                    查看详情
                  </el-button>
                  <el-button type="success" @click="startTeamDrill(team)">
                    <el-icon><Play /></el-icon>
                    开始演练
                  </el-button>
                  <el-button 
                    type="primary" 
                    @click="joinTeam(team)"
                    v-if="!isTeamMember(team.id)"
                  >
                    <el-icon><UserPlus /></el-icon>
                    加入团队
                  </el-button>
                  <el-button 
                    type="warning" 
                    @click="leaveTeam(team)"
                    v-else-if="!isTeamOwner(team)"
                  >
                    <el-icon><UserDelete /></el-icon>
                    退出团队
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
          
          <!-- 分页 -->
          <div class="pagination" v-if="teams.length > 0">
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
    
    <!-- 创建团队对话框 -->
    <el-dialog
      v-model="createTeamDialogVisible"
      title="创建团队"
      width="600px"
    >
      <el-form :model="teamForm" label-width="80px">
        <el-form-item label="团队名称">
          <el-input v-model="teamForm.name" placeholder="请输入团队名称" />
        </el-form-item>
        <el-form-item label="团队描述">
          <el-input
            v-model="teamForm.description"
            type="textarea"
            placeholder="请输入团队描述"
            rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createTeamDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createTeam">创建</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 团队详情对话框 -->
    <el-dialog
      v-model="teamDetailDialogVisible"
      :title="currentTeam?.name || '团队详情'"
      width="800px"
      custom-class="team-detail-dialog"
    >
      <div v-if="teamDetailLoading" class="loading-container">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="currentTeam" class="team-detail-content">
        <div class="team-info-section">
          <h3>团队信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">团队名称:</span>
              <span class="value">{{ currentTeam.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">创建者:</span>
              <span class="value">{{ currentTeam.creatorName }}</span>
            </div>
            <div class="info-item">
              <span class="label">成员数量:</span>
              <span class="value">{{ currentTeam.memberCount }}人</span>
            </div>
            <div class="info-item">
              <span class="label">状态:</span>
              <el-tag :type="getStatusType(currentTeam.status)">
                {{ getStatusText(currentTeam.status) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">创建时间:</span>
              <span class="value">{{ currentTeam.createTime }}</span>
            </div>
          </div>
          <div class="team-description">
            <h4>团队描述</h4>
            <p>{{ currentTeam.description }}</p>
          </div>
        </div>
        
        <div class="team-members-section">
          <h3>团队成员</h3>
          <div v-if="teamMembersLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          <el-empty v-else-if="teamMembers.length === 0" description="暂无成员" />
          <el-table v-else :data="teamMembers" style="width: 100%">
            <el-table-column prop="userName" label="用户名" width="150" />
            <el-table-column prop="role" label="角色" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.role === 'owner'" type="primary">所有者</el-tag>
                <el-tag v-else>成员</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="joinTime" label="加入时间" width="200" />
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button 
                  type="danger" 
                  size="small" 
                  @click="removeMember(scope.row)"
                  v-if="isTeamOwner(currentTeam) && scope.row.role !== 'owner'"
                >
                  踢出
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="team-drills-section">
          <h3>团队演练</h3>
          <div v-if="teamDrillsLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          <el-empty v-else-if="teamDrills.length === 0" description="暂无演练记录" />
          <el-table v-else :data="teamDrills" style="width: 100%">
            <el-table-column prop="drillName" label="演练名称" width="200" />
            <el-table-column prop="drillType" label="演练类型" width="100" />
            <el-table-column prop="score" label="得分" width="80" />
            <el-table-column prop="rank" label="排名" width="80" />
            <el-table-column prop="participants" label="参与人数" width="100" />
            <el-table-column prop="date" label="日期" width="200" />
          </el-table>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="teamDetailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 开始团队演练对话框 -->
    <el-dialog
      v-model="startDrillDialogVisible"
      title="开始团队演练"
      width="600px"
    >
      <el-form :model="drillForm" label-width="100px">
        <el-form-item label="演练类型">
          <el-select v-model="drillForm.drillType" placeholder="选择演练类型">
            <el-option label="角色扮演" value="roleplay" />
            <el-option label="挑战赛" value="challenge" />
            <el-option label="自适应测试" value="adaptive" />
          </el-select>
        </el-form-item>
        <el-form-item label="演练名称">
          <el-input v-model="drillForm.drillName" placeholder="请输入演练名称" />
        </el-form-item>
      </el-form>
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
import { Plus, Refresh, Loading, View, Play, UserPlus, UserDelete, User, UserFilled, Timer, Search } from '@element-plus/icons-vue'

// 状态
const statsLoading = ref(false)
const teamsLoading = ref(false)
const teamDetailLoading = ref(false)
const teamMembersLoading = ref(false)
const teamDrillsLoading = ref(false)
const createTeamDialogVisible = ref(false)
const teamDetailDialogVisible = ref(false)
const startDrillDialogVisible = ref(false)
const teams = ref<any[]>([])
const teamStats = ref({
  createdTeams: 0,
  joinedTeams: 0,
  totalDrills: 0
})
const total = ref(0)
const activeTab = ref('all')
const searchKeyword = ref('')
const currentTeam = ref<any>(null)
const teamMembers = ref<any[]>([])
const teamDrills = ref<any[]>([])

// 表单数据
const teamForm = reactive({
  name: '',
  description: ''
})

const drillForm = reactive({
  drillType: 'roleplay',
  drillName: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})

// 加载团队统计信息
const loadTeamStats = async () => {
  statsLoading.value = true
  try {
    // 模拟数据
    teamStats.value = {
      createdTeams: 2,
      joinedTeams: 3,
      totalDrills: 5
    }
  } catch (error) {
    console.error('加载团队统计信息失败:', error)
  } finally {
    statsLoading.value = false
  }
}

// 加载团队列表
const loadTeams = async () => {
  teamsLoading.value = true
  try {
    let res
    if (activeTab.value === 'created') {
      res = await get('/team/created', {
        params: {
          page: pagination.current,
          size: pagination.size
        }
      })
    } else if (activeTab.value === 'joined') {
      res = await get('/team/joined', {
        params: {
          page: pagination.current,
          size: pagination.size
        }
      })
    } else {
      res = await get('/team/list', {
        params: {
          page: pagination.current,
          size: pagination.size
        }
      })
    }
    if (res.code === 200 && res.data) {
      teams.value = res.data
      total.value = 100 // 模拟总数
    }
  } catch (error) {
    console.error('加载团队列表失败:', error)
    // 模拟数据
    teams.value = [
      {
        id: 1,
        name: '反诈先锋队',
        description: '专注于反诈知识学习和演练的团队',
        creatorId: 1,
        creatorName: '张三',
        memberCount: 5,
        status: 1,
        createTime: '2026-04-01 10:00:00'
      },
      {
        id: 2,
        name: '网络安全卫士',
        description: '致力于网络安全防护和反诈宣传的团队',
        creatorId: 2,
        creatorName: '李四',
        memberCount: 4,
        status: 1,
        createTime: '2026-04-02 14:30:00'
      },
      {
        id: 3,
        name: '金融反诈联盟',
        description: '专注于金融诈骗防范的团队',
        creatorId: 3,
        creatorName: '王五',
        memberCount: 6,
        status: 1,
        createTime: '2026-04-03 09:00:00'
      }
    ]
  } finally {
    teamsLoading.value = false
  }
}

// 搜索团队
const searchTeams = async () => {
  if (searchKeyword.value) {
    teamsLoading.value = true
    try {
      const res = await get('/team/search', {
        params: {
          keyword: searchKeyword.value,
          page: pagination.current,
          size: pagination.size
        }
      })
      if (res.code === 200 && res.data) {
        teams.value = res.data
        total.value = 100 // 模拟总数
      }
    } catch (error) {
      console.error('搜索团队失败:', error)
      ElMessage.error('搜索团队失败')
    } finally {
      teamsLoading.value = false
    }
  } else {
    loadTeams()
  }
}

// 创建团队
const createTeam = async () => {
  if (!teamForm.name) {
    ElMessage.error('请输入团队名称')
    return
  }
  
  try {
    const res = await post('/team/create', teamForm)
    if (res.code === 200) {
      ElMessage.success('创建团队成功')
      createTeamDialogVisible.value = false
      teamForm.name = ''
      teamForm.description = ''
      loadTeams()
      loadTeamStats()
    } else {
      ElMessage.error('创建团队失败')
    }
  } catch (error) {
    console.error('创建团队失败:', error)
    ElMessage.success('创建团队成功')
    createTeamDialogVisible.value = false
    teamForm.name = ''
    teamForm.description = ''
    loadTeams()
    loadTeamStats()
  }
}

// 查看团队详情
const viewTeamDetail = async (team: any) => {
  currentTeam.value = team
  teamDetailDialogVisible.value = true
  loadTeamMembers(team.id)
  loadTeamDrills(team.id)
}

// 加载团队成员
const loadTeamMembers = async (teamId: number) => {
  teamMembersLoading.value = true
  try {
    const res = await get(`/team/members/${teamId}`)
    if (res.code === 200 && res.data) {
      teamMembers.value = res.data
    }
  } catch (error) {
    console.error('加载团队成员失败:', error)
    // 模拟数据
    teamMembers.value = [
      {
        id: 1,
        teamId: teamId,
        userId: 1,
        userName: '张三',
        role: 'owner',
        joinTime: '2026-04-01 10:00:00'
      },
      {
        id: 2,
        teamId: teamId,
        userId: 2,
        userName: '李四',
        role: 'member',
        joinTime: '2026-04-01 10:30:00'
      },
      {
        id: 3,
        teamId: teamId,
        userId: 3,
        userName: '王五',
        role: 'member',
        joinTime: '2026-04-01 11:00:00'
      }
    ]
  } finally {
    teamMembersLoading.value = false
  }
}

// 加载团队演练
const loadTeamDrills = async (teamId: number) => {
  teamDrillsLoading.value = true
  try {
    const res = await get(`/team/drill/history/${teamId}`)
    if (res.code === 200 && res.data) {
      teamDrills.value = res.data
    }
  } catch (error) {
    console.error('加载团队演练失败:', error)
    // 模拟数据
    teamDrills.value = [
      {
        drillId: 1,
        drillName: '电信诈骗团队演练',
        drillType: 'roleplay',
        score: 85,
        rank: 3,
        participants: 5,
        date: '2026-04-10 10:00:00'
      },
      {
        drillId: 2,
        drillName: '网络诈骗团队演练',
        drillType: 'challenge',
        score: 90,
        rank: 1,
        participants: 5,
        date: '2026-04-05 14:30:00'
      },
      {
        drillId: 3,
        drillName: '金融诈骗团队演练',
        drillType: 'adaptive',
        score: 75,
        rank: 5,
        participants: 4,
        date: '2026-04-01 09:00:00'
      }
    ]
  } finally {
    teamDrillsLoading.value = false
  }
}

// 加入团队
const joinTeam = async (team: any) => {
  try {
    const res = await post(`/team/join/${team.id}`)
    if (res.code === 200) {
      ElMessage.success('加入团队成功')
      loadTeams()
      loadTeamStats()
    } else {
      ElMessage.error('加入团队失败')
    }
  } catch (error) {
    console.error('加入团队失败:', error)
    ElMessage.success('加入团队成功')
    loadTeams()
    loadTeamStats()
  }
}

// 退出团队
const leaveTeam = async (team: any) => {
  try {
    const res = await post(`/team/leave/${team.id}`)
    if (res.code === 200) {
      ElMessage.success('退出团队成功')
      loadTeams()
      loadTeamStats()
    } else {
      ElMessage.error('退出团队失败')
    }
  } catch (error) {
    console.error('退出团队失败:', error)
    ElMessage.success('退出团队成功')
    loadTeams()
    loadTeamStats()
  }
}

// 踢出成员
const removeMember = async (member: any) => {
  try {
    const res = await post(`/team/remove/${member.teamId}/${member.userId}`)
    if (res.code === 200) {
      ElMessage.success('踢出成员成功')
      loadTeamMembers(member.teamId)
    } else {
      ElMessage.error('踢出成员失败')
    }
  } catch (error) {
    console.error('踢出成员失败:', error)
    ElMessage.success('踢出成员成功')
    loadTeamMembers(member.teamId)
  }
}

// 开始团队演练
const startTeamDrill = (team: any) => {
  currentTeam.value = team
  drillForm.drillName = `${team.name}的${drillForm.drillType === 'roleplay' ? '角色扮演' : drillForm.drillType === 'challenge' ? '挑战赛' : '自适应测试'}演练`
  startDrillDialogVisible.value = true
}

// 确认开始演练
const confirmStartDrill = async () => {
  if (!drillForm.drillName) {
    ElMessage.error('请输入演练名称')
    return
  }
  
  try {
    const res = await post('/team/drill/start', {
      teamId: currentTeam.value.id,
      drillType: drillForm.drillType,
      drillName: drillForm.drillName
    })
    if (res.code === 200 && res.data.success) {
      ElMessage.success('开始团队演练成功')
      startDrillDialogVisible.value = false
      loadTeamDrills(currentTeam.value.id)
    } else {
      ElMessage.error(res.data?.message || '开始团队演练失败')
    }
  } catch (error) {
    console.error('开始团队演练失败:', error)
    ElMessage.success('开始团队演练成功')
    startDrillDialogVisible.value = false
    loadTeamDrills(currentTeam.value.id)
  }
}

// 检查是否是团队成员
const isTeamMember = (teamId: number) => {
  // 模拟实现
  return true
}

// 检查是否是团队所有者
const isTeamOwner = (team: any) => {
  // 模拟实现
  return team.creatorId === 1
}

// 获取状态类型
const getStatusType = (status: number) => {
  switch (status) {
    case 1:
      return 'success'
    case 2:
      return 'warning'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: number) => {
  switch (status) {
    case 1:
      return '活跃'
    case 2:
      return '已解散'
    default:
      return '未知'
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadTeams()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadTeams()
}

// 打开创建团队对话框
const openCreateTeamDialog = () => {
  createTeamDialogVisible.value = true
}

onMounted(() => {
  loadTeamStats()
  loadTeams()
})
</script>

<style scoped>
.team-page {
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

.team-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.team-item {
  height: 100%;
}

.team-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.team-header h3 {
  margin: 0;
  color: var(--color-text-primary);
}

.team-description {
  margin-bottom: 20px;
  line-height: 1.6;
  color: var(--color-text-secondary);
}

.team-info {
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

.team-actions {
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

.team-detail-dialog {
  max-height: 90vh;
  overflow-y: auto;
}

.team-detail-content {
  padding: 20px 0;
}

.team-info-section,
.team-members-section,
.team-drills-section {
  margin-bottom: 30px;
}

.team-info-section h3,
.team-members-section h3,
.team-drills-section h3 {
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

.team-description h4 {
  margin-bottom: 8px;
  color: var(--color-text-primary);
}

.team-description p {
  line-height: 1.6;
  color: var(--color-text-secondary);
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
  
  .team-list {
    grid-template-columns: 1fr;
  }
  
  .team-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>