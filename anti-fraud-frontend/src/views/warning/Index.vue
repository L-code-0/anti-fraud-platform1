<template>
  <div class="warning-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
      </div>
      <div class="header-content">
        <h1>实时预警中心</h1>
        <p>获取最新诈骗预警信息，提前防范风险</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 预警统计 -->
      <div class="stats-section">
        <div class="stat-card" v-for="stat in stats" :key="stat.label">
          <div class="stat-icon" :style="{ background: stat.gradient }">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
        </div>
      </div>

      <!-- 预警筛选 -->
      <div class="filter-section">
        <div class="filter-card">
          <div class="filter-header">
            <h3>预警筛选</h3>
          </div>
          <div class="filter-content">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-select v-model="filterForm.type" placeholder="诈骗类型" class="w-full">
                  <el-option label="全部" value="" />
                  <el-option label="电话诈骗" value="phone" />
                  <el-option label="网络诈骗" value="online" />
                  <el-option label="短信诈骗" value="sms" />
                  <el-option label="社交诈骗" value="social" />
                </el-select>
              </el-col>
              <el-col :span="8">
                <el-select v-model="filterForm.level" placeholder="预警等级" class="w-full">
                  <el-option label="全部" value="" />
                  <el-option label="低" value="low" />
                  <el-option label="中" value="medium" />
                  <el-option label="高" value="high" />
                </el-select>
              </el-col>
              <el-col :span="8">
                <el-button type="primary" @click="filterWarnings">
                  <el-icon><Search /></el-icon>
                  筛选
                </el-button>
                <el-button @click="resetFilter">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>

      <!-- 最新预警 -->
      <div class="warnings-section">
        <div class="section-header">
          <h2>最新预警</h2>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            发布预警
          </el-button>
        </div>

        <div class="warnings-list">
          <div class="warning-item" v-for="warning in warnings" :key="warning.id">
            <div class="warning-level" :class="'level-' + warning.level">
              {{ warning.levelName }}
            </div>
            <div class="warning-icon" :class="'type-' + warning.type">
              <el-icon><component :is="warning.icon" /></el-icon>
            </div>
            <div class="warning-content">
              <div class="warning-header">
                <span class="warning-type">{{ warning.typeName }}</span>
                <span class="warning-time">{{ warning.time }}</span>
              </div>
              <h4>{{ warning.title }}</h4>
              <p>{{ warning.desc }}</p>
              <div class="warning-tags">
                <span class="tag" v-for="tag in warning.tags" :key="tag">{{ tag }}</span>
              </div>
            </div>
            <div class="warning-actions">
              <el-button type="primary" text @click="viewWarning(warning)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button type="danger" text @click="reportFraud(warning)">
                <el-icon><WarnTriangleFilled /></el-icon>
                我遇到类似情况
              </el-button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 发布预警对话框 -->
    <el-dialog
      v-model="showAddDialog"
      title="发布预警"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="warningForm" label-position="top" class="warning-form">
        <el-form-item label="预警标题" required>
          <el-input v-model="warningForm.title" placeholder="请输入预警标题" />
        </el-form-item>

        <el-form-item label="诈骗类型" required>
          <el-select v-model="warningForm.type" placeholder="请选择诈骗类型" class="w-full">
            <el-option label="电话诈骗" value="phone" />
            <el-option label="网络诈骗" value="online" />
            <el-option label="短信诈骗" value="sms" />
            <el-option label="社交诈骗" value="social" />
          </el-select>
        </el-form-item>

        <el-form-item label="预警等级" required>
          <el-select v-model="warningForm.level" placeholder="请选择预警等级" class="w-full">
            <el-option label="低" value="low" />
            <el-option label="中" value="medium" />
            <el-option label="高" value="high" />
          </el-select>
        </el-form-item>

        <el-form-item label="预警内容" required>
          <el-input
            v-model="warningForm.content"
            type="textarea"
            :rows="4"
            placeholder="请详细描述预警内容，包括诈骗手法、识别方法、防范措施等"
          />
        </el-form-item>

        <el-form-item label="标签">
          <el-tag
            v-for="tag in warningForm.tags"
            :key="tag"
            closable
            @close="removeTag(tag)"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-model="newTag"
            placeholder="输入标签并按回车添加"
            @keyup.enter="addTag"
            style="width: 160px; margin-left: 10px;"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitWarning">
          <el-icon><Upload /></el-icon>
          发布预警
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import * as VueRouter from 'vue-router'
const useRouter = VueRouter.useRouter
import { ElMessage } from 'element-plus'
import {
  View, Plus, Upload, Search, Refresh, WarnTriangleFilled
} from '@element-plus/icons-vue'

const router = useRouter()
const showAddDialog = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

const filterForm = reactive({
  type: '',
  level: ''
})

const warningForm = reactive({
  title: '',
  type: '',
  level: '',
  content: '',
  tags: [] as string[]
})

const newTag = ref('')

const stats = ref([
  { icon: 'WarnTriangleFilled', value: '23', label: '今日预警', gradient: 'var(--gradient-danger)' },
  { icon: 'Bell', value: '156', label: '本周预警', gradient: 'var(--gradient-warning)' },
  { icon: 'Message', value: '892', label: '本月预警', gradient: 'var(--gradient-info)' },
  { icon: 'Trophy', value: '98%', label: '预警准确率', gradient: 'var(--gradient-success)' }
])

const warnings = ref([
  {
    id: 1,
    type: 'phone',
    typeName: '电话诈骗',
    icon: 'Phone',
    level: 'high',
    levelName: '高',
    title: '警惕冒充客服注销账户诈骗',
    desc: '近期有不法分子冒充电商客服，以注销账户需要转账验证为由实施诈骗，请提高警惕',
    time: '2小时前',
    tags: ['高发', '电话诈骗', '冒充客服']
  },
  {
    id: 2,
    type: 'online',
    typeName: '网络诈骗',
    icon: 'Monitor',
    level: 'medium',
    levelName: '中',
    title: '虚假投资理财平台预警',
    desc: '多个虚假投资理财APP以高回报率为诱饵骗取钱财，请勿轻信',
    time: '5小时前',
    tags: ['投资诈骗', 'APP诈骗', '高风险']
  },
  {
    id: 3,
    type: 'social',
    typeName: '社交诈骗',
    icon: 'Message',
    level: 'high',
    levelName: '高',
    title: '杀猪盘诈骗手法升级',
    desc: '杀猪盘诈骗手法不断升级，请谨慎网络交友',
    time: '1天前',
    tags: ['杀猪盘', '情感诈骗', '高发']
  },
  {
    id: 4,
    type: 'sms',
    typeName: '短信诈骗',
    icon: 'Message',
    level: 'low',
    levelName: '低',
    title: '假冒银行积分兑换短信',
    desc: '收到假冒银行积分兑换短信，点击链接后可能导致个人信息泄露',
    time: '2天前',
    tags: ['短信诈骗', '钓鱼链接']
  }
])

const filterWarnings = () => {
  // 模拟筛选功能
  ElMessage.success('筛选成功')
}

const resetFilter = () => {
  filterForm.type = ''
  filterForm.level = ''
  ElMessage.success('筛选条件已重置')
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
}

const viewWarning = (warning: any) => {
  // 查看预警详情
  ElMessage.info('查看预警详情')
}

const reportFraud = (warning: any) => {
  // 跳转到举报页面
  router.push('/report')
}

const addTag = () => {
  if (newTag.value && !warningForm.tags.includes(newTag.value)) {
    warningForm.tags.push(newTag.value)
    newTag.value = ''
  }
}

const removeTag = (tag: string) => {
  const index = warningForm.tags.indexOf(tag)
  if (index > -1) {
    warningForm.tags.splice(index, 1)
  }
}

const submitWarning = () => {
  if (!warningForm.title || !warningForm.type || !warningForm.level || !warningForm.content) {
    ElMessage.warning('请填写必填项')
    return
  }

  ElMessage.success('预警发布成功')
  showAddDialog.value = false
}
</script>

<style scoped>
.warning-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

.page-header {
  position: relative;
  padding: var(--spacing-16) var(--spacing-6);
  overflow: hidden;
}

.header-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.header-bg .bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 50%, #fcd34d 100%);
}

.header-content {
  position: relative;
  z-index: 1;
  max-width: 1280px;
  margin: 0 auto;
  text-align: center;
  color: white;
}

.header-content h1 {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-4);
}

.header-content p {
  font-size: var(--font-size-lg);
  opacity: 0.9;
}

.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6) var(--spacing-12);
}

/* 统计 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-4);
  margin-top: calc(-1 * var(--spacing-10));
  margin-bottom: var(--spacing-8);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-5);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

/* 筛选 */
.filter-section {
  margin-bottom: var(--spacing-8);
}

.filter-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-5);
  box-shadow: var(--shadow-md);
}

.filter-header {
  margin-bottom: var(--spacing-4);
}

.filter-header h3 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

/* 预警列表 */
.warnings-section {
  margin-bottom: var(--spacing-8);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
}

.section-header h2 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.warnings-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-6);
}

.warning-item {
  display: flex;
  gap: var(--spacing-4);
  padding: var(--spacing-5);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

.warning-item:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.warning-level {
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  color: white;
  flex-shrink: 0;
  align-self: flex-start;
  margin-top: 8px;
}

.warning-level.level-low {
  background: var(--success-color);
}

.warning-level.level-medium {
  background: var(--warning-color);
}

.warning-level.level-high {
  background: var(--danger-color);
}

.warning-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.warning-icon.type-phone { background: var(--danger-color); }
.warning-icon.type-online { background: var(--warning-color); }
.warning-icon.type-sms { background: var(--info-color); }
.warning-icon.type-social { background: var(--success-color); }

.warning-content {
  flex: 1;
}

.warning-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-2);
}

.warning-type {
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--danger-bg);
  color: var(--danger-color);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-sm);
}

.warning-time {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.warning-content h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.warning-content p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-3);
}

.warning-tags {
  display: flex;
  gap: var(--spacing-2);
  flex-wrap: wrap;
}

.warning-tags .tag {
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--bg-secondary);
  color: var(--text-tertiary);
  font-size: var(--font-size-xs);
  border-radius: var(--radius-sm);
}

.warning-actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
  flex-shrink: 0;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: var(--spacing-6);
}

/* 预警表单 */
.warning-form {
  padding: var(--spacing-2);
}

/* 响应式 */
@media (max-width: 1024px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: var(--spacing-10) var(--spacing-4);
  }

  .header-content h1 {
    font-size: var(--font-size-2xl);
  }

  .stats-section {
    grid-template-columns: 1fr;
    margin-top: 0;
  }

  .warning-item {
    flex-direction: column;
  }

  .warning-actions {
    flex-direction: row;
    justify-content: flex-start;
  }
}
</style>
