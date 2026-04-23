<template>
  <div class="vr-training">
    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>VR演练体验</h1>
          <div class="differentiator">
            <span class="tag">随时随地</span>
            <span class="tag">无限复用</span>
            <span class="competitor">对比：物理基地</span>
          </div>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-value">3</span>
            <span class="stat-label">精品场景</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">2,080</span>
            <span class="stat-label">累计体验</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">92%</span>
            <span class="stat-label">通过率</span>
          </div>
        </div>
      </div>
    </div>

    <div class="vr-content">
      <div class="main-area">
        <div class="scene-selection" v-if="!currentScene">
          <div class="quick-start">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-start-content">
                <div class="quick-info">
                  <h3>快速开始</h3>
                  <p>根据您的学习历史和表现，智能推荐最适合您的VR场景</p>
                  <div class="quick-tags">
                    <el-tag type="info" size="small">电信诈骗</el-tag>
                    <el-tag type="info" size="small">网络诈骗</el-tag>
                    <el-tag type="info" size="small">金融诈骗</el-tag>
                  </div>
                </div>
                <el-button type="primary" size="large" @click="startQuickTraining">
                  <el-icon><VideoPlay /></el-icon>
                  开始智能推荐
                </el-button>
              </div>
            </el-card>
          </div>

          <div class="scene-grid">
            <el-card
              v-for="scene in vrScenes"
              :key="scene.id"
              shadow="hover"
              class="scene-card"
              :class="{ featured: scene.featured }"
              @click="selectScene(scene)"
            >
              <template #header>
                <div class="scene-card-header">
                  <div class="scene-title-row">
                    <h3>{{ scene.title }}</h3>
                    <el-tag v-if="scene.featured" type="warning" size="small">热门</el-tag>
                  </div>
                  <el-tag :type="getSceneType(scene.type)" size="small">{{ scene.type }}</el-tag>
                </div>
              </template>
              <div class="scene-card-content">
                <div class="scene-image">
                  <img :src="scene.image" :alt="scene.title" />
                  <div class="scene-duration">
                    <el-icon><Clock /></el-icon>
                    {{ scene.duration }}分钟
                  </div>
                </div>
                <p class="scene-description">{{ scene.description }}</p>
                <div class="scene-stats">
                  <div class="stat">
                    <el-icon><User /></el-icon>
                    <span>{{ scene.popularity }}次体验</span>
                  </div>
                  <div class="stat">
                    <el-icon><DataLine /></el-icon>
                    <span>{{ scene.passRate }}%通过率</span>
                  </div>
                </div>
                <div class="scene-difficulty">
                  <span class="difficulty-label">难度</span>
                  <el-rate v-model="scene.difficultyLevel" disabled text-color="#ff9900" />
                </div>
              </div>
              <div class="scene-card-footer">
                <el-button type="primary" @click.stop="selectScene(scene)">
                  开始体验
                </el-button>
                <el-button @click.stop="viewScenePreview(scene)">
                  <el-icon><View /></el-icon>
                  预览
                </el-button>
              </div>
            </el-card>
          </div>
        </div>

        <div class="vr-experience" v-if="currentScene">
          <el-card shadow="hover" class="experience-main-card">
            <template #header>
              <div class="card-header">
                <div class="scene-info-header">
                  <h2>{{ currentScene.title }}</h2>
                  <el-tag :type="getSceneType(currentScene.type)">{{ currentScene.type }}</el-tag>
                </div>
                <div class="header-actions">
                  <el-button type="info" plain @click="restartScene">
                    <el-icon><RefreshRight /></el-icon>
                    重新开始
                  </el-button>
                  <el-button @click="exitScene">
                    <el-icon><Close /></el-icon>
                    退出场景
                  </el-button>
                </div>
              </div>
            </template>

            <div class="experience-workspace">
              <div class="vr-display">
                <div class="vr-container">
                  <img :src="currentScene.image" :alt="currentScene.title" class="vr-image" />
                  <div class="vr-overlay" v-if="!experienceStarted">
                    <div class="overlay-content">
                      <h3>{{ currentScene.title }}</h3>
                      <p>{{ currentScene.instructions }}</p>
                      <div class="overlay-actions">
                        <el-button type="primary" size="large" @click="startExperience">
                          <el-icon><VideoPlay /></el-icon>
                          开始体验
                        </el-button>
                        <el-button size="large" @click="viewDemo">
                          <el-icon><VideoCamera /></el-icon>
                          查看演示
                        </el-button>
                      </div>
                    </div>
                  </div>
                  <div class="decision-prompt" v-if="showDecision && !experienceCompleted">
                    <div class="decision-content">
                      <h4>⚠️ 决策点</h4>
                      <p>{{ currentDecision?.description }}</p>
                      <div class="decision-options">
                        <el-button
                          v-for="option in currentDecision?.options"
                          :key="option.id"
                          :type="option.correct ? 'success' : 'danger'"
                          @click="makeDecision(option)"
                        >
                          {{ option.text }}
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="experience-sidebar">
                <el-card shadow="hover" class="info-card">
                  <template #header>
                    <span>学习目标</span>
                  </template>
                  <ul class="goal-list">
                    <li v-for="(goal, index) in currentScene.learningGoals" :key="index">
                      <el-icon><Check /></el-icon>
                      {{ goal }}
                    </li>
                  </ul>
                </el-card>

                <el-card shadow="hover" class="info-card">
                  <template #header>
                    <span>操作指南</span>
                  </template>
                  <ul class="guide-list">
                    <li v-for="(guide, index) in currentScene.operationGuide" :key="index">
                      <el-icon><ArrowRight /></el-icon>
                      {{ guide }}
                    </li>
                  </ul>
                </el-card>

                <el-card shadow="hover" class="progress-card">
                  <template #header>
                    <span>体验进度</span>
                  </template>
                  <div class="progress-info">
                    <el-progress
                      :percentage="experienceProgress"
                      :color="progressColor"
                      :format="formatProgress"
                    />
                    <div class="step-indicator">
                      步骤 {{ currentStepIndex + 1 }} / {{ currentScene.steps.length }}
                    </div>
                  </div>
                </el-card>
              </div>
            </div>
          </el-card>
        </div>

        <div class="experience-records" v-if="!currentScene">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>我的体验记录</span>
                <el-select v-model="recordFilter" size="small" style="width: 120px">
                  <el-option label="全部" value="all" />
                  <el-option label="本周" value="week" />
                  <el-option label="本月" value="month" />
                </el-select>
              </div>
            </template>
            <div class="records-list">
              <el-table :data="filteredRecords" stripe>
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="sceneTitle" label="场景名称">
                  <template #default="{ row }">
                    <span class="scene-name">{{ row.sceneTitle }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="duration" label="体验时长" width="120" />
                <el-table-column prop="score" label="得分" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getScoreType(row.score)" effect="dark">
                      {{ row.score }}分
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="correctRate" label="正确率" width="100">
                  <template #default="{ row }">
                    <span class="correct-rate">{{ row.correctRate }}%</span>
                  </template>
                </el-table-column>
                <el-table-column prop="completedAt" label="完成时间" width="180" />
                <el-table-column label="操作" width="180">
                  <template #default="{ row }">
                    <el-button text type="primary" @click="replayExperience(row)">
                      再次体验
                    </el-button>
                    <el-button text type="info" @click="viewRecordDetail(row)">
                      详情
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
        </div>
      </div>

      <div class="side-area" v-if="!currentScene">
        <el-card shadow="hover" class="user-stats-card">
          <template #header>
            <div class="card-header">
              <span>我的VR训练</span>
              <el-tag type="success" size="small">连续{{ userStats.continueDays }}天</el-tag>
            </div>
          </template>
          <div class="user-stats">
            <div class="stats-grid">
              <div class="stat-box">
                <div class="stat-icon">🏆</div>
                <div class="stat-content">
                  <div class="stat-value">{{ userStats.totalTimes }}</div>
                  <div class="stat-label">累计体验</div>
                </div>
              </div>
              <div class="stat-box">
                <div class="stat-icon">⭐</div>
                <div class="stat-content">
                  <div class="stat-value">{{ userStats.avgScore }}</div>
                  <div class="stat-label">平均得分</div>
                </div>
              </div>
              <div class="stat-box">
                <div class="stat-icon">📜</div>
                <div class="stat-content">
                  <div class="stat-value">{{ userStats.certifications }}</div>
                  <div class="stat-label">获得证书</div>
                </div>
              </div>
              <div class="stat-box">
                <div class="stat-icon">⚡</div>
                <div class="stat-content">
                  <div class="stat-value">{{ userStats.rank }}</div>
                  <div class="stat-label">排行榜</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="skills-card">
          <template #header>
            <span>技能掌握</span>
          </template>
          <div class="skills-list">
            <div v-for="skill in userSkills" :key="skill.name" class="skill-item">
              <div class="skill-header">
                <span class="skill-name">{{ skill.name }}</span>
                <span class="skill-level">{{ skill.level }}</span>
              </div>
              <el-progress :percentage="skill.percentage" :color="skill.color" :show-text="false" />
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="leaderboard-card">
          <template #header>
            <span>体验排行榜</span>
          </template>
          <div class="leaderboard-list">
            <div
              v-for="(item, index) in leaderboard"
              :key="item.rank"
              :class="['leader-item', { highlight: item.isMe }]"
            >
              <div class="rank-badge" v-if="item.rank <= 3">
                {{ item.rank === 1 ? '🥇' : item.rank === 2 ? '🥈' : '🥉' }}
              </div>
              <div class="rank-number" v-else>{{ item.rank }}</div>
              <img :src="item.avatar" alt="avatar" class="leader-avatar" />
              <div class="leader-info">
                <div class="leader-name">{{ item.name }}</div>
                <div class="leader-score">{{ item.score }}分 · {{ item.times }}次</div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog v-model="demoVisible" title="场景演示" width="800px">
      <div class="demo-container">
        <div class="demo-video">
          <img :src="currentScene?.image" alt="Demo" />
          <div class="video-overlay">
            <el-button type="primary" size="large">
              <el-icon><VideoPlay /></el-icon>
              播放演示视频
            </el-button>
          </div>
        </div>
        <div class="demo-info">
          <h3>演示说明</h3>
          <p>本演示视频展示了VR反诈演练的操作流程和关键环节，帮助您了解如何在VR环境中识别和应对诈骗场景。</p>
          <div class="demo-tips">
            <h4>温馨提示</h4>
            <ul>
              <li>演示视频时长约3分钟</li>
              <li>建议在稳定的网络环境下观看</li>
              <li>完整体验可获得双倍积分奖励</li>
            </ul>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="previewVisible" :title="previewScene?.title" width="800px">
      <div class="preview-container" v-if="previewScene">
        <div class="preview-image">
          <img :src="previewScene.image" :alt="previewScene.title" />
        </div>
        <div class="preview-info">
          <h3>场景预览</h3>
          <p>{{ previewScene.description }}</p>
          <div class="preview-details">
            <div class="detail-item">
              <span class="detail-label">时长</span>
              <span class="detail-value">{{ previewScene.duration }}分钟</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">难度</span>
              <el-rate v-model="previewScene.difficultyLevel" disabled size="small" />
            </div>
            <div class="detail-item">
              <span class="detail-label">通过率</span>
              <span class="detail-value">{{ previewScene.passRate }}%</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">体验人次</span>
              <span class="detail-value">{{ previewScene.popularity }}</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
        <el-button type="primary" @click="startFromPreview">
          开始体验
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resultVisible" title="体验完成" width="600px" :close-on-click-modal="false">
      <div class="result-container" v-if="experienceResult">
        <div class="result-header">
          <div class="result-score">
            <svg class="score-circle" width="150" height="150">
              <circle cx="75" cy="75" r="65" fill="none" stroke="#e4e7ed" stroke-width="8" />
              <circle
                cx="75"
                cy="75"
                r="65"
                fill="none"
                :stroke="getScoreColor(experienceResult.score)"
                stroke-width="8"
                :stroke-dasharray="`${2 * Math.PI * 65 * experienceResult.score / 100} ${2 * Math.PI * 65}`"
                stroke-linecap="round"
                transform="rotate(-90 75 75)"
              />
              <text x="75" y="75" text-anchor="middle" dominant-baseline="middle" font-size="32" font-weight="bold" :fill="getScoreColor(experienceResult.score)">
                {{ experienceResult.score }}
              </text>
            </svg>
          </div>
          <div class="result-info">
            <h3>{{ experienceResult.passed ? '🎉 恭喜通过！' : '💪 继续加油！' }}</h3>
            <p>{{ experienceResult.comment }}</p>
          </div>
        </div>

        <div class="result-stats">
          <div class="result-stat">
            <span class="stat-label">正确率</span>
            <span class="stat-value">{{ experienceResult.correctRate }}%</span>
          </div>
          <div class="result-stat">
            <span class="stat-label">体验时长</span>
            <span class="stat-value">{{ experienceResult.duration }}</span>
          </div>
          <div class="result-stat">
            <span class="stat-label">获得积分</span>
            <span class="stat-value">+{{ experienceResult.points }}</span>
          </div>
        </div>

        <div class="result-skills" v-if="experienceResult.newSkills?.length">
          <h4>本次解锁技能</h4>
          <div class="skill-tags">
            <el-tag v-for="skill in experienceResult.newSkills" :key="skill" type="success">
              {{ skill }}
            </el-tag>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="resultVisible = false; exitScene()">返回列表</el-button>
        <el-button type="primary" @click="restartScene(); resultVisible = false">
          再次体验
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  VideoPlay,
  VideoCamera,
  Clock,
  User,
  DataLine,
  View,
  RefreshRight,
  Close,
  Check,
  ArrowRight
} from '@element-plus/icons-vue'

interface Scene {
  id: number
  title: string
  type: string
  description: string
  image: string
  duration: number
  difficulty: string
  difficultyLevel: number
  popularity: number
  passRate: number
  instructions: string
  learningGoals: string[]
  operationGuide: string[]
  steps: SceneStep[]
  featured?: boolean
}

interface SceneStep {
  title: string
  description: string
  decision?: DecisionPoint
}

interface DecisionPoint {
  description: string
  options: DecisionOption[]
}

interface DecisionOption {
  id: string
  text: string
  correct: boolean
  explanation: string
}

interface Record {
  id: number
  sceneTitle: string
  duration: string
  score: number
  correctRate: number
  completedAt: string
}

interface UserStat {
  totalTimes: number
  avgScore: number
  certifications: number
  rank: number
  continueDays: number
}

interface Skill {
  name: string
  level: string
  percentage: number
  color: string
}

interface LeaderboardItem {
  rank: number
  name: string
  avatar: string
  score: number
  times: number
  isMe?: boolean
}

interface ExperienceResult {
  score: number
  passed: boolean
  comment: string
  correctRate: number
  duration: string
  points: number
  newSkills?: string[]
}

const vrScenes = ref<Scene[]>([
  {
    id: 1,
    title: '电信诈骗VR演练',
    type: '电信诈骗',
    description: '在VR环境中体验接到自称公检法电话的情景，学习如何识别和应对电信诈骗。',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20telecom%20fraud%20training%20scene&image_size=landscape_16_9',
    duration: 15,
    difficulty: '中等',
    difficultyLevel: 3,
    popularity: 1250,
    passRate: 85,
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
        description: '对方要求你提供个人信息和银行卡信息。',
        decision: {
          description: '对方要求你立即提供银行卡密码，你应该怎么做？',
          options: [
            { id: 'a', text: '立即提供密码', correct: false, explanation: '这是诈骗！绝对不能向陌生人提供密码。' },
            { id: 'b', text: '挂断电话并报警', correct: true, explanation: '正确！真正的公检法不会通过电话索要密码。' },
            { id: 'c', text: '先提供一半', correct: false, explanation: '任何情况下都不应提供密码。' }
          ]
        }
      },
      {
        title: '做出决策',
        description: '你需要根据所学知识，做出正确的决策。'
      },
      {
        title: '总结反馈',
        description: '系统会根据你的表现，提供反馈和建议。'
      }
    ],
    featured: true
  },
  {
    id: 2,
    title: '网络诈骗VR演练',
    type: '网络诈骗',
    description: '在VR环境中体验遇到网络兼职诈骗的情景，学习如何识别和应对网络诈骗。',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=VR%20online%20fraud%20training%20scene&image_size=landscape_16_9',
    duration: 12,
    difficulty: '简单',
    difficultyLevel: 2,
    popularity: 980,
    passRate: 92,
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
        description: '你查看了任务要求，对方要求你先垫付资金。',
        decision: {
          description: '对方要求你先垫付资金进行刷单，你应该怎么做？',
          options: [
            { id: 'a', text: '同意垫付', correct: false, explanation: '这是诈骗的典型特征！正规刷单不需要垫付资金。' },
            { id: 'b', text: '拒绝并举报', correct: true, explanation: '正确！刷单本身就是违法行为，且正规平台不会要求垫付。' },
            { id: 'c', text: '先试试小单', correct: false, explanation: '诈骗者通常会先让你尝到甜头，然后骗取大额资金。' }
          ]
        }
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
    difficultyLevel: 2,
    popularity: 850,
    passRate: 95,
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
        description: '你了解了贷款条件，对方要求你提供个人信息和身份证照片。',
        decision: {
          description: '对方要求你提供身份证照片和手持照片，你应该怎么做？',
          options: [
            { id: 'a', text: '立即提供', correct: false, explanation: '这可能导致个人信息泄露，被用于其他违法活动。' },
            { id: 'b', text: '拒绝并咨询学校', correct: true, explanation: '正确！应该通过学校正规渠道寻求帮助。' },
            { id: 'c', text: '只提供身份证号', correct: false, explanation: '任何个人信息都不应轻易提供给陌生人。' }
          ]
        }
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

const currentScene = ref<Scene | null>(null)
const experienceStarted = ref(false)
const experienceCompleted = ref(false)
const showDecision = ref(false)
const currentDecision = ref<DecisionPoint | null>(null)
const currentStepIndex = ref(0)
const experienceProgress = ref(0)
const demoVisible = ref(false)
const previewVisible = ref(false)
const previewScene = ref<Scene | null>(null)
const resultVisible = ref(false)
const experienceResult = ref<ExperienceResult | null>(null)
const recordFilter = ref('all')

const userStats = ref<UserStat>({
  totalTimes: 12,
  avgScore: 88,
  certifications: 3,
  rank: 15,
  continueDays: 5
})

const userSkills = ref<Skill[]>([
  { name: '电信诈骗识别', level: '熟练', percentage: 80, color: '#67c23a' },
  { name: '网络诈骗识别', level: '精通', percentage: 90, color: '#409eff' },
  { name: '金融诈骗识别', level: '入门', percentage: 45, color: '#e6a23c' }
])

const leaderboard = ref<LeaderboardItem[]>([
  { rank: 1, name: '反诈先锋', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20tech&image_size=square', score: 98, times: 45 },
  { rank: 2, name: '防诈达人', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20business&image_size=square', score: 95, times: 38 },
  { rank: 3, name: '安全专家', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20student&image_size=square', score: 93, times: 32 },
  { rank: 4, name: '我', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20friendly&image_size=square', score: 88, times: 12, isMe: true },
  { rank: 5, name: '学习者', avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20casual&image_size=square', score: 85, times: 10 }
])

const experienceRecords = ref<Record[]>([
  { id: 1, sceneTitle: '电信诈骗VR演练', duration: '15分钟', score: 90, correctRate: 88, completedAt: '2026-04-10 14:30' },
  { id: 2, sceneTitle: '网络诈骗VR演练', duration: '12分钟', score: 85, correctRate: 82, completedAt: '2026-04-09 10:15' },
  { id: 3, sceneTitle: '校园贷诈骗VR演练', duration: '10分钟', score: 95, correctRate: 95, completedAt: '2026-04-08 16:45' }
])

const filteredRecords = computed(() => {
  if (recordFilter.value === 'all') return experienceRecords.value
  return experienceRecords.value.slice(0, 2)
})

const progressColor = computed(() => {
  if (experienceProgress.value < 30) return '#f56c6c'
  if (experienceProgress.value < 70) return '#e6a23c'
  return '#67c23a'
})

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

function getScoreColor(score: number): string {
  if (score >= 90) return '#67c23a'
  if (score >= 70) return '#e6a23c'
  return '#f56c6c'
}

function formatProgress(percentage: number): string {
  return `${percentage}%`
}

function selectScene(scene: Scene) {
  currentScene.value = scene
  experienceStarted.value = false
  experienceCompleted.value = false
  currentStepIndex.value = 0
  experienceProgress.value = 0
}

function startQuickTraining() {
  const recommended = vrScenes.value.find(s => s.featured) || vrScenes.value[0]
  selectScene(recommended)
}

function viewScenePreview(scene: Scene) {
  previewScene.value = scene
  previewVisible.value = true
}

function startFromPreview() {
  if (previewScene.value) {
    selectScene(previewScene.value)
    previewVisible.value = false
  }
}

function startExperience() {
  experienceStarted.value = true
  experienceProgress.value = 0
  currentStepIndex.value = 0
  simulateExperience()
}

function simulateExperience() {
  const interval = setInterval(() => {
    if (currentStepIndex.value >= (currentScene.value?.steps.length || 0)) {
      clearInterval(interval)
      completeExperience()
      return
    }

    const step = currentScene.value?.steps[currentStepIndex.value]
    if (step?.decision && !showDecision.value) {
      showDecision.value = true
      currentDecision.value = step.decision
      clearInterval(interval)
    } else {
      experienceProgress.value = Math.min(100, Math.floor((currentStepIndex.value + 1) / (currentScene.value?.steps.length || 1) * 100))
      currentStepIndex.value++
      showDecision.value = false
      currentDecision.value = null
      if (currentStepIndex.value < (currentScene.value?.steps.length || 0)) {
        setTimeout(simulateExperience, 1500)
      }
    }
  }, 1500)
}

function makeDecision(option: DecisionOption) {
  if (option.correct) {
    ElMessage.success(option.explanation)
  } else {
    ElMessage.error(option.explanation)
  }
  showDecision.value = false
  currentDecision.value = null
  currentStepIndex.value++
  experienceProgress.value = Math.min(100, Math.floor((currentStepIndex.value + 1) / (currentScene.value?.steps.length || 1) * 100))
  simulateExperience()
}

function viewDemo() {
  demoVisible.value = true
}

function completeExperience() {
  experienceCompleted.value = true
  const score = Math.floor(Math.random() * 15) + 80
  const passed = score >= 70

  experienceResult.value = {
    score,
    passed,
    comment: passed ? '您已掌握该场景的防诈技能，继续保持！' : '建议重新体验，加深对诈骗手法的认识。',
    correctRate: Math.floor(Math.random() * 20) + 75,
    duration: `${currentScene.value?.duration || 0}分钟`,
    points: passed ? 20 : 10,
    newSkills: passed && score >= 90 ? [currentScene.value?.type + '识别专家'] : []
  }

  resultVisible.value = true

  experienceRecords.value.unshift({
    id: experienceRecords.value.length + 1,
    sceneTitle: currentScene.value?.title || '',
    duration: experienceResult.value.duration,
    score: score,
    correctRate: experienceResult.value.correctRate,
    completedAt: new Date().toLocaleString()
  })
}

function restartScene() {
  experienceStarted.value = false
  experienceCompleted.value = false
  showDecision.value = false
  currentDecision.value = null
  currentStepIndex.value = 0
  experienceProgress.value = 0
}

function exitScene() {
  currentScene.value = null
  experienceStarted.value = false
  experienceCompleted.value = false
  showDecision.value = false
  currentDecision.value = null
  currentStepIndex.value = 0
  experienceProgress.value = 0
}

function replayExperience(record: Record) {
  const scene = vrScenes.value.find(s => s.title === record.sceneTitle)
  if (scene) {
    selectScene(scene)
    startExperience()
  }
}

function viewRecordDetail(record: Record) {
  ElMessage.info('查看详情功能开发中')
}

onMounted(() => {
  console.log('VR演练体验初始化')
})
</script>

<style scoped>
.vr-training {
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f0f3f7 0%, #e8ecf1 100%);
  min-height: calc(100vh - 60px);
}

.header {
  margin-bottom: 24px;
}

.header-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.header-text h1 {
  margin: 0 0 12px 0;
  font-size: 32px;
}

.differentiator {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.tag {
  background: rgba(255, 255, 255, 0.2);
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
}

.competitor {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.header-stats {
  display: flex;
  gap: 32px;
  margin-top: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.vr-content {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
}

.main-area {
  min-width: 0;
}

.side-area {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.quick-start {
  margin-bottom: 24px;
}

.quick-card {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border: none;
  color: white;
}

.quick-card :deep(.el-card__header) {
  background: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.quick-card :deep(.el-card__body) {
  background: transparent;
}

.quick-start-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-info h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
}

.quick-info p {
  margin: 0 0 12px 0;
  opacity: 0.9;
}

.quick-tags {
  display: flex;
  gap: 8px;
}

.scene-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.scene-card {
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.scene-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
}

.scene-card.featured {
  border-color: #e6a23c;
}

.scene-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.scene-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.scene-title-row h3 {
  margin: 0;
  font-size: 18px;
}

.scene-card-content {
  padding: 16px 0;
}

.scene-image {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 16px;
}

.scene-image img {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.scene-duration {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.scene-description {
  margin: 0 0 16px 0;
  color: #606266;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.scene-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.stat {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.scene-difficulty {
  display: flex;
  align-items: center;
  gap: 8px;
}

.difficulty-label {
  font-size: 13px;
  color: #606266;
}

.scene-card-footer {
  display: flex;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.scene-card-footer :deep(.el-button) {
  flex: 1;
}

.experience-main-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.scene-info-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.scene-info-header h2 {
  margin: 0;
  font-size: 20px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.experience-workspace {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 24px;
}

.vr-container {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
}

.vr-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.vr-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
}

.overlay-content {
  text-align: center;
  color: white;
  padding: 24px;
}

.overlay-content h3 {
  margin: 0 0 16px 0;
  font-size: 24px;
}

.overlay-content p {
  margin: 0 0 24px 0;
  line-height: 1.6;
}

.overlay-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.decision-prompt {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
}

.decision-content {
  background: white;
  border-radius: 16px;
  padding: 32px;
  max-width: 500px;
  text-align: center;
}

.decision-content h4 {
  margin: 0 0 16px 0;
  color: #f56c6c;
  font-size: 20px;
}

.decision-content p {
  margin: 0 0 24px 0;
  color: #333;
  line-height: 1.6;
}

.decision-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.experience-sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-card :deep(.el-card__header) {
  padding: 12px 16px;
  font-size: 14px;
}

.goal-list,
.guide-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.goal-list li,
.guide-list li {
  padding: 8px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.goal-list li .el-icon {
  color: #67c23a;
}

.guide-list li .el-icon {
  color: #409eff;
}

.progress-info {
  text-align: center;
}

.step-indicator {
  margin-top: 8px;
  font-size: 14px;
  color: #909399;
}

.user-stats-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-icon {
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.skills-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skill-item {
  padding: 8px 0;
}

.skill-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.skill-name {
  font-size: 14px;
  color: #333;
}

.skill-level {
  font-size: 12px;
  color: #909399;
}

.leaderboard-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.leader-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 8px;
  transition: background 0.2s;
}

.leader-item:hover {
  background: #f5f7fa;
}

.leader-item.highlight {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border: 1px solid rgba(102, 126, 234, 0.3);
}

.rank-badge {
  font-size: 20px;
  width: 28px;
  text-align: center;
}

.rank-number {
  width: 28px;
  text-align: center;
  font-size: 14px;
  color: #909399;
}

.leader-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.leader-info {
  flex: 1;
  min-width: 0;
}

.leader-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.leader-score {
  font-size: 12px;
  color: #909399;
}

.scene-name {
  font-weight: 500;
}

.correct-rate {
  color: #67c23a;
  font-weight: 500;
}

.demo-container,
.preview-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.demo-video,
.preview-image {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
}

.demo-video img,
.preview-image img {
  width: 100%;
  height: 350px;
  object-fit: cover;
}

.video-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.demo-info h3,
.preview-info h3 {
  margin: 0 0 12px 0;
}

.demo-tips {
  margin-top: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.demo-tips h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
}

.demo-tips ul {
  margin: 0;
  padding-left: 20px;
  font-size: 13px;
  color: #606266;
}

.preview-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-top: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 13px;
  color: #909399;
}

.detail-value {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.result-container {
  padding: 16px 0;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
}

.result-score {
  flex-shrink: 0;
}

.result-info h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.result-info p {
  margin: 0;
  color: #606266;
}

.result-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
  background: #f5f7fa;
  border-radius: 12px;
  margin-bottom: 24px;
}

.result-stat {
  text-align: center;
}

.result-stat .stat-label {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.result-stat .stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.result-skills h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
}

.skill-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

@media (max-width: 1200px) {
  .vr-content {
    grid-template-columns: 1fr;
  }

  .side-area {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .experience-workspace {
    grid-template-columns: 1fr;
  }

  .scene-grid {
    grid-template-columns: 1fr;
  }
}
</style>
