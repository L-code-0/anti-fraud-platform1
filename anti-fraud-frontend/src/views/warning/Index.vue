<template>
  <div class="warning-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <el-icon><Warning /></el-icon>
          智能预警系统
        </h1>
        <p>实时监控风险行为，及时预警诈骗风险</p>
        <div class="header-stats">
          <div class="stat-item danger">
            <el-icon><WarningFilled /></el-icon>
            <span>{{ highRiskCount }} 高风险</span>
          </div>
          <div class="stat-item warning">
            <el-icon><Warning /></el-icon>
            <span>{{ mediumRiskCount }} 中风险</span>
          </div>
          <div class="stat-item info">
            <el-icon><InfoFilled /></el-icon>
            <span>{{ lowRiskCount }} 低风险</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 操作栏 -->
    <el-card class="action-card" shadow="hover">
      <div class="action-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索预警信息..."
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
          <el-select v-model="riskLevel" placeholder="风险等级" clearable size="large" class="filter-select">
            <el-option label="全部" :value="0" />
            <el-option label="高风险" :value="1" />
            <el-option label="中风险" :value="2" />
            <el-option label="低风险" :value="3" />
          </el-select>

          <el-select v-model="warningType" placeholder="预警类型" clearable size="large" class="filter-select">
            <el-option label="全部" :value="0" />
            <el-option label="电信诈骗" :value="1" />
            <el-option label="网络诈骗" :value="2" />
            <el-option label="校园贷" :value="3" />
            <el-option label="兼职诈骗" :value="4" />
          </el-select>
        </div>
      </div>
    </el-card>

    <!-- 预警列表 -->
    <el-card class="warning-list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>预警信息</h3>
          <span class="count-tag">{{ total }} 条预警</span>
        </div>
      </template>

      <el-table :data="warningList" style="width: 100%">
        <el-table-column prop="id" label="预警ID" width="120" />
        <el-table-column label="风险等级" width="120">
          <template #default="scope">
            <el-tag :type="getRiskLevelType(scope.row.riskLevel)">
              {{ getRiskLevelName(scope.row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningType" label="预警类型" width="150">
          <template #default="scope">
            <span>{{ getWarningTypeName(scope.row.warningType) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="预警内容" />
        <el-table-column prop="riskScore" label="风险分数" width="100" />
        <el-table-column prop="createTime" label="预警时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleWarningDetail(scope.row.id)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="success" size="small" @click="handleProcessWarning(scope.row.id)">
              <el-icon><Check /></el-icon>
              处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty 
        v-if="warningList.length === 0 && !loading" 
        description="暂无预警信息"
        :image-size="200"
      />

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 预警详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="预警详情"
      width="800px"
    >
      <div v-if="currentWarning" class="warning-detail">
        <div class="detail-header">
          <div class="detail-info">
            <h4>{{ currentWarning.content }}</h4>
            <div class="detail-meta">
              <el-tag :type="getRiskLevelType(currentWarning.riskLevel)">
                {{ getRiskLevelName(currentWarning.riskLevel) }}
              </el-tag>
              <span class="meta-item">
                <el-icon><Timer /></el-icon>
                {{ currentWarning.createTime }}
              </span>
              <span class="meta-item">
                <el-icon><DataAnalysis /></el-icon>
                风险分数：{{ currentWarning.riskScore }}
              </span>
            </div>
          </div>
        </div>
        <div class="detail-content">
          <h5>风险分析</h5>
          <p>{{ currentWarning.analysis }}</p>
          <h5>建议措施</h5>
          <p>{{ currentWarning.suggestion }}</p>
          <h5>相关信息</h5>
          <div class="related-info">
            <div class="info-item">
              <span class="info-label">相关用户：</span>
              <span>{{ currentWarning.relatedUser }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">相关知识：</span>
              <el-tag v-for="knowledge in currentWarning.relatedKnowledge" :key="knowledge" size="small" type="info">
                {{ knowledge }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDetailDialog = false">关闭</el-button>
          <el-button type="primary" @click="handleProcessWarning(currentWarning?.id)">
            处理预警
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 处理预警对话框 -->
    <el-dialog
      v-model="showProcessDialog"
      title="处理预警"
      width="600px"
    >
      <el-form :model="processForm" label-width="100px">
        <el-form-item label="处理状态">
          <el-select v-model="processForm.status" placeholder="请选择处理状态">
            <el-option label="已处理" :value="1" />
            <el-option label="待处理" :value="2" />
            <el-option label="误报" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理结果">
          <el-input
            v-model="processForm.result"
            type="textarea"
            :rows="4"
            placeholder="请输入处理结果"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showProcessDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitProcess">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { 
  Warning, WarningFilled, InfoFilled, Search, 
  View, Check, Timer, DataAnalysis 
} from '@element-plus/icons-vue'

const router = useRouter()

// 搜索和筛选
const searchKeyword = ref('')
const riskLevel = ref(0)
const warningType = ref(0)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)

// 统计数据
const highRiskCount = ref(12)
const mediumRiskCount = ref(28)
const lowRiskCount = ref(45)

// 预警列表
const warningList = ref<any[]>([])

// 对话框状态
const showDetailDialog = ref(false)
const showProcessDialog = ref(false)

// 当前预警信息
const currentWarning = ref<any>(null)
const currentWarningId = ref('')

// 处理表单
const processForm = ref({
  status: 1,
  result: ''
})

// 加载预警列表
const loadWarnings = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      keyword: searchKeyword.value,
      riskLevel: riskLevel.value,
      warningType: warningType.value
    }
    const res = await get('/warning/list', params)
    warningList.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
  } catch (e) {
    // 模拟数据
    warningList.value = Array.from({ length: 10 }, (_, i) => ({
      id: i + 1,
      riskLevel: [1, 2, 3][i % 3],
      warningType: [1, 2, 3, 4][i % 4],
      content: ['可疑电信诈骗电话', '可疑网络兼职诈骗', '可疑校园贷短信', '可疑钓鱼网站链接'][i % 4],
      riskScore: Math.floor(Math.random() * 50) + 50,
      createTime: '2024-01-15 10:30',
      analysis: '系统检测到该行为符合诈骗特征，建议立即核实',
      suggestion: '请立即联系相关用户，进行风险提示',
      relatedUser: '张三',
      relatedKnowledge: ['电信诈骗防范', '网络诈骗识别']
    }))
    total.value = 85
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  loadWarnings()
}

// 分页
const handlePageChange = () => {
  loadWarnings()
}

const handleSizeChange = () => {
  page.value = 1
  loadWarnings()
}

// 查看预警详情
const handleWarningDetail = async (warningId: string) => {
  try {
    const res = await get(`/warning/${warningId}`)
    currentWarning.value = res.data
  } catch (e) {
    // 模拟数据
    currentWarning.value = {
      id: warningId,
      riskLevel: 1,
      warningType: 1,
      content: '可疑电信诈骗电话',
      riskScore: 85,
      createTime: '2024-01-15 10:30',
      analysis: '系统检测到该电话号码与已知诈骗号码特征匹配，近期有多人举报类似号码。该号码通过语音机器人进行诈骗，内容涉及冒充公检法、虚假中奖等。',
      suggestion: '1. 立即联系相关用户，告知风险 2. 建议用户不要接听该号码 3. 引导用户学习相关防范知识 4. 如已遭受损失，建议报警',
      relatedUser: '张三',
      relatedKnowledge: ['电信诈骗防范指南', '冒充公检法诈骗识别', '诈骗电话应对技巧']
    }
  }
  showDetailDialog.value = true
}

// 处理预警
const handleProcessWarning = (warningId: string) => {
  currentWarningId.value = warningId
  processForm.value = {
    status: 1,
    result: ''
  }
  showProcessDialog.value = true
}

// 提交处理结果
const handleSubmitProcess = async () => {
  if (!processForm.value.result) {
    ElMessage.warning('请输入处理结果')
    return
  }
  try {
    await post('/warning/process', {
      id: currentWarningId.value,
      status: processForm.value.status,
      result: processForm.value.result
    })
    ElMessage.success('处理成功')
    showProcessDialog.value = false
    loadWarnings()
  } catch (e) {
    ElMessage.error('处理失败')
  }
}

// 获取风险等级类型
const getRiskLevelType = (level: number) => {
  const types: Record<number, string> = {
    1: 'danger',
    2: 'warning',
    3: 'info'
  }
  return types[level] || 'info'
}

// 获取风险等级名称
const getRiskLevelName = (level: number) => {
  const names: Record<number, string> = {
    1: '高风险',
    2: '中风险',
    3: '低风险'
  }
  return names[level] || '未知'
}

// 获取预警类型名称
const getWarningTypeName = (type: number) => {
  const names: Record<number, string> = {
    1: '电信诈骗',
    2: '网络诈骗',
    3: '校园贷',
    4: '兼职诈骗'
  }
  return names[type] || '未知'
}

onMounted(() => {
  loadWarnings()
})
</script>

<style scoped>
.warning-page {
  padding: 0 0 40px;
  min-height: calc(100vh - 160px);
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
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
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-4px);
}

.stat-item.danger {
  background: rgba(239, 68, 68, 0.2);
}

.stat-item.warning {
  background: rgba(245, 158, 11, 0.2);
}

.stat-item.info {
  background: rgba(59, 130, 246, 0.2);
}

.stat-item span {
  font-size: 16px;
  font-weight: 500;
}

/* 操作栏 */
.action-card {
  border-radius: 12px;
  margin-bottom: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.action-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.action-bar {
  display: flex;
  gap: 16px;
  align-items: center;
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

/* 预警列表 */
.warning-list-card {
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0;
  margin: -20px -20px 24px;
  padding: 20px;
  background: var(--el-color-primary-light-9);
  border-radius: 12px 12px 0 0;
}

.card-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

.count-tag {
  background: var(--el-color-primary);
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  box-shadow: 0 1px 4px rgba(66, 184, 131, 0.2);
}

/* 预警详情 */
.warning-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-header {
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color);
}

.detail-info h4 {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
}

.detail-meta {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.detail-content h5 {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 16px 0 8px;
}

.detail-content p {
  color: var(--el-text-color-secondary);
  line-height: 1.5;
  margin: 0 0 16px;
}

.related-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.info-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  min-width: 80px;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 2px solid var(--el-color-primary-light-9);
}

/* 响应式 */
@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    max-width: none;
  }
  
  .filter-actions {
    justify-content: space-between;
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