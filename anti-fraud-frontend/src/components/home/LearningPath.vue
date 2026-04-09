<template>
  <section class="learning-path">
    <div class="section-header center">
      <h2>
        <span class="title-icon path-icon">
          <el-icon><Compass /></el-icon>
        </span>
        学习路径
      </h2>
      <p class="section-desc">循序渐进，系统掌握反诈知识</p>
    </div>
    <div class="path-container">
      <div class="path-line"></div>
      <div class="path-steps">
        <div 
          class="path-step" 
          v-for="(step, index) in learningSteps" 
          :key="index"
          :style="{ animationDelay: `${index * 0.2}s` }"
        >
          <div class="step-dot">
            <div class="dot-inner"></div>
            <div class="dot-pulse"></div>
          </div>
          <div class="step-content">
            <div class="step-number">Step {{ index + 1 }}</div>
            <h4>{{ step.title }}</h4>
            <p>{{ step.desc }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const learningSteps = ref([
  { title: '学习知识', desc: '阅读反诈知识文章，了解常见诈骗手法' },
  { title: '参与测试', desc: '完成知识测试，检验学习效果' },
  { title: '模拟演练', desc: '体验真实诈骗场景，提升防范能力' },
  { title: '分享传播', desc: '向同学分享反诈知识，共建安全校园' }
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
  color: white;
  font-size: 20px;
}

.path-icon {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
}

.section-desc {
  color: var(--color-text-muted);
  font-size: 15px;
}

/* ===== 学习路径 ===== */
.learning-path {
  background: white;
  border-radius: var(--radius-xl);
  padding: 48px;
  margin-bottom: var(--spacing-2xl);
}

.path-container {
  position: relative;
  padding-top: 20px;
}

.path-line {
  position: absolute;
  top: 44px;
  left: 50px;
  right: 50px;
  height: 2px;
  background: linear-gradient(90deg, var(--color-primary-light), var(--color-primary), var(--color-primary-light));
  opacity: 0.3;
}

.path-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
}

.path-step {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  animation: fadeInUp 0.6s ease-out both;
}

.step-dot {
  position: relative;
  width: 24px;
  height: 24px;
  margin-bottom: 20px;
}

.dot-inner {
  position: absolute;
  inset: 4px;
  background: var(--color-primary);
  border-radius: 50%;
}

.dot-pulse {
  position: absolute;
  inset: 0;
  border: 2px solid var(--color-primary);
  border-radius: 50%;
  animation: dotPulse 2s ease-in-out infinite;
}

@keyframes dotPulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.5); opacity: 0; }
}

.step-content {
  max-width: 180px;
}

.step-number {
  display: inline-block;
  background: var(--color-primary);
  color: white;
  padding: 2px 12px;
  border-radius: var(--radius-full);
  font-size: 11px;
  font-weight: 600;
  margin-bottom: 8px;
}

.step-content h4 {
  color: var(--color-text-primary);
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 6px;
}

.step-content p {
  color: var(--color-text-muted);
  font-size: 13px;
  line-height: 1.5;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .path-line {
    display: none;
  }
  
  .path-steps {
    flex-direction: column;
    gap: 24px;
  }
  
  .path-step {
    flex-direction: row;
    text-align: left;
    gap: 16px;
  }
  
  .step-content {
    max-width: none;
  }
  
  .learning-path {
    padding: 32px 24px;
  }
}
</style>