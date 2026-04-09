import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

type Theme = 'light' | 'dark'

export const useThemeStore = defineStore('theme', () => {
  // 获取保存的主题
  const savedTheme = localStorage.getItem('theme') as Theme || 'light'
  const isDark = ref(savedTheme === 'dark')

  // 切换主题
  const toggleTheme = () => {
    isDark.value = !isDark.value
    const theme = isDark.value ? 'dark' : 'light'
    localStorage.setItem('theme', theme)
    applyTheme(theme)
  }

  // 应用主题
  const applyTheme = (theme: Theme) => {
    const root = document.documentElement
    if (theme === 'dark') {
      root.classList.add('dark')
      root.classList.remove('light')
    } else {
      root.classList.add('light')
      root.classList.remove('dark')
    }
  }

  // 初始化主题
  const initTheme = () => {
    applyTheme(isDark.value ? 'dark' : 'light')
  }

  // 监听主题变化
  watch(isDark, (newVal) => {
    applyTheme(newVal ? 'dark' : 'light')
  })

  return {
    isDark,
    toggleTheme,
    initTheme
  }
})