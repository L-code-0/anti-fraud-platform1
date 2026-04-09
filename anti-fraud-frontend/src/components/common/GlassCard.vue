<template>
  <div 
    class="glass-card" 
    :class="{ 
      'glass-card-hoverable': hoverable,
      'glass-card-glow': glow,
      [`glass-card-${size}`]: true
    }"
    :style="customStyle"
  >
    <!-- 背景装饰 -->
    <div class="glass-bg-decorations">
      <div class="decoration decoration-1"></div>
      <div class="decoration decoration-2"></div>
    </div>
    
    <!-- 发光效果 -->
    <div class="glass-glow-effect" v-if="glow" :style="glowStyle"></div>
    
    <!-- 顶部渐变条 -->
    <div class="glass-top-bar" v-if="accentColor" :style="{ background: accentColor }"></div>
    
    <!-- 主内容 -->
    <div class="glass-card-content">
      <slot />
    </div>
    
    <!-- 光泽动画 -->
    <div class="glass-shine-effect" v-if="shine"></div>
    
    <!-- 边框发光 -->
    <div class="glass-border-glow" v-if="borderGlow" :style="borderGlowStyle"></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  hoverable?: boolean
  glow?: boolean
  shine?: boolean
  borderGlow?: boolean
  size?: 'sm' | 'md' | 'lg'
  accentColor?: string
  glowColor?: string
  borderColor?: string
}

const props = withDefaults(defineProps<Props>(), {
  hoverable: true,
  glow: false,
  shine: true,
  borderGlow: false,
  size: 'md'
})

const customStyle = computed(() => ({
  '--glow-color': props.glowColor || 'rgba(102, 126, 234, 0.3)',
  '--border-glow-color': props.borderColor || 'rgba(102, 126, 234, 0.5)'
}))

const glowStyle = computed(() => ({
  background: `radial-gradient(circle at 50% 0%, ${props.glowColor || 'rgba(102, 126, 234, 0.3)'} 0%, transparent 70%)`
}))

const borderGlowStyle = computed(() => ({
  '--border-color': props.borderColor || 'rgba(102, 126, 234, 0.5)'
}))
</script>

<style scoped>
.glass-card {
  position: relative;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 尺寸 */
.glass-card-sm {
  border-radius: 12px;
}

.glass-card-md {
  border-radius: 20px;
}

.glass-card-lg {
  border-radius: 28px;
}

/* 可悬停效果 */
.glass-card-hoverable:hover {
  transform: translateY(-8px) scale(1.01);
  box-shadow: 
    0 25px 50px -12px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.4);
}

/* 背景装饰 */
.glass-bg-decorations {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.decoration {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  transition: all 0.5s ease;
}

.decoration-1 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  top: -80px;
  right: -80px;
}

.decoration-2 {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  bottom: -60px;
  left: -60px;
}

.glass-card:hover .decoration-1 {
  transform: translate(-20px, 20px) scale(1.2);
}

.glass-card:hover .decoration-2 {
  transform: translate(20px, -20px) scale(1.2);
}

/* 发光效果 */
.glass-glow-effect {
  position: absolute;
  inset: 0;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.4s ease;
}

.glass-card:hover .glass-glow-effect {
  opacity: 1;
}

/* 顶部渐变条 */
.glass-top-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.glass-card:hover .glass-top-bar {
  opacity: 1;
}

/* 主内容 */
.glass-card-content {
  position: relative;
  z-index: 1;
  padding: 24px;
}

.glass-card-sm .glass-card-content {
  padding: 16px;
}

.glass-card-lg .glass-card-content {
  padding: 32px;
}

/* 光泽动画 */
.glass-shine-effect {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.4) 50%,
    transparent 100%
  );
  transition: left 0.8s ease;
  pointer-events: none;
}

.glass-card:hover .glass-shine-effect {
  left: 100%;
}

/* 边框发光 */
.glass-border-glow {
  position: absolute;
  inset: -1px;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(135deg, var(--border-color), transparent, var(--border-color));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.4s ease;
  pointer-events: none;
}

.glass-card:hover .glass-border-glow {
  opacity: 1;
  animation: borderRotate 3s linear infinite;
}

@keyframes borderRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .glass-card {
    background: rgba(30, 30, 40, 0.85);
    border-color: rgba(255, 255, 255, 0.1);
  }
  
  .glass-card:hover {
    box-shadow: 
      0 25px 50px -12px rgba(0, 0, 0, 0.5),
      0 0 0 1px rgba(255, 255, 255, 0.2);
  }
}
</style>

