<template>
  <div class="register-page">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <h2>用户注册</h2>
          <p class="subtitle">创建账号，开启反诈学习之旅</p>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleRegister"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                prefix-icon="User"
                size="large"
                @blur="checkUsername"
              >
                <template #suffix>
                  <el-icon v-if="usernameChecking" class="is-loading"><Loading /></el-icon>
                  <el-icon v-else-if="usernameAvailable === true" color="#38a169"><CircleCheck /></el-icon>
                  <el-icon v-else-if="usernameAvailable === false" color="#c53030"><CircleClose /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input
                v-model="form.realName"
                placeholder="请输入真实姓名"
                prefix-icon="Avatar"
                size="large"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            size="large"
            maxlength="11"
          >
            <template #suffix>
              <el-button 
                text 
                type="primary" 
                size="small" 
                :disabled="!canSendCode || sendingCode"
                @click="sendCode"
              >
                {{ codeButtonText }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="验证码" prop="code" v-if="showCodeInput">
          <el-input
            v-model="form.code"
            placeholder="请输入验证码"
            prefix-icon="Key"
            size="large"
            maxlength="6"
          />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱（选填）"
            prefix-icon="Message"
            size="large"
          />
        </el-form-item>
        
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学号/工号" prop="userNo">
              <el-input
                v-model="form.userNo"
                placeholder="请输入学号/工号"
                prefix-icon="Postcard"
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属院系" prop="departmentId">
              <el-select v-model="form.departmentId" placeholder="请选择院系" size="large" style="width: 100%">
                <el-option label="计算机学院" :value="1" />
                <el-option label="商学院" :value="2" />
                <el-option label="外国语学院" :value="3" />
                <el-option label="艺术学院" :value="4" />
                <el-option label="其他" :value="99" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @input="checkPasswordStrength"
          />
          <!-- 密码强度指示器 -->
          <div class="password-strength" v-if="form.password">
            <div class="strength-bar">
              <span 
                v-for="i in 4" 
                :key="i" 
                class="strength-segment"
                :class="{ active: passwordStrength.score >= i }"
                :style="{ backgroundColor: passwordStrength.score >= i ? passwordStrength.color : '' }"
              ></span>
            </div>
            <span class="strength-text" :style="{ color: passwordStrength.color }">
              {{ passwordStrength.text }}
            </span>
          </div>
          <div class="password-tips">
            <span :class="{ active: passwordChecks.length }">
              <el-icon v-if="passwordChecks.length"><CircleCheck /></el-icon>
              至少8个字符
            </span>
            <span :class="{ active: passwordChecks.hasNumber }">
              <el-icon v-if="passwordChecks.hasNumber"><CircleCheck /></el-icon>
              包含数字
            </span>
            <span :class="{ active: passwordChecks.hasLower }">
              <el-icon v-if="passwordChecks.hasLower"><CircleCheck /></el-icon>
              包含小写字母
            </span>
            <span :class="{ active: passwordChecks.hasUpper }">
              <el-icon v-if="passwordChecks.hasUpper"><CircleCheck /></el-icon>
              包含大写字母
            </span>
          </div>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="agreement">
          <el-checkbox v-model="form.agreement">
            我已阅读并同意
            <el-link type="primary">《用户协议》</el-link>
            和
            <el-link type="primary">《隐私政策》</el-link>
          </el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRegister"
            style="width: 100%"
          >
            注册
          </el-button>
        </el-form-item>
        
        <div class="register-footer">
          已有账号？
          <router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const usernameChecking = ref(false)
const usernameAvailable = ref<boolean | null>(null)
const sendingCode = ref(false)
const countdown = ref(0)
const showCodeInput = ref(false)

const form = reactive({
  username: '',
  realName: '',
  phone: '',
  code: '',
  email: '',
  userNo: '',
  departmentId: null as number | null,
  password: '',
  confirmPassword: '',
  agreement: false
})

const passwordStrength = reactive({
  score: 0,
  text: '',
  color: ''
})

const passwordChecks = reactive({
  length: false,
  hasNumber: false,
  hasLower: false,
  hasUpper: false
})

// 验证用户名是否已存在
const checkUsername = async () => {
  if (!form.username || form.username.length < 3) {
    usernameAvailable.value = null
    return
  }
  
  usernameChecking.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    // 实际项目中应该调用后端接口
    // const res = await get(`/auth/check-username?username=${form.username}`)
    // usernameAvailable.value = res.data.available
    usernameAvailable.value = true
  } catch (e) {
    usernameAvailable.value = false
  } finally {
    usernameChecking.value = false
  }
}

// 检查密码强度
const checkPasswordStrength = () => {
  const pwd = form.password
  
  passwordChecks.length = pwd.length >= 8
  passwordChecks.hasNumber = /\d/.test(pwd)
  passwordChecks.hasLower = /[a-z]/.test(pwd)
  passwordChecks.hasUpper = /[A-Z]/.test(pwd)
  
  const score = Object.values(passwordChecks).filter(Boolean).length
  passwordStrength.score = score
  
  if (score === 0) {
    passwordStrength.text = ''
    passwordStrength.color = ''
  } else if (score === 1) {
    passwordStrength.text = '弱'
    passwordStrength.color = '#c53030'
  } else if (score === 2) {
    passwordStrength.text = '一般'
    passwordStrength.color = '#dd6b20'
  } else if (score === 3) {
    passwordStrength.text = '较强'
    passwordStrength.color = '#d69e2e'
  } else {
    passwordStrength.text = '强'
    passwordStrength.color = '#38a169'
  }
}

// 发送验证码
const canSendCode = computed(() => {
  return /^1[3-9]\d{9}$/.test(form.phone)
})

const codeButtonText = computed(() => {
  return countdown.value > 0 ? `${countdown.value}秒后重试` : '获取验证码'
})

const sendCode = async () => {
  if (!canSendCode.value || sendingCode.value) return
  
  sendingCode.value = true
  try {
    // 模拟发送验证码
    await new Promise(resolve => setTimeout(resolve, 1000))
    showCodeInput.value = true
    ElMessage.success('验证码已发送')
    
    // 开始倒计时
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (e) {
    ElMessage.error('发送失败，请重试')
  } finally {
    sendingCode.value = false
  }
}

// 自定义验证规则
const validateUsername = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (value.length < 3 || value.length > 20) {
    callback(new Error('用户名长度为3-20个字符'))
  } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线'))
  } else if (usernameAvailable.value === false) {
    callback(new Error('用户名已存在'))
  } else {
    callback()
  }
}

const validatePhone = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const validatePassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 8) {
    callback(new Error('密码长度至少8个字符'))
  } else if (passwordStrength.score < 2) {
    callback(new Error('密码强度不足'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateAgreement = (rule: any, value: boolean, callback: any) => {
  if (!value) {
    callback(new Error('请阅读并同意用户协议'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  username: [
    { required: true, validator: validateUsername, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreement: [
    { validator: validateAgreement, trigger: 'change' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await userStore.register({
      username: form.username,
      password: form.password,
      realName: form.realName,
      phone: form.phone,
      roleId: 1 // 默认学生角色
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // 错误已在请求拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: calc(100vh - 200px);
  padding: 40px 20px;
}

.register-card {
  width: 100%;
  max-width: 600px;
  border-radius: var(--radius-lg);
}

.card-header {
  text-align: center;
}

.card-header h2 {
  color: var(--color-text-primary);
  margin-bottom: 8px;
  font-size: 24px;
}

.card-header .subtitle {
  color: var(--color-text-muted);
  font-size: 14px;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
}

.strength-bar {
  display: flex;
  gap: 4px;
  flex: 1;
}

.strength-segment {
  flex: 1;
  height: 4px;
  background-color: var(--color-bg-active);
  border-radius: 2px;
  transition: background-color var(--transition-fast);
}

.strength-segment.active {
  background-color: currentColor;
}

.strength-text {
  font-size: 12px;
  font-weight: var(--font-weight-medium);
}

.password-tips {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
}

.password-tips span {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--color-text-muted);
  transition: color var(--transition-fast);
}

.password-tips span.active {
  color: var(--color-success);
}

.register-footer {
  text-align: center;
  color: var(--color-text-muted);
  font-size: 14px;
}

.register-footer a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
}

.register-footer a:hover {
  text-decoration: underline;
}

:deep(.el-checkbox__label) {
  font-size: 13px;
  color: var(--color-text-secondary);
}

:deep(.el-link) {
  font-size: 13px;
}
</style>
