import { ref, watch, onUnmounted, type Ref } from 'vue'

/**
 * 防抖函数 - Composable形式
 * @param value 需要防抖的值
 * @param delay 延迟时间（毫秒）
 */
export function useDebounce<T>(value: Ref<T>, delay: number = 300): Ref<T> {
  const debouncedValue = ref(value.value) as Ref<T>
  let timer: ReturnType<typeof setTimeout> | null = null

  watch(value, (newValue) => {
    if (timer) {
      clearTimeout(timer)
    }
    timer = setTimeout(() => {
      debouncedValue.value = newValue
    }, delay)
  })

  onUnmounted(() => {
    if (timer) {
      clearTimeout(timer)
    }
  })

  return debouncedValue
}

/**
 * 防抖函数 - 普通函数形式
 * @param fn 需要防抖的函数
 * @param delay 延迟时间（毫秒）
 */
export function debounce<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): (...args: Parameters<T>) => void {
  let timer: ReturnType<typeof setTimeout> | null = null

  return function (this: any, ...args: Parameters<T>) {
    if (timer) {
      clearTimeout(timer)
    }
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 节流函数 - Composable形式
 * @param value 需要节流的值
 * @param interval 间隔时间（毫秒）
 */
export function useThrottle<T>(value: Ref<T>, interval: number = 300): Ref<T> {
  const throttledValue = ref(value.value) as Ref<T>
  let lastTime = 0
  let timer: ReturnType<typeof setTimeout> | null = null

  watch(value, (newValue) => {
    const now = Date.now()
    
    if (now - lastTime >= interval) {
      throttledValue.value = newValue
      lastTime = now
    } else {
      // 确保最后一次更新会被执行
      if (timer) {
        clearTimeout(timer)
      }
      timer = setTimeout(() => {
        throttledValue.value = newValue
        lastTime = Date.now()
      }, interval - (now - lastTime))
    }
  })

  onUnmounted(() => {
    if (timer) {
      clearTimeout(timer)
    }
  })

  return throttledValue
}

/**
 * 节流函数 - 普通函数形式
 * @param fn 需要节流的函数
 * @param interval 间隔时间（毫秒）
 */
export function throttle<T extends (...args: any[]) => any>(
  fn: T,
  interval: number = 300
): (...args: Parameters<T>) => void {
  let lastTime = 0
  let timer: ReturnType<typeof setTimeout> | null = null

  return function (this: any, ...args: Parameters<T>) {
    const now = Date.now()
    
    if (now - lastTime >= interval) {
      fn.apply(this, args)
      lastTime = now
    } else {
      if (timer) {
        clearTimeout(timer)
      }
      timer = setTimeout(() => {
        fn.apply(this, args)
        lastTime = Date.now()
      }, interval - (now - lastTime))
    }
  }
}
