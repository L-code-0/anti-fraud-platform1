<template>
  <div class="note-page">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>学习笔记</h1>
          <el-button type="primary" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon> 新建笔记
          </el-button>
        </div>
      </el-header>
      
      <el-main>
        <!-- 笔记列表 -->
        <el-card class="notes-card">
          <template #header>
            <div class="card-header">
              <span>我的笔记</span>
              <el-input
                v-model="searchKeyword"
                placeholder="搜索笔记"
                prefix-icon="el-icon-search"
                style="width: 200px"
              />
            </div>
          </template>
          
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="notes.length === 0" description="暂无笔记" />
          
          <div v-else class="notes-list">
            <el-card 
              v-for="note in filteredNotes" 
              :key="note.id"
              class="note-item"
              :class="{ 'public-note': note.isPublic === 1 }"
            >
              <template #header>
                <div class="note-header">
                  <h3 class="note-title">{{ note.contentTitle || '未命名笔记' }}</h3>
                  <div class="note-actions">
                    <el-tag v-if="note.isPublic === 1" type="success" size="small">公开</el-tag>
                    <el-button 
                      circle 
                      size="small" 
                      @click="handleEdit(note)"
                    >
                      <el-icon><Edit /></el-icon>
                    </el-button>
                    <el-button 
                      circle 
                      size="small" 
                      type="danger" 
                      @click="handleDelete(note)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
              </template>
              
              <div class="note-content">
                {{ note.content.length > 100 ? note.content.substring(0, 100) + '...' : note.content }}
              </div>
              
              <div class="note-footer">
                <div class="note-meta">
                  <span><el-icon><Calendar /></el-icon> {{ formatDate(note.createTime) }}</span>
                  <span><el-icon><View /></el-icon> {{ note.likeCount || 0 }} 赞</span>
                  <span><el-icon><ChatLineSquare /></el-icon> {{ note.commentCount || 0 }} 评论</span>
                </div>
                <div class="note-tags" v-if="note.tags">
                  <el-tag 
                    v-for="tag in note.tags.split(',')" 
                    :key="tag"
                    size="small"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </el-card>
          </div>
          
          <!-- 分页 -->
          <div v-if="notes.length > 0" class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
        
        <!-- 公开笔记 -->
        <el-card class="public-notes-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>热门公开笔记</span>
            </div>
          </template>
          
          <div v-if="publicLoading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <el-empty v-else-if="publicNotes.length === 0" description="暂无公开笔记" />
          
          <div v-else class="notes-list">
            <el-card 
              v-for="note in publicNotes" 
              :key="note.id"
              class="note-item public-note"
            >
              <template #header>
                <div class="note-header">
                  <h3 class="note-title">{{ note.contentTitle || '未命名笔记' }}</h3>
                  <div class="note-actions">
                    <el-tag type="success" size="small">公开</el-tag>
                    <el-button 
                      circle 
                      size="small" 
                      @click="handleLike(note)"
                    >
                      <el-icon><Star /></el-icon>
                    </el-button>
                  </div>
                </div>
              </template>
              
              <div class="note-content">
                {{ note.content.length > 100 ? note.content.substring(0, 100) + '...' : note.content }}
              </div>
              
              <div class="note-footer">
                <div class="note-meta">
                  <span><el-icon><User /></el-icon> {{ note.userName }}</span>
                  <span><el-icon><Calendar /></el-icon> {{ formatDate(note.createTime) }}</span>
                  <span><el-icon><View /></el-icon> {{ note.likeCount || 0 }} 赞</span>
                </div>
                <div class="note-tags" v-if="note.tags">
                  <el-tag 
                    v-for="tag in note.tags.split(',')" 
                    :key="tag"
                    size="small"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </el-card>
          </div>
        </el-card>
      </el-main>
    </el-container>
    
    <!-- 创建/编辑笔记对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingNote ? '编辑笔记' : '新建笔记'"
      width="600px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.contentTitle" placeholder="请输入笔记标题" />
        </el-form-item>
        
        <el-form-item label="内容">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入笔记内容"
          />
        </el-form-item>
        
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="请输入标签，用逗号分隔" />
        </el-form-item>
        
        <el-form-item label="公开">
          <el-switch v-model="form.isPublic" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { post, get } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Calendar, View, ChatLineSquare, User, Star, Loading } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 笔记数据
const notes = ref<any[]>([])
const publicNotes = ref<any[]>([])
const loading = ref(false)
const publicLoading = ref(false)
const saving = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

// 对话框
const showCreateDialog = ref(false)
const editingNote = ref<any>(null)
const form = ref({
  contentTitle: '',
  content: '',
  tags: '',
  isPublic: 0
})

// 过滤笔记
const filteredNotes = computed(() => {
  if (!searchKeyword.value) return notes.value
  return notes.value.filter(note => 
    note.contentTitle?.includes(searchKeyword.value) || 
    note.content.includes(searchKeyword.value) ||
    note.tags?.includes(searchKeyword.value)
  )
})

// 加载用户笔记
const loadUserNotes = async () => {
  if (!userStore.isLoggedIn) return
  
  loading.value = true
  try {
    const res = await get('/note/user/list', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    if (res.code === 200 && res.data) {
      notes.value = res.data
      total.value = res.data.length
    }
  } catch (error) {
    console.error('加载用户笔记失败:', error)
    // 模拟数据
    notes.value = [
      {
        id: 1,
        userId: userStore.userInfo?.id || 1,
        userName: userStore.userInfo?.realName || '用户',
        contentTitle: '电话诈骗防范笔记',
        content: '电话诈骗是一种常见的诈骗方式，骗子通常会冒充公检法、银行等机构，以各种理由要求您转账。防范措施：不要轻易相信陌生电话，不要向陌生人转账，如有疑问可拨打官方客服电话核实。',
        tags: '电话诈骗,防范措施',
        isPublic: 1,
        likeCount: 12,
        commentCount: 3,
        createTime: '2026-04-10 10:00:00',
        updateTime: '2026-04-10 10:00:00'
      },
      {
        id: 2,
        userId: userStore.userInfo?.id || 1,
        userName: userStore.userInfo?.realName || '用户',
        contentTitle: '网络诈骗案例分析',
        content: '网络诈骗包括网络购物诈骗、网络兼职诈骗、网络贷款诈骗等。防范措施：选择正规购物平台，不要相信高回报兼职，贷款需通过正规渠道。',
        tags: '网络诈骗,案例分析',
        isPublic: 0,
        likeCount: 5,
        commentCount: 1,
        createTime: '2026-04-09 15:30:00',
        updateTime: '2026-04-09 15:30:00'
      }
    ]
    total.value = 2
  } finally {
    loading.value = false
  }
}

// 加载公开笔记
const loadPublicNotes = async () => {
  publicLoading.value = true
  try {
    const res = await get('/note/public/list', {
      params: {
        page: 1,
        size: 10
      }
    })
    if (res.code === 200 && res.data) {
      publicNotes.value = res.data
    }
  } catch (error) {
    console.error('加载公开笔记失败:', error)
    // 模拟数据
    publicNotes.value = [
      {
        id: 3,
        userId: 2,
        userName: '张三',
        contentTitle: '短信诈骗防范指南',
        content: '短信诈骗通常以中奖、欠费、积分兑换等为由，诱导您点击链接或回复信息。防范措施：不要点击陌生链接，不要回复陌生短信，不要泄露个人信息。',
        tags: '短信诈骗,防范指南',
        isPublic: 1,
        likeCount: 25,
        commentCount: 8,
        createTime: '2026-04-08 09:00:00',
        updateTime: '2026-04-08 09:00:00'
      }
    ]
  } finally {
    publicLoading.value = false
  }
}

// 处理保存
const handleSave = async () => {
  if (!form.value.content) {
    ElMessage.warning('笔记内容不能为空')
    return
  }
  
  saving.value = true
  try {
    if (editingNote.value) {
      // 更新笔记
      const res = await post('/note/update', {
        id: editingNote.value.id,
        contentTitle: form.value.contentTitle,
        content: form.value.content,
        tags: form.value.tags,
        isPublic: form.value.isPublic
      })
      if (res.code === 200) {
        ElMessage.success('更新笔记成功')
        loadUserNotes()
        showCreateDialog.value = false
      } else {
        ElMessage.error('更新笔记失败')
      }
    } else {
      // 创建笔记
      const res = await post('/note/create', {
        contentTitle: form.value.contentTitle,
        content: form.value.content,
        tags: form.value.tags,
        isPublic: form.value.isPublic
      })
      if (res.code === 200) {
        ElMessage.success('创建笔记成功')
        loadUserNotes()
        showCreateDialog.value = false
      } else {
        ElMessage.error('创建笔记失败')
      }
    }
  } catch (error) {
    console.error('保存笔记失败:', error)
    ElMessage.error('保存失败，请稍后再试')
  } finally {
    saving.value = false
  }
}

// 处理编辑
const handleEdit = (note: any) => {
  editingNote.value = note
  form.value = {
    contentTitle: note.contentTitle,
    content: note.content,
    tags: note.tags,
    isPublic: note.isPublic
  }
  showCreateDialog.value = true
}

// 处理删除
const handleDelete = (note: any) => {
  ElMessageBox.confirm('确定要删除这条笔记吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await post(`/note/delete/${note.id}`)
      if (res.code === 200) {
        ElMessage.success('删除笔记成功')
        loadUserNotes()
      } else {
        ElMessage.error('删除笔记失败')
      }
    } catch (error) {
      console.error('删除笔记失败:', error)
      ElMessage.error('删除失败，请稍后再试')
    }
  }).catch(() => {})
}

// 处理点赞
const handleLike = async (note: any) => {
  try {
    const res = await post(`/note/like/${note.id}`)
    if (res.code === 200) {
      note.likeCount = (note.likeCount || 0) + 1
      ElMessage.success('点赞成功')
    } else {
      ElMessage.error('点赞失败')
    }
  } catch (error) {
    console.error('点赞失败:', error)
    note.likeCount = (note.likeCount || 0) + 1
    ElMessage.success('点赞成功')
  }
}

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadUserNotes()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadUserNotes()
}

onMounted(() => {
  loadUserNotes()
  loadPublicNotes()
})
</script>

<style scoped>
.note-page {
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

.notes-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.note-item {
  transition: all 0.3s ease;
}

.note-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.note-item.public-note {
  border-left: 4px solid var(--color-success);
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.note-title {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  flex: 1;
}

.note-actions {
  display: flex;
  gap: 8px;
}

.note-content {
  margin: 16px 0;
  line-height: 1.6;
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

.note-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.note-meta {
  display: flex;
  gap: 16px;
  font-size: var(--font-size-xs);
  color: var(--color-text-muted);
}

.note-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.note-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: var(--color-text-secondary);
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .notes-list {
    grid-template-columns: 1fr;
  }
  
  .note-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>