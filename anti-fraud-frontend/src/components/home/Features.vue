<template>
  <section class="features">
    <div class="section-header">
      <div class="header-content">
        <h2>
          <span class="title-icon">
            <el-icon><Grid /></el-icon>
          </span>
          核心功能
        </h2>
        <p class="section-desc">多维度学习反诈知识，全面提升防范意识</p>
      </div>
    </div>
    <div class="features-grid">
      <div 
        class="feature-card" 
        v-for="(item, index) in features" 
        :key="item.title"
        :style="{ animationDelay: `${index * 0.1}s` }"
        @click="$router.push(item.path)"
      >
        <div class="card-glow" :style="{ background: item.gradient }"></div>
        <div class="card-content">
          <div class="feature-icon-wrapper">
            <div class="feature-icon" :style="{ background: item.gradient }">
              <el-icon :size="32" color="white">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="icon-ring"></div>
          </div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.desc }}</p>
          <div class="feature-footer">
            <span class="feature-action">
              立即进入
              <el-icon><ArrowRight /></el-icon>
            </span>
          </div>
        </div>
        <div class="card-shine"></div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Reading, EditPen, Monitor, Bell } from '@element-plus/icons-vue'

const features = ref([
  { 
    title: '知识学习', 
    desc: '丰富的反诈知识库，系统学习反诈知识', 
    icon: Reading, 
    gradient: 'linear-gradient(135deg, #4299e1 0%, #3182ce 100%)',
    path: '/knowledge' 
  },
  { 
    title: '测试考试', 
    desc: '检验学习成果，查漏补缺', 
    icon: EditPen, 
    gradient: 'linear-gradient(135deg, #48bb78 0%, #38a169 100%)',
    path: '/test' 
  },
  { 
    title: '演练模拟', 
    desc: '实战模拟训练，提升应对能力', 
    icon: Monitor, 
    gradient: 'linear-gradient(135deg, #ed8936 0%, #dd6b20 100%)',
    path: '/simulation' 
  },
  { 
    title: '举报预警', 
    desc: '及时预警提醒，共同防范诈骗', 
    icon: Bell, 
    gradient: 'linear-gradient(135deg, #f56565 0%, #e53e3e 100%)',
    path: '/report' 
  }
])
</script>

<style scoped>
/* ===== 区块头部 ===== */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header.center {
  flex-direction: column;
  text-align: center;
  gap: 8px;
}

.section-header h2 {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  color: var(--color-text-primary);
  font-weight: 700;
}

.title-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  color: white;
  font-size: 20px;
}

.section-desc {
  color: var(--color-text-muted);
  font-size: 15px;
}

/* ===== 功能模块 ===== */
.features {
  margin-bottom: var(--spacing-2xl);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.feature-card {
  position: relative;
  background: white;
  border-radius: var(--radius-lg);
  padding: 32px 24px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.4s ease;
  animation: fadeInUp 0.6s ease-out both;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-card:hover .card-glow {
  opacity: 1;
}

.card-content {
  position: relative;
  z-index: 1;
}

.feature-icon-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.feature-icon {
  width: 72px;
  height: 72px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  transition: transform 0.3s ease;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1);
}

.icon-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 88px;
  height: 88px;
  border: 2px solid currentColor;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: all 0.3s ease;
}

.feature-card:hover .icon-ring {
  opacity: 0.3;
  width: 96px;
  height: 96px;
}

.feature-card h3 {
  text-align: center;
  margin-bottom: 8px;
  color: var(--color-text-primary);
  font-size: 18px;
  font-weight: 600;
}

.feature-card p {
  text-align: center;
  color: var(--color-text-muted);
  font-size: 14px;
  margin-bottom: 16px;
}

.feature-footer {
  text-align: center;
}

.feature-action {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--color-primary);
  font-size: 14px;
  font-weight: 500;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.3s ease;
}

.feature-card:hover .feature-action {
  opacity: 1;
  transform: translateY(0);
}

.card-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s ease;
}

.feature-card:hover .card-shine {
  left: 100%;
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .features-grid {
    grid-template-columns: 1fr;
  }
}
</style>