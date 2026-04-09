<template>
  <div class="admin-works">
    <el-card>
      <template #header>
        <span>作品管理</span>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="作品类型">
          <el-select v-model="searchParams.worksType" placeholder="全部" clearable>
            <el-option value="ESSAY" label="征文" />
            <el-option value="VIDEO" label="短视频" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="全部" clearable>
            <el-option :value="0" label="待审核" />
            <el-option :value="1" label="已通过" />
            <el-option :value="2" label="已拒绝" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadWorks">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 作品列表 -->
      <el-table :data="worksList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="作品标题" min-width="200" />
        <el-table-column prop="worksTypeName" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.worksType === 'ESSAY' ? 'success' : 'warning'">
              {{ row.worksTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="authorName" label="作者" width="100" />
        <el-table-column prop="department" label="院系" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isExcellent" label="优秀" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isExcellent" type="danger">优秀作品</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button text type="primary" @click="viewWorks(row)">查看</el-button>
            <el-button v-if="row.status === 0" text type="success" @click="auditWorks(row, 1)">通过</el-button>
            <el-button v-if="row.status === 0" text type="danger" @click="auditWorks(row, 2)">拒绝</el-button>
            <el-button text type="warning" @click="setExcellent(row)">{{ row.isExcellent ? '取消优秀' : '设为优秀' }}</el-button>
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
          @current-change="loadWorks"
        />
      </div>
    </el-card>
    
    <!-- 查看作品详情 -->
    <el-dialog v-model="detailVisible" title="作品详情" width="700px">
      <div v-if="currentWorks" class="works-detail">
        <h3>{{ currentWorks.title }}</h3>
        <div class="works-meta">
          <span>作者：{{ currentWorks.authorName }}</span>
          <span>院系：{{ currentWorks.department }}</span>
          <span>类型：{{ currentWorks.worksTypeName }}</span>
        </div>
        <el-divider />
        <div v-if="currentWorks.worksType === 'ESSAY'" class="works-content">
          {{ currentWorks.content }}
        </div>
        <div v-else class="works-video">
          <video v-if="currentWorks.fileUrl" :src="currentWorks.fileUrl" controls style="width: 100%"></video>
          <el-empty v-else description="暂无视频" />
        </div>
        <div v-if="currentWorks.description" class="works-desc">
          <strong>作品简介：</strong>{{ currentWorks.description }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const worksList = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentWorks = ref<any>(null)

const searchParams = reactive({
  worksType: '',
  status: null as number | null
})

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 0: 'info', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const loadWorks = async () => {
  try {
    const res = await get('/works/list', {
      page: page.value,
      size: size.value,
      ...searchParams
    })
    worksList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    worksList.value = [
      { id: 1, title: '反诈有我，安全同行', worksType: 'ESSAY', worksTypeName: '征文', authorName: '张三', department: '计算机学院', status: 0, statusName: '待审核', isExcellent: false, viewCount: 0, likeCount: 0 },
      { id: 2, title: '冒充公检法诈骗揭秘', worksType: 'VIDEO', worksTypeName: '短视频', authorName: '李四', department: '新闻学院', status: 1, statusName: '已通过', isExcellent: true, viewCount: 256, likeCount: 45 },
      { id: 3, title: '守护青春，远离诈骗', worksType: 'ESSAY', worksTypeName: '征文', authorName: '王五', department: '法学院', status: 1, statusName: '已通过', isExcellent: false, viewCount: 128, likeCount: 23 }
    ]
    total.value = 3
  }
}

const viewWorks = (row: any) => {
  currentWorks.value = {
    ...row,
    content: '这是一篇关于反诈的征文作品，详细描述了作者对电信诈骗的认识和防范建议...',
    description: '本文从大学生角度出发，分享了如何识别和防范电信诈骗的经验。'
  }
  detailVisible.value = true
}

const auditWorks = async (row: any, status: number) => {
  try {
    const remark = status === 2 ? await ElMessageBox.prompt('请输入拒绝原因', '审核', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /\S+/,
      inputErrorMessage: '请输入拒绝原因'
    }) : null
    
    await post(`/works/${row.id}/audit`, null, {
      params: { status, remark: remark?.value || '' }
    })
    ElMessage.success('审核完成')
    loadWorks()
  } catch (e) {}
}

const setExcellent = async (row: any) => {
  try {
    await post(`/works/${row.id}/excellent`, null, {
      params: { excellent: !row.isExcellent, rank: 1, points: 50 }
    })
    ElMessage.success(row.isExcellent ? '已取消优秀' : '已设为优秀作品')
    loadWorks()
  } catch (e) {}
}

onMounted(() => {
  loadWorks()
})
</script>

<style scoped>
.admin-works {
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

.works-detail h3 {
  margin: 0 0 15px;
}

.works-meta {
  color: #909399;
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.works-content {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
  line-height: 1.8;
}

.works-video {
  margin: 15px 0;
}

.works-desc {
  margin-top: 15px;
  color: #606266;
}
</style>
