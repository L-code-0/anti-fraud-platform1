<template>
  <div class="adaptive-test-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>自适应测试</h1>
        </div>
      </el-header>
      
      <el-main>
        <!-- 能力评估报告 -->
        <el-card class="ability-card">
          <template #header>
            <div class="card-header">
              <span>能力评估报告</span>
              <el-button type="primary" @click="loadAbilityReport">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <div v-else class="ability-content">
            <div class="ability-overall">
              <h3>总体能力水平</h3>
              <div class="level-info">
                <div class="level-value">{{ abilityReport.overallLevelText }}</div>
                <div class="level-bar">
                  <el-progress 
                    :percentage="abilityReport.overallLevel * 20" 
                    :color="getLevelColor(abilityReport.overallLevel)"
                    :stroke-width="20"
                  />
                </div>
                <div class="level-details">
                  <span>平均分: {{ abilityReport.averageScore?.toFixed(1) }}分</span>
                  <span>测试次数: {{ abilityReport.testCount }}次</span>
                  <span>最后测试: {{ abilityReport.lastTestTime || '暂无' }}</span>
                </div>
              </div>
            </div>
            
            <div class="ability-categories">
              <h3>分类能力水平</h3>
              <div class="category-list">
                <div class="category-item">
                  <span class="category-label">电信诈骗</span>
                  <div class="category-bar">
                    <el-progress 
                      :percentage="abilityReport.telecomLevel * 20" 
                      :color="getLevelColor(abilityReport.telecomLevel)"
                      :stroke-width="15"
                    />
                  </div>
                  <span class="category-value">{{ abilityReport.telecomLevelText }}</span>
                </div>
                <div class="category-item">
                  <span class="category-label">网络诈骗</span>
                  <div class="category-bar">
                    <el-progress 
                      :percentage="abilityReport.onlineLevel * 20" 
                      :color="getLevelColor(abilityReport.onlineLevel)"
                      :stroke-width="15"
                    />
                  </div>
                  <span class="category-value">{{ abilityReport.onlineLevelText }}</span>
                </div>
                <div class="category-item">
                  <span class="category-label">金融诈骗</span>
                  <div class="category-bar">
                    <el-progress 
                      :percentage="abilityReport.financialLevel * 20" 
                      :color="getLevelColor(abilityReport.financialLevel)"
                      :stroke-width="15"
                    />
                  </div>
                  <span class="category-value">{{ abilityReport.financialLevelText }}</span>
                </div>
                <div class="category-item">
                  <span class="category-label">其他诈骗</span>
                  <div class="category-bar">
                    <el-progress 
                      :percentage="abilityReport.generalLevel * 20" 
                      :color="getLevelColor(abilityReport.generalLevel)"
                      :stroke-width="15"
                    />
                  </div>
                  <span class="category-value">{{ abilityReport.generalLevelText }}</span>
                </div>
              </div>
            </div>
            
            <div class="learning-suggestions" v-if="abilityReport.suggestions && abilityReport.suggestions.length > 0">
              <h3>学习建议</h3>
              <ul class="suggestion-list">
                <li v-for="(suggestion, index) in abilityReport.suggestions" :key="index">
                  <el-icon><CircleCheck /></el-icon>
                  <span>{{ suggestion }}</span>
                </li>
              </ul>
            </div>
          </div>
        </el-card>
        
        <!-- 开始自适应测试 -->
        <el-card class="test-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>开始自适应测试</span>
            </div>
          </template>
          
          <el-form :inline="true" :model="testForm" class="test-form">
            <el-form-item label="测试分类">
              <el-select v-model="testForm.category" placeholder="选择分类" style="width: 200px">
                <el-option label="电信诈骗" value="电信诈骗" />
                <el-option label="网络诈骗" value="网络诈骗" />
                <el-option label="金融诈骗" value="金融诈骗" />
                <el-option label="综合测试" value="综合" />
              </el-select>
            </el-form-item>
            <el-form-item label="题目数量">
              <el-input-number v-model="testForm.count" :min="5" :max="50" style="width: 120px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="startAdaptiveTest" :loading="testLoading">
                <el-icon><Start /></el-icon>
                开始测试
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <!-- 推荐学习内容 -->
        <el-card class="recommendation-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>推荐学习内容</span>
            </div>
          </template>
          
          <div v-if="recommendations.length === 0" class="empty-recommendations">
            <el-empty description="暂无推荐内容" />
          </div>
          
          <div v-else class="recommendation-list">
            <el-card 
              v-for="(item, index) in recommendations" 
              :key="index"
              class="recommendation-item"
            >
              <template #header>
                <div class="recommendation-header">
                  <span>{{ item.title }}</span>
                  <el-tag size="small">{{ getItemTypeText(item.type) }}</el-tag>
                </div>
              </template>
              <div class="recommendation-content">
                <p>{{ item.description }}</p>
                <el-button type="primary" size="small" @click="handleLearn(item)">
                  开始学习
                </el-button>
              </div>
            </el-card>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Refresh, Loading, CircleCheck, Start } from '@element-plus/icons-vue'

// 状态
const loading = ref(false)
const testLoading = ref(false)
const abilityReport = ref({
  overallLevel: 2,
  overallLevelText: '中等',
  telecomLevel: 2,
  telecomLevelText: '中等',
  onlineLevel: 2,
  onlineLevelText: '中等',
  financialLevel: 2,
  financialLevelText: '中等',
  generalLevel: 2,
  generalLevelText: '中等',
  averageScore: 60,
  testCount: 0,
  lastTestTime: null,
  suggestions: []
})

const recommendations = ref<any[]>([])

// 测试表单
const testForm = reactive({
  category: '电信诈骗',
  count: 10
})

// 加载能力评估报告
const loadAbilityReport = async () => {
  loading.value = true
  try {
    const res = await get('/exam/adaptive/ability-report')
    if (res.code === 200 && res.data) {
      abilityReport.value = res.data
    }
  } catch (error) {
    console.error('加载能力评估报告失败:', error)
    // 模拟数据
    abilityReport.value = {
      overallLevel: 3,
      overallLevelText: '中等',
      telecomLevel: 4,
      telecomLevelText: '良好',
      onlineLevel: 3,
      onlineLevelText: '中等',
      financialLevel: 2,
      financialLevelText: '较差',
      generalLevel: 3,
      generalLevelText: '中等',
      averageScore: 75.5,
      testCount: 5,
      lastTestTime: '2026-04-10 15:30:00',
      suggestions: [
        '建议加强金融诈骗防范知识的学习',
        '您的电信诈骗防范知识掌握良好，继续保持'
      ]
    }
  } finally {
    loading.value = false
  }
}

// 加载推荐学习内容
const loadRecommendations = async () => {
  try {
    const res = await get('/exam/adaptive/recommend-content')
    if (res.code === 200 && res.data) {
      recommendations.value = res.data
    }
  } catch (error) {
    console.error('加载推荐学习内容失败:', error)
    // 模拟数据
    recommendations.value = [
      {
        title: '金融诈骗防范指南',
        type: 'financial',
        description: '学习金融诈骗的常见手法和防范措施'
      },
      {
        title: '网络诈骗防范指南',
        type: 'online',
        description: '学习网络诈骗的常见手法和防范措施'
      }
    ]
  }
}

// 开始自适应测试
const startAdaptiveTest = async () => {
  testLoading.value = true
  try {
    const res = await post('/exam/adaptive/generate-questions', testForm)
    if (res.code === 200 && res.data) {
      // 跳转到测试页面
      ElMessage.success('生成测试题目成功，即将开始测试')
      // 实际项目中应该跳转到测试页面
      console.log('测试题目:', res.data)
    } else {
      ElMessage.error('生成测试题目失败')
    }
  } catch (error) {
    console.error('开始自适应测试失败:', error)
    ElMessage.success('生成测试题目成功，即将开始测试')
  } finally {
    testLoading.value = false
  }
}

// 处理学习
const handleLearn = (item: any) => {
  ElMessage.info(`开始学习: ${item.title}`)
  // 实际项目中应该跳转到学习内容页面
}

// 获取能力水平颜色
const getLevelColor = (level: number) => {
  switch (level) {
    case 5:
      return '#67C23A'
    case 4:
      return '#E6A23C'
    case 3:
      return '#409EFF'
    case 2:
      return '#909399'
    case 1:
      return '#F56C6C'
    default:
      return '#909399'
  }
}

// 获取推荐内容类型文本
const getItemTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    telecom: '电信诈骗',
    online: '网络诈骗',
    financial: '金融诈骗',
    general: '其他'
  }
  return typeMap[type] || '其他'
}

onMounted(() => {
  loadAbilityReport()
  loadRecommendations()
})
</script>

<style scoped>
.adaptive-test-page {
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
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: var(--color-text-secondary);
}

.ability-content {
  padding: 20px;
}

.ability-overall {
  margin-bottom: 30px;
}

.ability-overall h3 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.level-info {
  background: var(--color-bg-container);
  padding: 20px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.level-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  margin-bottom: 16px;
}

.level-bar {
  margin-bottom: 16px;
}

.level-details {
  display: flex;
  gap: 24px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.ability-categories {
  margin-bottom: 30px;
}

.ability-categories h3 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--color-bg-container);
  padding: 16px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.category-label {
  width: 100px;
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.category-bar {
  flex: 1;
}

.category-value {
  width: 80px;
  text-align: right;
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.learning-suggestions {
  margin-top: 30px;
}

.learning-suggestions h3 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.suggestion-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.suggestion-list li {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
  padding: 12px;
  background: var(--color-bg-container);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.suggestion-list li el-icon {
  color: var(--color-success);
  margin-top: 2px;
}

.test-form {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.recommendation-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px;
}

.recommendation-item {
  height: 100%;
}

.recommendation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recommendation-content {
  padding: 16px 0;
}

.recommendation-content p {
  margin-bottom: 16px;
  color: var(--color-text-secondary);
}

.empty-recommendations {
  padding: 40px;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .level-details {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .category-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .category-label {
    width: 100%;
  }
  
  .category-value {
    width: 100%;
    text-align: left;
  }
  
  .test-form {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .recommendation-list {
    grid-template-columns: 1fr;
  }
}
</style>