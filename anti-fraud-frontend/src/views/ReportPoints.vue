<template>
  <div class="report-points-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>举报积分奖励</h1>
          <el-button type="primary" @click="loadUserPoints">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </el-header>
      
      <el-main>
        <!-- 积分概览 -->
        <el-card class="points-overview-card">
          <template #header>
            <div class="card-header">
              <span>积分概览</span>
            </div>
          </template>
          
          <div v-if="pointsLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <div v-else class="points-overview">
            <div class="points-card">
              <div class="points-value">{{ userPoints.totalPoints || 0 }}</div>
              <div class="points-label">总积分</div>
            </div>
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-value">{{ userPoints.reportCount || 0 }}</div>
                <div class="stat-label">举报次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userPoints.successCount || 0 }}</div>
                <div class="stat-label">成功处理</div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 积分历史 -->
        <el-card class="points-history-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>积分历史</span>
            </div>
          </template>
          
          <div v-if="pointsLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="userPoints.pointHistory && userPoints.pointHistory.length === 0" description="暂无积分历史" />
          
          <div v-else class="points-history">
            <el-table :data="userPoints.pointHistory || []" style="width: 100%">
              <el-table-column prop="date" label="日期" width="150" />
              <el-table-column prop="points" label="积分" width="100">
                <template #default="scope">
                  <span class="points-value">{{ scope.row.points > 0 ? '+' : '' }}{{ scope.row.points }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
        
        <!-- 积分规则 -->
        <el-card class="points-rules-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>积分规则</span>
            </div>
          </template>
          
          <div class="rules-content">
            <el-list>
              <el-list-item>
                <div class="rule-item">
                  <div class="rule-icon">
                    <el-icon><Star /></el-icon>
                  </div>
                  <div class="rule-content">
                    <h4>基础举报积分</h4>
                    <p>提交有效举报，获得10积分</p>
                  </div>
                </div>
              </el-list-item>
              <el-list-item>
                <div class="rule-item">
                  <div class="rule-icon">
                    <el-icon><Star /></el-icon>
                  </div>
                  <div class="rule-content">
                    <h4>成功处理奖励</h4>
                    <p>举报成功处理，额外获得5积分</p>
                  </div>
                </div>
              </el-list-item>
              <el-list-item>
                <div class="rule-item">
                  <div class="rule-icon">
                    <el-icon><Star /></el-icon>
                  </div>
                  <div class="rule-content">
                    <h4>类型奖励</h4>
                    <p>电信诈骗举报，额外获得3积分</p>
                  </div>
                </div>
              </el-list-item>
              <el-list-item>
                <div class="rule-item">
                  <div class="rule-icon">
                    <el-icon><Star /></el-icon>
                  </div>
                  <div class="rule-content">
                    <h4>连续举报奖励</h4>
                    <p>连续7天举报，额外获得20积分</p>
                  </div>
                </div>
              </el-list-item>
            </el-list>
          </div>
        </el-card>
        
        <!-- 积分兑换 -->
        <el-card class="points-exchange-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>积分兑换</span>
            </div>
          </template>
          
          <div class="exchange-content">
            <el-grid :columns="3" :gutter="20">
              <el-grid-item>
                <div class="exchange-item">
                  <div class="exchange-icon">
                    <el-icon><Gift /></el-icon>
                  </div>
                  <h4>精美礼品</h4>
                  <p>100积分</p>
                  <el-button type="primary" size="small">立即兑换</el-button>
                </div>
              </el-grid-item>
              <el-grid-item>
                <div class="exchange-item">
                  <div class="exchange-icon">
                    <el-icon><Ticket /></el-icon>
                  </div>
                  <h4>优惠券</h4>
                  <p>50积分</p>
                  <el-button type="primary" size="small">立即兑换</el-button>
                </div>
              </el-grid-item>
              <el-grid-item>
                <div class="exchange-item">
                  <div class="exchange-icon">
                    <el-icon><Medal /></el-icon>
                  </div>
                  <h4>荣誉称号</h4>
                  <p>200积分</p>
                  <el-button type="primary" size="small">立即兑换</el-button>
                </div>
              </el-grid-item>
            </el-grid>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Refresh, Loading, Star, Gift, Ticket, Medal } from '@element-plus/icons-vue'

// 状态
const pointsLoading = ref(false)
const userPoints = ref({
  totalPoints: 0,
  reportCount: 0,
  successCount: 0,
  pointHistory: []
})

// 加载用户举报积分
const loadUserPoints = async () => {
  pointsLoading.value = true
  try {
    const res = await get('/report/points')
    if (res.code === 200 && res.data) {
      userPoints.value = res.data
    }
  } catch (error) {
    console.error('加载用户举报积分失败:', error)
    // 模拟数据
    userPoints.value = {
      totalPoints: 150,
      reportCount: 10,
      successCount: 8,
      pointHistory: [
        { date: '2026-04-10', points: 15 },
        { date: '2026-04-05', points: 12 },
        { date: '2026-04-01', points: 10 }
      ]
    }
  } finally {
    pointsLoading.value = false
  }
}

onMounted(() => {
  loadUserPoints()
})
</script>

<style scoped>
.report-points-page {
  min-height: 100vh;
  background: var(--color-bg-page);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-content h1 {
  margin: 0;
  font-size: var(--font-size-2xl);
  color: var(--color-text-primary);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: var(--color-text-secondary);
}

.points-overview {
  display: flex;
  align-items: center;
  gap: 40px;
  flex-wrap: wrap;
  padding: 20px;
}

.points-card {
  background: linear-gradient(135deg, #409EFF, #69c0ff);
  padding: 40px;
  border-radius: var(--radius-lg);
  color: white;
  text-align: center;
  min-width: 200px;
}

.points-value {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: 8px;
}

.points-label {
  font-size: var(--font-size-base);
  opacity: 0.9;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  flex: 1;
  min-width: 300px;
}

.stat-item {
  background: var(--color-bg-container);
  padding: 20px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  text-align: center;
}

.stat-value {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  margin-bottom: 8px;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.points-history {
  padding: 20px;
}

.points-history .points-value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-success);
}

.rules-content {
  padding: 20px;
}

.rule-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--color-border);
}

.rule-item:last-child {
  border-bottom: none;
}

.rule-icon {
  font-size: 24px;
  color: var(--color-warning);
  margin-top: 4px;
}

.rule-content h4 {
  margin: 0 0 8px 0;
  color: var(--color-text-primary);
}

.rule-content p {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.exchange-content {
  padding: 20px;
}

.exchange-item {
  background: var(--color-bg-container);
  padding: 24px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.exchange-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.exchange-icon {
  font-size: 48px;
  color: var(--color-primary);
  margin-bottom: 16px;
}

.exchange-item h4 {
  margin: 0 0 8px 0;
  color: var(--color-text-primary);
}

.exchange-item p {
  margin: 0 0 16px 0;
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .points-overview {
    flex-direction: column;
    align-items: stretch;
    gap: 20px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>