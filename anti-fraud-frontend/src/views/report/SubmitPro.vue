<template>
  <div class="report-submit">
    <el-card class="page-header-card">
      <div class="header-info">
        <h1>举报诈骗信息</h1>
        <p>您的举报信息将有助于打击诈骗犯罪，保护更多人免受侵害</p>
      </div>
    </el-card>

    <el-card class="form-card">
      <el-steps :active="currentStep" finish-status="success" class="steps">
        <el-step title="选择类型" />
        <el-step title="填写信息" />
        <el-step title="上传证据" />
        <el-step title="确认提交" />
      </el-steps>

      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" class="report-form">
        <!-- Step 1: 选择类型 -->
        <div v-show="currentStep === 0" class="step-content">
          <h3>请选择诈骗类型</h3>
          <div class="type-grid">
            <div v-for="type in fraudTypes" :key="type.value" 
              class="type-card" :class="{ 'selected': formData.type === type.value }"
              @click="formData.type = type.value">
              <div class="type-icon" :style="{ background: type.color }">
                <el-icon :size="32"><component :is="type.icon" /></el-icon>
              </div>
              <div class="type-info">
                <span class="type-name">{{ type.label }}</span>
                <span class="type-desc">{{ type.description }}</span>
              </div>
              <el-icon v-if="formData.type === type.value" class="check-icon"><Check /></el-icon>
            </div>
          </div>
        </div>

        <!-- Step 2: 填写信息 -->
        <div v-show="currentStep === 1" class="step-content">
          <el-form-item label="诈骗标题" prop="title">
            <el-input v-model="formData.title" placeholder="请简要描述诈骗情况" maxlength="100" show-word-limit />
          </el-form-item>
          <el-form-item label="诈骗详情" prop="description">
            <el-input v-model="formData.description" type="textarea" :rows="6" placeholder="请详细描述诈骗经过，包括时间、地点、方式、涉及金额等信息" maxlength="1000" show-word-limit />
          </el-form-item>
          <el-form-item label="发生地点" prop="location">
            <el-input v-model="formData.location" placeholder="请输入诈骗发生地点或平台" />
          </el-form-item>
          <el-form-item label="发生时间" prop="occurTime">
            <el-date-picker v-model="formData.occurTime" type="datetime" placeholder="选择诈骗发生时间" style="width: 100%" />
          </el-form-item>
          <el-form-item label="涉案金额" v-if="formData.type === 2">
            <el-input-number v-model="formData.amount" :min="0" :precision="2" :step="100" placeholder="请输入涉案金额" style="width: 100%" />
          </el-form-item>
          <el-form-item label="对方账号">
            <el-input v-model="formData.fraudAccount" placeholder="请输入诈骗者账号（选填）" />
          </el-form-item>
          <el-form-item label="您的联系方式" prop="contact">
            <el-input v-model="formData.contact" placeholder="请输入联系方式（手机号或邮箱）" />
          </el-form-item>
        </div>

        <!-- Step 3: 上传证据 -->
        <div v-show="currentStep === 2" class="step-content">
          <el-form-item label="上传证据">
            <div class="upload-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>请上传相关证据材料，如聊天记录截图、转账凭证、电话录音等</span>
            </div>
            <el-upload
              v-model:file-list="fileList"
              action="#"
              :auto-upload="false"
              list-type="picture-card"
              :on-preview="handlePreview"
              :on-remove="handleRemove"
              :limit="9"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="补充说明">
            <el-input v-model="formData.supplement" type="textarea" :rows="4" placeholder="如有其他补充说明，请在此填写" maxlength="500" show-word-limit />
          </el-form-item>
        </div>

        <!-- Step 4: 确认提交 -->
        <div v-show="currentStep === 3" class="step-content">
          <div class="confirm-summary">
            <h3>举报信息确认</h3>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="诈骗类型">{{ getTypeName(formData.type) }}</el-descriptions-item>
              <el-descriptions-item label="诈骗标题">{{ formData.title }}</el-descriptions-item>
              <el-descriptions-item label="发生地点">{{ formData.location }}</el-descriptions-item>
              <el-descriptions-item label="发生时间">{{ formData.occurTime }}</el-descriptions-item>
              <el-descriptions-item label="涉案金额">{{ formData.amount ? formData.amount + '元' : '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="证据数量">{{ fileList.length }} 个文件</el-descriptions-item>
            </el-descriptions>
            
            <el-alert type="info" :closable="false" class="anonymous-tip">
              <template #title>
                <div class="anonymous-option">
                  <el-checkbox v-model="formData.isAnonymous">匿名举报</el-checkbox>
                  <el-tooltip content="匿名举报将隐藏您的个人信息，但可能影响后续追踪">
                    <el-icon><QuestionFilled /></el-icon>
                  </el-tooltip>
                </div>
              </template>
              <p>匿名举报可保护您的隐私，但建议留下联系方式以便警方核实情况。</p>
            </el-alert>
          </div>
        </div>

        <div class="form-actions">
          <el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
          <el-button v-if="currentStep < 3" type="primary" @click="nextStep">下一步</el-button>
          <el-button v-if="currentStep === 3" type="primary" @click="handleSubmit" :loading="submitting">
            提交举报
          </el-button>
        </div>
      </el-form>
    </el-card>

    <el-dialog v-model="previewVisible" title="预览" width="60%">
      <img :src="previewUrl" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules, type UploadUserFile } from 'element-plus'
import { Plus, Check, InfoFilled, QuestionFilled, Phone, Monitor, Mail, User, Money } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const currentStep = ref(0)
const submitting = ref(false)
const fileList = ref<UploadUserFile[]>([])
const previewVisible = ref(false)
const previewUrl = ref('')

const formData = reactive({
  type: null as number | null,
  title: '',
  description: '',
  location: '',
  occurTime: '',
  amount: null as number | null,
  fraudAccount: '',
  contact: '',
  supplement: '',
  isAnonymous: false
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入诈骗标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入诈骗详情', trigger: 'blur' }],
  location: [{ required: true, message: '请输入发生地点', trigger: 'blur' }],
  occurTime: [{ required: true, message: '请选择发生时间', trigger: 'change' }],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$|^[\w.-]+@[\w.-]+\.\w+$/, message: '请输入正确的手机号或邮箱', trigger: 'blur' }
  ]
}

const fraudTypes = [
  { value: 1, label: '电话诈骗', description: '冒充客服、公检法等电话诈骗', icon: Phone, color: '#ef4444' },
  { value: 2, label: '网络诈骗', description: '虚假网站、刷单返利等网络诈骗', icon: Monitor, color: '#f97316' },
  { value: 3, label: '短信诈骗', description: '钓鱼短信、积分兑换等诈骗', icon: Mail, color: '#eab308' },
  { value: 4, label: '情感诈骗', description: '杀猪盘、网恋诈骗等', icon: User, color: '#22c55e' },
  { value: 5, label: '投资诈骗', description: '虚假投资、非法集资等', icon: Money, color: '#3b82f6' },
  { value: 6, label: '其他诈骗', description: '其他类型的诈骗行为', icon: Monitor, color: '#6b7280' }
]

const getTypeName = (type: number | null) => {
  if (!type) return ''
  return fraudTypes.find(t => t.value === type)?.label || ''
}

const handlePreview = (file: UploadUserFile) => {
  previewUrl.value = file.url || ''
  previewVisible.value = true
}

const handleRemove = (file: UploadUserFile) => {
  console.log(file)
}

const nextStep = async () => {
  if (currentStep.value === 0) {
    if (!formData.type) {
      ElMessage.warning('请选择诈骗类型')
      return
    }
  } else if (currentStep.value === 1) {
    if (!formRef.value) return
    await formRef.value.validateField(['title', 'description', 'location', 'occurTime', 'contact'], () => {})
  }
  currentStep.value++
}

const prevStep = () => {
  if (currentStep.value > 0) currentStep.value--
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1500))
    ElMessage.success('举报提交成功！您的举报编号：' + Math.random().toString().slice(2, 10))
    router.push('/report')
  } catch (e) {
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.report-submit { max-width: 900px; margin: 0 auto; }
.page-header-card { margin-bottom: var(--spacing-6); }
.header-info h1 { margin: 0 0 var(--spacing-2); font-size: var(--font-size-3xl); }
.header-info p { margin: 0; color: var(--text-secondary); }
.form-card { padding: var(--spacing-6); }
.steps { margin-bottom: var(--spacing-8); }
.step-content h3 { margin-bottom: var(--spacing-4); color: var(--text-primary); }
.type-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: var(--spacing-4); }
.type-card { display: flex; align-items: center; gap: var(--spacing-4); padding: var(--spacing-4); border: 2px solid var(--border-color); border-radius: var(--radius-lg); cursor: pointer; transition: all var(--transition-fast); }
.type-card:hover { border-color: var(--primary-color); background: var(--hover-bg-color); }
.type-card.selected { border-color: var(--primary-color); background: rgba(64, 158, 255, 0.08); }
.type-icon { width: 56px; height: 56px; border-radius: var(--radius-lg); display: flex; align-items: center; justify-content: center; color: white; }
.type-info { flex: 1; }
.type-name { display: block; font-weight: var(--font-weight-semibold); margin-bottom: var(--spacing-1); }
.type-desc { display: block; font-size: var(--font-size-sm); color: var(--text-secondary); }
.check-icon { color: var(--primary-color); font-size: 20px; }
.upload-tip { display: flex; align-items: center; gap: var(--spacing-2); padding: var(--spacing-3); background: var(--bg-page); border-radius: var(--radius-md); margin-bottom: var(--spacing-4); color: var(--text-secondary); font-size: var(--font-size-sm); }
.confirm-summary h3 { margin-bottom: var(--spacing-4); }
.anonymous-tip { margin-top: var(--spacing-4); }
.anonymous-option { display: flex; align-items: center; gap: var(--spacing-2); }
.form-actions { display: flex; justify-content: center; gap: var(--spacing-4); margin-top: var(--spacing-8); }
@media (max-width: 768px) {
  .type-grid { grid-template-columns: 1fr; }
}
</style>
