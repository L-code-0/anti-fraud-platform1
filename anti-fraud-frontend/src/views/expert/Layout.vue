<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapsed ? '64px' : '220px'" class="admin-aside">
        <div class="logo" @click="toggleCollapse">
          <div class="logo-icon">
            <el-icon :size="24"><Monitor /></el-icon>
          </div>
          <transition name="fade">
            <span v-if="!isCollapsed" class="logo-text">专家中心</span>
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
            <el-menu-item index="/expert">
              <el-icon><DataAnalysis /></el-icon>
              <template #title>专家首页</template>
            </el-menu-item>
            
            <el-menu-item index="/expert/analysis">
              <el-icon><TrendCharts /></el-icon>
              <template #title>案例分析</template>
            </el-menu-item>
            
            <el-menu-item index="/expert/advice">
              <el-icon><ChatDotRound /></el-icon>
              <template #title>专家建议</template>
            </el-menu-item>
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
              <el-breadcrumb-item :to="{ path: '/expert' }">
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
            <el-tooltip content="返回首页" placement="bottom">
              <el-button :icon="HomeFilled" circle size="small" @click="$router.push('/')" />
            </el-tooltip>
          </div>
        </el-header>
        
        <el-main class="admin-main">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Fold, Expand, HomeFilled, Refresh, DataAnalysis, TrendCharts, ChatDotRound, Monitor, Lock } from '@element-plus/icons-vue'

const route = useRoute()
const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => {
  return route.meta.title || '专家中心'
})

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const refreshPage = () => {
  window.location.reload()
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.admin-aside {
  background: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  cursor: pointer;
  border-bottom: 1px solid var(--border-light);
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: var(--gradient-primary);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.logo-text {
  margin-left: 12px;
  font-weight: 600;
  font-size: 16px;
  color: var(--text-primary);
  white-space: nowrap;
}

.menu-scrollbar {
  flex: 1;
  overflow-y: auto;
}

.admin-menu {
  border: none;
  padding: 8px 0;
}

.admin-menu:not(.el-menu--collapse) {
  width: 220px;
}

.collapse-btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid var(--border-light);
  cursor: pointer;
  color: var(--text-secondary);
  transition: background 0.2s;
}

.collapse-btn:hover {
  background: var(--bg-light);
}

.main-container {
  margin-left: 220px;
  transition: margin-left 0.3s;
  flex-direction: column;
  min-height: 100vh;
}

.admin-header {
  background: #fff;
  border-bottom: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  position: sticky;
  top: 0;
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

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.admin-main {
  padding: 20px;
  background: var(--bg-page);
  min-height: calc(100vh - 60px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
