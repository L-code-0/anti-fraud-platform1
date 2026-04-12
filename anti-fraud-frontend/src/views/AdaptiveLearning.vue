<template>
  <div class="adaptive-learning">
    <!-- 头部 -->
    <div class="header">
      <h1>自适应学习路径</h1>
      <p>国家反诈中心 - 个性化推荐+实时调整</p>
    </div>

    <!-- 用户画像 -->
    <div class="user-profile">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>用户画像</span>
            <el-button type="primary" @click="updateProfile">更新画像</el-button>
          </div>
        </template>
        <div class="profile-content">
          <div class="profile-item">
            <span class="label">风险等级：</span>
            <el-tag :type="getRiskLevelType(userProfile.riskLevel)">{{ userProfile.riskLevel }}</el-tag>
          </div>
          <div class="profile-item">
            <span class="label">学习阶段：</span>
            <el-tag type="info">{{ userProfile.learningStage }}</el-tag>
          </div>
          <div class="profile-item">
            <span class="label">兴趣领域：</span>
            <div class="interest-tags">
              <el-tag v-for="interest in userProfile.interests" :key="interest" size="small" effect="plain">
                {{ interest }}
              </el-tag>
            </div>
          </div>
          <div class="profile-item">
            <span class="label">学习偏好：</span>
            <el-tag size="small" effect="plain">{{ userProfile.learningPreference }}</el-tag>
          </div>
          <div class="profile-item">
            <span class="label">完成课程：</span>
            <span>{{ userProfile.completedCourses }}/{{ userProfile.totalCourses }}</span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 学习路径 -->
    <div class="learning-path">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>学习路径</span>
            <el-button type="info" @click="refreshPath">刷新路径</el-button>
          </div>
        </template>
        <div class="path-content">
          <div class="path-timeline">
            <div 
              v-for="(stage, index) in learningPath" 
              :key="stage.id"
              :class="['path-stage', { 'active': stage.status === 'current', 'completed': stage.status === 'completed' }]"
            >
              <div class="stage-number">{{ index + 1 }}</div>
              <div class="stage-content">
                <h3>{{ stage.title }}</h3>
                <p>{{ stage.description }}</p>
                <div class="stage-courses">
                  <el-button 
                    v-for="course in stage.courses" 
                    :key="course.id"
                    :type="getCourseType(course.status)"
                    :plain="course.status !== 'completed'"
                    @click="viewCourse(course)"
                  >
                    {{ course.title }}
                  </el-button>
                </div>
                <div class="stage-progress" v-if="stage.status === 'current'">
                  <el-progress :percentage="stage.progress" :format="formatProgress" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 个性化推荐 -->
    <div class="recommendations">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>个性化推荐</span>
            <el-button type="info" @click="refreshRecommendations">刷新推荐</el-button>
          </div>
        </template>
        <div class="recommend-content">
          <div class="recommend-item" v-for="(item, index) in recommendations" :key="item.id">
            <div class="recommend-rank">{{ index + 1 }}</div>
            <div class="recommend-info">
              <h4>{{ item.title }}</h4>
              <p>{{ item.description }}</p>
              <div class="recommend-meta">
                <span class="meta-item">{{ item.type }}</span>
                <span class="meta-item">{{ item.duration }}分钟</span>
                <span class="meta-item">匹配度: {{ item.matchRate }}%</span>
              </div>
            </div>
            <el-button type="primary" @click="viewCourse(item)">学习</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 学习统计 -->
    <div class="learning-stats">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>学习统计</span>
          </div>
        </template>
        <div class="stats-content">
          <div class="stats-item">
            <div class="stats-value">{{ stats.totalLearningTime }}</div>
            <div class="stats-label">总学习时长(分钟)</div>
          </div>
          <div class="stats-item">
            <div class="stats-value">{{ stats.completedCourses }}</div>
            <div class="stats-label">已完成课程</div>
          </div>
          <div class="stats-item">
            <div class="stats-value">{{ stats.assessmentScore }}</div>
            <div class="stats-label">平均评估分数</div>
          </div>
          <div class="stats-item">
            <div class="stats-value">{{ stats.streakDays }}</div>
            <div class="stats-label">连续学习天数</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 课程详情对话框 -->
    <el-dialog v-model="courseVisible" :title="currentCourse?.title" width="800px">
      <div class="course-detail" v-if="currentCourse">
        <div class="course-header">
          <h3>{{ currentCourse.title }}</h3>
          <div class="course-meta">
            <span class="meta-item">{{ currentCourse.type }}</span>
            <span class="meta-item">{{ currentCourse.duration }}分钟</span>
            <span class="meta-item" v-if="currentCourse.difficulty">{{ currentCourse.difficulty }}</span>
          </div>
        </div>
        <div class="course-content">
          <p>{{ currentCourse.description }}</p>
          <div class="course-materials">
            <h4>课程材料</h4>
            <ul>
              <li v-for="(material, index) in currentCourse.materials" :key="index">
                {{ material }}
              </li>
            </ul>
          </div>
        </div>
        <div class="course-assessment" v-if="currentCourse.assessment">
          <h4>课程评估</h4>
          <el-form :model="assessmentForm">
            <el-form-item label="您对本课程的评价">
              <el-rate v-model="assessmentForm.rating" />
            </el-form-item>
            <el-form-item label="学习收获">
              <el-input type="textarea" v-model="assessmentForm.feedback" placeholder="请输入您的学习收获" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitAssessment">提交评估</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="courseVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const userProfile = ref({
  riskLevel: '低风险',
  learningStage: '初级',
  interests: ['电信诈骗', '网络诈骗', '校园贷'],
  learningPreference: '视频学习',
  completedCourses: 5,
  totalCourses: 20
})

const learningPath = ref([
  {
    id: 1,
    title: '基础认知',
    description: '了解常见诈骗类型和基本防范措施',
    status: 'completed',
    progress: 100,
    courses: [
      { id: 101, title: '诈骗类型介绍', status: 'completed' },
      { id: 102, title: '防范基本常识', status: 'completed' },
      { id: 103, title: '案例分析入门', status: 'completed' }
    ]
  },
  {
    id: 2,
    title: '进阶学习',
    description: '深入学习各类诈骗的识别和防范技巧',
    status: 'current',
    progress: 60,
    courses: [
      { id: 201, title: '电信诈骗防范', status: 'completed' },
      { id: 202, title: '网络诈骗防范', status: 'completed' },
      { id: 203, title: '校园贷诈骗防范', status: 'in-progress' },
      { id: 204, title: '兼职诈骗防范', status: 'pending' }
    ]
  },
  {
    id: 3,
    title: '高级应用',
    description: '掌握高级防范技巧和实战应对能力',
    status: 'pending',
    progress: 0,
    courses: [
      { id: 301, title: '实战演练', status: 'pending' },
      { id: 302, title: '风险评估', status: 'pending' },
      { id: 303, title: '防诈宣传', status: 'pending' }
    ]
  }
])

const recommendations = ref([
  {
    id: 401,
    title: '新型网络诈骗识别技巧',
    description: '了解最新的网络诈骗手法和识别方法',
    type: '视频课程',
    duration: 30,
    matchRate: 95
  },
  {
    id: 402,
    title: '电信诈骗案例分析',
    description: '通过真实案例学习如何识别电信诈骗',
    type: '图文教程',
    duration: 20,
    matchRate: 90
  },
  {
    id: 403,
    title: '防诈意识测试',
    description: '测试您的防诈意识水平',
    type: '互动测试',
    duration: 15,
    matchRate: 85
  }
])

const stats = ref({
  totalLearningTime: 120,
  completedCourses: 5,
  assessmentScore: 85,
  streakDays: 7
})

const courseVisible = ref(false)
const currentCourse = ref<any>(null)
const assessmentForm = reactive({
  rating: 0,
  feedback: ''
})

function getRiskLevelType(level: string): string {
  const typeMap: Record<string, string> = {
    '低风险': 'success',
    '中风险': 'warning',
    '高风险': 'danger'
  }
  return typeMap[level] || 'info'
}

function getCourseType(status: string): string {
  const typeMap: Record<string, string> = {
    'completed': 'success',
    'in-progress': 'warning',
    'pending': 'info'
  }
  return typeMap[status] || 'info'
}

function formatProgress(percentage: number): string {
  return `${percentage}%`
}

function updateProfile() {
  // 模拟更新用户画像
  ElMessage.success('用户画像更新成功')
}

function refreshPath() {
  // 模拟刷新学习路径
  ElMessage.success('学习路径刷新成功')
}

function refreshRecommendations() {
  // 模拟刷新推荐
  ElMessage.success('推荐内容刷新成功')
}

function viewCourse(course: any) {
  currentCourse.value = {
    ...course,
    materials: [
      '课程讲义',
      '案例分析',
      '防范指南'
    ],
    assessment: true
  }
  courseVisible.value = true
}

function submitAssessment() {
  if (assessmentForm.rating === 0) {
    ElMessage.warning('请为课程评分')
    return
  }
  
  // 模拟提交评估
  ElMessage.success('评估提交成功')
  courseVisible.value = false
  // 更新学习进度
  updateLearningProgress()
}

function updateLearningProgress() {
  // 模拟更新学习进度
  const currentStage = learningPath.value.find(stage => stage.status === 'current')
  if (currentStage) {
    currentStage.progress = Math.min(100, currentStage.progress + 20)
    if (currentStage.progress === 100) {
      currentStage.status = 'completed'
      const nextStage = learningPath.value[learningPath.value.indexOf(currentStage) + 1]
      if (nextStage) {
        nextStage.status = 'current'
      }
    }
  }
}

onMounted(() => {
  // 初始化学习路径
  console.log('自适应学习路径初始化')
})
</script>

<style scoped>
.adaptive-learning {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 32px;
}

.header h1 {
  font-size: 32px;
  margin-bottom: 8px;
  color: #333;
}

.header p {
  font-size: 16px;
  color: #666;
}

.user-profile,
.learning-path,
.recommendations,
.learning-stats {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-content {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 16px;
}

.profile-item {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1 1 45%;
  min-width: 200px;
}

.label {
  font-weight: 500;
  color: #606266;
}

.interest-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.path-timeline {
  position: relative;
  padding-left: 40px;
}

.path-timeline::before {
  content: '';
  position: absolute;
  left: 19px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e4e7ed;
}

.path-stage {
  position: relative;
  margin-bottom: 32px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.path-stage::before {
  content: '';
  position: absolute;
  left: -40px;
  top: 24px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #e4e7ed;
  border: 2px solid #fff;
  box-shadow: 0 0 0 2px #e4e7ed;
  transition: all 0.3s ease;
}

.path-stage.active {
  background: #ecf5ff;
  border: 1px solid #d9ecff;
}

.path-stage.active::before {
  background: #409eff;
  box-shadow: 0 0 0 2px #d9ecff;
}

.path-stage.completed {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
}

.path-stage.completed::before {
  background: #67c23a;
  box-shadow: 0 0 0 2px #e1f3d8;
}

.stage-number {
  position: absolute;
  left: -37px;
  top: 20px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  z-index: 1;
  transition: all 0.3s ease;
}

.path-stage.active .stage-number {
  color: #409eff;
}

.path-stage.completed .stage-number {
  color: #67c23a;
}

.stage-content h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.stage-content p {
  margin: 0 0 16px 0;
  color: #666;
  font-size: 14px;
}

.stage-courses {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.stage-progress {
  margin-top: 16px;
}

.recommend-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.recommend-item:hover {
  background: #ecf5ff;
}

.recommend-rank {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 16px;
  flex-shrink: 0;
}

.recommend-info {
  flex: 1;
}

.recommend-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.recommend-info p {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
}

.recommend-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.stats-content {
  display: flex;
  gap: 24px;
  justify-content: space-around;
}

.stats-item {
  text-align: center;
  flex: 1;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stats-label {
  font-size: 14px;
  color: #606266;
}

.course-detail {
  padding: 20px 0;
}

.course-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.course-header h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #333;
}

.course-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #606266;
}

.course-content {
  margin-bottom: 24px;
}

.course-content p {
  line-height: 1.5;
  color: #303133;
}

.course-materials {
  margin-top: 24px;
}

.course-materials h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
}

.course-materials ul {
  margin: 0;
  padding-left: 20px;
}

.course-materials li {
  margin-bottom: 8px;
  color: #606266;
}

.course-assessment {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.course-assessment h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .adaptive-learning {
    padding: 16px;
  }
  
  .profile-item {
    flex: 1 1 100%;
  }
  
  .stage-courses {
    flex-direction: column;
  }
  
  .recommend-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .recommend-rank {
    margin-bottom: 12px;
  }
  
  .stats-content {
    flex-direction: column;
    gap: 16px;
  }
  
  .stats-item {
    flex: 1 1 100%;
  }
}
</style>