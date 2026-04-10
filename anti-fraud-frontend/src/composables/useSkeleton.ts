/**
 * 骨架屏状态管理 composable
 * 简化骨架屏的显示逻辑
 */
import { ref, computed, type Ref, type ComputedRef } from 'vue'

interface UseSkeletonOptions {
  /** 骨架屏类型 */
  type?: 'card' | 'list' | 'table' | 'detail' | 'stat' | 'user' | 'search' | 'tabs' | 'chart'
  /** 行数（用于列表和表格类型） */
  rows?: number
  /** 列数（用于表格类型） */
  columns?: number
  /** 图片高度 */
  imageHeight?: string
  /** 自定义宽度 */
  width?: string
  /** 自定义高度 */
  height?: string
}

interface UseSkeletonReturn {
  /** 是否显示骨架屏 */
  show: Ref<boolean>
  /** 骨架屏类型 */
  type: Ref<string>
  /** 行数 */
  rows: Ref<number>
  /** 列数 */
  columns: Ref<number>
  /** 图片高度 */
  imageHeight: Ref<string>
  /** 宽度 */
  width: Ref<string>
  /** 高度 */
  height: Ref<string>
  /** 是否正在加载（骨架屏可见） */
  isLoading: ComputedRef<boolean>
  /** 显示骨架屏 */
  start: () => void
  /** 隐藏骨架屏，显示内容 */
  end: () => void
  /** 切换状态 */
  toggle: () => void
  /** 配置骨架屏参数 */
  config: (options: UseSkeletonOptions) => void
}

/**
 * 骨架屏状态管理
 */
export function useSkeleton(options: UseSkeletonOptions = {}): UseSkeletonReturn {
  const show = ref(false)
  const type = ref(options.type || 'card')
  const rows = ref(options.rows || 4)
  const columns = ref(options.columns || 4)
  const imageHeight = ref(options.imageHeight || '160px')
  const width = ref(options.width || '100%')
  const height = ref(options.height || 'auto')
  
  const isLoading = computed(() => show.value)
  
  const start = () => {
    show.value = true
  }
  
  const end = () => {
    show.value = false
  }
  
  const toggle = () => {
    show.value = !show.value
  }
  
  const config = (newOptions: UseSkeletonOptions) => {
    if (newOptions.type !== undefined) type.value = newOptions.type
    if (newOptions.rows !== undefined) rows.value = newOptions.rows
    if (newOptions.columns !== undefined) columns.value = newOptions.columns
    if (newOptions.imageHeight !== undefined) imageHeight.value = newOptions.imageHeight
    if (newOptions.width !== undefined) width.value = newOptions.width
    if (newOptions.height !== undefined) height.value = newOptions.height
  }
  
  return {
    show,
    type,
    rows,
    columns,
    imageHeight,
    width,
    height,
    isLoading,
    start,
    end,
    toggle,
    config
  }
}

/**
 * 异步数据加载骨架屏
 */
interface UseDataWithSkeletonOptions<T> extends UseSkeletonOptions {
  /** 数据加载函数 */
  fetchData: () => Promise<T>
  /** 加载成功回调 */
  onSuccess?: (data: T) => void
  /** 加载失败回调 */
  onError?: (error: unknown) => void
}

interface UseDataWithSkeletonReturn<T> extends UseSkeletonReturn {
  /** 加载的数据 */
  data: Ref<T | null>
  /** 是否加载中 */
  loading: Ref<boolean>
  /** 错误信息 */
  error: Ref<unknown>
  /** 重新加载 */
  reload: () => Promise<void>
}

export function useDataWithSkeleton<T = any>(
  options: UseDataWithSkeletonOptions<T>
): UseDataWithSkeletonReturn<T> {
  const {
    fetchData,
    onSuccess,
    onError,
    ...skeletonOptions
  } = options
  
  const data = ref<T | null>(null)
  const loading = ref(false)
  const error = ref<unknown>(null)
  
  const skeleton = useSkeleton(skeletonOptions)
  
  const reload = async () => {
    skeleton.start()
    loading.value = true
    error.value = null
    
    try {
      const result = await fetchData()
      data.value = result
      onSuccess?.(result)
    } catch (err) {
      error.value = err
      onError?.(err)
    } finally {
      skeleton.end()
      loading.value = false
    }
  }
  
  // 初始加载
  reload()
  
  return {
    ...skeleton,
    data,
    loading,
    error,
    reload
  }
}

export default {
  useSkeleton,
  useDataWithSkeleton
}
