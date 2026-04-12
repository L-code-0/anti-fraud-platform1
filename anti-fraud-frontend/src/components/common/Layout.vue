<template>
  <div class="layout-wrapper" :class="{ 'dark-mode': isDark }">
    <el-container class="app-container">
      <!-- 全局顶部导航栏 -->
      <el-header class="app-header" :class="{ 'header-scrolled': isScrolled }">
        <div class="header-content">
          <!-- 左侧：Logo和导航 -->
          <div class="header-left">
            <div class="logo" @click="$router.push('/')">
              <el-icon class="logo-icon"><Lock /></el-icon>
              <span class="logo-text">高校反诈安全</span>
              <span class="logo-badge">MU</span>
            </div>

            <el-menu
              mode="horizontal"
              :default-active="activeMenu"
              router
              class="nav-menu"
              :ellipsis="false"
            >
              <el-menu-item index="/">首页</el-menu-item>
              <el-menu-item index="/knowledge">知识学习</el-menu-item>
              <el-menu-item index="/test">测试考试</el-menu-item>
              <el-menu-item index="/simulation">演练模拟</el-menu-item>
              <el-menu-item index="/report">举报预警</el-menu-item>
              <el-menu-item index="/class" class="hide-mobile">班级管理</el-menu-item>
              <el-menu-item index="/activity" class="hide-mobile">活动中心</el-menu-item>
              <el-menu-item index="/points" class="hide-mobile">积分中心</el-menu-item>
              <!-- 管理菜单 -->
              <el-sub-menu v-if="Number(userStore.userInfo?.roleId) >= 3" index="/admin">
                <template #title>
                  <el-icon><Setting /></el-icon>管理后台
                </template>
                <el-menu-item index="/admin">仪表盘</el-menu-item>
                <el-menu-item index="/admin/users">用户管理</el-menu-item>
                <el-menu-item index="/admin/knowledge">知识管理</el-menu-item>
                <el-menu-item index="/admin/questions">题库管理</el-menu-item>
              </el-sub-menu>
            </el-menu>
          </div>

          <!-- 右侧：搜索和用户操作 -->
          <div class="header-right">
            <!-- 全局搜索 -->
            <div class="global-search">
              <el-input
                v-model="searchQuery"
                placeholder="搜索知识、试题、演练..."
                :prefix-icon="Search"
                clearable
                @focus="showSearchPanel = true"
                @blur="handleSearchBlur"
                class="search-input"
              />
              <!-- 搜索面板 -->
              <transition name="slide-fade">
                <div v-if="showSearchPanel && searchQuery" class="search-panel">
                  <div class="search-results">
                    <template v-if="searchResults.knowledge?.length || searchResults.tests?.length">
                      <div class="search-section" v-if="searchResults.knowledge?.length">
                        <div class="section-title">
                          <el-icon><Document /></el-icon> 知识内容
                        </div>
                        <div
                          v-for="item in searchResults.knowledge"
                          :key="item.id"
                          class="search-item"
                          @click="handleSearchItemClick('/knowledge/' + item.id)"
                        >
                          <span>{{ item.title }}</span>
                          <el-tag size="small">{{ item.category }}</el-tag>
                        </div>
                      </div>
                      <div class="search-section" v-if="searchResults.tests?.length">
                        <div class="section-title">
                          <el-icon><EditPen /></el-icon> 测试试卷
                        </div>
                        <div
                          v-for="item in searchResults.tests"
                          :key="item.id"
                          class="search-item"
                          @click="handleSearchItemClick('/test/' + item.id)"
                        >
                          <span>{{ item.title }}</span>
                          <el-tag size="small" type="warning">{{ item.questionCount }}题</el-tag>
                        </div>
                      </div>
                    </template>
                    <div v-else class="search-empty">
                      <el-icon><Search /></el-icon>
                      <span>未找到相关结果</span>
                    </div>
                  </div>
                </div>
              </transition>
            </div>

            <!-- 快捷操作 -->
            <div class="quick-actions">
              <el-tooltip content="排行榜" placement="bottom">
                <el-button :icon="Rank" circle @click="$router.push('/leaderboard')" aria-label="排行榜" />
              </el-tooltip>
              <el-tooltip content="成就中心" placement="bottom">
                <el-button :icon="Trophy" circle @click="$router.push('/achievement')" aria-label="成就中心" />
              </el-tooltip>
            </div>

            <!-- 主题切换 -->
            <ThemeToggle />

            <!-- 消息通知 -->
            <NotificationPanel />

            <!-- 移动端菜单 -->
            <MobileMenu />

            <!-- 用户信息 -->
            <template v-if="userStore.isLoggedIn && userStore.userInfo">
              <el-dropdown @command="handleCommand" trigger="click">
                <span class="user-info">
                  <el-avatar :size="36" :src="userStore.userInfo?.avatar" class="user-avatar">
                    {{ userStore.userInfo?.realName?.charAt(0) || 'U' }}
                  </el-avatar>
                  <div class="user-details hide-mobile">
                    <span class="username">{{ userStore.userInfo?.realName || '用户' }}</span>
                    <div class="user-meta">
                      <el-tag size="small" type="warning">Lv.{{ userStore.userInfo?.level || 1 }}</el-tag>
                      <span class="points">
                        <el-icon><Coin /></el-icon>
                        {{ userStore.userInfo?.points || 0 }}
                      </span>
                    </div>
                  </div>
                  <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <div class="user-dropdown-header">
                    <el-avatar :size="48" :src="userStore.userInfo?.avatar">
                      {{ userStore.userInfo?.realName?.charAt(0) || 'U' }}
                    </el-avatar>
                    <div class="dropdown-user-info">
                      <span class="dropdown-username">{{ userStore.userInfo?.realName }}</span>
                      <span class="dropdown-role">{{ getRoleName(userStore.userInfo?.roleId) }}</span>
                    </div>
                  </div>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">
                      <el-icon><User /></el-icon>个人中心
                    </el-dropdown-item>
                    <el-dropdown-item command="points">
                      <el-icon><Coin /></el-icon>积分中心
                    </el-dropdown-item>
                    <el-dropdown-item command="wrongbook">
                      <el-icon><DocumentCopy /></el-icon>错题本
                    </el-dropdown-item>
                    <el-dropdown-item command="favorites">
                      <el-icon><Star /></el-icon>我的收藏
                    </el-dropdown-item>
                    <el-dropdown-item command="leaderboard">
                      <el-icon><Medal /></el-icon>排行榜
                    </el-dropdown-item>
                    <el-dropdown-item command="achievement">
                      <el-icon><Trophy /></el-icon>成就中心
                    </el-dropdown-item>
                    <el-dropdown-item divided command="settings">
                      <el-icon><Setting /></el-icon>账号设置
                    </el-dropdown-item>
                    <el-dropdown-item command="logout" class="logout-item">
                      <el-icon><SwitchButton /></el-icon>退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <div class="auth-buttons">
                <el-button @click="$router.push('/login')">登录</el-button>
                <el-button type="primary" @click="$router.push('/register')">注册</el-button>
              </div>
            </template>
          </div>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="app-main">
        <slot></slot>
      </el-main>

      <!-- 底部 -->
      <el-footer class="app-footer">
        <div class="footer-content">
          <div class="footer-main">
            <div class="footer-section">
              <h4>关于我们</h4>
              <p>高校反诈安全知识普及平台致力于提高高校师生的防诈骗意识</p>
            </div>
            <div class="footer-section">
              <h4>快速链接</h4>
              <ul>
                <li><a href="javascript:;">使用帮助</a></li>
                <li><a href="javascript:;">常见问题</a></li>
                <li><a href="javascript:;">意见反馈</a></li>
              </ul>
            </div>
            <div class="footer-section">
              <h4>法律声明</h4>
              <ul>
                <li><a href="javascript:;">隐私政策</a></li>
                <li><a href="javascript:;">服务条款</a></li>
                <li><a href="javascript:;">联系我们</a></li>
              </ul>
            </div>
            <div class="footer-section">
              <h4>关注我们</h4>
              <div class="social-links">
                <el-tooltip content="微信公众号">
                  <el-button circle aria-label="微信公众号"><el-icon><ChatDotRound /></el-icon></el-button>
                </el-tooltip>
                <el-tooltip content="微博">
                  <el-button circle aria-label="微博"><el-icon><Promotion /></el-icon></el-button>
                </el-tooltip>
                <el-tooltip content="抖音">
                  <el-button circle aria-label="抖音"><el-icon><VideoPlay /></el-icon></el-button>
                </el-tooltip>
              </div>
            </div>
          </div>
          <el-divider />
          <div class="footer-bottom">
            <p>© 2026 高校反诈安全知识普及平台 | 保护财产安全，从学习反诈知识开始</p>
            <p class="footer-stats">
              <span>注册用户：<strong>10,000+</strong></span>
              <span>学习次数：<strong>50,000+</strong></span>
              <span>举报线索：<strong>1,000+</strong></span>
            </p>
          </div>
        </div>
      </el-footer>
    </el-container>

    <!-- 回到顶部 -->
    <transition name="fade">
      <div v-if="showBackToTop" class="back-to-top" @click="scrollToTop" @keydown="handleBackToTopKeydown" role="button" aria-label="回到顶部" tabindex="0">
        <el-icon><Top /></el-icon>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import * as VueRouter from 'vue-router'
const useRoute = VueRouter.useRoute
const useRouter = VueRouter.useRouter
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import ThemeToggle from './ThemeToggle.vue'
import NotificationPanel from './NotificationPanel.vue'
import MobileMenu from './MobileMenu.vue'
import {
  Lock, SwitchButton, Setting, Monitor, User, Star, TrendCharts,
  ChatDotRound, QuestionFilled, DocumentCopy, Rank, Trophy, Medal,
  Bell, Search, Document, ChatLineSquare, Coin, Edit, ArrowDown,
  VideoPlay, Promotion, Top, ChatLineSquare as ChatSquare, EditPen
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 搜索相关
const searchQuery = ref('')
const showSearchPanel = ref(false)
const searchResults = ref({
  knowledge: [] as any[],
  tests: [] as any[],
  simulations: [] as any[]
})

// 返回顶部
const showBackToTop = ref(false)

// 滚动状态
const isScrolled = ref(false)

// 深色模式
const isDark = computed(() => document.body.classList.contains('dark'))

const activeMenu = computed(() => route.path)

// 获取角色名称
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

// 搜索处理
let searchTimer: number | null = null
watch(searchQuery, (val) => {
  if (searchTimer) clearTimeout(searchTimer)
  if (val.trim()) {
    searchTimer = window.setTimeout(() => {
      performSearch(val)
    }, 300)
  } else {
    searchResults.value = {
      knowledge: [],
      tests: [],
      simulations: []
    }
  }
})

const performSearch = (query: string) => {
  // 模拟搜索结果
  searchResults.value = {
    knowledge: [
      { id: 1, title: '冒充公检法诈骗防范指南', category: '电信诈骗' },
      { id: 2, title: '网络购物诈骗案例分析', category: '网络诈骗' },
      { id: 3, title: '校园贷诈骗风险提示', category: '金融诈骗' }
    ],
    tests: [
      { id: 1, title: '反诈知识测试卷A', questionCount: 20 },
      { id: 2, title: '电信诈骗专项测试', questionCount: 15 }
    ],
    simulations: [
      { id: 1, title: '电话诈骗场景模拟', difficulty: '中等' },
      { id: 2, title: '网络钓鱼防范演练', difficulty: '高级' }
    ]
  }
}

const handleSearchBlur = () => {
  setTimeout(() => {
    showSearchPanel.value = false
  }, 200)
}

const handleSearchItemClick = (path: string) => {
  showSearchPanel.value = false
  searchQuery.value = ''
  router.push(path)
}

// 用户操作处理
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'points':
      router.push('/points')
      break
    case 'wrongbook':
      router.push('/wrongbook')
      break
    case 'favorites':
      router.push('/favorites')
      break
    case 'leaderboard':
      router.push('/leaderboard')
      break
    case 'achievement':
      router.push('/achievement')
      break
    case 'settings':
      router.push('/profile?tab=settings')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
      break
  }
}

// 滚动处理
const handleScroll = () => {
  showBackToTop.value = window.scrollY > 400
  isScrolled.value = window.scrollY > 50
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleBackToTopKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' || event.key === ' ') {
    event.preventDefault()
    scrollToTop()
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* 页面过渡动画 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity var(--transition-normal);
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

/* 滑动淡入淡出 */
.slide-fade-enter-active {
  transition: all var(--transition-normal);
}

.slide-fade-leave-active {
  transition: all var(--transition-fast);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* ==================== 全局头部 ==================== */
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border-bottom: 1px solid var(--border-primary);
  z-index: var(--z-fixed);
  padding: 0;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
}

.app-header.header-scrolled {
  background: var(--glass-bg-heavy);
  box-shadow: var(--shadow-md);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-6);
  gap: var(--spacing-6);
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-6);
  flex: 1;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  cursor: pointer;
  transition: all var(--transition-fast);
  padding: var(--spacing-2) 0;
}

.logo:hover {
  opacity: 0.8;
}

.logo:hover .logo-icon {
  transform: rotate(10deg) scale(1.1);
}

.logo-icon {
  font-size: 28px;
  color: var(--primary-color);
  transition: transform var(--transition-normal);
}

.logo-text {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  white-space: nowrap;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-badge {
  padding: var(--spacing-1) var(--spacing-2);
  background: var(--gradient-success);
  color: white;
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
  border-radius: var(--radius-sm);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

/* 导航菜单 */
.nav-menu {
  border: none;
  background: transparent;
  flex: 1;
}

.nav-menu :deep(.el-menu-item),
.nav-menu :deep(.el-sub-menu__title) {
  height: 64px;
  line-height: 64px;
  padding: 0 var(--spacing-4);
  border-bottom: 3px solid transparent;
  transition: all var(--transition-fast);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-sub-menu__title:hover) {
  background: var(--bg-hover);
  color: var(--primary-color);
}

.nav-menu :deep(.el-menu-item.is-active) {
  border-bottom-color: var(--primary-color);
  color: var(--primary-color);
  background: var(--primary-bg);
}

/* 右侧区域 */
.header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
}

/* 全局搜索 */
.global-search {
  position: relative;
  width: 320px;
  transition: width var(--transition-normal);
}

.global-search:focus-within {
  width: 380px;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-full);
  background: var(--bg-secondary);
  border: 1px solid var(--border-primary);
  box-shadow: none;
  transition: all var(--transition-fast);
  height: 40px;
}

.search-input :deep(.el-input__wrapper:hover),
.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px var(--primary-bg);
  background: var(--bg-primary);
}

/* 搜索面板 */
.search-panel {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl);
  z-index: var(--z-dropdown);
  max-height: 400px;
  overflow-y: auto;
}

.search-results {
  padding: var(--spacing-3);
}

.search-section {
  margin-bottom: var(--spacing-3);
}

.search-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-3);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--text-muted);
}

.search-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-2) var(--spacing-3);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background var(--transition-fast);
}

.search-item:hover {
  background: var(--bg-hover);
}

.search-item span {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
}

.search-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-6);
  color: var(--text-muted);
  gap: var(--spacing-2);
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  gap: var(--spacing-2);
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  cursor: pointer;
  padding: var(--spacing-2) var(--spacing-3);
  border-radius: var(--radius-lg);
  transition: background var(--transition-fast);
}

.user-info:hover {
  background: var(--bg-hover);
}

.user-avatar {
  border: 2px solid var(--primary-color);
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

.user-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.points {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-xs);
  color: var(--warning-color);
}

.dropdown-icon {
  color: var(--text-muted);
}

/* 下拉菜单 */
.user-dropdown-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  padding: var(--spacing-4);
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-primary);
}

.dropdown-user-info {
  display: flex;
  flex-direction: column;
}

.dropdown-username {
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.dropdown-role {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

/* ==================== 主内容区 ==================== */
.app-main {
  min-height: 100vh;
  padding: 0;
  margin-top: 64px;
  background: var(--bg-secondary);
}

/* ==================== 底部 ==================== */
.app-footer {
  background: var(--text-primary);
  color: white;
  padding: var(--spacing-12) 0 var(--spacing-6);
}

.footer-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6);
}

.footer-main {
  display: grid;
  grid-template-columns: 2fr repeat(3, 1fr);
  gap: var(--spacing-8);
  margin-bottom: var(--spacing-6);
}

.footer-section h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-4);
}

.footer-section p {
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section li {
  margin-bottom: var(--spacing-2);
}

.footer-section a {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-size: var(--font-size-sm);
  transition: color var(--transition-fast);
}

.footer-section a:hover {
  color: white;
}

.social-links {
  display: flex;
  gap: var(--spacing-2);
}

.social-links .el-button {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: white;
}

.social-links .el-button:hover {
  background: rgba(255, 255, 255, 0.2);
}

.footer-bottom {
  text-align: center;
  padding-top: var(--spacing-6);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-bottom p {
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: var(--spacing-2);
}

.footer-stats {
  display: flex;
  justify-content: center;
  gap: var(--spacing-6);
}

.footer-stats span {
  color: rgba(255, 255, 255, 0.7);
}

.footer-stats strong {
  color: var(--primary-light);
}

/* ==================== 回到顶部 ==================== */
.back-to-top {
  position: fixed;
  bottom: var(--spacing-8);
  right: var(--spacing-8);
  width: 48px;
  height: 48px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  cursor: pointer;
  box-shadow: var(--shadow-lg);
  z-index: var(--z-fixed);
  transition: all var(--transition-normal);
}

.back-to-top:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

/* ==================== 响应式 ==================== */
@media (max-width: 1024px) {
  .hide-mobile {
    display: none !important;
  }

  .global-search {
    width: 240px;
  }

  .global-search:focus-within {
    width: 280px;
  }

  .footer-main {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }

  .header-right {
    gap: var(--spacing-2);
  }

  .quick-actions {
    display: none;
  }

  .auth-buttons .el-button {
    padding: var(--spacing-2) var(--spacing-3);
    font-size: var(--font-size-sm);
  }

  .footer-main {
    grid-template-columns: 1fr;
    gap: var(--spacing-6);
  }

  .footer-stats {
    flex-direction: column;
    gap: var(--spacing-2);
  }

  .back-to-top {
    bottom: var(--spacing-4);
    right: var(--spacing-4);
    width: 40px;
    height: 40px;
  }
  
  /* 搜索框适配 */
  .global-search {
    width: 160px;
  }
  
  .global-search:focus-within {
    width: 200px;
  }
  
  /* 隐藏移动端不需要的元素 */
  .user-details {
    display: none;
  }
  
  .logo-text {
    display: none;
  }
  
  .logo {
    gap: var(--spacing-2);
  }
  
  .logo-icon {
    font-size: 24px;
  }
  
  .header-content {
    padding: 0 var(--spacing-4);
  }
}
</style>
