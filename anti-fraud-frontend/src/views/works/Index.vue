<template>
  <div class="works-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>作品展示</h2>
      <el-button type="primary" @click="$router.push('/works/submit')">
        <el-icon><Plus /></el-icon>
        提交作品
      </el-button>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :inline="true">
        <el-form-item label="作品类型">
          <el-select v-model="filterParams.worksType" placeholder="全部类型" clearable @change="loadWorks">
            <el-option value="ESSAY" label="征文" />
            <el-option value="VIDEO" label="短视频" />
          </el-select>
        </el-form-item>
        <el-form-item label="只看优秀">
          <el-switch v-model="filterParams.excellent" @change="loadWorks" />
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 优秀作品展示 -->
    <div v-if="excellentWorks.length > 0 && !filterParams.worksType && !filterParams.excellent" class="excellent-section">
      <h3>
        <el-icon><Trophy /></el-icon>
        优秀作品
      </h3>
      <div class="works-grid">
        <div v-for="work in excellentWorks" :key="work.id" class="work-card excellent" @click="viewDetail(work.id)">
          <div class="work-cover">
            <img v-if="work.coverImage" :src="work.coverImage" alt="封面" />
            <div v-else class="default-cover">
              <el-icon v-if="work.worksType === 'ESSAY'" :size="48"><Document /></el-icon>
              <el-icon v-else :size="48"><VideoPlay /></el-icon>
            </div>
            <div class="rank-badge" v-if="work.rank">
              <el-icon><Medal /></el-icon>
              第{{ work.rank }}名
            </div>
          </div>
          <div class="work-info">
            <h4>{{ work.title }}</h4>
            <div class="work-meta">
              <span>{{ work.authorName }}</span>
              <span>{{ work.worksTypeName }}</span>
            </div>
            <div class="work-stats">
              <span><el-icon><View /></el-icon> {{ work.viewCount }}</span>
              <span><el-icon><Star /></el-icon> {{ work.likeCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 全部作品列表 -->
    <el-card class="works-list-card">
      <template #header>
        <span>全部作品</span>
      </template>

      <div class="works-grid">
        <div v-for="work in worksList" :key="work.id" class="work-card" @click="viewDetail(work.id)">
          <div class="work-cover">
            <img v-if="work.coverImage" :src="work.coverImage" alt="封面" />
            <div v-else class="default-cover">
              <el-icon v-if="work.worksType === 'ESSAY'" :size="48"><Document /></el-icon>
              <el-icon v-else :size="48"><VideoPlay /></el-icon>
            </div>
            <el-tag v-if="work.isExcellent" type="danger" size="small" class="excellent-tag">优秀</el-tag>
          </div>
          <div class="work-info">
            <h4>{{ work.title }}</h4>
            <div class="work-meta">
              <span>{{ work.authorName }}</span>
              <el-tag :type="work.worksType === 'ESSAY' ? 'success' : 'warning'" size="small">
                {{ work.worksTypeName }}
              </el-tag>
            </div>
            <div class="work-desc" v-if="work.description">{{ work.description }}</div>
            <div class="work-stats">
              <span><el-icon><View /></el-icon> {{ work.viewCount }}</span>
              <span><el-icon><Star /></el-icon> {{ work.likeCount }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="worksList.length === 0" description="暂无作品" />

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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Trophy, Document, VideoPlay, View, Star, Medal } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

interface Works {
  id: number
  title: string
  worksType: string
  worksTypeName: string
  authorName: string
  department: string
  coverImage: string
  description: string
  viewCount: number
  likeCount: number
  isExcellent: number
  rank: number
}

const filterParams = ref({
  worksType: '',
  excellent: false
})

const excellentWorks = ref<Works[]>([])
const worksList = ref<Works[]>([])
const page = ref(1)
const size = ref(12)
const total = ref(0)

// 加载优秀作品
const loadExcellentWorks = async () => {
  try {
    const res = await request.get('/works/list', {
      params: { page: 1, size: 4, excellent: true }
    })
    if (res.data.code === 200) {
      excellentWorks.value = res.data.data.records
    }
  } catch (error) {
    console.error('加载优秀作品失败', error)
  }
}

// 加载作品列表
const loadWorks = async () => {
  try {
    const params: any = { page: page.value, size: size.value }
    if (filterParams.value.worksType) {
      params.worksType = filterParams.value.worksType
    }
    if (filterParams.value.excellent) {
      params.excellent = true
    }

    const res = await request.get('/works/list', { params })
    if (res.data.code === 200) {
      worksList.value = res.data.data.records
      total.value = res.data.data.total
    }
  } catch (error) {
    ElMessage.error('加载作品列表失败')
  }
}

// 查看详情
const viewDetail = (id: number) => {
  router.push(`/works/${id}`)
}

onMounted(() => {
  loadExcellentWorks()
  loadWorks()
})
</script>

<style scoped>
.works-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
}

.filter-card {
  margin-bottom: 20px;
}

.excellent-section {
  margin-bottom: 24px;
}

.excellent-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  color: #e6a23c;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.work-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #ebeef5;
}

.work-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.work-card.excellent {
  border: 2px solid #e6a23c;
}

.work-cover {
  position: relative;
  height: 160px;
  background: #f5f7fa;
  overflow: hidden;
}

.work-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #c0c4cc;
}

.excellent-tag {
  position: absolute;
  top: 10px;
  right: 10px;
}

.rank-badge {
  position: absolute;
  bottom: 10px;
  left: 10px;
  background: linear-gradient(135deg, #e6a23c, #f56c6c);
  color: #fff;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.work-info {
  padding: 16px;
}

.work-info h4 {
  margin: 0 0 8px;
  font-size: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.work-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
  color: #909399;
}

.work-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.work-stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.work-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.works-list-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
