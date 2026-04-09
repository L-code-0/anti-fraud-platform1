<template>
  <section class="cta-section">
    <div class="cta-content">
      <h2>准备好开始你的反诈学习之旅了吗？</h2>
      <p>加入我们，一起守护校园安全</p>
      <el-button type="primary" size="large" class="cta-btn" @click="$router.push('/register')">
        立即注册
        <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>
  </section>
</template>

<script setup lang="ts">
// CTA区域组件
</script>

<style scoped>
/* ===== CTA区域 ===== */
.cta-section {
  background: linear-gradient(135deg, #1a365d 0%, #2b6cb0 50%, #4299e1 100%);
  border-radius: var(--radius-xl);
  padding: 60px 40px;
  text-align: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.cta-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.cta-content {
  position: relative;
  z-index: 1;
}

.cta-content h2 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 12px;
}

.cta-content p {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 24px;
}

.cta-btn {
  padding: 16px 40px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-lg);
  background: white;
  color: var(--color-primary-dark);
  border: none;
  gap: 8px;
}

.cta-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}
</style>