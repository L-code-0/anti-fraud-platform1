<template>
  <div class="admin-reports">
    <el-card>
      <template #header>
        <span>举报管理</span>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="全部" clearable>
            <el-option :value="0" label="待处理" />
            <el-option :value="1" label="处理中" />
            <el-option :value="2" label="已处理" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="searchParams.riskLevel" placeholder="全部" clearable>
            <el-option :value="1" label="低" />
            <el-option :value="2" label="中" />
            <el-option :value="3" label="高" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadReports">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 举报列表 -->
      <el-table :data="reportList" style="width: 100%">
        <el-table-column prop="reportNo" label="举报编号" width="150" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="reportType" label="类型" width="100">
          <template #default="{ row }">
            {{ ['可疑电话', '可疑短信', '可疑链接', '其他'][row.reportType - 1] }}
          </template>
        </el-table-column>
        <el-table-column prop="fraudType" label="诈骗类型" width="100" />
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getRiskType(row.riskLevel)">
              {{ ['低', '中', '高'][row.riskLevel - 1] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ ['待处理', '处理中', '已处理'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button text type="primary" @click="viewReport(row)">查看</el-button>
            <el-button v-if="row.status < 2" text type="success" @click="handleReport(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadReports"
        />
      </div>
    </el-card>
    
    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailVisible" title="举报详情" width="600px">
      <el-descriptions :column="1" border v-if="currentReport">
        <el-descriptions-item label="举报编号">{{ currentReport.reportNo }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ currentReport.title }}</el-descriptions-item>
        <el-descriptions-item label="举报类型">
          {{ ['可疑电话', '可疑短信', '可疑链接', '其他'][currentReport.reportType - 1] }}
        </el-descriptions-item>
        <el-descriptions-item label="诈骗类型">{{ currentReport.fraudType }}</el-descriptions-item>
        <el-descriptions-item label="详细描述">{{ currentReport.description }}</el-descriptions-item>
        <el-descriptions-item label="可疑号码">{{ currentReport.phoneNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="可疑链接">{{ currentReport.linkUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理结果">{{ currentReport.handleResult || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    
    <!-- 处理对话框 -->
    <el-dialog v-model="handleVisible" title="处理举报" width="500px">
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="风险等级">
          <el-radio-group v-model="handleForm.riskLevel">
            <el-radio :value="1">低</el-radio>
            <el-radio :value="2">中</el-radio>
            <el-radio :value="3">高</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理结果">
          <el-input v-model="handleForm.handleResult" type="textarea" :rows="3" placeholder="请输入处理结果" />
        </el-form-item>
        <el-form-item label="奖励积分">
          <el-input-number v-model="handleForm.rewardPoints" :min="0" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage } from 'element-plus'

const reportList = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const handleVisible = ref(false)
const currentReport = ref<any>(null)

const searchParams = reactive({
  status: null as number | null,
  riskLevel: null as number | null
})

const handleForm = reactive({
  riskLevel: 1,
  handleResult: '',
  rewardPoints: 0
})

const getRiskType = (level: number) => {
  const types: Record<number, string> = { 1: 'info', 2: 'warning', 3: 'danger' }
  return types[level] || 'info'
}

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'success' }
  return types[status] || 'info'
}

const loadReports = async () => {
  try {
    const res = await get('/admin/report/list', {
      page: page.value,
      size: size.value,
      ...searchParams
    })
    reportList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    reportList.value = [
      { id: 1, reportNo: 'RPT20260115001', title: '可疑电话举报', reportType: 1, fraudType: '电信诈骗', riskLevel: 3, status: 0, createTime: '2026-01-15 10:30:00' },
      { id: 2, reportNo: 'RPT20260114002', title: '可疑链接举报', reportType: 3, fraudType: '网络诈骗', riskLevel: 2, status: 1, createTime: '2026-01-14 15:20:00' },
      { id: 3, reportNo: 'RPT20260113003', title: '兼职诈骗举报', reportType: 4, fraudType: '兼职诈骗', riskLevel: 1, status: 2, handleResult: '已核实并处理', createTime: '2026-01-13 09:15:00' }
    ]
    total.value = 3
  }
}

const viewReport = (row: any) => {
  currentReport.value = row
  detailVisible.value = true
}

const handleReport = (row: any) => {
  currentReport.value = row
  handleForm.riskLevel = row.riskLevel || 1
  handleForm.handleResult = ''
  handleForm.rewardPoints = 10
  handleVisible.value = true
}

const submitHandle = async () => {
  if (!handleForm.handleResult) {
    ElMessage.warning('请填写处理结果')
    return
  }
  
  try {
    await post(`/admin/report/${currentReport.value.id}/handle`, handleForm)
    ElMessage.success('处理成功')
    handleVisible.value = false
    loadReports()
  } catch (e) {}
}

onMounted(() => {
  loadReports()
})
</script>

<style scoped>
.admin-reports {
  max-width: 1400px;
  margin: 0 auto;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
