import type { App, Directive, DirectiveBinding } from 'vue'

// 图片懒加载指令
const lazyLoad: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const img = el as HTMLImageElement
    const src = binding.value
    
    // 设置占位图
    img.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2YzZjNmMyIvPjx0ZXh0IHg9IjEwMCIgeT0iMTAwIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTIiIGZpbGw9IiM2NjYiPuiAg+WKnzIwMHgyMDA8L3RleHQ+PC9zdmc+'
    
    // 创建观察器
    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          // 当图片进入视口时加载
          img.src = src
          // 加载后停止观察
          observer.unobserve(img)
        }
      })
    }, {
      threshold: 0.1 // 当图片10%进入视口时触发
    })
    
    // 开始观察
    observer.observe(img)
    
    // 存储观察器到元素上，以便在卸载时清理
    (img as any).__lazyObserver = observer
  },
  unmounted(el: HTMLElement) {
    const img = el as HTMLImageElement
    // 清理观察器
    if ((img as any).__lazyObserver) {
      (img as any).__lazyObserver.disconnect()
    }
  }
}

// 注册指令
const registerLazyLoadDirective = (app: App) => {
  app.directive('lazy', lazyLoad)
}

export default registerLazyLoadDirective
