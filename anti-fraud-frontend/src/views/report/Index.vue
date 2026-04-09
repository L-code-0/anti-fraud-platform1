<template>
  <div class="report-page">
    <el-row :gutter="20">
      <!-- 预警列表 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>预警信息</span>
            </div>
          </template>
          
          <div
            v-for="warning in warningList"
            :key="warning.id"
            class="warning-item"
            @click="showWarningDetail(warning)"
          >
            <div class="warning-level" :class="`level-${warning.warningLevel}`">
              {{ getLevelText(warning.warningLevel) }}
            </div>
            <div class="warning-content">
              <h4>{{ warning.title }}</h4>
              <p>{{ warning.content }}</p>
              <div class="warning-meta">
                <span>{{ warning.fraudType }}</span>
                <span>{{ warning.publishTime }}</span>
                <span><el-icon><View /></el-icon> {{ warning.viewCount }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 快捷举报 -->
      <el-col :span="8">
        <el-card class="quick-report">
          <template #header>
            <span>快捷举报</span>
          </template>
          
          <el-form label-position="top">
            <el-form-item label="举报类型">
              <el-select v-model="reportForm.reportType" placeholder="请选择">
                <el-option :value="1" label="可疑电话" />
                <el-option :value="2" label="可疑短信" />
                <el-option :value="3" label="可疑链接" />
                <el-option :value="4" label="其他" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="诈骗类型">
              <el-select v-model="reportForm.fraudType" placeholder="请选择">
                <el-option value="电信诈骗" />
                <el-option value="网络诈骗" />
                <el-option value="校园贷诈骗" />
                <el-option value="兼职诈骗" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="简要描述">
              <el-input
                v-model="reportForm.description"
                type="textarea"
                :rows="4"
                placeholder="请简要描述诈骗情况..."
              />
            </el-form-item>
            
            <el-form-item label="可疑号码/链接">
              <el-input v-model="reportForm.phoneNumber" placeholder="请输入" />
            </el-form-item>
            
            <el-button type="primary" @click="submitReport" :loading="submitting">
              提交举报
            </el-button>
          </el-form>
        </el-card>
        
        <el-card class="my-reports">
          <template #header>
            <span>我的举报</span>
          </template>
          
          <div
            v-for="item in myReports"
            :key="item.id"
            class="report-item"
          >
            <div class="report-status">
              <el-tag :type="getStatusType(item.status)">
                {{ getStatusText(item.status) }}
              </el-tag>
            </div>
            <div class="report-content">
              <h5>{{ item.title }}</h5>
              <p>{{ item.reportNo }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 预警详情弹窗 -->
    <el-dialog v-model="showDetail" :title="currentWarning?.title" width="600px">
      <div class="warning-detail">
        <el-tag :type="getLevelType(currentWarning?.warningLevel)">
          {{ getLevelText(currentWarning?.warningLevel) }}
        </el-tag>
        <div class="detail-content">{{ currentWarning?.content }}</div>
        <div class="prevention-tips">
          <h4>防范要点</h4>
          <p>{{ currentWarning?.preventionTips }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'

const warningList = ref<any[]>([])
const myReports = ref<any[]>([])
const showDetail = ref(false)
const currentWarning = ref<any>(null)
const submitting = ref(false)

const reportForm = reactive({
  reportType: 1,
  fraudType: '',
  description: '',
  phoneNumber: ''
})

const getLevelText = (level: number) => {
  const levels: Record<number, string> = {
    1: '蓝色预警',
    2: '黄色预警',
    3: '橙色预警',
    4: '红色预警'
  }
  return levels[level] || '预警'
}

const getLevelType = (level: number) => {
  const types: Record<number, string> = {
    1: 'info',
    2: 'warning',
    3: 'danger',
    4: 'danger'
  }
  return types[level] || 'info'
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = {
    0: '待处理',
    1: '处理中',
    2: '已处理'
  }
  return texts[status] || '未知'
}

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    0: 'info',
    1: 'warning',
    2: 'success'
  }
  return types[status] || 'info'
}

const showWarningDetail = (warning: any) => {
  currentWarning.value = warning
  showDetail.value = true
}

const submitReport = async () => {
  if (!reportForm.description) {
    ElMessage.warning('请描述诈骗情况')
    return
  }
  
  submitting.value = true
  try {
    await post('/report/submit', reportForm)
    ElMessage.success('举报提交成功，感谢您的参与')
    reportForm.description = ''
    reportForm.phoneNumber = ''
    loadMyReports()
  } catch (e) {}
  submitting.value = false
}

const loadWarnings = async () => {
  try {
    const res = await get('/report/warnings')
    warningList.value = res.data || []
  } catch (e) {
    warningList.value = [
      { id: 1, title: '警惕新型"AI换脸"诈骗', content: '近期出现利用AI技术进行视频通话诈骗的新手法...', warningLevel: 4, fraudType: '电信诈骗', viewCount: 2356, publishTime: '2024-01-15' },
      { id: 2, title: '冒充客服退款诈骗高发', content: '近期冒充电商平台客服退款诈骗案件高发...', warningLevel: 3, fraudType: '电信诈骗', viewCount: 1892, publishTime: '2024-01-14' },
      { id: 3, title: '寒假兼职诈骗预警', content: '寒假期间是兼职诈骗高发期...', warningLevel: 2, fraudType: '兼职诈骗', viewCount: 1256, publishTime: '2024-01-13' }
    ]
  }
}

const loadMyReports = async () => {
  try {
    const res = await get('/report/my-reports')
    myReports.value = res.data?.records || []
  } catch (e) {
    myReports.value = []
  }
}

onMounted(() => {
  loadWarnings()
  loadMyReports()
})
</script>

<style scoped>
.report-page {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.warning-item {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #EBEEF5;
  cursor: pointer;
}

.warning-item:hover {
  background: #f5f7fa;
}

.warning-level {
  padding: 4px 8px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
  height: fit-content;
  margin-right: 16px;
}

.warning-level.level-1 { background: #409EFF; }
.warning-level.level-2 { background: #E6A23C; }
.warning-level.level-3 { background: #F56C6C; }
.warning-level.level-4 { background: #F56C6C; }

.warning-content h4 {
  margin-bottom: 8px;
  color: #303133;
}

.warning-content p {
  color: #606266;
  font-size: 13px;
  margin-bottom: 8px;
}

.warning-meta {
  display: flex;
  gap: 16px;
  color: #909399;
  font-size: 12px;
}

.warning-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.quick-report {
  margin-bottom: 20px;
}

.quick-report .el-select {
  width: 100%;
}

.my-reports .report-item {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #EBEEF5;
}

.report-content h5 {
  margin: 0 0 4px;
  color: #303133;
  font-size: 13px;
}

.report-content p {
  margin: 0;
  color: #909399;
  font-size: 12px;
}

.warning-detail .detail-content {
  margin: 16px 0;
  color: #606266;
  line-height: 1.8;
}

.prevention-tips {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
}

.prevention-tips h4 {
  margin-bottom: 8px;
  color: #303133;
}

.prevention-tips p {
  color: #606266;
  white-space: pre-line;
}
</style>
