<template>
  <div class="register-page">
    <!-- 动态背景 -->
    <div class="register-bg">
      <div class="bg-gradient"></div>
      <div class="bg-mesh"></div>
      <div class="bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
      <div class="bg-grid"></div>
    </div>

    <!-- 注册容器 -->
    <div class="register-container">
      <div class="register-card-wrapper">
        <div class="register-card glass-card">
          <!-- 返回按钮 -->
          <button class="back-btn" @click="router.push('/login')">
            <el-icon><ArrowLeft /></el-icon>
            <span>返回登录</span>
          </button>

          <!-- Logo区域 -->
          <div class="register-header">
            <div class="logo-wrapper">
              <div class="logo-icon">
                <el-icon><UserPlus /></el-icon>
              </div>
              <div class="logo-ring"></div>
            </div>
            <h1>创建账号</h1>
            <p class="subtitle">加入我们，开启反诈学习之旅</p>
          </div>

          <!-- 步骤指示器 -->
          <div class="steps-indicator">
            <div class="step-item" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
              <div class="step-dot">1</div>
              <span>基本信息</span>
            </div>
            <div class="step-line" :class="{ active: currentStep > 1 }"></div>
            <div class="step-item" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
              <div class="step-dot">2</div>
              <span>身份验证</span>
            </div>
            <div class="step-line" :class="{ active: currentStep > 2 }"></div>
            <div class="step-item" :class="{ active: currentStep >= 3 }">
              <div class="step-dot">3</div>
              <span>完成注册</span>
            </div>
          </div>

          <!-- 注册表单 -->
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-position="top"
            class="register-form"
          >
            <!-- 步骤1: 基本信息 -->
            <div v-show="currentStep === 1" class="step-content">
              <el-form-item label="用户名" prop="username">
                <el-input
                  v-model="form.username"
                  placeholder="3-20个字符，可包含字母、数字、下划线"
                  size="large"
                  clearable
                  @blur="checkUsername"
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                  <template #suffix>
                    <el-icon v-if="usernameChecking" class="is-loading"><Loading /></el-icon>
                    <el-icon v-else-if="usernameAvailable === true" color="#10b981"><CircleCheck /></el-icon>
                    <el-icon v-else-if="usernameAvailable === false" color="#ef4444"><CircleClose /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item label="真实姓名" prop="realName">
                <el-input
                  v-model="form.realName"
                  placeholder="请输入真实姓名"
                  size="large"
                >
                  <template #prefix>
                    <el-icon><Avatar /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item label="学号/工号" prop="userNo">
                <el-input
                  v-model="form.userNo"
                  placeholder="请输入学号或工号"
                  size="large"
                >
                  <template #prefix>
                    <el-icon><Postcard /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item label="院系/部门" prop="department">
                <el-select
                  v-model="form.department"
                  placeholder="请选择院系或部门"
                  size="large"
                  class="w-full"
                >
                  <el-option label="计算机学院" value="cs" />
                  <el-option label="信息工程学院" value="ie" />
                  <el-option label="软件学院" value="se" />
                  <el-option label="网络空间安全学院" value="css" />
                  <el-option label="数学学院" value="math" />
                  <el-option label="物理学院" value="phys" />
                  <el-option label="外语学院" value="fl" />
                  <el-option label="经济管理学院" value="em" />
                  <el-option label="其他" value="other" />
                </el-select>
              </el-form-item>

              <el-button
                type="primary"
                size="large"
                class="step-btn"
                @click="nextStep"
              >
                <span>下一步</span>
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>

            <!-- 步骤2: 身份验证 -->
            <div v-show="currentStep === 2" class="step-content">
              <el-form-item label="手机号" prop="phone">
                <el-input
                  v-model="form.phone"
                  placeholder="请输入手机号"
                  size="large"
                  maxlength="11"
                >
                  <template #prefix>
                    <el-icon><Phone /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item label="验证码" prop="code">
                <el-input
                  v-model="form.code"
                  placeholder="请输入验证码"
                  size="large"
                  maxlength="6"
                  style="width: 60%"
                >
                  <template #prefix>
                    <el-icon><Key /></el-icon>
                  </template>
                </el-input>
                <el-button
                  type="primary"
                  size="large"
                  class="code-btn"
                  :disabled="!canSendCode || sendingCode"
                  @click="sendCode"
                >
                  {{ codeButtonText }}
                </el-button>
              </el-form-item>

              <el-form-item label="邮箱（选填）" prop="email">
                <el-input
                  v-model="form.email"
                  placeholder="用于找回密码和接收通知"
                  size="large"
                >
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <div class="step-actions">
                <el-button size="large" @click="prevStep">
                  <el-icon><ArrowLeft /></el-icon>
                  <span>上一步</span>
                </el-button>
                <el-button
                  type="primary"
                  size="large"
                  @click="nextStep"
                >
                  <span>下一步</span>
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>

            <!-- 步骤3: 设置密码 -->
            <div v-show="currentStep === 3" class="step-content">
              <el-form-item label="设置密码" prop="password">
                <el-input
                  v-model="form.password"
                  type="password"
                  placeholder="8-20个字符，包含大小写字母和数字"
                  size="large"
                  show-password
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="form.confirmPassword"
                  type="password"
                  placeholder="请再次输入密码"
                  size="large"
                  show-password
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <!-- 密码强度 -->
              <div class="password-strength" v-if="form.password">
                <div class="strength-label">密码强度：</div>
                <div class="strength-bar">
                  <div
                    class="strength-fill"
                    :style="{ width: passwordStrength.level * 25 + '%' }"
                    :class="'level-' + passwordStrength.level"
                  ></div>
                </div>
                <div class="strength-text" :class="'text-' + passwordStrength.level">
                  {{ passwordStrength.text }}
                </div>
              </div>

              <el-form-item>
                <el-checkbox v-model="agreedToTerms">
                  我已阅读并同意<a href="javascript:;" @click.prevent="showTerms = true">《用户协议》</a>
                  和<a href="javascript:;" @click.prevent="showPrivacy = true">《隐私政策》</a>
                </el-checkbox>
              </el-form-item>

              <div class="step-actions">
                <el-button size="large" @click="prevStep">
                  <el-icon><ArrowLeft /></el-icon>
                  <span>上一步</span>
                </el-button>
                <el-button
                  type="primary"
                  size="large"
                  :loading="loading"
                  :disabled="!agreedToTerms"
                  @click="handleRegister"
                >
                  <span>立即注册</span>
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>
          </el-form>

          <!-- 登录链接 -->
          <div class="login-link">
            <span>已有账号？</span>
            <router-link to="/login">立即登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, UserPlus, Lock, Avatar, Postcard, Phone, Key, Message,
  ArrowLeft, ArrowRight, Loading, CircleCheck, CircleClose
} from '@element-plus/icons-vue'

const router = useRouter()

const formRef = ref()
const currentStep = ref(1)
const loading = ref(false)
const usernameChecking = ref(false)
const usernameAvailable = ref<boolean | null>(null)
const canSendCode = ref(true)
const sendingCode = ref(false)
const codeButtonText = ref('获取验证码')
const agreedToTerms = ref(false)
const showTerms = ref(false)
const showPrivacy = ref(false)

const form = reactive({
  username: '',
  realName: '',
  userNo: '',
  department: '',
  phone: '',
  code: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  userNo: [
    { required: true, message: '请输入学号或工号', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择院系或部门', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度为8-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const passwordStrength = computed(() => {
  const password = form.password
  if (!password) return { level: 0, text: '' }

  let level = 0
  const checks = {
    length: password.length >= 8,
    lowercase: /[a-z]/.test(password),
    uppercase: /[A-Z]/.test(password),
    number: /\d/.test(password),
    special: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)
  }

  if (checks.length) level++
  if (checks.lowercase && checks.uppercase) level++
  if (checks.number) level++
  if (checks.special) level++

  const texts = ['', '弱', '中等', '强', '非常强']
  const colors = ['', 'text-danger', 'text-warning', 'text-success', 'text-success']

  return {
    level,
    text: texts[level],
    color: colors[level]
  }
})

const checkUsername = async () => {
  if (!form.username || form.username.length < 3) return

  usernameChecking.value = true
  usernameAvailable.value = null

  // 模拟检查
  await new Promise(resolve => setTimeout(resolve, 500))

  usernameAvailable.value = form.username !== 'admin'
  usernameChecking.value = false
}

const sendCode = async () => {
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }

  sendingCode.value = true
  let count = 60
  codeButtonText.value = `${count}秒后重试`

  const timer = setInterval(() => {
    count--
    if (count <= 0) {
      clearInterval(timer)
      codeButtonText.value = '获取验证码'
      canSendCode.value = true
    } else {
      codeButtonText.value = `${count}秒后重试`
      canSendCode.value = false
    }
  }, 1000)

  ElMessage.success('验证码已发送')
  sendingCode.value = false
}

const nextStep = async () => {
  if (currentStep.value === 1) {
    const validFields = ['username', 'realName', 'userNo', 'department']
    let isValid = true
    for (const field of validFields) {
      const item = formRef.value?.fields?.find(f => f.prop === field)
      if (item) {
        await item.validate()
          .then(() => {})
          .catch(() => { isValid = false })
      }
    }
    if (isValid) currentStep.value++
  } else if (currentStep.value === 2) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    if (!agreedToTerms.value) {
      ElMessage.warning('请先阅读并同意用户协议和隐私政策')
      return
    }

    loading.value = true

    try {
      // 模拟注册
      await new Promise(resolve => setTimeout(resolve, 1500))

      ElMessage.success({
        message: '注册成功！正在跳转到登录页...',
        duration: 2000
      })

      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } catch (error) {
      ElMessage.error('注册失败，请稍后重试')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
/* ==================== 页面布局 ========== */
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: var(--spacing-6);
}

/* ==================== 动态背景 ========== */
.register-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.register-bg .bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a5f 50%, #0f172a 100%);
}

.register-bg .bg-mesh {
  position: absolute;
  inset: 0;
  background: var(--gradient-mesh);
  opacity: 0.6;
}

.register-bg .bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.register-bg .bg-shapes {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.register-bg .shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
  animation: floatShape 25s ease-in-out infinite;
}

.register-bg .shape-1 {
  width: 500px;
  height: 500px;
  background: var(--primary-color);
  top: -150px;
  right: -100px;
}

.register-bg .shape-2 {
  width: 400px;
  height: 400px;
  background: var(--success-color);
  bottom: -100px;
  left: -100px;
  animation-delay: -8s;
}

.register-bg .shape-3 {
  width: 300px;
  height: 300px;
  background: var(--info-color);
  top: 50%;
  left: 50%;
  animation-delay: -15s;
}

@keyframes floatShape {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(30px, -30px) scale(1.1);
  }
}

/* ==================== 注册卡片 ========== */
.register-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 480px;
  animation: slideUp 0.6s ease forwards;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.register-card {
  background: var(--glass-bg-heavy);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-2xl);
  padding: var(--spacing-8);
  box-shadow: var(--shadow-2xl);
}

/* 返回按钮 */
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-3);
  background: transparent;
  border: none;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
  margin-bottom: var(--spacing-4);
  border-radius: var(--radius-md);
}

.back-btn:hover {
  color: var(--primary-color);
  background: var(--bg-hover);
}

/* Logo区域 */
.register-header {
  text-align: center;
  margin-bottom: var(--spacing-6);
}

.logo-wrapper {
  position: relative;
  width: 64px;
  height: 64px;
  margin: 0 auto var(--spacing-4);
}

.logo-icon {
  width: 64px;
  height: 64px;
  background: var(--gradient-primary);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  position: relative;
  z-index: 1;
  box-shadow: var(--shadow-primary);
}

.logo-ring {
  position: absolute;
  inset: -4px;
  border: 2px solid var(--primary-color);
  border-radius: var(--radius-lg);
  opacity: 0.3;
  animation: pulseRing 2s ease-in-out infinite;
}

@keyframes pulseRing {
  0%, 100% {
    transform: scale(1);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.5;
  }
}

.register-header h1 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.register-header .subtitle {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
}

/* 步骤指示器 */
.steps-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--spacing-8);
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-2);
}

.step-dot {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--bg-tertiary);
  border: 2px solid var(--border-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--text-muted);
  transition: all var(--transition-normal);
}

.step-item.active .step-dot {
  background: var(--gradient-primary);
  border-color: var(--primary-color);
  color: white;
  box-shadow: var(--shadow-primary);
}

.step-item.completed .step-dot {
  background: var(--success-color);
  border-color: var(--success-color);
  color: white;
}

.step-item span {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
  transition: color var(--transition-fast);
}

.step-item.active span {
  color: var(--primary-color);
  font-weight: var(--font-weight-medium);
}

.step-line {
  width: 60px;
  height: 2px;
  background: var(--border-primary);
  margin: 0 var(--spacing-2);
  margin-bottom: 20px;
  transition: background var(--transition-normal);
}

.step-line.active {
  background: var(--success-color);
}

/* 表单样式 */
.register-form {
  margin-bottom: var(--spacing-6);
}

.step-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.register-form :deep(.el-form-item) {
  margin-bottom: var(--spacing-5);
}

.register-form :deep(.el-form-item__label) {
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
  padding-bottom: var(--spacing-2);
}

.register-form :deep(.el-input__wrapper) {
  padding: var(--spacing-3) var(--spacing-4);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-primary);
  transition: all var(--transition-fast);
}

.register-form :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-light);
}

.register-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px var(--primary-bg), var(--shadow-sm);
}

.register-form :deep(.el-input__prefix .el-icon) {
  color: var(--text-muted);
}

.register-form :deep(.el-select__wrapper) {
  padding: var(--spacing-3) var(--spacing-4);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-primary);
}

.register-form :deep(.el-select__wrapper:hover) {
  border-color: var(--primary-light);
}

.register-form :deep(.el-select__wrapper.is-focused) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px var(--primary-bg), var(--shadow-sm);
}

.code-btn {
  margin-left: var(--spacing-3);
  width: 40%;
}

/* 密码强度 */
.password-strength {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-4);
  padding: var(--spacing-3);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
}

.strength-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  white-space: nowrap;
}

.strength-bar {
  flex: 1;
  height: 6px;
  background: var(--bg-tertiary);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: var(--radius-full);
  transition: all var(--transition-normal);
}

.strength-fill.level-1 { background: var(--danger-color); }
.strength-fill.level-2 { background: var(--warning-color); }
.strength-fill.level-3 { background: var(--success-color); }
.strength-fill.level-4 { background: var(--success-color); }

.strength-text {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  min-width: 50px;
  text-align: right;
}

.strength-text.text-1 { color: var(--danger-color); }
.strength-text.text-2 { color: var(--warning-color); }
.strength-text.text-3, .strength-text.text-4 { color: var(--success-color); }

/* 步骤按钮 */
.step-btn {
  width: 100%;
  height: 48px;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  background: var(--gradient-primary);
  border: none;
  border-radius: var(--radius-md);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-2);
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-primary);
  margin-top: var(--spacing-4);
}

.step-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.step-actions {
  display: flex;
  gap: var(--spacing-4);
  margin-top: var(--spacing-4);
}

.step-actions .el-button {
  flex: 1;
  height: 48px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-2);
  transition: all var(--transition-normal);
}

.step-actions .el-button--primary {
  background: var(--gradient-primary);
  border: none;
  color: white;
  box-shadow: var(--shadow-primary);
}

.step-actions .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

.register-form :deep(.el-checkbox__label) {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.register-form :deep(.el-checkbox__label a) {
  color: var(--primary-color);
  text-decoration: none;
}

/* 登录链接 */
.login-link {
  text-align: center;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.login-link a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  margin-left: var(--spacing-1);
  transition: color var(--transition-fast);
}

.login-link a:hover {
  color: var(--primary-dark);
}

/* 响应式 */
@media (max-width: 480px) {
  .register-page {
    padding: var(--spacing-4);
  }

  .register-card {
    padding: var(--spacing-6);
  }

  .step-line {
    width: 30px;
  }

  .code-btn {
    width: 45%;
  }
}
</style>
