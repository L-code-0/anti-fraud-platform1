<template>
  <div class="feedback-page">
    <!-- 头部 -->
    <div class="header">
      <h1>意见反馈</h1>
      <el-button type="primary" @click="handleOpenSubmit">
        <el-icon><Edit /></el-icon>
        提交反馈
      </el-button>
    </div>

    <!-- 反馈统计 -->
    <div class="stats-card">
      <div class="stat-item">
        <div class="stat-icon blue">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ feedbackStore.feedbacks.length }}</span>
          <span class="stat-label">我的反馈</span>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon orange">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ pendingCount }}</span>
          <span class="stat-label">待处理</span>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon green">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ resolvedCount }}</span>
          <span class="stat-label">已解决</span>
        </div>
      </div>
    </div>

    <!-- 反馈列表 -->
    <div class="feedback-list" v-loading="loading">
      <template v-if="feedbackStore.feedbacks.length > 0">
        <div 
          v-for="item in feedbackStore.feedbacks" 
          :key="item.id"
          class="feedback-item"
        >
          <div class="feedback-header">
            <span class="feedback-type" :class="`type-${item.type}`">
              {{ getTypeText(item.type) }}
            </span>
            <span class="feedback-status" :class="`status-${item.status}`">
              {{ getStatusText(item.status) }}
            </span>
            <span class="feedback-time">{{ formatTime(item.createdAt) }}</span>
          </div>
          <p class="feedback-content">{{ item.content }}</p>
          
          <!-- 图片预览 -->
          <div v-if="item.images && item.images.length > 0" class="feedback-images">
            <el-image 
              v-for="(img, index) in item.images" 
              :key="index"
              :src="img"
              :preview-src-list="item.images"
              fit="cover"
              class="feedback-image"
            />
          </div>
          
          <!-- 回复内容 -->
          <div v-if="item.reply" class="feedback-reply">
            <div class="reply-header">
              <el-icon color="#67c23a"><ChatLineSquare /></el-icon>
              <span>官方回复</span>
              <span class="reply-time">{{ formatTime(item.repliedAt) }}</span>
            </div>
            <p class="reply-content">{{ item.reply }}</p>
          </div>
          
          <div class="feedback-actions">
            <el-button text size="small" @click="handleDelete(item.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </template>
      <el-empty v-else description="暂无反馈记录" />
    </div>

    <!-- 提交对话框 -->
    <el-dialog
      v-model="submitVisible"
      title="提交反馈"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form 
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="反馈类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择反馈类型" style="width: 100%">
            <el-option value="suggestion" label="功能建议" />
            <el-option value="bug" label="系统问题" />
            <el-option value="complaint" label="投诉举报" />
            <el-option value="other" label="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="反馈内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="5"
            placeholder="请详细描述您的问题或建议..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="上传图片">
          <el-upload
            v-model:file-list="form.images"
            action="/api/upload"
            list-type="picture-card"
            :before-upload="beforeImageUpload"
            :on-remove="handleImageRemove"
            :on-success="handleImageSuccess"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传3张图片，每张不超过5MB</div>
        </el-form-item>
        
        <el-form-item label="联系方式">
          <el-input
            v-model="form.contact"
            placeholder="选填，便于我们联系您"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="submitVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          提交反馈
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Edit, ChatDotRound, Clock, CircleCheck, Delete, ChatLineSquare, Plus } from '@element-plus/icons-vue'
import { useFeedbackStore } from '@/stores/feedback'
import type { Feedback, FeedbackType, FeedbackStatus } from '@/types'

const feedbackStore = useFeedbackStore()

const formRef = ref<FormInstance>()
const submitVisible = ref(false)
const submitting = ref(false)

const form = ref({
  type: 'suggestion' as FeedbackType,
  content: '',
  images: [] as any[],
  contact: ''
})

const rules: FormRules = {
  type: [{ required: true, message: '请选择反馈类型', trigger: 'change' }],
  content: [
    { required: true, message: '请输入反馈内容', trigger: 'blur' },
    { min: 10, message: '内容至少10个字符', trigger: 'blur' }
  ]
}

const loading = computed(() => feedbackStore.loading)

const pendingCount = computed(() => 
  feedbackStore.feedbacks.filter(f => f.status === 'pending' || f.status === 'processing').length
)

const resolvedCount = computed(() => 
  feedbackStore.feedbacks.filter(f => f.status === 'resolved').length
)

function getTypeText(type: FeedbackType): string {
  const textMap: Record<FeedbackType, string> = {
    suggestion: '功能建议',
    bug: '系统问题',
    complaint: '投诉举报',
    other: '其他'
  }
  return textMap[type] || '其他'
}

function getStatusText(status: FeedbackStatus): string {
  const textMap: Record<FeedbackStatus, string> = {
    pending: '待处理',
    processing: '处理中',
    resolved: '已解决',
    rejected: '已驳回'
  }
  return textMap[status] || '未知'
}

function formatTime(timeStr?: string): string {
  if (!timeStr) return ''
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return time.toLocaleDateString()
}

function handleOpenSubmit() {
  form.value = {
    type: 'suggestion',
    content: '',
    images: [],
    contact: ''
  }
  submitVisible.value = true
}

function beforeImageUpload(file: File): boolean {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  if (form.value.images.length >= 3) {
    ElMessage.error('最多上传3张图片')
    return false
  }
  return true
}

function handleImageRemove() {
  // 处理图片移除
}

function handleImageSuccess() {
  // 处理图片上传成功
}

async function handleSubmit() {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const result = await feedbackStore.submitFeedback({
        type: form.value.type,
        content: form.value.content,
        images: form.value.images.map((i: any) => i.url),
        contact: form.value.contact
      })
      
      if (result.success) {
        ElMessage.success(result.message)
        submitVisible.value = false
      }
    }
  })
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除这条反馈吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await feedbackStore.deleteFeedback(id)
    ElMessage.success('删除成功')
  } catch {
    // 取消删除
  }
}

onMounted(() => {
  feedbackStore.fetchMyFeedbacks()
})
</script>

<style scoped>
.feedback-page {
  max-width: 900px;
  margin: 0 auto;
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

.stats-card {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.blue { background: #ecf5ff; color: #409eff; }
.stat-icon.orange { background: #fdf6ec; color: #e6a23c; }
.stat-icon.green { background: #f0f9eb; color: #67c23a; }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.feedback-list {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.feedback-item {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.feedback-item:last-child {
  border-bottom: none;
}

.feedback-item:hover {
  background-color: #fafafa;
}

.feedback-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.feedback-type {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 4px;
}

.type-suggestion { background: #ecf5ff; color: #409eff; }
.type-bug { background: #fdf6ec; color: #e6a23c; }
.type-complaint { background: #fef0f0; color: #f56c6c; }
.type-other { background: #f4f4f5; color: #909399; }

.feedback-status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 4px;
}

.status-pending { background: #fdf6ec; color: #e6a23c; }
.status-processing { background: #ecf5ff; color: #409eff; }
.status-resolved { background: #f0f9eb; color: #67c23a; }
.status-rejected { background: #fef0f0; color: #f56c6c; }

.feedback-time {
  font-size: 12px;
  color: #999;
  margin-left: auto;
}

.feedback-content {
  margin: 0 0 12px;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.feedback-images {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.feedback-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
}

.feedback-reply {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #666;
}

.reply-time {
  margin-left: auto;
  font-size: 12px;
  color: #999;
}

.reply-content {
  margin: 0;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.feedback-actions {
  display: flex;
  justify-content: flex-end;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

@media (max-width: 768px) {
  .feedback-page {
    padding: 16px;
  }
  
  .stats-card {
    flex-direction: column;
    gap: 16px;
  }
  
  .feedback-header {
    flex-wrap: wrap;
  }
  
  .feedback-time {
    margin-left: 0;
    width: 100%;
    margin-top: 4px;
  }
}
</style>
