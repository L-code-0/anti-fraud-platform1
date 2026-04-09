<template>
  <section class="warnings">
    <div class="section-header">
      <div class="header-content">
        <h2>
          <span class="title-icon warning-icon">
            <el-icon><Bell /></el-icon>
          </span>
          最新预警
        </h2>
      </div>
      <el-button text type="primary" class="more-btn" @click="$router.push('/report')">
        查看更多
        <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>
    <div class="warnings-grid">
      <div 
        class="warning-card" 
        v-for="(warning, index) in warnings" 
        :key="warning.id"
        :style="{ animationDelay: `${index * 0.15}s` }"
      >
        <div class="warning-header">
          <div class="warning-level" :class="`level-${warning.warningLevel}`">
            <span class="level-dot"></span>
            {{ getLevelText(warning.warningLevel) }}
          </div>
          <div class="warning-time">{{ warning.publishTime }}</div>
        </div>
        <h4>{{ warning.title }}</h4>
        <p class="warning-content">{{ warning.content }}</p>
        <div class="warning-footer">
          <el-button text size="small" type="primary" class="detail-btn">
            了解详情
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="warning-glow" :class="`glow-${warning.warningLevel}`"></div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'

const warnings = ref<any[]>([])

const getLevelText = (level: number) => {
  const levels: Record<number, string> = {
    1: '蓝色预警',
    2: '黄色预警',
    3: '橙色预警',
    4: '红色预警'
  }
  return levels[level] || '预警'
}

onMounted(async () => {
  try {
    const warningRes = await get('/report/warnings/latest')
    warnings.value = warningRes.data || []
  } catch (e) {
    // 使用模拟数据
    warnings.value = [
      { id: 1, title: '警惕新型"AI换脸"诈骗', content: '近期出现利用AI技术进行视频通话诈骗的新手法，诈骗分子通过AI换脸技术冒充熟人进行诈骗...', warningLevel: 4, publishTime: '2024-01-15' },
      { id: 2, title: '冒充客服退款诈骗高发', content: '近期冒充电商平台客服退款诈骗案件高发，请广大师生提高警惕...', warningLevel: 3, publishTime: '2024-01-14' },
      { id: 3, title: '寒假兼职诈骗预警', content: '寒假期间是兼职诈骗高发期，请同学们谨慎对待各种兼职信息...', warningLevel: 2, publishTime: '2024-01-13' }
    ]
  }
})
</script>

<style scoped>
/* ===== 区块头部 ===== */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
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

.warning-icon {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
}

.more-btn {
  font-weight: 500;
  gap: 4px;
}

/* ===== 预警模块 ===== */
.warnings {
  margin-bottom: var(--spacing-2xl);
}

.warnings-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.warning-card {
  position: relative;
  background: white;
  border-radius: var(--radius-lg);
  padding: 24px;
  overflow: hidden;
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease-out both;
}

.warning-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.warning-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.warning-level {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: var(--radius-full);
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.level-dot {
  width: 6px;
  height: 6px;
  background: currentColor;
  border-radius: 50%;
}

.warning-level.level-1 { background: #4299e1; }
.warning-level.level-2 { background: #ed8936; }
.warning-level.level-3 { background: #f56565; }
.warning-level.level-4 { 
  background: #e53e3e; 
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(229, 62, 62, 0.4); }
  50% { box-shadow: 0 0 0 8px rgba(229, 62, 62, 0); }
}

.warning-time {
  color: var(--color-text-muted);
  font-size: 12px;
}

.warning-card h4 {
  margin-bottom: 8px;
  color: var(--color-text-primary);
  font-size: 16px;
  font-weight: 600;
}

.warning-content {
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 16px;
}

.warning-footer {
  display: flex;
  justify-content: flex-end;
}

.detail-btn {
  gap: 4px;
}

.warning-glow {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.warning-card:hover .warning-glow {
  opacity: 1;
}

.glow-1 { background: linear-gradient(90deg, #4299e1, #3182ce); }
.glow-2 { background: linear-gradient(90deg, #ed8936, #dd6b20); }
.glow-3 { background: linear-gradient(90deg, #f56565, #e53e3e); }
.glow-4 { background: linear-gradient(90deg, #e53e3e, #c53030); }

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .warnings-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .warnings-grid {
    grid-template-columns: 1fr;
  }
}
</style>