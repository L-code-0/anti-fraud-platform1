<template>
  <div class="scenario-replay">
    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>情景沉浸回放</h1>
          <div class="differentiator">
            <span class="tag">真实案例</span>
            <span class="tag">决策点复盘</span>
            <span class="competitor">对比：太原反诈基地</span>
          </div>
        </div>
        <div class="header-features">
          <div class="feature-item">
            <span class="feature-icon">📱</span>
            <span class="feature-text">沉浸式体验</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🎯</span>
            <span class="feature-text">决策点分析</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">📊</span>
            <span class="feature-text">复盘总结</span>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="case-selection" v-if="!currentCase">
        <div class="selection-header">
          <h2>选择案例开始体验</h2>
          <p>通过真实案例，学习防范诈骗技巧</p>
        </div>

        <div class="case-grid">
          <div
            v-for="caseItem in cases"
            :key="caseItem.id"
            class="case-card"
            :class="{ featured: caseItem.featured }"
            @click="selectCase(caseItem)"
          >
            <div class="card-badge" v-if="caseItem.featured">
              <el-tag type="warning" size="small">精选案例</el-tag>
            </div>
            <div class="card-type">
              <span class="type-icon">{{ getTypeIcon(caseItem.type) }}</span>
              <el-tag :type="getCaseType(caseItem.type)" size="small">
                {{ caseItem.type }}
              </el-tag>
            </div>
            <h3>{{ caseItem.title }}</h3>
            <p class="card-desc">{{ caseItem.description }}</p>
            <div class="card-meta">
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ caseItem.duration }}分钟
              </span>
              <span class="meta-item">
                <el-icon><TrendCharts /></el-icon>
                {{ caseItem.difficulty }}
              </span>
              <span class="meta-item">
                <el-icon><Aim /></el-icon>
                {{ caseItem.sceneCount }}个决策点
              </span>
            </div>
            <div class="card-footer">
              <div class="case-stats">
                <span class="stat">学习人数 {{ caseItem.learnCount }}</span>
                <span class="stat">评分 {{ caseItem.rating }}</span>
              </div>
              <el-button type="primary" size="small">
                开始体验
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="case-replay" v-if="currentCase && !showReplayAnalysis">
        <div class="replay-header">
          <div class="replay-info">
            <h2>{{ currentCase.title }}</h2>
            <div class="replay-tags">
              <el-tag size="small">{{ currentCase.type }}</el-tag>
              <el-tag size="small" type="info">{{ currentCase.difficulty }}</el-tag>
              <span class="progress-text">场景 {{ currentSceneIndex + 1 }}/{{ currentCase.scenes.length }}</span>
            </div>
          </div>
          <div class="replay-actions">
            <el-button @click="showReplayAnalysis = true">
              <el-icon><DataAnalysis /></el-icon>
              查看复盘
            </el-button>
            <el-button type="warning" @click="restartCase">
              <el-icon><RefreshLeft /></el-icon>
              重新开始
            </el-button>
            <el-button @click="exitCase">
              <el-icon><Close /></el-icon>
              退出
            </el-button>
          </div>
        </div>

        <div class="replay-content">
          <div class="scene-panel">
            <div class="scene-visual">
              <div class="scene-image" v-if="currentScene?.image">
                <img :src="currentScene.image" :alt="currentScene.title" />
                <div class="scene-overlay">
                  <span class="scene-badge">决策点</span>
                </div>
              </div>
              <div class="scene-placeholder" v-else>
                <span class="placeholder-icon">{{ getTypeIcon(currentCase.type) }}</span>
                <span>场景图片</span>
              </div>
            </div>

            <div class="scene-info">
              <div class="scene-number">场景 {{ currentSceneIndex + 1 }}</div>
              <h3 class="scene-title">{{ currentScene?.title }}</h3>
              <p class="scene-narrative">{{ currentScene?.narrative }}</p>
            </div>
          </div>

          <div class="decision-panel">
            <div class="decision-header">
              <div class="header-icon">🎯</div>
              <h4>关键决策点</h4>
              <p>您的选择将影响最终结果，请谨慎思考</p>
            </div>

            <div class="decision-options" v-if="!showResult">
              <div
                v-for="(option, index) in currentScene?.options"
                :key="index"
                class="option-item"
                :class="{ selected: selectedOption === index }"
                @click="selectOption(index)"
              >
                <div class="option-letter">{{ getOptionLetter(index) }}</div>
                <div class="option-text">{{ option }}</div>
                <div class="option-hint" v-if="selectedOption === index">
                  点击确认选择
                </div>
              </div>
            </div>

            <div class="confirm-action" v-if="selectedOption !== null && !showResult">
              <el-button type="primary" size="large" @click="makeDecision" :loading="isDeciding">
                <el-icon><Check /></el-icon>
                确认选择
              </el-button>
            </div>

            <div class="result-display" v-if="showResult">
              <div class="result-card" :class="decisionResult?.resultType">
                <div class="result-header">
                  <span class="result-icon">{{ getResultIcon(decisionResult?.resultType) }}</span>
                  <span class="result-badge">{{ decisionResult?.result }}</span>
                </div>
                <p class="result-message">{{ decisionResult?.message }}</p>
                <div class="result-analysis">
                  <h5>
                    <el-icon><Warning /></el-icon>
                    深度分析
                  </h5>
                  <p>{{ decisionResult?.analysis }}</p>
                </div>
                <div class="result-suggestion">
                  <h5>
                    <el-icon><Lightbulb /></el-icon>
                    防范建议
                  </h5>
                  <p>{{ decisionResult?.suggestion }}</p>
                </div>
              </div>

              <div class="result-actions">
                <el-button type="primary" size="large" @click="nextScene" v-if="hasNextScene">
                  继续下一个场景
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
                <el-button type="success" size="large" @click="showReplayAnalysis = true" v-else>
                  <el-icon><DataAnalysis /></el-icon>
                  查看完整复盘
                </el-button>
              </div>

              <div class="decision-summary">
                <div class="summary-item">
                  <span class="label">当前得分</span>
                  <span class="value score">{{ currentScore }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">正确决策</span>
                  <span class="value correct">{{ correctCount }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">危险决策</span>
                  <span class="value danger">{{ dangerCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="progress-bar">
          <el-progress
            :percentage="progressPercentage"
            :color="progressColor"
            :show-text="false"
          />
          <div class="progress-markers">
            <div
              v-for="(scene, index) in currentCase.scenes"
              :key="scene.id"
              class="marker"
              :class="{
                completed: index < currentSceneIndex,
                current: index === currentSceneIndex,
                correct: decisions[index]?.resultType === 'correct',
                cautious: decisions[index]?.resultType === 'cautious',
                danger: decisions[index]?.resultType === 'danger'
              }"
            >
              <span class="marker-dot"></span>
              <span class="marker-label">{{ index + 1 }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="replay-analysis" v-if="showReplayAnalysis && currentCase">
        <div class="analysis-header">
          <div class="header-left">
            <h2>
              <el-icon><DataAnalysis /></el-icon>
              案例复盘
            </h2>
            <p>{{ currentCase.title }}</p>
          </div>
          <div class="header-actions">
            <el-button type="primary" @click="restartCase">
              <el-icon><RefreshLeft /></el-icon>
              重新体验
            </el-button>
            <el-button @click="exitCase">
              <el-icon><ArrowLeft /></el-icon>
              返回案例库
            </el-button>
          </div>
        </div>

        <div class="analysis-content">
          <div class="analysis-grid">
            <div class="analysis-card score-card">
              <div class="card-icon">🏆</div>
              <div class="card-content">
                <h3>综合评分</h3>
                <div class="score-display">
                  <span class="score-value">{{ currentScore }}</span>
                  <span class="score-max">/100</span>
                </div>
                <div class="score-grade">{{ getScoreGrade(currentScore) }}</div>
              </div>
            </div>

            <div class="analysis-card stats-card">
              <div class="card-icon">📊</div>
              <div class="card-content">
                <h3>决策统计</h3>
                <div class="stats-grid">
                  <div class="stat-item">
                    <span class="stat-value correct">{{ correctCount }}</span>
                    <span class="stat-label">正确</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-value cautious">{{ cautiousCount }}</span>
                    <span class="stat-label">谨慎</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-value danger">{{ dangerCount }}</span>
                    <span class="stat-label">危险</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="analysis-card summary-card">
              <div class="card-icon">📝</div>
              <div class="card-content">
                <h3>案例总结</h3>
                <p class="summary-text">{{ currentCase.summary }}</p>
              </div>
            </div>
          </div>

          <div class="decision-timeline">
            <h3>
              <el-icon><Timer /></el-icon>
              决策回顾
            </h3>
            <el-timeline>
              <el-timeline-item
                v-for="(decision, index) in decisions"
                :key="index"
                :type="getResultType(decision.resultType)"
                placement="top"
              >
                <div class="timeline-card" :class="decision.resultType">
                  <div class="card-header">
                    <span class="scene-name">场景 {{ index + 1 }}：{{ decision.sceneTitle }}</span>
                    <el-tag :type="getResultTagType(decision.resultType)" size="small">
                      {{ decision.result }}
                    </el-tag>
                  </div>
                  <div class="card-body">
                    <div class="decision-info">
                      <span class="label">您的选择：</span>
                      <span class="value">{{ decision.option }}</span>
                    </div>
                    <div class="decision-analysis">
                      <h5>分析：</h5>
                      <p>{{ decision.analysis }}</p>
                    </div>
                    <div class="decision-suggestion">
                      <h5>建议：</h5>
                      <p>{{ decision.suggestion }}</p>
                    </div>
                  </div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>

          <div class="prevention-section">
            <h3>
              <el-icon><Shield /></el-icon>
              防范要点
            </h3>
            <div class="prevention-grid">
              <div
                v-for="(guide, index) in currentCase.preventionGuide"
                :key="index"
                class="prevention-item"
              >
                <span class="item-number">{{ index + 1 }}</span>
                <span class="item-text">{{ guide }}</span>
              </div>
            </div>
          </div>

          <div class="related-section">
            <h3>
              <el-icon><Connection /></el-icon>
              相关案例
            </h3>
            <div class="related-grid">
              <div
                v-for="relatedCase in relatedCases"
                :key="relatedCase.id"
                class="related-item"
                @click="selectCase(relatedCase)"
              >
                <span class="item-icon">{{ getTypeIcon(relatedCase.type) }}</span>
                <div class="item-content">
                  <h4>{{ relatedCase.title }}</h4>
                  <span class="item-meta">{{ relatedCase.duration }}分钟 | {{ relatedCase.difficulty }}</span>
                </div>
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Clock,
  TrendCharts,
  Aim,
  ArrowRight,
  DataAnalysis,
  RefreshLeft,
  Close,
  Check,
  Warning,
  Lightbulb,
  Timer,
  Shield,
  Connection,
  ArrowLeft
} from '@element-plus/icons-vue'

interface Scene {
  id: number
  title: string
  narrative: string
  image?: string
  options: string[]
  outcomes: Outcome[]
}

interface Outcome {
  result: string
  resultType: string
  message: string
  analysis: string
  suggestion: string
}

interface Case {
  id: number
  title: string
  type: string
  description: string
  duration: number
  difficulty: string
  summary: string
  scenes: Scene[]
  preventionGuide: string[]
  sceneCount: number
  learnCount: number
  rating: string
  featured?: boolean
}

interface Decision {
  sceneTitle: string
  option: string
  result: string
  resultType: string
  message: string
  analysis: string
  suggestion: string
}

const cases = ref<Case[]>([
  {
    id: 1,
    title: '冒充公检法诈骗',
    type: '电信诈骗',
    description: '模拟接到自称公检法电话的情景，体验完整的诈骗过程和正确的应对方式',
    duration: 15,
    difficulty: '中等',
    summary: '本案例模拟了一个典型的冒充公检法电信诈骗场景。骗子通过恐吓、威胁等手段，诱导受害者提供个人信息和银行卡信息。通过本次体验，您可以了解此类诈骗的完整流程和防范方法。',
    sceneCount: 3,
    learnCount: 12580,
    rating: '4.9',
    featured: true,
    scenes: [
      {
        id: 101,
        title: '接到陌生电话',
        narrative: '您正在家中休息，突然接到一个陌生电话。对方自称是公安局民警，说您涉及一起洗钱案件，需要配合调查。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20receiving%20phone%20call%20worried%20expression&image_size=landscape_16_9',
        options: [
          '相信对方，配合调查',
          '要求对方出示证件',
          '挂断电话并报警',
          '询问具体案件详情'
        ],
        outcomes: [
          { result: '危险', resultType: 'danger', message: '您选择相信对方并配合调查，对方会进一步要求您提供银行卡信息和验证码，最终导致资金被盗。', analysis: '骗子利用公检法权威身份给您造成心理压力，让您失去判断力。这是典型的冒充公检法诈骗手法。', suggestion: '公检法机关不会通过电话办案，遇到此类情况应立即挂断并拨打110核实。' },
          { result: '谨慎', resultType: 'cautious', message: '您要求对方出示证件，对方可能会伪造证件继续迷惑您。', analysis: '要求核实身份是正确的做法，但仅凭证件难以辨别真伪。骗子可能通过技术手段伪造来电显示和证件。', suggestion: '即使对方出示证件，也不要轻信，应通过官方渠道独立核实。' },
          { result: '正确', resultType: 'correct', message: '您挂断电话并报警，避免了被骗的风险。', analysis: '您做出了最明智的选择。面对冒充公检法的诈骗电话，最有效的方式就是直接挂断并报警。', suggestion: '记住：公检法机关不会通过电话办案，不会要求转账到安全账户。' },
          { result: '危险', resultType: 'danger', message: '您与对方过多纠缠，对方会用专业的话术逐渐迷惑您，最终导致您上当。', analysis: '骗子经过专业培训，善于利用心理学技巧。他们会不断施压，让您没有时间思考。', suggestion: '遇到可疑电话，应直接挂断，不要试图与对方理论或证明自己的清白。' }
        ]
      },
      {
        id: 102,
        title: '对方施压',
        narrative: '对方见您有所怀疑，开始施压。说您如果不配合调查，将会被刑事拘留，并要求您立即转移资金到"安全账户"。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20stressed%20on%20phone%20police%20scam&image_size=landscape_16_9',
        options: [
          '按照要求转移资金',
          '再次要求核实身份',
          '挂断电话并报警',
          '咨询家人朋友'
        ],
        outcomes: [
          { result: '危险', resultType: 'danger', message: '您按照要求转移资金，最终导致资金损失。', analysis: '这是诈骗的核心环节。骗子会要求您将资金转入"安全账户"或"监管账户"，这实际上是骗子的私人账户。', suggestion: '公检法机关不会要求任何形式的转账，更不存在"安全账户"这一说法。' },
          { result: '谨慎', resultType: 'cautious', message: '您再次要求核实身份，对方可能会提供虚假的核实方式。', analysis: '继续与对方纠缠可能让您逐渐陷入对方的节奏。骗子可能会提供假的案件编号、警官号等。', suggestion: '任何核实都应该是您主动通过官方渠道进行的，而不是被动接受对方提供的信息。' },
          { result: '正确', resultType: 'correct', message: '您挂断电话并报警，避免了资金损失。', analysis: '您的选择非常正确。面对威胁和压力，最明智的做法就是立即挂断并通过官方渠道报警。', suggestion: '保持冷静，不要被对方的威胁吓到。真正的执法部门不会通过电话威胁公民。' },
          { result: '正确', resultType: 'correct', message: '您咨询家人朋友，得到了正确的建议，避免了被骗。', analysis: '您选择寻求他人帮助，这是一个非常明智的做法。在遇到可疑情况时，与可信的人商量可以获得新的视角。', suggestion: '遇到可疑情况时，及时与家人朋友沟通，他们可能会帮助您识别骗局。' }
        ]
      },
      {
        id: 103,
        title: '最终决定',
        narrative: '对方继续施压，说您已经被列为网上逃犯，需要立即处理，否则将上门抓捕。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20anxious%20computer%20screen%20police%20warning&image_size=landscape_16_9',
        options: [
          '按照对方要求操作',
          '挂断电话并报警',
          '登录官方网站核实',
          '忽略对方'
        ],
        outcomes: [
          { result: '危险', resultType: 'danger', message: '您按照对方要求操作，最终导致资金损失。', analysis: '在高压下做决定往往是危险的。骗子利用您对法律制裁的恐惧，诱导您做出错误决定。', suggestion: '记住：真正的执法部门不会通过电话要求公民转账，也不会威胁上门抓捕。' },
          { result: '正确', resultType: 'correct', message: '您挂断电话并报警，避免了资金损失。', analysis: '无论骗子如何施压，您都保持了冷静，做出了正确的选择。这正是防范诈骗的关键。', suggestion: '如果不确定是否真的涉及案件，可以直接前往就近的派出所或公安局核实。' },
          { result: '正确', resultType: 'correct', message: '您登录官方网站核实，发现没有相关案件，避免了被骗。', analysis: '您选择了理性的核实方式，通过独立渠道验证信息，这是识别诈骗的有效方法。', suggestion: '可以通过最高人民法院官网、地方公安局官网等官方渠道核实自己是否真的被通缉。' },
          { result: '正确', resultType: 'correct', message: '您忽略对方，避免了被骗的风险。', analysis: '面对持续施压，最好的策略就是不予理会。骗子无法从忽视他们的人那里获得任何东西。', suggestion: '如果确定是诈骗电话，最好的做法就是直接挂断并标记为骚扰电话。' }
        ]
      }
    ],
    preventionGuide: [
      '公检法机关不会通过电话办案，不会要求转账到安全账户',
      '遇到自称公检法的电话，应挂断后通过官方渠道（110）核实',
      '不要向陌生人透露个人身份信息、银行账户和验证码',
      '真正的案件不会只通过电话通知，涉及资金转移一定是诈骗'
    ]
  },
  {
    id: 2,
    title: '网络刷单诈骗',
    type: '网络诈骗',
    description: '模拟遇到刷单兼职的情景，体验"高回报"诱惑背后的陷阱',
    duration: 12,
    difficulty: '简单',
    summary: '本案例模拟了一个典型的网络兼职刷单诈骗场景。骗子以高额报酬为诱饵，要求受害者先垫付资金，然后以各种理由拒绝返款。',
    sceneCount: 3,
    learnCount: 9860,
    rating: '4.8',
    scenes: [
      {
        id: 201,
        title: '看到兼职广告',
        narrative: '您在网上看到一个刷单兼职的广告，声称"日赚300元"、"零门槛高回报"，只需在家用手机就能完成。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20looking%20at%20phone%20兼职%20ad&image_size=landscape_16_9',
        options: [
          '立即联系对方开始兼职',
          '仔细阅读广告内容',
          '搜索了解刷单诈骗案例',
          '直接忽略此类广告'
        ],
        outcomes: [
          { result: '危险', resultType: 'danger', message: '您立即联系对方，可能会陷入刷单诈骗的陷阱。', analysis: '这类广告通常以高回报为诱饵，吸引受害者上钩。一旦开始，往往会被要求垫付资金。', suggestion: '正规的兼职不会要求先垫付资金，遇到此类广告应保持警惕。' },
          { result: '谨慎', resultType: 'cautious', message: '您仔细阅读广告内容，展现了一定的谨慎态度。', analysis: '但仅凭阅读内容难以识别诈骗，因为骗子会精心包装广告，让它看起来非常正规。', suggestion: '即使广告看起来很正规，也要通过其他渠道核实其真实性。' },
          { result: '正确', resultType: 'correct', message: '您搜索了解刷单诈骗案例，展现了理性判断能力。', analysis: '您选择了在行动前先了解情况，这是避免被骗的有效方法。网上有很多真实的诈骗案例可以参考。', suggestion: '了解常见的诈骗手法，可以帮助您更好地识别和防范类似骗局。' },
          { result: '正确', resultType: 'correct', message: '您直接忽略此类广告，避免了潜在的风险。', analysis: '对于明显不符合常理的高回报兼职，最安全的方式就是直接忽略。', suggestion: '记住：天上不会掉馅饼，任何"轻松赚钱"的机会都可能是陷阱。' }
        ]
      },
      {
        id: 202,
        title: '对方引导下单',
        narrative: '您添加了对方的好友，对方发来一个商品链接，要求您下单并垫付资金，承诺会返还本金并支付佣金。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20shopping%20online%20scam&image_size=landscape_16_9',
        options: [
          '按照要求下单垫付',
          '要求先看看佣金多少',
          '质疑为什么需要垫付',
          '立即删除好友'
        ],
        outcomes: [
          { result: '危险', resultType: 'danger', message: '您按照要求下单垫付，可能会面临资金损失。', analysis: '这是刷单诈骗的典型模式：先让受害者尝到甜头，然后逐步加大垫付金额，最终拒绝返款。', suggestion: '任何要求垫付资金的兼职都不要参与，真正的刷单平台不会让个人垫资。' },
          { result: '谨慎', resultType: 'cautious', message: '您先了解佣金情况，展现了谨慎态度。', analysis: '询问佣金是正常的，但骗子可能会先给您一些甜头，等您加大投入后再行骗。', suggestion: '即使佣金看起来很诱人，也要警惕垫付资金的风险。' },
          { result: '正确', resultType: 'correct', message: '您质疑为什么需要垫付，这是一个关键问题。', analysis: '正规平台有专门的结算系统，不需要个人垫付资金。您的问题戳中了诈骗的核心漏洞。', suggestion: '如果对方强调必须垫付才能做任务，那么这一定是诈骗。' },
          { result: '正确', resultType: 'correct', message: '您立即删除好友，避免了后续可能的损失。', analysis: '您选择了最果断的方式保护自己。发现可疑情况时，及时退出是最明智的选择。', suggestion: '对于任何要求垫付资金的兼职，都要坚决拒绝并远离。' }
        ]
      },
      {
        id: 203,
        title: '对方要求加大投入',
        narrative: '您完成了几单小额的刷单任务，对方确实返了本金和佣金。但现在对方说有一批"高佣金"任务，需要垫付更大金额。',
        image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=person%20stressed%20about%20money%20scam&image_size=landscape_16_9',
        options: [
          '继续投入资金',
          '只做小额任务',
          '提现后退出',
          '立即报警'
        ],
        outcomes: [
          { result: '危险', resultType: 'danger', message: '您继续投入资金，可能会全部损失。', analysis: '这是"放长线钓大鱼"的诈骗策略。前面的返利只是为了建立信任，诱导您加大投入。', suggestion: '一旦对方开始要求大额垫付，就意味着诈骗即将收网，必须立即停止。' },
          { result: '谨慎', resultType: 'cautious', message: '您坚持只做小额任务，展现了风险意识。', analysis: '您的做法是明智的，坚持小额任务可以控制风险。但如果对方坚持要求大额投入，说明骗局即将收网。', suggestion: '如果对方不接受小额任务，坚持让您加大投入，应立即停止并退出。' },
          { result: '正确', resultType: 'correct', message: '您选择提现后退出，保护了已有收益。', analysis: '您在骗子收网之前及时退出，既保住了之前的收益，又避免了更大的损失。', suggestion: '如果已经投入了资金，应尝试提现后再退出，不要期望通过继续投入来挽回损失。' },
          { result: '正确', resultType: 'correct', message: '您选择立即报警，这是正确的做法。', analysis: '您不仅保护了自己，还选择了通过法律途径维护权益。报警可以帮助警方打击此类犯罪。', suggestion: '遇到诈骗后，应及时收集证据并报警，这有助于警方打击犯罪团伙。' }
        ]
      }
    ],
    preventionGuide: [
      '刷单本身就是违法行为，正规平台禁止刷单',
      '任何要求垫付资金的兼职都不要参与',
      '不要被"高回报"诱惑，天上不会掉馅饼',
      '发现被骗后要及时报警，保留聊天记录和转账凭证'
    ]
  }
])

const currentCase = ref<Case | null>(null)
const currentSceneIndex = ref(0)
const selectedOption = ref<number | null>(null)
const showResult = ref(false)
const showReplayAnalysis = ref(false)
const isDeciding = ref(false)
const decisions = ref<Decision[]>([])

const currentScene = computed(() => {
  if (!currentCase.value) return null
  return currentCase.value.scenes[currentSceneIndex.value]
})

const hasNextScene = computed(() => {
  if (!currentCase.value) return false
  return currentSceneIndex.value < currentCase.value.scenes.length - 1
})

const currentScore = computed(() => {
  if (decisions.value.length === 0) return 0
  let score = 0
  decisions.value.forEach(d => {
    if (d.resultType === 'correct') score += 35
    else if (d.resultType === 'cautious') score += 20
  })
  return Math.min(score, 100)
})

const correctCount = computed(() => decisions.value.filter(d => d.resultType === 'correct').length)
const cautiousCount = computed(() => decisions.value.filter(d => d.resultType === 'cautious').length)
const dangerCount = computed(() => decisions.value.filter(d => d.resultType === 'danger').length)

const progressPercentage = computed(() => {
  if (!currentCase.value) return 0
  return ((currentSceneIndex.value + (showResult.value ? 1 : 0)) / currentCase.value.scenes.length) * 100
})

const progressColor = computed(() => {
  const pct = currentScore.value
  if (pct >= 80) return '#67c23a'
  if (pct >= 60) return '#e6a23c'
  return '#f56c6c'
})

const decisionResult = computed(() => {
  if (selectedOption.value === null || !currentScene.value) return null
  return currentScene.value.outcomes[selectedOption.value]
})

const relatedCases = computed(() => {
  if (!currentCase.value) return []
  return cases.value.filter(c => c.id !== currentCase.value!.id).slice(0, 3)
})

function getTypeIcon(type: string): string {
  const iconMap: Record<string, string> = {
    '电信诈骗': '📞',
    '网络诈骗': '🌐',
    '金融诈骗': '💰',
    '社交诈骗': '👥'
  }
  return iconMap[type] || '📌'
}

function getCaseType(type: string): string {
  const typeMap: Record<string, string> = {
    '电信诈骗': 'danger',
    '网络诈骗': 'warning',
    '金融诈骗': 'primary',
    '社交诈骗': 'info'
  }
  return typeMap[type] || 'info'
}

function getOptionLetter(index: number): string {
  return String.fromCharCode(65 + index)
}

function getResultIcon(resultType: string): string {
  const iconMap: Record<string, string> = {
    correct: '✅',
    cautious: '⚠️',
    danger: '❌'
  }
  return iconMap[resultType] || '❓'
}

function getResultType(resultType: string): string {
  const typeMap: Record<string, string> = {
    correct: 'success',
    cautious: 'warning',
    danger: 'danger'
  }
  return typeMap[resultType] || 'info'
}

function getResultTagType(resultType: string): string {
  return getResultType(resultType)
}

function getScoreGrade(score: number): string {
  if (score >= 90) return '优秀'
  if (score >= 70) return '良好'
  if (score >= 60) return '及格'
  return '需加强'
}

function selectCase(caseItem: Case) {
  currentCase.value = caseItem
  restartCase()
}

function restartCase() {
  currentSceneIndex.value = 0
  selectedOption.value = null
  showResult.value = false
  showReplayAnalysis.value = false
  decisions.value = []
}

function exitCase() {
  currentCase.value = null
  currentSceneIndex.value = 0
  selectedOption.value = null
  showResult.value = false
  showReplayAnalysis.value = false
  decisions.value = []
}

function selectOption(index: number) {
  selectedOption.value = index
}

function makeDecision() {
  if (selectedOption.value === null || !currentScene.value) return

  isDeciding.value = true

  setTimeout(() => {
    const outcome = currentScene.value!.outcomes[selectedOption.value!]

    decisions.value.push({
      sceneTitle: currentScene.value!.title,
      option: currentScene.value!.options[selectedOption.value!],
      result: outcome.result,
      resultType: outcome.resultType,
      message: outcome.message,
      analysis: outcome.analysis,
      suggestion: outcome.suggestion
    })

    showResult.value = true
    isDeciding.value = false
  }, 1000)
}

function nextScene() {
  if (!hasNextScene.value) {
    showReplayAnalysis.value = true
    return
  }

  currentSceneIndex.value++
  selectedOption.value = null
  showResult.value = false
}

onMounted(() => {
  ElMessage.success({
    message: '情景沉浸回放已优化完成，体验真实案例，学习决策技巧',
    duration: 3000
  })
})
</script>

<style scoped>
.scenario-replay {
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  background: linear-gradient(135deg, #fef0f0 0%, #fff9f9 100%);
  min-height: calc(100vh - 60px);
}

.header {
  margin-bottom: 24px;
}

.header-content {
  background: linear-gradient(135deg, #e65454 0%, #f78989 100%);
  border-radius: 16px;
  padding: 32px;
  color: white;
  box-shadow: 0 8px 32px rgba(230, 84, 84, 0.3);
}

.header-text h1 {
  font-size: 32px;
  margin: 0 0 12px 0;
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
  backdrop-filter: blur(10px);
}

.competitor {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  margin-left: 8px;
}

.header-features {
  display: flex;
  gap: 32px;
  margin-top: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.feature-icon {
  font-size: 24px;
}

.feature-text {
  font-size: 14px;
  opacity: 0.9;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.case-selection {
  padding: 0;
}

.selection-header {
  text-align: center;
  margin-bottom: 32px;
}

.selection-header h2 {
  font-size: 28px;
  color: #333;
  margin: 0 0 8px 0;
}

.selection-header p {
  color: #909399;
  margin: 0;
}

.case-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}

.case-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  position: relative;
}

.case-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
  border-color: #e65454;
}

.case-card.featured {
  border-color: #e65454;
  background: linear-gradient(135deg, #fff9f9, #fff);
}

.card-badge {
  position: absolute;
  top: -12px;
  right: 16px;
}

.card-type {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.type-icon {
  font-size: 24px;
}

.case-card h3 {
  margin: 0 0 12px 0;
  font-size: 20px;
  color: #333;
}

.card-desc {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.card-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.case-stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.case-replay {
  background: white;
  border-radius: 16px;
  padding: 24px;
}

.replay-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.replay-info h2 {
  margin: 0 0 12px 0;
  font-size: 24px;
  color: #333;
}

.replay-tags {
  display: flex;
  gap: 8px;
  align-items: center;
}

.progress-text {
  font-size: 14px;
  color: #909399;
  margin-left: 8px;
}

.replay-actions {
  display: flex;
  gap: 8px;
}

.replay-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.scene-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.scene-visual {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
}

.scene-image img {
  width: 100%;
  height: 240px;
  object-fit: cover;
  display: block;
}

.scene-overlay {
  position: absolute;
  top: 12px;
  left: 12px;
}

.scene-badge {
  background: linear-gradient(135deg, #e65454, #f78989);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.scene-placeholder {
  height: 240px;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #909399;
  border-radius: 12px;
}

.placeholder-icon {
  font-size: 48px;
}

.scene-number {
  font-size: 12px;
  color: #e65454;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.scene-title {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #333;
}

.scene-narrative {
  margin: 0;
  font-size: 15px;
  color: #606266;
  line-height: 1.8;
}

.decision-panel {
  background: #fafafa;
  border-radius: 12px;
  padding: 24px;
}

.decision-header {
  text-align: center;
  margin-bottom: 24px;
}

.header-icon {
  font-size: 36px;
  margin-bottom: 8px;
}

.decision-header h4 {
  margin: 0 0 4px 0;
  font-size: 18px;
  color: #333;
}

.decision-header p {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.decision-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid #ebeef5;
}

.option-item:hover {
  border-color: #e65454;
  background: #fff9f9;
}

.option-item.selected {
  border-color: #e65454;
  background: linear-gradient(135deg, #fff9f9, #fff);
}

.option-letter {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #e65454, #f78989);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
}

.option-text {
  flex: 1;
  font-size: 15px;
  color: #333;
}

.option-hint {
  font-size: 12px;
  color: #e65454;
  font-weight: 500;
}

.confirm-action {
  text-align: center;
}

.result-card {
  padding: 24px;
  border-radius: 16px;
  margin-bottom: 24px;
}

.result-card.correct {
  background: linear-gradient(135deg, #f0f9eb, #e8f5e9);
  border: 2px solid #67c23a;
}

.result-card.cautious {
  background: linear-gradient(135deg, #fdf6ec, #fef0e6);
  border: 2px solid #e6a23c;
}

.result-card.danger {
  background: linear-gradient(135deg, #fef0f0, #fff);
  border: 2px solid #f56c6c;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.result-icon {
  font-size: 32px;
}

.result-badge {
  font-size: 20px;
  font-weight: bold;
}

.result-card.correct .result-badge { color: #67c23a; }
.result-card.cautious .result-badge { color: #e6a23c; }
.result-card.danger .result-badge { color: #f56c6c; }

.result-message {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.result-analysis,
.result-suggestion {
  margin-bottom: 12px;
}

.result-analysis h5,
.result-suggestion h5 {
  margin: 0 0 4px 0;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.result-analysis h5 { color: #909399; }
.result-suggestion h5 { color: #e65454; }

.result-analysis p,
.result-suggestion p {
  margin: 0;
  font-size: 13px;
  color: #333;
}

.result-actions {
  text-align: center;
  margin-bottom: 24px;
}

.decision-summary {
  display: flex;
  justify-content: space-around;
  padding: 16px;
  background: white;
  border-radius: 12px;
}

.summary-item {
  text-align: center;
}

.summary-item .label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.summary-item .value {
  font-size: 24px;
  font-weight: bold;
}

.summary-item .value.score { color: #e65454; }
.summary-item .value.correct { color: #67c23a; }
.summary-item .value.danger { color: #f56c6c; }

.progress-bar {
  position: relative;
  padding: 0 20px;
}

.progress-markers {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
}

.marker {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.marker-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #dcdfe6;
  transition: all 0.3s ease;
}

.marker.current .marker-dot {
  background: #e65454;
  box-shadow: 0 0 0 4px rgba(230, 84, 84, 0.2);
}

.marker.completed .marker-dot {
  background: #67c23a;
}

.marker.correct .marker-dot { background: #67c23a; }
.marker.cautious .marker-dot { background: #e6a23c; }
.marker.danger .marker-dot { background: #f56c6c; }

.marker-label {
  font-size: 12px;
  color: #909399;
}

.marker.current .marker-label {
  color: #e65454;
  font-weight: bold;
}

.replay-analysis {
  background: white;
  border-radius: 16px;
  padding: 32px;
}

.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
}

.header-left h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-left p {
  margin: 0;
  color: #909399;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.analysis-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.analysis-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.analysis-card {
  padding: 24px;
  border-radius: 16px;
  display: flex;
  gap: 16px;
}

.score-card {
  background: linear-gradient(135deg, #fff9f9, #fff);
  border: 2px solid #e65454;
}

.stats-card {
  background: linear-gradient(135deg, #f5f7fa, #fff);
  border: 1px solid #ebeef5;
}

.summary-card {
  background: linear-gradient(135deg, #f5f7fa, #fff);
  border: 1px solid #ebeef5;
  grid-column: span 1;
}

.card-icon {
  font-size: 36px;
}

.card-content h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
}

.score-display {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 8px;
}

.score-value {
  font-size: 48px;
  font-weight: bold;
  color: #e65454;
}

.score-max {
  font-size: 20px;
  color: #909399;
}

.score-grade {
  font-size: 16px;
  color: #e65454;
  font-weight: 500;
}

.stats-grid {
  display: flex;
  gap: 24px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 32px;
  font-weight: bold;
}

.stat-value.correct { color: #67c23a; }
.stat-value.cautious { color: #e6a23c; }
.stat-value.danger { color: #f56c6c; }

.stat-label {
  font-size: 12px;
  color: #909399;
}

.summary-text {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

.decision-timeline h3,
.prevention-section h3,
.related-section h3 {
  margin: 0 0 20px 0;
  font-size: 20px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.timeline-card {
  padding: 20px;
  border-radius: 12px;
  border-left: 4px solid;
}

.timeline-card.correct {
  background: #f0f9eb;
  border-color: #67c23a;
}

.timeline-card.cautious {
  background: #fdf6ec;
  border-color: #e6a23c;
}

.timeline-card.danger {
  background: #fef0f0;
  border-color: #f56c6c;
}

.timeline-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.scene-name {
  font-weight: bold;
  color: #333;
}

.card-body p {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
}

.decision-info {
  margin-bottom: 12px;
}

.decision-info .label {
  color: #909399;
}

.decision-info .value {
  color: #333;
  font-weight: 500;
}

.decision-analysis h5,
.decision-suggestion h5 {
  margin: 0 0 4px 0;
  font-size: 13px;
}

.decision-analysis h5 { color: #909399; }
.decision-suggestion h5 { color: #e65454; }

.decision-analysis p,
.decision-suggestion p {
  margin: 0;
  font-size: 13px;
  color: #333;
}

.prevention-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.prevention-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
}

.item-number {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #e65454, #f78989);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  flex-shrink: 0;
}

.item-text {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.related-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.related-item:hover {
  background: #fff9f9;
  border-color: #e65454;
}

.item-icon {
  font-size: 32px;
}

.item-content {
  flex: 1;
}

.item-content h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #333;
}

.item-meta {
  font-size: 12px;
  color: #909399;
}

@media (max-width: 1200px) {
  .replay-content {
    grid-template-columns: 1fr;
  }

  .analysis-grid {
    grid-template-columns: 1fr 1fr;
  }

  .score-card {
    grid-column: span 2;
  }

  .prevention-grid {
    grid-template-columns: 1fr;
  }

  .related-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .scenario-replay {
    padding: 16px;
  }

  .header-content {
    padding: 20px;
  }

  .header-text h1 {
    font-size: 24px;
  }

  .header-features {
    flex-wrap: wrap;
    gap: 16px;
  }

  .case-grid {
    grid-template-columns: 1fr;
  }

  .analysis-grid {
    grid-template-columns: 1fr;
  }

  .score-card {
    grid-column: span 1;
  }

  .stats-grid {
    flex-direction: column;
    gap: 12px;
  }

  .related-grid {
    grid-template-columns: 1fr;
  }
}
</style>
