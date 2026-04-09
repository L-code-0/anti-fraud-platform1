<template>
  <div id="app" :class="{ 'dark-mode': isDark }">
    <el-container class="app-container">
      <!-- 全局顶部导航栏 -->
      <el-header class="app-header" :class="{ 'header-scrolled': isScrolled }">
        <div class="header-content">
          <!-- 左侧：Logo和导航 -->
          <div class="header-left">
            <div class="logo" @click="$router.push('/')">
              <el-icon class="logo-icon"><Lock /></el-icon>
              <span class="logo-text">高校反诈安全</span>
              <el-tag size="small" type="success" class="logo-badge">v2.0</el-tag>
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
              <el-menu-item index="/class">班级管理</el-menu-item>
              <el-menu-item index="/warning">智能预警</el-menu-item>
              <el-menu-item index="/analysis/data">数据分析</el-menu-item>
              <!-- 管理菜单 - 仅管理员可见 -->
              <el-sub-menu v-if="Number(userStore.userInfo?.roleId) >= 3" index="/admin">
                <template #title>
                  <el-icon><Setting /></el-icon>管理后台
                </template>
                <el-menu-item index="/admin">仪表盘</el-menu-item>
                <el-menu-item index="/admin/users">用户管理</el-menu-item>
                <el-menu-item index="/admin/knowledge">知识管理</el-menu-item>
                <el-menu-item index="/admin/questions">题库管理</el-menu-item>
                <el-menu-item index="/admin/analysis">数据分析</el-menu-item>
              </el-sub-menu>
              <!-- 专家菜单 - 仅专家可见 -->
              <el-sub-menu v-if="Number(userStore.userInfo?.roleId) === 4" index="/expert">
                <template #title>
                  <el-icon><Monitor /></el-icon>专家中心
                </template>
                <el-menu-item index="/expert">专家首页</el-menu-item>
                <el-menu-item index="/expert/analysis">案例分析</el-menu-item>
                <el-menu-item index="/expert/advice">专家建议</el-menu-item>
              </el-sub-menu>
              <el-menu-item index="/activity" class="hide-mobile">活动中心</el-menu-item>
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
                    <div v-if="!searchResults.knowledge && !searchResults.tests && !searchResults.simulations" class="search-empty">
                      <el-icon><Search /></el-icon>
                      <span>未找到相关结果</span>
                    </div>
                    <template v-else>
                      <div class="search-section" v-if="searchResults.knowledge?.length">
                        <div class="section-title">知识内容</div>
                        <div 
                          v-for="item in searchResults.knowledge" 
                          :key="item.id"
                          class="search-item"
                          @click="handleSearchItemClick('/knowledge/' + item.id)"
                        >
                          <el-icon><Document /></el-icon>
                          <span>{{ item.title }}</span>
                          <el-tag size="small">{{ item.category }}</el-tag>
                        </div>
                      </div>
                      <div class="search-section" v-if="searchResults.tests?.length">
                        <div class="section-title">测试试卷</div>
                        <div 
                          v-for="item in searchResults.tests" 
                          :key="item.id"
                          class="search-item"
                          @click="handleSearchItemClick('/test/' + item.id)"
                        >
                          <el-icon><DocumentCopy /></el-icon>
                          <span>{{ item.title }}</span>
                          <el-tag size="small" type="warning">{{ item.questionCount }}题</el-tag>
                        </div>
                      </div>
                      <div class="search-section" v-if="searchResults.simulations?.length">
                        <div class="section-title">演练场景</div>
                        <div 
                          v-for="item in searchResults.simulations" 
                          :key="item.id"
                          class="search-item"
                          @click="handleSearchItemClick('/simulation/' + item.id)"
                        >
                          <el-icon><VideoPlay /></el-icon>
                          <span>{{ item.title }}</span>
                          <el-tag size="small" type="success">{{ item.difficulty }}</el-tag>
                        </div>
                      </div>
                    </template>
                  </div>
                </div>
              </transition>
            </div>
            
            <!-- 快捷操作 -->
            <div class="quick-actions">
              <el-tooltip content="排行榜" placement="bottom">
                <el-button :icon="Rank" circle @click="$router.push('/leaderboard')" />
              </el-tooltip>
              <el-tooltip content="成就中心" placement="bottom">
                <el-button :icon="Trophy" circle @click="$router.push('/achievement')" />
              </el-tooltip>
              <el-tooltip content="帮助中心" placement="bottom">
                <el-button :icon="QuestionFilled" circle @click="$router.push('/help')" />
              </el-tooltip>
            </div>
            
            <!-- 主题切换 -->
            <ThemeToggle />
            
            <!-- 消息通知 -->
            <NotificationPanel />
            
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
                        <el-icon><Star /></el-icon>
                        {{ userStore.userInfo?.points || 0 }}积分
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
                    <el-dropdown-item command="analysis">
                      <el-icon><TrendCharts /></el-icon>学习报告
                    </el-dropdown-item>
                    <el-dropdown-item command="qa">
                      <el-icon><ChatDotRound /></el-icon>在线答疑
                    </el-dropdown-item>
                    <el-dropdown-item divided command="wrongbook">
                      <el-icon><DocumentCopy /></el-icon>错题本
                    </el-dropdown-item>
                    <el-dropdown-item command="favorites">
                      <el-icon><Star /></el-icon>我的收藏
                    </el-dropdown-item>
                    <el-dropdown-item command="notes">
                      <el-icon><Edit /></el-icon>我的笔记
                    </el-dropdown-item>
                    <el-dropdown-item command="leaderboard">
                      <el-icon><Medal /></el-icon>排行榜
                    </el-dropdown-item>
                    <el-dropdown-item command="achievement">
                      <el-icon><Trophy /></el-icon>成就中心
                    </el-dropdown-item>
                    <el-dropdown-item command="feedback">
                      <el-icon><ChatLineSquare /></el-icon>意见反馈
                    </el-dropdown-item>
                    <el-dropdown-item command="notifications">
                      <el-icon><Bell /></el-icon>消息中心
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
        <div class="page-container">
          <router-view v-slot="{ Component }">
            <transition name="page-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
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
                  <el-button circle><el-icon><ChatDotRound /></el-icon></el-button>
                </el-tooltip>
                <el-tooltip content="微博">
                  <el-button circle><el-icon><Promotion /></el-icon></el-button>
                </el-tooltip>
                <el-tooltip content="抖音">
                  <el-button circle><el-icon><VideoPlay /></el-icon></el-button>
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
      <div v-if="showBackToTop" class="back-to-top" @click="scrollToTop">
        <el-icon><Top /></el-icon>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import NotificationPanel from '@/components/common/NotificationPanel.vue'
import { 
  Lock, SwitchButton, Setting, Monitor, User, Star, TrendCharts, 
  ChatDotRound, QuestionFilled, DocumentCopy, Rank, Trophy, Medal, 
  Bell, Search, Document, ChatLineSquare, Coin, Edit, ArrowDown,
  VideoPlay, Promotion, Top, ChatLineSquare as ChatSquare
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 搜索相关
const searchQuery = ref('')
const showSearchPanel = ref(false)
const searchResults = ref<{
  knowledge?: any[]
  tests?: any[]
  simulations?: any[]
}>({})

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
    searchResults.value = {}
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
    case 'analysis':
      router.push('/analysis')
      break
    case 'qa':
      router.push('/qa')
      break
    case 'wrongbook':
      router.push('/wrongbook')
      break
    case 'favorites':
      router.push('/favorites')
      break
    case 'notes':
      router.push('/notes')
      break
    case 'leaderboard':
      router.push('/leaderboard')
      break
    case 'achievement':
      router.push('/achievement')
      break
    case 'feedback':
      router.push('/feedback')
      break
    case 'notifications':
      router.push('/notifications')
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
  transition: opacity 0.3s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

/* 滑动淡入淡出 */
.slide-fade-enter-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-fade-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 全局头部 */
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: var(--bg-glass, rgba(255, 255, 255, 0.85));
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border-color);
  z-index: var(--z-fixed);
  padding: 0;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.app-header.header-scrolled {
  background: var(--bg-glass, rgba(255, 255, 255, 0.95));
  box-shadow: var(--shadow-md);
}

:global(.dark) .app-header {
  background: rgba(22, 33, 62, 0.9);
}

:global(.dark) .app-header.header-scrolled {
  background: rgba(22, 33, 62, 0.95);
}

.header-content {
  max-width: 1600px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  gap: 32px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 32px;
  flex: 1;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 8px 0;
}

.logo:hover {
  opacity: 0.8;
  transform: translateY(-2px);
}

.logo-icon {
  font-size: 32px;
  color: var(--primary-color);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo:hover .logo-icon {
  transform: rotate(5deg);
}

.logo-text {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  white-space: nowrap;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-badge {
  margin-left: 8px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

.nav-menu {
  border: none;
  background: transparent;
  flex: 1;
}

.nav-menu :deep(.el-menu-item),
.nav-menu :deep(.el-sub-menu__title) {
  height: 64px;
  line-height: 64px;
  padding: 0 20px;
  border-bottom: 3px solid transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-sub-menu__title:hover) {
  background: var(--hover-bg-color);
  color: var(--primary-color);
}

.nav-menu :deep(.el-menu-item.is-active) {
  border-bottom-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(64, 158, 255, 0.05);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 全局搜索 */
.global-search {
  position: relative;
  width: 360px;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.global-search:focus-within {
  width: 400px;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 24px;
  background: var(--bg-page);
  border: 1px solid var(--border-color);
  box-shadow: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 40px;
}

.search-input :deep(.el-input__wrapper:hover),
.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
  background: var(--bg-card);
}

.search-panel {
  position: absolute;
  top: calc(100% + 12px);
  left: 0;
  right: 0;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  max-height: 480px;
  overflow-y: auto;
  z-index: var(--z-dropdown);
  animation: slideDown 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.search-results {
  padding: 16px;
}

.search-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px 24px;
  color: var(--text-secondary);
}

.search-empty el-icon {
  font-size: 48px;
  opacity: 0.5;
}

.search-section {
  margin-bottom: 20px;
}

.search-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--text-secondary);
  padding: 8px 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  background: var(--hover-bg-color);
  border-radius: var(--radius-md);
  margin-bottom: 8px;
}

.search-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.search-item:hover {
  background: var(--hover-bg-color);
  border-color: var(--primary-light);
  transform: translateX(4px);
}

.search-item span {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: var(--font-weight-medium);
}

.search-item el-icon {
  font-size: 18px;
  color: var(--primary-color);
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  gap: 8px;
  background: var(--bg-card);
  padding: 4px;
  border-radius: var(--radius-full);
  border: 1px solid var(--border-color);
}

.quick-actions el-button {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.quick-actions el-button:hover {
  transform: translateY(-2px);
  color: var(--primary-color);
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: var(--radius-xl);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.user-info:hover {
  background: var(--hover-bg-color);
  border-color: var(--primary-light);
  transform: translateY(-2px);
}

.user-avatar {
  border: 2px solid var(--primary-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-info:hover .user-avatar {
  transform: scale(1.1);
  box-shadow: 0 0 12px rgba(64, 158, 255, 0.4);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  font-size: var(--font-size-sm);
}

.user-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.points {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--font-size-xs);
  color: var(--warning-color);
  font-weight: var(--font-weight-medium);
}

.dropdown-icon {
  font-size: 14px;
  color: var(--text-secondary);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-info:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* 用户下拉头部 */
.user-dropdown-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--bg-gradient);
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 8px;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.dropdown-user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.dropdown-username {
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  font-size: var(--font-size-base);
}

.dropdown-role {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  background: rgba(64, 158, 255, 0.1);
  padding: 2px 8px;
  border-radius: var(--radius-full);
  align-self: flex-start;
}

/* 登出项 */
.logout-item {
  color: var(--danger-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logout-item:hover {
  background: rgba(245, 108, 108, 0.1);
}

/* 认证按钮 */
.auth-buttons {
  display: flex;
  gap: 12px;
}

.auth-buttons el-button {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.auth-buttons el-button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

/* 主内容区 */
.app-main {
  margin-top: 64px;
  min-height: calc(100vh - 64px - 240px);
  background: var(--bg-page);
  padding: 0;
}

.page-container {
  max-width: 1600px;
  margin: 0 auto;
  padding: 32px;
}

/* 超宽屏优化 */
@media (min-width: 1920px) {
  .page-container {
    max-width: 1800px;
    padding: 40px;
  }
  
  .header-content {
    max-width: 1800px;
  }
}

/* 底部 */
.app-footer {
  background: var(--bg-card);
  border-top: 1px solid var(--border-color);
  padding: 64px 32px 32px;
  margin-top: 40px;
}

.footer-content {
  max-width: 1600px;
  margin: 0 auto;
}

.footer-main {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 64px;
  margin-bottom: 32px;
}

@media (max-width: 1200px) {
  .footer-main {
    grid-template-columns: repeat(2, 1fr);
    gap: 48px;
  }
}

@media (max-width: 768px) {
  .footer-main {
    grid-template-columns: 1fr;
    gap: 32px;
  }
}

.footer-section h4 {
  margin-bottom: 20px;
  font-size: var(--font-size-lg);
  color: var(--text-primary);
  font-weight: var(--font-weight-semibold);
  position: relative;
  padding-bottom: 8px;
}

.footer-section h4::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), var(--primary-light));
  border-radius: var(--radius-full);
}

.footer-section p {
  color: var(--text-secondary);
  line-height: var(--line-height-relaxed);
  margin-bottom: 16px;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section li {
  margin-bottom: 12px;
}

.footer-section a {
  color: var(--text-secondary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-block;
  padding: 4px 0;
  position: relative;
}

.footer-section a::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--primary-color);
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.footer-section a:hover {
  color: var(--primary-color);
  transform: translateX(4px);
}

.footer-section a:hover::after {
  width: 100%;
}

.social-links {
  display: flex;
  gap: 12px;
}

.social-links el-button {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border-color);
}

.social-links el-button:hover {
  transform: translateY(-3px) rotate(5deg);
  border-color: var(--primary-color);
  color: var(--primary-color);
  box-shadow: var(--shadow-md);
}

.footer-bottom {
  text-align: center;
  padding-top: 32px;
  border-top: 1px solid var(--border-color);
}

.footer-bottom p {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  margin-bottom: 16px;
}

.footer-stats {
  display: flex;
  justify-content: center;
  gap: 32px;
  flex-wrap: wrap;
  margin-bottom: 24px;
}

.footer-stats span {
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 6px;
}

.footer-stats strong {
  color: var(--primary-color);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
}

/* 回到顶部 */
.back-to-top {
  position: fixed;
  bottom: 32px;
  right: 32px;
  width: 52px;
  height: 52px;
  background: var(--bg-card);
  border: 2px solid var(--primary-color);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: var(--shadow-lg);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: var(--z-sticky);
  color: var(--primary-color);
}

.back-to-top:hover {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
  transform: translateY(-6px) scale(1.1);
  box-shadow: 0 10px 25px rgba(64, 158, 255, 0.4);
}

.back-to-top el-icon {
  font-size: 20px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.back-to-top:hover el-icon {
  transform: translateY(-2px);
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .header-content {
    padding: 0 24px;
    gap: 24px;
  }
  
  .global-search {
    width: 300px;
  }
  
  .global-search:focus-within {
    width: 340px;
  }
  
  .header-left {
    gap: 24px;
  }
}

@media (max-width: 992px) {
  .header-content {
    padding: 0 16px;
    gap: 16px;
  }
  
  .logo-text {
    display: none;
  }
  
  .global-search {
    width: 240px;
  }
  
  .global-search:focus-within {
    width: 280px;
  }
  
  .quick-actions {
    display: none;
  }
  
  .nav-menu :deep(.el-menu-item),
  .nav-menu :deep(.el-sub-menu__title) {
    padding: 0 16px;
  }
}

@media (max-width: 768px) {
  .app-header {
    height: 56px;
  }
  
  .app-main {
    margin-top: 56px;
    min-height: calc(100vh - 56px - 200px);
  }
  
  .page-container {
    padding: 16px;
  }
  
  .global-search {
    display: none;
  }
  
  .user-details {
    display: none;
  }
  
  .header-right {
    gap: 8px;
  }
  
  .app-footer {
    padding: 32px 16px 24px;
  }
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>



