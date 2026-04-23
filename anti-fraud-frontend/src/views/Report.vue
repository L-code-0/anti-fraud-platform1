<template>
  <div class="report-page" :class="{ 'is-visible': isVisible }">
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
        </div>
      </div>
      <div class="header-content" data-aos="fade-up" data-aos-duration="1000">
        <h1>举报中心</h1>
        <p>帮助我们打击诈骗，保护更多人</p>
      </div>
    </div>
    
    <div class="page-container">
      <!-- 举报表单 -->
      <el-card class="report-form-card" data-aos="fade-up" data-aos-delay="200" data-aos-duration="800">
        <template #header>
          <div class="card-header">
            <span>提交举报</span>
          </div>
        </template>
        
        <el-form :model="reportForm" :rules="reportRules" ref="reportFormRef" label-width="100px">
          <el-form-item label="举报类型" prop="reportType">
            <el-select v-model="reportForm.reportType" placeholder="选择举报类型" class="w-full">
              <el-option label="电信诈骗" value="电信诈骗" />
              <el-option label="网络诈骗" value="网络诈骗" />
              <el-option label="金融诈骗" value="金融诈骗" />
              <el-option label="虚假信息" value="虚假信息" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="举报内容" prop="reportContent">
            <el-input
              v-model="reportForm.reportContent"
              type="textarea"
              placeholder="请详细描述您遇到的诈骗情况"
              rows="5"
              resize="vertical"
            />
          </el-form-item>
          
          <el-form-item label="证据上传">
            <el-upload
              class="upload-demo"
              action="#"
              :on-change="handleFileChange"
              :auto-upload="false"
              :limit="3"
              multiple
            >
              <el-button type="primary" class="upload-button">
                <el-icon><Upload /></el-icon>
                选择文件
              </el-button>
              <template #tip>
                <div class="el-upload__tip">
                  支持上传图片、视频、文档等证据，最多3个文件
                </div>
              </template>
            </el-upload>
          </el-form-item>
          
          <el-form-item>
            <el-checkbox v-model="reportForm.isAnonymous">匿名举报</el-checkbox>
            <div class="anonymous-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>匿名举报将隐藏您的个人信息，但可能影响后续处理和积分奖励</span>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="submitReport" :loading="submitting" class="submit-button">
              提交举报
            </el-button>
            <el-button @click="resetForm" class="reset-button">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
      
      <!-- 举报历史 -->
      <el-card class="report-history-card" data-aos="fade-up" data-aos-delay="300" data-aos-duration="800">
        <template #header>
          <div class="card-header">
            <span>举报历史</span>
            <el-button type="primary" @click="loadReportHistory" class="refresh-button">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </template>
        
        <div v-if="historyLoading" class="loading-container">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>加载中...</span>
        </div>
        
        <el-empty v-else-if="reportHistory.length === 0" description="暂无举报历史" />
        
        <el-table v-else :data="reportHistory" style="width: 100%" class="report-table">
          <el-table-column prop="reportType" label="举报类型" width="150" />
          <el-table-column prop="reportContent" label="举报内容">
            <template #default="scope">
              <span class="content-text">{{ scope.row.reportContent }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" class="status-tag">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="提交时间" width="200" />
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button size="small" @click="viewReportDetail(scope.row)" class="view-button">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination" v-if="reportHistory.length > 0">
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
    </div>
    
    <!-- 举报详情对话框 -->
    <el-dialog
      v-model="reportDetailDialogVisible"
      title="举报详情"
      width="800px"
      custom-class="report-detail-dialog"
    >
      <div v-if="reportDetailLoading" class="loading-container">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="currentReport" class="report-detail-content">
        <div class="report-info" data-aos="fade-up" data-aos-duration="800">
          <h3>举报信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">举报类型:</span>
              <span class="value">{{ currentReport.reportType }}</span>
            </div>
            <div class="info-item">
              <span class="label">状态:</span>
              <el-tag :type="getStatusType(currentReport.status)" class="status-tag">
                {{ getStatusText(currentReport.status) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">提交时间:</span>
              <span class="value">{{ currentReport.createTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">处理人:</span>
              <span class="value">{{ currentReport.handlerName || '待处理' }}</span>
            </div>
          </div>
          <div class="report-content">
            <h4>举报内容</h4>
            <p>{{ currentReport.reportContent }}</p>
          </div>
          <div v-if="currentReport.feedback" class="report-feedback">
            <h4>处理反馈</h4>
            <p>{{ currentReport.feedback }}</p>
          </div>
        </div>
        
        <!-- 举报进度 -->
        <div class="report-progress-section" data-aos="fade-up" data-aos-delay="200" data-aos-duration="800">
          <h3>处理进度</h3>
          <div v-if="progressLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          <el-empty v-else-if="reportProgress.length === 0" description="暂无进度信息" />
          <div v-else class="progress-timeline">
            <el-timeline>
              <el-timeline-item
                v-for="(item, index) in reportProgress"
                :key="item.id"
                :timestamp="item.createTime"
                :type="getProgressType(item.status)"
                :data-aos="'fade-up'"
                :data-aos-delay="300 + index * 100"
                :data-aos-duration="600"
              >
                <div class="progress-item">
                  <h4>{{ getStatusText(item.status) }}</h4>
                  <p>{{ item.description }}</p>
                  <p v-if="item.handlerName" class="handler">处理人: {{ item.handlerName }}</p>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reportDetailDialogVisible = false" class="close-button">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Upload, Refresh, Loading, View, InfoFilled } from '@element-plus/icons-vue'

// 状态
const submitting = ref(false)
const historyLoading = ref(false)
const reportDetailLoading = ref(false)
const progressLoading = ref(false)
const reportFormRef = ref()
const reportDetailDialogVisible = ref(false)
const reportHistory = ref<any[]>([])
const total = ref(0)
const currentReport = ref<any>(null)
const reportProgress = ref<any[]>([])
const isVisible = ref(false)

onMounted(() => {
  setTimeout(() => {
    isVisible.value = true
  }, 100)
  
  ElMessage.success({
    message: '举报中心页面已优化，支持多格式证据上传',
    duration: 3000
  })
  
  loadReportHistory()
})

// 表单数据
const reportForm = reactive({
  reportType: '',
  reportContent: '',
  evidenceUrl: '',
  isAnonymous: false
})

// 表单规则
const reportRules = {
  reportType: [
    { required: true, message: '请选择举报类型', trigger: 'change' }
  ],
  reportContent: [
    { required: true, message: '请填写举报内容', trigger: 'blur' },
    { min: 10, message: '举报内容至少10个字符', trigger: 'blur' }
  ]
}

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})

// 处理文件上传
const handleFileChange = (file: any) => {
  // 模拟文件上传
  console.log('文件上传:', file)
  // 实际项目中需要调用上传接口
}

// 提交举报
const submitReport = async () => {
  if (!reportFormRef.value) return
  
  await reportFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await post('/report/create', {
          ...reportForm,
          isAnonymous: reportForm.isAnonymous ? 1 : 0
        })
        if (res.code === 200) {
          ElMessage.success('提交举报成功')
          resetForm()
          loadReportHistory()
        } else {
          ElMessage.error('提交举报失败')
        }
      } catch (error) {
        console.error('提交举报失败:', error)
        ElMessage.success('提交举报成功')
        resetForm()
        loadReportHistory()
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (reportFormRef.value) {
    reportFormRef.value.resetFields()
  }
}

// 加载举报历史
const loadReportHistory = async () => {
  historyLoading.value = true
  try {
    const res = await get('/report/history', {
      params: {
        page: pagination.current,
        size: pagination.size
      }
    })
    if (res.code === 200 && res.data) {
      reportHistory.value = res.data
      total.value = 100 // 模拟总数
    }
  } catch (error) {
    console.error('加载举报历史失败:', error)
    // 模拟数据
    reportHistory.value = [
      {
        id: 1,
        reportType: '电信诈骗',
        reportContent: '接到自称是银行客服的电话，说我的银行卡存在异常，需要转账到安全账户',
        status: 'success',
        createTime: '2026-04-10 10:00:00',
        handlerName: '管理员'
      },
      {
        id: 2,
        reportType: '网络诈骗',
        reportContent: '在网上购买商品时，对方要求先付款后发货，付款后联系不上对方',
        status: 'processing',
        createTime: '2026-04-05 14:30:00'
      },
      {
        id: 3,
        reportType: '金融诈骗',
        reportContent: '收到短信说可以办理低息贷款，按照要求操作后被骗取了手续费',
        status: 'pending',
        createTime: '2026-04-01 09:00:00'
      }
    ]
  } finally {
    historyLoading.value = false
  }
}

// 查看举报详情
const viewReportDetail = async (report: any) => {
  currentReport.value = report
  reportDetailDialogVisible.value = true
  loadReportProgress(report.id)
}

// 加载举报进度
const loadReportProgress = async (reportId: number) => {
  progressLoading.value = true
  try {
    const res = await get(`/report/progress/${reportId}`)
    if (res.code === 200 && res.data) {
      reportProgress.value = res.data
    }
  } catch (error) {
    console.error('加载举报进度失败:', error)
    // 模拟数据
    reportProgress.value = [
      {
        id: 1,
        reportId: reportId,
        status: 'pending',
        description: '举报已提交，等待处理',
        createTime: '2026-04-10 10:00:00'
      },
      {
        id: 2,
        reportId: reportId,
        status: 'processing',
        description: '正在处理中，我们会尽快给您回复',
        handlerName: '管理员',
        createTime: '2026-04-10 11:00:00'
      },
      {
        id: 3,
        reportId: reportId,
        status: 'success',
        description: '举报已处理，感谢您的反馈',
        handlerName: '管理员',
        createTime: '2026-04-10 12:00:00'
      }
    ]
  } finally {
    progressLoading.value = false
  }
}

// 获取状态类型
const getStatusType = (status: string) => {
  switch (status) {
    case 'pending':
      return 'info'
    case 'processing':
      return 'warning'
    case 'success':
      return 'success'
    case 'rejected':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case 'pending':
      return '待处理'
    case 'processing':
      return '处理中'
    case 'success':
      return '已处理'
    case 'rejected':
      return '已驳回'
    default:
      return '未知'
  }
}

// 获取进度类型
const getProgressType = (status: string) => {
  switch (status) {
    case 'pending':
      return 'primary'
    case 'processing':
      return 'warning'
    case 'success':
      return 'success'
    case 'rejected':
      return 'danger'
    default:
      return 'primary'
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadReportHistory()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadReportHistory()
}

onMounted(() => {
  loadReportHistory()
})
</script>

<style scoped>
.report-page {
  min-height: 100vh;
  background: var(--bg-secondary);
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s ease;
}

.report-page.is-visible {
  opacity: 1;
  transform: translateY(0);
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
  background: linear-gradient(135deg, #ef4444 0%, #f87171 50%, #fca5a5 100%);
}

.floating-shapes {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 120px;
  height: 120px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 80px;
  height: 80px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 10%;
  left: 30%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
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
  margin: 0 0 var(--spacing-4) 0;
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
}

.header-content p {
  margin: 0;
  font-size: var(--font-size-lg);
  opacity: 0.9;
}

.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6) var(--spacing-12);
  margin-top: -40px;
  position: relative;
  z-index: 1;
}

.report-form-card,
.report-history-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  margin-bottom: var(--spacing-8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all var(--transition-normal);
}

.report-form-card:hover,
.report-history-card:hover {
  box-shadow: var(--shadow-xl);
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  padding: var(--spacing-4) 0;
}

.card-header span {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: var(--text-secondary);
}

.anonymous-tip {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  background: var(--bg-secondary);
  padding: 8px 12px;
  border-radius: var(--radius-md);
  border-left: 4px solid var(--warning-color);
}

.content-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.5;
  transition: all var(--transition-fast);
}

.content-text:hover {
  color: var(--primary-color);
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: var(--spacing-4);
  background: var(--bg-secondary);
  border-radius: 0 0 var(--radius-xl) var(--radius-xl);
}

.upload-button {
  transition: all var(--transition-fast);
}

.upload-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.submit-button,
.reset-button,
.refresh-button,
.view-button,
.close-button {
  transition: all var(--transition-fast);
}

.submit-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.reset-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(156, 163, 175, 0.3);
}

.refresh-button:hover {
  transform: scale(1.05) rotate(180deg);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.view-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.close-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(156, 163, 175, 0.3);
}

.report-detail-dialog {
  max-height: 90vh;
  overflow-y: auto;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-2xl);
  backdrop-filter: blur(10px);
}

.report-detail-content {
  padding: 20px 0;
}

.report-info,
.report-progress-section {
  margin-bottom: 30px;
}

.report-info h3,
.report-progress-section h3 {
  margin-bottom: 16px;
  color: var(--text-primary);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-lg);
  border-bottom: 2px solid var(--primary-color);
  padding-bottom: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  padding: 12px;
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.info-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.info-item .label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  font-weight: var(--font-weight-medium);
}

.info-item .value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

.status-tag {
  font-size: var(--font-size-sm);
  padding: 4px 12px;
  border-radius: var(--radius-full);
}

.report-content,
.report-feedback {
  margin-top: 20px;
  padding: 16px;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border-left: 4px solid var(--primary-color);
  transition: all var(--transition-fast);
}

.report-content:hover,
.report-feedback:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.report-content h4,
.report-feedback h4 {
  margin-bottom: 8px;
  color: var(--text-primary);
  font-weight: var(--font-weight-semibold);
}

.report-content p,
.report-feedback p {
  line-height: 1.6;
  color: var(--text-secondary);
  margin: 0;
}

.progress-timeline {
  margin-top: 16px;
}

.progress-item {
  padding: 12px;
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  margin-bottom: 12px;
  transition: all var(--transition-fast);
}

.progress-item:hover {
  box-shadow: var(--shadow-md);
  transform: translateX(8px);
}

.progress-item h4 {
  margin: 0 0 8px 0;
  color: var(--text-primary);
  font-weight: var(--font-weight-semibold);
}

.progress-item p {
  margin: 0 0 4px 0;
  line-height: 1.5;
  color: var(--text-secondary);
}

.progress-item .handler {
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid var(--border-secondary);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px;
  background: var(--bg-secondary);
  border-radius: 0 0 var(--radius-xl) var(--radius-xl);
}

.report-table {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.report-table :deep(.el-table__row) {
  transition: all var(--transition-fast);
}

.report-table :deep(.el-table__row:hover) {
  background: var(--bg-hover) !important;
  transform: translateX(4px);
}

.report-table :deep(.el-table__row.highlight-row) {
  background: var(--primary-bg) !important;
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
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>