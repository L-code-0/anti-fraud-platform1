import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

// 引入全局样式 - 设计系统 
import './styles/design-system.css'  // 新设计系统变量
import './styles/components.css'      // 通用组件样式
import './styles/animations.css'      // 动画样式

// 引入国际化
import { getLocale } from './i18n'

// 引入WebSocket客户端
import { webSocketClient } from './utils/websocket'

// 引入图片懒加载指令
import registerLazyLoadDirective from './directives/lazyLoad'

// 获取当前语言
const currentLocale = getLocale()
// 设置Element Plus的语言
const elementLocale = currentLocale === 'zh-CN' ? zhCn : en

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: elementLocale })

// 注册图片懒加载指令
registerLazyLoadDirective(app)

// 初始化WebSocket连接
const token = localStorage.getItem('token')
if (token) {
  const wsUrl = `ws://localhost:8080/ws?token=${token}`
  webSocketClient.init(wsUrl)
  
  // 注册消息处理器
  webSocketClient.on('notification', (data) => {
    console.log('收到通知:', data)
    // 这里可以触发通知提示
  })
  
  webSocketClient.on('realtime', (data) => {
    console.log('收到实时消息:', data)
    // 这里可以处理实时数据更新
  })
}

app.mount('#app')