<template>
  <div class="mobile-menu-wrapper">
    <!-- 移动端菜单按钮 -->
    <el-button 
      :icon="menuIcon" 
      circle 
      class="mobile-menu-btn" 
      @click="toggleMenu"
      :class="{ 'menu-active': isMenuOpen }"
    />
    
    <!-- 移动端菜单面板 -->
    <transition name="slide-right">
      <div v-if="isMenuOpen" class="mobile-menu-panel">
        <div class="menu-header">
          <div class="logo" @click="closeMenu">
            <el-icon class="logo-icon"><Lock /></el-icon>
            <span class="logo-text">高校反诈安全</span>
          </div>
          <el-button :icon="Close" circle @click="closeMenu" class="close-btn" />
        </div>
        
        <el-menu
          mode="vertical"
          :default-active="activeMenu"
          router
          class="mobile-nav-menu"
        >
          <el-menu-item index="/" @click="closeMenu">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/knowledge" @click="closeMenu">
            <el-icon><Document /></el-icon>
            <span>知识学习</span>
          </el-menu-item>
          <el-menu-item index="/test" @click="closeMenu">
            <el-icon><EditPen /></el-icon>
            <span>测试考试</span>
          </el-menu-item>
          <el-menu-item index="/simulation" @click="closeMenu">
            <el-icon><Monitor /></el-icon>
            <span>演练模拟</span>
          </el-menu-item>
          <el-menu-item index="/report" @click="closeMenu">
            <el-icon><Warning /></el-icon>
            <span>举报预警</span>
          </el-menu-item>
          <el-menu-item index="/class" @click="closeMenu">
            <el-icon><Collection /></el-icon>
            <span>班级管理</span>
          </el-menu-item>
          <el-menu-item index="/activity" @click="closeMenu">
            <el-icon><Ticket /></el-icon>
            <span>活动中心</span>
          </el-menu-item>
          <el-menu-item index="/points" @click="closeMenu">
            <el-icon><Coin /></el-icon>
            <span>积分中心</span>
          </el-menu-item>
          <el-menu-item index="/leaderboard" @click="closeMenu">
            <el-icon><Rank /></el-icon>
            <span>排行榜</span>
          </el-menu-item>
          <el-menu-item index="/achievement" @click="closeMenu">
            <el-icon><Trophy /></el-icon>
            <span>成就中心</span>
          </el-menu-item>
          
          <!-- 管理菜单 -->
          <el-sub-menu v-if="Number(userStore.userInfo?.roleId) >= 3" index="/admin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>管理后台</span>
            </template>
            <el-menu-item index="/admin" @click="closeMenu">
              <el-icon><DataAnalysis /></el-icon>
              <span>仪表盘</span>
            </el-menu-item>
            <el-menu-item index="/admin/users" @click="closeMenu">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/knowledge" @click="closeMenu">
              <el-icon><Document /></el-icon>
              <span>知识管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/questions" @click="closeMenu">
              <el-icon><Edit /></el-icon>
              <span>题库管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
        
        <!-- 用户操作区 -->
        <div class="menu-footer">
          <template v-if="userStore.isLoggedIn && userStore.userInfo">
            <div class="user-info">
              <el-avatar :size="40" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.realName?.charAt(0) || 'U' }}
              </el-avatar>
              <div class="user-details">
                <span class="username">{{ userStore.userInfo?.realName || '用户' }}</span>
                <span class="user-role">{{ getRoleName(userStore.userInfo?.roleId) }}</span>
              </div>
            </div>
            <div class="user-actions">
              <el-button type="primary" @click="goToProfile">个人中心</el-button>
              <el-button @click="logout">退出登录</el-button>
            </div>
          </template>
          <template v-else>
            <div class="auth-actions">
              <el-button @click="goToLogin">登录</el-button>
              <el-button type="primary" @click="goToRegister">注册</el-button>
            </div>
          </template>
        </div>
      </div>
    </transition>
    
    <!-- 遮罩层 -->
    <transition name="fade">
      <div v-if="isMenuOpen" class="menu-overlay" @click="closeMenu"></div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import {
  Lock, House, Document, EditPen, Monitor, Warning, Collection,
  Ticket, Coin, Rank, Trophy, Setting, DataAnalysis, User, Edit,
  Close, Menu as MenuIcon
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isMenuOpen = ref(false)

const menuIcon = computed(() => isMenuOpen.value ? Close : MenuIcon)
const activeMenu = computed(() => route.path)

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
  document.body.style.overflow = isMenuOpen.value ? 'hidden' : 'auto'
}

const closeMenu = () => {
  isMenuOpen.value = false
  document.body.style.overflow = 'auto'
}

const getRoleName = (roleId: number | undefined) => {
  const roles: Record<number, string> = {
    1: '学生',
    2: '教师',
    3: '管理员',
    4: '专家',
    5: '系统管理员'
  }
  return roles[roleId || 1] || '用户'
}

const goToProfile = () => {
  closeMenu()
  router.push('/profile')
}

const goToLogin = () => {
  closeMenu()
  router.push('/login')
}

const goToRegister = () => {
  closeMenu()
  router.push('/register')
}

const logout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  closeMenu()
  router.push('/login')
}
</script>

<style scoped>
.mobile-menu-wrapper {
  position: relative;
  z-index: var(--z-fixed);
}

.mobile-menu-btn {
  display: none !important;
  background: var(--bg-card) !important;
  border: 1px solid var(--border-color) !important;
}

.menu-active {
  background: var(--primary-color) !important;
  color: white !important;
  border-color: var(--primary-color) !important;
}

/* 移动端菜单面板 */
.mobile-menu-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: 80vw;
  max-width: 300px;
  height: 100vh;
  background: var(--bg-card);
  box-shadow: -4px 0 12px rgba(0, 0, 0, 0.1);
  z-index: var(--z-modal);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.menu-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-4);
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-card);
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  cursor: pointer;
}

.logo-icon {
  font-size: 24px;
  color: var(--primary-color);
}

.logo-text {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  white-space: nowrap;
}

.close-btn {
  background: var(--bg-page) !important;
  border: 1px solid var(--border-color) !important;
}

.mobile-nav-menu {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-4) 0;
  border: none !important;
}

.mobile-nav-menu :deep(.el-menu-item) {
  height: 48px !important;
  line-height: 48px !important;
  padding: 0 var(--spacing-6) !important;
  margin: 0 !important;
  border: none !important;
  font-size: var(--font-size-base) !important;
  color: var(--text-primary) !important;
  transition: all var(--transition-fast);
}

.mobile-nav-menu :deep(.el-menu-item:hover),
.mobile-nav-menu :deep(.el-menu-item.is-active) {
  background: var(--hover-bg-color) !important;
  color: var(--primary-color) !important;
}

.mobile-nav-menu :deep(.el-sub-menu__title) {
  height: 48px !important;
  line-height: 48px !important;
  padding: 0 var(--spacing-6) !important;
  margin: 0 !important;
  border: none !important;
  font-size: var(--font-size-base) !important;
  color: var(--text-primary) !important;
  transition: all var(--transition-fast);
}

.mobile-nav-menu :deep(.el-sub-menu__title:hover) {
  background: var(--hover-bg-color) !important;
  color: var(--primary-color) !important;
}

.menu-footer {
  padding: var(--spacing-4);
  border-top: 1px solid var(--border-color);
  background: var(--bg-card);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-4);
}

.user-details {
  flex: 1;
}

.username {
  display: block;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
  margin-bottom: 2px;
}

.user-role {
  display: block;
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.user-actions {
  display: flex;
  gap: var(--spacing-2);
}

.auth-actions {
  display: flex;
  gap: var(--spacing-2);
}

.menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: var(--z-modal-backdrop);
}

/* 过渡动画 */
.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform var(--transition-normal);
}

.slide-right-enter-from {
  transform: translateX(100%);
}

.slide-right-leave-to {
  transform: translateX(100%);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-normal);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式显示 */
@media (max-width: 768px) {
  .mobile-menu-btn {
    display: flex !important;
  }
}
</style>
