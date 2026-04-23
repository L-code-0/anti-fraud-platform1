<template>
  <div class="anti-cheat-monitor" v-if="enabled">
    <div class="monitor-indicator" :class="{ active: isMonitoring }">
      <el-icon><VideoCamera /></el-icon>
      <span>{{ isMonitoring ? '监控中' : '未监控' }}</span>
    </div>
    <div class="monitor-data" v-if="showData">
      <div class="data-item">
        <span class="label">切屏次数:</span>
        <span class="value">{{ monitorData.screenChanges }}</span>
      </div>
      <div class="data-item">
        <span class="label">鼠标移动:</span>
        <span class="value">{{ monitorData.mouseMovements }}</span>
      </div>
      <div class="data-item">
        <span class="label">答题速度:</span>
        <span class="value">{{ monitorData.answerSpeed.toFixed(1) }}s/题</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { post } from '@/utils/request'
import { VideoCamera } from '@element-plus/icons-vue'

const props = defineProps({
  enabled: {
    type: Boolean,
    default: true
  },
  examId: {
    type: Number,
    required: true
  },
  showData: {
    type: Boolean,
    default: false
  }
})

const isMonitoring = ref(false)
const monitorData = ref({
  screenChanges: 0,
  mouseMovements: 0,
  answerSpeed: 0,
  deviceCount: 1,
  tabSwitches: 0,
  copyPasteCount: 0,
  windowResizeCount: 0,
  keyboardInput: {
    typingSpeed: 0
  }
})

let screenChangeTimer: number | null = null
let mouseMoveTimer: number | null = null
let lastAnswerTime: number = 0
let answerCount: number = 0
let lastTabSwitchTime: number = 0
let keyPressCount: number = 0
let keyPressStartTime: number = 0

// 开始监控
const startMonitoring = () => {
  if (!props.enabled) return
  
  isMonitoring.value = true
  
  // 监控切屏
  monitorScreenChanges()
  
  // 监控鼠标移动
  monitorMouseMovements()
  
  // 监控答题速度
  monitorAnswerSpeed()
  
  // 监控标签页切换
  monitorTabSwitches()
  
  // 监控复制粘贴
  monitorCopyPaste()
  
  // 监控窗口大小变化
  monitorWindowResize()
  
  // 监控键盘输入
  monitorKeyboardInput()
  
  // 定期发送监控数据
  screenChangeTimer = window.setInterval(() => {
    sendMonitorData()
  }, 30000) // 每30秒发送一次
}

// 监控切屏
const monitorScreenChanges = () => {
  let lastVisibilityState = document.visibilityState
  document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'hidden' && lastVisibilityState === 'visible') {
      monitorData.value.screenChanges++
    }
    lastVisibilityState = document.visibilityState
  })
}

// 监控鼠标移动
const monitorMouseMovements = () => {
  document.addEventListener('mousemove', () => {
    monitorData.value.mouseMovements++
  })
}

// 监控答题速度
const monitorAnswerSpeed = () => {
  // 可以通过事件监听来捕获答题行为
  // 这里简化处理，实际项目中需要根据具体的答题组件来实现
}

// 监控标签页切换
const monitorTabSwitches = () => {
  window.addEventListener('blur', () => {
    const now = Date.now()
    if (now - lastTabSwitchTime > 1000) { // 避免频繁触发
      monitorData.value.tabSwitches++
      lastTabSwitchTime = now
    }
  })
}

// 监控复制粘贴
const monitorCopyPaste = () => {
  document.addEventListener('copy', () => {
    monitorData.value.copyPasteCount++
  })
  document.addEventListener('paste', () => {
    monitorData.value.copyPasteCount++
  })
}

// 监控窗口大小变化
const monitorWindowResize = () => {
  window.addEventListener('resize', () => {
    monitorData.value.windowResizeCount++
  })
}

// 监控键盘输入
const monitorKeyboardInput = () => {
  document.addEventListener('keydown', () => {
    if (keyPressStartTime === 0) {
      keyPressStartTime = Date.now()
    }
    keyPressCount++
    
    // 每10次按键计算一次打字速度
    if (keyPressCount % 10 === 0) {
      const now = Date.now()
      const timeElapsed = (now - keyPressStartTime) / 1000 // 秒
      if (timeElapsed > 0) {
        monitorData.value.keyboardInput.typingSpeed = (keyPressCount / timeElapsed) * 60 // 字符/分钟
      }
    }
  })
}

// 发送监控数据
const sendMonitorData = async () => {
  try {
    const data = JSON.stringify(monitorData.value)
    await post('/exam/monitor/record', {
      examId: props.examId,
      monitorType: 'user_behavior',
      monitorData: data
    })
  } catch (error) {
    console.error('发送监控数据失败:', error)
  }
}

// 检测作弊
const detectCheating = async () => {
  try {
    const data = JSON.stringify(monitorData.value)
    const res = await post('/exam/monitor/detect', {
      examId: props.examId,
      monitorData: data
    })
    if (res.code === 200 && res.data) {
      if (res.data.isCheating) {
        // 显示警告
        console.warn('检测到疑似作弊行为:', res.data.riskDescriptions)
      }
    }
  } catch (error) {
    console.error('检测作弊失败:', error)
  }
}

// 记录答题时间
const recordAnswerTime = () => {
  const now = Date.now()
  if (lastAnswerTime > 0) {
    const timeSpent = (now - lastAnswerTime) / 1000
    answerCount++
    monitorData.value.answerSpeed = answerCount > 0 ? 
      (monitorData.value.answerSpeed * (answerCount - 1) + timeSpent) / answerCount : 
      timeSpent
  }
  lastAnswerTime = now
}

// 暴露方法给父组件
defineExpose({
  startMonitoring,
  detectCheating,
  recordAnswerTime,
  getMonitorData: () => monitorData.value
})

onMounted(() => {
  startMonitoring()
})

onUnmounted(() => {
  if (screenChangeTimer) {
    clearInterval(screenChangeTimer)
  }
  if (mouseMoveTimer) {
    clearInterval(mouseMoveTimer)
  }
})
</script>

<style scoped>
.anti-cheat-monitor {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
}

.monitor-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--color-bg-page);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
}

.monitor-indicator:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.monitor-indicator.active {
  color: var(--color-success);
  border: 1px solid var(--color-success);
}

.monitor-data {
  margin-top: 8px;
  padding: 12px;
  background: var(--color-bg-page);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

.data-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.data-item:last-child {
  margin-bottom: 0;
}

.label {
  font-weight: var(--font-weight-medium);
}

.value {
  color: var(--color-text-primary);
}

@media (max-width: 768px) {
  .anti-cheat-monitor {
    bottom: 10px;
    right: 10px;
  }
  
  .monitor-indicator {
    padding: 6px 12px;
    font-size: var(--font-size-xs);
  }
  
  .monitor-data {
    padding: 8px;
  }
}
</style>