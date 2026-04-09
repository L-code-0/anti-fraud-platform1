<template>
  <el-tooltip :content="themeStore.isDark ? '切换到浅色模式' : '切换到深色模式'" placement="bottom">
    <button class="theme-toggle" @click="themeStore.toggleTheme">
      <Transition name="rotate" mode="out-in">
        <el-icon :size="20" :key="themeStore.isDark ? 'dark' : 'light'">
          <Sunny v-if="themeStore.isDark" />
          <Moon v-else />
        </el-icon>
      </Transition>
    </button>
  </el-tooltip>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { Sunny, Moon } from '@element-plus/icons-vue'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()

onMounted(() => {
  themeStore.initTheme()
})
</script>

<style scoped>
.theme-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  color: inherit;
  cursor: pointer;
  transition: all 0.3s ease;
}

.theme-toggle:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.05);
}

.theme-toggle:active {
  transform: scale(0.95);
}

/* 旋转动画 */
.rotate-enter-active,
.rotate-leave-active {
  transition: all 0.3s ease;
}

.rotate-enter-from {
  opacity: 0;
  transform: rotate(-90deg);
}

.rotate-leave-to {
  opacity: 0;
  transform: rotate(90deg);
}
</style>
