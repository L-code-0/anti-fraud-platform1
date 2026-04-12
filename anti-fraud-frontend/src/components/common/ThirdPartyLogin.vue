<template>
  <div class="third-party-login">
    <!-- 第三方登录按钮 -->
    <div class="social-login">
      <el-tooltip content="微信登录" placement="top">
        <el-button 
          circle 
          size="large" 
          class="social-btn wechat"
          @click="handleWechatLogin"
        >
          <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
            <path d="M8.5 11a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3zm5 0a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3zm5.5 4.5a1 1 0 1 0 0-2 1 1 0 0 0 0 2zm3-4a7 7 0 0 1-6.5 7h-2a5.5 5.5 0 0 1-5-5.5 4.5 4.5 0 0 1 .5-2.5 6.5 6.5 0 0 0 4-4.5H9.5a5.5 5.5 0 0 0-5 5.5c0 2.15 1.24 4.03 3.08 5.08A10.94 10.94 0 0 1 12 17.5a11 11 0 0 1-1.5-10.98 12 12 0 0 1 10.5-6.5 12 12 0 0 1 5 22h-2a10 10 0 0 1-5.5-19z"/>
          </svg>
        </el-button>
      </el-tooltip>
      <el-tooltip content="钉钉登录" placement="top">
        <el-button 
          circle 
          size="large" 
          class="social-btn dingtalk"
          @click="handleDingTalkLogin"
        >
          <el-icon :size="20"><ChatDotRound /></el-icon>
        </el-button>
      </el-tooltip>
      <el-tooltip content="企业邮箱" placement="top">
        <el-button 
          circle 
          size="large" 
          class="social-btn mail"
          @click="handleEmailLogin"
        >
          <el-icon :size="20"><Message /></el-icon>
        </el-button>
      </el-tooltip>
    </div>

    <!-- 微信登录二维码弹窗 -->
    <el-dialog
      v-model="showWechatQrCode"
      title="微信扫码登录"
      width="300px"
      center
      destroy-on-close
    >
      <div class="qr-code-container">
        <div v-if="wechatQrCodeUrl" class="qr-code">
          <img :src="wechatQrCodeUrl" alt="微信登录二维码" />
        </div>
        <div v-else class="loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>生成二维码中...</span>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showWechatQrCode = false">取消</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 钉钉登录二维码弹窗 -->
    <el-dialog
      v-model="showDingTalkQrCode"
      title="钉钉扫码登录"
      width="300px"
      center
      destroy-on-close
    >
      <div class="qr-code-container">
        <div v-if="dingTalkQrCodeUrl" class="qr-code">
          <img :src="dingTalkQrCodeUrl" alt="钉钉登录二维码" />
        </div>
        <div v-else class="loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>生成二维码中...</span>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showDingTalkQrCode = false">取消</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Message, Loading } from '@element-plus/icons-vue'
import { 
  getWechatQrCode, 
  getDingTalkQrCode, 
  wechatLogin, 
  dingTalkLogin 
} from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 二维码状态
const showWechatQrCode = ref(false)
const showDingTalkQrCode = ref(false)
const wechatQrCodeUrl = ref('')
const dingTalkQrCodeUrl = ref('')

// 处理微信登录
const handleWechatLogin = async () => {
  try {
    // 获取微信登录二维码
    const res = await getWechatQrCode()
    if (res.code === 200 && res.data) {
      wechatQrCodeUrl.value = res.data.qrCodeUrl
      showWechatQrCode.value = true
      
      // 模拟扫码登录流程（实际项目中应该使用WebSocket或轮询）
      setTimeout(() => {
        handleWechatLoginSuccess()
      }, 5000)
    } else {
      ElMessage.error('获取微信二维码失败')
    }
  } catch (error) {
    console.error('获取微信二维码失败:', error)
    ElMessage.error('获取微信二维码失败')
  }
}

// 处理钉钉登录
const handleDingTalkLogin = async () => {
  try {
    // 获取钉钉登录二维码
    const res = await getDingTalkQrCode()
    if (res.code === 200 && res.data) {
      dingTalkQrCodeUrl.value = res.data.qrCodeUrl
      showDingTalkQrCode.value = true
      
      // 模拟扫码登录流程（实际项目中应该使用WebSocket或轮询）
      setTimeout(() => {
        handleDingTalkLoginSuccess()
      }, 5000)
    } else {
      ElMessage.error('获取钉钉二维码失败')
    }
  } catch (error) {
    console.error('获取钉钉二维码失败:', error)
    ElMessage.error('获取钉钉二维码失败')
  }
}

// 处理企业邮箱登录
const handleEmailLogin = () => {
  ElMessage.info('企业邮箱登录功能开发中')
}

// 微信登录成功处理
const handleWechatLoginSuccess = async () => {
  try {
    // 模拟微信登录（实际项目中应该使用真实的code）
    const res = await wechatLogin('mock_code')
    if (res.code === 200 && res.data) {
      ElMessage.success('微信登录成功')
      showWechatQrCode.value = false
      
      // 存储用户信息
      if (res.data.user) {
        userStore.setToken(res.data.user.token)
        userStore.setUserInfo(res.data.user)
      } else if (res.data.token) {
        userStore.setToken(res.data.token)
        userStore.setUserInfo(res.data)
      }
      
      router.push('/home')
    } else {
      ElMessage.error('微信登录失败')
    }
  } catch (error) {
    console.error('微信登录失败:', error)
    ElMessage.error('微信登录失败')
  }
}

// 钉钉登录成功处理
const handleDingTalkLoginSuccess = async () => {
  try {
    // 模拟钉钉登录（实际项目中应该使用真实的code）
    const res = await dingTalkLogin('mock_code')
    if (res.code === 200 && res.data) {
      ElMessage.success('钉钉登录成功')
      showDingTalkQrCode.value = false
      
      // 存储用户信息
      if (res.data.user) {
        userStore.setToken(res.data.user.token)
        userStore.setUserInfo(res.data.user)
      } else if (res.data.token) {
        userStore.setToken(res.data.token)
        userStore.setUserInfo(res.data)
      }
      
      router.push('/home')
    } else {
      ElMessage.error('钉钉登录失败')
    }
  } catch (error) {
    console.error('钉钉登录失败:', error)
    ElMessage.error('钉钉登录失败')
  }
}
</script>

<style scoped>
.third-party-login {
  width: 100%;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-6);
}

.social-btn {
  width: 44px;
  height: 44px;
  transition: all var(--transition-fast);
}

.social-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
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

.qr-code-container {
  padding: var(--spacing-4);
  text-align: center;
}

.qr-code {
  margin: 0 auto;
  width: 200px;
  height: 200px;
  padding: var(--spacing-2);
  background: white;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.qr-code img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-3);
  padding: var(--spacing-6) 0;
}

.loading .el-icon {
  font-size: 32px;
  color: var(--primary-color);
}

.loading span {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}
</style>