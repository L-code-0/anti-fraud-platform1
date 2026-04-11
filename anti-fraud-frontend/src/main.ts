import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

// 引入全局样式 - 设计系统 
import './styles/design-system.css'  // 新设计系统变量
import './styles/components.css'      // 通用组件样式
import './styles/animations.css'      // 动画样式

// 引入图片懒加载指令
import registerLazyLoadDirective from './directives/lazyLoad'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

// 注册图片懒加载指令
registerLazyLoadDirective(app)

app.mount('#app')