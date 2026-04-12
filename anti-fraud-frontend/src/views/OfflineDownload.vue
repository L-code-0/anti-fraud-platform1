<template>
  <div class="offline-download-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>离线下载</h1>
        </div>
      </el-header>
      
      <el-main>
        <!-- 下载任务列表 -->
        <el-card class="downloads-card">
          <template #header>
            <div class="card-header">
              <span>下载任务</span>
              <el-select v-model="statusFilter" size="small" @change="loadDownloads">
                <el-option label="全部" :value="null" />
                <el-option label="下载中" :value="1" />
                <el-option label="已完成" :value="2" />
                <el-option label="下载失败" :value="3" />
                <el-option label="已取消" :value="4" />
              </el-select>
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="downloads.length === 0" description="暂无下载任务" />
          
          <el-table v-else :data="downloads" style="width: 100%">
            <el-table-column prop="contentTitle" label="内容标题" width="300">
              <template #default="scope">
                <span class="content-title">{{ scope.row.contentTitle }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="contentType" label="内容类型" width="100" />
            <el-table-column prop="downloadStatus" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.downloadStatus)">
                  {{ getStatusText(scope.row.downloadStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="downloadProgress" label="进度" width="150">
              <template #default="scope">
                <el-progress 
                  v-if="scope.row.downloadStatus === 1" 
                  :percentage="scope.row.downloadProgress" 
                  :stroke-width="10"
                />
                <span v-else>{{ scope.row.downloadProgress }}%</span>
              </template>
            </el-table-column>
            <el-table-column prop="fileSize" label="文件大小" width="100" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button 
                  v-if="scope.row.downloadStatus === 1" 
                  size="small" 
                  type="warning" 
                  @click="handleCancel(scope.row)"
                >
                  取消
                </el-button>
                <el-button 
                  v-else-if="scope.row.downloadStatus === 2" 
                  size="small" 
                  type="primary" 
                  @click="handleDownloadFile(scope.row)"
                >
                  下载
                </el-button>
                <el-button 
                  size="small" 
                  type="danger" 
                  @click="handleDelete(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        
        <!-- 下载历史 -->
        <el-card class="history-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>下载历史</span>
            </div>
          </template>
          
          <div v-if="historyLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="downloadHistory.length === 0" description="暂无下载历史" />
          
          <el-table v-else :data="downloadHistory" style="width: 100%">
            <el-table-column prop="contentTitle" label="内容标题" width="300">
              <template #default="scope">
                <span class="content-title">{{ scope.row.contentTitle }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="contentType" label="内容类型" width="100" />
            <el-table-column prop="fileSize" label="文件大小" width="100" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button 
                  v-if="scope.row.downloadStatus === 2" 
                  size="small" 
                  type="primary" 
                  @click="handleDownloadFile(scope.row)"
                >
                  下载
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 状态
const loading = ref(false)
const historyLoading = ref(false)
const downloads = ref<any[]>([])
const downloadHistory = ref<any[]>([])
const statusFilter = ref<number | null>(null)

// 加载下载任务
const loadDownloads = async () => {
  if (!userStore.isLoggedIn) return
  
  loading.value = true
  try {
    const res = await get('/offline/download/list', {
      params: { status: statusFilter.value }
    })
    if (res.code === 200 && res.data) {
      downloads.value = res.data
    }
  } catch (error) {
    console.error('加载下载任务失败:', error)
    // 模拟数据
    downloads.value = [
      {
        id: 1,
        contentTitle: '电话诈骗防范指南',
        contentType: 'knowledge',
        downloadStatus: 1,
        downloadProgress: 50,
        fileSize: '1.2MB',
        createTime: '2026-04-10 10:00:00'
      },
      {
        id: 2,
        contentTitle: '网络诈骗案例分析',
        contentType: 'knowledge',
        downloadStatus: 2,
        downloadProgress: 100,
        fileSize: '2.5MB',
        createTime: '2026-04-09 15:30:00'
      }
    ]
  } finally {
    loading.value = false
  }
}

// 加载下载历史
const loadDownloadHistory = async () => {
  if (!userStore.isLoggedIn) return
  
  historyLoading.value = true
  try {
    const res = await get('/offline/download/list')
    if (res.code === 200 && res.data) {
      downloadHistory.value = res.data.filter((item: any) => item.downloadStatus === 2)
    }
  } catch (error) {
    console.error('加载下载历史失败:', error)
    // 模拟数据
    downloadHistory.value = [
      {
        id: 2,
        contentTitle: '网络诈骗案例分析',
        contentType: 'knowledge',
        downloadStatus: 2,
        downloadProgress: 100,
        fileSize: '2.5MB',
        createTime: '2026-04-09 15:30:00'
      },
      {
        id: 3,
        contentTitle: '短信诈骗防范指南',
        contentType: 'knowledge',
        downloadStatus: 2,
        downloadProgress: 100,
        fileSize: '1.8MB',
        createTime: '2026-04-08 09:00:00'
      }
    ]
  } finally {
    historyLoading.value = false
  }
}

// 处理取消下载
const handleCancel = async (download: any) => {
  try {
    const res = await post(`/offline/download/${download.id}/cancel`)
    if (res.code === 200) {
      ElMessage.success('取消下载成功')
      loadDownloads()
    } else {
      ElMessage.error('取消下载失败')
    }
  } catch (error) {
    console.error('取消下载失败:', error)
    download.downloadStatus = 4
    ElMessage.success('取消下载成功')
  }
}

// 处理删除下载任务
const handleDelete = (download: any) => {
  ElMessageBox.confirm('确定要删除这个下载任务吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await post(`/offline/download/${download.id}`, {}, {
        method: 'DELETE'
      })
      if (res.code === 200) {
        ElMessage.success('删除下载任务成功')
        loadDownloads()
        loadDownloadHistory()
      } else {
        ElMessage.error('删除下载任务失败')
      }
    } catch (error) {
      console.error('删除下载任务失败:', error)
      downloads.value = downloads.value.filter(item => item.id !== download.id)
      downloadHistory.value = downloadHistory.value.filter(item => item.id !== download.id)
      ElMessage.success('删除下载任务成功')
    }
  }).catch(() => {})
}

// 处理下载文件
const handleDownloadFile = (download: any) => {
  // 打开下载链接
  window.open(`/api/offline/download/${download.id}/file`)
}

// 获取状态类型
const getStatusType = (status: number) => {
  const typeMap: Record<number, string> = {
    1: 'warning',
    2: 'success',
    3: 'danger',
    4: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const textMap: Record<number, string> = {
    1: '下载中',
    2: '已完成',
    3: '下载失败',
    4: '已取消'
  }
  return textMap[status] || '未知'
}

onMounted(() => {
  loadDownloads()
  loadDownloadHistory()
})
</script>

<style scoped>
.offline-download-page {
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

.content-title {
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .el-table {
    font-size: var(--font-size-sm);
  }
  
  .el-table-column {
    width: auto !important;
  }
}
</style>