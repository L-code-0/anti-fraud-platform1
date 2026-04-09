<template>
  <section class="banner">
    <div class="banner-bg">
      <div class="bg-gradient"></div>
      <div class="bg-particles">
        <span v-for="i in 20" :key="i" class="particle" :style="getParticleStyle(i)"></span>
      </div>
      <div class="bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
    
    <div class="banner-content">
      <div class="banner-badge">
        <el-icon><Lock /></el-icon>
        <span>安全守护</span>
      </div>
      <h1 class="banner-title">
        <span class="title-line">高校反诈安全知识普及平台</span>
      </h1>
      <p class="banner-subtitle">
        <span class="highlight">学、测、练、报</span> 四位一体，全面提升反诈能力
      </p>
      <div class="banner-actions">
        <el-button type="primary" size="large" class="action-btn primary-btn" @click="$router.push('/knowledge')">
          <el-icon><Reading /></el-icon>
          <span>开始学习</span>
          <div class="btn-ripple"></div>
        </el-button>
        <el-button size="large" class="action-btn secondary-btn" @click="$router.push('/simulation')">
          <el-icon><Monitor /></el-icon>
          <span>体验演练</span>
        </el-button>
      </div>
      
      <!-- 统计数据 - 数字滚动动画 -->
      <div class="banner-stats">
        <div class="stat-item" v-for="(stat, index) in statistics" :key="stat.label">
          <div class="stat-icon">
            <component :is="stat.icon" />
          </div>
          <div class="stat-content">
            <div class="stat-number">
              <span class="number-value" :data-target="stat.value">{{ stat.displayValue }}</span>
              <span class="number-suffix">{{ stat.suffix }}</span>
            </div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { User, Document, EditPen, Bell } from '@element-plus/icons-vue'

const statistics = reactive([
  { label: '注册用户', value: 10000, displayValue: '0', suffix: '+', icon: 'User' },
  { label: '知识文章', value: 500, displayValue: '0', suffix: '+', icon: 'Document' },
  { label: '测试完成', value: 50000, displayValue: '0', suffix: '+', icon: 'EditPen' },
  { label: '诈骗拦截', value: 1000, displayValue: '0', suffix: '+', icon: 'Bell' }
])

const getParticleStyle = (index: number) => {
  const random = (min: number, max: number) => Math.random() * (max - min) + min
  return {
    '--x': `${random(0, 100)}%`,
    '--y': `${random(0, 100)}%`,
    '--duration': `${random(3, 8)}s`,
    '--delay': `${random(0, 5)}s`,
    '--size': `${random(2, 6)}px`
  }
}

// 数字滚动动画
const animateNumbers = () => {
  statistics.forEach((stat, index) => {
    const duration = 2000
    const startTime = Date.now()
    const startValue = 0
    const endValue = stat.value
    
    const animate = () => {
      const elapsed = Date.now() - startTime
      const progress = Math.min(elapsed / duration, 1)
      
      // 使用easeOutExpo缓动函数
      const easeProgress = 1 - Math.pow(2, -10 * progress)
      const currentValue = Math.floor(startValue + (endValue - startValue) * easeProgress)
      
      stat.displayValue = currentValue.toLocaleString()
      
      if (progress < 1) {
        requestAnimationFrame(animate)
      }
    }
    
    setTimeout(() => requestAnimationFrame(animate), index * 200)
  })
}

onMounted(() => {
  // 启动数字动画
  setTimeout(animateNumbers, 500)
})
</script>

<style scoped>
/* ===== Banner升级样式 ===== */
.banner {
  position: relative;
  border-radius: var(--radius-xl);
  padding: 80px 48px;
  text-align: center;
  color: white;
  margin-bottom: var(--spacing-xl);
  overflow: hidden;
  min-height: 420px;
}

.banner-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #0f1f33 0%, #1a365d 30%, #2b6cb0 70%, #4299e1 100%);
}

.bg-gradient {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at 30% 20%, rgba(66, 153, 225, 0.3) 0%, transparent 50%),
              radial-gradient(ellipse at 70% 80%, rgba(43, 108, 176, 0.4) 0%, transparent 50%);
}

.bg-particles {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.particle {
  position: absolute;
  left: var(--x);
  top: var(--y);
  width: var(--size);
  height: var(--size);
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: float var(--duration) ease-in-out var(--delay) infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.6; }
  50% { transform: translateY(-30px) scale(1.2); opacity: 1; }
}

.bg-shapes {
  position: absolute;
  inset: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
}

.shape-1 {
  width: 400px;
  height: 400px;
  background: rgba(66, 153, 225, 0.2);
  top: -100px;
  right: -100px;
  animation: morph 8s ease-in-out infinite;
}

.shape-2 {
  width: 300px;
  height: 300px;
  background: rgba(72, 187, 120, 0.15);
  bottom: -50px;
  left: -50px;
  animation: morph 10s ease-in-out infinite reverse;
}

.shape-3 {
  width: 200px;
  height: 200px;
  background: rgba(237, 137, 54, 0.1);
  top: 50%;
  left: 30%;
  animation: morph 6s ease-in-out infinite;
}

@keyframes morph {
  0%, 100% { border-radius: 60% 40% 30% 70% / 60% 30% 70% 40%; transform: rotate(0deg); }
  50% { border-radius: 30% 60% 70% 40% / 50% 60% 30% 60%; transform: rotate(180deg); }
}

.banner-content {
  position: relative;
  z-index: 1;
}

.banner-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  padding: 8px 20px;
  border-radius: var(--radius-full);
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 24px;
  animation: fadeInDown 0.8s ease-out;
}

@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

.banner-title {
  font-size: 42px;
  font-weight: 800;
  margin-bottom: 16px;
  animation: fadeInUp 0.8s ease-out 0.2s both;
}

.title-line {
  background: linear-gradient(90deg, #fff 0%, rgba(255, 255, 255, 0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.banner-subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 32px;
  animation: fadeInUp 0.8s ease-out 0.4s both;
}

.banner-subtitle .highlight {
  display: inline-block;
  background: linear-gradient(90deg, #48bb78, #38a169);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 700;
}

.banner-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 48px;
  animation: fadeInUp 0.8s ease-out 0.6s both;
}

.action-btn {
  padding: 14px 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-lg);
  transition: all 0.3s ease;
}

.primary-btn {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
  border: none;
  box-shadow: 0 4px 20px rgba(72, 187, 120, 0.4);
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(72, 187, 120, 0.5);
}

.secondary-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  color: white;
}

.secondary-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
}

/* 统计数据 */
.banner-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  padding-top: 32px;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
  animation: fadeInUp 0.8s ease-out 0.8s both;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-content {
  text-align: left;
}

.stat-number {
  font-size: 28px;
  font-weight: 800;
  line-height: 1;
  margin-bottom: 4px;
}

.number-suffix {
  font-size: 18px;
  font-weight: 600;
}

.stat-label {
  font-size: 13px;
  opacity: 0.8;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .banner {
    padding: 48px 24px;
    border-radius: var(--radius-lg);
  }
  
  .banner-title {
    font-size: 28px;
  }
  
  .banner-subtitle {
    font-size: 15px;
  }
  
  .banner-actions {
    flex-direction: column;
    gap: 12px;
  }
  
  .action-btn {
    width: 100%;
  }
  
  .banner-stats {
    flex-wrap: wrap;
    gap: 24px;
  }
  
  .stat-item {
    min-width: 45%;
    justify-content: center;
  }
}
</style>