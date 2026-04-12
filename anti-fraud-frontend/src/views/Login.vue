<template>
  <div class="login-page">
    <!-- 动态背景 -->
    <div class="login-bg">
      <div class="bg-gradient"></div>
      <div class="bg-mesh"></div>
      <div class="bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
      </div>
      <div class="bg-particles">
        <span v-for="i in 30" :key="i" class="particle" :style="getParticleStyle(i)"></span>
      </div>
      <div class="bg-grid"></div>
    </div>
    
    <!-- 登录容器 -->
    <div class="login-container">
      <!-- 左侧介绍 -->
      <div class="login-intro" v-if="!isMobile">
        <div class="intro-content">
          <div class="intro-badge">
            <el-icon><Lock /></el-icon>
            <span>安全可信</span>
          </div>
          <h1 class="intro-title">高校反诈安全知识普及平台</h1>
          <p class="intro-desc">
            汇聚专业反诈知识，通过学、测、练、报四位一体的方式，
            全面提升高校师生的防骗意识和能力
          </p>
          
          <div class="intro-features">
            <div class="feature-item" v-for="feature in introFeatures" :key="feature.title">
              <div class="feature-icon" :style="{ background: feature.gradient }">
                <el-icon><component :is="feature.icon" /></el-icon>
              </div>
              <div class="feature-text">
                <h4>{{ feature.title }}</h4>
                <p>{{ feature.desc }}</p>
              </div>
            </div>
          </div>
          
          <div class="intro-stats">
            <div class="stat-box">
              <span class="stat-num">10K+</span>
              <span class="stat-label">学习用户</span>
            </div>
            <div class="stat-box">
              <span class="stat-num">500+</span>
              <span class="stat-label">知识文章</span>
            </div>
            <div class="stat-box">
              <span class="stat-num">95%</span>
              <span class="stat-label">防护效果</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 登录卡片 -->
      <div class="login-card-wrapper">
        <div class="login-card glass-card" :class="{ 'shake': hasError }">
          <!-- Logo区域 -->
          <div class="login-header">
            <div class="logo-wrapper">
              <div class="logo-icon">
                <el-icon><Lock /></el-icon>
              </div>
              <div class="logo-ring"></div>
            </div>
            <h1>欢迎回来</h1>
            <p class="subtitle">登录您的账户，开启反诈学习之旅</p>
          </div>
          
          <!-- 登录表单 -->
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-position="top"
            class="login-form"
            @submit.prevent="handleLogin"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                size="large"
                clearable
                @focus="focusedField = 'username'"
                @blur="focusedField = ''; onUsernameBlur()"
                @input="onUsernameInput"
              >
                <template #prefix>
                  <el-icon :class="{ 'focused': focusedField === 'username' }"><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                show-password
                autocomplete="new-password"
                @focus="focusedField = 'password'"
                @blur="focusedField = ''"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <el-icon :class="{ 'focused': focusedField === 'password' }"><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <!-- 验证码 -->
            <el-form-item label="验证码" prop="captchaCode">
              <div class="captcha-row">
                <el-input
                  v-model="form.captchaCode"
                  placeholder="请输入验证码"
                  size="large"
                  class="captcha-input"
                  @keyup.enter="handleLogin"
                  maxlength="4"
                >
                  <template #prefix>
                    <el-icon><Key /></el-icon>
                  </template>
                </el-input>
                <div class="captcha-image" @click="refreshCaptcha" title="点击刷新验证码">
                  <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
                  <el-icon v-else class="is-loading"><Loading /></el-icon>
                </div>
              </div>
            </el-form-item>
            
            <!-- 安全提示 -->
            <div class="security-tips" v-if="remainingAttempts < 5 && remainingAttempts > 0">
              <el-alert
                :title="`密码错误，您还有 ${remainingAttempts} 次尝试机会`"
                type="warning"
                :closable="false"
                show-icon
              />
            </div>
            
            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <a href="javascript:;" class="forgot-link">忘记密码？</a>
            </div>
            
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-btn"
                @click="handleLogin"
                :disabled="isLocked"
              >
                <template v-if="!loading">
                  <span>登 录</span>
                  <el-icon class="btn-arrow"><ArrowRight /></el-icon>
                </template>
                <template v-else>
                  <span>登录中...</span>
                </template>
              </el-button>
            </el-form-item>
          </el-form>
          
          <!-- 分割线 -->
          <div class="divider">
            <span>其他登录方式</span>
          </div>
          
          <!-- 第三方登录 -->
          <ThirdPartyLogin />
          
          <!-- 注册提示 -->
          <div class="login-footer">
            <span>还没有账号？</span>
            <router-link to="/register" class="register-link">立即注册</router-link>
          </div>
        </div>
        
        <!-- 演示账号 -->
        <div class="demo-card glass-card">
          <div class="demo-header">
            <el-icon><Key /></el-icon>
            <span>快速体验</span>
          </div>
          <div class="demo-list">
            <div class="demo-item" @click="fillDemo('student1', '123456')">
              <div class="demo-role">
                <span class="role-icon student"><el-icon><User /></el-icon></span>
                <span class="role">学生</span>
              </div>
              <span class="account">student1 / 123456</span>
            </div>
            <div class="demo-item" @click="fillDemo('teacher1', '123456')">
              <div class="demo-role">
                <span class="role-icon teacher"><el-icon><UserFilled /></el-icon></span>
                <span class="role">教师</span>
              </div>
              <span class="account">teacher1 / 123456</span>
            </div>
            <div class="demo-item" @click="fillDemo('expert1', '123456')">
              <div class="demo-role">
                <span class="role-icon expert"><el-icon><Avatar /></el-icon></span>
                <span class="role">专家</span>
              </div>
              <span class="account">expert1 / 123456</span>
            </div>
            <div class="demo-item" @click="fillDemo('admin', '123456')">
              <div class="demo-role">
                <span class="role-icon admin"><el-icon><Setting /></el-icon></span>
                <span class="role">管理员</span>
              </div>
              <span class="account">admin / 123456</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  User, Lock, ArrowRight, ChatDotRound, Message, Key, 
  UserFilled, Avatar, Setting, Loading
} from '@element-plus/icons-vue'
import { 
  getCaptcha, 
  login as loginApi, 
  getLoginSecurityStatus 
} from '@/api/auth'
import { 
  isValidUsername,
  isValidPhone,
  validatePasswordFormat,
  getDeviceId
} from '@/utils/security'
import ThirdPartyLogin from '@/components/common/ThirdPartyLogin.vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const hasError = ref(false)
const focusedField = ref('')
const rememberMe = ref(false)
const isMobile = ref(window.innerWidth < 992)

// 验证码相关
const captchaKey = ref('')
const captchaImage = ref('')
const remainingAttempts = ref(5)
const isLocked = ref(false)
const lockEndTime = ref(0)

// 表单数据
const form = reactive({
  username: '',
  password: '',
  captchaCode: ''
})

// 验证规则
const validateUsername = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (!isValidUsername(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线，长度3-20位'))
  } else {
    callback()
  }
}

const validatePassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error('密码长度为6-20个字符'))
  } else {
    callback()
  }
}

const validateCaptcha = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入验证码'))
  } else if (value.length !== 4) {
    callback(new Error('验证码为4位数字'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  username: [
    { required: true, validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  captchaCode: [
    { required: true, validator: validateCaptcha, trigger: 'blur' }
  ]
}

// 功能介绍
const introFeatures = [
  {
    icon: 'Reading',
    title: '丰富知识库',
    desc: '涵盖各类诈骗手法详解',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    icon: 'EditPen',
    title: '在线测试',
    desc: '检验学习成果和能力',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    icon: 'Monitor',
    title: '情景演练',
    desc: '真实场景模拟体验',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  }
]

// 粒子动画
const getParticleStyle = (index: number) => {
  const random = (min: number, max: number) => Math.random() * (max - min) + min
  return {
    '--x': `${random(0, 100)}%`,
    '--y': `${random(0, 100)}%`,
    '--duration': `${random(10, 25)}s`,
    '--delay': `${random(0, 8)}s`,
    '--size': `${random(2, 6)}px`,
    '--opacity': random(0.2, 0.8)
  }
}

// 填充演示账号
const fillDemo = (username: string, password: string) => {
  form.username = username
  form.password = password
  ElMessage({
    message: '已填充演示账号',
    type: 'success',
    duration: 1500
  })
}

// 用户名输入处理
const onUsernameInput = () => {
  // 清除错误状态
  hasError.value = false
}

// 用户名失焦检查
const onUsernameBlur = async () => {
  if (form.username && isValidUsername(form.username)) {
    try {
      // 检查登录安全状态
      const res = await getLoginSecurityStatus(form.username)
      if (res.code === 200 && res.data) {
        remainingAttempts.value = res.data.remainingAttempts || 5
        if (!res.data.allowed) {
          isLocked.value = true
          lockEndTime.value = Date.now() + (res.data.lockRemainingSeconds || 0) * 1000
          // 启动锁定倒计时
          startLockCountdown()
        }
      }
    } catch (error) {
      console.error('获取登录安全状态失败:', error)
    }
  }
}

// 锁定倒计时
const lockCountdownTimer = ref<number | null>(null)
const startLockCountdown = () => {
  if (lockCountdownTimer.value) {
    clearInterval(lockCountdownTimer.value)
  }
  
  lockCountdownTimer.value = window.setInterval(() => {
    const remaining = lockEndTime.value - Date.now()
    if (remaining <= 0) {
      isLocked.value = false
      remainingAttempts.value = 5
      if (lockCountdownTimer.value) {
        clearInterval(lockCountdownTimer.value)
      }
    }
  }, 1000)
}

// 刷新验证码
const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    if (res.code === 200 && res.data) {
      captchaKey.value = res.data.captchaKey
      // 后端已返回完整的 data URL，直接使用
      captchaImage.value = res.data.captchaImage || res.data.image
      form.captchaCode = ''
    }
  } catch (error) {
    ElMessage.error('获取验证码失败')
  }
}

// 登录处理
const handleLogin = async () => {
  if (isLocked.value) {
    ElMessage.warning('账号已被锁定，请稍后再试')
    return
  }
  
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) {
      hasError.value = true
      setTimeout(() => { hasError.value = false }, 500)
      return
    }

    loading.value = true

    try {
      // 获取设备ID
      const deviceId = getDeviceId()
      
      // 调用登录API
      const res = await loginApi({
        username: form.username,
        password: form.password,
        captchaKey: captchaKey.value,
        captchaCode: form.captchaCode
      })

      if (res.code === 200) {
        ElMessage.success({
          message: '登录成功，欢迎回来！',
          duration: 2000
        })
        
        // 存储用户信息
        if (res.data) {
          userStore.setToken(res.data.token)
          userStore.setUserInfo(res.data)
        }
        
        router.push('/home')
      } else {
        ElMessage.error(res.message || '登录失败')
        hasError.value = true
        setTimeout(() => { hasError.value = false }, 500)
        
        // 刷新验证码
        refreshCaptcha()
        
        // 更新剩余尝试次数
        if (res.data?.remainingAttempts) {
          remainingAttempts.value = res.data.remainingAttempts
        }
        
        // 检查是否被锁定
        if (res.data?.locked) {
          isLocked.value = true
          lockEndTime.value = Date.now() + (res.data.lockRemainingSeconds || 1800000)
          startLockCountdown()
        }
      }
    } catch (error: any) {
      console.error('登录失败:', error)
      
      let errorMsg = '登录失败，请检查用户名和密码'
      if (error.response?.data?.message) {
        errorMsg = error.response.data.message
      }
      
      ElMessage.error(errorMsg)
      hasError.value = true
      setTimeout(() => { hasError.value = false }, 500)
      
      // 刷新验证码
      refreshCaptcha()
      
      // 更新剩余尝试次数
      if (error.response?.data?.data?.remainingAttempts) {
        remainingAttempts.value = error.response.data.data.remainingAttempts
      }
    } finally {
      loading.value = false
    }
  })
}

// 响应式处理
const checkMobile = () => {
  isMobile.value = window.innerWidth < 992
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  // 初始化验证码
  refreshCaptcha()
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
  if (lockCountdownTimer.value) {
    clearInterval(lockCountdownTimer.value)
  }
})
</script>

<style scoped>
/* 验证码相关样式 */
.captcha-row {
  display: flex;
  gap: 12px;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 40px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #dcdfe6;
  transition: border-color 0.3s;
}

.captcha-image:hover {
  border-color: #409eff;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 安全提示样式 */
.security-tips {
  margin-bottom: 16px;
}

/* 锁定状态样式 */
.login-btn.is-disabled {
  background: #c0c4cc;
  border-color: #c0c4cc;
  cursor: not-allowed;
}

/* 其他原有样式保持不变 */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 背景样式 */
.login-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.login-bg .bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1e3a5f 0%, #0f172a 50%, #1e293b 100%);
}

.login-bg .bg-mesh {
  position: absolute;
  inset: 0;
  background: var(--gradient-mesh);
  opacity: 0.6;
}

.login-bg .bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

/* 容器样式 */
.login-container {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-16);
  max-width: 1200px;
  width: 100%;
  padding: var(--spacing-6);
  margin: 0 auto;
}

/* 登录卡片 */
.login-card-wrapper {
  flex: 0 0 420px;
  animation: slideUp 0.8s ease forwards;
  animation-delay: 0.2s;
  opacity: 0;
}

.login-card {
  background: var(--glass-bg-heavy);
  backdrop-filter: var(--glass-blur);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-2xl);
  padding: var(--spacing-8);
  box-shadow: var(--shadow-2xl);
}

.login-card.shake {
  animation: shake 0.5s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-6px); }
  20%, 40%, 60%, 80% { transform: translateX(6px); }
}

/* Logo区域 */
.login-header {
  text-align: center;
  margin-bottom: var(--spacing-8);
}

.logo-wrapper {
  position: relative;
  width: 72px;
  height: 72px;
  margin: 0 auto var(--spacing-4);
}

.logo-icon {
  width: 72px;
  height: 72px;
  background: var(--gradient-primary);
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  position: relative;
  z-index: 1;
  box-shadow: var(--shadow-primary);
}

.logo-ring {
  position: absolute;
  inset: -4px;
  border: 2px solid var(--primary-color);
  border-radius: var(--radius-xl);
  opacity: 0.3;
  animation: pulseRing 2s ease-in-out infinite;
}

/* 表单样式 */
.login-form {
  margin-bottom: var(--spacing-6);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
}

.forgot-link {
  color: var(--primary-color);
  text-decoration: none;
  font-size: var(--font-size-sm);
}

.forgot-link:hover {
  text-decoration: underline;
}

/* 分割线 */
.divider {
  text-align: center;
  margin: var(--spacing-6) 0;
  position: relative;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: calc(50% - 60px);
  height: 1px;
  background: var(--border-color);
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider span {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  padding: 0 var(--spacing-3);
  background: white;
}

/* 第三方登录 */
.social-login {
  display: flex;
  justify-content: center;
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-6);
}

.social-btn {
  width: 44px;
  height: 44px;
}

.social-btn.wechat {
  background: #07c160;
  border-color: #07c160;
  color: white;
}

.social-btn.dingtalk {
  background: #1677ff;
  border-color: #1677ff;
  color: white;
}

.social-btn.mail {
  background: #666;
  border-color: #666;
  color: white;
}

/* 注册提示 */
.login-footer {
  text-align: center;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.register-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
}

.register-link:hover {
  text-decoration: underline;
}

/* 演示账号卡片 */
.demo-card {
  margin-top: var(--spacing-4);
  padding: var(--spacing-4);
  background: var(--glass-bg-light);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
}

.demo-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-3);
}

.demo-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
}

.demo-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-2) var(--spacing-3);
  background: rgba(255, 255, 255, 0.5);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.demo-item:hover {
  background: rgba(64, 158, 255, 0.1);
}

.demo-role {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.role-icon {
  width: 24px;
  height: 24px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.role-icon.student {
  background: var(--primary-color);
  color: white;
}

.role-icon.teacher {
  background: var(--success-color);
  color: white;
}

.role-icon.expert {
  background: var(--warning-color);
  color: white;
}

.role-icon.admin {
  background: var(--danger-color);
  color: white;
}

.role {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
}

.account {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  font-family: monospace;
}

/* 动画 */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 48px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  background: var(--gradient-primary);
  border: none;
  border-radius: var(--radius-lg);
  transition: all var(--transition-normal);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-primary-lg);
}

.btn-arrow {
  margin-left: var(--spacing-2);
  transition: transform var(--transition-fast);
}

.login-btn:hover .btn-arrow {
  transform: translateX(4px);
}

/* 左侧介绍区域 */
.login-intro {
  flex: 1;
  color: var(--text-inverse);
  animation: slideRight 0.8s ease forwards;
}

@keyframes slideRight {
  from {
    opacity: 0;
    transform: translateX(-40px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.intro-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-4);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  backdrop-filter: blur(10px);
  margin-bottom: var(--spacing-6);
}

.intro-title {
  font-size: var(--font-size-5xl);
  font-weight: var(--font-weight-bold);
  line-height: 1.2;
  margin-bottom: var(--spacing-4);
  background: linear-gradient(135deg, #fff 0%, rgba(255, 255, 255, 0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.intro-desc {
  font-size: var(--font-size-lg);
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.7;
  margin-bottom: var(--spacing-8);
  max-width: 480px;
}

.intro-features {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-8);
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-3);
  padding: var(--spacing-4);
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(10px);
  transition: all var(--transition-normal);
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.feature-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  font-size: 18px;
  color: white;
  flex-shrink: 0;
}

.feature-text h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-1);
}

.feature-text p {
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.6);
}

.intro-stats {
  display: flex;
  gap: var(--spacing-8);
}

.stat-box {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, var(--primary-light) 0%, var(--info-color) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.6);
}

/* 响应式 */
@media (max-width: 992px) {
  .login-intro {
    display: none;
  }
  
  .login-card-wrapper {
    flex: 0 0 100%;
    max-width: 420px;
  }
}

@media (max-width: 480px) {
  .login-card {
    padding: var(--spacing-6);
  }
  
  .demo-card {
    display: none;
  }
}
</style>
