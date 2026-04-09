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
            <el-icon><Shield /></el-icon>
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
                @blur="focusedField = ''"
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
                @focus="focusedField = 'password'"
                @blur="focusedField = ''"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <el-icon :class="{ 'focused': focusedField === 'password' }"><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
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
          <div class="social-login">
            <el-tooltip content="企业微信" placement="top">
              <el-button circle size="large" class="social-btn wechat">
                <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                  <path d="M8.5 11a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3zm5 0a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3zm5.5 4.5a1 1 0 1 0 0-2 1 1 0 0 0 0 2zm3-4a7 7 0 0 1-6.5 7h-2a5.5 5.5 0 0 1-5-5.5 4.5 4.5 0 0 1 .5-2.5 6.5 6.5 0 0 0 4-4.5H9.5a5.5 5.5 0 0 0-5 5.5c0 2.15 1.24 4.03 3.08 5.08A10.94 10.94 0 0 1 12 17.5a11 11 0 0 1-1.5-10.98 12 12 0 0 1 10.5-6.5 12 12 0 0 1 5 22h-2a10 10 0 0 1-5.5-19z"/>
                </svg>
              </el-button>
            </el-tooltip>
            <el-tooltip content="钉钉" placement="top">
              <el-button circle size="large" class="social-btn dingtalk">
                <el-icon :size="20"><ChatDotRound /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="企业邮箱" placement="top">
              <el-button circle size="large" class="social-btn mail">
                <el-icon :size="20"><Message /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
          
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
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  User, Lock, ArrowRight, ChatDotRound, Message, Key, 
  UserFilled, Avatar, Setting, Key as Shield
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const hasError = ref(false)
const focusedField = ref('')
const rememberMe = ref(false)
const isMobile = ref(window.innerWidth < 992)

const form = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ]
}

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

const fillDemo = (username: string, password: string) => {
  form.username = username
  form.password = password
  ElMessage({
    message: '已填充演示账号',
    type: 'success',
    duration: 1500
  })
}

const handleLogin = async () => {
  hasError.value = false

  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) {
      hasError.value = true
      setTimeout(() => hasError.value = false, 500)
      return
    }

    loading.value = true
    try {
      console.log('开始登录，用户名:', form.username)
      await userStore.login(form.username, form.password)
      
      console.log('登录成功，token:', userStore.token)
      console.log('登录成功，用户信息:', userStore.userInfo)
      
      ElMessage({
        message: '登录成功，欢迎回来！',
        type: 'success',
        duration: 1500
      })
      
      // 延迟跳转，确保token已保存
      setTimeout(() => {
        // 强制刷新页面以加载正确的菜单
        window.location.href = '/'
      }, 500)
    } catch (error: any) {
      console.error('登录失败:', error)
      hasError.value = true
      setTimeout(() => hasError.value = false, 500)
      ElMessage.error(error.message || '登录失败，请检查用户名和密码')
    } finally {
      loading.value = false
    }
  })
}

// 监听窗口大小变化
window.addEventListener('resize', () => {
  isMobile.value = window.innerWidth < 992
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
  background: #0f0c29;
}

.login-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, 
    #0f0c29 0%, 
    #302b63 50%, 
    #24243e 100%);
}

.bg-mesh {
  position: absolute;
  inset: 0;
  background-image: 
    radial-gradient(ellipse at 20% 30%, rgba(102, 126, 234, 0.3) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 70%, rgba(118, 75, 162, 0.3) 0%, transparent 50%);
  animation: meshMove 20s ease-in-out infinite;
}

@keyframes meshMove {
  0%, 100% { transform: scale(1) rotate(0deg); }
  50% { transform: scale(1.1) rotate(5deg); }
}

.bg-grid {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 50s linear infinite;
}

@keyframes gridMove {
  0% { transform: perspective(500px) rotateX(60deg) translateY(0); }
  100% { transform: perspective(500px) rotateX(60deg) translateY(50px); }
}

.bg-shapes {
  position: absolute;
  inset: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  animation: float 20s infinite ease-in-out;
}

.shape-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.3), rgba(118, 75, 162, 0.2));
  top: -150px;
  right: -100px;
  animation-delay: 0s;
  filter: blur(60px);
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, rgba(236, 72, 153, 0.2), rgba(168, 85, 247, 0.2));
  bottom: -100px;
  left: -100px;
  animation-delay: -5s;
  filter: blur(60px);
}

.shape-3 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.2), rgba(0, 242, 254, 0.2));
  top: 40%;
  left: 10%;
  animation-delay: -10s;
  filter: blur(50px);
}

.shape-4 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, rgba(52, 211, 153, 0.2), rgba(16, 185, 129, 0.2));
  top: 20%;
  right: 30%;
  animation-delay: -15s;
  filter: blur(40px);
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -30px) scale(1.05); }
  50% { transform: translate(0, -50px) scale(1); }
  75% { transform: translate(-30px, -30px) scale(0.95); }
}

.bg-particles {
  position: absolute;
  inset: 0;
}

.particle {
  position: absolute;
  width: var(--size);
  height: var(--size);
  background: rgba(255, 255, 255, var(--opacity));
  border-radius: 50%;
  left: var(--x);
  top: var(--y);
  animation: particle-float var(--duration) var(--delay) infinite ease-in-out;
}

@keyframes particle-float {
  0%, 100% { transform: translate(0, 0) scale(1); opacity: var(--opacity); }
  50% { transform: translate(50px, -80px) scale(1.5); opacity: 1; }
}

.login-container {
  position: relative;
  z-index: 10;
  display: flex;
  gap: 60px;
  align-items: center;
  max-width: 1200px;
  width: 100%;
}

/* 左侧介绍区域 */
.login-intro {
  flex: 1;
  animation: fadeInLeft 0.8s ease-out;
}

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-40px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.intro-content {
  color: white;
}

.intro-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 50px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 14px;
  margin-bottom: 24px;
}

.intro-title {
  font-size: 42px;
  font-weight: 700;
  line-height: 1.3;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #fff 0%, rgba(255,255,255,0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.intro-desc {
  font-size: 16px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 40px;
}

.intro-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(8px);
}

.feature-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.feature-text h4 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}

.feature-text p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.intro-stats {
  display: flex;
  gap: 32px;
}

.stat-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  min-width: 100px;
  transition: all 0.3s ease;
}

.stat-box:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-4px);
}

.stat-num {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 4px;
}

/* 登录卡片容器 */
.login-card-wrapper {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

/* 登录卡片 */
.login-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 
    0 25px 50px -12px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  animation: slideUp 0.6s ease-out;
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

.login-card.shake {
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-10px); }
  40%, 80% { transform: translateX(10px); }
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.logo-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 36px;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
  animation: pulse-glow 3s infinite ease-in-out;
}

@keyframes pulse-glow {
  0%, 100% { box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4); }
  50% { box-shadow: 0 10px 40px rgba(102, 126, 234, 0.6), 0 0 60px rgba(102, 126, 234, 0.3); }
}

.logo-ring {
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 88px;
  height: 88px;
  border: 2px solid rgba(102, 126, 234, 0.3);
  border-radius: 24px;
  animation: ring-pulse 3s infinite ease-in-out;
}

@keyframes ring-pulse {
  0%, 100% { transform: translateX(-50%) scale(1); opacity: 1; }
  50% { transform: translateX(-50%) scale(1.1); opacity: 0.5; }
}

.login-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.login-header .subtitle {
  color: #64748b;
  font-size: 14px;
}

.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #1a1a2e;
  padding-bottom: 8px !important;
}

.login-form :deep(.el-input__wrapper) {
  padding: 14px 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 0 0 1px #e2e8f0 inset;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2) inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #667eea inset !important;
}

.login-form :deep(.el-input__prefix .el-icon) {
  color: #94a3b8;
  transition: color 0.3s ease;
}

.login-form :deep(.el-input__prefix .el-icon.focused) {
  color: #667eea;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.forgot-link {
  color: #667eea;
  font-size: 14px;
  transition: color 0.3s ease;
}

.forgot-link:hover {
  color: #764ba2;
}

.login-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.login-btn:active {
  transform: translateY(0);
}

.btn-arrow {
  transition: transform 0.3s ease;
}

.login-btn:hover .btn-arrow {
  transform: translateX(4px);
}

.divider {
  display: flex;
  align-items: center;
  margin: 24px 0;
  color: #94a3b8;
  font-size: 13px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e2e8f0;
}

.divider span {
  padding: 0 16px;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 24px;
}

.social-btn {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #64748b;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.social-btn:hover {
  transform: translateY(-3px);
}

.social-btn.wechat:hover {
  border-color: #07c160;
  color: #07c160;
  box-shadow: 0 4px 15px rgba(7, 193, 96, 0.3);
}

.social-btn.dingtalk:hover {
  border-color: #1677ff;
  color: #1677ff;
  box-shadow: 0 4px 15px rgba(22, 119, 255, 0.3);
}

.social-btn.mail:hover {
  border-color: #667eea;
  color: #667eea;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.login-footer {
  text-align: center;
  color: #64748b;
  font-size: 14px;
}

.register-link {
  color: #667eea;
  font-weight: 600;
  margin-left: 4px;
  transition: color 0.3s ease;
}

.register-link:hover {
  color: #764ba2;
}

/* 演示账号卡片 */
.demo-card {
  width: 260px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 
    0 25px 50px -12px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  animation: slideUp 0.6s ease-out 0.2s both;
}

.demo-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
  color: #667eea;
  font-weight: 600;
  font-size: 15px;
}

.demo-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.demo-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  background: #f8fafc;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.demo-item:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.3);
  transform: translateX(4px);
}

.demo-role {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-icon {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: white;
}

.role-icon.student { background: linear-gradient(135deg, #667eea, #764ba2); }
.role-icon.teacher { background: linear-gradient(135deg, #f093fb, #f5576c); }
.role-icon.expert { background: linear-gradient(135deg, #4facfe, #00f2fe); }
.role-icon.admin { background: linear-gradient(135deg, #43e97b, #38f9d7); }

.role {
  font-weight: 600;
  color: #1a1a2e;
  font-size: 14px;
}

.demo-item .account {
  font-size: 11px;
  color: #94a3b8;
  font-family: 'SF Mono', Consolas, monospace;
}

/* 响应式 */
@media (max-width: 1200px) {
  .login-intro {
    display: none;
  }
  
  .login-container {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .login-page {
    padding: 16px;
  }
  
  .login-card {
    width: 100%;
    max-width: 400px;
    padding: 28px;
  }
  
  .demo-card {
    display: none;
  }
  
  .login-header h1 {
    font-size: 20px;
  }
}
</style>



