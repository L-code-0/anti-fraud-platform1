<template>
  <section class="hot-knowledge">
    <div class="section-header">
      <div class="header-content">
        <h2>
          <span class="title-icon hot-icon">
            <el-icon><TrendCharts /></el-icon>
          </span>
          热门知识
        </h2>
      </div>
      <el-button text type="primary" class="more-btn" @click="$router.push('/knowledge')">
        查看更多
        <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>
    <div class="knowledge-grid">
      <div 
        class="knowledge-card" 
        v-for="(item, index) in hotKnowledge" 
        :key="item.id"
        :style="{ animationDelay: `${index * 0.1}s` }"
        @click="$router.push(`/knowledge/${item.id}`)"
      >
        <div class="knowledge-cover-wrapper">
          <img :src="item.coverImage || defaultCover" class="knowledge-cover" />
          <div class="cover-overlay">
            <el-icon :size="24"><View /></el-icon>
          </div>
          <div class="knowledge-rank" v-if="index < 3">
            <span class="rank-badge" :class="`rank-${index + 1}`">TOP {{ index + 1 }}</span>
          </div>
        </div>
        <div class="knowledge-info">
          <h4>{{ item.title }}</h4>
          <div class="knowledge-stats">
            <span class="stat">
              <el-icon><View /></el-icon> 
              <span class="stat-num">{{ formatNumber(item.viewCount) }}</span>
            </span>
            <span class="stat">
              <el-icon><Star /></el-icon> 
              <span class="stat-num">{{ item.collectCount }}</span>
            </span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'
import { View, Star } from '@element-plus/icons-vue'

const defaultCover = 'https://images.unsplash.com/photo-1557804506-669a67965ba0?w=300&h=200&fit=crop'
const hotKnowledge = ref<any[]>([])

const formatNumber = (num: number) => {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num
}

onMounted(async () => {
  try {
    const knowledgeRes = await get('/knowledge/hot')
    hotKnowledge.value = knowledgeRes.data || []
  } catch (e) {
    // 使用模拟数据
    hotKnowledge.value = [
      { id: 1, title: '冒充公检法诈骗案例分析', viewCount: 2356, collectCount: 56, coverImage: '' },
      { id: 2, title: '网络兼职刷单诈骗防范指南', viewCount: 1892, collectCount: 45, coverImage: '' },
      { id: 3, title: '校园贷陷阱识别指南', viewCount: 3210, collectCount: 78, coverImage: '' },
      { id: 4, title: '网络购物诈骗案例分析', viewCount: 1567, collectCount: 34, coverImage: '' }
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

.hot-icon {
  background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
}

.more-btn {
  font-weight: 500;
  gap: 4px;
}

/* ===== 热门知识 ===== */
.hot-knowledge {
  margin-bottom: var(--spacing-2xl);
}

.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.knowledge-card {
  background: white;
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease-out both;
}

.knowledge-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-lg);
}

.knowledge-cover-wrapper {
  position: relative;
  overflow: hidden;
}

.knowledge-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.knowledge-card:hover .knowledge-cover {
  transform: scale(1.1);
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: white;
}

.knowledge-card:hover .cover-overlay {
  opacity: 1;
}

.knowledge-rank {
  position: absolute;
  top: 12px;
  left: 12px;
}

.rank-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: var(--radius-full);
  font-size: 11px;
  font-weight: 700;
  color: white;
}

.rank-1 { background: linear-gradient(135deg, #f6ad55 0%, #ed8936 100%); }
.rank-2 { background: linear-gradient(135deg, #a0aec0 0%, #718096 100%); }
.rank-3 { background: linear-gradient(135deg, #d69e2e 0%, #b7791f 100%); }

.knowledge-info {
  padding: 16px;
}

.knowledge-info h4 {
  margin-bottom: 12px;
  color: var(--color-text-primary);
  font-size: 15px;
  font-weight: 600;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
  min-height: 42px;
}

.knowledge-stats {
  display: flex;
  gap: 16px;
}

.knowledge-stats .stat {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--color-text-muted);
  font-size: 13px;
}

.stat-num {
  font-weight: 500;
  color: var(--color-text-secondary);
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .knowledge-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .knowledge-grid {
    grid-template-columns: 1fr;
  }
}
</style>