<template>
  <div class="admin-scenes">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>演练场景管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 创建场景
          </el-button>
        </div>
      </template>
      
      <!-- 搜索和筛选 -->
      <div class="filter-section">
        <el-input
          v-model="searchKey"
          placeholder="搜索场景名称"
          style="width: 300px"
          prefix-icon="Search"
          @keyup.enter="loadScenes"
        />
        <el-select
          v-model="sceneTypeFilter"
          placeholder="筛选类型"
          style="width: 150px; margin-left: 10px"
          @change="loadScenes"
        >
          <el-option :value="0" label="全部" />
          <el-option :value="1" label="电信诈骗" />
          <el-option :value="2" label="网络诈骗" />
          <el-option :value="3" label="校园贷" />
          <el-option :value="4" label="兼职诈骗" />
          <el-option :value="5" label="其他" />
        </el-select>
        <el-select
          v-model="statusFilter"
          placeholder="筛选状态"
          style="width: 100px; margin-left: 10px"
          @change="loadScenes"
        >
          <el-option :value="-1" label="全部" />
          <el-option :value="1" label="启用" />
          <el-option :value="0" label="禁用" />
        </el-select>
      </div>
      
      <!-- 场景列表 -->
      <el-table :data="sceneList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="sceneName" label="场景名称" min-width="180" />
        <el-table-column prop="sceneType" label="类型" width="100">
          <template #default="{ row }">
            {{ ['电信诈骗', '网络诈骗', '校园贷', '兼职诈骗', '其他'][row.sceneType - 1] }}
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="getDifficultyType(row.difficulty)">
              {{ ['简单', '中等', '困难'][row.difficulty - 1] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分)" width="80" />
        <el-table-column prop="playCount" label="参与人数" width="100" />
        <el-table-column prop="avgScore" label="平均分" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="推荐" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.isRecommend" @change="toggleRecommend(row)" />
          </template>
        </el-table-column>
        <el-table-column label="标签" min-width="150">
          <template #default="{ row }">
            <el-tag v-for="tag in row.tags || []" :key="tag" size="small" style="margin-right: 5px">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button text type="warning" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button text type="danger" @click="deleteScene(row)">删除</el-button>
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
          @current-change="loadScenes"
        />
      </div>
    </el-card>
    
    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑场景' : '创建场景'" width="900px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="场景名称">
          <el-input v-model="form.sceneName" placeholder="请输入场景名称" />
        </el-form-item>
        <el-form-item label="场景类型">
          <el-select v-model="form.sceneType" placeholder="请选择">
            <el-option :value="1" label="电信诈骗" />
            <el-option :value="2" label="网络诈骗" />
            <el-option :value="3" label="校园贷" />
            <el-option :value="4" label="兼职诈骗" />
            <el-option :value="5" label="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级">
          <el-radio-group v-model="form.difficulty">
            <el-radio :value="1">简单</el-radio>
            <el-radio :value="2">中等</el-radio>
            <el-radio :value="3">困难</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="场景时长">
          <el-input-number v-model="form.duration" :min="5" :max="60" />
          <span style="margin-left: 10px">分钟</span>
        </el-form-item>
        <el-form-item label="场景描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入场景描述" />
        </el-form-item>
        <el-form-item label="背景设定">
          <el-input v-model="form.background" type="textarea" :rows="3" placeholder="请输入背景故事设定" />
        </el-form-item>
        <el-form-item label="场景标签">
          <el-tag
            v-for="(tag, index) in form.tags"
            :key="index"
            closable
            @close="form.tags.splice(index, 1)"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-model="newTag"
            placeholder="输入标签后按回车添加"
            @keyup.enter="addTag"
            style="width: 200px; margin-left: 10px"
          />
        </el-form-item>
        <el-form-item label="演练流程">
          <div class="script-editor">
            <el-button type="primary" size="small" @click="openScriptEditor" style="margin-bottom: 10px">
              打开脚本编辑器
            </el-button>
            <el-input v-model="form.script" type="textarea" :rows="6" placeholder="请输入演练流程脚本（JSON格式）" />
          </div>
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="请输入封面图URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 脚本编辑器对话框 -->
    <el-dialog v-model="scriptEditorVisible" title="脚本编辑器" width="800px">
      <div class="script-editor-content">
        <el-button type="primary" size="small" @click="addDialogStep">
          添加对话步骤
        </el-button>
        <div v-for="(step, index) in scriptSteps" :key="index" class="script-step">
          <el-collapse>
            <el-collapse-item :title="`步骤 ${index + 1}`">
              <el-form :model="step" label-width="100px">
                <el-form-item label="角色">
                  <el-select v-model="step.role" placeholder="请选择">
                    <el-option value="system" label="系统" />
                    <el-option value="user" label="用户" />
                  </el-select>
                </el-form-item>
                <el-form-item label="内容">
                  <el-input v-model="step.content" type="textarea" :rows="2" placeholder="请输入对话内容" />
                </el-form-item>
                <el-form-item label="选项" v-if="step.role === 'system'">
                  <div v-for="(option, oIndex) in step.options" :key="oIndex" class="option-item">
                    <el-input :value="option" @input="updateOption(step, oIndex, $event)" placeholder="请输入选项内容" style="width: 400px" />
                    <el-button type="danger" size="small" @click="step.options.splice(oIndex, 1)">
                      删除
                    </el-button>
                  </div>
                  <el-button type="primary" size="small" @click="step.options.push('')">
                    添加选项
                  </el-button>
                </el-form-item>
                <el-form-item label="正确选项" v-if="step.role === 'system'">
                  <el-input-number v-model="step.correctIndex" :min="0" :max="step.options.length - 1" />
                </el-form-item>
                <el-form-item label="分支ID" v-if="step.role === 'system'">
                  <el-input v-model="step.branchId" placeholder="输入分支ID，留空表示无分支" />
                </el-form-item>
                <el-form-item label="时间限制" v-if="step.role === 'system'">
                  <el-input-number v-model="step.timeLimit" :min="0" placeholder="输入时间限制（秒），0表示无限制" />
                  <span style="margin-left: 10px">秒</span>
                </el-form-item>
                <el-button type="danger" @click="scriptSteps.splice(index, 1)">
                  删除步骤
                </el-button>
              </el-form>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
      <template #footer>
        <el-button @click="scriptEditorVisible = false">取消</el-button>
        <el-button type="primary" @click="saveScript">保存脚本</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post, put, del } from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

// 场景列表相关
const sceneList = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchKey = ref('')
const sceneTypeFilter = ref(0)
const statusFilter = ref(-1)

// 对话框相关
const dialogVisible = ref(false)
const scriptEditorVisible = ref(false)
const editId = ref<number | null>(null)

// 表单数据
const form = reactive({
  sceneName: '',
  sceneType: 1,
  difficulty: 2,
  duration: 15,
  description: '',
  background: '',
  script: '',
  coverImage: '',
  tags: [] as string[]
})

// 标签相关
const newTag = ref('')

// 脚本编辑器相关
const scriptSteps = ref<any[]>([])

const getDifficultyType = (difficulty: number) => {
  const types: Record<number, string> = { 1: 'success', 2: 'warning', 3: 'danger' }
  return types[difficulty] || 'info'
}

const loadScenes = async () => {
  try {
    const res = await get('/admin/simulation/scenes', {
      page: page.value, 
      size: size.value,
      sceneType: sceneTypeFilter.value > 0 ? sceneTypeFilter.value : undefined,
      status: statusFilter.value >= 0 ? statusFilter.value : undefined
    })
    sceneList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    sceneList.value = [
      { id: 1, sceneName: '冒充公检法诈骗演练', sceneType: 1, difficulty: 2, duration: 15, playCount: 256, avgScore: 85, status: 1, isRecommend: true, tags: ['电信诈骗', '公检法'] },
      { id: 2, sceneName: '网络兼职刷单诈骗', sceneType: 2, difficulty: 1, duration: 10, playCount: 180, avgScore: 78, status: 1, isRecommend: true, tags: ['网络诈骗', '兼职'] },
      { id: 3, sceneName: '校园贷陷阱识别', sceneType: 3, difficulty: 3, duration: 20, playCount: 120, avgScore: 72, status: 1, isRecommend: false, tags: ['校园贷', '金融诈骗'] }
    ]
    total.value = 3
  }
}

const openDialog = (row?: any) => {
  editId.value = row?.id || null
  if (row) {
    Object.assign(form, {
      sceneName: row.sceneName,
      sceneType: row.sceneType,
      difficulty: row.difficulty,
      duration: row.duration,
      description: row.description || '',
      background: row.background || '',
      script: row.script || '',
      coverImage: row.coverImage || '',
      tags: row.tags || []
    })
  } else {
    Object.assign(form, {
      sceneName: '',
      sceneType: 1,
      difficulty: 2,
      duration: 15,
      description: '',
      background: '',
      script: '',
      coverImage: '',
      tags: []
    })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.sceneName) {
    ElMessage.warning('请填写场景名称')
    return
  }
  
  try {
    if (editId.value) {
      await put(`/admin/simulation/scenes/${editId.value}`, form)
      ElMessage.success('更新成功')
    } else {
      await post('/admin/simulation/scenes', form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadScenes()
  } catch (e) {
    ElMessage.error('操作失败，请重试')
  }
}

const toggleRecommend = async (row: any) => {
  try {
    await put(`/admin/simulation/scenes/${row.id}/recommend`, null, {
      params: { isRecommend: row.isRecommend }
    })
    ElMessage.success('设置成功')
  } catch (e) {
    ElMessage.error('设置失败，请重试')
  }
}

const toggleStatus = async (row: any) => {
  try {
    await put(`/admin/simulation/scenes/${row.id}/status`, null, {
      params: { status: row.status === 1 ? 0 : 1 }
    })
    row.status = row.status === 1 ? 0 : 1
    ElMessage.success('操作成功')
  } catch (e) {
    ElMessage.error('操作失败，请重试')
  }
}

const deleteScene = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除该场景吗？', '提示', { type: 'warning' })
    await del(`/admin/simulation/scenes/${row.id}`)
    ElMessage.success('删除成功')
    loadScenes()
  } catch (e) {
    ElMessage.error('删除失败，请重试')
  }
}

const addTag = () => {
  if (newTag.value && !form.tags.includes(newTag.value)) {
    form.tags.push(newTag.value)
    newTag.value = ''
  }
}

const openScriptEditor = () => {
  try {
    if (form.script) {
      scriptSteps.value = JSON.parse(form.script)
    } else {
      scriptSteps.value = []
    }
  } catch (e) {
    scriptSteps.value = []
  }
  scriptEditorVisible.value = true
}

const addDialogStep = () => {
  scriptSteps.value.push({
    role: 'system',
    content: '',
    options: [''],
    correctIndex: 0,
    branchId: '',
    timeLimit: 0
  })
}

const updateOption = (step: any, index: number, value: string) => {
  step.options[index] = value
}

const saveScript = () => {
  form.script = JSON.stringify(scriptSteps.value, null, 2)
  scriptEditorVisible.value = false
  ElMessage.success('脚本保存成功')
}

onMounted(() => {
  loadScenes()
})
</script>

<style scoped>
.admin-scenes {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.script-editor {
  position: relative;
}

.script-step {
  margin-top: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 10px;
  background: #f9f9f9;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.option-item .el-input {
  margin-right: 10px;
}

.script-editor-content {
  max-height: 500px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .filter-section {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .filter-section .el-input,
  .filter-section .el-select {
    margin-left: 0 !important;
    margin-top: 10px;
    width: 100% !important;
  }
}

@media (max-width: 768px) {
  .admin-scenes {
    padding: 0 16px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .card-header .el-button {
    margin-top: 10px;
  }
  
  .el-dialog {
    width: 95% !important;
  }
}
</style>
