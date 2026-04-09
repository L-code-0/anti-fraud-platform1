<template>
  <div class="advice-page">
    <div class="page-header">
      <h2 class="page-title">专家建议</h2>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        发布建议
      </el-button>
    </div>
    
    <el-card shadow="hover" class="tips-card">
      <template #header>
        <span>撰写规范</span>
      </template>
      <ul class="tips-list">
        <li>建议内容应具有针对性和实用性</li>
        <li>针对当前高发诈骗类型提供具体防范措施</li>
        <li>可以结合真实案例进行分析</li>
        <li>语言要通俗易懂，便于普通用户理解</li>
      </ul>
    </el-card>
    
    <el-timeline>
      <el-timeline-item
        v-for="item in adviceList"
        :key="item.id"
        :timestamp="item.createTime"
        placement="top"
        type="primary"
      >
        <el-card shadow="hover" class="advice-card">
          <div class="advice-header">
            <el-tag :type="item.categoryType" size="small">
              {{ item.category }}
            </el-tag>
            <span class="author">发布者：{{ item.author }}</span>
          </div>
          <h3 class="advice-title">{{ item.title }}</h3>
          <p class="advice-content">{{ item.content }}</p>
          <div class="card-footer">
            <span><el-icon><View /></el-icon> {{ item.viewCount }}</span>
            <span><el-icon><Star /></el-icon> {{ item.thumbCount }}</span>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    
    <el-empty v-if="adviceList.length === 0" description="暂无专家建议" />
    
    <!-- 添加建议弹窗 -->
    <el-dialog v-model="showAddDialog" title="发布专家建议" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="建议标题">
          <el-input v-model="form.title" placeholder="请输入建议标题" />
        </el-form-item>
        <el-form-item label="建议分类">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option label="防骗技巧" value="防骗技巧" />
            <el-option label="安全提醒" value="安全提醒" />
            <el-option label="案例警示" value="案例警示" />
            <el-option label="政策解读" value="政策解读" />
          </el-select>
        </el-form-item>
        <el-form-item label="建议内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入建议内容" />
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
  category: '防骗技巧',
  content: ''
})

const adviceList = ref([
  {
    id: 1,
    title: '如何识别冒充公检法诈骗',
    category: '防骗技巧',
    categoryType: 'success',
    author: '专家李明',
    content: '公检法机关不会通过电话、微信、QQ等方式办案，也不会设立"安全账户"。如接到类似电话，请立即挂断并拨打110核实。',
    viewCount: 3456,
    thumbCount: 234,
    createTime: '2024-03-15'
  },
  {
    id: 2,
    title: '警惕网络购物退款陷阱',
    category: '安全提醒',
    categoryType: 'warning',
    author: '专家王芳',
    content: '正规电商平台的退款流程会在APP内完成，不会要求你点击陌生链接或提供验证码。遇到"客服"主动联系时，请通过官方渠道核实。',
    viewCount: 2345,
    thumbCount: 156,
    createTime: '2024-03-10'
  },
  {
    id: 3,
    title: '刷单返利都是诈骗',
    category: '案例警示',
    categoryType: 'danger',
    author: '专家张强',
    content: '刷单本身就是违法行为，且绝大多数刷单返利都是诈骗。前几笔小额返利只是为了让你放松警惕，后续大额刷单将血本无归。',
    viewCount: 4567,
    thumbCount: 345,
    createTime: '2024-03-05'
  }
])

const handleSubmit = () => {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  ElMessage.success('发布成功')
  showAddDialog.value = false
  form.value = { title: '', category: '防骗技巧', content: '' }
}
</script>

<style scoped>
.advice-page {
  max-width: 800px;
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

.advice-card {
  border-radius: 12px;
}

.advice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.author {
  color: var(--text-secondary);
  font-size: 12px;
}

.advice-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px;
  color: var(--text-primary);
}

.advice-content {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0 0 12px;
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
