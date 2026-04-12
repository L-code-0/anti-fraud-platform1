<template>
  <div class="scenario-replay">
    <!-- 头部 -->
    <div class="header">
      <h1>情景沉浸回放</h1>
      <p>太原反诈基地 - 真实案例+决策点复盘</p>
    </div>

    <!-- 案例选择 -->
    <div class="case-selection" v-if="!currentCase">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>选择案例</span>
          </div>
        </template>
        <div class="case-list">
          <el-card 
            v-for="caseItem in cases" 
            :key="caseItem.id"
            shadow="hover"
            class="case-card"
            @click="selectCase(caseItem)"
          >
            <template #header>
              <div class="case-card-header">
                <h3>{{ caseItem.title }}</h3>
                <el-tag :type="getCaseType(caseItem.type)">{{ caseItem.type }}</el-tag>
              </div>
            </template>
            <div class="case-card-content">
              <p>{{ caseItem.description }}</p>
              <div class="case-meta">
                <span class="meta-item">{{ caseItem.duration }}分钟</span>
                <span class="meta-item">{{ caseItem.difficulty }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </el-card>
    </div>

    <!-- 案例回放 -->
    <div class="case-replay" v-if="currentCase">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>{{ currentCase.title }}</span>
            <el-button type="info" @click="restartCase">重新开始</el-button>
            <el-button type="warning" @click="exitCase">退出案例</el-button>
          </div>
        </template>
        <div class="replay-content">
          <!-- 场景展示 -->
          <div class="scene-display">
            <div class="scene-image">
              <img :src="currentScene?.image" :alt="currentScene?.title" />
            </div>
            <div class="scene-narrative">
              <h3>{{ currentScene?.title }}</h3>
              <p>{{ currentScene?.narrative }}</p>
            </div>
          </div>

          <!-- 决策点 -->
          <div class="decision-point" v-if="currentScene?.options && currentScene?.options.length > 0">
            <h4>请做出选择：</h4>
            <div class="decision-options">
              <el-button 
                v-for="(option, index) in currentScene?.options" 
                :key="index"
                type="primary"
                plain
                @click="makeDecision(index)"
              >
                {{ option }}
              </el-button>
            </div>
          </div>

          <!-- 结果展示 -->
          <div class="result-display" v-if="showResult">
            <el-alert
              :title="decisionResult?.result"
              :type="getResultType(decisionResult?.result)"
              :description="decisionResult?.message"
              show-icon
              :closable="false"
            />
            <div class="result-suggestion">
              <h4>防范建议：</h4>
              <p>{{ decisionResult?.suggestion }}</p>
            </div>
            <div class="result-actions">
              <el-button type="primary" @click="nextScene" v-if="hasNextScene">继续</el-button>
              <el-button type="success" @click="showReplay" v-else>查看复盘</el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 复盘分析 -->
    <div class="replay-analysis" v-if="showReplayAnalysis">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>案例复盘</span>
            <el-button type="info" @click="exitReplay">返回</el-button>
          </div>
        </template>
        <div class="analysis-content">
          <div class="case-summary">
            <h3>案例总结</h3>
            <p>{{ currentCase?.summary }}</p>
          </div>
          <div class="decision-history">
            <h3>决策历史</h3>
            <el-timeline>
              <el-timeline-item 
                v-for="(decision, index) in decisionHistory" 
                :key="index"
                :type="getResultType(decision.result)"
                :timestamp="`场景 ${index + 1}`"
              >
                <div class="timeline-content">
                  <h4>{{ decision.sceneTitle }}</h4>
                  <p>您的选择：{{ decision.option }}</p>
                  <p class="result-text">{{ decision.result }}：{{ decision.message }}</p>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
          <div class="prevention-guide">
            <h3>防范指南</h3>
            <ul>
              <li v-for="(guide, index) in currentCase?.preventionGuide" :key="index">
                {{ guide }}
              </li>
            </ul>
          </div>
          <div class="related-cases">
            <h3>相关案例</h3>
            <div class="related-list">
              <el-button 
                v-for="(relatedCase, index) in relatedCases" 
                :key="index"
                type="info"
                plain
                @click="selectCase(relatedCase)"
              >
                {{ relatedCase.title }}
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const cases = ref([
  {
    id: 1,
    title: '电信诈骗 - 冒充公检法',
    type: '电信诈骗',
    description: '模拟接到自称公检法电话的情景，体验完整的诈骗过程和防范措施',
    duration: 15,
    difficulty: '中等',
    summary: '本案例模拟了一个典型的冒充公检法电信诈骗场景，骗子通过恐吓、威胁等手段，诱导受害者提供个人信息和银行卡信息。通过本次体验，用户可以了解此类诈骗的完整流程和防范方法。',
    scenes: [
      {
        id: 101,
        title: '接到陌生电话',
        narrative: '你正在家中休息，突然接到一个陌生电话，对方自称是公安局的民警，说你涉及一起洗钱案件，需要配合调查。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20receiving%20phone%20call%20worried%20expression&image_size=landscape_16_9',
        options: [
          '相信对方，配合调查',
          '要求对方出示证件',
          '挂断电话并报警',
          '询问具体案件详情'
        ],
        outcomes: [
          {
            result: '危险',
            message: '你选择相信对方并配合调查，对方会进一步要求你提供银行卡信息和验证码，最终导致资金被盗。',
            suggestion: '遇到自称公检法的电话，不要轻信，应挂断后通过官方渠道核实。'
          },
          {
            result: '谨慎',
            message: '你要求对方出示证件，对方可能会伪造证件继续迷惑你。',
            suggestion: '即使对方出示证件，也不要轻信，应通过官方渠道核实。'
          },
          {
            result: '正确',
            message: '你挂断电话并报警，避免了被骗的风险。',
            suggestion: '遇到可疑电话，应及时报警，不要与对方纠缠。'
          },
          {
            result: '危险',
            message: '你与对方过多纠缠，对方会用专业的话术逐渐迷惑你，最终导致你上当。',
            suggestion: '遇到可疑电话，应直接挂断并报警。'
          }
        ]
      },
      {
        id: 102,
        title: '对方施压',
        narrative: '对方见你有所怀疑，开始施压，说你如果不配合调查，将会被刑事拘留，并要求你立即转移资金到安全账户。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20stressed%20on%20phone%20police%20scam&image_size=landscape_16_9',
        options: [
          '按照要求转移资金',
          '再次要求核实身份',
          '挂断电话并报警',
          '咨询家人朋友'
        ],
        outcomes: [
          {
            result: '危险',
            message: '你按照要求转移资金，最终导致资金损失。',
            suggestion: '公检法机关不会要求转账，遇到此类要求应立即报警。'
          },
          {
            result: '谨慎',
            message: '你再次要求核实身份，对方可能会提供虚假的核实方式。',
            suggestion: '应通过官方渠道核实，如拨打110。'
          },
          {
            result: '正确',
            message: '你挂断电话并报警，避免了资金损失。',
            suggestion: '遇到可疑电话，应及时报警，不要与对方纠缠。'
          },
          {
            result: '正确',
            message: '你咨询家人朋友，得到了正确的建议，避免了被骗。',
            suggestion: '遇到可疑情况，应及时咨询他人，避免单独做决定。'
          }
        ]
      },
      {
        id: 103,
        title: '最终决定',
        narrative: '对方继续施压，说你已经被列为网上逃犯，需要立即处理，否则将上门抓捕。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20anxious%20computer%20screen%20police%20warning&image_size=landscape_16_9',
        options: [
          '按照要求操作',
          '挂断电话并报警',
          '登录官方网站核实',
          '忽略对方'
        ],
        outcomes: [
          {
            result: '危险',
            message: '你按照要求操作，最终导致资金损失。',
            suggestion: '公检法机关不会通过电话办案，遇到此类情况应立即报警。'
          },
          {
            result: '正确',
            message: '你挂断电话并报警，避免了资金损失。',
            suggestion: '遇到可疑电话，应及时报警，不要与对方纠缠。'
          },
          {
            result: '正确',
            message: '你登录官方网站核实，发现没有相关案件，避免了被骗。',
            suggestion: '遇到可疑情况，应通过官方渠道核实。'
          },
          {
            result: '正确',
            message: '你忽略对方，避免了被骗的风险。',
            suggestion: '遇到可疑电话，应直接挂断，不要与对方纠缠。'
          }
        ]
      }
    ],
    preventionGuide: [
      '公检法机关不会通过电话办案，不会要求转账',
      '遇到自称公检法的电话，应挂断后通过官方渠道核实',
      '不要向陌生人透露个人信息和银行卡信息',
      '遇到可疑情况，应及时报警'
    ]
  },
  {
    id: 2,
    title: '网络兼职诈骗 - 刷单',
    type: '网络诈骗',
    description: '模拟遇到刷单兼职的情景，体验完整的诈骗过程和防范措施',
    duration: 12,
    difficulty: '简单',
    summary: '本案例模拟了一个典型的网络兼职刷单诈骗场景，骗子以高额报酬为诱饵，要求受害者先垫付资金，然后以各种理由拒绝返款。通过本次体验，用户可以了解此类诈骗的完整流程和防范方法。',
    scenes: [
      {
        id: 201,
        title: '看到兼职广告',
        narrative: '你在网上看到一个刷单兼职的广告，声称只要完成任务就能获得高额报酬。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20looking%20at%20phone%20兼职%20ad&image_size=landscape_16_9',
        options: [
          '立即报名参加',
          '查看详细要求',
          '搜索相关信息',
          '直接忽略'
        ],
        outcomes: [
          {
            result: '危险',
            message: '你立即报名参加，对方会要求你先垫付资金，最终导致资金损失。',
            suggestion: '刷单本身就是违法行为，遇到此类广告应直接忽略。'
          },
          {
            result: '危险',
            message: '你查看详细要求，对方会用高额报酬迷惑你，最终导致你上当。',
            suggestion: '刷单兼职都是诈骗，不要相信任何形式的刷单广告。'
          },
          {
            result: '正确',
            message: '你搜索相关信息，了解到这是一种常见的诈骗手段，避免了被骗。',
            suggestion: '遇到可疑兼职，应先搜索相关信息，了解其真实性。'
          },
          {
            result: '正确',
            message: '你直接忽略，避免了被骗的风险。',
            suggestion: '遇到可疑广告，应直接忽略，不要点击任何链接。'
          }
        ]
      },
      {
        id: 202,
        title: '完成第一单',
        narrative: '你报名参加后，对方要求你完成第一单任务，购买指定商品，承诺完成后立即返款。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20shopping%20online%20computer&image_size=landscape_16_9',
        options: [
          '按照要求购买',
          '要求先返款',
          '拒绝操作',
          '咨询客服'
        ],
        outcomes: [
          {
            result: '危险',
            message: '你按照要求购买，对方可能会返款第一单，但后续会要求你购买更大金额的商品。',
            suggestion: '刷单是诈骗的常见手段，不要相信任何形式的刷单兼职。'
          },
          {
            result: '正确',
            message: '你要求先返款，对方无法满足，你避免了被骗。',
            suggestion: '正规兼职不会要求先垫付资金，遇到此类要求应立即拒绝。'
          },
          {
            result: '正确',
            message: '你拒绝操作，避免了被骗的风险。',
            suggestion: '遇到可疑兼职，应直接拒绝，不要抱有侥幸心理。'
          },
          {
            result: '危险',
            message: '你咨询客服，客服会用话术迷惑你，最终导致你上当。',
            suggestion: '刷单兼职的客服都是骗子，不要相信他们的任何承诺。'
          }
        ]
      },
      {
        id: 203,
        title: '要求继续刷单',
        narrative: '对方返款第一单后，要求你完成更大金额的任务，承诺获得更高的报酬。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20worried%20computer%20large%20amount&image_size=landscape_16_9',
        options: [
          '继续刷单',
          '要求返款',
          '停止操作',
          '报警'
        ],
        outcomes: [
          {
            result: '危险',
            message: '你继续刷单，对方会以各种理由拒绝返款，最终导致资金损失。',
            suggestion: '刷单是诈骗的常见手段，不要相信任何形式的刷单兼职。'
          },
          {
            result: '危险',
            message: '你要求返款，对方会以各种理由拖延，最终消失。',
            suggestion: '遇到此类情况，应立即报警，不要继续与对方纠缠。'
          },
          {
            result: '正确',
            message: '你停止操作，避免了进一步的资金损失。',
            suggestion: '遇到可疑情况，应立即停止操作，避免进一步损失。'
          },
          {
            result: '正确',
            message: '你报警，警方会介入调查，帮助你挽回损失。',
            suggestion: '遇到诈骗，应立即报警，寻求警方帮助。'
          }
        ]
      }
    ],
    preventionGuide: [
      '刷单本身就是违法行为，遇到此类广告应直接忽略',
      '正规兼职不会要求先垫付资金',
      '不要相信高额报酬的诱惑',
      '遇到可疑兼职，应先搜索相关信息，了解其真实性'
    ]
  },
  {
    id: 3,
    title: '校园贷诈骗',
    type: '金融诈骗',
    description: '模拟遇到校园贷的情景，体验完整的诈骗过程和防范措施',
    duration: 10,
    difficulty: '简单',
    summary: '本案例模拟了一个典型的校园贷诈骗场景，骗子以无抵押、低利息为诱饵，诱导受害者借款，然后收取高额利息和手续费。通过本次体验，用户可以了解此类诈骗的完整流程和防范方法。',
    scenes: [
      {
        id: 301,
        title: '看到贷款广告',
        narrative: '你急需用钱，看到校园里的贷款广告，声称无抵押、低利息。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=student%20looking%20at%20loan%20ad%20campus&image_size=landscape_16_9',
        options: [
          '联系对方办理贷款',
          '咨询学校老师',
          '向家人朋友借钱',
          '忽略广告'
        ],
        outcomes: [
          {
            result: '危险',
            message: '你联系对方办理贷款，对方会收取高额手续费和利息，最终导致你陷入债务陷阱。',
            suggestion: '校园贷往往存在高额利息和暴力催收，应避免接触。'
          },
          {
            result: '正确',
            message: '你咨询学校老师，得到了正确的建议，避免了被骗。',
            suggestion: '遇到资金困难，应向学校或正规金融机构寻求帮助。'
          },
          {
            result: '正确',
            message: '你向家人朋友借钱，避免了陷入校园贷的陷阱。',
            suggestion: '遇到资金困难，应优先向家人朋友求助。'
          },
          {
            result: '正确',
            message: '你忽略广告，避免了被骗的风险。',
            suggestion: '遇到可疑贷款广告，应直接忽略。'
          }
        ]
      },
      {
        id: 302,
        title: '办理贷款',
        narrative: '你联系对方后，对方要求你提供个人信息和身份证照片，然后签订贷款合同。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=student%20giving%20id%20card%20worried&image_size=landscape_16_9',
        options: [
          '提供个人信息',
          '要求查看合同细则',
          '拒绝办理',
          '咨询专业人士'
        ],
        outcomes: [
          {
            result: '危险',
            message: '你提供个人信息，对方会利用你的信息进行贷款，最终导致你背负债务。',
            suggestion: '不要向陌生人提供个人信息和身份证照片。'
          },
          {
            result: '正确',
            message: '你要求查看合同细则，发现其中存在不合理条款，避免了被骗。',
            suggestion: '签订任何合同前，应仔细阅读条款，了解其中的风险。'
          },
          {
            result: '正确',
            message: '你拒绝办理，避免了被骗的风险。',
            suggestion: '遇到可疑贷款，应直接拒绝，不要抱有侥幸心理。'
          },
          {
            result: '正确',
            message: '你咨询专业人士，了解到这是一种诈骗手段，避免了被骗。',
            suggestion: '遇到金融问题，应咨询专业人士，获取正确的建议。'
          }
        ]
      }
    ],
    preventionGuide: [
      '树立正确的消费观念，避免过度消费',
      '遇到资金困难向学校或正规金融机构寻求帮助',
      '不要相信无抵押、低利息的贷款广告',
      '了解相关法律法规，增强法律意识'
    ]
  }
])

const currentCase = ref<any>(null)
const currentSceneIndex = ref(0)
const currentScene = ref<any>(null)
const showResult = ref(false)
const decisionResult = ref<any>(null)
const showReplayAnalysis = ref(false)
const decisionHistory = ref<any[]>([])
const relatedCases = ref<any[]>([])

function getCaseType(type: string): string {
  const typeMap: Record<string, string> = {
    '电信诈骗': 'danger',
    '网络诈骗': 'warning',
    '金融诈骗': 'primary'
  }
  return typeMap[type] || 'info'
}

function getResultType(result: string): string {
  const typeMap: Record<string, string> = {
    '正确': 'success',
    '谨慎': 'warning',
    '危险': 'danger'
  }
  return typeMap[result] || 'info'
}

function selectCase(caseItem: any) {
  currentCase.value = caseItem
  currentSceneIndex.value = 0
  currentScene.value = caseItem.scenes[currentSceneIndex.value]
  showResult.value = false
  decisionResult.value = null
  showReplayAnalysis.value = false
  decisionHistory.value = []
  // 生成相关案例
  relatedCases.value = cases.value.filter(c => c.id !== caseItem.id)
}

function makeDecision(index: number) {
  const outcome = currentScene.value.outcomes[index]
  decisionResult.value = outcome
  // 记录决策历史
  decisionHistory.value.push({
    sceneTitle: currentScene.value.title,
    option: currentScene.value.options[index],
    result: outcome.result,
    message: outcome.message
  })
  showResult.value = true
}

function nextScene() {
  currentSceneIndex.value++
  if (currentSceneIndex.value < currentCase.value.scenes.length) {
    currentScene.value = currentCase.value.scenes[currentSceneIndex.value]
    showResult.value = false
    decisionResult.value = null
  }
}

function hasNextScene(): boolean {
  return currentSceneIndex.value < currentCase.value.scenes.length - 1
}

function showReplay() {
  showReplayAnalysis.value = true
}

function restartCase() {
  currentSceneIndex.value = 0
  currentScene.value = currentCase.value.scenes[currentSceneIndex.value]
  showResult.value = false
  decisionResult.value = null
  decisionHistory.value = []
}

function exitCase() {
  currentCase.value = null
  currentScene.value = null
  showResult.value = false
  decisionResult.value = null
  showReplayAnalysis.value = false
  decisionHistory.value = []
}

function exitReplay() {
  showReplayAnalysis.value = false
}

onMounted(() => {
  // 初始化案例数据
  console.log('情景沉浸回放初始化')
})
</script>

<style scoped>
.scenario-replay {
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

.case-selection,
.case-replay,
.replay-analysis {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.case-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-top: 24px;
}

.case-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.case-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.case-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.case-card-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.case-card-content p {
  margin: 12px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.case-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.replay-content {
  padding: 20px 0;
}

.scene-display {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
  align-items: center;
}

.scene-image {
  flex: 1;
  max-width: 500px;
  border-radius: 8px;
  overflow: hidden;
}

.scene-image img {
  width: 100%;
  height: auto;
  object-fit: cover;
}

.scene-narrative {
  flex: 1;
}

.scene-narrative h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #333;
}

.scene-narrative p {
  margin: 0;
  color: #666;
  line-height: 1.5;
  font-size: 16px;
}

.decision-point {
  margin-bottom: 32px;
}

.decision-point h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
}

.decision-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.result-display {
  margin-bottom: 32px;
}

.result-suggestion {
  margin-top: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.result-suggestion h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.result-suggestion p {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.result-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.analysis-content {
  padding: 20px 0;
}

.case-summary,
.decision-history,
.prevention-guide,
.related-cases {
  margin-bottom: 32px;
}

.case-summary h3,
.decision-history h3,
.prevention-guide h3,
.related-cases h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #333;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.case-summary p {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.timeline-content h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #333;
}

.timeline-content p {
  margin: 4px 0;
  color: #666;
  font-size: 14px;
}

.result-text {
  font-weight: 500;
  color: #333;
}

.prevention-guide ul {
  margin: 0;
  padding-left: 20px;
}

.prevention-guide li {
  margin-bottom: 8px;
  color: #666;
  line-height: 1.5;
}

.related-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

@media (max-width: 768px) {
  .scenario-replay {
    padding: 16px;
  }
  
  .case-list {
    grid-template-columns: 1fr;
  }
  
  .scene-display {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .scene-image {
    max-width: 100%;
  }
  
  .decision-options {
    flex-direction: column;
  }
  
  .result-actions {
    flex-direction: column;
  }
  
  .related-list {
    flex-direction: column;
  }
}
</style>