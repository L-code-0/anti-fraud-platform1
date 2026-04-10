/**
 * 数据加载状态管理 composable
 * 简化组件中的加载状态、错误处理和骨架屏逻辑
 */
import { ref, computed, type Ref } from 'vue'
import { useLoadingStore } from '@/stores/loading'
import { MessageService } from '@/utils/message'

interface UseAsyncDataOptions<T> {
  /** 加载成功后的回调 */
  onSuccess?: (data: T) => void
  /** 加载失败后的回调 */
  onError?: (error: unknown) => void
  /** 是否显示加载动画 */
  showLoading?: boolean
  /** 加载提示文本 */
  loadingText?: string
}

interface UseAsyncDataReturn<T> {
  /** 数据 */
  data: Ref<T | null>
  /** 是否加载中 */
  loading: Ref<boolean>
  /** 是否有错误 */
  error: Ref<unknown>
  /** 是否加载成功 */
  isSuccess: Ref<boolean>
  /** 执行异步操作 */
  execute: (fn: () => Promise<T>) => Promise<T | undefined>
  /** 重置状态 */
  reset: () => void
}

/**
 * 处理异步数据加载
 */
export function useAsyncData<T = any>(options: UseAsyncDataOptions<T> = {}): UseAsyncDataReturn<T> {
  const loadingStore = useLoadingStore()
  
  const data = ref<T | null>(null) as Ref<T | null>
  const loading = ref(false)
  const error = ref<unknown>(null)
  const isSuccess = ref(false)
  
  const execute = async (fn: () => Promise<T>): Promise<T | undefined> => {
    // 如果有全局loadingStore使用它
    if (options.showLoading) {
      return loadingStore.wrap(async () => {
        const result = await fn()
        data.value = result
        isSuccess.value = true
        options.onSuccess?.(result)
        return result
      }, { 
        text: options.loadingText,
        showError: true,
        showSuccess: false
      }).then(result => result as T | undefined)
    }
    
    // 使用组件本地状态
    loading.value = true
    error.value = null
    isSuccess.value = false
    
    try {
      const result = await fn()
      data.value = result
      isSuccess.value = true
      options.onSuccess?.(result)
      return result
    } catch (err) {
      error.value = err
      options.onError?.(err)
      return undefined
    } finally {
      loading.value = false
    }
  }
  
  const reset = () => {
    data.value = null
    loading.value = false
    error.value = null
    isSuccess.value = false
  }
  
  return {
    data,
    loading,
    error,
    isSuccess,
    execute,
    reset
  }
}

/**
 * 分页数据加载
 */
interface UsePaginatedDataOptions<T> extends UseAsyncDataOptions<T[]> {
  /** 初始页码 */
  initialPage?: number
  /** 每页大小 */
  pageSize?: number
}

interface UsePaginatedDataReturn<T> extends UseAsyncDataReturn<T[]> {
  /** 当前页码 */
  page: Ref<number>
  /** 每页大小 */
  pageSize: Ref<number>
  /** 总数据量 */
  total: Ref<number>
  /** 总页数 */
  totalPages: computed<number>
  /** 是否还有更多数据 */
  hasMore: computed<boolean>
  /** 加载下一页 */
  loadMore: () => Promise<void>
  /** 刷新数据 */
  refresh: () => Promise<void>
  /** 跳转到指定页 */
  goToPage: (page: number) => Promise<void>
}

export function usePaginatedData<T = any>(
  fetchFn: (page: number, pageSize: number) => Promise<{ list: T[]; total: number }>,
  options: UsePaginatedDataOptions<T> = {}
): UsePaginatedDataReturn<T> {
  const { 
    initialPage = 1, 
    pageSize: initialPageSize = 10,
    ...asyncOptions 
  } = options
  
  const page = ref(initialPage)
  const pageSize = ref(initialPageSize)
  const total = ref(0)
  
  const { data, loading, error, isSuccess, execute, reset } = useAsyncData<T[]>(asyncOptions)
  
  const totalPages = computed(() => Math.ceil(total.value / pageSize.value))
  const hasMore = computed(() => page.value < totalPages.value)
  
  const fetchData = async () => {
    await execute(async () => {
      const result = await fetchFn(page.value, pageSize.value)
      total.value = result.total
      return result.list
    })
  }
  
  // 初始加载
  if (options.showLoading !== false) {
    fetchData()
  }
  
  const loadMore = async () => {
    if (!hasMore.value && !loading.value) return
    page.value++
    await fetchData()
  }
  
  const refresh = async () => {
    page.value = initialPage
    await fetchData()
  }
  
  const goToPage = async (targetPage: number) => {
    if (targetPage < 1 || targetPage > totalPages.value) return
    page.value = targetPage
    await fetchData()
  }
  
  return {
    data,
    loading,
    error,
    isSuccess,
    page,
    pageSize,
    total,
    totalPages,
    hasMore,
    execute: fetchData,
    loadMore,
    refresh,
    goToPage,
    reset
  }
}

export default {
  useAsyncData,
  usePaginatedData
}
