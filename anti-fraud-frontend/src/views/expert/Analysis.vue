<template>
  <div class="analysis-page">
    <div class="page-header">
      <h2 class="page-title">案例分析</h2>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        发布案例
      </el-button>
    </div>
    
    <el-card shadow="hover" class="tips-card">
      <template #header>
        <span>撰写指南</span>
      </template>
      <ul class="tips-list">
        <li>详细描述案例的背景、过程和结果</li>
        <li>分析诈骗手段的关键特征和识别方法</li>
        <li>提供实用的防范建议和应对策略</li>
        <li>适当使用截图或图表辅助说明</li>
      </ul>
    </el-card>
    
    <el-row :gutter="20">
      <el-col :span="8" v-for="item in analysisList" :key="item.id">
        <el-card shadow="hover" class="analysis-card">
          <template #header>
            <div class="card-header">
              <el-tag :type="item.type === 1 ? 'danger' : 'warning'" size="small">
                {{ item.type === 1 ? '典型案例' : '新型诈骗' }}
              </el-tag>
              <span class="date">{{ item.createTime }}</span>
            </div>
          </template>
          <h3 class="analysis-title">{{ item.title }}</h3>
          <p class="analysis-desc">{{ item.summary }}</p>
          <div class="card-footer">
            <span><el-icon><View /></el-icon> {{ item.viewCount }}</span>
            <span><el-icon><Star /></el-icon> {{ item.thumbCount }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-empty v-if="analysisList.length === 0" description="暂无案例分析" />
    
    <!-- 添加案例弹窗 -->
    <el-dialog v-model="showAddDialog" title="发布案例分析" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="案例标题">
          <el-input v-model="form.title" placeholder="请输入案例标题" />
        </el-form-item>
        <el-form-item label="案例类型">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="典型案例" :value="1" />
            <el-option label="新型诈骗" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="案例摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入案例摘要" />
        </el-form-item>
        <el-form-item label="详细内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入详细内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, View, Star } from '@element-plus/icons-vue'

const showAddDialog = ref(false)
const form = ref({
  title: '',
  type: 1,
  summary: '',
  content: ''
})

const analysisList = ref([
  {
    id: 1,
    title: '冒充客服诈骗：警惕"退款"陷阱',
    type: 1,
    summary: '受害人接到自称电商客服的电话，称其订单异常需要退款，按照对方指示操作后被骗。',
    viewCount: 1234,
    thumbCount: 89,
    createTime: '2026-03-15'
  },
  {
    id: 2,
    title: 'AI换脸诈骗新手段曝光',
    type: 2,
    summary: '近期出现利用AI换脸技术冒充熟人诈骗的案件，骗子通过视频通话取得信任后实施诈骗。',
    viewCount: 2345,
    thumbCount: 156,
    createTime: '2026-03-10'
  }
])

const handleSubmit = () => {
  if (!form.value.title || !form.value.summary) {
    ElMessage.warning('请填写完整信息')
    return
  }
  ElMessage.success('发布成功')
  showAddDialog.value = false
  form.value = { title: '', type: 1, summary: '', content: '' }
}
</script>

<style scoped>
.analysis-page {
  max-width: 1200px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: var(--text-primary);
}

.tips-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  color: var(--text-secondary);
}

.tips-list li {
  margin-bottom: 8px;
}

.analysis-card {
  margin-bottom: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.analysis-card:hover {
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.date {
  color: var(--text-secondary);
  font-size: 12px;
}

.analysis-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
  color: var(--text-primary);
}

.analysis-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  gap: 16px;
  color: var(--text-secondary);
  font-size: 14px;
}

.card-footer span {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
