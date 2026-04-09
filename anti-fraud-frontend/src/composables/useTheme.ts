import { ref, onMounted } from 'vue'

export type ThemeMode = 'light' | 'dark' | 'auto'

const STORAGE_KEY = 'theme-mode'

// 全局状态
const currentTheme = ref<ThemeMode>('auto')
const isDark = ref(false)

// 获取系统主题
const getSystemTheme = (): 'light' | 'dark' => {
  if (typeof window !== 'undefined' && window.matchMedia) {
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
  }
  return 'light'
}

// 应用主题
const applyTheme = (theme: ThemeMode) => {
  const html = document.documentElement
  
  if (theme === 'auto') {
    const systemTheme = getSystemTheme()
    isDark.value = systemTheme === 'dark'
    
    if (isDark.value) {
      html.setAttribute('data-theme', 'dark')
    } else {
      html.removeAttribute('data-theme')
    }
  } else {
    isDark.value = theme === 'dark'
    
    if (theme === 'dark') {
      html.setAttribute('data-theme', 'dark')
    } else {
      html.setAttribute('data-theme', 'light')
    }
  }
}

// 切换主题
const toggleTheme = () => {
  const themes: ThemeMode[] = ['light', 'dark', 'auto']
  const currentIndex = themes.indexOf(currentTheme.value)
  const nextIndex = (currentIndex + 1) % themes.length
  setTheme(themes[nextIndex])
}

// 设置主题
const setTheme = (theme: ThemeMode) => {
  currentTheme.value = theme
  applyTheme(theme)
  localStorage.setItem(STORAGE_KEY, theme)
}

export function useTheme() {
  onMounted(() => {
    // 初始化主题
    const savedTheme = localStorage.getItem(STORAGE_KEY) as ThemeMode | null
    if (savedTheme) {
      currentTheme.value = savedTheme
    }
    applyTheme(currentTheme.value)
    
    // 监听系统主题变化
    if (typeof window !== 'undefined' && window.matchMedia) {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
      
      mediaQuery.addEventListener('change', () => {
        if (currentTheme.value === 'auto') {
          applyTheme('auto')
        }
      })
    }
  })
  
  return {
    currentTheme,
    isDark,
    setTheme,
    toggleTheme
  }
}
