<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <div class="profile-header">
            <el-avatar :size="100">
              {{ userStore.userInfo.realName?.charAt(0) }}
            </el-avatar>
            <h2>{{ userStore.userInfo.realName }}</h2>
            <el-tag>{{ getRoleName(userStore.userInfo.roleId) }}</el-tag>
          </div>
          
          <el-descriptions :column="1" border class="profile-info">
            <el-descriptions-item label="用户名">
              {{ userStore.userInfo.username }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ userStore.userInfo.phone || '未设置' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ userStore.userInfo.email || '未设置' }}
            </el-descriptions-item>
            <el-descriptions-item label="积分">
              {{ userStore.userInfo.points }}
            </el-descriptions-item>
            <el-descriptions-item label="等级">
              Lv.{{ userStore.userInfo.level }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card>
          <el-tabs>
            <el-tab-pane label="修改资料">
              <el-form :model="profileForm" label-width="80px">
                <el-form-item label="真实姓名">
                  <el-input v-model="profileForm.realName" />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="profileForm.phone" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="profileForm.email" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            
            <el-tab-pane label="修改密码">
              <el-form :model="passwordForm" label-width="100px">
                <el-form-item label="原密码">
                  <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认密码">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            
            <el-tab-pane label="学习记录">
              <el-timeline>
                <el-timeline-item
                  v-for="record in studyRecords"
                  :key="record.id"
                  :timestamp="record.time"
                  placement="top"
                >
                  <el-card>
                    <h4>{{ record.title }}</h4>
                    <p>{{ record.content }}</p>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const profileForm = reactive({
  realName: userStore.userInfo.realName || '',
  phone: userStore.userInfo.phone || '',
  email: userStore.userInfo.email || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const studyRecords = ref([
  { id: 1, time: '2024-01-15 10:30', title: '完成知识学习', content: '学习了"冒充公检法诈骗案例分析"' },
  { id: 2, time: '2024-01-14 15:20', title: '测试通过', content: '完成了"反诈知识入门测试"，得分85分' },
  { id: 3, time: '2024-01-13 09:15', title: '演练完成', content: '完成了"冒充客服退款诈骗"演练，得分92分' }
])

const getRoleName = (roleId: number) => {
  const roles: Record<number, string> = {
    1: '学生',
    2: '教师',
    3: '管理员',
    4: '专家',
    5: '系统管理员'
  }
  return roles[roleId] || '未知'
}
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
  padding: 30px 0;
}

.profile-header h2 {
  margin: 16px 0 8px;
  color: #303133;
}

.profile-info {
  margin-top: 20px;
}
</style>
