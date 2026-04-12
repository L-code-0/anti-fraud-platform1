<template>
  <div class="vr-training">
    <!-- 头部 -->
    <div class="header">
      <h1>VR演练体验</h1>
      <p>物理基地 - 随时随地+无限复用</p>
    </div>

    <!-- VR场景选择 -->
    <div class="scene-selection" v-if="!currentScene">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>选择VR场景</span>
            <el-button type="primary" @click="startQuickTraining">快速开始</el-button>
          </div>
        </template>
        <div class="scene-list">
          <el-card 
            v-for="scene in vrScenes" 
            :key="scene.id"
            shadow="hover"
            class="scene-card"
            @click="selectScene(scene)"
          >
            <template #header>
              <div class="scene-card-header">
                <h3>{{ scene.title }}</h3>
                <el-tag :type="getSceneType(scene.type)">{{ scene.type }}</el-tag>
              </div>
            </template>
            <div class="scene-card-content">
              <div class="scene-image">
                <img :src="scene.image" :alt="scene.title" />
              </div>
              <p class="scene-description">{{ scene.description }}</p>
              <div class="scene-meta">
                <span class="meta-item">{{ scene.duration }}分钟</span>
                <span class="meta-item">{{ scene.difficulty }}</span>
                <span class="meta-item">{{ scene.popularity }}次体验</span>
              </div>
            </div>
          </el-card>
        </div>
      </el-card>
    </div>

    <!-- VR演练界面 -->
    <div class="vr-experience" v-if="currentScene">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>{{ currentScene.title }}</span>
            <el-button type="info" @click="restartScene">重新开始</el-button>
            <el-button type="warning" @click="exitScene">退出场景</el-button>
          </div>
        </template>
        <div class="experience-content">
          <!-- VR场景展示 -->
          <div class="vr-display">
            <div class="vr-container">
              <img :src="currentScene.image" :alt="currentScene.title" class="vr-image" />
              <div class="vr-overlay">
                <div class="vr-instructions">
                  <h3>场景说明</h3>
                  <p>{{ currentScene.instructions }}</p>
                </div>
                <div class="vr-controls">
                  <el-button type="primary" @click="startExperience">开始体验</el-button>
                  <el-button @click="viewDemo">查看演示</el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 场景信息 -->
          <div class="scene-info">
            <div class="info-section">
              <h3>场景详情</h3>
              <p>{{ currentScene.description }}</p>
            </div>
            <div class="info-section">
              <h3>学习目标</h3>
              <ul>
                <li v-for="(goal, index) in currentScene.learningGoals" :key="index">
                  {{ goal }}
                </li>
              </ul>
            </div>
            <div class="info-section">
              <h3>操作指南</h3>
              <ul>
                <li v-for="(guide, index) in currentScene.operationGuide" :key="index">
                  {{ guide }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 体验记录 -->
    <div class="experience-records" v-if="!currentScene">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>体验记录</span>
          </div>
        </template>
        <div class="records-list">
          <el-table :data="experienceRecords" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="sceneTitle" label="场景名称" />
            <el-table-column prop="duration" label="体验时长" width="120" />
            <el-table-column prop="score" label="得分" width="80">
              <template #default="{ row }">
                <el-tag :type="getScoreType(row.score)">{{ row.score }}分</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="completedAt" label="完成时间" width="180" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button text type="primary" @click="replayExperience(row)">
                  再次体验
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
    </div>

    <!-- 体验中对话框 -->
    <el-dialog v-model="experienceVisible" title="VR体验中" width="800px" :close-on-click-modal="false">
      <div class="experience-modal">
        <div class="experience-progress">
          <el-progress :percentage="experienceProgress" :format="formatProgress" />
        </div>
        <div class="experience-content">
          <h3>{{ currentScene?.title }}</h3>
          <p>{{ currentScene?.instructions }}</p>
          <div class="experience-steps">
            <div 
              v-for="(step, index) in currentScene?.steps" 
              :key="index"
              :class="['step-item', { 'active': currentStep === index, 'completed': currentStep > index }]"
            >
              <div class="step-number">{{ index + 1 }}</div>
              <div class="step-content">
                <h4>{{ step.title }}</h4>
                <p>{{ step.description }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="experience-actions">
          <el-button @click="pauseExperience" v-if="!isPaused">暂停</el-button>
          <el-button @click="resumeExperience" v-else>继续</el-button>
          <el-button type="danger" @click="exitExperience">退出体验</el-button>
          <el-button type="primary" @click="nextStep" v-if="currentStep < (currentScene?.steps.length || 0) - 1">下一步</el-button>
          <el-button type="success" @click="completeExperience" v-else>完成体验</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 演示视频对话框 -->
    <el-dialog v-model="demoVisible" title="场景演示" width="800px">
      <div class="demo-container">
        <div class="demo-video">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20anti-fraud%20training%20demo%20video%20screenshot&image_size=landscape_16_9" alt="Demo Video" />
          <div class="video-overlay">
            <el-button type="primary" size="large" @click="demoVisible = false">
              播放演示
            </el-button>
          </div>
        </div>
        <div class="demo-info">
          <h3>演示说明</h3>
          <p>本演示视频展示了VR反诈演练的操作流程和关键环节，帮助您了解如何在VR环境中识别和应对诈骗场景。</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const vrScenes = ref([
  {
    id: 1,
    title: '电信诈骗VR演练',
    type: '电信诈骗',
    description: '在VR环境中体验接到自称公检法电话的情景，学习如何识别和应对电信诈骗。',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20telecom%20fraud%20training%20scene&image_size=landscape_16_9',
    duration: 15,
    difficulty: '中等',
    popularity: 1250,
    instructions: '在这个VR场景中，你将接到一个自称是公安局的电话，对方说你涉及一起洗钱案件，需要配合调查。你需要根据所学知识，做出正确的判断和应对措施。',
    learningGoals: [
      '识别电信诈骗的特征',
      '掌握应对电信诈骗的方法',
      '提高防诈意识和反应能力'
    ],
    operationGuide: [
      '使用手柄选择对话选项',
      '通过点头或摇头进行确认',
      '使用扳机键进行操作'
    ],
    steps: [
      {
        title: '接到陌生电话',
        description: '你正在家中休息，突然接到一个陌生电话，对方自称是公安局的民警。'
      },
      {
        title: '对方说明情况',
        description: '对方说你涉及一起洗钱案件，需要配合调查。'
      },
      {
        title: '对方要求提供信息',
        description: '对方要求你提供个人信息和银行卡信息。'
      },
      {
        title: '做出决策',
        description: '你需要根据所学知识，做出正确的决策。'
      },
      {
        title: '总结反馈',
        description: '系统会根据你的表现，提供反馈和建议。'
      }
    ]
  },
  {
    id: 2,
    title: '网络诈骗VR演练',
    type: '网络诈骗',
    description: '在VR环境中体验遇到网络兼职诈骗的情景，学习如何识别和应对网络诈骗。',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20online%20fraud%20training%20scene&image_size=landscape_16_9',
    duration: 12,
    difficulty: '简单',
    popularity: 980,
    instructions: '在这个VR场景中，你将在网上看到一个刷单兼职的广告，声称只要完成任务就能获得高额报酬。你需要根据所学知识，做出正确的判断和应对措施。',
    learningGoals: [
      '识别网络诈骗的特征',
      '掌握应对网络诈骗的方法',
      '提高防诈意识和反应能力'
    ],
    operationGuide: [
      '使用手柄选择操作选项',
      '通过手势进行确认',
      '使用触摸板进行导航'
    ],
    steps: [
      {
        title: '看到兼职广告',
        description: '你在网上看到一个刷单兼职的广告，声称只要完成任务就能获得高额报酬。'
      },
      {
        title: '了解任务要求',
        description: '你查看了任务要求，对方要求你先垫付资金。'
      },
      {
        title: '做出决策',
        description: '你需要根据所学知识，做出正确的决策。'
      },
      {
        title: '总结反馈',
        description: '系统会根据你的表现，提供反馈和建议。'
      }
    ]
  },
  {
    id: 3,
    title: '校园贷诈骗VR演练',
    type: '金融诈骗',
    description: '在VR环境中体验遇到校园贷诈骗的情景，学习如何识别和应对校园贷诈骗。',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20campus%20loan%20fraud%20training%20scene&image_size=landscape_16_9',
    duration: 10,
    difficulty: '简单',
    popularity: 850,
    instructions: '在这个VR场景中，你急需用钱，看到校园里的贷款广告，声称无抵押、低利息。你需要根据所学知识，做出正确的判断和应对措施。',
    learningGoals: [
      '识别校园贷诈骗的特征',
      '掌握应对校园贷诈骗的方法',
      '提高防诈意识和反应能力'
    ],
    operationGuide: [
      '使用手柄选择操作选项',
      '通过语音进行交互',
      '使用扳机键进行操作'
    ],
    steps: [
      {
        title: '看到贷款广告',
        description: '你急需用钱，看到校园里的贷款广告，声称无抵押、低利息。'
      },
      {
        title: '了解贷款条件',
        description: '你了解了贷款条件，对方要求你提供个人信息和身份证照片。'
      },
      {
        title: '做出决策',
        description: '你需要根据所学知识，做出正确的决策。'
      },
      {
        title: '总结反馈',
        description: '系统会根据你的表现，提供反馈和建议。'
      }
    ]
  }
])

const currentScene = ref<any>(null)
const experienceVisible = ref(false)
const demoVisible = ref(false)
const experienceProgress = ref(0)
const currentStep = ref(0)
const isPaused = ref(false)

const experienceRecords = ref<any[]>([
  {
    id: 1,
    sceneTitle: '电信诈骗VR演练',
    duration: '15分钟',
    score: 90,
    completedAt: '2026-04-10 14:30'
  },
  {
    id: 2,
    sceneTitle: '网络诈骗VR演练',
    duration: '12分钟',
    score: 85,
    completedAt: '2026-04-09 10:15'
  },
  {
    id: 3,
    sceneTitle: '校园贷诈骗VR演练',
    duration: '10分钟',
    score: 95,
    completedAt: '2026-04-08 16:45'
  }
])

function getSceneType(type: string): string {
  const typeMap: Record<string, string> = {
    '电信诈骗': 'danger',
    '网络诈骗': 'warning',
    '金融诈骗': 'primary'
  }
  return typeMap[type] || 'info'
}

function getScoreType(score: number): string {
  if (score >= 90) return 'success'
  if (score >= 70) return 'warning'
  return 'danger'
}

function formatProgress(percentage: number): string {
  return `${percentage}%`
}

function selectScene(scene: any) {
  currentScene.value = scene
}

function startQuickTraining() {
  // 随机选择一个场景
  const randomIndex = Math.floor(Math.random() * vrScenes.value.length)
  currentScene.value = vrScenes.value[randomIndex]
}

function startExperience() {
  experienceVisible.value = true
  experienceProgress.value = 0
  currentStep.value = 0
  isPaused.value = false
  // 模拟体验进度
  const interval = setInterval(() => {
    if (!isPaused.value && currentStep.value < (currentScene.value?.steps.length || 0)) {
      experienceProgress.value += 1
      if (experienceProgress.value >= 100) {
        clearInterval(interval)
      }
    }
  }, 500)
}

function viewDemo() {
  demoVisible.value = true
}

function nextStep() {
  if (currentStep.value < (currentScene.value?.steps.length || 0) - 1) {
    currentStep.value++
    experienceProgress.value = Math.min(100, Math.floor((currentStep.value + 1) / (currentScene.value?.steps.length || 1) * 100))
  }
}

function pauseExperience() {
  isPaused.value = true
}

function resumeExperience() {
  isPaused.value = false
}

function exitExperience() {
  experienceVisible.value = false
  ElMessage.warning('体验已退出')
}

function completeExperience() {
  experienceVisible.value = false
  // 模拟评分
  const score = Math.floor(Math.random() * 20) + 80
  // 添加体验记录
  experienceRecords.value.unshift({
    id: experienceRecords.value.length + 1,
    sceneTitle: currentScene.value.title,
    duration: `${currentScene.value.duration}分钟`,
    score: score,
    completedAt: new Date().toLocaleString()
  })
  ElMessage.success(`体验完成！得分：${score}分`)
}

function replayExperience(record: any) {
  // 找到对应的场景
  const scene = vrScenes.value.find(s => s.title === record.sceneTitle)
  if (scene) {
    currentScene.value = scene
    startExperience()
  }
}

function restartScene() {
  startExperience()
}

function exitScene() {
  currentScene.value = null
}

onMounted(() => {
  // 初始化VR场景数据
  console.log('VR演练体验初始化')
})
</script>

<style scoped>
.vr-training {
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

.scene-selection,
.vr-experience,
.experience-records {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.scene-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-top: 24px;
}

.scene-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.scene-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.scene-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.scene-card-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.scene-image {
  margin: 16px 0;
  border-radius: 8px;
  overflow: hidden;
}

.scene-image img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.scene-description {
  margin: 0 0 16px 0;
  color: #666;
  line-height: 1.5;
  font-size: 14px;
}

.scene-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.experience-content {
  display: flex;
  gap: 24px;
  padding: 20px 0;
}

.vr-display {
  flex: 1;
  max-width: 600px;
}

.vr-container {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}

.vr-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.vr-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  color: white;
  text-align: center;
}

.vr-instructions {
  margin-bottom: 32px;
}

.vr-instructions h3 {
  margin: 0 0 16px 0;
  font-size: 20px;
}

.vr-instructions p {
  margin: 0;
  line-height: 1.5;
}

.vr-controls {
  display: flex;
  gap: 12px;
}

.scene-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-section h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.info-section p {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.info-section ul {
  margin: 0;
  padding-left: 20px;
}

.info-section li {
  margin-bottom: 8px;
  color: #666;
  line-height: 1.5;
}

.records-list {
  margin-top: 24px;
}

.experience-modal {
  padding: 20px 0;
}

.experience-progress {
  margin-bottom: 24px;
}

.experience-content h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #333;
}

.experience-content p {
  margin: 0 0 24px 0;
  color: #666;
  line-height: 1.5;
}

.experience-steps {
  margin-bottom: 32px;
}

.step-item {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.step-item.active {
  background: #ecf5ff;
  border: 1px solid #d9ecff;
}

.step-item.completed {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
}

.step-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.step-item.active .step-number {
  background: #409eff;
  color: white;
}

.step-item.completed .step-number {
  background: #67c23a;
  color: white;
}

.step-content h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.step-content p {
  margin: 0;
  color: #666;
  line-height: 1.5;
  font-size: 14px;
}

.experience-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 32px;
}

.demo-container {
  padding: 20px 0;
}

.demo-video {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 24px;
}

.demo-video img {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
}

.demo-info h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #333;
}

.demo-info p {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .vr-training {
    padding: 16px;
  }
  
  .scene-list {
    grid-template-columns: 1fr;
  }
  
  .experience-content {
    flex-direction: column;
  }
  
  .vr-display {
    max-width: 100%;
  }
  
  .vr-overlay {
    padding: 20px;
  }
  
  .vr-controls {
    flex-direction: column;
    width: 100%;
  }
  
  .experience-actions {
    flex-direction: column;
  }
  
  .step-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .step-number {
    align-self: flex-start;
  }
}
</style>