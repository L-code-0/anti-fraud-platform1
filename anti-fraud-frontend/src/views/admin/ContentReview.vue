<template>
  <div class="content-review-page">
    <!-- 头部 -->
    <div class="header">
      <h1>内容审核工作流</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出审核记录
        </el-button>
      </div>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="审核状态">
          <el-select v-model="filterForm.status" placeholder="请选择" clearable>
            <el-option value="pending" label="待审核" />
            <el-option value="approved" label="已通过" />
            <el-option value="rejected" label="已拒绝" />
            <el-option value="processing" label="审核中" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容类型">
          <el-select v-model="filterForm.contentType" placeholder="请选择" clearable>
            <el-option value="knowledge" label="知识内容" />
            <el-option value="question" label="题库题目" />
            <el-option value="activity" label="活动" />
            <el-option value="report" label="举报" />
            <el-option value="work" label="作业" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核时间">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="审核人">
          <el-input v-model="filterForm.reviewer" placeholder="请输入审核人" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedItems.length > 0">
      <el-button type="primary" @click="batchApprove">批量通过</el-button>
      <el-button type="danger" @click="batchReject">批量拒绝</el-button>
      <el-button @click="selectedItems = []">取消选择</el-button>
    </div>
    
    <!-- 审核列表 -->
    <div class="review-list" v-loading="loading">
      <el-table :data="reviewItems" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="contentType" label="内容类型" width="120">
          <template #default="{ row }">
            {{ getContentTypeText(row.contentType) }}
          </template>
        </el-table-column>
        <el-table-column prop="contentTitle" label="内容标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="submitter" label="提交人" width="120" />
        <el-table-column prop="status" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentStage" label="当前阶段" width="100" />
        <el-table-column prop="reviewer" label="审核人" width="120" />
        <el-table-column prop="createdAt" label="提交时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="审核时间" width="180">
          <template #default="{ row }">
            {{ row.updatedAt ? formatTime(row.updatedAt) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleReview(row)" v-if="row.status === 'pending' || row.status === 'processing'">
              审核
            </el-button>
            <el-button text type="info" size="small" @click="handleViewDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 审核对话框 -->
    <el-dialog v-model="reviewVisible" title="内容审核" width="800px">
      <div class="review-dialog">
        <div class="review-info">
          <h3>{{ currentItem?.contentTitle }}</h3>
          <div class="info-row">
            <span class="label">内容类型：</span>
            <span>{{ getContentTypeText(currentItem?.contentType) }}</span>
          </div>
          <div class="info-row">
            <span class="label">提交人：</span>
            <span>{{ currentItem?.submitter }}</span>
          </div>
          <div class="info-row">
            <span class="label">提交时间：</span>
            <span>{{ formatTime(currentItem?.createdAt) }}</span>
          </div>
          <div class="info-row">
            <span class="label">当前阶段：</span>
            <span>{{ currentItem?.currentStage }}</span>
          </div>
        </div>

        <div class="content-preview">
          <h4>内容预览</h4>
          <div class="preview-content" v-html="currentItem?.contentPreview"></div>
        </div>

        <div class="review-history">
          <h4>审核历史</h4>
          <el-timeline>
            <el-timeline-item
              v-for="(history, index) in currentItem?.reviewHistory || []"
              :key="index"
              :timestamp="formatTime(history.timestamp)"
              :type="history.status === 'approved' ? 'success' : history.status === 'rejected' ? 'danger' : 'info'"
            >
              <div class="timeline-content">
                <div class="history-reviewer">{{ history.reviewer }} - {{ getStatusText(history.status) }}</div>
                <div class="history-comment" v-if="history.comment">{{ history.comment }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>

        <div class="review-form" v-if="currentItem?.status === 'pending' || currentItem?.status === 'processing'">
          <h4>审核操作</h4>
          <el-form :model="reviewForm">
            <el-form-item label="审核结果">
              <el-radio-group v-model="reviewForm.result">
                <el-radio value="approved">通过</el-radio>
                <el-radio value="rejected">拒绝</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="审核意见">
              <el-input
                v-model="reviewForm.comment"
                type="textarea"
                rows="4"
                placeholder="请输入审核意见"
              />
            </el-form-item>
            <el-form-item label="下一步审核人" v-if="reviewForm.result === 'approved' && currentItem?.currentStage !== '最终审核'">
              <el-select v-model="reviewForm.nextReviewer" placeholder="请选择下一步审核人">
                <el-option
                  v-for="reviewer in reviewers"
                  :key="reviewer.id"
                  :label="reviewer.name"
                  :value="reviewer.id"
                />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewVisible = false">取消</el-button>
          <el-button
            v-if="currentItem?.status === 'pending' || currentItem?.status === 'processing'"
            type="primary"
            @click="handleSubmitReview"
            :loading="submitting"
          >
            提交审核
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="审核详情" width="600px">
      <el-descriptions :column="2" border v-if="currentItem">
        <el-descriptions-item label="审核ID">{{ currentItem.id }}</el-descriptions-item>
        <el-descriptions-item label="内容类型">{{ getContentTypeText(currentItem.contentType) }}</el-descriptions-item>
        <el-descriptions-item label="内容标题" :span="2">{{ currentItem.contentTitle }}</el-descriptions-item>
        <el-descriptions-item label="提交人">{{ currentItem.submitter }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatTime(currentItem.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(currentItem.status)" size="small">
            {{ getStatusText(currentItem.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="当前阶段">{{ currentItem.currentStage }}</el-descriptions-item>
        <el-descriptions-item label="审核人" :span="2">{{ currentItem.reviewer }}</el-descriptions-item>
        <el-descriptions-item label="审核时间" :span="2">{{ currentItem.updatedAt ? formatTime(currentItem.updatedAt) : '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2">{{ currentItem.finalComment || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Download } from '@element-plus/icons-vue'

const loading = ref(false)
const submitting = ref(false)
const reviewVisible = ref(false)
const detailVisible = ref(false)
const currentItem = ref<any>(null)
const selectedItems = ref<any[]>([])

const filterForm = reactive({
  status: '',
  contentType: '',
  dateRange: [] as string[],
  reviewer: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

const reviewForm = reactive({
  result: '',
  comment: '',
  nextReviewer: ''
})

const reviewItems = ref<any[]>([])
const reviewers = ref([
  { id: 1, name: '审核员1' },
  { id: 2, name: '审核员2' },
  { id: 3, name: '管理员' }
])

function getContentTypeText(type: string): string {
  const textMap: Record<string, string> = {
    knowledge: '知识内容',
    question: '题库题目',
    activity: '活动',
    report: '举报',
    work: '作业'
  }
  return textMap[type] || type
}

function getStatusText(status: string): string {
  const textMap: Record<string, string> = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝',
    processing: '审核中'
  }
  return textMap[status] || status
}

function getStatusType(status: string): string {
  const typeMap: Record<string, string> = {
    pending: 'info',
    approved: 'success',
    rejected: 'danger',
    processing: 'warning'
  }
  return typeMap[status] || 'info'
}

function formatTime(timeStr: string): string {
  return new Date(timeStr).toLocaleString()
}

function handleSearch() {
  pagination.currentPage = 1
  fetchReviewItems()
}

function handleReset() {
  filterForm.status = ''
  filterForm.contentType = ''
  filterForm.dateRange = []
  filterForm.reviewer = ''
  pagination.currentPage = 1
  fetchReviewItems()
}

function handleRefresh() {
  fetchReviewItems()
  ElMessage.success('刷新成功')
}

function handleExport() {
  loading.value = true
  // 模拟导出功能
  setTimeout(() => {
    loading.value = false
    ElMessage.success('审核记录导出成功')
  }, 1000)
}

function handleReview(row: any) {
  currentItem.value = row
  reviewForm.result = ''
  reviewForm.comment = ''
  reviewForm.nextReviewer = ''
  reviewVisible.value = true
}

function handleViewDetail(row: any) {
  currentItem.value = row
  detailVisible.value = true
}

function handleSubmitReview() {
  if (!reviewForm.result) {
    ElMessage.warning('请选择审核结果')
    return
  }

  submitting.value = true
  // 模拟审核提交
  setTimeout(() => {
    submitting.value = false
    reviewVisible.value = false
    ElMessage.success('审核提交成功')
    fetchReviewItems()
  }, 1000)
}

function handleSizeChange(size: number) {
  pagination.pageSize = size
  fetchReviewItems()
}

function handleCurrentChange(page: number) {
  pagination.currentPage = page
  fetchReviewItems()
}

function handleSelectionChange(val: any[]) {
  selectedItems.value = val
}

function batchApprove() {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要操作的审核项')
    return
  }
  
  submitting.value = true
  // 模拟批量通过操作
  setTimeout(() => {
    submitting.value = false
    ElMessage.success('批量通过成功')
    selectedItems.value = []
    fetchReviewItems()
  }, 1000)
}

function batchReject() {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要操作的审核项')
    return
  }
  
  submitting.value = true
  // 模拟批量拒绝操作
  setTimeout(() => {
    submitting.value = false
    ElMessage.success('批量拒绝成功')
    selectedItems.value = []
    fetchReviewItems()
  }, 1000)
}

function fetchReviewItems() {
  loading.value = true
  // 模拟获取审核列表
  setTimeout(() => {
    reviewItems.value = [
      {
        id: 1,
        contentType: 'knowledge',
        contentTitle: '电信诈骗防范指南',
        submitter: 'user1',
        status: 'pending',
        currentStage: '初审',
        reviewer: '',
        createdAt: new Date(Date.now() - 3600000).toISOString(),
        updatedAt: null,
        finalComment: '',
        contentPreview: '<p>本文档介绍了电信诈骗的常见类型和防范措施...</p>',
        reviewHistory: []
      },
      {
        id: 2,
        contentType: 'question',
        contentTitle: '以下哪种行为属于电信诈骗？',
        submitter: 'user2',
        status: 'approved',
        currentStage: '最终审核',
        reviewer: '审核员2',
        createdAt: new Date(Date.now() - 7200000).toISOString(),
        updatedAt: new Date(Date.now() - 3600000).toISOString(),
        finalComment: '内容准确，通过审核',
        contentPreview: '<p>题目：以下哪种行为属于电信诈骗？</p><p>A. 电话通知中奖</p><p>B. 银行客服来电</p><p>C. 快递员上门取件</p><p>D. 朋友借钱</p>',
        reviewHistory: [
          {
            reviewer: '审核员1',
            status: 'approved',
            comment: '题目合理，建议通过',
            timestamp: new Date(Date.now() - 5400000).toISOString()
          },
          {
            reviewer: '审核员2',
            status: 'approved',
            comment: '内容准确，通过审核',
            timestamp: new Date(Date.now() - 3600000).toISOString()
          }
        ]
      },
      {
        id: 3,
        contentType: 'activity',
        contentTitle: '反诈知识竞赛',
        submitter: 'user3',
        status: 'rejected',
        currentStage: '初审',
        reviewer: '审核员1',
        createdAt: new Date(Date.now() - 10800000).toISOString(),
        updatedAt: new Date(Date.now() - 7200000).toISOString(),
        finalComment: '活动规则不清晰，需要修改',
        contentPreview: '<p>活动名称：反诈知识竞赛</p><p>活动时间：2026年4月15日</p><p>活动规则：...',
        reviewHistory: [
          {
            reviewer: '审核员1',
            status: 'rejected',
            comment: '活动规则不清晰，需要修改',
            timestamp: new Date(Date.now() - 7200000).toISOString()
          }
        ]
      },
      {
        id: 4,
        contentType: 'report',
        contentTitle: '举报可疑网站',
        submitter: 'user4',
        status: 'processing',
        currentStage: '复审',
        reviewer: '审核员2',
        createdAt: new Date(Date.now() - 14400000).toISOString(),
        updatedAt: new Date(Date.now() - 10800000).toISOString(),
        finalComment: '',
        contentPreview: '<p>举报内容：发现一个可疑的钓鱼网站，模仿银行官网</p><p>网站地址：www.fakebank.com</p>',
        reviewHistory: [
          {
            reviewer: '审核员1',
            status: 'approved',
            comment: '情况属实，转复审',
            timestamp: new Date(Date.now() - 10800000).toISOString()
          }
        ]
      },
      {
        id: 5,
        contentType: 'work',
        contentTitle: '反诈学习心得',
        submitter: 'user5',
        status: 'pending',
        currentStage: '初审',
        reviewer: '',
        createdAt: new Date(Date.now() - 18000000).toISOString(),
        updatedAt: null,
        finalComment: '',
        contentPreview: '<p>通过学习反诈知识，我了解到了很多防范诈骗的方法...</p>',
        reviewHistory: []
      }
    ]
    pagination.total = reviewItems.value.length
    loading.value = false
  }, 1000)
}

onMounted(() => {
  fetchReviewItems()
})
</script>

<style scoped>
.content-review-page {
  padding: 24px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header h1 {
  margin: 0;
  font-size: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-bar {
  padding: 20px;
  background: white;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.batch-actions {
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.review-list {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

.review-dialog {
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-height: 600px;
  overflow-y: auto;
}

.review-info {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
}

.review-info h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #333;
}

.info-row {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.info-row .label {
  font-weight: 500;
  margin-right: 8px;
  min-width: 80px;
}

.content-preview {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
}

.content-preview h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
}

.preview-content {
  line-height: 1.6;
  color: #606266;
}

.review-history h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
}

.timeline-content {
  padding: 8px 0;
}

.history-reviewer {
  font-weight: 500;
  margin-bottom: 4px;
}

.history-comment {
  font-size: 14px;
  color: #606266;
  line-height: 1.4;
}

.review-form h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .content-review-page {
    padding: 16px;
  }
  
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .filter-bar {
    padding: 16px;
  }
  
  .review-list {
    padding: 16px;
  }
}
</style>