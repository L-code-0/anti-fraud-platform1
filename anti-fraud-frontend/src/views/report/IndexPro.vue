<template>
  <div class="report-index">
    <el-card class="page-header-card">
      <div class="header-content">
        <div class="header-info">
          <h1>举报预警中心</h1>
          <p>发现诈骗行为？立即举报，共同维护网络安全</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="$router.push('/report/submit')">
            <el-icon><Plus /></el-icon> 立即举报
          </el-button>
          <el-button @click="showSubscriptionDialog = true">
            <el-icon><Bell /></el-icon> 预警订阅
          </el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-icon blue"><el-icon><Document /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ reportStats.total }}</span>
            <span class="stat-label">总举报数</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-icon green"><el-icon><CircleCheck /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ reportStats.processed }}</span>
            <span class="stat-label">已处理</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-icon orange"><el-icon><Warning /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ warningStats.total }}</span>
            <span class="stat-label">预警信息</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-icon purple"><el-icon><DataLine /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ myReports }}</span>
            <span class="stat-label">我的举报</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card class="tabs-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="举报列表" name="reports">
          <div class="filter-row">
            <el-input v-model="searchQuery" placeholder="搜索举报内容..." clearable class="search-input">
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
            <el-select v-model="reportType" placeholder="全部类型" clearable>
              <el-option label="电话诈骗" :value="1" />
              <el-option label="网络诈骗" :value="2" />
              <el-option label="短信诈骗" :value="3" />
              <el-option label="其他" :value="4" />
            </el-select>
            <el-select v-model="reportStatus" placeholder="全部状态" clearable>
              <el-option label="待处理" :value="0" />
              <el-option label="处理中" :value="1" />
              <el-option label="已完成" :value="2" />
              <el-option label="已驳回" :value="3" />
            </el-select>
          </div>

          <el-table :data="filteredReports" stripe v-loading="loading">
            <el-table-column prop="id" label="编号" width="80" />
            <el-table-column label="诈骗类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getTypeColor(row.type)">{{ row.typeName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="举报标题" min-width="200" show-overflow-tooltip />
            <el-table-column label="发生地点" width="120">
              <template #default="{ row }">
                <span>{{ row.location || '未知' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="举报时间" width="160" />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="viewReport(row)">查看</el-button>
                <el-button link type="primary" @click="trackReport(row)">追踪</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="totalCount"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="预警信息" name="warnings">
          <div class="warnings-grid">
            <el-card v-for="warning in warnings" :key="warning.id" class="warning-card" :class="{ 'urgent': warning.level === 3 }">
              <div class="warning-header">
                <el-tag :type="getWarningLevelType(warning.level)" size="large">
                  {{ getWarningLevelText(warning.level) }}
                </el-tag>
                <span class="warning-time">{{ warning.publishTime }}</span>
              </div>
              <h3 class="warning-title">{{ warning.title }}</h3>
              <p class="warning-content">{{ warning.content }}</p>
              <div class="warning-tags">
                <el-tag v-for="tag in warning.tags" :key="tag" size="small">{{ tag }}</el-tag>
              </div>
              <div class="warning-footer">
                <span class="warning-source">{{ warning.source }}</span>
                <el-button size="small" @click="handleWarningDetail(warning)">查看详情</el-button>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="我的举报" name="my-reports">
          <el-table :data="myReportList" stripe>
            <el-table-column prop="id" label="编号" width="80" />
            <el-table-column label="诈骗类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getTypeColor(row.type)">{{ row.typeName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="举报标题" min-width="200" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="处理进度" width="200">
              <template #default="{ row }">
                <el-progress :percentage="row.progress" :stroke-width="8" :show-text="false" />
                <span class="progress-text">{{ row.progress }}%</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="举报时间" width="160" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button link type="primary" @click="viewReport(row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="showSubscriptionDialog" title="预警订阅" width="500px">
      <div class="subscription-form">
        <el-form :model="subscriptionForm" label-width="100px">
          <el-form-item label="订阅类型">
            <el-checkbox-group v-model="subscriptionForm.types">
              <el-checkbox label="电话诈骗">电话诈骗</el-checkbox>
              <el-checkbox label="网络诈骗">网络诈骗</el-checkbox>
              <el-checkbox label="短信诈骗">短信诈骗</el-checkbox>
              <el-checkbox label="情感诈骗">情感诈骗</el-checkbox>
              <el-checkbox label="投资诈骗">投资诈骗</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="预警级别">
            <el-checkbox-group v-model="subscriptionForm.levels">
              <el-checkbox label="3">紧急</el-checkbox>
              <el-checkbox label="2">重要</el-checkbox>
              <el-checkbox label="1">一般</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="通知方式">
            <el-radio-group v-model="subscriptionForm.notifyType">
              <el-radio label="app">APP推送</el-radio>
              <el-radio label="email">邮件</el-radio>
              <el-radio label="sms">短信</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showSubscriptionDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubscribe">确认订阅</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showReportDialog" title="举报详情" width="600px">
      <div v-if="currentReport" class="report-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="举报编号">{{ currentReport.id }}</el-descriptions-item>
          <el-descriptions-item label="诈骗类型">
            <el-tag :type="getTypeColor(currentReport.type)">{{ currentReport.typeName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="举报时间">{{ currentReport.createTime }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusType(currentReport.status)">{{ getStatusText(currentReport.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发生地点" :span="2">{{ currentReport.location }}</el-descriptions-item>
          <el-descriptions-item label="举报标题" :span="2">{{ currentReport.title }}</el-descriptions-item>
          <el-descriptions-item label="详细描述" :span="2">{{ currentReport.description }}</el-descriptions-item>
          <el-descriptions-item label="附件" :span="2">
            <div v-if="currentReport.attachments?.length">
              <el-image v-for="(img, i) in currentReport.attachments" :key="i" :src="img" style="width: 100px; height: 100px; margin-right: 8px;" fit="cover" />
            </div>
            <span v-else>无附件</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider>处理进度</el-divider>
        <el-steps :active="currentReport.status" finish-status="success">
          <el-step title="提交举报" />
          <el-step title="审核中" />
          <el-step title="处理中" />
          <el-step title="已完成" />
        </el-steps>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Bell, Search, Document, CircleCheck, Warning, DataLine } from '@element-plus/icons-vue'

const router = useRouter()

const activeTab = ref('reports')
const loading = ref(false)
const searchQuery = ref('')
const reportType = ref<number | null>(null)
const reportStatus = ref<number | null>(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)
const showSubscriptionDialog = ref(false)
const showReportDialog = ref(false)
const currentReport = ref<any>(null)

const reportStats = reactive({ total: 1256, processed: 1089 })
const warningStats = reactive({ total: 48 })
const myReports = ref(5)

const subscriptionForm = reactive({
  types: ['电话诈骗', '网络诈骗'],
  levels: ['3', '2'],
  notifyType: 'app'
})

const reports = ref<any[]>([])
const warnings = ref<any[]>([])
const myReportList = ref<any[]>([])

const filteredReports = computed(() => {
  let result = reports.value
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(r => r.title.toLowerCase().includes(query))
  }
  if (reportType.value) result = result.filter(r => r.type === reportType.value)
  if (reportStatus.value !== null) result = result.filter(r => r.status === reportStatus.value)
  return result
})

const getTypeColor = (type: number) => ({ 1: 'danger', 2: 'warning', 3: 'success', 4: 'info' }[type] || 'info')
const getTypeName = (type: number) => ({ 1: '电话诈骗', 2: '网络诈骗', 3: '短信诈骗', 4: '其他' }[type] || '其他')
const getStatusType = (status: number) => ({ 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }[status] || 'info')
const getStatusText = (status: number) => ({ 0: '待处理', 1: '处理中', 2: '已完成', 3: '已驳回' }[status] || '未知')
const getWarningLevelType = (level: number) => ({ 1: 'info', 2: 'warning', 3: 'danger' }[level] || 'info')
const getWarningLevelText = (level: number) => ({ 1: '一般', 2: '重要', 3: '紧急' }[level] || '未知')

const viewReport = (report: any) => {
  currentReport.value = report
  showReportDialog.value = true
}

const trackReport = (report: any) => { ElMessage.info('追踪功能开发中') }
const handleWarningDetail = (warning: any) => { ElMessage.info('查看预警详情') }
const handleSubscribe = () => {
  ElMessage.success('订阅成功')
  showSubscriptionDialog.value = false
}

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    reports.value = [
      { id: 1001, type: 1, typeName: '电话诈骗', title: '疑似冒充公检法诈骗电话', location: '北京市海淀区', status: 2, createTime: '2026-01-15 14:30', progress: 100, attachments: [] },
      { id: 1002, type: 2, typeName: '网络诈骗', title: '虚假购物网站诱导转账', location: '网络', status: 1, createTime: '2026-01-14 10:20', progress: 60, attachments: [] },
      { id: 1003, type: 3, typeName: '短信诈骗', title: '冒充银行积分兑换短信', location: '手机', status: 0, createTime: '2026-01-13 16:45', progress: 10, attachments: [] },
      { id: 1004, type: 2, typeName: '网络诈骗', title: '刷单返利诈骗', location: '网络', status: 2, createTime: '2026-01-12 09:15', progress: 100, attachments: [] },
      { id: 1005, type: 4, typeName: '其他', title: '游戏账号交易诈骗', location: '网络', status: 3, createTime: '2026-01-11 20:30', progress: 0, attachments: [] }
    ]
    warnings.value = [
      { id: 1, level: 3, title: '紧急预警：近期冒充京东客服诈骗高发', content: '近期有不法分子冒充京东客服，以注销白条、影响征信等名义实施诈骗，请提高警惕。', tags: ['冒充客服', '征信诈骗'], publishTime: '2小时前', source: '官方预警' },
      { id: 2, level: 2, title: '重要提示：虚假投资理财平台泛滥', content: '近期发现多个虚假投资理财平台，打着高收益幌子进行诈骗，请勿轻信。', tags: ['投资诈骗', '高收益陷阱'], publishTime: '1天前', source: '反诈中心' },
      { id: 3, level: 1, title: '日常提醒：谨防网购退款诈骗', content: '提醒广大用户，接到自称卖家客服的退款电话时，请通过官方渠道核实。', tags: ['网购退款', '客服诈骗'], publishTime: '3天前', source: '安全中心' }
    ]
    myReportList.value = reports.value.slice(0, 3)
    totalCount.value = reports.value.length
    loading.value = false
  }, 500)
}

onMounted(() => { loadData() })
</script>

<style scoped>
.report-index { max-width: 1440px; margin: 0 auto; }
.page-header-card { margin-bottom: var(--spacing-6); }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.header-info h1 { margin: 0 0 var(--spacing-2); font-size: var(--font-size-3xl); }
.header-info p { margin: 0; color: var(--text-secondary); }
.header-actions { display: flex; gap: var(--spacing-3); }
.stats-row { margin-bottom: var(--spacing-6); }
.stat-card { display: flex; align-items: center; gap: var(--spacing-4); padding: var(--spacing-4); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-sm); }
.stat-icon { width: 56px; height: 56px; border-radius: var(--radius-xl); display: flex; align-items: center; justify-content: center; font-size: 24px; color: white; }
.stat-icon.blue { background: linear-gradient(135deg, #667eea, #764ba2); }
.stat-icon.green { background: linear-gradient(135deg, #11998e, #38ef7d); }
.stat-icon.orange { background: linear-gradient(135deg, #f97316, #fbbf24); }
.stat-icon.purple { background: linear-gradient(135deg, #7c3aed, #a855f7); }
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: var(--font-size-2xl); font-weight: var(--font-weight-bold); }
.stat-label { font-size: var(--font-size-sm); color: var(--text-secondary); }
.tabs-card { margin-bottom: var(--spacing-6); }
.filter-row { display: flex; gap: var(--spacing-3); margin-bottom: var(--spacing-4); flex-wrap: wrap; }
.search-input { width: 240px; }
.pagination-wrapper { display: flex; justify-content: center; margin-top: var(--spacing-4); }
.progress-text { font-size: var(--font-size-xs); color: var(--text-secondary); margin-left: var(--spacing-2); }
.warnings-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: var(--spacing-4); }
.warning-card { position: relative; }
.warning-card.urgent { border-left: 4px solid var(--danger-color); }
.warning-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--spacing-3); }
.warning-time { color: var(--text-secondary); font-size: var(--font-size-sm); }
.warning-title { margin: 0 0 var(--spacing-2); font-size: var(--font-size-lg); }
.warning-content { color: var(--text-regular); font-size: var(--font-size-sm); margin-bottom: var(--spacing-3); }
.warning-tags { display: flex; gap: var(--spacing-1); flex-wrap: wrap; margin-bottom: var(--spacing-3); }
.warning-footer { display: flex; justify-content: space-between; align-items: center; }
.warning-source { color: var(--text-secondary); font-size: var(--font-size-sm); }
@media (max-width: 768px) {
  .header-content { flex-direction: column; gap: var(--spacing-4); }
  .filter-row { flex-direction: column; }
  .search-input { width: 100%; }
  .warnings-grid { grid-template-columns: 1fr; }
}
</style>
