<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapsed ? '64px' : '220px'" class="admin-aside">
        <div class="logo" @click="toggleCollapse">
          <div class="logo-icon">
            <el-icon :size="24"><Lock /></el-icon>
          </div>
          <transition name="fade">
            <span v-if="!isCollapsed" class="logo-text">后台管理</span>
          </transition>
        </div>
        
        <el-scrollbar class="menu-scrollbar">
          <el-menu
            :default-active="activeMenu"
            router
            :collapse="isCollapsed"
            :collapse-transition="false"
            background-color="transparent"
            text-color="var(--text-secondary)"
            active-text-color="var(--primary)"
            class="admin-menu"
          >
            <el-menu-item index="/admin">
              <el-icon><DataAnalysis /></el-icon>
              <template #title>仪表盘</template>
            </el-menu-item>
            
            <el-sub-menu index="user">
              <template #title>
                <el-icon><User /></el-icon>
                <span>用户管理</span>
              </template>
              <el-menu-item index="/admin/users">用户列表</el-menu-item>
            </el-sub-menu>
            
            <el-sub-menu index="content">
              <template #title>
                <el-icon><Document /></el-icon>
                <span>内容管理</span>
              </template>
              <el-menu-item index="/admin/knowledge">知识管理</el-menu-item>
              <el-menu-item index="/admin/questions">题库管理</el-menu-item>
              <el-menu-item index="/admin/papers">试卷管理</el-menu-item>
              <el-menu-item index="/admin/scenes">演练管理</el-menu-item>
            </el-sub-menu>
            
            <el-sub-menu index="report">
              <template #title>
                <el-icon><Warning /></el-icon>
                <span>举报预警</span>
              </template>
              <el-menu-item index="/admin/reports">举报管理</el-menu-item>
              <el-menu-item index="/admin/warnings">预警管理</el-menu-item>
            </el-sub-menu>
            
            <el-sub-menu index="activity">
              <template #title>
                <el-icon><Calendar /></el-icon>
                <span>宣传与活动</span>
              </template>
              <el-menu-item index="/admin/activities">活动管理</el-menu-item>
              <el-menu-item index="/admin/tasks">班级任务</el-menu-item>
              <el-menu-item index="/admin/works">作品管理</el-menu-item>
            </el-sub-menu>
            
            <el-menu-item index="/admin/stats">
              <el-icon><TrendCharts /></el-icon>
              <template #title>数据统计</template>
            </el-menu-item>
            
            <el-sub-menu index="system">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/admin/operation-log">操作日志</el-menu-item>
              <el-menu-item index="/admin/monitor">系统监控</el-menu-item>
              <el-menu-item index="/admin/data-export">数据导出</el-menu-item>
              <el-menu-item index="/admin/data-analysis">数据分析</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-scrollbar>
        
        <!-- 折叠按钮 -->
        <div class="collapse-btn" @click="toggleCollapse">
          <el-icon :size="18">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
        </div>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-container class="main-container">
        <el-header class="admin-header">
          <div class="header-left">
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item :to="{ path: '/admin' }">
                <el-icon><HomeFilled /></el-icon>
                <span>首页</span>
              </el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-tooltip content="刷新页面" placement="bottom">
              <el-button :icon="Refresh" circle size="small" @click="refreshPage" />
            </el-tooltip>
            <el-tooltip content="全屏" placement="bottom">
              <el-button :icon="FullScreen" circle size="small" @click="toggleFullScreen" />
            </el-tooltip>
            <el-dropdown trigger="click">
              <div class="user-info">
                <el-avatar :size="32" class="user-avatar">
                  {{ userInfo.realName?.charAt(0) || 'A' }}
                </el-avatar>
                <span class="user-name">{{ userInfo.realName || '管理员' }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/profile')">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="primary" text @click="$router.push('/')">
              <el-icon><Monitor /></el-icon>
              <span>返回前台</span>
            </el-button>
          </div>
        </el-header>
        
        <el-main class="admin-main">
          <router-view v-slot="{ Component }">
            <transition name="slide-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
        
        <el-footer class="admin-footer">
          <span>高校反诈安全知识普及平台 © 2024</span>
        </el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Fold, Expand, Refresh, FullScreen, Monitor, SwitchButton, Lock, DataAnalysis, User, Document, Warning, Calendar, TrendCharts, HomeFilled, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const isCollapsed = ref(false)
const userInfo = computed(() => {
  try {
    const stored = localStorage.getItem('userInfo')
    return stored && stored !== 'undefined' && stored !== 'null' 
      ? JSON.parse(stored) 
      : {}
  } catch {
    return {}
  }
})

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '')

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const refreshPage = () => {
  window.location.reload()
}

const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.admin-aside {
  background: linear-gradient(180deg, #1e3a5f 0%, #0f1f33 100%);
  height: 100vh;
  display: flex;
  flex-direction: column;
  transition: width var(--transition-normal);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 100;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 0 16px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all var(--transition-fast);
}

.logo:hover {
  background: rgba(255, 255, 255, 0.05);
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(43, 108, 176, 0.4);
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: white;
  white-space: nowrap;
  background: linear-gradient(90deg, #fff 0%, #a8c7e6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.menu-scrollbar {
  flex: 1;
  overflow: hidden;
}

.admin-menu {
  border-right: none;
}

.admin-menu :deep(.el-menu-item),
.admin-menu :deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
  margin: 4px 8px;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.admin-menu :deep(.el-menu-item:hover),
.admin-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(43, 108, 176, 0.3) 0%, rgba(43, 108, 176, 0.1) 100%) !important;
  border-left: 3px solid var(--primary);
  margin-left: 8px;
  padding-left: 17px !important;
}

.collapse-btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  transition: all var(--transition-fast);
}

.collapse-btn:hover {
  color: white;
  background: rgba(255, 255, 255, 0.05);
}

.main-container {
  display: flex;
  flex-direction: column;
  background: var(--bg-secondary);
}

.admin-header {
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 99;
}

.header-left {
  display: flex;
  align-items: center;
}

.breadcrumb {
  display: flex;
  align-items: center;
}

.breadcrumb :deep(.el-breadcrumb__item) {
  display: flex;
  align-items: center;
}

.breadcrumb :deep(.el-breadcrumb__inner) {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--text-secondary);
  transition: color var(--transition-fast);
}

.breadcrumb :deep(.el-breadcrumb__inner:hover) {
  color: var(--primary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
}

.user-info:hover {
  background: var(--bg-secondary);
}

.user-avatar {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  color: white;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.admin-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.admin-footer {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: var(--text-tertiary);
  background: white;
  border-top: 1px solid var(--border-light);
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s ease-in;
}

.slide-fade-enter-from {
  transform: translateX(20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateX(-20px);
  opacity: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .admin-aside {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform var(--transition-normal);
  }
  
  .admin-aside:not(.collapsed) {
    transform: translateX(0);
  }
  
  .user-name {
    display: none;
  }
  
  .admin-main {
    padding: 16px;
  }
}
</style>

