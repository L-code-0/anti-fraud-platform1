<template>
  <div class="notice-page">
    <!-- 头部 -->
    <div class="header">
      <h1>系统公告</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleAddNotice">
          <el-icon><Plus /></el-icon>
          发布公告
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="公告状态">
          <el-select v-model="filterForm.status" placeholder="请选择" clearable>
            <el-option value="draft" label="草稿" />
            <el-option value="published" label="已发布" />
            <el-option value="expired" label="已过期" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="filterForm.type" placeholder="请选择" clearable>
            <el-option value="system" label="系统公告" />
            <el-option value="activity" label="活动通知" />
            <el-option value="alert" label="安全预警" />
            <el-option value="info" label="信息通知" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filterForm.keyword" placeholder="请输入标题或内容关键词" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 公告列表 -->
    <div class="notice-list" v-loading="loading">
      <el-table :data="notices" stripe>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="公告标题" min-width="200">
          <template #default="{ row }">
            <div class="title-wrapper">
              <span :class="{ 'highlight': row.isTop }" :style="{ fontWeight: row.isTop ? 'bold' : 'normal' }">
                {{ row.title }}
              </span>
              <el-tag v-if="row.isTop" size="small" type="warning" effect="dark" style="margin-left: 8px">
                置顶
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="公告类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeType(row.type)" size="small">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisher" label="发布人" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ row.publishTime ? formatTime(row.publishTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="过期时间" width="180">
          <template #default="{ row }">
            {{ row.expireTime ? formatTime(row.expireTime) : '永久' }}
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="查看次数" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleViewNotice(row)">
              查看
            </el-button>
            <el-button text type="success" size="small" @click="handleEditNotice(row)" v-if="row.status === 'draft'">
              编辑
            </el-button>
            <el-button text type="warning" size="small" @click="handlePublishNotice(row)" v-if="row.status === 'draft'">
              发布
            </el-button>
            <el-button text type="danger" size="small" @click="handleDeleteNotice(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 公告编辑对话框 -->
    <el-dialog v-model="noticeVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="800px">
      <el-form :model="noticeForm" :rules="noticeRules" ref="noticeFormRef">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="noticeForm.type" placeholder="请选择公告类型">
            <el-option value="system" label="系统公告" />
            <el-option value="activity" label="活动通知" />
            <el-option value="alert" label="安全预警" />
            <el-option value="info" label="信息通知" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-editor
            v-model="noticeForm.content"
            :min-height="300"
            :toolbar="toolbar"
          />
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker
            v-model="noticeForm.publishTime"
            type="datetime"
            placeholder="请选择发布时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="过期时间">
          <el-date-picker
            v-model="noticeForm.expireTime"
            type="datetime"
            placeholder="请选择过期时间（可选）"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="noticeForm.isTop" />
        </el-form-item>
        <el-form-item label="是否弹窗">
          <el-switch v-model="noticeForm.isPopup" />
        </el-form-item>
        <el-form-item label="目标用户">
          <el-select v-model="noticeForm.targetUsers" multiple placeholder="请选择目标用户">
            <el-option value="all" label="所有用户" />
            <el-option value="student" label="学生" />
            <el-option value="teacher" label="教师" />
            <el-option value="admin" label="管理员" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="noticeVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitNotice" :loading="submitting">
            {{ isEdit ? '保存修改' : '发布公告' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 公告查看对话框 -->
    <el-dialog v-model="viewVisible" title="公告详情" width="800px">
      <div class="notice-detail" v-if="currentNotice">
        <h2 class="notice-title">{{ currentNotice.title }}</h2>
        <div class="notice-meta">
          <span class="meta-item">
            <el-tag :type="getTypeType(currentNotice.type)" size="small">
              {{ getTypeText(currentNotice.type) }}
            </el-tag>
          </span>
          <span class="meta-item">发布人：{{ currentNotice.publisher }}</span>
          <span class="meta-item">发布时间：{{ formatTime(currentNotice.publishTime) }}</span>
          <span class="meta-item" v-if="currentNotice.expireTime">过期时间：{{ formatTime(currentNotice.expireTime) }}</span>
          <span class="meta-item">查看次数：{{ currentNotice.viewCount }}</span>
        </div>
        <div class="notice-content" v-html="currentNotice.content"></div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElForm } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const submitting = ref(false)
const noticeVisible = ref(false)
const viewVisible = ref(false)
const isEdit = ref(false)
const currentNotice = ref<any>(null)
const noticeFormRef = ref<InstanceType<typeof ElForm> | null>(null)

const filterForm = reactive({
  status: '',
  type: '',
  dateRange: [] as string[],
  keyword: ''
})

const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

const noticeForm = reactive({
  id: '',
  title: '',
  type: 'system',
  content: '',
  publishTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
  expireTime: '',
  isTop: false,
  isPopup: false,
  targetUsers: ['all']
})

const noticeRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  publishTime: [{ required: true, message: '请选择发布时间', trigger: 'change' }]
}

const toolbar = [
  'undo', 'redo', '|',
  'bold', 'italic', 'underline', 'strikethrough', '|',
  'font-size', 'font-family', '|',
  'list', '|',
  'link', '|',
  'emoji', '|',
  'image', '|',
  'full-screen'
]

const notices = ref<any[]>([])

function getTypeText(type: string): string {
  const textMap: Record<string, string> = {
    system: '系统公告',
    activity: '活动通知',
    alert: '安全预警',
    info: '信息通知'
  }
  return textMap[type] || type
}

function getTypeType(type: string): string {
  const typeMap: Record<string, string> = {
    system: 'primary',
    activity: 'success',
    alert: 'danger',
    info: 'info'
  }
  return typeMap[type] || 'info'
}

function getStatusText(status: string): string {
  const textMap: Record<string, string> = {
    draft: '草稿',
    published: '已发布',
    expired: '已过期'
  }
  return textMap[status] || status
}

function getStatusType(status: string): string {
  const typeMap: Record<string, string> = {
    draft: 'info',
    published: 'success',
    expired: 'warning'
  }
  return typeMap[status] || 'info'
}

function formatTime(timeStr: string): string {
  return new Date(timeStr).toLocaleString()
}

function handleSearch() {
  pagination.currentPage = 1
  fetchNotices()
}

function handleReset() {
  filterForm.status = ''
  filterForm.type = ''
  filterForm.dateRange = []
  filterForm.keyword = ''
  pagination.currentPage = 1
  fetchNotices()
}

function handleRefresh() {
  fetchNotices()
  ElMessage.success('刷新成功')
}

function handleAddNotice() {
  isEdit.value = false
  noticeForm.id = ''
  noticeForm.title = ''
  noticeForm.type = 'system'
  noticeForm.content = ''
  noticeForm.publishTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
  noticeForm.expireTime = ''
  noticeForm.isTop = false
  noticeForm.isPopup = false
  noticeForm.targetUsers = ['all']
  noticeVisible.value = true
}

function handleEditNotice(row: any) {
  isEdit.value = true
  noticeForm.id = row.id
  noticeForm.title = row.title
  noticeForm.type = row.type
  noticeForm.content = row.content
  noticeForm.publishTime = row.publishTime
  noticeForm.expireTime = row.expireTime
  noticeForm.isTop = row.isTop
  noticeForm.isPopup = row.isPopup
  noticeForm.targetUsers = row.targetUsers
  noticeVisible.value = true
}

function handleViewNotice(row: any) {
  currentNotice.value = row
  viewVisible.value = true
}

function handlePublishNotice(row: any) {
  // 模拟发布公告
  ElMessage.success('公告发布成功')
  fetchNotices()
}

function handleDeleteNotice(row: any) {
  // 模拟删除公告
  ElMessage.success('公告删除成功')
  fetchNotices()
}

function handleSubmitNotice() {
  if (!noticeFormRef.value) return
  
  noticeFormRef.value.validate((valid) => {
    if (valid) {
      submitting.value = true
      // 模拟提交公告
      setTimeout(() => {
        submitting.value = false
        noticeVisible.value = false
        ElMessage.success(isEdit.value ? '公告修改成功' : '公告发布成功')
        fetchNotices()
      }, 1000)
    }
  })
}

function handleSizeChange(size: number) {
  pagination.pageSize = size
  fetchNotices()
}

function handleCurrentChange(page: number) {
  pagination.currentPage = page
  fetchNotices()
}

function fetchNotices() {
  loading.value = true
  // 模拟获取公告列表
  setTimeout(() => {
    notices.value = [
      {
        id: 1,
        title: '系统升级通知',
        type: 'system',
        content: '<p>尊敬的用户，系统将于2026年4月15日凌晨2:00-4:00进行升级维护，期间系统可能会暂时无法访问，请您提前做好准备。</p>',
        status: 'published',
        publisher: '管理员',
        publishTime: new Date(Date.now() - 86400000).toISOString().slice(0, 19).replace('T', ' '),
        expireTime: new Date(Date.now() + 86400000).toISOString().slice(0, 19).replace('T', ' '),
        isTop: true,
        isPopup: true,
        targetUsers: ['all'],
        viewCount: 1250
      },
      {
        id: 2,
        title: '反诈知识竞赛活动通知',
        type: 'activity',
        content: '<p>为提高大家的反诈意识，我们将于2026年4月20日举办反诈知识竞赛活动，欢迎大家积极参与！</p><p>活动规则：...</p>',
        status: 'published',
        publisher: '管理员',
        publishTime: new Date(Date.now() - 172800000).toISOString().slice(0, 19).replace('T', ' '),
        expireTime: new Date(Date.now() + 604800000).toISOString().slice(0, 19).replace('T', ' '),
        isTop: false,
        isPopup: false,
        targetUsers: ['all'],
        viewCount: 850
      },
      {
        id: 3,
        title: '新型电信诈骗预警',
        type: 'alert',
        content: '<p>近期发现一种新型电信诈骗手段，诈骗分子冒充银行客服，以信用卡逾期为由骗取个人信息和验证码，请大家提高警惕！</p>',
        status: 'published',
        publisher: '管理员',
        publishTime: new Date(Date.now() - 259200000).toISOString().slice(0, 19).replace('T', ' '),
        expireTime: new Date(Date.now() + 1296000000).toISOString().slice(0, 19).replace('T', ' '),
        isTop: true,
        isPopup: true,
        targetUsers: ['all'],
        viewCount: 2100
      },
      {
        id: 4,
        title: '系统功能更新说明',
        type: 'info',
        content: '<p>本次更新新增了以下功能：</p><ol><li>实时数据大屏</li><li>个性化报告</li><li>风险评估模型</li><li>预测分析</li></ol>',
        status: 'published',
        publisher: '管理员',
        publishTime: new Date(Date.now() - 345600000).toISOString().slice(0, 19).replace('T', ' '),
        expireTime: '',
        isTop: false,
        isPopup: false,
        targetUsers: ['all'],
        viewCount: 1500
      },
      {
        id: 5,
        title: '测试公告',
        type: 'system',
        content: '<p>这是一条测试公告</p>',
        status: 'draft',
        publisher: '管理员',
        publishTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
        expireTime: '',
        isTop: false,
        isPopup: false,
        targetUsers: ['all'],
        viewCount: 0
      }
    ]
    pagination.total = notices.value.length
    loading.value = false
  }, 1000)
}

onMounted(() => {
  fetchNotices()
})
</script>

<style scoped>
.notice-page {
  padding: 24px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header h1 {
  margin: 0;
  font-size: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-bar {
  padding: 20px;
  background: white;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.notice-list {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

.title-wrapper {
  display: flex;
  align-items: center;
}

.highlight {
  color: #409eff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.notice-detail {
  padding: 20px 0;
}

.notice-title {
  margin: 0 0 20px 0;
  font-size: 24px;
  color: #333;
  text-align: center;
}

.notice-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
  font-size: 14px;
  color: #606266;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.notice-content {
  line-height: 1.8;
  color: #303133;
}

.notice-content img {
  max-width: 100%;
  height: auto;
  margin: 16px 0;
}

@media (max-width: 768px) {
  .notice-page {
    padding: 16px;
  }
  
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .filter-bar {
    padding: 16px;
  }
  
  .notice-list {
    padding: 16px;
  }
  
  .notice-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>