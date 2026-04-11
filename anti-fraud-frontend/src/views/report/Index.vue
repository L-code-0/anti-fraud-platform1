<template>
  <div class="report-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
      </div>
      <div class="header-content">
        <h1>举报预警中心</h1>
        <p>发现诈骗线索，及时举报；获取最新预警信息</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 快捷入口 -->
      <div class="quick-actions">
        <div class="action-card primary" @click="showReportDialog = true">
          <div class="action-icon">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="action-info">
            <h3>立即举报</h3>
            <p>发现诈骗信息，立即举报</p>
          </div>
          <el-button type="primary" size="large">
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div class="action-card success" @click="router.push('/warning')">
          <div class="action-icon">
            <el-icon><Bell /></el-icon>
          </div>
          <div class="action-info">
            <h3>预警订阅</h3>
            <p>订阅感兴趣的类型推送</p>
          </div>
          <el-button type="success" size="large">
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 最新预警 -->
      <div class="warnings-section">
        <div class="section-header">
          <h2>最新预警</h2>
          <el-button text @click="router.push('/warning')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div class="warnings-list">
          <div class="warning-item" v-for="warning in warnings" :key="warning.id">
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
              <el-button type="primary" text>
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
      </div>

      <!-- 举报历史 -->
      <div class="history-section">
        <div class="section-header">
          <h2>我的举报记录</h2>
          <el-button type="primary" @click="showReportDialog = true">
            <el-icon><Plus /></el-icon>
            新增举报
          </el-button>
        </div>

        <div class="history-table">
          <el-table :data="reportHistory" stripe style="width: 100%">
            <el-table-column prop="id" label="举报编号" width="120" />
            <el-table-column prop="type" label="诈骗类型" width="120">
              <template #default="{ row }">
                <span class="type-badge" :class="'type-' + row.typeId">
                  {{ row.type }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="举报内容" min-width="200" />
            <el-table-column prop="status" label="处理状态" width="100">
              <template #default="{ row }">
                <span class="status-badge" :class="'status-' + row.statusId">
                  {{ row.status }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="time" label="举报时间" width="120" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" text size="small">
                  <el-icon><View /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 举报统计 -->
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
    </div>

    <!-- 举报对话框 -->
    <el-dialog
      v-model="showReportDialog"
      title="提交举报"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="reportForm" label-position="top" class="report-form">
        <el-form-item label="诈骗类型" required>
          <div class="type-selection">
            <el-select v-model="reportForm.type" placeholder="请选择诈骗类型" class="w-full">
              <el-option label="电话诈骗" value="phone" />
              <el-option label="网络诈骗" value="online" />
              <el-option label="短信诈骗" value="sms" />
              <el-option label="社交诈骗" value="social" />
              <el-option label="其他" value="other" />
            </el-select>
            <el-button type="primary" text @click="autoClassify" style="margin-top: 8px;">
              <el-icon><MagicStick /></el-icon>
              智能分类
            </el-button>
          </div>
          <div v-if="autoClassifyResult" class="auto-classify-result" :class="autoClassifyResult.confidence > 0.7 ? 'high-confidence' : 'low-confidence'">
            <el-icon><Check /></el-icon>
            <span>智能分类结果：{{ autoClassifyResult.type }} (置信度：{{ Math.round(autoClassifyResult.confidence * 100) }}%)</span>
          </div>
        </el-form-item>

        <el-form-item label="诈骗方式" required>
          <el-select v-model="reportForm.method" placeholder="请选择诈骗方式" class="w-full">
            <el-option label="冒充客服" value="fake-service" />
            <el-option label="冒充公检法" value="fake-police" />
            <el-option label="杀猪盘" value="pig-butchering" />
            <el-option label="刷单诈骗" value="刷单" />
            <el-option label="贷款诈骗" value="loan" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="发生时间" required>
          <el-date-picker
            v-model="reportForm.time"
            type="datetime"
            placeholder="请选择发生时间"
            class="w-full"
          />
        </el-form-item>

        <el-form-item label="诈骗金额（可选）">
          <el-input v-model="reportForm.amount" placeholder="请输入损失金额">
            <template #append>元</template>
          </el-input>
        </el-form-item>

        <el-form-item label="详细描述" required>
          <el-input
            v-model="reportForm.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述诈骗过程，包括对方说的话、要求做的事等"
          />
        </el-form-item>

        <el-form-item label="上传证据（可选）">
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :limit="3"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">支持 JPG、PNG 格式，最多上传3张图片</div>
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="reportForm.agreeTerms">
            我确认以上信息真实有效，愿意配合相关部门调查
          </el-checkbox>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showReportDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReport">
          <el-icon><Upload /></el-icon>
          提交举报
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Warning, Bell, ArrowRight, View, Plus, Upload,
  WarnTriangleFilled, MagicStick, Check
} from '@element-plus/icons-vue'

const router = useRouter()
const showReportDialog = ref(false)

const reportForm = reactive({
  type: '',
  method: '',
  time: '',
  amount: '',
  description: '',
  agreeTerms: false
})

const autoClassifyResult = ref<any>(null)

const warnings = ref([
  {
    id: 1,
    type: 'phone',
    typeName: '电话诈骗',
    icon: 'Phone',
    title: '警惕冒充客服注销账户诈骗',
    desc: '近期有不法分子冒充电商客服，以注销账户需要转账验证为由实施诈骗，请提高警惕',
    time: '2小时前',
    tags: ['高发', '电话诈骗']
  },
  {
    id: 2,
    type: 'online',
    typeName: '网络诈骗',
    icon: 'Monitor',
    title: '虚假投资理财平台预警',
    desc: '多个虚假投资理财APP以高回报率为诱饵骗取钱财，请勿轻信',
    time: '5小时前',
    tags: ['投资诈骗', 'APP诈骗']
  },
  {
    id: 3,
    type: 'social',
    typeName: '社交诈骗',
    icon: 'Message',
    title: '杀猪盘诈骗手法升级',
    desc: '杀猪盘诈骗手法不断升级，请谨慎网络交友',
    time: '1天前',
    tags: ['杀猪盘', '情感诈骗']
  }
])

const reportHistory = ref([
  {
    id: 'RPT20260115001',
    type: '电话诈骗',
    typeId: 'phone',
    content: '收到冒充银行客服的电话...',
    status: '处理中',
    statusId: 'processing',
    time: '2026-01-15'
  },
  {
    id: 'RPT20260110002',
    type: '网络诈骗',
    typeId: 'online',
    content: '发现虚假购物网站...',
    status: '已处理',
    statusId: 'resolved',
    time: '2026-01-10'
  },
  {
    id: 'RPT20260105003',
    type: '短信诈骗',
    typeId: 'sms',
    content: '收到假冒银行积分兑换短信...',
    status: '已处理',
    statusId: 'resolved',
    time: '2026-01-05'
  }
])

const stats = ref([
  { icon: 'Document', value: '15', label: '总举报数', gradient: 'var(--gradient-primary)' },
  { icon: 'CircleCheck', value: '12', label: '已处理', gradient: 'var(--gradient-success)' },
  { icon: 'Clock', value: '3', label: '处理中', gradient: 'var(--gradient-warning)' },
  { icon: 'Trophy', value: '500', label: '获得积分', gradient: 'var(--gradient-info)' }
])

const reportFraud = (warning: any) => {
  reportForm.type = warning.type
  reportForm.method = ''
  showReportDialog.value = true
}

const autoClassify = () => {
  if (!reportForm.description) {
    ElMessage.warning('请先填写详细描述')
    return
  }

  // 模拟智能分类
  const description = reportForm.description.toLowerCase()
  let type = 'other'
  let confidence = 0.5

  // 基于关键词的简单分类
  if (description.includes('电话') || description.includes('来电') || description.includes('客服')) {
    type = 'phone'
    confidence = 0.85
  } else if (description.includes('网站') || description.includes('app') || description.includes('网购') || description.includes('投资')) {
    type = 'online'
    confidence = 0.9
  } else if (description.includes('短信') || description.includes('验证码') || description.includes('链接')) {
    type = 'sms'
    confidence = 0.8
  } else if (description.includes('微信') || description.includes('QQ') || description.includes('交友') || description.includes('聊天')) {
    type = 'social'
    confidence = 0.75
  }

  // 映射类型到中文
  const typeMap: Record<string, string> = {
    phone: '电话诈骗',
    online: '网络诈骗',
    sms: '短信诈骗',
    social: '社交诈骗',
    other: '其他'
  }

  autoClassifyResult.value = {
    type: typeMap[type],
    confidence
  }

  // 自动填充类型
  reportForm.type = type

  ElMessage.success('智能分类完成')
}

const submitReport = () => {
  if (!reportForm.type || !reportForm.method || !reportForm.description) {
    ElMessage.warning('请填写必填项')
    return
  }

  if (!reportForm.agreeTerms) {
    ElMessage.warning('请勾选确认条款')
    return
  }

  ElMessage.success('举报提交成功，感谢您的参与！')
  showReportDialog.value = false
}
</script>

<style scoped>
.report-page {
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
  background: linear-gradient(135deg, #dc2626 0%, #ef4444 50%, #f87171 100%);
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

/* 快捷入口 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-4);
  margin-top: calc(-1 * var(--spacing-10));
  margin-bottom: var(--spacing-8);
}

.action-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-6);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.action-card.primary {
  background: var(--gradient-danger);
  color: white;
}

.action-card.success {
  background: var(--gradient-success);
  color: white;
}

.action-icon {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.action-info {
  flex: 1;
}

.action-info h3 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-1);
}

.action-info p {
  font-size: var(--font-size-sm);
  opacity: 0.9;
}

/* 最新预警 */
.warnings-section {
  margin-bottom: var(--spacing-10);
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

/* 举报历史 */
.history-section {
  margin-bottom: var(--spacing-10);
}

.history-table {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-4);
  box-shadow: var(--shadow-md);
}

.type-badge {
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  color: white;
}

.type-badge.type-phone { background: var(--danger-color); }
.type-badge.type-online { background: var(--warning-color); }
.type-badge.type-sms { background: var(--info-color); }
.type-badge.type-social { background: var(--success-color); }

.status-badge {
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.status-badge.status-processing {
  background: var(--warning-bg);
  color: var(--warning-color);
}

.status-badge.status-resolved {
  background: var(--success-bg);
  color: var(--success-color);
}

/* 统计 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-4);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-5);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
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

/* 举报表单 */
.report-form {
  padding: var(--spacing-2);
}

.w-full {
  width: 100%;
}

.upload-tip {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
  margin-top: var(--spacing-2);
}

.type-selection {
  position: relative;
}

.auto-classify-result {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2);
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  margin-top: var(--spacing-2);
}

.auto-classify-result.high-confidence {
  background: var(--success-bg);
  color: var(--success-color);
}

.auto-classify-result.low-confidence {
  background: var(--warning-bg);
  color: var(--warning-color);
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    padding: var(--spacing-10) var(--spacing-4);
  }

  .header-content h1 {
    font-size: var(--font-size-2xl);
  }

  .quick-actions {
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

  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
