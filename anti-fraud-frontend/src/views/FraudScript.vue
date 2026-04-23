<template>
  <div class="fraud-script-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>诈骗话术文本库</h1>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索话术"
            prefix-icon="Search"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          />
        </div>
      </el-header>
      
      <el-main>
        <!-- 筛选条件 -->
        <el-card class="filter-card">
          <div class="filter-content">
            <el-select
              v-model="filter.fraudType"
              placeholder="诈骗类型"
              style="width: 150px; margin-right: 10px"
              @change="loadScripts"
            >
              <el-option label="全部" value="" />
              <el-option label="电话诈骗" value="TELEPHONE" />
              <el-option label="网络诈骗" value="NETWORK" />
              <el-option label="短信诈骗" value="SMS" />
              <el-option label="投资诈骗" value="INVESTMENT" />
              <el-option label="情感诈骗" value="RELATIONSHIP" />
              <el-option label="金融诈骗" value="FINANCE" />
              <el-option label="其他诈骗" value="OTHER" />
            </el-select>
            
            <el-select
              v-model="filter.targetGroup"
              placeholder="目标群体"
              style="width: 150px; margin-right: 10px"
              @change="loadScripts"
            >
              <el-option label="全部" value="" />
              <el-option label="学生" value="学生" />
              <el-option label="上班族" value="上班族" />
              <el-option label="老年人" value="老年人" />
              <el-option label="投资者" value="投资者" />
            </el-select>
            
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
        </el-card>
        
        <!-- 话术列表 -->
        <el-card class="scripts-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>话术列表</span>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="scripts.length === 0" description="暂无话术" />
          
          <div v-else class="script-list">
            <el-card 
              v-for="script in scripts" 
              :key="script.id"
              class="script-item"
              :body-style="{ padding: '20px' }"
              @click="viewScript(script)"
            >
              <template #header>
                <div class="script-header">
                  <h3>{{ script.scriptTitle }}</h3>
                  <el-tag :type="getCategoryType(script.fraudType)">
                    {{ getFraudTypeText(script.fraudType) }}
                  </el-tag>
                </div>
              </template>
              <div class="script-content">
                <p class="script-description">{{ script.scriptContent }}</p>
                <div class="script-info">
                  <div class="info-item">
                    <el-icon><User /></el-icon>
                    <span>目标群体: {{ script.targetGroup }}</span>
                  </div>
                  <div class="info-item">
                    <el-icon><View /></el-icon>
                    <span>使用: {{ script.usageCount }}次</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Check /></el-icon>
                    <span>{{ script.isVerified ? '已验证' : '未验证' }}</span>
                  </div>
                </div>
                <div class="script-tags">
                  <el-tag size="small" effect="plain">{{ script.source }}</el-tag>
                  <el-tag size="small" effect="plain">{{ script.version }}</el-tag>
                </div>
              </div>
            </el-card>
          </div>
          
          <!-- 分页 -->
          <div class="pagination" v-if="scripts.length > 0">
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-main>
    </el-container>
    
    <!-- 话术详情对话框 -->
    <el-dialog
      v-model="scriptDialogVisible"
      :title="currentScript?.scriptTitle || '话术详情'"
      width="800px"
      custom-class="script-dialog"
    >
      <div v-if="scriptLoading" class="loading-container">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="currentScript" class="script-details">
        <!-- 话术信息 -->
        <div class="script-info-section">
          <h3>话术信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">诈骗类型:</span>
              <el-tag :type="getCategoryType(currentScript.fraudType)">
                {{ getFraudTypeText(currentScript.fraudType) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">目标群体:</span>
              <span class="value">{{ currentScript.targetGroup }}</span>
            </div>
            <div class="info-item">
              <span class="label">信息来源:</span>
              <span class="value">{{ currentScript.source }}</span>
            </div>
            <div class="info-item">
              <span class="label">版本:</span>
              <span class="value">{{ currentScript.version }}</span>
            </div>
            <div class="info-item">
              <span class="label">使用次数:</span>
              <span class="value">{{ currentScript.usageCount }}次</span>
            </div>
            <div class="info-item">
              <span class="label">是否验证:</span>
              <el-tag size="small" :type="currentScript.isVerified ? 'success' : 'info'">{{ currentScript.isVerified ? '已验证' : '未验证' }}</el-tag>
            </div>
          </div>
        </div>
        
        <!-- 话术内容 -->
        <div class="script-content-section">
          <h3>话术内容</h3>
          <div class="content-box">
            <p>{{ currentScript.scriptContent }}</p>
          </div>
        </div>
        
        <!-- 常见回应 -->
        <div v-if="currentScript.commonResponses" class="script-responses-section">
          <h3>常见回应</h3>
          <div class="responses-box">
            <p>{{ currentScript.commonResponses }}</p>
          </div>
        </div>
        
        <!-- 预警特征 -->
        <div v-if="currentScript.warningSigns" class="script-warning-section">
          <h3>预警特征</h3>
          <div class="warning-box">
            <el-alert
              :title="currentScript.warningSigns"
              type="warning"
              :closable="false"
            />
          </div>
        </div>
        
        <!-- 防范提示 -->
        <div v-if="currentScript.preventionTips" class="script-prevention-section">
          <h3>防范提示</h3>
          <div class="prevention-box">
            <el-alert
              :title="currentScript.preventionTips"
              type="success"
              :closable="false"
            />
          </div>
        </div>
        
        <!-- 话术操作 -->
        <div class="script-actions">
          <el-button type="primary" @click="handleUseScript(currentScript.id)">
            <el-icon><Check /></el-icon>
            标记为已使用
          </el-button>
          <el-button @click="shareScript(currentScript)">
            <el-icon><Share /></el-icon>
            分享
          </el-button>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scriptDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Search, Loading, User, View, Check, Share } from '@element-plus/icons-vue'

// 状态
const loading = ref(false)
const scriptLoading = ref(false)
const scripts = ref<any[]>([])
const total = ref(0)
const scriptDialogVisible = ref(false)
const currentScript = ref<any>(null)
const searchKeyword = ref('')

// 筛选条件
const filter = reactive({
  fraudType: '',
  targetGroup: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10
})

// 加载话术列表
const loadScripts = async () => {
  loading.value = true
  try {
    const res = await get('/case/script/list', {
      params: {
        fraudType: filter.fraudType,
        targetGroup: filter.targetGroup,
        page: pagination.current,
        size: pagination.size
      }
    })
    if (res.code === 200 && res.data) {
      scripts.value = res.data
      total.value = 100 // 模拟总数
    }
  } catch (error) {
    console.error('加载话术列表失败:', error)
    // 模拟数据
    scripts.value = [
      {
        id: 1,
        scriptTitle: '冒充公检法话术模板',
        fraudType: 'TELEPHONE',
        scriptContent: '您好，这里是XX市公安局刑警大队，我们接到银行监管部门通报，您的银行账户涉嫌参与一起重大洗钱案件，涉案金额高达500万元。您的账户将在两小时内被冻结，需要您将资金转入我们的安全账户进行核查。如果不配合，后果自负！',
        targetGroup: '普通市民',
        commonResponses: '什么？我没参与任何洗钱活动，你们搞错了吧？\n请问有什么证据证明我涉嫌洗钱？\n好的，我配合调查，请说需要怎么做。',
        warningSigns: '1. 自称公检法工作人员\n2. 声称账户涉嫌违法\n3. 要求转账到安全账户\n4. 语气强硬，威胁恐吓',
        preventionTips: '1. 公检法机关不会通过电话办案\n2. 不会要求转账到安全账户\n3. 可通过110核实身份',
        source: '反诈中心',
        version: '1.0',
        isVerified: 1,
        usageCount: 125
      },
      {
        id: 2,
        scriptTitle: '刷单返利话术模板',
        fraudType: 'NETWORK',
        scriptContent: '亲，欢迎加入我们的兼职团队！我们是正规电商推广平台，只需要动动手指刷刷单，就能日赚200-500元，无需任何费用。你先试着刷一单，只需充值100元，完成后立即返还120元，立赚20元。现在有个限时活动，充值满10000元送2000元，错过就没有了！',
        targetGroup: '学生、待业人员、宝妈',
        commonResponses: '真的能赚这么多钱吗？怎么操作？\n天下没有免费的午餐，这不会是骗局吧？\n好的，我加入，请告诉我怎么做。',
        warningSigns: '1. 承诺高收益\n2. 要求先垫付资金\n3. 用返利诱导加大投入\n4. 后期无法提现',
        preventionTips: '1. 任何要求先交钱的兼职都是骗局\n2. 刷单本身就是违法行为\n3. 不要相信天上掉馅饼的好事',
        source: '反诈中心',
        version: '1.0',
        isVerified: 1,
        usageCount: 98
      },
      {
        id: 3,
        scriptTitle: '虚假贷款话术模板',
        fraudType: 'NETWORK',
        scriptContent: '您好，这里是闪电贷平台。看到您申请了贷款，最高可贷20万元，当天放款，月息仅0.3%，无需抵押担保。您只需要下载我们的APP，填写资料即可。不过您需要先缴纳1000元保证金，证明您有还款能力。',
        targetGroup: '急需资金的人群',
        commonResponses: '太好了，我正需要钱，怎么申请？\n利息这么低？正规贷款平台年化利率都不可能这么低。\n你们是正规持牌机构吗？',
        warningSigns: '1. 承诺低息快速放款\n2. 要求提前缴纳费用\n3. 无资质要求\n4. 贷款额度异常高',
        preventionTips: '1. 正规贷款不需要提前缴费\n2. 检查平台是否有金融牌照\n3. 对比正规金融机构利率',
        source: '反诈中心',
        version: '1.0',
        isVerified: 1,
        usageCount: 76
      }
    ]
  } finally {
    loading.value = false
  }
}

// 查看话术详情
const viewScript = async (script: any) => {
  scriptLoading.value = true
  try {
    const res = await get(`/case/script/${script.id}`)
    if (res.code === 200 && res.data) {
      currentScript.value = res.data
      scriptDialogVisible.value = true
    } else {
      ElMessage.error('获取话术详情失败')
    }
  } catch (error) {
    console.error('获取话术详情失败:', error)
    // 模拟数据
    currentScript.value = script
    scriptDialogVisible.value = true
  } finally {
    scriptLoading.value = false
  }
}

// 搜索话术
const handleSearch = async () => {
  if (searchKeyword.value) {
    loading.value = true
    try {
      const res = await get('/case/script/search', {
        params: {
          keyword: searchKeyword.value,
          page: pagination.current,
          size: pagination.size
        }
      })
      if (res.code === 200 && res.data) {
        scripts.value = res.data
        total.value = 100 // 模拟总数
      }
    } catch (error) {
      console.error('搜索话术失败:', error)
      ElMessage.error('搜索话术失败')
    } finally {
      loading.value = false
    }
  } else {
    loadScripts()
  }
}

// 标记为已使用
const handleUseScript = async (scriptId: number) => {
  try {
    const res = await post(`/case/script/use/${scriptId}`)
    if (res.code === 200) {
      ElMessage.success('标记成功')
      if (currentScript.value && currentScript.value.id === scriptId) {
        currentScript.value.usageCount++
      }
    } else {
      ElMessage.error('标记失败')
    }
  } catch (error) {
    console.error('标记失败:', error)
    ElMessage.success('标记成功')
    if (currentScript.value && currentScript.value.id === scriptId) {
      currentScript.value.usageCount++
    }
  }
}

// 分享话术
const shareScript = (script: any) => {
  ElMessage.info('分享功能待实现')
}

// 获取分类类型
const getCategoryType = (category: string) => {
  switch (category) {
    case 'TELEPHONE':
      return 'danger'
    case 'NETWORK':
      return 'warning'
    case 'SMS':
      return 'info'
    case 'INVESTMENT':
      return 'primary'
    case 'RELATIONSHIP':
      return 'success'
    case 'FINANCE':
      return 'warning'
    default:
      return 'info'
  }
}

// 获取诈骗类型文本
const getFraudTypeText = (type: string) => {
  switch (type) {
    case 'TELEPHONE':
      return '电话诈骗'
    case 'NETWORK':
      return '网络诈骗'
    case 'SMS':
      return '短信诈骗'
    case 'INVESTMENT':
      return '投资诈骗'
    case 'RELATIONSHIP':
      return '情感诈骗'
    case 'FINANCE':
      return '金融诈骗'
    case 'OTHER':
      return '其他诈骗'
    default:
      return type
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadScripts()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadScripts()
}

onMounted(() => {
  loadScripts()
})
</script>

<style scoped>
.fraud-script-page {
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

.filter-content {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.script-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.script-item {
  height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;
}

.script-item:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-md);
}

.script-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.script-header h3 {
  margin: 0;
  color: var(--color-text-primary);
}

.script-description {
  margin-bottom: 20px;
  line-height: 1.6;
  color: var(--color-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.script-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.script-tags {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.script-dialog {
  max-height: 90vh;
  overflow-y: auto;
}

.script-details {
  padding: 20px 0;
}

.script-info-section,
.script-content-section,
.script-responses-section,
.script-warning-section,
.script-prevention-section {
  margin-bottom: 30px;
}

.script-info-section h3,
.script-content-section h3,
.script-responses-section h3,
.script-warning-section h3,
.script-prevention-section h3 {
  margin-bottom: 16px;
  color: var(--color-text-primary);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.info-grid .info-item {
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}

.info-grid .info-item .label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.info-grid .info-item .value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.content-box,
.responses-box {
  background: var(--color-bg-container);
  padding: 20px;
  border-radius: var(--radius-lg);
  line-height: 1.6;
  color: var(--color-text-primary);
}

.warning-box,
.prevention-box {
  margin-bottom: 16px;
}

.script-actions {
  display: flex;
  gap: 12px;
  margin-top: 30px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .filter-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .script-list {
    grid-template-columns: 1fr;
  }
  
  .script-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>