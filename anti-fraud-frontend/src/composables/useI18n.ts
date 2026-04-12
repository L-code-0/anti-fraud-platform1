/**
 * 国际化组合式函数
 */
import { computed } from 'vue'
import { getLocale, setLocale, t, Locale } from '@/i18n'

export function useI18n() {
  // 当前语言
  const locale = computed(() => getLocale())
  
  // 切换语言
  const changeLocale = (newLocale: Locale) => {
    setLocale(newLocale)
  }
  
  // 翻译函数
  const translate = (key: string, fallback?: string) => {
    return t(key, fallback)
  }
  
  return {
    locale,
    changeLocale,
    t: translate
  }
}
