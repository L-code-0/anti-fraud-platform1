<template>
  <div class="report-submit-page">
    <el-card>
      <template #header>
        <h2>提交举报</h2>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="举报类型" prop="reportType">
          <el-radio-group v-model="form.reportType">
            <el-radio :value="1">可疑电话</el-radio>
            <el-radio :value="2">可疑短信</el-radio>
            <el-radio :value="3">可疑链接</el-radio>
            <el-radio :value="4">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="诈骗类型" prop="fraudType">
          <el-select v-model="form.fraudType" placeholder="请选择诈骗类型">
            <el-option value="电信诈骗" />
            <el-option value="网络诈骗" />
            <el-option value="校园贷诈骗" />
            <el-option value="兼职诈骗" />
            <el-option value="冒充诈骗" />
            <el-option value="投资理财诈骗" />
            <el-option value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入举报标题" />
        </el-form-item>
        
        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="5"
            placeholder="请详细描述诈骗情况，包括时间、地点、经过等..."
          />
        </el-form-item>
        
        <el-form-item label="可疑号码">
          <el-input v-model="form.phoneNumber" placeholder="请输入可疑电话号码" />
        </el-form-item>
        
        <el-form-item label="可疑链接">
          <el-input v-model="form.linkUrl" placeholder="请输入可疑链接地址" />
        </el-form-item>
        
        <el-form-item label="联系人">
          <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        
        <el-form-item label="联系电话">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="匿名举报">
          <el-switch v-model="form.isAnonymous" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            提交举报
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()

const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive({
  reportType: 1,
  fraudType: '',
  title: '',
  description: '',
  phoneNumber: '',
  linkUrl: '',
  contactName: '',
  contactPhone: '',
  isAnonymous: false
})

const rules: FormRules = {
  reportType: [{ required: true, message: '请选择举报类型', trigger: 'change' }],
  fraudType: [{ required: true, message: '请选择诈骗类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入举报标题', trigger: 'blur' }],
  description: [{ required: true, message: '请详细描述诈骗情况', trigger: 'blur' }]
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    await post('/report/submit', form)
    ElMessage.success('举报提交成功，感谢您的参与')
    router.push('/report')
  } catch (e) {}
  submitting.value = false
}
</script>

<style scoped>
.report-submit-page {
  max-width: 800px;
  margin: 0 auto;
}
</style>
