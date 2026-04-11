<template>
  <div class="admin-knowledge">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>知识管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 发布知识
          </el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="分类">
          <el-select v-model="searchParams.categoryId" placeholder="全部" clearable>
            <el-option v-for="cat in categories" :key="cat.id" :value="cat.id" :label="cat.categoryName" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="全部" clearable>
            <el-option :value="1" label="上架" />
            <el-option :value="0" label="下架" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadKnowledge">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 知识列表 -->
      <el-table :data="knowledgeList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="title-cell">
              <span>{{ row.title }}</span>
              <div class="tags">
                <el-tag v-if="row.isTop" type="danger" size="small">置顶</el-tag>
                <el-tag v-if="row.isHot" type="warning" size="small">热门</el-tag>
                <el-tag v-if="row.isRecommend" type="success" size="small">推荐</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="contentType" label="类型" width="80">
          <template #default="{ row }">
            {{ ['文章', '视频', '案例'][row.contentType - 1] }}
          </template>
        </el-table-column>
        <el-table-column prop="authorName" label="作者" width="100" />
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button 
              text 
              :type="row.status === 1 ? 'warning' : 'success'" 
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button text type="danger" @click="deleteKnowledge(row)">删除</el-button>
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
          @current-change="loadKnowledge"
        />
      </div>
    </el-card>
    
    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑知识' : '发布知识'" width="800px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option v-for="cat in categories" :key="cat.id" :value="cat.id" :label="cat.categoryName" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容类型">
          <el-radio-group v-model="form.contentType">
            <el-radio :value="1">文章</el-radio>
            <el-radio :value="2">视频</el-radio>
            <el-radio :value="3">案例</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入摘要" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="请输入封面图URL" />
        </el-form-item>
        <el-form-item label="设置">
          <el-checkbox v-model="form.isTop">置顶</el-checkbox>
          <el-checkbox v-model="form.isHot">热门</el-checkbox>
          <el-checkbox v-model="form.isRecommend">推荐</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post, put, del } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const knowledgeList = ref<any[]>([])
const categories = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const editId = ref<number | null>(null)

const searchParams = reactive({
  categoryId: null as number | null,
  status: null as number | null
})

const form = reactive({
  title: '',
  categoryId: null as number | null,
  contentType: 1,
  summary: '',
  content: '',
  coverImage: '',
  isTop: false,
  isHot: false,
  isRecommend: false
})

const loadCategories = async () => {
  try {
    const res = await get('/knowledge/categories')
    categories.value = res.data || []
  } catch (e) {
    categories.value = [
      { id: 1, categoryName: '电信诈骗' },
      { id: 2, categoryName: '网络诈骗' },
      { id: 3, categoryName: '校园贷诈骗' },
      { id: 4, categoryName: '兼职诈骗' }
    ]
  }
}

const loadKnowledge = async () => {
  try {
    const res = await get('/admin/knowledge/list', {
      page: page.value,
      size: size.value,
      ...searchParams
    })
    knowledgeList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    knowledgeList.value = [
      { id: 1, title: '冒充公检法诈骗案例分析', contentType: 1, authorName: '王专家', viewCount: 2356, likeCount: 128, status: 1, isHot: true, isRecommend: true, publishTime: '2026-01-15' },
      { id: 2, title: '网络兼职刷单诈骗防范指南', contentType: 1, authorName: '李老师', viewCount: 1892, likeCount: 98, status: 1, isRecommend: true, publishTime: '2026-01-14' },
      { id: 3, title: '校园贷陷阱识别指南', contentType: 1, authorName: '王专家', viewCount: 3210, likeCount: 156, status: 1, isHot: true, publishTime: '2026-01-13' }
    ]
    total.value = 3
  }
}

const openDialog = (row?: any) => {
  editId.value = row?.id || null
  if (row) {
    Object.assign(form, {
      title: row.title,
      categoryId: row.categoryId,
      contentType: row.contentType,
      summary: row.summary || '',
      content: row.content || '',
      coverImage: row.coverImage || '',
      isTop: row.isTop,
      isHot: row.isHot,
      isRecommend: row.isRecommend
    })
  } else {
    Object.assign(form, {
      title: '',
      categoryId: null,
      contentType: 1,
      summary: '',
      content: '',
      coverImage: '',
      isTop: false,
      isHot: false,
      isRecommend: false
    })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.title || !form.categoryId) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    if (editId.value) {
      await put(`/admin/knowledge/${editId.value}`, form)
      ElMessage.success('更新成功')
    } else {
      await post('/admin/knowledge', form)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadKnowledge()
  } catch (e) {}
}

const toggleStatus = async (row: any) => {
  try {
    await put(`/admin/knowledge/${row.id}/status`, null, {
      params: { status: row.status === 1 ? 0 : 1 }
    })
    ElMessage.success('操作成功')
    loadKnowledge()
  } catch (e) {}
}

const deleteKnowledge = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除该知识吗？', '提示', { type: 'warning' })
    await del(`/admin/knowledge/${row.id}`)
    ElMessage.success('删除成功')
    loadKnowledge()
  } catch (e) {}
}

onMounted(() => {
  loadCategories()
  loadKnowledge()
})
</script>

<style scoped>
.admin-knowledge {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.title-cell {
  display: flex;
  flex-direction: column;
}

.title-cell .tags {
  margin-top: 4px;
  display: flex;
  gap: 4px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
