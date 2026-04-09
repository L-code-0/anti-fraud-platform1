<template>
  <div class="works-submit-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>提交作品</span>
          <el-button text @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        label-position="right"
      >
        <!-- 作品标题 -->
        <el-form-item label="作品标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入作品标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <!-- 作品类型 -->
        <el-form-item label="作品类型" prop="worksType">
          <el-radio-group v-model="form.worksType" @change="handleTypeChange">
            <el-radio value="ESSAY">
              <el-icon><Document /></el-icon>
              征文
            </el-radio>
            <el-radio value="VIDEO">
              <el-icon><VideoPlay /></el-icon>
              短视频
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 关联活动 -->
        <el-form-item label="参与活动">
          <el-select
            v-model="form.activityId"
            placeholder="选择参与的活动（可选）"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="activity in activities"
              :key="activity.id"
              :label="activity.title"
              :value="activity.id"
            />
          </el-select>
        </el-form-item>

        <!-- 征文内容 -->
        <el-form-item
          v-if="form.worksType === 'ESSAY'"
          label="征文内容"
          prop="content"
        >
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="15"
            placeholder="请输入征文内容..."
            maxlength="10000"
            show-word-limit
          />
        </el-form-item>

        <!-- 视频上传 -->
        <el-form-item
          v-if="form.worksType === 'VIDEO'"
          label="视频文件"
          prop="fileUrl"
        >
          <el-input
            v-model="form.fileUrl"
            placeholder="请输入视频链接地址"
          >
            <template #append>
              <el-button @click="showUploadTip = true">上传说明</el-button>
            </template>
          </el-input>
          <div class="upload-tip">
            支持 MP4、MOV 格式，建议时长 1-5 分钟，请先上传到视频平台获取链接
          </div>
        </el-form-item>

        <!-- 封面图 -->
        <el-form-item label="封面图片">
          <el-input
            v-model="form.coverImage"
            placeholder="请输入封面图片链接（可选）"
          />
        </el-form-item>

        <!-- 作品描述 -->
        <el-form-item label="作品简介">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请简要描述作品内容..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <!-- 提交须知 -->
        <el-form-item>
          <el-alert
            title="提交须知"
            type="info"
            :closable="false"
          >
            <ul class="notice-list">
              <li>作品内容需原创，不得抄袭或侵权</li>
              <li>作品内容需与反诈主题相关</li>
              <li>提交后需等待管理员审核通过后才会公开展示</li>
              <li>优秀作品将获得积分奖励</li>
            </ul>
          </el-alert>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            提交作品
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 上传说明弹窗 -->
    <el-dialog v-model="showUploadTip" title="视频上传说明" width="500px">
      <div class="upload-dialog">
        <p>由于平台限制，暂不支持直接上传视频文件。请按以下步骤操作：</p>
        <ol>
          <li>将视频上传至视频平台（如B站、腾讯视频等）</li>
          <li>获取视频分享链接或嵌入代码</li>
          <li>将链接粘贴到"视频文件"输入框中</li>
        </ol>
        <el-alert type="warning" :closable="false" style="margin-top: 16px">
          建议视频时长控制在 1-5 分钟，格式为 MP4 或 MOV
        </el-alert>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { ArrowLeft, Document, VideoPlay } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const showUploadTip = ref(false)
const activities = ref<any[]>([])

const form = reactive({
  title: '',
  worksType: 'ESSAY',
  activityId: null as number | null,
  content: '',
  fileUrl: '',
  coverImage: '',
  description: ''
})

const rules = reactive<FormRules>({
  title: [
    { required: true, message: '请输入作品标题', trigger: 'blur' },
    { min: 5, max: 200, message: '标题长度在5-200个字符之间', trigger: 'blur' }
  ],
  worksType: [
    { required: true, message: '请选择作品类型', trigger: 'change' }
  ],
  content: [
    {
      required: true,
      validator: (rule, value, callback) => {
        if (form.worksType === 'ESSAY' && !value) {
          callback(new Error('请输入征文内容'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  fileUrl: [
    {
      required: true,
      validator: (rule, value, callback) => {
        if (form.worksType === 'VIDEO' && !value) {
          callback(new Error('请输入视频链接'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 切换作品类型
const handleTypeChange = () => {
  form.content = ''
  form.fileUrl = ''
}

// 加载进行中的活动
const loadActivities = async () => {
  try {
    const res = await request.get('/activity/list', {
      params: { page: 1, size: 20, status: 1 }
    })
    if (res.data.code === 200) {
      activities.value = res.data.data.records || []
    }
  } catch (error) {
    console.error('加载活动列表失败', error)
  }
}

// 提交作品
const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (!valid) return

  submitting.value = true
  try {
    const res = await request.post('/works', form)
    if (res.data.code === 200) {
      ElMessage.success('提交成功，请等待审核')
      router.push('/works')
    } else {
      ElMessage.error(res.data.message || '提交失败')
    }
  } catch (error) {
    ElMessage.error('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = () => {
  formRef.value?.resetFields()
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.works-submit-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.notice-list {
  margin: 8px 0 0;
  padding-left: 20px;
}

.notice-list li {
  margin: 4px 0;
}

.upload-dialog ol {
  padding-left: 20px;
}

.upload-dialog ol li {
  margin: 8px 0;
}
</style>
