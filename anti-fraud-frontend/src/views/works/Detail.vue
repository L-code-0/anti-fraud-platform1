<template>
  <div class="works-detail-container">
    <el-skeleton v-if="loading" animated>
      <template #template>
        <el-skeleton-item variant="h1" style="width: 50%" />
        <el-skeleton-item variant="text" style="margin-top: 16px" />
      </template>
    </el-skeleton>

    <template v-else-if="works">
      <!-- 返回按钮 -->
      <div class="back-nav">
        <el-button text @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>

      <!-- 作品主体 -->
      <el-card class="main-card">
        <!-- 标题区域 -->
        <div class="header-section">
          <div class="title-row">
            <h1>{{ works.title }}</h1>
            <div class="title-tags">
              <el-tag :type="works.worksType === 'ESSAY' ? 'success' : 'warning'">
                {{ works.worksTypeName }}
              </el-tag>
              <el-tag v-if="works.isExcellent" type="danger">
                <el-icon><Trophy /></el-icon>
                优秀作品
              </el-tag>
              <el-tag v-if="works.rank" type="warning">
                第{{ works.rank }}名
              </el-tag>
            </div>
          </div>

          <div class="meta-info">
            <span class="author">
              <el-icon><User /></el-icon>
              {{ works.authorName }}
            </span>
            <span class="department">
              <el-icon><School /></el-icon>
              {{ works.department || '未填写院系' }}
            </span>
            <span class="time">
              <el-icon><Clock /></el-icon>
              {{ formatDate(works.createTime) }}
            </span>
          </div>

          <div class="activity-link" v-if="works.activityName">
            <el-icon><Link /></el-icon>
            参与活动：{{ works.activityName }}
          </div>
        </div>

        <el-divider />

        <!-- 征文内容 -->
        <div v-if="works.worksType === 'ESSAY'" class="content-section">
          <div class="essay-content">{{ works.content }}</div>
        </div>

        <!-- 短视频内容 -->
        <div v-else class="video-section">
          <div class="video-player" v-if="works.fileUrl">
            <video :src="works.fileUrl" controls></video>
          </div>
          <el-empty v-else description="暂无视频" />
        </div>

        <!-- 作品描述 -->
        <div v-if="works.description" class="description-section">
          <h3>作品简介</h3>
          <p>{{ works.description }}</p>
        </div>

        <el-divider />

        <!-- 互动区域 -->
        <div class="interaction-section">
          <div class="stats">
            <span><el-icon><View /></el-icon> {{ works.viewCount }} 次浏览</span>
            <span><el-icon><Star /></el-icon> {{ works.likeCount }} 次点赞</span>
          </div>
          <el-button type="primary" :icon="Star" @click="handleLike" :loading="liking">
            点赞
          </el-button>
        </div>

        <!-- 审核信息（非公开） -->
        <template v-if="works.status !== 1">
          <el-divider />
          <el-alert
            :title="works.status === 0 ? '作品审核中' : '作品未通过审核'"
            :type="works.status === 0 ? 'info' : 'error'"
            :description="works.auditRemark"
            show-icon
          />
        </template>
      </el-card>

      <!-- 相关推荐 -->
      <el-card class="recommend-card" v-if="recommendWorks.length > 0">
        <template #header>
          <span>相关推荐</span>
        </template>
        <div class="recommend-list">
          <div
            v-for="item in recommendWorks"
            :key="item.id"
            class="recommend-item"
            @click="viewOther(item.id)"
          >
            <div class="recommend-cover">
              <el-icon v-if="item.worksType === 'ESSAY'" :size="24"><Document /></el-icon>
              <el-icon v-else :size="24"><VideoPlay /></el-icon>
            </div>
            <div class="recommend-info">
              <h4>{{ item.title }}</h4>
              <span>{{ item.authorName }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </template>

    <el-empty v-else description="作品不存在或已下架" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, User, School, Clock, Link, View, Star, Trophy, Document, VideoPlay } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

interface WorksDetail {
  id: number
  title: string
  worksType: string
  worksTypeName: string
  authorName: string
  department: string
  activityName: string
  content: string
  fileUrl: string
  coverImage: string
  description: string
  status: number
  auditRemark: string
  viewCount: number
  likeCount: number
  isExcellent: number
  rank: number
  createTime: string
}

const loading = ref(true)
const liking = ref(false)
const works = ref<WorksDetail | null>(null)
const recommendWorks = ref<any[]>([])

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 加载作品详情
const loadWorksDetail = async () => {
  const id = route.params.id
  try {
    const res = await request.get(`/works/${id}`)
    if (res.data.code === 200) {
      works.value = res.data.data
    } else {
      ElMessage.error(res.data.message || '加载失败')
    }
  } catch (error) {
    ElMessage.error('加载作品详情失败')
  } finally {
    loading.value = false
  }
}

// 点赞
const handleLike = async () => {
  liking.value = true
  try {
    const res = await request.post(`/works/${works.value?.id}/like`)
    if (res.data.code === 200) {
      ElMessage.success('点赞成功')
      if (works.value) {
        works.value.likeCount++
      }
    }
  } catch (error) {
    ElMessage.error('点赞失败')
  } finally {
    liking.value = false
  }
}

// 加载推荐作品
const loadRecommend = async () => {
  try {
    const res = await request.get('/works/list', {
      params: { page: 1, size: 4, worksType: works.value?.worksType }
    })
    if (res.data.code === 200) {
      recommendWorks.value = res.data.data.records.filter(
        (item: any) => item.id !== works.value?.id
      ).slice(0, 3)
    }
  } catch (error) {
    console.error('加载推荐失败', error)
  }
}

// 查看其他作品
const viewOther = (id: number) => {
  router.push(`/works/${id}`)
}

onMounted(async () => {
  await loadWorksDetail()
  if (works.value) {
    loadRecommend()
  }
})
</script>

<style scoped>
.works-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.back-nav {
  margin-bottom: 16px;
}

.main-card {
  margin-bottom: 20px;
}

.header-section {
  text-align: center;
}

.title-row {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.title-row h1 {
  margin: 0;
  font-size: 28px;
}

.title-tags {
  display: flex;
  gap: 8px;
}

.title-tags .el-tag {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-info {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 16px;
  color: #909399;
  font-size: 14px;
}

.meta-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.activity-link {
  margin-top: 12px;
  color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.content-section {
  padding: 20px 0;
}

.essay-content {
  font-size: 16px;
  line-height: 2;
  text-indent: 2em;
  white-space: pre-wrap;
}

.video-section {
  padding: 20px 0;
}

.video-player {
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-player video {
  width: 100%;
  display: block;
}

.description-section {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  margin-top: 20px;
}

.description-section h3 {
  margin: 0 0 8px;
  font-size: 14px;
  color: #606266;
}

.description-section p {
  margin: 0;
  color: #909399;
}

.interaction-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats {
  display: flex;
  gap: 24px;
  color: #909399;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.recommend-card {
  margin-bottom: 20px;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommend-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.recommend-item:hover {
  background: #f5f7fa;
}

.recommend-cover {
  width: 60px;
  height: 60px;
  background: #ecf5ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409eff;
}

.recommend-info {
  flex: 1;
}

.recommend-info h4 {
  margin: 0 0 4px;
  font-size: 14px;
}

.recommend-info span {
  font-size: 12px;
  color: #909399;
}
</style>
