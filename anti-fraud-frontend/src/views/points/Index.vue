<template>
  <div class="points-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg">
        <div class="bg-gradient"></div>
      </div>
      <div class="header-content">
        <h1>积分中心</h1>
        <p>积分当钱花，福利享不停</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 积分概览 -->
      <div class="points-overview">
        <div class="points-card main">
          <div class="points-icon">
            <el-icon><Coin /></el-icon>
          </div>
          <div class="points-info">
            <span class="points-value">{{ userPoints }}</span>
            <span class="points-label">我的积分</span>
          </div>
          <el-button type="primary" @click="showEarnDialog = true">
            <el-icon><Plus /></el-icon>
            赚积分
          </el-button>
        </div>

        <div class="stats-row">
          <div class="stat-item">
            <span class="stat-value">{{ todayPoints }}</span>
            <span class="stat-label">今日获取</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ monthPoints }}</span>
            <span class="stat-label">本月获取</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ totalEarned }}</span>
            <span class="stat-label">累计获取</span>
          </div>
        </div>
      </div>

      <!-- 获取积分方式 -->
      <div class="earn-section">
        <div class="section-header">
          <h2>赚积分</h2>
          <span class="tip">更多方式持续更新中...</span>
        </div>

        <div class="earn-grid">
          <div class="earn-card" v-for="item in earnMethods" :key="item.id">
            <div class="earn-icon" :style="{ background: item.gradient }">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <div class="earn-info">
              <h4>{{ item.title }}</h4>
              <p>{{ item.desc }}</p>
            </div>
            <div class="earn-points">
              <span class="points-plus">+{{ item.points }}</span>
              <span class="points-unit">积分</span>
            </div>
            <el-button
              :type="item.done ? 'info' : 'primary'"
              size="small"
              :disabled="item.done"
              @click="handleEarn(item)"
            >
              {{ item.done ? '已完成' : item.frequency }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 积分商城 -->
      <div class="mall-section">
        <div class="section-header">
          <h2>积分商城</h2>
          <el-button text>
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div class="mall-tabs">
          <button
            v-for="tab in mallTabs"
            :key="tab.id"
            class="tab-btn"
            :class="{ active: activeMallTab === tab.id }"
            @click="activeMallTab = tab.id"
          >
            {{ tab.name }}
          </button>
        </div>

        <div class="mall-grid">
          <div class="mall-item" v-for="item in mallItems" :key="item.id">
            <div class="item-image">
              <img :src="item.cover" :alt="item.name" />
              <div class="item-tag" :class="'tag-' + item.tag">
                {{ item.tagName }}
              </div>
            </div>
            <div class="item-content">
              <h4>{{ item.name }}</h4>
              <p>{{ item.desc }}</p>
              <div class="item-footer">
                <div class="item-price">
                  <el-icon><Coin /></el-icon>
                  <span>{{ item.price }}</span>
                </div>
                <el-button type="primary" size="small" @click="exchangeItem(item)">
                  立即兑换
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 积分记录 -->
      <div class="history-section">
        <div class="section-header">
          <h2>积分记录</h2>
          <el-button text @click="showAllHistory = !showAllHistory">
            {{ showAllHistory ? '收起' : '查看全部' }}
            <el-icon><component :is="showAllHistory ? 'Top' : 'Bottom'" /></el-icon>
          </el-button>
        </div>

        <div class="history-list">
          <div class="history-item" v-for="item in displayedHistory" :key="item.id">
            <div class="history-icon" :class="item.type">
              <el-icon><component :is="item.type === 'add' ? 'Plus' : 'Minus'" /></el-icon>
            </div>
            <div class="history-content">
              <h4>{{ item.title }}</h4>
              <p>{{ item.time }}</p>
            </div>
            <div class="history-change" :class="item.type">
              {{ item.type === 'add' ? '+' : '-' }}{{ item.points }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 赚积分对话框 -->
    <el-dialog v-model="showEarnDialog" title="赚积分" width="600px">
      <div class="earn-list">
        <div class="earn-item" v-for="item in earnMethods" :key="item.id">
          <div class="earn-item-icon" :style="{ background: item.gradient }">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <div class="earn-item-info">
            <h4>{{ item.title }}</h4>
            <p>{{ item.desc }}</p>
          </div>
          <div class="earn-item-action">
            <span class="earn-points">+{{ item.points }}</span>
            <el-button
              :type="item.done ? 'info' : 'primary'"
              size="small"
              :disabled="item.done"
            >
              {{ item.frequency }}
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 兑换对话框 -->
    <el-dialog v-model="showExchangeDialog" title="确认兑换" width="400px">
      <div class="exchange-preview" v-if="selectedItem">
        <img :src="selectedItem.cover" :alt="selectedItem.name" />
        <div class="exchange-info">
          <h4>{{ selectedItem.name }}</h4>
          <p>{{ selectedItem.desc }}</p>
          <div class="exchange-price">
            <el-icon><Coin /></el-icon>
            <span>{{ selectedItem.price }}积分</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showExchangeDialog = false">取消</el-button>
        <el-button
          type="primary"
          :disabled="userPoints < (selectedItem?.price || 0)"
          @click="confirmExchange"
        >
          确认兑换
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Coin, Plus, ArrowRight, Top, Bottom, Reading, EditPen,
  Calendar, Clock, Star, Gift, ShoppingCart, Trophy, Medal
} from '@element-plus/icons-vue'

const userPoints = ref(2350)
const todayPoints = ref(120)
const monthPoints = ref(560)
const totalEarned = ref(3850)

const showEarnDialog = ref(false)
const showExchangeDialog = ref(false)
const showAllHistory = ref(false)
const activeMallTab = ref('all')
const selectedItem = ref<any>(null)

const mallTabs = [
  { id: 'all', name: '全部' },
  { id: 'physical', name: '实物' },
  { id: 'virtual', name: '虚拟' },
  { id: 'privilege', name: '特权' }
]

const earnMethods = ref([
  { id: 1, icon: 'Reading', title: '每日学习', desc: '每日完成一篇知识学习', points: 10, frequency: '每日', done: false, gradient: 'var(--gradient-primary)' },
  { id: 2, icon: 'EditPen', title: '每日测试', desc: '每日完成一套测试题', points: 20, frequency: '每日', done: true, gradient: 'var(--gradient-success)' },
  { id: 3, icon: 'Calendar', title: '每日签到', desc: '连续签到可获得额外积分', points: 5, frequency: '每日', done: true, gradient: 'var(--gradient-warning)' },
  { id: 4, icon: 'Star', title: '收藏文章', desc: '收藏一篇知识文章', points: 2, frequency: '每次', done: false, gradient: 'var(--gradient-info)' },
  { id: 5, icon: 'Clock', title: '学习时长', desc: '累计学习满1小时', points: 30, frequency: '每日', done: false, gradient: 'var(--gradient-purple)' },
  { id: 6, icon: 'Trophy', title: '通过考试', desc: '通过任意考试', points: 50, frequency: '每次', done: false, gradient: 'var(--gradient-gold)' }
])

const mallItems = ref([
  { id: 1, name: '精美笔记本', desc: '平台定制款，限量发行', cover: 'https://picsum.photos/seed/item1/200/200', price: 500, tag: 'hot', tagName: '热门' },
  { id: 2, name: '积分加倍卡', desc: '1小时内获取积分翻倍', cover: 'https://picsum.photos/seed/item2/200/200', price: 100, tag: 'new', tagName: '新品' },
  { id: 3, name: 'VIP会员(1月)', desc: '解锁全部学习内容', cover: 'https://picsum.photos/seed/item3/200/200', price: 2000, tag: '', tagName: '' },
  { id: 4, name: '反诈徽章', desc: '荣誉象征，展示防骗成就', cover: 'https://picsum.photos/seed/item4/200/200', price: 300, tag: 'limit', tagName: '限量' },
  { id: 5, name: '定制马克杯', desc: '刻有个人昵称', cover: 'https://picsum.photos/seed/item5/200/200', price: 800, tag: 'hot', tagName: '热门' },
  { id: 6, name: '错题解析特权', desc: '解锁全部错题详细解析', cover: 'https://picsum.photos/seed/item6/200/200', price: 150, tag: '', tagName: '' }
])

const historyRecords = ref([
  { id: 1, title: '每日签到', time: '2024-01-15 08:00', points: 5, type: 'add' },
  { id: 2, title: '完成测试', time: '2024-01-15 10:30', points: 20, type: 'add' },
  { id: 3, title: '学习文章', time: '2024-01-15 14:00', points: 10, type: 'add' },
  { id: 4, title: '兑换商品', time: '2024-01-14 16:00', points: 100, type: 'sub' },
  { id: 5, title: '通过考试', time: '2024-01-14 11:00', points: 50, type: 'add' },
  { id: 6, title: '兑换商品', time: '2024-01-13 15:00', points: 500, type: 'sub' }
])

const displayedHistory = computed(() => {
  return showAllHistory.value ? historyRecords.value : historyRecords.value.slice(0, 3)
})

const handleEarn = (item: any) => {
  if (item.id === 1) {
    ElMessage.success('学习已完成，获得10积分！')
    userPoints.value += item.points
    todayPoints.value += item.points
    item.done = true
  }
}

const exchangeItem = (item: any) => {
  selectedItem.value = item
  showExchangeDialog.value = true
}

const confirmExchange = () => {
  if (!selectedItem.value) return

  if (userPoints.value < selectedItem.value.price) {
    ElMessage.warning('积分不足')
    return
  }

  userPoints.value -= selectedItem.value.price
  ElMessage.success(`恭喜！成功兑换${selectedItem.value.name}`)
  showExchangeDialog.value = false
}
</script>

<style scoped>
.points-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

.page-header {
  position: relative;
  padding: var(--spacing-16) var(--spacing-6);
  overflow: hidden;
}

.header-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.header-bg .bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #d97706 0%, #f59e0b 50%, #fbbf24 100%);
}

.header-content {
  position: relative;
  z-index: 1;
  max-width: 1280px;
  margin: 0 auto;
  text-align: center;
  color: white;
}

.header-content h1 {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-4);
}

.header-content p {
  font-size: var(--font-size-lg);
  opacity: 0.9;
}

.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--spacing-6) var(--spacing-12);
}

/* 积分概览 */
.points-overview {
  display: flex;
  gap: var(--spacing-6);
  margin-top: calc(-1 * var(--spacing-10));
  margin-bottom: var(--spacing-8);
}

.points-card.main {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-5);
  padding: var(--spacing-6);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
}

.points-icon {
  width: 72px;
  height: 72px;
  background: var(--gradient-warning);
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  box-shadow: var(--shadow-warning);
}

.points-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.points-value {
  font-size: var(--font-size-5xl);
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, var(--warning-color) 0%, var(--warning-light) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.points-label {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
}

.stats-row {
  display: flex;
  align-items: center;
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-6);
  box-shadow: var(--shadow-lg);
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  display: block;
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: var(--border-primary);
}

/* 赚积分 */
.earn-section {
  margin-bottom: var(--spacing-10);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
}

.section-header h2 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.tip {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.earn-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-4);
}

.earn-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  padding: var(--spacing-4);
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

.earn-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.earn-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: white;
  flex-shrink: 0;
}

.earn-info {
  flex: 1;
}

.earn-info h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.earn-info p {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.earn-points {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: var(--spacing-3);
}

.points-plus {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--success-color);
}

.points-unit {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

/* 积分商城 */
.mall-section {
  margin-bottom: var(--spacing-10);
}

.mall-tabs {
  display: flex;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-6);
}

.tab-btn {
  padding: var(--spacing-2) var(--spacing-4);
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.tab-btn:hover {
  background: var(--bg-hover);
}

.tab-btn.active {
  background: var(--gradient-primary);
  border-color: var(--primary-color);
  color: white;
}

.mall-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-6);
}

.mall-item {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

.mall-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

.item-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.mall-item:hover .item-image img {
  transform: scale(1.1);
}

.item-tag {
  position: absolute;
  top: var(--spacing-3);
  right: var(--spacing-3);
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: white;
}

.item-tag.tag-hot { background: var(--danger-color); }
.item-tag.tag-new { background: var(--success-color); }
.item-tag.tag-limit { background: var(--warning-color); }

.item-content {
  padding: var(--spacing-4);
}

.item-content h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.item-content p {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
  margin-bottom: var(--spacing-3);
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--warning-color);
}

/* 积分记录 */
.history-section {
  margin-bottom: var(--spacing-8);
}

.history-list {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.history-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-4) var(--spacing-5);
  border-bottom: 1px solid var(--border-secondary);
}

.history-item:last-child {
  border-bottom: none;
}

.history-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
}

.history-icon.add {
  background: var(--success-color);
}

.history-icon.sub {
  background: var(--danger-color);
}

.history-content {
  flex: 1;
}

.history-content h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.history-content p {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.history-change {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
}

.history-change.add {
  color: var(--success-color);
}

.history-change.sub {
  color: var(--danger-color);
}

/* 赚积分对话框 */
.earn-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
}

.earn-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  padding: var(--spacing-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
}

.earn-item-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: white;
}

.earn-item-info {
  flex: 1;
}

.earn-item-info h4 {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-1);
}

.earn-item-info p {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.earn-item-action {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
}

.earn-item-action .earn-points {
  flex-direction: row;
  gap: var(--spacing-1);
}

/* 兑换对话框 */
.exchange-preview {
  display: flex;
  gap: var(--spacing-4);
  padding: var(--spacing-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
}

.exchange-preview img {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-lg);
  object-fit: cover;
}

.exchange-info {
  flex: 1;
}

.exchange-info h4 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-2);
}

.exchange-info p {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
  margin-bottom: var(--spacing-3);
}

.exchange-price {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--warning-color);
}

/* 响应式 */
@media (max-width: 1024px) {
  .points-overview {
    flex-direction: column;
  }

  .earn-grid,
  .mall-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: var(--spacing-10) var(--spacing-4);
  }

  .header-content h1 {
    font-size: var(--font-size-2xl);
  }

  .page-container {
    padding: 0 var(--spacing-4) var(--spacing-8);
  }

  .earn-grid,
  .mall-grid {
    grid-template-columns: 1fr;
  }

  .stats-row {
    flex-direction: column;
    gap: var(--spacing-4);
  }

  .stat-divider {
    width: 100%;
    height: 1px;
  }
}
</style>
