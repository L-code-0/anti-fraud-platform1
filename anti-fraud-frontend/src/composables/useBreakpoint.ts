import { ref, onMounted, onUnmounted } from 'vue'

interface Breakpoints {
  xs: number  // < 576px
  sm: number  // >= 576px
  md: number  // >= 768px
  lg: number  // >= 992px
  xl: number  // >= 1200px
  xxl: number // >= 1400px
}

const breakpoints: Breakpoints = {
  xs: 0,
  sm: 576,
  md: 768,
  lg: 992,
  xl: 1200,
  xxl: 1400
}

export function useBreakpoint() {
  const width = ref(0)
  const height = ref(0)
  
  const isXs = ref(false)
  const isSm = ref(false)
  const isMd = ref(false)
  const isLg = ref(false)
  const isXl = ref(false)
  const isXxl = ref(false)
  
  const isMobile = ref(false)
  const isTablet = ref(false)
  const isDesktop = ref(false)

  const update = () => {
    width.value = window.innerWidth
    height.value = window.innerHeight
    
    isXs.value = width.value < breakpoints.sm
    isSm.value = width.value >= breakpoints.sm && width.value < breakpoints.md
    isMd.value = width.value >= breakpoints.md && width.value < breakpoints.lg
    isLg.value = width.value >= breakpoints.lg && width.value < breakpoints.xl
    isXl.value = width.value >= breakpoints.xl && width.value < breakpoints.xxl
    isXxl.value = width.value >= breakpoints.xxl
    
    isMobile.value = width.value < breakpoints.md
    isTablet.value = width.value >= breakpoints.md && width.value < breakpoints.lg
    isDesktop.value = width.value >= breakpoints.lg
  }

  onMounted(() => {
    update()
    window.addEventListener('resize', update)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', update)
  })

  return {
    width,
    height,
    isXs,
    isSm,
    isMd,
    isLg,
    isXl,
    isXxl,
    isMobile,
    isTablet,
    isDesktop,
    breakpoints
  }
}
